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
package com.pms.component.ganttchart;

import com.pms.DashboardUI;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.DateToLongConverter;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
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
import org.tltv.gantt.client.shared.SubStep;
import com.pms.component.ganttchart.util.UriFragmentWrapperFactory;
import com.pms.component.ganttchart.util.Util;

import javax.servlet.annotation.WebServlet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class DemoUI  {

    private Gantt gantt;

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
            org.tltv.gantt.client.shared.Resolution res = (org
                    .tltv.gantt.client.shared.Resolution) event
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


    public Component init() {
        ganttListener = null;
        createGantt();

        MenuBar menu = controlsMenuBar();
        Panel controls = createControls();

        TabSheet tabsheet = new TabSheet();
        tabsheet.setSizeFull();

        Component wrapper = UriFragmentWrapperFactory.wrapByUriFragment(UI
                .getCurrent().getPage().getUriFragment(), gantt);
        if (wrapper instanceof GanttListener) {
            ganttListener = (GanttListener) wrapper;
        }

        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.addComponent(menu);
        layout.addComponent(controls);
        layout.addComponent(wrapper);
        layout.setExpandRatio(wrapper, 1);

        return  layout;
    }

    private void createGantt() {
        gantt = new Gantt();
        gantt.setWidth(100, Sizeable.Unit.PERCENTAGE);
        gantt.setHeight(500, Sizeable.Unit.PIXELS);
        gantt.setResizableSteps(true);
        gantt.setMovableSteps(true);
        gantt.addAttachListener(ganttAttachListener);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        gantt.setStartDate(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        gantt.setEndDate(cal.getTime());

        cal.setTime(new Date());
        Step step1 = new Step("First step");
        step1.setDescription("Description tooltip");
        step1.setStartDate(cal.getTime().getTime());
        cal.add(Calendar.MONTH, 2);
        step1.setEndDate(cal.getTime().getTime());

        Step step2 = new Step("Second step");
        step2.setDescription("Description tooltip for second step");
        cal.add(Calendar.DATE, 1);
        step2.setStartDate(cal.getTime().getTime());
        cal.add(Calendar.MONTH, 4);
        step2.setEndDate(cal.getTime().getTime());
        step2.setPredecessor(step1);

        Step step3 = new Step("Third step");
        step3.setDescription("<b>HTML</b> <i>content</i> is <u>supported</u> in tooltips.");
        cal.add(Calendar.DATE, 1);
        step3.setStartDate(cal.getTime().getTime());
        cal.add(Calendar.MONTH, 12);
        step3.setEndDate(cal.getTime().getTime());
        step3.setPredecessor(step2);

        Step step4 = new Step("Fourth step");
        step4.setDescription("Tooltip is <b>VTooltip</b>. <p>Looks same for all Vaadin components.");
        step4.setStartDate(step2.getStartDate());
        step4.setEndDate(step2.getEndDate());
        step4.setPredecessor(step1);

        Step stepWithSubSteps = new Step("Step with sub-steps");
        stepWithSubSteps.setDescription("Tooltip for Step with sub-steps");

        cal.setTime(new Date(step1.getStartDate()));
        cal.add(Calendar.DATE, 7);

        SubStep subStep1 = new SubStep("Sub-step A");
        subStep1.setDescription("Tooltip for Sub-step A");
        subStep1.setBackgroundColor("A8D9DD");
        subStep1.setStartDate(step1.getStartDate());
        subStep1.setEndDate(cal.getTime());

        SubStep subStep2 = new SubStep("Sub-step B");
        subStep2.setDescription("Tooltip for Sub-step B");
        subStep2.setBackgroundColor("A8D9BB");
        subStep2.setStartDate(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        subStep2.setEndDate(cal.getTime());

        SubStep subStep3 = new SubStep("Sub-step C");
        subStep3.setDescription("Tooltip for Sub-step C");
        subStep3.setBackgroundColor("A8D999");
        subStep3.setStartDate(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        subStep3.setEndDate(step1.getEndDate());

        stepWithSubSteps.addSubStep(subStep1);
        stepWithSubSteps.addSubStep(subStep2);
        stepWithSubSteps.addSubStep(subStep3);

        gantt.addStep(step1);
        gantt.addStep(step2);
        gantt.addStep(step3);
        gantt.addStep(step4);
        gantt.addStep(stepWithSubSteps);

        String[] colors = new String[] { "11FF11", "33FF33", "55FF55",
                "77FF77", "99FF99", "BBFFBB", "DDFFDD" };

        cal.setTime(new Date());
        for (int i = 0; i < 10; i++) {
            Step step = new Step("Step " + i);
            step.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, 14);
            step.setEndDate(cal.getTime().getTime());
            step.setBackgroundColor(colors[i % colors.length]);
            gantt.addStep(step);
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
        controls.addComponent(localeSelect);
        controls.addComponent(timezoneSelect);
        controls.addComponent(heightAndUnit);
        controls.addComponent(widthAndUnit);
        controls.addComponent(createStep);
        controls.setComponentAlignment(createStep, Alignment.MIDDLE_LEFT);

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
        final Window win = new Window("Step Editor");
        win.setResizable(false);
        win.center();

        final Collection<Component> hidden = new ArrayList<Component>();

        BeanItem<AbstractStep> item = new BeanItem<AbstractStep>(step);

        final FieldGroup group = new FieldGroup(item);
        group.setBuffered(true);

        TextField captionField = new TextField("Caption");
        captionField.setNullRepresentation("");
        group.bind(captionField, "caption");

        TextField descriptionField = new TextField("Description");
        descriptionField.setNullRepresentation("");
        group.bind(descriptionField, "description");
        descriptionField.setVisible(false);
        hidden.add(descriptionField);

        NativeSelect captionMode = new NativeSelect("Caption Mode");
        captionMode.addItem(Step.CaptionMode.TEXT);
        captionMode.addItem(Step.CaptionMode.HTML);
        group.bind(captionMode, "captionMode");
        captionMode.setVisible(false);
        hidden.add(captionMode);

        final NativeSelect parentStepSelect = new NativeSelect("Parent Step");
        parentStepSelect.setEnabled(false);
        if (!gantt.getSteps().contains(step)) {
            // new step
            parentStepSelect.setEnabled(true);
            for (Step parentStepCanditate : gantt.getSteps()) {
                parentStepSelect.addItem(parentStepCanditate);
                parentStepSelect.setItemCaption(parentStepCanditate,
                        parentStepCanditate.getCaption());
                if (step instanceof SubStep) {
                    if (parentStepCanditate.getSubSteps().contains(step)) {
                        parentStepSelect.setValue(parentStepCanditate);
                        parentStepSelect.setEnabled(false);
                        break;
                    }
                }
            }
        }
        parentStepSelect.setVisible(false);
        hidden.add(parentStepSelect);

        TextField bgField = new TextField("Background color");
        bgField.setNullRepresentation("");
        group.bind(bgField, "backgroundColor");
        bgField.setVisible(false);
        hidden.add(bgField);

        DateField startDate = new DateField("Start date");
        startDate.setLocale(gantt.getLocale());
        startDate.setTimeZone(gantt.getTimeZone());
        startDate.setResolution(Resolution.SECOND);
        startDate.setConverter(new DateToLongConverter());
        group.bind(startDate, "startDate");

        DateField endDate = new DateField("End date");
        endDate.setLocale(gantt.getLocale());
        endDate.setTimeZone(gantt.getTimeZone());
        endDate.setResolution(Resolution.SECOND);
        endDate.setConverter(new DateToLongConverter());
        group.bind(endDate, "endDate");

        CheckBox showMore = new CheckBox("Show all settings");
        showMore.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                for (Component c : hidden) {
                    c.setVisible((Boolean) event.getProperty().getValue());
                }
                win.center();
            }
        });

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        content.setSpacing(true);
        win.setContent(content);

        content.addComponent(captionField);
        content.addComponent(captionMode);
        content.addComponent(descriptionField);
        content.addComponent(parentStepSelect);
        content.addComponent(bgField);
        content.addComponent(startDate);
        content.addComponent(endDate);
        content.addComponent(showMore);

        HorizontalLayout buttons = new HorizontalLayout();
        content.addComponent(buttons);

        Button ok = new Button("Ok", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    group.commit();
                    AbstractStep step = ((BeanItem<AbstractStep>) group
                            .getItemDataSource()).getBean();
                    gantt.markStepDirty(step);
                    if (parentStepSelect.isEnabled()
                            && parentStepSelect.getValue() != null) {
                        SubStep subStep = addSubStep(parentStepSelect, step);
                        step = subStep;
                    }
                    if (step instanceof Step
                            && !gantt.getSteps().contains(step)) {
                        gantt.addStep((Step) step);
                    }
                    if (ganttListener != null && step instanceof Step) {
                        ganttListener.stepModified((Step) step);
                    }
                    win.close();
                } catch (CommitException e) {
                    Notification.show("Commit failed", e.getMessage(),
                            Type.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }

            private SubStep addSubStep(final NativeSelect parentStepSelect,
                    AbstractStep dataSource) {
                SubStep subStep = new SubStep();
                subStep.setCaption(dataSource.getCaption());
                subStep.setCaptionMode(dataSource.getCaptionMode());
                subStep.setStartDate(dataSource.getStartDate());
                subStep.setEndDate(dataSource.getEndDate());
                subStep.setBackgroundColor(dataSource.getBackgroundColor());
                subStep.setDescription(dataSource.getDescription());
                subStep.setStyleName(dataSource.getStyleName());
                ((Step) parentStepSelect.getValue()).addSubStep(subStep);
                return subStep;
            }
        });
        Button cancel = new Button("Cancel", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                group.discard();
                win.close();
            }
        });
        Button delete = new Button("Delete", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                AbstractStep step = ((BeanItem<AbstractStep>) group
                        .getItemDataSource()).getBean();
                if (step instanceof SubStep) {
                    SubStep substep = (SubStep) step;
                    substep.getOwner().removeSubStep(substep);
                } else {
                    gantt.removeStep((Step) step);
                    if (ganttListener != null) {
                        ganttListener.stepDeleted((Step) step);
                    }
                }
                win.close();
            }
        });
        buttons.addComponent(ok);
        buttons.addComponent(cancel);
        buttons.addComponent(delete);
        win.setClosable(true);

        DashboardUI.getCurrent().getUI().addWindow(win);
    }
}
