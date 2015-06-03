package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.domain.Project;
import com.pms.domain.UserStory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Collection;

/**
 * Created by Damitha on 6/2/2015.
 */
public class ViewProject extends CustomComponent {

    private Table userStoryTable;
    private String projectName;
    private Button createUserStory;
    private Project project;
    public VerticalLayout viewProjectLayout;

   public ViewProject(String projectName)
    {
        this.projectName=projectName;
        buildViewProject();

    }


    public Component getProject()
    {
        return viewProjectLayout;
    }




    private void buildViewProject()
    {
        viewProjectLayout = new VerticalLayout();
        viewProjectLayout.setMargin(true);
        viewProjectLayout.setSpacing(true);
        viewProjectLayout.setSizeFull();


        //get project object prom the database
        ProjectDAO projectDAO=(ProjectDAO) DashboardUI.context.getBean("Project");
        project= projectDAO.getProjectFromProjectName(projectName);


        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Project - "+project.getName());
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        viewProjectLayout.addComponent(header);


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



        viewProjectLayout.addComponent(buildToolbar());

        userStoryTable= new Table("");
        userStoryTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        userStoryTable.addStyleName(ValoTheme.TABLE_COMPACT);
        userStoryTable.setSelectable(true);


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
            viewUserStoryButton.setData(userStory.getProject().getName()+"/"+userStory.getName());
            editUserStoryButton.setData(userStory);

            userStoryTable.addItem(new Object[]{index, userStory.getName(), userStory.getPriority(), userStory.getDescription(), userStory.getDomain(), userStory.getAssignedSprint(), editUserStoryButton, viewUserStoryButton}, index);


            editUserStoryButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    UserStoryWindow.open((UserStory)event.getButton().getData());

                }
            });

            viewUserStoryButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {



                    //getUI().getNavigator().navigateTo("Schedule_Task/"+projectName+"/"+(String)event.getButton().getData());
                    System.out.println("#################################################"+(String)event.getButton().getData());
                   // getUI().getNavigator().navigateTo("Schedule_Task/"+(String)event.getButton().getData());
                    //System.out.println(getUI().getNavigator().getState());
                    DashboardUI.getCurrent().getNavigator().navigateTo("Schedule_Task/"+(String)event.getButton().getData());

                }
            });


        }


        //set table height to only for contained rows
/*         int actualRowCount = userStoryTable.size();
         userStoryTable.setPageLength(Math.min(actualRowCount, 15));*/
        viewProjectLayout.addComponent(userStoryTable);
        viewProjectLayout.setExpandRatio(userStoryTable, 1);



/*        Button newUserStoryButton= new Button("New User Story");
        newUserStoryButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UserStoryWindow.open(project);


            }
        });*/

        //viewProjectLayout.addComponent(newUserStoryButton);



        //return  viewProjectLayout;

    }





    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("User Story List");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        createUserStory = buildCreateUserStory();
        HorizontalLayout tools = new HorizontalLayout(buildFilter(),
                createUserStory);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Button buildCreateUserStory() {
        final Button createUserStoryButton = new Button("Create UserStory");
        createUserStoryButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                UserStory userStory = new UserStory();
                userStory.setName("");
                userStory.setProject(project);
                UserStoryWindow.open(userStory);


            }
        });

        return createUserStoryButton;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                Container.Filterable data = (Container.Filterable) userStoryTable.getContainerDataSource();
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
                                || filterByProperty("User Story Name", item,
                                event.getText());

                    }

                    @Override
                    public boolean appliesToProperty(final Object propertyId) {
                        if (propertyId.equals("Index")
                                || propertyId.equals("User Story Name")) {
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
                ((com.vaadin.data.Container.Filterable) userStoryTable.getContainerDataSource())
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
