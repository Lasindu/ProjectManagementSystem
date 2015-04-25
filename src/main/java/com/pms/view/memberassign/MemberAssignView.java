package com.pms.view.memberassign;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Upulie on 4/1/2015.
 */
public class MemberAssignView extends VerticalLayout implements View {

    private final VerticalLayout root;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    public MemberAssignView()
    {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        addComponent(root);

        root.addComponent(new Label("Member Assign View"));
    }

}
