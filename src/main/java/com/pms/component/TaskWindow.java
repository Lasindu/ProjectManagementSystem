package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import com.vaadin.data.Item;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
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



    private Task task;
    private UserStory userStory;
    private Collection<Task> userStoryTasks;

    private TextField name;
    private TextArea description;
    private ComboBox priority;
    private ComboBox severity;
    private ListSelect preRequisits;
    private ListSelect dependancy;
    private TextField memberType;
    private TextField estimateTime;
    private TextField assignedTo;
    private TextField completeTime;
    private ComboBox isCr;


    private TaskWindow (UserStory userStory)
    {
        this.userStory=userStory;
        userStoryTasks=userStory.getUserStoryTasks();


        addStyleName("profile-window");
        setCaption("New Task");
        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        Panel mainPanel = new Panel("");
        mainPanel.setHeight(90.0f, Unit.PERCENTAGE);
        mainPanel.setHeightUndefined();
        mainPanel.setContent(buildTask());
        mainPanel.getContent().setSizeUndefined();
        setContent(mainPanel);






    }

    private Component buildTask()
    {

        FormLayout taskForm = new FormLayout();
        taskForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        taskForm.setCaption("Task");
        taskForm.setSizeFull();
        taskForm.setMargin(new MarginInfo(true, true, true, true));
        taskForm.setSpacing(true);

        name = new TextField("Task Name");
        taskForm.addComponent(name);

        description = new TextArea("Task Description");
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

        preRequisits = new ListSelect("Pre Requisits");
        preRequisits.setWidth("400px");
        preRequisits.setNullSelectionAllowed(true);
        for (Task task : userStoryTasks) {
            preRequisits.addItem(task.getName());
        }
        preRequisits.setMultiSelect(true);
        preRequisits.setRows(7);
        taskForm.addComponent(preRequisits);

        dependancy = new ListSelect("Dependancy");
        for (Task task : userStoryTasks) {
            dependancy.addItem(task.getName());
        }
        dependancy.setWidth("400px");
        dependancy.setNullSelectionAllowed(true);
        dependancy.setMultiSelect(true);
        dependancy.setRows(7);
        taskForm.addComponent(dependancy);

        memberType= new TextField("Member Type");
        taskForm.addComponent(memberType);

        estimateTime= new TextField("Estimate Time");
        taskForm.addComponent(estimateTime);

        assignedTo = new TextField("Assinged to");
        taskForm.addComponent(assignedTo);

        completeTime= new TextField("Complete Time");
        taskForm.addComponent(completeTime);

        isCr = new ComboBox("Is CR");
        isCr.addItem("true");
        isCr.addItem("false");
        taskForm.addComponent(isCr);

        taskForm.addComponent(buildFooter());


        return  taskForm;
    }

    private Component buildFooter() {

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setMargin(true);
        buttonsLayout.setSpacing(true);


        Button cancelButton = new Button("Cancel");

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                close();


            }
        });
        Button addNewButton = new Button("Create New Task");
        addNewButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

            /*    try {


                    fieldGroup.commit();*/

                task= new Task();
                task.setName(name.getValue().toString());
                task.setDescription(description.getValue().toString());

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                task.setDate(dateFormat.format(date).toString());

                task.setPriority(Integer.parseInt(priority.getValue().toString()));
                task.setSeverity(Integer.parseInt(severity.getValue().toString()));
                task.setMemberType(memberType.getValue().toString());
                task.setEstimateTime(estimateTime.getValue().toString());
                task.setAssignedTo(assignedTo.getValue().toString());
                task.setCompleteTime(completeTime.getValue().toString());

                if (isCr.getValue().toString().equals("false"))
                    task.setCr(false);
                else
                    task.setCr(true);

                task.setUserStory(userStory);

                userStory.getUserStoryTasks().add(task);

                UserStoryDAO userStoryDAO= (UserStoryDAO)DashboardUI.context.getBean("UserStory");
                userStoryDAO.updateUserStory(userStory);




            /*    ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");
                project.getProjectUserStories().add(userStory);
                projectDAO.updateProject(project);*/


                Notification success = new Notification(
                        "Task Created successfully");
                success.setDelayMsec(2000);
                success.setStyleName("bar success small");
                success.setPosition(Position.BOTTOM_CENTER);
                success.show(Page.getCurrent());


                close();
                Page.getCurrent().reload();

            /*    } catch (FieldGroup.CommitException e) {
                    Notification.show("Error while creating User Story",
                            Notification.Type.ERROR_MESSAGE);
                }*/
            }
        });


        buttonsLayout.addComponent(cancelButton);
        buttonsLayout.addComponent(addNewButton);

        return buttonsLayout;
    }





    public static void open(UserStory userStory) {
        Window w = new TaskWindow(userStory);
        UI.getCurrent().addWindow(w);
        w.focus();

    }


}
