package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.ProjectDAO;
import com.pms.domain.Project;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;

/**
 * Created by Damitha on 6/2/2015.
 */
public class ViewAllProjects {

    public  VerticalLayout viewProjectLayout;
    private Table viewProjectTable;
    private Button create;

    public ViewAllProjects()
    {
        buildViewAllProjects();

    }

    public Component getAllProjects()
    {
        return viewProjectLayout;
    }


    private void buildViewAllProjects()
    {
        viewProjectLayout = new VerticalLayout();
        //viewProjectLayout.setCaption("View Project");
        viewProjectLayout.setMargin(true);
        viewProjectLayout.setSpacing(true);
        viewProjectLayout.setSizeFull();

        viewProjectLayout.addComponent(buildToolbar());


        viewProjectTable= new Table("");
        //viewProjectTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
        viewProjectTable.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        viewProjectTable.addStyleName(ValoTheme.TABLE_COMPACT);
        viewProjectTable.setSelectable(true);

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

        ProjectDAO projectDAO= (ProjectDAO) DashboardUI.context.getBean("Project");

        List<Project> projectList=projectDAO.getAllProjects();

        for(int x=0;x<projectList.size();x++)
        {
            int index=x+1;

            Button editProjectButton=new Button("Edit Project");
            Button viewProjectButton=new Button("View Project");
            editProjectButton.setData(projectList.get(x));
            viewProjectButton.setData(projectList.get(x).getName());

            viewProjectTable.addItem(new Object[] {index,projectList.get(x).getName(),projectList.get(x).getClientName(),projectList.get(x).getDescription(),projectList.get(x).getDate(),projectList.get(x).getStartDate(),projectList.get(x).getDeliveredDate(),editProjectButton,viewProjectButton},index);


            editProjectButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    ProjectWindow.open((Project)event.getButton().getData());


                }
            });

            viewProjectButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {

                    DashboardUI.getCurrent().getNavigator().navigateTo("Schedule_Task/"+(String)event.getButton().getData());


                }
            });


        }



        viewProjectLayout.addComponent(viewProjectTable);
        viewProjectLayout.setExpandRatio(viewProjectTable,1);




        //return  viewProjectLayout;

    }










    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label("Project List");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        create = buildCreateReport();
        HorizontalLayout tools = new HorizontalLayout(buildFilter(),
                create);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Button buildCreateReport() {

        final Button createNewProjectButton = new Button("Create New Project");
        createNewProjectButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Project project= new Project();
                project.setName("");
                ProjectWindow.open(project);


            }
        });


        return createNewProjectButton;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                Container.Filterable data = (Container.Filterable) viewProjectTable.getContainerDataSource();
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
                ((com.vaadin.data.Container.Filterable) viewProjectTable.getContainerDataSource())
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
