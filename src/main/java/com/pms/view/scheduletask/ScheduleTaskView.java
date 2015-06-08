package com.pms.view.scheduletask;

import com.pms.DashboardUI;
import com.pms.component.*;
import com.pms.dao.ProjectDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Collection;
import java.util.List;

/**
 * Created by Upulie on 4/1/2015.
 */
public class ScheduleTaskView extends CssLayout implements View {



    private VerticalLayout mainLayout;
    private Panel mainPanel;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        if(viewChangeEvent.getParameters() != null){
            // split at "/", add each part as a label
            String[] msgs = viewChangeEvent.getParameters().split("/");

            if(msgs.length==1)
            {
                if(msgs[0].equals(""))
                {
                    mainLayout.removeAllComponents();
                    ViewAllProjects viewAllProjects=new ViewAllProjects();
                    mainLayout.addComponent(viewAllProjects.getAllProjects());

                }
                else
                {
                    mainLayout.removeAllComponents();
                    String projectName=msgs[0].replace("%20", " ");
                    ViewProject viewProject=new ViewProject(projectName);
                    mainLayout.addComponent(viewProject.getProject());

                }

            }
            if(msgs.length==2)
            {
                mainLayout.removeAllComponents();
                String projectName=msgs[0].replace("%20", " ");
                String userStoryName= msgs[1].replace("%20", " ");
                ViewUserStory viewUserStory=new ViewUserStory(projectName,userStoryName);
                mainLayout.addComponent(viewUserStory.getUserStory());

            }
            if(msgs.length==3)
            {
                mainLayout.removeAllComponents();
                String projectName=msgs[0].replace("%20", " ");
                String userStoryName= msgs[1].replace("%20", " ");
                String taskId=msgs[2].replace("%20", " ");
                ViewTask viewTask=new ViewTask(taskId);
                mainLayout.addComponent(viewTask.getTask());

            }

        }


    }



    public ScheduleTaskView()
    {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();

        mainPanel= new Panel();
        mainPanel.setSizeFull();
        addComponent(mainPanel);

        mainLayout= new VerticalLayout();
        //mainLayout.setSizeFull();
        mainPanel.setContent(mainLayout);

        ViewAllProjects viewAllProjects=new ViewAllProjects();
        mainLayout.addComponent(viewAllProjects.getAllProjects());


    }




}
