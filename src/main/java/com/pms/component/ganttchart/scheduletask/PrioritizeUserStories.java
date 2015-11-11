package com.pms.component.ganttchart.scheduletask;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pms.DashboardUI;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;

import java.util.*;

/**
 * Created by Upulie on 6/28/2015.
 */
public class PrioritizeUserStories {

    Collection<UserStory> allUserStories;
    Map sortedUserStoryMap;
    int userStoryCount;
    int sortedUserStoryCount;
    UserStoryDAO userStoryDAO;

    //This method return Prioritize UserStoryMap
    public Map getPrioritizeUserStoryMap(Project project)
    {
        prioritize(project);
        return sortedUserStoryMap;
    }

    public void prioritize(Project project) {

        //final prioritize user story map
        sortedUserStoryMap = new HashMap<Integer, UserStory>();
        sortedUserStoryCount = 0;


        //get all user stories form database
        userStoryDAO = (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        allUserStories = new ArrayList<UserStory>();
        if (userStoryDAO.getAllUserSeriesOfProject(project) != null)
            allUserStories.addAll(userStoryDAO.getAllUserSeriesOfProject(project));
        userStoryCount = allUserStories.size();


        List<UserStory> priority1UserStories = new ArrayList<UserStory>();
        List<UserStory> priority2UserStories = new ArrayList<UserStory>();
        List<UserStory> priority3UserStories = new ArrayList<UserStory>();
        List<UserStory> priority4UserStories = new ArrayList<UserStory>();
        List<UserStory> priority5UserStories = new ArrayList<UserStory>();


        List<UserStory> alreadyDoneUserStories = new ArrayList<UserStory>();
        List<UserStory> notDoneUserStories = new ArrayList<UserStory>();

        //divide done and not done user stories
        for (UserStory userStory : allUserStories) {

            if (userStory.getState() != null && userStory.getState().equals("done"))
                alreadyDoneUserStories.add(userStory);
            else
                notDoneUserStories.add(userStory);
        }


        //already done user stories adding to the sortedUserStory Map
        for (int x = 1; x <= alreadyDoneUserStories.size(); x++) {

            for (UserStory userStory : alreadyDoneUserStories) {

                if (userStory.getSequenceNo() == x) {
                    sortedUserStoryCount += 1;
                    sortedUserStoryMap.put(sortedUserStoryCount, userStory);
                    break;
                }
            }


        }


        for (UserStory userStory : notDoneUserStories) {
            if (userStory.getPriority() == 1) {
                priority1UserStories.add(userStory);
            }

            if (userStory.getPriority() == 2) {
                priority2UserStories.add(userStory);
            }

            if (userStory.getPriority() == 3) {
                priority3UserStories.add(userStory);
            }

            if (userStory.getPriority() == 4) {
                priority4UserStories.add(userStory);
            }

            if (userStory.getPriority() == 5) {
                priority5UserStories.add(userStory);
            }
        }


        if (priority1UserStories.size() > 1)
            userStoriesCheckPreRequisitAndDependency(priority1UserStories);
        else if (priority1UserStories.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, priority1UserStories.get(0));
        }

        if (priority2UserStories.size() > 1)
            userStoriesCheckPreRequisitAndDependency(priority2UserStories);
        else if (priority2UserStories.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, priority2UserStories.get(0));
        }

        if (priority3UserStories.size() > 1)
            userStoriesCheckPreRequisitAndDependency(priority3UserStories);
        else if (priority3UserStories.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, priority3UserStories.get(0));

        }


        if (priority4UserStories.size() > 1)
            userStoriesCheckPreRequisitAndDependency(priority4UserStories);
        else if (priority4UserStories.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, priority4UserStories.get(0));

        }

        if (priority5UserStories.size() > 1)
            userStoriesCheckPreRequisitAndDependency(priority5UserStories);
        else if (priority5UserStories.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, priority5UserStories.get(0));

        }



        //this method used to update state initial to working
        stateUpdate();


    }



    //check the state of the user story and update state if update is necessary
    private void stateUpdate() {
        Iterator it = sortedUserStoryMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove();

            UserStory userStory=(UserStory)pair.getValue();

            if(userStory.getState().equals("working"))
            {
                break;
            }
            else if(userStory.getState().equals("initial"))
            {
                userStory.setState("working");
                userStoryDAO.updateUserStory(userStory);
                sortedUserStoryMap.put(pair.getKey(),userStory);
                break;
            }
        }

    }


    private void userStoriesCheckPreRequisitAndDependency(List<UserStory> userStories) {

        List<UserStory> noPreRequist = new ArrayList<UserStory>();
        List<UserStory> hasPreRequist = new ArrayList<UserStory>();

        for (UserStory userStory : userStories) {
            if (userStory.getPreRequisits() != null && !userStory.getPreRequisits().isEmpty())
                hasPreRequist.add(userStory);

            else
                noPreRequist.add(userStory);

        }


        List<UserStory> noPreRequisit_NoDependency = new ArrayList<UserStory>();
        List<UserStory> noPreRequisit_hasDependency = new ArrayList<UserStory>();

        for (UserStory userStory : noPreRequist) {
            if (userStory.getDependancy() != null && !userStory.getDependancy().isEmpty())
                noPreRequisit_hasDependency.add(userStory);
            else
                noPreRequisit_NoDependency.add(userStory);


        }


        if (noPreRequisit_hasDependency.size() > 1)
            prioritize_UserStories_HasDependency(noPreRequisit_hasDependency);
        else if (noPreRequisit_hasDependency.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, noPreRequisit_hasDependency.get(0));
        }

        if (noPreRequisit_NoDependency.size() > 1)
            prioritize_UserStories_NoDependency(noPreRequisit_NoDependency);
        else if (noPreRequisit_NoDependency.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, noPreRequisit_NoDependency.get(0));

        }

        if (hasPreRequist.size() > 1)
            prioritize_UserStories_HasPreReqisits(hasPreRequist);
        else if (hasPreRequist.size() == 1) {
            sortedUserStoryCount += 1;
            sortedUserStoryMap.put(sortedUserStoryCount, hasPreRequist.get(0));

        }


    }


    private void prioritize_UserStories_HasPreReqisits(List<UserStory> userStories) {

        List<UserStory> allPrerequisitdAllocatedList = new ArrayList<UserStory>();

        //total userstory count

        int newSortedCount = sortedUserStoryCount + userStories.size();

        int whileLoopCount = 0;

        while (sortedUserStoryCount != newSortedCount) {
            if (whileLoopCount > userStories.size())
                break;


            whileLoopCount += 1;


            System.out.println("inside has prerequisite while loop allocated user story count" + sortedUserStoryCount);

            for (UserStory userStory : userStories) {

                if (checkInSortedList(userStory.getName()))
                    continue;

                String preRequistNameList = userStory.getPreRequisits();

                if (preRequistNameList != null && !preRequistNameList.isEmpty()) {
                    String[] preReqistiList = preRequistNameList.split(",");


                    boolean allallocated = true;
                    for (String preRequist : preReqistiList) {
                        UserStory userStory1 = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), preRequist);

                        // if(!userStory1.getState().equals("allocated"))
                        if (!checkInSortedList(userStory1.getName())) {
                            allallocated = false;
                            break;
                        }

                    }

                    if (allallocated) {
                        allPrerequisitdAllocatedList.add(userStory);

                    }

                }


            }

            List<UserStory> hasPreRequisit_NoDependency = new ArrayList<UserStory>();
            List<UserStory> hasPreRequisit_hasDependency = new ArrayList<UserStory>();
            for (UserStory userStory : allPrerequisitdAllocatedList) {
                if (userStory.getDependancy() == null || userStory.getDependancy().isEmpty())
                    hasPreRequisit_NoDependency.add(userStory);
                else
                    hasPreRequisit_hasDependency.add(userStory);

            }


            if (hasPreRequisit_hasDependency.size() > 1)
                prioritize_UserStories_HasDependency(hasPreRequisit_hasDependency);
            else if (hasPreRequisit_hasDependency.size() == 1) {
                sortedUserStoryCount += 1;
                sortedUserStoryMap.put(sortedUserStoryCount, hasPreRequisit_hasDependency.get(0));
            }


            if (hasPreRequisit_NoDependency.size() > 1)
                prioritize_UserStories_NoDependency(hasPreRequisit_NoDependency);
            else if (hasPreRequisit_NoDependency.size() == 1) {

                sortedUserStoryCount += 1;
                sortedUserStoryMap.put(sortedUserStoryCount, hasPreRequisit_NoDependency.get(0));
            }


            allPrerequisitdAllocatedList = new ArrayList<UserStory>();

        }


    }

    private void prioritize_UserStories_NoDependency(List<UserStory> userStories) {
        Map noDependency_WithTime = new HashMap<UserStory, Integer>();

        for (UserStory userStory : userStories) {
            int totoalTime = 0;

            Collection<Task> userStoryTasks = new ArrayList<Task>();
            userStoryTasks.addAll(userStoryDAO.getUserStoryTaskList(userStory));

            if (userStoryTasks.size() > 0)
                for (Task task : userStoryTasks) {
                    totoalTime = totoalTime + Integer.parseInt(task.getEstimateTime());
                }

            noDependency_WithTime.put(userStory, totoalTime);


        }

        noDependency_WithTime = PrioritizeUserStories.sortUserStoryMapDesendingOrder(noDependency_WithTime);

        Iterator it = noDependency_WithTime.entrySet().iterator();
        while (it.hasNext()) {
            {
                Map.Entry pair = (Map.Entry) it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue());
                sortedUserStoryCount += 1;
                sortedUserStoryMap.put(sortedUserStoryCount, pair.getKey());
                it.remove();

            }
        }

    }

    private void prioritize_UserStories_HasDependency(List<UserStory> userStories) {


        Map userStoryWithDependencyHighestPriority = new HashMap<UserStory, Integer>();
        Map userStoryWithHighestPriorityCount = new HashMap<UserStory, Integer>();


        //find highest prority and number of hights priority of each user story
        for (UserStory userStory : userStories) {

            int dependencyHighestPriority = 0;
            int highestPriorityCount = 0;

            String dependencyNameList = userStory.getDependancy();
            if (dependencyNameList != null && !dependencyNameList.isEmpty()) {
                String[] dependencies = userStory.getDependancy().split(",");

                //find the dependency highest  priority
                for (String dependencyName : dependencies) {
                    UserStory userStory1 = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), dependencyName);

                    if (dependencyHighestPriority == 0)
                        dependencyHighestPriority = userStory1.getPriority();
                    else if (userStory1.getPriority() < dependencyHighestPriority)
                        dependencyHighestPriority = userStory1.getPriority();
                }


                //find highest priority dependency user story count
                for (String dependencyName : dependencies) {
                    UserStory userStory1 = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), dependencyName);

                    if (userStory1.getPriority() == dependencyHighestPriority)
                        highestPriorityCount += 1;
                }

            }

            userStoryWithDependencyHighestPriority.put(userStory, dependencyHighestPriority);
            userStoryWithHighestPriorityCount.put(userStory, highestPriorityCount);


        }







        //for each highets prority level
        for (int x = 1; x <= 5; x++) {
            //this user temp userstory list contains user stories which contrains same highet dependency prority
            List<UserStory> tempUserStoryList = new ArrayList<UserStory>();
            Iterator iterator = userStoryWithDependencyHighestPriority.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();

                if ((Integer) pair.getValue() == x)
                    tempUserStoryList.add((UserStory) pair.getKey());

                //iterator.remove();
            }


            if (tempUserStoryList.size() == 1) {
                sortedUserStoryCount += 1;
                sortedUserStoryMap.put(sortedUserStoryCount, tempUserStoryList.get(0));
            } else {

                Map<UserStory, Integer> tempUserStoryWithHighestPriorityCount = new HashMap<UserStory, Integer>();

                for (UserStory userStory1 : tempUserStoryList) {
                    tempUserStoryWithHighestPriorityCount.put(userStory1, (Integer) userStoryWithHighestPriorityCount.get(userStory1));

                }

                //sort tempUserStoryWithHighestPriorityCount temp user story count map with desending order then maximum count come first
                tempUserStoryWithHighestPriorityCount = PrioritizeUserStories.sortUserStoryMapDesendingOrder(tempUserStoryWithHighestPriorityCount);


                //get same higrest prority count user storyes
                Multimap<Integer, UserStory> multiMap = HashMultimap.create();

                for (Map.Entry<UserStory, Integer> entry : tempUserStoryWithHighestPriorityCount.entrySet()) {
                    multiMap.put(entry.getValue(), entry.getKey());
                }

                for (Map.Entry<Integer, Collection<UserStory>> entry : multiMap.asMap().entrySet()) {
                    // System.out.println("Original value: " + entry.getKey() + " was mapped to keys: "
                    //         + entry.getValue());

                    Collection<UserStory> userStoryTasks = entry.getValue();

                    Map userStory_WithTotalHighestPriorityTaskTime = new HashMap<UserStory, Integer>();


                    //find total task time in each user story
                    for (UserStory userStory1 : userStoryTasks) {

                        int totalTaskTime = 0;

                        String dependencyNameList = userStory1.getDependancy();
                        if (dependencyNameList != null && !dependencyNameList.isEmpty()) {
                            String[] dependencies = userStory1.getDependancy().split(",");

                            for (String dependencyName : dependencies) {
                                UserStory userStory2 = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory1.getProject().getName(), dependencyName);
                                if (userStory2.getPriority() == userStoryWithDependencyHighestPriority.get(userStory1)) {
                                    Collection<Task> userStoryTasks2 = (userStoryDAO.getUserStoryTaskList(userStory2));

                                    for (Task task : userStoryTasks2) {
                                        totalTaskTime = totalTaskTime + Integer.parseInt(task.getEstimateTime());
                                    }
                                }

                            }

                            userStory_WithTotalHighestPriorityTaskTime.put(userStory1, totalTaskTime);

                        }

                    }


                    //accessing order sort task time for dependency
                    userStory_WithTotalHighestPriorityTaskTime = PrioritizeUserStories.sortUserStoryMapDesendingOrder(userStory_WithTotalHighestPriorityTaskTime);

                    Iterator it3 = userStory_WithTotalHighestPriorityTaskTime.entrySet().iterator();
                    while (it3.hasNext()) {
                        {
                            Map.Entry pair1 = (Map.Entry) it3.next();
                            //System.out.println(pair.getKey() + " = " + pair.getValue());
                            sortedUserStoryCount += 1;
                            sortedUserStoryMap.put(sortedUserStoryCount, pair1.getKey());
                            it3.remove();

                        }
                    }


                }


            }


        }




/*
        //this map contains userstories and their dependecies highest priority one
        Map withDependencyPriority = new HashMap<UserStory, Integer>();

        for (UserStory userStory : userStories) {

            int dependencyHighestPriority = 0;

            String dependencyNameList = userStory.getDependancy();
            if (dependencyNameList != null && !dependencyNameList.isEmpty()) {
                String[] dependencies = userStory.getDependancy().split(",");

                for (String dependencyName : dependencies) {
                    UserStory userStory1 = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), dependencyName);

                    if (dependencyHighestPriority == 0)
                        dependencyHighestPriority = userStory1.getPriority();
                    else if (userStory1.getPriority() < dependencyHighestPriority)
                        dependencyHighestPriority = userStory1.getPriority();
                }

            }

            withDependencyPriority.put(userStory, dependencyHighestPriority);
        }

        withDependencyPriority = PrioritizeUserStories.sortUserStoryMapAccendingOrder(withDependencyPriority);


        int priority = 1;
        //List<UserStory> UserSotyies_HasSameDependencyPriority = new ArrayList<UserStory>();
        Iterator it1 = withDependencyPriority.entrySet().iterator();


        Map userStory_WithTotalTaskTime = new HashMap<UserStory, Integer>();

        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry) it1.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            it1.remove(); // avoids a ConcurrentModificationException

            if ((Integer) pair.getValue() == priority) {
                // UserSotyies_HasSameDependencyPriority.add((UserStory)pair.getKey());

                int totalTaskTime = 0;


                Collection<Task> userStoryTasks = userStoryDAO.getUserStoryTaskList(((UserStory) pair.getKey()));

                for (Task task : userStoryTasks) {
                    totalTaskTime = totalTaskTime + Integer.parseInt(task.getEstimateTime());
                }
                userStory_WithTotalTaskTime.put(((UserStory) pair.getKey()), totalTaskTime);

            } else {
                userStory_WithTotalTaskTime = PrioritizeUserStories.sortUserStoryMapAccendingOrder(userStory_WithTotalTaskTime);

                Iterator it3 = userStory_WithTotalTaskTime.entrySet().iterator();
                while (it3.hasNext()) {
                    {
                        Map.Entry pair1 = (Map.Entry) it3.next();
                        //System.out.println(pair.getKey() + " = " + pair.getValue());
                        sortedUserStoryCount += 1;
                        sortedUserStoryMap.put(sortedUserStoryCount, pair1.getKey());
                        it3.remove();

                    }
                }


                int totalTaskTime = 0;
                Collection<Task> userStoryTasks = (userStoryDAO.getUserStoryTaskList((UserStory) pair.getKey()));

                for (Task task : userStoryTasks) {
                    totalTaskTime = totalTaskTime + Integer.parseInt(task.getEstimateTime());
                }

                userStory_WithTotalTaskTime = new HashMap<UserStory, Integer>();

                priority = (Integer) pair.getValue();
                userStory_WithTotalTaskTime.put(((UserStory) pair.getKey()), totalTaskTime);

            }

            if (it1.toString().isEmpty()) {
                userStory_WithTotalTaskTime = PrioritizeUserStories.sortUserStoryMapAccendingOrder(userStory_WithTotalTaskTime);

                Iterator it3 = userStory_WithTotalTaskTime.entrySet().iterator();
                while (it3.hasNext()) {
                    {
                        Map.Entry pair1 = (Map.Entry) it3.next();
                        //System.out.println(pair.getKey() + " = " + pair.getValue());
                        sortedUserStoryCount += 1;
                        sortedUserStoryMap.put(sortedUserStoryCount, pair1.getKey());
                        it3.remove();

                    }
                }


            }


        }*/


    }


    private boolean checkInSortedList(String userStoryName) {
        for (int x = 1; x <= sortedUserStoryCount; x++) {
            if (((UserStory) sortedUserStoryMap.get(x)).getName().equals(userStoryName))
                return true;
        }
        return false;
    }


    //1,2,3,4,5
    private static Map<UserStory, Integer> sortUserStoryMapAccendingOrder(Map<UserStory, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<UserStory, Integer>> list =
                new LinkedList<Map.Entry<UserStory, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<UserStory, Integer>>() {
            public int compare(Map.Entry<UserStory, Integer> o1,
                               Map.Entry<UserStory, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<UserStory, Integer> sortedMap = new LinkedHashMap<UserStory, Integer>();
        for (Iterator<Map.Entry<UserStory, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<UserStory, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

   // 99,98,97,96
    private static Map<UserStory, Integer> sortUserStoryMapDesendingOrder(Map<UserStory, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<UserStory, Integer>> list =
                new LinkedList<Map.Entry<UserStory, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<UserStory, Integer>>() {
            public int compare(Map.Entry<UserStory, Integer> o1,
                               Map.Entry<UserStory, Integer> o2) {
                //return (o1.getValue()).compareTo(o2.getValue());
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<UserStory, Integer> sortedMap = new LinkedHashMap<UserStory, Integer>();
        for (Iterator<Map.Entry<UserStory, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<UserStory, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


}





