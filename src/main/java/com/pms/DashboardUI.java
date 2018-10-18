package com.pms;

import com.google.common.eventbus.Subscribe;
import com.pms.dao.LoginDAO;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
//import com.vaadin.demo.dashboard.dao.DataProvider;
//import com.vaadin.demo.dashboard.dao.dummy.DummyDataProvider;
import com.pms.domain.User;
import com.pms.event.DashboardEvent.BrowserResizeEvent;
import com.pms.event.DashboardEvent.CloseOpenWindowsEvent;
import com.pms.event.DashboardEvent.UserLoggedOutEvent;
import com.pms.event.DashboardEvent.UserLoginRequestedEvent;
import com.pms.event.DashboardEventBus;
import com.pms.view.LoginView;
import com.pms.view.MainView;
import com.vaadin.server.*;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Locale;

@Theme("dashboard")
@Widgetset("com.pms.DashboardWidgetSet")
@Title("Project Management System")
@SuppressWarnings("serial")
public final class DashboardUI extends UI {

    /*
     * This field stores an access to the dummy backend layer. In real
     * applications you most likely gain access to your beans trough lookup or
     * injection; and not in the UI but somewhere closer to where they're
     * actually accessed.
     */
    private final DashboardEventBus dashboardEventbus = new DashboardEventBus();
    public static ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(VaadinServlet.getCurrent().getServletContext());

    @Override
    protected void init(final VaadinRequest request) {
          setLocale(Locale.US);

        DashboardEventBus.register(this);
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);



        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event bus on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            final BrowserWindowResizeEvent event) {
                        DashboardEventBus.post(new BrowserResizeEvent());
                    }
                });
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
               User.class.getName());

       //if (user != null && "admin".equals(user.getRole())) {
        if (user != null ) {

            // Authenticated user
            setContent(new MainView());
            removeStyleName("loginview");
            getNavigator().navigateTo(getNavigator().getState());
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }

    @Subscribe
    public void userLoginRequested(final UserLoginRequestedEvent event) {
       // User user = getDataProvider().authenticate(event.getUserName(),
         //       event.getPassword());

        LoginDAO loginDAO=(LoginDAO)DashboardUI.context.getBean("UserLogin");
        User user =loginDAO.authenticateUser(event.getUserName(),event.getPassword());

        if(user!=null)
        {
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            VaadinSession.getCurrent().setAttribute("role", user.getRole());
            updateContent();

        }
        else
        {
            setContent(new LoginView());
            addStyleName("loginview");
        }

    }

    @Subscribe
    public void userLoggedOut(final UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close();
        }
    }

    /**
     * @return An instance for accessing the (dummy) services layer.
     */

    public static DashboardEventBus getDashboardEventbus() {
        return ((DashboardUI) getCurrent()).dashboardEventbus;
    }
}
