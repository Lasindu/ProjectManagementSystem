package com.pms.view.dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.pms.component.ganttchart.DemoUI;
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
public class DashboardView  extends Panel implements View {

   // private final VerticalLayout root;
    private Gantt gantt;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "MMM dd HH:mm:ss zzz yyyy");

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    public DashboardView() {
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


        DemoUI demoUI = new DemoUI();
        setContent(demoUI.init());


    }

}
