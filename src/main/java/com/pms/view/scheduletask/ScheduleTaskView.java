package com.pms.view.scheduletask;

import com.pms.DashboardUI;
import com.pms.component.NewProjectComp;
import com.pms.component.TaskWindow;
import com.pms.component.UserStoryWindow;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Collection;
import java.util.List;

/**
 * Created by Upulie on 4/1/2015.
 */
public class ScheduleTaskView extends CssLayout implements View {


    TabSheet tabs;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        if(viewChangeEvent.getParameters() != null){
            // split at "/", add each part as a label
            String[] msgs = viewChangeEvent.getParameters().split("/");

            if(msgs.length==1)
            {
                if(msgs[0].equals(""))
                {
                    initialView();

                }
                else
                {
                    projectView(msgs[0].replace("%20"," "));

                }

            }
            if(msgs.length==2)
            {

                userStoryView(msgs[0].replace("%20"," "),msgs[1].replace("%20", " "));

            }

        }


    }




    public ScheduleTaskView()
    {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();

        tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);

        addComponent(tabs);

    }



    private void initialView()
    {
        tabs.removeAllComponents();
        tabs.addComponent(buildAllProjectView());
        tabs.addComponent(buildNewProject());

    }

    private void projectView(String projectName)
    {
        tabs.removeAllComponents();
        tabs.addComponent(buildProjectView(projectName));
        tabs.addComponent(buildNewProject());

    }

    private void userStoryView(String projectName,String projectId)
    {
        tabs.removeAllComponents();
        tabs.addComponent(buildUserStoryView(projectName,projectId));
        tabs.addComponent(buildNewProject());

    }





    private Component buildProjectView(final String projectName)
    {
        VerticalLayout viewProjectLayout = new VerticalLayout();
        viewProjectLayout.setCaption("View Project");
        viewProjectLayout.setMargin(true);
        viewProjectLayout.setSpacing(true);

        ProjectDAO projectDAO=(ProjectDAO)DashboardUI.context.getBean("Project");
        final Project project= projectDAO.getProjectFromProjectName(projectName);


        Label name=new Label("Project Name :   "+project.getName());
        viewProjectLayout.addComponent(name);
        Label clientName= new Label("Client Name :   "+project.getClientName());
        viewProjectLayout.addComponent(clientName);
        Label description=new Label("Description :   "+project.getDescription());
        viewProjectLayout.addComponent(description);
        Label startDate=new Label("Start Date :   "+project.getStartDate());
        viewProjectLayout.addComponent(startDate);
        Label deliveredDate=new Label("Delivered Date :   "+project.getDeliveredDate());
        viewProjectLayout.addComponent(deliveredDate);



        Table userStoryTable= new Table("User Stories");


        userStoryTable.addContainerProperty("Index", Integer.class, null);
        userStoryTable.addContainerProperty("User Story Name",  String.class, null);
        userStoryTable.addContainerProperty("Priority",Integer.class, null);
        userStoryTable.addContainerProperty("Description", String.class, null);
        userStoryTable.addContainerProperty("Domain", String.class, null);
        userStoryTable.addContainerProperty("Assigned Sprint", String.class, null);
        userStoryTable.addContainerProperty("Edit User Story", Button.class, null);
        userStoryTable.addContainerProperty("View User Story", Button.class, null);
        userStoryTable.setSizeFull();

        Collection<UserStory> projectUserStories=project.getProjectUserStories();


        int index=0;
        for(UserStory userStory:projectUserStories)
        {
            index++;

            Button editUserStoryButton=new Button("Edit UserStory");
            Button viewUserStoryButton=new Button("View UserStory");
            viewUserStoryButton.setData(userStory.getName());

            userStoryTable.addItem(new Object[] {index,userStory.getName(),userStory.getPriority(),userStory.getDescription(),userStory.getDomain(),userStory.getAssignedSprint(),editUserStoryButton,viewUserStoryButton},index);



            viewUserStoryButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    getUI().getNavigator().navigateTo("Schedule_Task/"+projectName+"/"+(String)event.getButton().getData());


                }
            });


        }


        //set table height to only for contained rows
        int actualRowCount = userStoryTable.size();
        userStoryTable.setPageLength(Math.min(actualRowCount, 15));
        viewProjectLayout.addComponent(userStoryTable);



        Button newUserStoryButton= new Button("New User Story");
        newUserStoryButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserStoryWindow.open(project);


            }
        });

        viewProjectLayout.addComponent(newUserStoryButton);



        return  viewProjectLayout;
    }







    private Component buildUserStoryView(String projectName,String userStoryName)
    {

        VerticalLayout viewUserStoryLayout = new VerticalLayout();
        viewUserStoryLayout.setCaption("View Project");
        viewUserStoryLayout.setMargin(true);
        viewUserStoryLayout.setSpacing(true);



        UserStoryDAO userStoryDAO=(UserStoryDAO)DashboardUI.context.getBean("UserStory");
        final UserStory userStory= userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(projectName,userStoryName);

        if(userStory!=null)
        {
            Label name = new Label("User Story Name :   "+userStory.getName());
            viewUserStoryLayout.addComponent(name);
            Label description= new Label("Description :   "+userStory.getDescription());
            viewUserStoryLayout.addComponent(description);
            Label priority= new Label("Priority :   "+userStory.getPriority());
            viewUserStoryLayout.addComponent(priority);
            Label date= new Label("Date :   "+userStory.getDate());
            viewUserStoryLayout.addComponent(date);
            Label preRequisits= new Label("Pre Requisits :   "+userStory.getPreRequisits());
            viewUserStoryLayout.addComponent(preRequisits);
            Label dependancy= new Label("Dependancy :   "+userStory.getDependancy());
            viewUserStoryLayout.addComponent(dependancy);
            Label domain= new Label("Domain :   "+userStory.getDomain());
            viewUserStoryLayout.addComponent(domain);
            Label assignedSprint= new Label("Assigned Sprint :   "+userStory.getAssignedSprint());
            viewUserStoryLayout.addComponent(assignedSprint);
            Label releasedDate= new Label("Released Date :   "+userStory.getReleasedDate());
            viewUserStoryLayout.addComponent(releasedDate);



            Table tasksTable= new Table("User Story Tasks");
            tasksTable.addContainerProperty("Index", Integer.class, null);
            tasksTable.addContainerProperty("Name",  String.class, null);
            tasksTable.addContainerProperty("Priority", Integer.class, null);
            tasksTable.addContainerProperty("Severity", Integer.class, null);
            tasksTable.addContainerProperty("Member Type", String.class, null);
            tasksTable.addContainerProperty("Estimate Time", String.class, null);
            tasksTable.addContainerProperty("Assigned To", String.class, null);
            tasksTable.addContainerProperty("Complete Time", String.class, null);
            tasksTable.addContainerProperty("Edit Task", Button.class, null);
            tasksTable.addContainerProperty("View Task", Button.class, null);
            tasksTable.setSizeFull();

            int index=0;
            for(Task task:userStory.getUserStoryTasks())
            {
                index++;

                Button editTaskButton=new Button("Edit Task");
                Button viewTaskButton=new Button("View Task");
                viewTaskButton.setData(userStory.getName());

                tasksTable.addItem(new Object[] {index,task.getName(),task.getPriority(),task.getSeverity(),task.getMemberType(),task.getEstimateTime(),task.getAssignedTo(),task.getCompleteTime(),editTaskButton,viewTaskButton},index);



                viewTaskButton.addClickListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {

                        //getUI().getNavigator().navigateTo("Schedule_Task/"+userStory.getProject().getName()+"/"+(String)event.getButton().getData());


                    }
                });


            }





            viewUserStoryLayout.addComponent(tasksTable);

            Button newTaskButton= new Button("New Task");
            newTaskButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    TaskWindow.open(userStory);



                }
            });

            viewUserStoryLayout.addComponent(newTaskButton);

        }




        return  viewUserStoryLayout;

    }









    private Component buildAllProjectView()
    {
        VerticalLayout viewProjectLayout = new VerticalLayout();
        viewProjectLayout.setCaption("View Project");
        viewProjectLayout.setMargin(true);
        viewProjectLayout.setSpacing(true);


        Table viewProjectTable= new Table("");
        viewProjectTable.addContainerProperty("Index", Integer.class, null);
        viewProjectTable.addContainerProperty("Name",  String.class, null);
        viewProjectTable.addContainerProperty("Client Name", String.class, null);
        viewProjectTable.addContainerProperty("Description", String.class, null);
        viewProjectTable.addContainerProperty("Created Date", String.class, null);
        viewProjectTable.addContainerProperty("Start Date", String.class, null);
        viewProjectTable.addContainerProperty("Delivered Date", String.class, null);
        viewProjectTable.addContainerProperty("Edit Project", Button.class, null);
        viewProjectTable.addContainerProperty("View Project", Button.class, null);
        viewProjectTable.setSizeFull();

        ProjectDAO projectDAO= (ProjectDAO)DashboardUI.context.getBean("Project");

        List<Project> projectList=projectDAO.getAllProjects();

        for(int x=0;x<projectList.size();x++)
        {
            int index=x+1;

            Button editProjectButton=new Button("Edit Project");
            Button viewProjectButton=new Button("View Project");
            viewProjectButton.setData(projectList.get(x).getName());

            viewProjectTable.addItem(new Object[] {index,projectList.get(x).getName(),projectList.get(x).getClientName(),projectList.get(x).getDescription(),projectList.get(x).getDate(),projectList.get(x).getStartDate(),projectList.get(x).getDeliveredDate(),editProjectButton,viewProjectButton},index);



            viewProjectButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    getUI().getNavigator().navigateTo("Schedule_Task/"+(String)event.getButton().getData());


                }
            });

        }



        viewProjectLayout.addComponent(viewProjectTable);




        return  viewProjectLayout;

    }





    private Component buildNewProject()
    {
        VerticalLayout newProjectLayout = new VerticalLayout();
        newProjectLayout.setCaption("New Project");
        newProjectLayout.setMargin(true);

        NewProjectComp newProjectComp= new NewProjectComp("");
        newProjectLayout.addComponent(newProjectComp.initContent());



        return  newProjectLayout;

    }
}
