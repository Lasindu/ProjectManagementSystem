package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Task;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Created by Upulie on 6/2/2015.
 */
public class ViewUserStory extends CustomComponent {

    private String userRole;
    public VerticalLayout viewUserStoryLayout;
    private String projectName;
    private String userStoryName;
    private Button create;
    private Table tasksTable;
    private UserStory userStory;

    public ViewUserStory(String projectName,String userStoryName)
    {
        this.projectName=projectName;
        this.userStoryName=userStoryName;
        buildViewUserStory();

    }


    public Component getUserStory()
    {
        return viewUserStoryLayout;

    }


    private void buildViewUserStory()
    {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        userRole=user.getRole();


        viewUserStoryLayout = new VerticalLayout();
        //viewUserStoryLayout.setCaption("View Project");
        viewUserStoryLayout.setMargin(true);
        viewUserStoryLayout.setSpacing(true);
        viewUserStoryLayout.setSizeFull();



        UserStoryDAO userStoryDAO=(UserStoryDAO) DashboardUI.context.getBean("UserStory");
        userStory= userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(projectName,userStoryName);

        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label(userStory.getProject().getName()+" - "+userStory.getName());
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        viewUserStoryLayout.addComponent(header);

        if(userStory!=null)
        {
            Label name = new Label("User Story Name :   "+userStory.getName());
            viewUserStoryLayout.addComponent(name);
            Label description= new Label("Description :   "+userStory.getDescription());
            viewUserStoryLayout.addComponent(description);
            Label domain= new Label("Domain :   "+userStory.getDomain());
            viewUserStoryLayout.addComponent(domain);
            Label priority= new Label("Priority :   "+userStory.getPriority());
            viewUserStoryLayout.addComponent(priority);
            Label assignedSprint= new Label("Assigned Sprint :   "+userStory.getAssignedSprint());
            viewUserStoryLayout.addComponent(assignedSprint);
            Label preRequisits= new Label("Pre Requisits :   "+userStory.getPreRequisits());
            viewUserStoryLayout.addComponent(preRequisits);
            Label dependancy= new Label("Dependancy :   "+userStory.getDependancy());
            viewUserStoryLayout.addComponent(dependancy);
            Label state= new Label("UserStory State :   "+userStory.getState());
            viewUserStoryLayout.addComponent(state);
            Label releasedDate= new Label("Released Date :   "+userStory.getReleasedDate());
            viewUserStoryLayout.addComponent(releasedDate);
            Label changedRequest= new Label("Changed Request :   "+userStory.isCR());
            viewUserStoryLayout.addComponent(changedRequest);



            viewUserStoryLayout.addComponent(buildToolbar());


            tasksTable= new Table("");
            tasksTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
            tasksTable.addStyleName(ValoTheme.TABLE_COMPACT);
            tasksTable.setSelectable(true);

            tasksTable.addContainerProperty("Index", Integer.class, null);
            tasksTable.addContainerProperty("Name",  String.class, null);
            tasksTable.addContainerProperty("Priority", Integer.class, null);
            tasksTable.addContainerProperty("Severity", Integer.class, null);
            tasksTable.addContainerProperty("Member Type", String.class, null);
            tasksTable.addContainerProperty("Estimate Time", String.class, null);
            tasksTable.addContainerProperty("Assigned To", String.class, null);
            tasksTable.addContainerProperty("Complete Time", String.class, null);


            tasksTable.addContainerProperty("View Task", Button.class, null);
            tasksTable.addContainerProperty("Edit Task", Button.class, null);
            if (userRole.equals("admin")||userRole.equals("pm")||userRole.equals("architect"))
            {
                tasksTable.addContainerProperty("Remove Task", Button.class, null);

            }



            tasksTable.setSizeFull();

            int index=0;
            for(Task task:userStory.getUserStoryTasks())
            {
                index++;


                if (userRole.equals("admin")||userRole.equals("pm")||userRole.equals("architect"))
                {
                    Button removeTaskButton=new Button("Remove Task");
                    Button editTaskButton=new Button("Edit Task");
                    Button viewTaskButton=new Button("View Task");

                    removeTaskButton.setData(task);
                    editTaskButton.setData(task);
                    viewTaskButton.setData(userStory.getProject().getName()+"/"+userStory.getName()+"/"+task.getTaskId());

                    tasksTable.addItem(new Object[] {index,task.getName(),task.getPriority(),task.getSeverity(),task.getMemberType(),task.getEstimateTime(),task.getAssignedTo(),task.getCompleteTime(),viewTaskButton,editTaskButton,removeTaskButton},index);

                    removeTaskButton.addClickListener(new Button.ClickListener() {
                        public void buttonClick(Button.ClickEvent event) {

                            final Task task= (Task)event.getButton().getData();
                            ConfirmDialog.show(DashboardUI.getCurrent(), "Please Confirm:", "Are you sure you want to delete task named :" + task.getName(),
                                    "I am", "Not quite", new ConfirmDialog.Listener() {

                                        public void onClose(ConfirmDialog dialog) {
                                            if (dialog.isConfirmed()) {
                                                // Confirmed to continue
                                                TaskDAO taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");




                                                //remove dependency
                                                String dependencyNameList = task.getDependancy();

                                                if(dependencyNameList!= null && !dependencyNameList.isEmpty() )
                                                    for(String taskName: dependencyNameList.split(","))
                                                    {
                                                        Task tempTask=taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(),taskName);

                                                        tempTask.setPreRequisits(tempTask.getPreRequisits().replace(task.getName(),""));

                                                        if(tempTask.getPreRequisits()!= null && !tempTask.getPreRequisits().isEmpty())
                                                        {
                                                            if(tempTask.getPreRequisits().startsWith(",,"))
                                                            {
                                                                tempTask.setPreRequisits(tempTask.getDependancy().substring(1, tempTask.getDependancy().length()));
                                                            }
                                                            else if(tempTask.getPreRequisits().contains(",,"))
                                                            {
                                                                tempTask.setPreRequisits(tempTask.getPreRequisits().replace(",,", ","));
                                                            }
                                                            else if(tempTask.getPreRequisits().endsWith(","))
                                                            {
                                                                tempTask.setPreRequisits(tempTask.getPreRequisits().substring(0, tempTask.getPreRequisits().length() - 1));
                                                            }

                                                            if(tempTask.getPreRequisits().isEmpty())
                                                                tempTask.setPreRequisits(null);

                                                        }



                                                        taskDAO.updateTask(tempTask);
                                                    }



                                                //remove prerequist
                                                String prerequiestNameList = task.getPreRequisits();

                                                if(prerequiestNameList!= null && !prerequiestNameList.isEmpty())
                                                    for(String taskName : prerequiestNameList.split(","))
                                                    {
                                                        Task tempTask = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(),taskName);

                                                        tempTask.setDependancy(tempTask.getDependancy().replace(task.getName(), ""));

                                                        if(tempTask.getDependancy()!= null && !tempTask.getDependancy().isEmpty()) {

                                                            if(tempTask.getDependancy().startsWith(","))
                                                            {
                                                                tempTask.setPreRequisits(tempTask.getDependancy().substring(1, tempTask.getDependancy().length()));
                                                            }

                                                            else if (tempTask.getDependancy().contains(",,")) {
                                                                tempTask.setDependancy(tempTask.getDependancy().replace(",,", ","));
                                                            }
                                                            else if (tempTask.getDependancy().endsWith(",")) {
                                                                tempTask.setDependancy(tempTask.getDependancy().substring(0, tempTask.getDependancy().length() - 1));
                                                            }

                                                            if(tempTask.getDependancy().isEmpty())
                                                                tempTask.setDependancy(null);
                                                        }

                                                        taskDAO.updateTask(tempTask);
                                                    }






                                                taskDAO.removeTask(task);
                                                Page.getCurrent().reload();

                                            } else {
                                                // User did not confirm

                                            }
                                        }
                                    });

                        }
                    });

                    editTaskButton.addClickListener(new Button.ClickListener() {
                        public void buttonClick(Button.ClickEvent event) {

                            TaskWindow.open((Task)event.getButton().getData());

                        }
                    });

                    viewTaskButton.addClickListener(new Button.ClickListener() {
                        public void buttonClick(Button.ClickEvent event) {

                            DashboardUI.getCurrent().getNavigator().navigateTo("Schedule_Task/" + (String) event.getButton().getData());

                        }
                    });

                }

                Button editTaskButton=new Button("Edit Task");
                Button viewTaskButton=new Button("View Task");

                editTaskButton.setData(task);
                viewTaskButton.setData(userStory.getProject().getName()+"/"+userStory.getName()+"/"+task.getTaskId());

                tasksTable.addItem(new Object[] {index,task.getName(),task.getPriority(),task.getSeverity(),task.getMemberType(),task.getEstimateTime(),task.getAssignedTo(),task.getCompleteTime(),editTaskButton,viewTaskButton},index);


                editTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        TaskWindow.open((Task)event.getButton().getData());

                    }
                });

                viewTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        DashboardUI.getCurrent().getNavigator().navigateTo("Schedule_Task/"+(String)event.getButton().getData());

                    }
                });


            }




    /*        viewUserStoryLayout.addComponent(tasksTable);

            Button newTaskButton= new Button("New Task");
            newTaskButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    TaskWindow.open(userStory);



                }
            });

            viewUserStoryLayout.addComponent(newTaskButton);*/
            viewUserStoryLayout.addComponent(tasksTable);
            viewUserStoryLayout.setExpandRatio(tasksTable,1);


        }



        //return  viewUserStoryLayout;

    }








    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Task List");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        if (userRole.equals("admin")||userRole.equals("pm")||userRole.equals("architect"))
        {
            create = buildCreateReport();
            HorizontalLayout tools;
            if(userStory.getState().equals("done"))
                tools = new HorizontalLayout(buildFilter());
            else
                tools = new HorizontalLayout(buildFilter(), create);

            tools.setSpacing(true);
            tools.addStyleName("toolbar");
            header.addComponent(tools);

        }
        else
        {
            create = buildCreateReport();
            HorizontalLayout tools = new HorizontalLayout(buildFilter());
            tools.setSpacing(true);
            tools.addStyleName("toolbar");
            header.addComponent(tools);

        }



        return header;
    }

    private Button buildCreateReport() {
        final Button createTask = new Button("Create New Task");
        createTask.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                Task task= new Task();
                task.setName("");
                task.setUserStory(userStory);

                TaskWindow.open(task);
            }
        });

        return createTask;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                Container.Filterable data = (Container.Filterable) tasksTable.getContainerDataSource();
                data.removeAllContainerFilters();
                data.addContainerFilter(new Container.Filter() {
                    @Override
                    public boolean passesFilter(final Object itemId,
                                                final Item item) {

                        if (event.getText() == null
                                || event.getText().equals("")) {
                            return true;
                        }

                        return filterByProperty("Index", item,
                                event.getText())
                                || filterByProperty("Name", item,
                                event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("Index")
                                || propertyId.equals("Name")) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        filter.setInputPrompt("Filter");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        filter.addShortcutListener(new ShortcutListener("Clear",
                ShortcutAction.KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                filter.setValue("");
                ((com.vaadin.data.Container.Filterable) tasksTable.getContainerDataSource())
                        .removeAllContainerFilters();
            }
        });
        return filter;
    }

    private boolean filterByProperty(final String prop, final Item item,
                                     final String text) {
        if (item == null || item.getItemProperty(prop) == null
                || item.getItemProperty(prop).getValue() == null) {
            return false;
        }
        String val = item.getItemProperty(prop).getValue().toString().trim()
                .toLowerCase();
        if (val.contains(text.toLowerCase().trim())) {
            return true;
        }
        return false;
    }
}
