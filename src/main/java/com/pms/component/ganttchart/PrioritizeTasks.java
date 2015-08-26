package com.pms.component.ganttchart;

import com.pms.DashboardUI;
import com.pms.dao.TaskDAO;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Task;
import com.pms.domain.UserStory;

import java.util.*;

/**
 * Created by Upulie on 8/25/2015.
 */
public class PrioritizeTasks {

    Collection<Task> allTasks;
    int sortedTaskCount;
    UserStoryDAO userStoryDAO;
    TaskDAO taskDAO;
    UserStory userStory;

    public void prioritize(UserStory userStory) {

        this.userStory = userStory;
        sortedTaskCount = 0;
        userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        allTasks = userStoryDAO.getUserStoryTaskList(userStory);


        List<Task> taskList = new ArrayList<Task>();
        taskList.addAll(allTasks);


        List<Task> hasPreRequisite = new ArrayList<Task>();
        List<Task> noPreRequisite = new ArrayList<Task>();

        for (Task task : taskList) {
            if (task.getPreRequisits() == null || task.getPreRequisits() == "") {
                noPreRequisite.add(task);

            } else {
                hasPreRequisite.add(task);
            }
        }


        orderPriority(noPreRequisite);
        prioritizeTasksHasPreRequisite(hasPreRequisite);

    }

    public void allocateMemberToTask(Task task) {

    }

    private void orderPriority(List<Task> taskList) {
        List<Task> priority1Tasks = new ArrayList<Task>();
        List<Task> priority2Tasks = new ArrayList<Task>();
        List<Task> priority3Tasks = new ArrayList<Task>();
        List<Task> priority4Tasks = new ArrayList<Task>();
        List<Task> priority5Tasks = new ArrayList<Task>();

        for (Task task : taskList) {
            switch (task.getPriority()) {
                case 1:
                    priority1Tasks.add(task);
                    break;
                case 2:
                    priority2Tasks.add(task);
                    break;
                case 3:
                    priority3Tasks.add(task);
                    break;
                case 4:
                    priority4Tasks.add(task);
                    break;
                case 5:
                    priority5Tasks.add(task);
                    break;

            }

        }


        if (priority1Tasks.size() == 1) {
            allocateMemberToTask(priority1Tasks.get(0));
            sortedTaskCount++;
        } else if (priority1Tasks.size() > 1)
            checkDependency(priority1Tasks);

        if (priority2Tasks.size() == 1) {
            allocateMemberToTask(priority2Tasks.get(0));
            sortedTaskCount++;
        } else if (priority2Tasks.size() > 1)
            checkDependency(priority2Tasks);


        if (priority3Tasks.size() == 1) {
            allocateMemberToTask(priority3Tasks.get(0));
            sortedTaskCount++;
        } else if (priority3Tasks.size() > 1)
            checkDependency(priority3Tasks);


        if (priority4Tasks.size() == 1) {
            allocateMemberToTask(priority4Tasks.get(0));
            sortedTaskCount++;
        } else if (priority4Tasks.size() > 1)
            checkDependency(priority4Tasks);

        if (priority5Tasks.size() == 1) {
            allocateMemberToTask(priority5Tasks.get(0));
            sortedTaskCount++;
        } else if (priority5Tasks.size() > 1)
            checkDependency(priority5Tasks);


    }

    private void checkDependency(List<Task> taskList) {
        List<Task> hasDependency = new ArrayList<Task>();
        List<Task> noDependency = new ArrayList<Task>();


        for (Task task : taskList) {
            if (task.getDependancy() == null || task.getDependancy() == "")
                noDependency.add(task);
            else
                hasDependency.add(task);
        }

        prioritizeTasksHasDependency(hasDependency);
        prioritizeTaskNoAnyDependency(noDependency);

    }

    private void prioritizeTasksHasDependency(List<Task> taskList)
    {
        if(taskList.size()==1)
        {
            allocateMemberToTask(taskList.get(0));
            sortedTaskCount++;

        }
        else if (taskList.size()>1)
        {
            Map taskWithDependencyHighestPriority= new HashMap<Task,Integer>();
            Map taskWithHighestPrioritycount =  new HashMap<Task,Integer>();

            for(Task task: taskList)
            {
                int dependencyHighestPriority = 0;
                int highestPriorityCount = 0;

                String dependencyNameList = task.getDependancy();
                if (dependencyNameList != null && !dependencyNameList.isEmpty()) {
                    String[] dependencies = dependencyNameList.split(",");

                    //find the dependency highest  priority
                    for (String dependencyName : dependencies) {
                        Task task1 = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(), dependencyName);

                        if (dependencyHighestPriority == 0)
                            dependencyHighestPriority = task1.getPriority();
                        else if (task1.getPriority() < dependencyHighestPriority)
                            dependencyHighestPriority = task1.getPriority();
                    }


                    //find highest priority dependency user story count
                    for (String dependencyName : dependencies) {
                        Task task1 = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(), dependencyName);

                        if (task1.getPriority() == dependencyHighestPriority)
                            highestPriorityCount += 1;
                    }

                }

                taskWithDependencyHighestPriority.put(task,dependencyHighestPriority);
                taskWithHighestPrioritycount.put(task,highestPriorityCount);


            }







        }

    }

    private void prioritizeTaskNoAnyDependency(List<Task> taskList)
    {

    }

    private void prioritizeTasksHasPreRequisite(List<Task> taskList) {
        int count = taskList.size();
        int loopCount = 0;

        List<Task> allPreRequisiteAllocatedList = new ArrayList<Task>();

        while (loopCount > count) {
            loopCount++;
            System.out.println("Inside task prioriteze while loop");


            for (Task task : taskList) {
                //TODO if task allocated need to continue

                String preRequisiteNameList = task.getPreRequisits();

                if (preRequisiteNameList != null && preRequisiteNameList != "") {
                    String[] preRequisiteList = preRequisiteNameList.split(",");

                    boolean allAllocated = true;

                    for (String preRequisite : preRequisiteList) {
                        Task task1 = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(), preRequisite);

                        //TODO check task is already allocated
                        if (true) {
                            allAllocated = false;
                            break;

                        }
                    }


                    if (allAllocated) {
                        allPreRequisiteAllocatedList.add(task);
                    }
                }


            }


            orderPriority(allPreRequisiteAllocatedList);
            allPreRequisiteAllocatedList = new ArrayList<Task>();


        }


    }


}


















































