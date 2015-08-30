package com.pms.component.ganttchart.scheduletask;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
    Map sortedTaskMap;
    int sortedTaskCount;
    UserStoryDAO userStoryDAO;
    TaskDAO taskDAO;
    UserStory userStory;

    public Map prioritize(UserStory userStory) {

        this.userStory = userStory;

        sortedTaskMap = new HashMap<Integer, Task>();
        sortedTaskCount = 0;
        userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        taskDAO = (TaskDAO) DashboardUI.context.getBean("Task");
        allTasks = userStoryDAO.getUserStoryTaskList(userStory);


        List<Task> taskList = new ArrayList<Task>();
        taskList.addAll(allTasks);


        List<Task> hasPreRequisite = new ArrayList<Task>();
        List<Task> noPreRequisite = new ArrayList<Task>();

        for (Task task : taskList) {
            if (task.getPreRequisits() == null || task.getPreRequisits().isEmpty()) {
                noPreRequisite.add(task);

            } else {
                hasPreRequisite.add(task);
            }
        }


        orderPriority(noPreRequisite);
        prioritizeTasksHasPreRequisite(hasPreRequisite);


        return  sortedTaskMap;
    }

    public void allocateMemberToTask(Task task) {

        sortedTaskCount++;
        sortedTaskMap.put(sortedTaskCount,task);

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

        } else if (priority1Tasks.size() > 1)
            checkDependency(priority1Tasks);

        if (priority2Tasks.size() == 1) {
            allocateMemberToTask(priority2Tasks.get(0));

        } else if (priority2Tasks.size() > 1)
            checkDependency(priority2Tasks);


        if (priority3Tasks.size() == 1) {
            allocateMemberToTask(priority3Tasks.get(0));

        } else if (priority3Tasks.size() > 1)
            checkDependency(priority3Tasks);


        if (priority4Tasks.size() == 1) {
            allocateMemberToTask(priority4Tasks.get(0));

        } else if (priority4Tasks.size() > 1)
            checkDependency(priority4Tasks);

        if (priority5Tasks.size() == 1) {
            allocateMemberToTask(priority5Tasks.get(0));

        } else if (priority5Tasks.size() > 1)
            checkDependency(priority5Tasks);


    }

    private void checkDependency(List<Task> taskList) {
        List<Task> hasDependency = new ArrayList<Task>();
        List<Task> noDependency = new ArrayList<Task>();


        for (Task task : taskList) {
            if (task.getDependancy() == null || task.getDependancy().isEmpty())
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


        }
        else if (taskList.size()>1) {
            Map<Task, Integer> taskWithDependencyHighestPriority = new HashMap<Task, Integer>();
            Map taskWithHighestPrioritycount = new HashMap<Task, Integer>();

            for (Task task : taskList) {
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

                taskWithDependencyHighestPriority.put(task, dependencyHighestPriority);
                taskWithHighestPrioritycount.put(task, highestPriorityCount);


            }

            taskWithDependencyHighestPriority= sortTaskMapAccendingOrder(taskWithDependencyHighestPriority);

            Multimap<Integer, Task> multiMap = HashMultimap.create();

            for (Map.Entry<Task, Integer> entry : taskWithDependencyHighestPriority.entrySet()) {
                multiMap.put(entry.getValue(), entry.getKey());
            }

            for (Map.Entry<Integer, Collection<Task>> entry : multiMap.asMap().entrySet()) {
                // System.out.println("Original value: " + entry.getKey() + " was mapped to keys: "
                //         + entry.getValue());

                //This tasks has same highet dependency prority

                //this map only include tasks that come to for loop ittration
                Map<Task, Integer> tempTaskWithHighestPrioritycount = new HashMap<Task, Integer>();
                Collection<Task> tempTaskList = entry.getValue();

                for(Task task: tempTaskList)
                {
                    tempTaskWithHighestPrioritycount.put(task,(Integer)taskWithHighestPrioritycount.get(task));

                }












                tempTaskWithHighestPrioritycount = sortTaskMapDesendingOrder(tempTaskWithHighestPrioritycount);


                Multimap<Integer, Task> multiMap2 = HashMultimap.create();

                for (Map.Entry<Task, Integer> entry2 : tempTaskWithHighestPrioritycount.entrySet()) {
                    multiMap2.put(entry2.getValue(), entry2.getKey());
                }

                for (Map.Entry<Integer, Collection<Task>> entry2 : multiMap2.asMap().entrySet()) {

                    Collection<Task> tempTaskList2 = entry2.getValue();

                    if(tempTaskList2.size()>1)
                    {




                        Map hasDependecnyWithTime = new HashMap<Task, Integer>();

                        for(Task task:tempTaskList2)
                        {
                            hasDependecnyWithTime.put(task,Integer.parseInt(task.getCompleteTime()));

                        }

                        //sorting the task with time
                        hasDependecnyWithTime= sortTaskMapAccendingOrder(hasDependecnyWithTime);



                        //allocating tasks
                        Iterator it = hasDependecnyWithTime.entrySet().iterator();
                        while (it.hasNext()) {
                            {
                                Map.Entry pair = (Map.Entry) it.next();
                                //System.out.println(pair.getKey() + " = " + pair.getValue());

                                allocateMemberToTask((Task)pair.getKey());
                                //sortedUserStoryMap.put(sortedUserStoryCount, pair.getKey());
                                it.remove();

                            }
                        }






                    }
                    else
                    {
                        //only one task come to inside this else condition for loop used to get first element of collection

                        for(Task task1: tempTaskList2)
                        {
                            allocateMemberToTask(task1);

                        }



                    }


                }









            }










            }

    }

    private void prioritizeTaskNoAnyDependency(List<Task> taskList)
    {
        Map noDependency_WithTime = new HashMap<Task, Integer>();

        for(Task task:taskList)
        {
            noDependency_WithTime.put(task,Integer.parseInt(task.getCompleteTime()));

        }

        //sorting the task with time
        noDependency_WithTime= sortTaskMapAccendingOrder(noDependency_WithTime);



        //allocating tasks
        Iterator it = noDependency_WithTime.entrySet().iterator();
        while (it.hasNext()) {
            {
                Map.Entry pair = (Map.Entry) it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue());

                allocateMemberToTask((Task)pair.getKey());
                //sortedUserStoryMap.put(sortedUserStoryCount, pair.getKey());
                it.remove();

            }
        }





    }

    private void prioritizeTasksHasPreRequisite(List<Task> taskList) {
        int count = taskList.size();
        int loopCount = 0;

        List<Task> allPreRequisiteAllocatedList = new ArrayList<Task>();

        while (loopCount < count) {
            loopCount++;
            System.out.println("Inside task prioriteze while loop");


            for (Task task : taskList) {
                if (checkInAlreadySortedList(task.getName()))
                    continue;


                String preRequisiteNameList = task.getPreRequisits();

                if (preRequisiteNameList != null && !preRequisiteNameList.isEmpty()) {
                    String[] preRequisiteList = preRequisiteNameList.split(",");

                    boolean allAllocated = true;

                    for (String preRequisite : preRequisiteList) {
                        Task task1 = taskDAO.getTaskFromUserStroyNameAndTaskName(userStory.getName(), preRequisite);

                        if (!checkInAlreadySortedList(task1.getName())) {
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



    private boolean checkInAlreadySortedList(String taskName) {
        for (int x = 1; x <= sortedTaskCount; x++) {
            if (((Task) sortedTaskMap.get(x)).getName().equals(taskName))
                return true;
        }
        return false;
    }



    //1,2,3,4,5
    private static Map<Task, Integer> sortTaskMapAccendingOrder(Map<Task, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Task, Integer>> list =
                new LinkedList<Map.Entry<Task, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Task, Integer>>() {
            public int compare(Map.Entry<Task, Integer> o1,
                               Map.Entry<Task, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<Task, Integer> sortedMap = new LinkedHashMap<Task, Integer>();
        for (Iterator<Map.Entry<Task, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<Task, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    // 99,98,97,96
    private static Map<Task, Integer> sortTaskMapDesendingOrder(Map<Task, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Task, Integer>> list =
                new LinkedList<Map.Entry<Task, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Task, Integer>>() {
            public int compare(Map.Entry<Task, Integer> o1,
                               Map.Entry<Task, Integer> o2) {
                //return (o1.getValue()).compareTo(o2.getValue());
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<Task, Integer> sortedMap = new LinkedHashMap<Task, Integer>();
        for (Iterator<Map.Entry<Task, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<Task, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }





}


















































