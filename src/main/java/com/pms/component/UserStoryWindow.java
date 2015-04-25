package com.pms.component;

import com.google.gwt.aria.client.ComboboxRole;
import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.UserStory;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
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
 * Created by Upulie on 4/19/2015.
 */
public class UserStoryWindow extends Window {

    Project project;
    Collection<UserStory> projectUserStories;

    private final BeanFieldGroup<UserStory> fieldGroup;
    private final UserStory userStory;


    @PropertyId("name")
    private TextField userStoryName;
    @PropertyId("description")
    private TextArea description;
    private ComboBox priority;
    private ListSelect preRequisits;
    private ListSelect dependancy;
    @PropertyId("domain")
    private TextField domain;
    @PropertyId("assignedSprint")
    private TextField assignedSprint;
    @PropertyId("releasedDate")
    private TextField releasedDate;
    private ComboBox isCr;


    private UserStoryWindow(Project project) {

        this.project = project;
        projectUserStories = project.getProjectUserStories();


        addStyleName("profile-window");
        setCaption("New User Story");
        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(90.0f, Unit.PERCENTAGE);

        Panel mainPanel = new Panel("");
        mainPanel.setHeight(90.0f, Unit.PERCENTAGE);
        mainPanel.setHeightUndefined();
        mainPanel.setContent(buildUserStory());
        mainPanel.getContent().setSizeUndefined();
        setContent(mainPanel);

        userStory = new UserStory();
        fieldGroup = new BeanFieldGroup<UserStory>(UserStory.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(userStory);

    }

    private Component buildUserStory() {
        FormLayout content = new FormLayout();
        content.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        content.setCaption("User Story");
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, true, true, true));
        content.setSpacing(true);

        userStoryName = new TextField("User Story Name");
        content.addComponent(userStoryName);

        description = new TextArea("Description");
        content.addComponent(description);

        priority = new ComboBox("Priority");
        priority.addItem(1);
        priority.addItem(2);
        priority.addItem(3);
        priority.addItem(4);
        priority.addItem(5);
        content.addComponent(priority);

        preRequisits = new ListSelect("Pre Requisits");
        preRequisits.setWidth("400px");
        preRequisits.setNullSelectionAllowed(true);
        for (UserStory user_Story : projectUserStories) {
            preRequisits.addItem(user_Story.getName());
        }
        preRequisits.setMultiSelect(true);
        preRequisits.setRows(7);
        content.addComponent(preRequisits);


        dependancy = new ListSelect("Dependency");
        for (UserStory user_Story : projectUserStories) {
            dependancy.addItem(user_Story.getName());
        }
        dependancy.setWidth("400px");
        dependancy.setNullSelectionAllowed(true);
        dependancy.setMultiSelect(true);
        dependancy.setRows(7);
        content.addComponent(dependancy);


        domain = new TextField("Domain");
        content.addComponent(domain);

        assignedSprint = new TextField("Assigned Sprint");
        content.addComponent(assignedSprint);

        releasedDate = new TextField("released Date");
        content.addComponent(releasedDate);

        isCr = new ComboBox("Is Cr");
        isCr.addItem("true");
        isCr.addItem("false");
        content.addComponent(isCr);

        content.addComponent(buildFooter());

        return content;

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
        Button addNewButton = new Button("Create New User Story");
        addNewButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

            /*    try {


                    fieldGroup.commit();*/

                UserStory userStory = new UserStory();
                //userStory =fieldGroup.getItemDataSource().getBean();

                userStory.setProject(project);
                userStory.setName(userStoryName.getValue().toString());
                userStory.setDescription(description.getValue().toString());
                userStory.setPriority(Integer.parseInt(priority.getValue().toString()));


                //set dependency for user story
                StringBuilder dependencyString = new StringBuilder();
                Set<Item> dependencyValues = (Set<Item>) dependancy.getValue();
                int size = dependencyValues.size();
                int index = 1;
                for (Object v : dependencyValues) {

                    dependencyString.append(v.toString());
                    if (index != size)
                        dependencyString.append(",");
                    index++;
                }
                userStory.setDependancy(dependencyString.toString());


                //set pre requist for user story
                StringBuilder preRequisitString = new StringBuilder();
                Set<Item> preRequisitsValues = (Set<Item>) preRequisits.getValue();
                int size2 = preRequisitsValues.size();
                int index2 = 1;
                for (Object v : preRequisitsValues) {

                    preRequisitString.append(v.toString());
                    if (index2 != size2)
                        preRequisitString.append(",");
                    index2++;
                }
                userStory.setPreRequisits(preRequisitString.toString());

                userStory.setDomain(domain.getValue().toString());
                userStory.setAssignedSprint(assignedSprint.getValue().toString());
                userStory.setReleasedDate(releasedDate.getValue().toString());

                if (isCr.getValue().toString().equals("false"))
                    userStory.setCR(false);
                else
                    userStory.setCR(true);


                ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");
                project.getProjectUserStories().add(userStory);
                projectDAO.updateProject(project);


                Notification success = new Notification(
                        "User Story Created successfully");
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


    public static void open(Project project) {
        Window w = new UserStoryWindow(project);
        UI.getCurrent().addWindow(w);
        w.focus();

    }


}
