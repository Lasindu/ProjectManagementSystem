package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserDAO;
import com.pms.domain.Project;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Upulie on 6/2/2015.
 */
public class ProjectWindow extends Window {

    private final BeanFieldGroup<Project> fieldGroup;
    private Project project;
    private boolean editmode=false;


    @PropertyId("name")
    private TextField projctName;
    @PropertyId("clientName")
    private TextField projctClientName;
    @PropertyId("description")
    private TextArea projectDescription;
    @PropertyId("date")
    private TextField projectCreatedDate;
   // @PropertyId("startDate")
    private PopupDateField   projectStartDate;
   // @PropertyId("deliveredDate")
    private PopupDateField   projectDeliveredDate;
    @PropertyId("sprintTime")
    private TextField projectSprintTime;


    private ProjectWindow(Project project) throws ParseException {
        this.project=project;

        if(!project.getName().isEmpty())
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


        detailsWrapper.addComponent(buildProject());
        content.addComponent(buildFooter());


        fieldGroup = new BeanFieldGroup<Project>(Project.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(project);



    }


    private Component buildProject() throws ParseException {
        FormLayout content = new FormLayout();
        content.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        content.setCaption("Project");
        content.setMargin(new MarginInfo(true, true, true, true));
        content.setSpacing(true);


        projctName = new TextField("Project Name");
        projctName.setNullRepresentation("");

        projctName.setRequired(true);
        projctName.setRequiredError("Project Name is Required");
        content.addComponent(projctName);

        projctClientName = new TextField("Client Name");
        projctClientName.setNullRepresentation("");
        content.addComponent(projctClientName);

        projectDescription = new TextArea("Description");
        projectDescription.setNullRepresentation("");
        content.addComponent(projectDescription);

        projectCreatedDate = new TextField("Created Date");

        projectSprintTime = new TextField("Sprint Time");
        projectSprintTime.setNullRepresentation("");
        projectSprintTime.setConverter(Integer.class);
        content.addComponent(projectSprintTime);


        projectStartDate = new PopupDateField  ("Start Date");
        projectStartDate.setDateFormat("yyyy-MM-dd");
        projectStartDate.setRangeStart(new Date());
        content.addComponent(projectStartDate);


        projectDeliveredDate = new PopupDateField  ("End Date");
        projectDeliveredDate.setDateFormat("yyyy-MM-dd");
        projectDeliveredDate.setRangeStart(new Date());
        content.addComponent(projectDeliveredDate);

        if(editmode)
        {

            DateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            if(project.getStartDate() != null && !project.getStartDate().isEmpty())
            {
                Date startDate = format.parse(project.getStartDate());
                projectStartDate.setValue(startDate);
            }
            if(project.getDeliveredDate() != null && !project.getDeliveredDate().isEmpty())
            {
                Date deliveredDate = format.parse(project.getDeliveredDate());
                projectDeliveredDate.setValue(deliveredDate);
            }

        }


        return content;

    }



    private Component buildFooter()
    {
        HorizontalLayout footer= new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);


        Button cancel= new Button("Cancel");
        cancel.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                close();

            }
        });

        Button submit;
        if(editmode)
        {
            submit= new Button("Update Project");

        }
        else
        {
            submit= new Button("Create Project");

        }

        submit.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        fieldGroup.commit();
                        Project project;
                        project =fieldGroup.getItemDataSource().getBean();

                        if(projectStartDate.getValue() != null && !projectStartDate.getValue().toString().isEmpty())
                        {
                            project.setStartDate(projectStartDate.getValue().toString());
                        }
                        if(projectDeliveredDate.getValue() != null && !projectDeliveredDate.getValue().toString().isEmpty()) {
                            project.setDeliveredDate(projectDeliveredDate.getValue().toString());
                        }

                        if(projectStartDate.getValue() != null && !projectStartDate.getValue().toString().isEmpty() && projectDeliveredDate.getValue() != null && !projectDeliveredDate.getValue().toString().isEmpty())
                        {
                            DateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
                            Date startDate = format.parse(project.getStartDate());
                            Date deliveredDate = format.parse(project.getDeliveredDate());

                            if(startDate.getTime() > deliveredDate.getTime())
                            {
                                Notification.show("Start Date > End date Please check dates ",
                                        Notification.Type.ERROR_MESSAGE);
                                return;
                            }




                        }



                        if (editmode)
                        {
                            ProjectDAO projectDAO= (ProjectDAO) DashboardUI.context.getBean("Project");
                            projectDAO.updateProject(project);

                            Notification success = new Notification(
                                    "Project updated successfully");
                            success.setDelayMsec(2000);
                            success.setStyleName("bar success small");
                            success.setPosition(Position.BOTTOM_CENTER);
                            success.show(Page.getCurrent());

                        }
                        else
                        {
                            //set date to project
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();
                            project.setDate(dateFormat.format(date).toString());



                            //ProjectDAO projectDAO= (ProjectDAO) DashboardUI.context.getBean("Project");
                            //projectDAO.saveNewProject(project);


                            User user=(User) VaadinSession.getCurrent().getAttribute(
                                    User.class.getName());
                            //we have to use this method for create new project because of  many to many mapping
                            UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
                            User projectsLoadedUsr=userDAO.loadUserProjects(user);
                            projectsLoadedUsr.getProjects().add(project);

                            userDAO.updateUser(projectsLoadedUsr);




                            Notification success = new Notification("Project Created successfully");
                            success.setDelayMsec(2000);
                            success.setStyleName("bar success small");
                            success.setPosition(Position.BOTTOM_CENTER);
                            success.show(Page.getCurrent());

                        }


                        // getUI().getNavigator().navigateTo("/");

                        Page.getCurrent().reload();

                    } catch (FieldGroup.CommitException e) {
                        Notification.show("Error while creating project please check required fields",
                                Notification.Type.ERROR_MESSAGE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            });

        footer.addComponent(submit);
        footer.addComponent(cancel);

        footer.setExpandRatio(cancel,1);

        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT);
        footer.setComponentAlignment(submit, Alignment.TOP_RIGHT);


        return footer;

    }



    public static void open(Project project) throws ParseException {
        Window w = new ProjectWindow(project);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
