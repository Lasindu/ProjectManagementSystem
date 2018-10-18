package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by Upulie on 4/24/2015.
 */
public class TaskWindow extends Window {


    UserStory userStory;
    private Collection<Task> userStoryTasks;

    private final BeanFieldGroup<Task> fieldGroup;
    private Task task;
    private boolean editmode = false;

    @PropertyId("name")
    private TextField name;
    @PropertyId("description")
    private TextArea description;
    @PropertyId("priority")
    private ComboBox priority;
    @PropertyId("severity")
    private ComboBox severity;
    private OptionGroup preRequisitsList;
    @PropertyId("memberType")
    private ComboBox memberType;
    @PropertyId("estimateTime")
    private TextField estimateTime;
    @PropertyId("assignedTo")
    private TextField assignedTo;
    @PropertyId("completeTime")
    private TextField completeTime;
    @PropertyId("state")
    private ComboBox state;
    @PropertyId("isCr")
    private OptionGroup isCr;
    private OptionGroup dependencyList;

    private OptionGroup technicalSkillsList;
    private ComboBox domainSkill;


    private TaskWindow(Task task) {
        this.userStory = task.getUserStory();
        userStoryTasks = userStory.getUserStoryTasks();
        this.task = task;


        if (!task.getName().equals("")) {
            editmode = true;
        }


        addStyleName("profile-window");
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        setContent(content);

        TabSheet detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1f);


        detailsWrapper.addComponent(buildTask());
        content.addComponent(buildFooter());


        fieldGroup = new BeanFieldGroup<Task>(Task.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(task);

        if(editmode)
        {
            fieldGroup.unbind(name);
            name.setValue(task.getName());
            name.setReadOnly(true);
        }


    }


    private Component buildTask() {

        final FormLayout taskForm = new FormLayout();
        taskForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        taskForm.setCaption("Task");
        taskForm.setMargin(new MarginInfo(true, true, true, true));
        taskForm.setSpacing(true);

        name = new TextField("Task Name");
        name.setNullRepresentation("");
        name.setRequired(true);
        taskForm.addComponent(name);

        priority = new ComboBox("Priority");
        priority.addItem(1);
        priority.addItem(2);
        priority.addItem(3);
        priority.addItem(4);
        priority.addItem(5);
        priority.setRequired(true);
        taskForm.addComponent(priority);

        estimateTime = new TextField("Estimate Time (Hours)");
        estimateTime.setNullRepresentation("");
        estimateTime.setRequired(true);
        taskForm.addComponent(estimateTime);

        completeTime = new TextField("Complete Time");
        completeTime.setNullRepresentation("");
        taskForm.addComponent(completeTime);

        state = new ComboBox("State");
        state.addItem("initial");
        state.addItem("working");
        state.addItem("done");
        taskForm.addComponent(state);

        severity = new ComboBox("Severity");
        severity.addItem(1);
        severity.addItem(2);
        severity.addItem(3);
        severity.addItem(4);
        severity.addItem(5);
        taskForm.addComponent(severity);

        preRequisitsList = new OptionGroup("Pre Requisits");
        preRequisitsList.setWidth("400px");
        preRequisitsList.setNullSelectionAllowed(true);
        for (Task task1 : userStoryTasks) {

            preRequisitsList.addItem(task1.getName());

        }
        preRequisitsList.setMultiSelect(true);

        Panel preRequestPanel = new Panel("");
        preRequestPanel.setHeight("100px");
        preRequestPanel.setContent(preRequisitsList);
        VerticalLayout preRequistLayout = new VerticalLayout();
        preRequistLayout.setCaption("Pre Requisits");
        preRequistLayout.addComponent(preRequestPanel);
        taskForm.addComponent(preRequistLayout);

        if (editmode) {

            preRequisitsList.removeItem(task.getName());

            String[] preRquisitList = task.getPreRequisits().split(",");

            for (String preRequistit : preRquisitList) {
                preRequisitsList.select(preRequistit);
            }

            //task1.setReadOnly(true);

        }


        description = new TextArea("Task Description");
        description.setNullRepresentation("");
        taskForm.addComponent(description);



        memberType = new ComboBox("Member Type");
        memberType.addItem("Dev");
        memberType.addItem("QA");
        memberType.addItem("System Engineer");
        memberType.setRequired(true);
        taskForm.addComponent(memberType);

        assignedTo = new TextField("Assinged to");
        assignedTo.setNullRepresentation("");
        taskForm.addComponent(assignedTo);






        isCr = new OptionGroup("Change Request");
        isCr.addItem(Boolean.TRUE);
        isCr.addItem(Boolean.FALSE);
        if(!editmode)
        isCr.setValue(Boolean.FALSE);
        else
        {
            if(task.isCr())
                isCr.setValue(Boolean.TRUE);
            else
                isCr.setValue(Boolean.FALSE);
        }
        isCr.addStyleName("horizontal");
        taskForm.addComponent(isCr);



        dependencyList = new OptionGroup("Dependency");
        dependencyList.setWidth("400px");
        dependencyList.setNullSelectionAllowed(true);
        for (Task task1 : userStoryTasks) {

            dependencyList.addItem(task1.getName());

        }
        dependencyList.setMultiSelect(true);

        Panel dependencyPanel = new Panel("");
        dependencyPanel.setHeight("100px");
        dependencyPanel.setContent(dependencyList);
        final VerticalLayout dependencyLayout = new VerticalLayout();
        dependencyLayout.setCaption("Dependency");
        dependencyLayout.addComponent(dependencyPanel);
        taskForm.addComponent(dependencyLayout);

        dependencyLayout.setVisible(false);

        if (editmode) {

            if(task.isCr())
                dependencyLayout.setVisible(true);

            dependencyList.removeItem(task.getName());

            if( task.getDependancy() != null && ! task.getDependancy().isEmpty())
            {
                String[] dependencyList1 = task.getDependancy().split(",");

                for (String dependency : dependencyList1) {
                    dependencyList.select(dependency);
                }

            }


            //task1.setReadOnly(true);

        }




        isCr.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                if(isCr.getValue().toString().equals("true"))
                {
                    dependencyLayout.setVisible(true);

                }
                else
                {
                    dependencyLayout.setVisible(false);

                }

                }
            });


        domainSkill = new ComboBox("Domain Skill");

        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        String domainSkillsList = taskDAO.getDomainSkillsOfTasks();

        if(domainSkillsList != null && !domainSkillsList.isEmpty())
        {
            for(String domainSkillString : domainSkillsList.split(","))
            {
                domainSkill.addItem(domainSkillString);
            }


            if(editmode)
            {
                if(task.getDomainSkill() != null && !task.getDomainSkill().isEmpty())
                if(domainSkillsList.contains(task.getDomainSkill()))
                {
                    domainSkill.setValue(task.getDomainSkill());
                }
            }
        }

        domainSkill.setRequired(true);
        taskForm.addComponent(domainSkill);


        technicalSkillsList = new OptionGroup("Technical Skills");
        technicalSkillsList.setWidth("400px");
        technicalSkillsList.setNullSelectionAllowed(true);

        String technicalSkillsOfTasks = taskDAO.getTechnicalSkillsOfTasks();

        if(technicalSkillsOfTasks != null && !technicalSkillsOfTasks.isEmpty())
        {
            for(String technicalSkillString : technicalSkillsOfTasks.split(","))
            {
                technicalSkillsList.addItem(technicalSkillString);
            }

        }
        technicalSkillsList.setMultiSelect(true);

        Panel technicalSkillsPanel = new Panel("");
        technicalSkillsPanel.setHeight("100px");
        technicalSkillsPanel.setContent(technicalSkillsList);
        final VerticalLayout technicalSkillsLayout = new VerticalLayout();
        technicalSkillsLayout.setCaption("Technical Skills");
        technicalSkillsLayout.addComponent(technicalSkillsPanel);
        taskForm.addComponent(technicalSkillsLayout);


        if(editmode)
        {
            if(task.getTechnicalSkills()!= null && !task.getTechnicalSkills().isEmpty())
            {
                for(String taskTechnicalSkill : task.getTechnicalSkills().split(","))
                {
                    if(technicalSkillsOfTasks.contains(taskTechnicalSkill))
                    {
                        technicalSkillsList.select(taskTechnicalSkill);

                    }
                }
            }
        }



        return taskForm;
    }

    private Component buildFooter() {

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);


        Button cancelButton = new Button("Cancel");

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

        Button submitButton;
        if (editmode) {
            submitButton = new Button("Update Task");
        } else {
            submitButton = new Button("Create New Task");

        }

        submitButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                try {


                    fieldGroup.commit();
                    Task newTask;
                    newTask = fieldGroup.getItemDataSource().getBean();
                    newTask.setUserStory(userStory);


                    //bind fields manually that not bind to the feald grope
                    if(isCr.getValue().toString().equals("true"))
                        newTask.setCr(true);
                    else
                        newTask.setCr(false);

                    if(domainSkill.getValue() != null)
                    newTask.setDomainSkill(domainSkill.getValue().toString());



                    StringBuilder technicalSkillsString = new StringBuilder();
                    Set<Item> technicalSkillsValues = (Set<Item>) technicalSkillsList.getValue();
                    int indexcount = 1;
                    for (Object v : technicalSkillsValues) {

                        if (indexcount != 1 && !v.toString().isEmpty())
                            technicalSkillsString.append(",");
                        technicalSkillsString.append(v.toString());

                        ++indexcount;

                    }

                    newTask.setTechnicalSkills(technicalSkillsString.toString());




                    if (editmode) {

                        //because of the bug in vaaadin cannot set readonly to single filed so need to set name manually
                        //when update the task
                        newTask.setName(name.getValue().toString());



                        TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
                        String[] preRequistListBeforEdit = task.getPreRequisits().split(",");

                        for (String preReqsuist : preRequistListBeforEdit) {
                            if (!preRequisitsList.isSelected(preReqsuist)) {
                                for (Task task1 : userStoryTasks) {
                                    if (task1.getName().equals(preReqsuist)) {
                                        task1.setDependancy(task1.getDependancy().replace(task.getName(), ""));


                                        if (task1.getDependancy() != null && !task1.getDependancy().isEmpty()) {
                                            if (task1.getDependancy().startsWith(",")) {
                                                task1.setDependancy(task1.getDependancy().substring(1, task1.getDependancy().length()));
                                            } else if (task1.getDependancy().contains(",,")) {
                                                task1.setDependancy(task1.getDependancy().replace(",,", ","));
                                            } else if (task1.getDependancy().endsWith(",")) {
                                                task1.setDependancy(task1.getDependancy().substring(0, task1.getDependancy().length() - 1));
                                            }

                                            if (task1.getDependancy().isEmpty()) {
                                                task1.setDependancy(null);
                                            }


                                        }


                                        taskDAO.updateTask(task1);
                                        break;
                                    }
                                }


                            }
                        }



                        //handle if old task is cr
                        if(task.isCr())
                        {
                            String[] dependencyBeforeEdit = task.getDependancy().split(",");
                            for (String dependency : dependencyBeforeEdit) {
                                if (!preRequisitsList.isSelected(dependency)) {
                                    for (Task task1 : userStoryTasks) {
                                        if (task1.getName().equals(dependency)) {
                                            task1.setPreRequisits(task1.getPreRequisits().replace(task.getName(), ""));


                                            if (task1.getPreRequisits() != null && !task1.getPreRequisits().isEmpty()) {
                                                if (task1.getPreRequisits().startsWith(",")) {
                                                    task1.setPreRequisits(task1.getPreRequisits().substring(1, task1.getPreRequisits().length()));
                                                } else if (task1.getPreRequisits().contains(",,")) {
                                                    task1.setPreRequisits(task1.getPreRequisits().replace(",,", ","));
                                                } else if (task1.getPreRequisits().endsWith(",")) {
                                                    task1.setPreRequisits(task1.getPreRequisits().substring(0, task1.getPreRequisits().length() - 1));
                                                }

                                                if (task1.getPreRequisits().isEmpty()) {
                                                    task1.setPreRequisits(null);
                                                }


                                            }


                                            taskDAO.updateTask(task1);
                                            break;
                                        }
                                    }


                                }
                            }


                        }





                    } else {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        newTask.setDate(dateFormat.format(date).toString());

                        newTask.setState("initial");

                    }

                    TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");




                    //Check New UserStory Priority with Prerequisite Priority
                    int taskPriority =Integer.parseInt(priority.getValue().toString());

                    Set<Item> preRequisitsValues1 = (Set<Item>) preRequisitsList.getValue();

                    for (Object v : preRequisitsValues1) {

                        String preRequistName = v.toString();
                        Task task1 = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(), preRequistName);

                        if(taskPriority < task1.getPriority())
                        {
                            Notification notification = new Notification("Your Selected Priority is Incorrect ",
                                    "<br/>You have Prerequisite that has lowpriorityy than this Task",
                                    Notification.Type.ERROR_MESSAGE,true);

                            notification.show(Page.getCurrent());
                            return;

                        }


                    }


                    //set prereuist for tasks
                    StringBuilder preRequisitString = new StringBuilder();
                    Set<Item> preRequisitsValues = (Set<Item>) preRequisitsList.getValue();
                    int size2 = preRequisitsValues.size();
                    int index2 = 1;
                    for (Object v : preRequisitsValues) {

                        if (index2 != 1 && !v.toString().isEmpty())
                            preRequisitString.append(",");
                        preRequisitString.append(v.toString());

                        index2++;


                        //following section manually set dependency in other user stories if this task depend on them
                        for (Task task1 : userStory.getUserStoryTasks()) {
                            if (task1.getName().equals(v.toString())) {
                                if (task1.getDependancy() == null || task1.getDependancy().isEmpty()) {
                                    task1.setDependancy(newTask.getName());
                                } else {

                                    if(!task1.getDependancy().contains(newTask.getName()))
                                    {
                                        StringBuilder dependencyString1 = new StringBuilder();
                                        dependencyString1.append(task1.getDependancy());
                                        dependencyString1.append(',' + newTask.getName());

                                        task1.setDependancy(dependencyString1.toString());

                                    }


                                }

                                taskDAO.updateTask(task1);


                            }
                        }


                    }
                    newTask.setPreRequisits(preRequisitString.toString());







                    //handle cr dependency

                    if(isCr.getValue().toString().equals("true"))
                    {
                        StringBuilder dependencyString = new StringBuilder();
                        Set<Item> dependencyValues = (Set<Item>) dependencyList.getValue();
                        int size = dependencyList.size();
                        int index = 1;
                        for(Object v : dependencyValues)
                        {
                            if (index != 1 && !v.toString().isEmpty())
                                dependencyString.append(",");
                            dependencyString.append(v.toString());

                            index++;


                            for (Task task1 : userStory.getUserStoryTasks()) {
                                if (task1.getName().equals(v.toString())) {
                                    if (task1.getPreRequisits() == null || task1.getPreRequisits().isEmpty()) {
                                        task1.setPreRequisits(newTask.getName());
                                    } else {

                                        if(!task1.getPreRequisits().contains( newTask.getName()))
                                        {
                                            StringBuilder preReqisitStirng1 = new StringBuilder();
                                            preReqisitStirng1.append(task1.getPreRequisits());
                                            preReqisitStirng1.append(',' + newTask.getName());

                                            task1.setDependancy(preReqisitStirng1.toString());
                                        }


                                    }

                                    taskDAO.updateTask(task1);


                                }
                            }


                        }

                        newTask.setDependancy(dependencyString.toString());

                    }










                    UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
                    userStory.getUserStoryTasks().add(newTask);
                    userStoryDAO.updateUserStory(userStory);


                    if (editmode) {
                        Notification success = new Notification(
                                "Task Updated successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    } else {
                        Notification success = new Notification(
                                "Task Created successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    }

                    close();
                    Page.getCurrent().reload();


                } catch (FieldGroup.CommitException e) {
                    Notification.show("Error while creating Task please check required fields",
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });


        footer.addComponent(submitButton);
        footer.addComponent(cancelButton);

        footer.setExpandRatio(cancelButton, 1);

        footer.setComponentAlignment(cancelButton, Alignment.TOP_RIGHT);
        footer.setComponentAlignment(submitButton, Alignment.TOP_RIGHT);


        return footer;
    }


    public static void open(Task task) {
        Window w = new TaskWindow(task);
        UI.getCurrent().addWindow(w);
        w.focus();

    }


}
