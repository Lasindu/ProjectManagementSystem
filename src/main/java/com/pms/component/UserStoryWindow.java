package com.pms.component;

import com.google.gwt.aria.client.ComboboxRole;
import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.User;
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
 * Created by Upulie on 4/19/2015.
 */
public class UserStoryWindow extends Window {

    Project project;
    Collection<UserStory> projectUserStories;

    private final BeanFieldGroup<UserStory> fieldGroup;
    private UserStory userStory;
    private boolean editmode=false;


    @PropertyId("name")
    private TextField userStoryName;
    @PropertyId("description")
    private TextArea description;
    @PropertyId("priority")
    private ComboBox priority;
    private OptionGroup preRequisitsList;
    @PropertyId("domain")
    private TextField domain;
    @PropertyId("assignedSprint")
    private TextField assignedSprint;
    @PropertyId("releasedDate")
    private PopupDateField releasedDate;
    @PropertyId("isCr")
    private OptionGroup isCr;


    private UserStoryWindow(UserStory userStory) {


        this.userStory=userStory;
        this.project = userStory.getProject();
        projectUserStories = project.getProjectUserStories();

        if(!userStory.getName().equals(""))
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
        content.addComponent(userStoryName);

        description = new TextArea("Description");
        description.setNullRepresentation("");
        content.addComponent(description);

        priority = new ComboBox("Priority");
        priority.addItem(1);
        priority.addItem(2);
        priority.addItem(3);
        priority.addItem(4);
        priority.addItem(5);
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
        VerticalLayout preRequistLayout= new VerticalLayout();
        preRequistLayout.setCaption("Pre Requisits");
        preRequistLayout.addComponent(preRequestPanel);
        content.addComponent(preRequistLayout);

        if(editmode)
        {
            String[] preRquisitList= userStory.getPreRequisits().split(",");

            for(String preRequistit:preRquisitList)
            {
                preRequisitsList.select(preRequistit);
            }
        }


        domain = new TextField("Domain");
        domain.setNullRepresentation("");
        content.addComponent(domain);

        assignedSprint = new TextField("Assigned Sprint");
        assignedSprint.setNullRepresentation("");
        content.addComponent(assignedSprint);

        releasedDate = new PopupDateField("released Date");
        releasedDate.setValue(new Date());
        releasedDate.setDateFormat("yyyy-MM-dd");
        //releasedDate.setNullRepresentation("");
        content.addComponent(releasedDate);

        isCr = new OptionGroup("Is Cr");
        isCr.addItem(Boolean.TRUE);
        isCr.addItem(Boolean.FALSE);
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
        if(editmode)
        {
            submitButton = new Button("Update User Story");
        }
        else
        {
            submitButton = new Button("Create New User Story");
        }
        //Button submitButton = new Button("Create New User Story");
        submitButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                try {

                    ProjectDAO projectDAO = (ProjectDAO)DashboardUI.context.getBean("Project");
                    UserStoryDAO userStoryDAO =(UserStoryDAO)DashboardUI.context.getBean("UserStory");

                    fieldGroup.commit();
                    UserStory userStory ;
                    userStory =fieldGroup.getItemDataSource().getBean();
                    userStory.setProject(project);


                    //when user edit prerequist need to update those edited prerquisit dependency
                    if (editmode)
                    {
                        String[] preRequistListBeforEdit=userStory.getPreRequisits().split(",");

                        for(String preReqsuist:preRequistListBeforEdit)
                        {
                            if(!preRequisitsList.isSelected(preReqsuist))
                            {
                                for (UserStory userStory1 : project.getProjectUserStories()) {
                                    if (userStory1.getName().equals(preReqsuist)) {
                                        userStory1.setDependancy(userStory1.getDependancy().replace(userStory.getName(),""));
                                        if(userStory1.getDependancy().contains(",,"))
                                        {
                                            userStory1.setDependancy(userStory1.getDependancy().replace(",,",","));
                                        }
                                        if(userStory1.getDependancy().endsWith(","))
                                        {
                                            userStory1.setDependancy(userStory1.getDependancy().substring(0,userStory1.getDependancy().length()-1));
                                        }

                                        userStoryDAO.updateUserStory(userStory1);
                                        break;
                                    }
                                }


                            }
                        }


                    }



                    //set pre requist for user story
                    StringBuilder preRequisitString = new StringBuilder();
                    Set<Item> preRequisitsValues = (Set<Item>) preRequisitsList.getValue();
                    int size2 = preRequisitsValues.size();
                    int index2 = 1;
                    for (Object v : preRequisitsValues) {

                        preRequisitString.append(v.toString());
                        if (index2 != size2)
                            preRequisitString.append(",");
                        index2++;




                        //following section manually set dependency in other user stories if this user story depend on them
                        for (UserStory userStory1 : project.getProjectUserStories()) {
                            if(userStory1.getName().equals(v.toString()))
                            {
                                if(userStory1.getDependancy().isEmpty())
                                {
                                    userStory1.setDependancy(userStory.getName());
                                }
                                else
                                {
                                    StringBuilder dependencyString1 = new StringBuilder();
                                    dependencyString1.append(userStory1.getDependancy());
                                    dependencyString1.append(','+userStory.getName());

                                    userStory1.setDependancy(dependencyString1.toString());

                                }

                                userStoryDAO.updateUserStory(userStory1);


                            }
                        }





                    }
                    userStory.setPreRequisits(preRequisitString.toString());


                    project.getProjectUserStories().add(userStory);
                    projectDAO.updateProject(project);

                    if(editmode)
                    {
                        Notification success = new Notification(
                                "User Story Updated successfully");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());

                    }
                    else
                    {
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








    public static void open(UserStory userStory) {
        Window w = new UserStoryWindow(userStory);
        UI.getCurrent().addWindow(w);
        w.focus();

    }


}
