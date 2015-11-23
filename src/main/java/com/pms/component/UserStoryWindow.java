package com.pms.component;

import com.pms.DashboardUI;
import com.pms.component.ganttchart.scheduletask.PrioritizeUserStories;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
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
import org.vaadin.dialogs.ConfirmDialog;

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
    private UserStory userStory;
    private String oldUserStoryState;
    private boolean editmode = false;


    @PropertyId("name")
    private TextField userStoryName;
    @PropertyId("state")
    private ComboBox state;
    @PropertyId("description")
    private TextArea description;
    @PropertyId("priority")
    private ComboBox priority;
    private OptionGroup preRequisitsList;
    @PropertyId("domain")
    private ComboBox domain;
    @PropertyId("assignedSprint")
    private TextField assignedSprint;
    @PropertyId("releasedDate")
    private PopupDateField releasedDate;
    @PropertyId("isCr")
    private OptionGroup isCr;


    private UserStoryWindow(UserStory userStory) {


        this.userStory = userStory;
        this.oldUserStoryState = userStory.getState();
        this.project = userStory.getProject();
        projectUserStories = project.getProjectUserStories();

        if (!userStory.getName().equals("")) {
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


        detailsWrapper.addComponent(buildUserStory());
        content.addComponent(buildFooter());


        fieldGroup = new BeanFieldGroup<UserStory>(UserStory.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(userStory);

    }

    private Component buildUserStory() {

        FormLayout content = new FormLayout();
        content.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        content.setCaption("User Story");
        content.setMargin(new MarginInfo(true, true, true, true));
        content.setSpacing(true);

        userStoryName = new TextField("User Story Name");
        userStoryName.setNullRepresentation("");
        userStoryName.setRequired(true);
        content.addComponent(userStoryName);

        if (editmode) {
            state = new ComboBox("State");
            state.addItem("initial");
            state.addItem("working");
            state.addItem("done");

            content.addComponent(state);

            //State Change Handle
            state.addListener(new Property.ValueChangeListener() {
                public void valueChange(Property.ValueChangeEvent event) {
                    if (state.getValue().toString().equals("initial") && !oldUserStoryState.equals("initial")) {
                        ConfirmDialog.show(DashboardUI.getCurrent(), "Please Confirm:", "Are you sure you want to update " + userStory.getName() + " State To \"Initial\" :",
                                "I am", "Not quite", new ConfirmDialog.Listener() {
                                    public void onClose(ConfirmDialog dialog) {
                                        if (dialog.isConfirmed()) {
                                        } else {
                                            state.setValue(oldUserStoryState);
                                            return;

                                        }
                                    }

                                });

                    } else if (state.getValue().toString().equals("working") && !(oldUserStoryState.equals("working"))) {
                        if (oldUserStoryState.equals("initial")) {
                            Notification notification = new Notification("Error", "You cannot change initial state UserStory to working State Manually",
                                    Notification.Type.ERROR_MESSAGE, true);
                            notification.show(Page.getCurrent());

                            //set old state without binding value
                            state.setValue(oldUserStoryState);
                            return;
                        } else {
                            ConfirmDialog.show(DashboardUI.getCurrent(), "Please Confirm:", "Are you sure you want to update " + userStory.getName() + " State To \"Working\" :",
                                    "I am", "Not quite", new ConfirmDialog.Listener() {

                                        public void onClose(ConfirmDialog dialog) {
                                            if (dialog.isConfirmed()) {
                                                for (UserStory userStory1 : project.getProjectUserStories()) {
                                                    if (userStory1.getState().equals("working")) {
                                                        userStory1.setState("initial");
                                                        UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
                                                        userStoryDAO.updateUserStory(userStory1);
                                                    }
                                                }

                                            } else {
                                                state.setValue(oldUserStoryState);
                                                return;
                                            }
                                        }

                                    });
                        }

                    } else if (state.getValue().toString().equals("done") && !oldUserStoryState.equals("done")) {

                        if (oldUserStoryState.equals("initial")) {
                            Notification notification = new Notification("Error", "You cannot change initial state UserStory to done",
                                    Notification.Type.ERROR_MESSAGE, true);
                            notification.show(Page.getCurrent());
                            state.setValue(oldUserStoryState);
                            return;
                        } else if (oldUserStoryState.equals("working")) {
                            ConfirmDialog.show(UI.getCurrent(), "Please Confirm:", "Are you sure you want to update  " + userStory.getName() + " State To Done :",
                                    "I am", "Not quite", new ConfirmDialog.Listener() {

                                        public void onClose(ConfirmDialog dialog) {
                                            if (dialog.isConfirmed()) {

                                            } else {
                                                state.setValue(oldUserStoryState);
                                                return;

                                            }
                                        }

                                    });

                        }


                    }


                }

            });
        }

        description = new TextArea("Description");
        description.setNullRepresentation("");
        content.addComponent(description);

        priority = new ComboBox("Priority");
        priority.addItem(1);
        priority.addItem(2);
        priority.addItem(3);
        priority.addItem(4);
        priority.addItem(5);
        priority.setRequired(true);
        content.addComponent(priority);


        preRequisitsList = new OptionGroup("Pre Requisits");
        preRequisitsList.setWidth("400px");
        preRequisitsList.setNullSelectionAllowed(true);
        for (UserStory user_Story : projectUserStories) {

            preRequisitsList.addItem(user_Story.getName());

        }
        preRequisitsList.setMultiSelect(true);


        Panel preRequestPanel = new Panel("");
        preRequestPanel.setHeight("100px");
        preRequestPanel.setContent(preRequisitsList);
        VerticalLayout PreRequisiteLayout = new VerticalLayout();
        PreRequisiteLayout.setCaption("Pre Requisits");
        PreRequisiteLayout.addComponent(preRequestPanel);
        content.addComponent(PreRequisiteLayout);

        if (editmode) {
            //remove current userStory name appier in the prerequist list
            preRequisitsList.removeItem(userStory.getName());

            String[] PreRequisiteList = userStory.getPreRequisits().split(",");

            for (String PreRequisite : PreRequisiteList) {
                preRequisitsList.select(PreRequisite);
            }

            userStoryName.setReadOnly(true);

        }


        domain = new ComboBox("Domain");
        domain.addItem("CRM");
        domain.addItem("Health Care");
        domain.addItem("Banking");
        domain.addItem("Mobile Development");
        content.addComponent(domain);

        assignedSprint = new TextField("Assigned Sprint");
        assignedSprint.setNullRepresentation("");
        content.addComponent(assignedSprint);

        releasedDate = new PopupDateField("released Date");
        releasedDate.setValue(new Date());
        releasedDate.setDateFormat("yyyy-MM-dd");
        //releasedDate.setNullRepresentation("");
        content.addComponent(releasedDate);

        isCr = new OptionGroup("Change Request");
        isCr.addItem(Boolean.TRUE);
        isCr.addItem(Boolean.FALSE);
        if(!editmode)
        isCr.setValue(Boolean.FALSE);
        else
        {
            if(userStory.isCR())
                isCr.setValue(Boolean.TRUE);
            else
                isCr.setValue(Boolean.FALSE);
        }
        isCr.addStyleName("horizontal");
        content.addComponent(isCr);

        //content.addComponent(buildFooter());

        return content;

    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        //buttonsLayout.setMargin(true);
        //buttonsLayout.setSpacing(true);


        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                close();

            }
        });


        Button submitButton;
        if (editmode) {
            submitButton = new Button("Update User Story");
        } else {
            submitButton = new Button("Create New User Story");
        }
        //Button submitButton = new Button("Create New User Story");
        submitButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                try {

                    ProjectDAO projectDAO = (ProjectDAO) DashboardUI.context.getBean("Project");
                    final UserStoryDAO userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");

                    fieldGroup.commit();
                    final UserStory newUserStory;
                    newUserStory = fieldGroup.getItemDataSource().getBean();
                    newUserStory.setProject(project);


                    //when user edit PreRequisite need to update those edited PreRequisite dependency
                    if (editmode) {
                        String[] PreRequisiteListBeforeEdit = userStory.getPreRequisits().split(",");

                        for (String PreRequisite : PreRequisiteListBeforeEdit) {
                            if (!preRequisitsList.isSelected(PreRequisite)) {
                                for (UserStory userStory1 : project.getProjectUserStories()) {
                                    if (userStory1.getName().equals(PreRequisite)) {
                                        userStory1.setDependancy(userStory1.getDependancy().replace(newUserStory.getName(), ""));


                                        if (userStory1.getDependancy() != null && !userStory1.getDependancy().isEmpty()) {
                                            if (userStory1.getDependancy().startsWith(",")) {
                                                userStory1.setDependancy(userStory1.getDependancy().substring(1, userStory1.getDependancy().length()));
                                            } else if (userStory1.getDependancy().contains(",,")) {
                                                userStory1.setDependancy(userStory1.getDependancy().replace(",,", ","));
                                            } else if (userStory1.getDependancy().endsWith(",")) {
                                                userStory1.setDependancy(userStory1.getDependancy().substring(0, userStory1.getDependancy().length() - 1));
                                            }

                                            if (userStory1.getDependancy().isEmpty()) {
                                                userStory1.setDependancy(null);
                                            }


                                        }


                                        userStoryDAO.updateUserStory(userStory1);
                                        break;
                                    }
                                }


                            }
                        }


                    } else {
                        newUserStory.setState("initial");
                        newUserStory.setAssignedSprint(0);


                    }

                    //Check New UserStory Priority with Prerequisite Priority
                    int userStoryPriority = Integer.parseInt(priority.getValue().toString());

                    Set<Item> PreRequisiteValues1 = (Set<Item>) preRequisitsList.getValue();

                    for (Object v : PreRequisiteValues1) {

                        String PreRequisiteName = v.toString();
                        UserStory PreRequisiteUserStory = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(project.getName(), PreRequisiteName);

                        if (userStoryPriority < PreRequisiteUserStory.getPriority()) {
                            Notification notification = new Notification("Your Selected Priority is Incorrect ",
                                    "<br/>You have PreRequisite that has lowpriorityy than this UserStory",
                                    Notification.Type.ERROR_MESSAGE, true);

                            notification.show(Page.getCurrent());
                            return;

                        }


                    }


                    //set PreRequisite for user story
                    StringBuilder PreRequisiteString = new StringBuilder();
                    Set<Item> PreRequisiteValues = (Set<Item>) preRequisitsList.getValue();
                    int size2 = PreRequisiteValues.size();
                    int index2 = 1;

                    for (Object v : PreRequisiteValues) {

                        if (index2 != 1 && !v.toString().isEmpty())
                            PreRequisiteString.append(",");
                        PreRequisiteString.append(v.toString());

                        index2++;


                        //following section manually set dependency in other user stories if this user story depend on them
                        for (UserStory userStory1 : project.getProjectUserStories()) {
                            if (userStory1.getName().equals(v.toString())) {
                                if (userStory1.getDependancy() == null || userStory1.getDependancy().isEmpty()) {
                                    userStory1.setDependancy(newUserStory.getName());
                                }
                                else {

                                    if(!userStory1.getDependancy().contains(newUserStory.getName()))
                                    {
                                        StringBuilder dependencyString1 = new StringBuilder();
                                        dependencyString1.append(userStory1.getDependancy());
                                        dependencyString1.append(',' + newUserStory.getName());

                                        userStory1.setDependancy(dependencyString1.toString());

                                    }

                                }

                                userStoryDAO.updateUserStory(userStory1);


                            }
                        }


                    }
                    newUserStory.setPreRequisits(PreRequisiteString.toString());


                    project.getProjectUserStories().add(newUserStory);
                    projectDAO.updateProject(project);


                    //If userstory state changed to done need to find next working state userstory so need to run prioritize userstorys
                    if(newUserStory.getState().equals("done") && !oldUserStoryState.equals("done"))
                    {
                        PrioritizeUserStories prioritizeUserStories= new PrioritizeUserStories();
                        prioritizeUserStories.prioritize(project);
                    }

                    if (editmode) {
                        Notification success = new Notification(
                                "User Story Updated successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    } else {
                        Notification success = new Notification(
                                "User Story Created successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    }


                    close();
                    Page.getCurrent().reload();


                } catch (FieldGroup.CommitException e) {
                    Notification.show("Error while creating User Story please check required fields",
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


    public static void open(UserStory userStory) {
        Window w = new UserStoryWindow(userStory);
        UI.getCurrent().addWindow(w);
        w.focus();

    }


}
