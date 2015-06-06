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


    private void createGantt() {
        gantt = new Gantt();
        gantt.setWidth(100, Unit.PERCENTAGE);
        gantt.setHeight(500, Unit.PIXELS);
        gantt.setResizableSteps(true);
        gantt.setMovableSteps(true);
        //   gantt.addAttachListener(ganttAttachListener);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        gantt.setStartDate(cal.getTime());
        cal.add(Calendar.YEAR, 1);
        gantt.setEndDate(cal.getTime());

        cal.setTime(new Date());
        Step step1 = new Step("First step");
        step1.setDescription("Description tooltip");
        step1.setStartDate(cal.getTime().getTime());
        cal.add(Calendar.MONTH, 2);
        step1.setEndDate(cal.getTime().getTime());

        Step step2 = new Step("Second step");
        step2.setDescription("Description tooltip for second step");
        cal.add(Calendar.DATE, 1);
        step2.setStartDate(cal.getTime().getTime());
        cal.add(Calendar.MONTH, 4);
        step2.setEndDate(cal.getTime().getTime());
        step2.setPredecessor(step1);

        Step step3 = new Step("Third step");
        step3.setDescription("<b>HTML</b> <i>content</i> is <u>supported</u> in tooltips.");
        cal.add(Calendar.DATE, 1);
        step3.setStartDate(cal.getTime().getTime());
        cal.add(Calendar.MONTH, 12);
        step3.setEndDate(cal.getTime().getTime());
        step3.setPredecessor(step2);

        Step step4 = new Step("Fourth step");
        step4.setDescription("Tooltip is <b>VTooltip</b>. <p>Looks same for all Vaadin components.");
        step4.setStartDate(step2.getStartDate());
        step4.setEndDate(step2.getEndDate());
        step4.setPredecessor(step1);

        Step stepWithSubSteps = new Step("Step with sub-steps");
        stepWithSubSteps.setDescription("Tooltip for Step with sub-steps");

        cal.setTime(new Date(step1.getStartDate()));
        cal.add(Calendar.DATE, 7);

        SubStep subStep1 = new SubStep("Sub-step A");
        subStep1.setDescription("Tooltip for Sub-step A");
        subStep1.setBackgroundColor("A8D9DD");
        subStep1.setStartDate(step1.getStartDate());
        subStep1.setEndDate(cal.getTime());

        SubStep subStep2 = new SubStep("Sub-step B");
        subStep2.setDescription("Tooltip for Sub-step B");
        subStep2.setBackgroundColor("A8D9BB");
        subStep2.setStartDate(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        subStep2.setEndDate(cal.getTime());

        SubStep subStep3 = new SubStep("Sub-step C");
        subStep3.setDescription("Tooltip for Sub-step C");
        subStep3.setBackgroundColor("A8D999");
        subStep3.setStartDate(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        subStep3.setEndDate(step1.getEndDate());

        stepWithSubSteps.addSubStep(subStep1);
        stepWithSubSteps.addSubStep(subStep2);
        stepWithSubSteps.addSubStep(subStep3);

        //gantt.addStep(step1);
       // gantt.addStep(step2);
       // gantt.addStep(step3);
       // gantt.addStep(step4);
       // gantt.addStep(stepWithSubSteps);

        String[] colors = new String[]{"11FF11", "33FF33", "55FF55",
                "77FF77", "99FF99", "BBFFBB", "DDFFDD"};

        cal.setTime(new Date());
        for (int i = 0; i < 10; i++) {
            Step step = new Step("Step " + i);
            step.setStartDate(cal.getTime().getTime());
            cal.add(Calendar.DATE, 14);
            step.setEndDate(cal.getTime().getTime());
            step.setBackgroundColor(colors[i % colors.length]);
            gantt.addStep(step);
        }

        gantt.addClickListener(new Gantt.ClickListener() {

            @Override
            public void onGanttClick(org.tltv.gantt.Gantt.ClickEvent event) {
                //openStepEditor(event.getStep());
            }
        });

        gantt.addMoveListener(new Gantt.MoveListener() {

            @Override
            public void onGanttMove(MoveEvent event) {
                Date start = new Date(event.getStartDate());
                Date end = new Date(event.getEndDate());

                dateFormat.setTimeZone(gantt.getTimeZone());

                Notification.show("Moved " + event.getStep().getCaption()
                                + " to Start Date: " + dateFormat.format(start)
                                + " End Date: " + dateFormat.format(end),
                        Type.TRAY_NOTIFICATION);
            }
        });

        gantt.addResizeListener(new Gantt.ResizeListener() {

            @Override
            public void onGanttResize(ResizeEvent event) {
                Date start = new Date(event.getStartDate());
                Date end = new Date(event.getEndDate());

                dateFormat.setTimeZone(gantt.getTimeZone());

                Notification.show("Resized " + event.getStep().getCaption()
                                + " to Start Date: " + dateFormat.format(start)
                                + " End Date: " + dateFormat.format(end),
                        Type.TRAY_NOTIFICATION);
            }
        });
    }
}
