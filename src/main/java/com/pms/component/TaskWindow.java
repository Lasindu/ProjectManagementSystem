package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import com.vaadin.data.Item;
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




    private UserStory userStory;
    private Collection<Task> userStoryTasks;

    private final BeanFieldGroup<Task> fieldGroup;
    private Task task;
    private boolean editmode=false;

    @PropertyId("name")
    private TextField name;
    @PropertyId("description")
    private TextArea description;
    @PropertyId("priority")
    private ComboBox priority;
    @PropertyId("severity")
    private ComboBox severity;
    private ListSelect preRequisitsList;
    //private ListSelect dependancyList;
    @PropertyId("memberType")
    private TextField memberType;
    @PropertyId("estimateTime")
    private TextField estimateTime;
    @PropertyId("assignedTo")
    private TextField assignedTo;
    @PropertyId("completeTime")
    private TextField completeTime;
    @PropertyId("isCr")
    private OptionGroup isCr;


    private TaskWindow (Task task)
    {
        this.userStory=task.getUserStory();
        userStoryTasks=userStory.getUserStoryTasks();
        this.task=task;


        if(!task.getName().equals(""))
        {
            editmode=true;
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


    }



    private Component buildTask()
    {

        FormLayout taskForm = new FormLayout();
        taskForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        taskForm.setCaption("Task");
        taskForm.setMargin(new MarginInfo(true, true, true, true));
        taskForm.setSpacing(true);

        name = new TextField("Task Name");
        name.setNullRepresentation("");
        taskForm.addComponent(name);

        description = new TextArea("Task Description");
        description.setNullRepresentation("");
        taskForm.addComponent(description);

        priority = new ComboBox("Priority");
        priority.addItem(1);
        priority.addItem(2);
        priority.addItem(3);
        priority.addItem(4);
        priority.addItem(5);
        taskForm.addComponent(priority);

        severity = new ComboBox("Severity");
        severity.addItem(1);
        severity.addItem(2);
        severity.addItem(3);
        severity.addItem(4);
        severity.addItem(5);
        taskForm.addComponent(severity);

        preRequisitsList = new ListSelect("Pre Requisits");
        preRequisitsList.setWidth("400px");
        preRequisitsList.setNullSelectionAllowed(true);
        for (Task task : userStoryTasks) {
            preRequisitsList.addItem(task.getName());
        }
        preRequisitsList.setMultiSelect(true);
        preRequisitsList.setRows(7);
        taskForm.addComponent(preRequisitsList);

   /*     dependancyList = new ListSelect("Dependancy");
        for (Task task : userStoryTasks) {
            dependancyList.addItem(task.getName());
        }
        dependancyList.setWidth("400px");
        dependancyList.setNullSelectionAllowed(true);
        dependancyList.setMultiSelect(true);
        dependancyList.setRows(7);
        taskForm.addComponent(dependancyList);*/

        memberType= new TextField("Member Type");
        memberType.setNullRepresentation("");
        taskForm.addComponent(memberType);

        estimateTime= new TextField("Estimate Time");
        estimateTime.setNullRepresentation("");
        taskForm.addComponent(estimateTime);

        assignedTo = new TextField("Assinged to");
        assignedTo.setNullRepresentation("");
        taskForm.addComponent(assignedTo);

        completeTime= new TextField("Complete Time");
        completeTime.setNullRepresentation("");
        taskForm.addComponent(completeTime);

        isCr = new OptionGroup("Is Cr");
        isCr.addItem(Boolean.TRUE);
        isCr.addItem(Boolean.FALSE);
        isCr.addStyleName("horizontal");
        taskForm.addComponent(isCr);


        return  taskForm;
    }

    private Component buildFooter() {

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);


        Button cancelButton = new Button("Cancel");

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {close();
            }
        });

        Button submitButton;
        if (editmode)
        {
            submitButton = new Button("Update Task");
        }
        else
        {
            submitButton = new Button("Create New Task");

        }

        submitButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                try {


                    fieldGroup.commit();
                    Task task;
                    task= fieldGroup.getItemDataSource().getBean();
                    task.setUserStory(userStory);

                    if (editmode)
                    {

                    }
                    else
                    {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        task.setDate(dateFormat.format(date).toString());

                    }



                    UserStoryDAO userStoryDAO= (UserStoryDAO)DashboardUI.context.getBean("UserStory");
                    userStory.getUserStoryTasks().add(task);
                    userStoryDAO.updateUserStory(userStory);



                    if (editmode)
                    {
                        Notification success = new Notification(
                                "Task Updated successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    }
                    else
                    {
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
                    Notification.show("Error while creating User Story",
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });


        footer.addComponent(submitButton);
        footer.addComponent(cancelButton);

        footer.setExpandRatio(cancelButton,1);

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
