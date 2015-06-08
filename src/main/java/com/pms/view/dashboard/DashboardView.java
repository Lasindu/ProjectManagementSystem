package com.pms.view.dashboard;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import com.pms.component.ganttchart.DemoUI;
import com.pms.domain.Project;
import com.pms.domain.User;
import com.vaadin.server.VaadinSession;
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
public class DashboardView  extends CssLayout implements View {

   // private final VerticalLayout root;
    private Gantt gantt;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "MMM dd HH:mm:ss zzz yyyy");

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    public DashboardView() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());

        List<Project> projectList = new ArrayList();
        projectList.addAll(user.getProjects());
        setSizeFull();
        removeAllComponents();

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);

       /* for(int x=0;x<projectList.size();x++)
        {
            DemoUI demoUI = new DemoUI();
            VerticalLayout layout= (VerticalLayout) demoUI.init();
            layout.setCaption(projectList.get(x).getName());
            //setContent(demoUI.init());
            tabs.addComponent(layout);

        }*/
        DemoUI demoUI = new DemoUI();
        VerticalLayout layout= (VerticalLayout) demoUI.init();
        layout.setCaption("1111111");
        //layout.setCaption(projectList.get(x).getName());
        //setContent(demoUI.init());
        tabs.addComponent(layout);

        DemoUI demoUI1 = new DemoUI();
        VerticalLayout layout1= (VerticalLayout) demoUI1.init();
        layout1.setCaption("dffdfdf");
        //layout.setCaption(projectList.get(x).getName());
        //setContent(demoUI.init());
        tabs.addComponent(layout1);

        addComponent(tabs);
       // addStyleName(ValoTheme.PANEL_BORDERLESS);
        //setSizeFull();
/*        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        // addComponent(root);

        root.addComponent(new Label("Dashboard View"));*/


        //final VerticalLayout layout = new VerticalLayout();
        //layout.setStyleName("demoContentLayout");
        //layout.setSizeFull();


       // createGantt();
       // layout.addComponent(gantt);

        //addComponent(layout);
        //layout.setExpandRatio(layout, 1);


        //DemoUI demoUI = new DemoUI();
        //setContent(demoUI.init());


    }

}
