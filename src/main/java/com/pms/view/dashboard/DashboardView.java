package com.pms.view.dashboard;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import com.pms.DashboardUI;
import com.pms.component.ganttchart.DemoUI;
import com.pms.component.ganttchart.GanttChart;
import com.pms.dao.UserDAO;
import com.pms.domain.Project;
import com.pms.domain.User;
import com.pms.view.LoginView;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.tltv.gantt.Gantt;
import org.tltv.gantt.Gantt.MoveEvent;
import org.tltv.gantt.Gantt.ResizeEvent;
import org.tltv.gantt.client.shared.AbstractStep;
import org.tltv.gantt.client.shared.Step;
import org.tltv.gantt.client.shared.SubStep;

import com.pms.component.ganttchart.util.UriFragmentWrapperFactory;
import com.pms.component.ganttchart.util.Util;
import com.pms.component.ganttchart.GanttListener;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.DateToLongConverter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification.Type;

/**
 * Created by Upulie on 4/2/2015.
 */
public class DashboardView  extends VerticalLayout implements View {


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    public DashboardView() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());


        UserDAO userDAO = (UserDAO) DashboardUI.context.getBean("User");
        //used session user to get the user projects
        final List<Project> projectList = new ArrayList();
        User projectsLoadedUser= userDAO.loadUserProjects(user);
        projectList.addAll(projectsLoadedUser.getProjects());





        final VerticalLayout mainLayout= new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(new MarginInfo(true,false,false,false));
        //mainLayout.setMargin(true);

        Label title = new Label("Dashboard");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        mainLayout.addComponent(title);


        setSizeFull();

/*        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);*/



        MenuBar menuBar = new MenuBar();
        mainLayout.addComponent(menuBar);
        final VerticalLayout layout = new VerticalLayout();
        mainLayout.addComponent(layout);

        for(int x=0;x<projectList.size();x++)
        {
            final Project project=projectList.get(x);

            if (x==0)
            {
                layout.addComponent(buildGanntChart(project));
            }
            menuBar.addItem(projectList.get(x).getName(), new Command() {

                @Override
                public void menuSelected(MenuItem selectedItem) {
                    layout.removeAllComponents();
                    layout.addComponent(buildGanntChart(project));

                }
            });
        }










   /*     for(int x=0;x<projectList.size();x++)
        {
            //GanttChart ganttChart = new GanttChart();
            VerticalLayout layout1 = new VerticalLayout();
            layout1.setCaption(projectList.get(x).getName());
            //layout1.addComponent(buildGanntChart(projectList.get(x)));
            tabs.addTab(layout1);

        }*/





       // mainLayout.addComponent(tabs);
        addComponent(mainLayout);



    }

    private Component buildGanntChart(Project project)
    {
        GanttChart ganttChart = new GanttChart();
        VerticalLayout layout = new VerticalLayout();
//        layout.setCaption(project.getName());
        layout.addComponent(ganttChart.init(project));
        return layout;

    }

}



