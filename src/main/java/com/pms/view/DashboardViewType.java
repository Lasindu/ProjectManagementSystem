package com.pms.view;

//#import com.pms.view.dashboard.DashboardView;
import com.pms.view.crhandle.CrHandleView;
import com.pms.view.dashboard.DashboardView;
import com.pms.view.memberassign.MemberAssignView;
import com.pms.view.scheduletask.ScheduleTaskView;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum DashboardViewType {
    DASHBOARD("dashboard", DashboardView.class, FontAwesome.HOME, true), CRHANDLE(
            "CR_Handle", CrHandleView.class, FontAwesome.BAR_CHART_O, false), MEMBERASSIGN(
            "Member_Assign", MemberAssignView.class, FontAwesome.TABLE, false), SCHEDULETASK(
            "Schedule_Task", ScheduleTaskView.class, FontAwesome.FILE_TEXT_O, true);

    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;

    private DashboardViewType(final String viewName,
            final Class<? extends View> viewClass, final Resource icon,
            final boolean stateful) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static DashboardViewType getByViewName(final String viewName) {
        DashboardViewType result = null;
        for (DashboardViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
