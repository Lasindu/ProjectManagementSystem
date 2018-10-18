/*
 * Copyright 2014 Tomi Virtanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pms.component.ganttchart.scheduletask;

import com.pms.DashboardUI;
import com.pms.component.ganttchart.GanttListener;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification.Type;
import org.tltv.gantt.Gantt;
import org.tltv.gantt.Gantt.MoveEvent;
import org.tltv.gantt.Gantt.ResizeEvent;
import org.tltv.gantt.client.shared.AbstractStep;
import org.tltv.gantt.client.shared.Step;
import com.pms.component.ganttchart.util.UriFragmentWrapperFactory;
import com.pms.component.ganttchart.util.Util;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class TaskGanntChart  {

    private Gantt gantt;

    private Project project;

    private NativeSelect localeSelect;
    private NativeSelect reso;

    private DateField start;
    private DateField end;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "MMM dd HH:mm:ss zzz yyyy");

    private HorizontalLayout controls;

    private GanttListener ganttListener;

    private ClientConnector.AttachListener ganttAttachListener = new ClientConnector.AttachListener() {

        @Override
        public void attach(ClientConnector.AttachEvent event) {
            syncLocaleAndTimezone();
        }
    };

    private ClickListener createStepClickListener = new ClickListener() {

        @Override
        public void buttonClick(ClickEvent event) {
            Step newStep = new Step();
            Date now = new Date();
            newStep.setStartDate(now.getTime());
            newStep.setEndDate(now.getTime() + (7 * 24 * 3600000));
            openStepEditor(newStep);
        }

    };

    private ValueChangeListener startDateValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            gantt.setStartDate((Date) event.getProperty().getValue());
        }
    };

    private ValueChangeListener endDateValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            gantt.setEndDate((Date) event.getProperty().getValue());
        }
    };

    private ValueChangeListener resolutionValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            org.tltv.gantt.client.shared.Resolution res = (org.tltv.gantt.client.shared.Resolution) event
                    .getProperty().getValue();
            if (validateResolutionChange(res)) {
                gantt.setResolution(res);
            }
        }

    };

    private ValueChangeListener localeValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            gantt.setLocale((Locale) event.getProperty().getValue());

            syncLocaleAndTimezone();
        }
    };

    private ValueChangeListener timezoneValueChangeListener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            String tzId = (String) event.getProperty().getValue();
            if ("Default".equals(tzId)) {
                gantt.setTimeZone(null);
            } else {
                gantt.setTimeZone(TimeZone.getTimeZone(tzId));
            }
            syncLocaleAndTimezone();
        }
    };


    public Component init(Project project) {

        this.project= project;

        ganttListener = null;
        createGantt(project);

        MenuBar menu = controlsMenuBar();
        Panel controls = createControls();

        TabSheet tabsheet = new TabSheet();
        tabsheet.setSizeFull();

        /*  Component wrapper = UriFragmentWrapperFactory.wrapByUriFragment(UI
                .getCurrent().getPage().getUriFragment(), gantt);
        if (wrapper instanceof GanttListener) {
            ganttListener = (GanttListener) wrapper;
        }*/

        //to show table
        Component wrapper = UriFragmentWrapperFactory.wrapByUriFragment("table", gantt);
        if (wrapper instanceof GanttListener) {
            ganttListener = (GanttListener) wrapper;
        }

        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        //layout.addComponent(menu);
        layout.addComponent(controls);
        layout.addComponent(wrapper);
        layout.setExpandRatio(wrapper, 1);

        controls.setVisible(false);

        return  layout;
    }

    private void createGantt(Project project) {


        gantt = new Gantt();
        gantt.setWidth(100, Sizeable.Unit.PERCENTAGE);
        gantt.setHeight(460, Sizeable.Unit.PIXELS);
        gantt.setResizableSteps(true);
        gantt.setMovableSteps(true);
        gantt.addAttachListener(ganttAttachListener);


        Calendar cal = Calendar.getInstance();

        Date date = new Date();
        date.setYear(2015);
        date.setMonth(0);
        date.setDate(5);


        // date.setTime(0);

        cal.setTime(date);
        cal.set(Calendar.HOUR,0);
        gantt.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 360);
        gantt.setEndDate(cal.getTime());
        cal.setTime(date);


        gantt.setYearsVisible(false);
        gantt.setMonthsVisible(false);
        gantt.setResolution(org.tltv.gantt.client.shared.Resolution.Week);
        gantt.setReadOnly(true);





    /*    UserStoryDAO userStoryDAO =(UserStoryDAO)DashboardUI.context.getBean("UserStory");

        List<Task> taskList = new ArrayList<Task>();
        taskList.addAll(userStoryDAO.getUserStoryTaskList(userStory));


        List<Task> priority1Tasks = new ArrayList<Task>();
        List<Task> priority2Tasks = new ArrayList<Task>();
        List<Task> priority3Tasks = new ArrayList<Task>();
        List<Task> priority4Tasks = new ArrayList<Task>();
        List<Task> priority5Tasks = new ArrayList<Task>();

        for (Task task : taskList) {
            switch (task.getPriority()) {
                case 1:
                    priority1Tasks.add(task);
                    break;
                case 2:
                    priority2Tasks.add(task);
                    break;
                case 3:
                    priority3Tasks.add(task);
                    break;
                case 4:
                    priority4Tasks.add(task);
                    break;
                case 5:
                    priority5Tasks.add(task);
                    break;

            }

        }
        String[] colors = new String[] { "11FF11", "33FF33", "55FF55",
                "77FF77", "99FF99", "BBFFBB", "DDFFDD" };

        cal.setTime(new Date());

        for (Task task1 : priority1Tasks)
        {
            Step step = new Step(task1.getName());
            step.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, 14);
            step.setEndDate(cal.getTime().getTime());
            step.setBackgroundColor(colors[10 % colors.length]);
            gantt.addStep(step);

        }

        for (Task task1 : priority2Tasks)
        {
            Step step = new Step(task1.getName());
            step.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, 14);
            step.setEndDate(cal.getTime().getTime());
            step.setBackgroundColor(colors[10 % colors.length]);
            gantt.addStep(step);

        }
        for (Task task1 : priority3Tasks)
        {
            Step step = new Step(task1.getName());
            step.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, 14);
            step.setEndDate(cal.getTime().getTime());
            step.setBackgroundColor(colors[10 % colors.length]);
            gantt.addStep(step);

        }
        for (Task task1 : priority4Tasks)
        {
            Step step = new Step(task1.getName());
            step.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, 14);
            step.setEndDate(cal.getTime().getTime());
            step.setBackgroundColor(colors[10 % colors.length]);
            gantt.addStep(step);

        }
        for (Task task1 : priority5Tasks)
        {
            Step step = new Step(task1.getName());
            step.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, 14);
            step.setEndDate(cal.getTime().getTime());
            step.setBackgroundColor(colors[10 % colors.length]);
            gantt.addStep(step);


        }
*/

        //this prioritize task because otherwise working userstory can be null
        PrioritizeUserStories prioritizeUserStories= new PrioritizeUserStories();
        Map userStorieMap = prioritizeUserStories.getPrioritizeUserStoryMap(project);

        PrioritizeTasks prioritizeTasks = new PrioritizeTasks();

        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        UserStory userStory=userStoryDAO.getCurrentWorkingUserStory(project);

        Map taskMap = prioritizeTasks.getPrioritizeTaskMap(userStory);

        Step previosStep=null;

        System.out.println(taskMap.toString());
        Iterator it = taskMap.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove();

            Task task1 =(Task)pair.getValue();

            Step step1 = new Step(task1.getName());
            step1.setDescription(task1.getName());
            step1.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, Integer.parseInt(task1.getEstimateTime())*7);
            step1.setEndDate(cal.getTime().getTime());

            //Change color of background according to state of task
            if(task1.getState().equals("initial"))
            {
                step1.setBackgroundColor("#A9F5F2");
            }
            else if(task1.getState().equals("working"))
            {
                step1.setBackgroundColor("#D0F5A9");
            }
            else if(task1.getState().equals("done"))
            {
                step1.setBackgroundColor("#F5D0A9");
            }

            if(previosStep==null)
            {
                gantt.addStep(step1);
                previosStep=step1;
            }
            else
            {
                step1.setPredecessor(previosStep);
                gantt.addStep(step1);

                previosStep=step1;

            }

        }




        gantt.addClickListener(new Gantt.ClickListener() {

            @Override
            public void onGanttClick(Gantt.ClickEvent event) {
                openStepEditor(event.getStep());
            }
        });

        gantt.addMoveListener(new Gantt.MoveListener() {

            @Override
            public void onGanttMove(MoveEvent event) {
                Date start = new Date(event.getStartDate());
                Date end = new Date(event.getEndDate());

                dateFormat.setTimeZone(gantt.getTimeZone());

                Notification.show("Moved " + event.getStep().getCaption()
                                + " to Start Date: " + dateFormat.format(start)
                                + " End Date: " + dateFormat.format(end),
                        Type.TRAY_NOTIFICATION);
            }
        });

        gantt.addResizeListener(new Gantt.ResizeListener() {

            @Override
            public void onGanttResize(ResizeEvent event) {
                Date start = new Date(event.getStartDate());
                Date end = new Date(event.getEndDate());

                dateFormat.setTimeZone(gantt.getTimeZone());

                Notification.show("Resized " + event.getStep().getCaption()
                                + " to Start Date: " + dateFormat.format(start)
                                + " End Date: " + dateFormat.format(end),
                        Type.TRAY_NOTIFICATION);
            }
        });
    }




    private void syncLocaleAndTimezone() {
        start.removeValueChangeListener(startDateValueChangeListener);
        end.removeValueChangeListener(endDateValueChangeListener);
        try {
            start.setLocale(gantt.getLocale());
            start.setTimeZone(gantt.getTimeZone());
            start.setValue(gantt.getStartDate());
            end.setLocale(gantt.getLocale());
            end.setTimeZone(gantt.getTimeZone());
            end.setValue(gantt.getEndDate());
        } finally {
            start.addValueChangeListener(startDateValueChangeListener);
            end.addValueChangeListener(endDateValueChangeListener);
        }
        dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss zzz yyyy",
                gantt.getLocale());
    }

    private Panel createControls() {
        Panel panel = new Panel();
        panel.setWidth(100, Sizeable.Unit.PERCENTAGE);

        controls = new HorizontalLayout();
        controls.setSpacing(true);
        controls.setMargin(true);
        panel.setContent(controls);

        start = createStartDateField();
        end = createEndDateField();

        Button createStep = new Button("Create New Step...",
                createStepClickListener);

        HorizontalLayout heightAndUnit = new HorizontalLayout(
                Util.createHeightEditor(gantt),
                Util.createHeightUnitEditor(gantt));

        HorizontalLayout widthAndUnit = new HorizontalLayout(
                Util.createWidthEditor(gantt),
                Util.createWidthUnitEditor(gantt));

        reso = new NativeSelect("Resolution");
        reso.setNullSelectionAllowed(false);
        reso.addItem(org.tltv.gantt.client.shared.Resolution.Hour);
        reso.addItem(org.tltv.gantt.client.shared.Resolution.Day);
        reso.addItem(org.tltv.gantt.client.shared.Resolution.Week);
        reso.setValue(gantt.getResolution());
        reso.setImmediate(true);
        reso.addValueChangeListener(resolutionValueChangeListener);

        localeSelect = new NativeSelect("Locale") {
            @Override
            public void attach() {
                super.attach();

                if (getValue() == null) {
                    // use default locale
                    setValue(gantt.getLocale());
                    addValueChangeListener(localeValueChangeListener);
                }
            }
        };
        localeSelect.setNullSelectionAllowed(false);
        for (Locale l : Locale.getAvailableLocales()) {
            localeSelect.addItem(l);
            localeSelect.setItemCaption(l, l.getDisplayName(DashboardUI.getCurrent().getUI().getLocale()));
        }
        localeSelect.setImmediate(true);

        String[] zones = new String[] { "GMT-0", "GMT-1", "GMT-2", "GMT-3",
                "GMT-4", "GMT-5", "GMT-6", "GMT-7", "GMT-8", "GMT-9", "GMT-10",
                "GMT-11", "GMT-12", "GMT+1", "GMT+2", "GMT+3", "GMT+4",
                "GMT+5", "GMT+6", "GMT+7", "GMT+8", "GMT+9", "GMT+10",
                "GMT+11", "GMT+12", "GMT+13", "GMT+14" };
        NativeSelect timezoneSelect = new NativeSelect("Timezone");
        timezoneSelect.setNullSelectionAllowed(false);
        timezoneSelect.addItem("Default");
        timezoneSelect.setItemCaption("Default", "Default ("
                + TimeZone.getDefault().getDisplayName() + ")");
        for (String timezoneId : zones) {
            TimeZone tz = TimeZone.getTimeZone(timezoneId);
            timezoneSelect.addItem(timezoneId);
            timezoneSelect.setItemCaption(timezoneId,
                    tz.getDisplayName(DashboardUI.getCurrent().getUI().getLocale()));
        }
        timezoneSelect.setValue("Default");
        timezoneSelect.setImmediate(true);
        timezoneSelect.addValueChangeListener(timezoneValueChangeListener);

        controls.addComponent(start);
        controls.addComponent(end);
        controls.addComponent(reso);
        //controls.addComponent(localeSelect);
        //controls.addComponent(timezoneSelect);
        controls.addComponent(heightAndUnit);
        controls.addComponent(widthAndUnit);
        //controls.addComponent(createStep);
        // controls.setComponentAlignment(createStep, Alignment.MIDDLE_LEFT);
        //controls.setComponentAlignment(widthAndUnit, Alignment.MIDDLE_LEFT);

        return panel;
    }

    private DateField createStartDateField() {
        DateField f = new DateField("Start date");
        f.setResolution(Resolution.SECOND);
        f.setImmediate(true);
        f.addValueChangeListener(startDateValueChangeListener);
        return f;
    }

    private DateField createEndDateField() {
        DateField f = new DateField("End date");
        f.setResolution(Resolution.SECOND);
        f.setImmediate(true);
        f.addValueChangeListener(endDateValueChangeListener);
        return f;
    }

    private boolean validateResolutionChange(
            final org.tltv.gantt.client.shared.Resolution res) {
        long max = 5 * 12 * 4 * 7 * 24 * 3600000L;
        if (res == org.tltv.gantt.client.shared.Resolution.Hour
                && (gantt.getEndDate().getTime() - gantt.getStartDate()
                .getTime()) > max) {

            // revert to previous resolution
            setResolution(gantt.getResolution());

            // make user to confirm hour resolution, if the timeline range is
            // more than one week long.
            Util.showConfirmationPopup(
                    "Timeline range is a quite long for hour resolution. Rendering may be slow. Continue anyway?",
                    new Runnable() {

                        @Override
                        public void run() {
                            setResolution(res);
                            gantt.setResolution(res);
                        }
                    });
            return false;
        }
        return true;
    }

    private void setResolution(
            org.tltv.gantt.client.shared.Resolution resolution) {
        reso.removeValueChangeListener(resolutionValueChangeListener);
        try {
            reso.setValue(resolution);
        } finally {
            reso.addValueChangeListener(resolutionValueChangeListener);
        }
    }

    private MenuBar controlsMenuBar() {
        MenuBar menu = new MenuBar();
        MenuItem editItem = menu.addItem("Edit", null);
        MenuItem formatItem = menu.addItem("Format", null);
        MenuItem viewItem = menu.addItem("View", null);

        MenuItem item = editItem.addItem("Enabled", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setEnabled(!gantt.isEnabled());
                selectedItem.setChecked(gantt.isEnabled());
            }
        });
        item.setCheckable(true);
        item.setChecked(gantt.isEnabled());

        item = editItem.addItem("Read-only", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setReadOnly(!gantt.isReadOnly());
                selectedItem.setChecked(gantt.isReadOnly());
            }
        });
        item.setCheckable(true);
        item.setChecked(gantt.isReadOnly());

        item = formatItem.addItem("Set 'MMM' month format", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setTimelineMonthFormat("MMM");
            }
        });
        item = formatItem.addItem("Set 'MMMM' month format", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setTimelineMonthFormat("MMMM");
            }
        });
        item = formatItem.addItem("Set 'yyyy MMMM' month format",
                new Command() {

                    @Override
                    public void menuSelected(MenuItem selectedItem) {
                        gantt.setTimelineMonthFormat("yyyy MMMM");
                    }
                });
        item = formatItem.addItem("Set 'dd.' week format", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setTimelineWeekFormat("dd.");
            }
        });
        item = formatItem.addItem("Set week number week format", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setTimelineWeekFormat(null);
            }
        });
        item = formatItem.addItem(
                "Set 'dd. EEEE' day format for Hour resolution", new Command() {

                    @Override
                    public void menuSelected(MenuItem selectedItem) {
                        gantt.setTimelineDayFormat("dd. EEEE");
                    }
                });

        item = viewItem.addItem("Show years", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setYearsVisible(!gantt.isYearsVisible());
                selectedItem.setChecked(gantt.isYearsVisible());
            }
        });
        item.setCheckable(true);
        item.setChecked(gantt.isYearsVisible());

        item = viewItem.addItem("Show months", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                gantt.setMonthsVisible(!gantt.isMonthsVisible());
                selectedItem.setChecked(gantt.isMonthsVisible());
            }
        });
        item.setCheckable(true);
        item.setChecked(gantt.isMonthsVisible());

        item = viewItem.addItem("Show Gantt with Table", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                DashboardUI.getCurrent().getUI().getPage().setLocation("#table");
                DashboardUI.getCurrent().getUI().getPage().reload();
            }
        });
        item = viewItem.addItem("Show Gantt with TreeTable", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                DashboardUI.getCurrent().getUI().getPage().setLocation("#treetable");
                DashboardUI.getCurrent().getUI().getPage().reload();
            }
        });
        item = viewItem.addItem("Show Gantt alone", new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                DashboardUI.getCurrent().getUI().getPage().setLocation("#");
                DashboardUI.getCurrent().getUI().getPage().reload();
            }
        });

        return menu;
    }

    private void openStepEditor(AbstractStep step) {
        final Window win = new Window("More Info");
        win.setResizable(false);
        win.center();


        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);
        win.setContent(content);

        String taskName = step.getCaption();
        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        UserStory userStory = userStoryDAO.getCurrentWorkingUserStory(project);

        TaskDAO taskDAO = (TaskDAO)DashboardUI.context.getBean("Task");
        Task task1 = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(),taskName);


        TextField userStoryNameField = new TextField("Task Name");
        userStoryNameField.setValue(task1.getName());

        TextField userStoryPriority = new TextField("Priority");
        userStoryPriority.setValue(String.valueOf(task1.getPriority()));

        TextField userStoryState = new TextField("State");
        userStoryState.setValue(task1.getState());

        TextField projectName = new TextField("Project Name");
        projectName.setValue(project.getName());

        TextField userStoryName = new TextField("UserStoryName Name");
        userStoryName.setValue(userStory.getName());


        content.addComponent(userStoryNameField);
        content.addComponent(userStoryPriority);
        content.addComponent(userStoryState);
        content.addComponent(projectName);
        content.addComponent(userStoryName);




        Button ok = new Button("Ok", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                win.close();
            }

        });

        content.addComponent(ok);
        win.setClosable(true);

        DashboardUI.getCurrent().getUI().addWindow(win);
    }





}
