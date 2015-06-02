package com.pms.component;

import com.pms.DashboardUI;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Task;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Damitha on 6/2/2015.
 */
public class ViewTask extends CustomComponent {

    public VerticalLayout viewTaskLayout;
    private Task task;
    private int taskId;
    public ViewTask(String taskId)
    {
        this.taskId=Integer.parseInt(taskId);
        buildTaskView();

    }

    public Component getTask()
    {
        return viewTaskLayout;
    }


    private void buildTaskView()
    {
        viewTaskLayout=new VerticalLayout();
        viewTaskLayout.setMargin(true);
        viewTaskLayout.setSpacing(true);


        TaskDAO taskDAO= (TaskDAO) DashboardUI.context.getBean("Task");
        task=taskDAO.getTaskByTaskId(taskId);


        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label title = new Label(task.getUserStory().getProject().getName()+" - "+task.getUserStory().getName()+" - "+task.getTaskId());
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H1);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        viewTaskLayout.addComponent(header);


        if(task!=null)
        {
            Label name = new Label("Task :   "+task.getName());
            viewTaskLayout.addComponent(name);
            Label description=new Label("Description :  "+task.getDescription());
            viewTaskLayout.addComponent(description);
            Label date= new Label("Date : "+task.getDate());
            viewTaskLayout.addComponent(date);
            Label priority = new Label("Priority : "+task.getPriority());
            viewTaskLayout.addComponent(priority);
            Label severity= new Label("Severity : "+task.getSeverity());
            viewTaskLayout.addComponent(severity);
            Label memberType = new Label("Member Type : "+task.getMemberType());
            viewTaskLayout.addComponent(memberType);
            Label estimateTime = new Label("Estimate Time "+task.getEstimateTime());
            viewTaskLayout.addComponent(estimateTime);
            Label assingedTo = new Label("Assigned To : "+task.getAssignedTo());
            viewTaskLayout.addComponent(assingedTo);
            Label completeTime = new Label("Complite Time : " +task.getCompleteTime());
            viewTaskLayout.addComponent(completeTime);
            Label isCr= new Label("Is Cr :"+task.isCr());
            viewTaskLayout.addComponent(isCr);




        }





    }
}






















