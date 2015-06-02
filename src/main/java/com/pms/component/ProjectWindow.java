package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.domain.Project;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Damitha on 6/2/2015.
 */
public class ProjectWindow extends Window {


    String projectName;
    private final BeanFieldGroup<Project> fieldGroup;
    private final Project project;



    @PropertyId("name")
    private TextField projctName;
    @PropertyId("clientName")
    private TextField projctClientName;
    @PropertyId("description")
    private TextArea projectDescription;
    @PropertyId("date")
    private TextField projectCreatedDate;
    @PropertyId("startDate")
    private TextField projectStartDate;
    @PropertyId("deliveredDate")
    private TextField ProjectDeliveredDate;


    private ProjectWindow(String projectName)
    {
        this.projectName=projectName;

        project=getProject();
        fieldGroup = new BeanFieldGroup<Project>(Project.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(project);


        addStyleName("profile-window");
        setCaption("New Project");
        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        Panel mainPanel = new Panel("");
        mainPanel.setHeight(90.0f, Unit.PERCENTAGE);
        mainPanel.setHeightUndefined();
        mainPanel.setContent(buildProject());
        mainPanel.getContent().setSizeUndefined();
        setContent(mainPanel);


    }

    private Project getProject()
    {
        if(projectName.equals("")){
            return new Project();
        }
        else{
            return new Project();
        }

    }


    private Component buildProject()
    {
        VerticalLayout mainLayout=new VerticalLayout();



        VerticalLayout root = new VerticalLayout();

        projctName = new TextField("Project Name");
        root.addComponent(projctName);
        projctClientName = new TextField("Client Name");
        root.addComponent(projctClientName);
        projectDescription = new TextArea("Description");
        root.addComponent(projectDescription);
        projectCreatedDate = new TextField("Created Date");
        projectStartDate = new TextField("Start Date");
        root.addComponent(projectStartDate);
        ProjectDeliveredDate = new TextField("End Date");
        root.addComponent(ProjectDeliveredDate);


        mainLayout.addComponent(root);
        mainLayout.addComponent(buildFooter());

        //return root;
        return mainLayout;

    }



    private Component buildFooter()
    {
        HorizontalLayout footer= new HorizontalLayout();
        Button reset= new Button("Reset All");
        footer.addComponent(reset);

        if(projectName.equals("")){
            Button create= new Button("Create Project");
            create.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        fieldGroup.commit();
                        Project project= new Project();
                        project =fieldGroup.getItemDataSource().getBean();

                        //set date to project
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        project.setDate(dateFormat.format(date).toString());

                        ProjectDAO projectDAO= (ProjectDAO) DashboardUI.context.getBean("Project");
                        projectDAO.saveNewProject(project);


                        Notification success = new Notification(
                                "Project updated successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());


                        // getUI().getNavigator().navigateTo("/");
                        Page.getCurrent().reload();

                    } catch (FieldGroup.CommitException e) {
                        Notification.show("Error while creating project",
                                Notification.Type.ERROR_MESSAGE);
                    }

                }
            });
            footer.addComponent(create);

        }
        else{
            Button update= new Button("Update Project");
            footer.addComponent(update);
        }

        return footer;

    }














    public static void open(String projectName) {
        Window w = new ProjectWindow(projectName);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
