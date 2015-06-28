package com.pms.component.ganttchart;

import com.pms.DashboardUI;
import com.pms.dao.UserStoryDAO;
import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;

import java.util.*;

/**
 * Created by Damitha on 6/28/2015.
 */
public class PrioritizeUserStories {

    Collection<UserStory> allUserStories;
    Map sortedUserStoryMap;
    int userStoryCount;
    int sortedUserStoryCount;
    UserStoryDAO userStoryDAO;

    public Map prioritize( Project project )
    {
        sortedUserStoryMap = new HashMap<Integer,UserStory>();
        sortedUserStoryCount=0;


        userStoryDAO= (UserStoryDAO) DashboardUI.context.getBean("UserStory");
        allUserStories=  new ArrayList<UserStory>();
        if (userStoryDAO.getAllUserSeriesOfProject(project)!=null)
        allUserStories.addAll(userStoryDAO.getAllUserSeriesOfProject(project));
        userStoryCount=allUserStories.size();




        List<UserStory> priority1UserStories = new ArrayList<UserStory>();
        List<UserStory> priority2UserStories = new ArrayList<UserStory>();
        List<UserStory> priority3UserStories = new ArrayList<UserStory>();
        List<UserStory> priority4UserStories = new ArrayList<UserStory>();
        List<UserStory> priority5UserStories = new ArrayList<UserStory>();


        for(UserStory userStory:allUserStories)
        {
            if(userStory.getPriority()==1)
            {
                priority1UserStories.add(userStory);
            }

            if(userStory.getPriority()==2)
            {
                priority2UserStories.add(userStory);
            }

            if(userStory.getPriority()==3)
            {
                priority3UserStories.add(userStory);
            }

            if(userStory.getPriority()==4)
            {
                priority4UserStories.add(userStory);
            }

            if(userStory.getPriority()==5)
            {
                priority5UserStories.add(userStory);
            }
        }


        if (priority1UserStories.size()>1)
            userStoriesCheckPreRequistCheckDependency(priority1UserStories);
        else if (priority1UserStories.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,priority1UserStories.get(0));
        }

        if(priority2UserStories.size()>1)
            userStoriesCheckPreRequistCheckDependency(priority2UserStories);
        else if(priority2UserStories.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,priority2UserStories.get(0));
        }

        if(priority3UserStories.size()>1)
         userStoriesCheckPreRequistCheckDependency(priority3UserStories);
        else if (priority3UserStories.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,priority3UserStories.get(0));

        }


        if(priority4UserStories.size()>1)
           userStoriesCheckPreRequistCheckDependency(priority4UserStories);
        else if (priority4UserStories.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,priority4UserStories.get(0));

        }

        if (priority5UserStories.size()>1)
             userStoriesCheckPreRequistCheckDependency(priority5UserStories);
        else if (priority5UserStories.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,priority5UserStories.get(0));

        }




        return sortedUserStoryMap;
    }

    private void userStoriesCheckPreRequistCheckDependency( List<UserStory> userStories)
    {
        List<UserStory> noPreRequist = new ArrayList<UserStory>();
        List<UserStory> hasPreRequist = new ArrayList<UserStory>();

        for(UserStory userStory:userStories)
        {
            if(userStory.getPreRequisits().isEmpty())
                noPreRequist.add(userStory);
            else
                hasPreRequist.add(userStory);

        }



        List<UserStory> noPreRequisit_NoDependency = new ArrayList<UserStory>();
        List<UserStory> noPreRequisit_hasDependency = new ArrayList<UserStory>();

        for(UserStory userStory:noPreRequist)
        {
            if(userStory.getDependancy().isEmpty())
                noPreRequisit_NoDependency.add(userStory);
            else
                noPreRequisit_hasDependency.add(userStory);

        }



        if(noPreRequisit_hasDependency.size()>1)
            sort_UserStories_HasDependency(noPreRequisit_hasDependency);
        else if (noPreRequisit_hasDependency.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,noPreRequisit_hasDependency.get(0));
        }

        if(noPreRequisit_NoDependency.size()>1)
            sort_UserStories_NoDependency(noPreRequisit_NoDependency);
        else if (noPreRequisit_NoDependency.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,noPreRequisit_NoDependency.get(0));

        }

        if(hasPreRequist.size()>1)
            sort_UserStories_HasPreReqisits(hasPreRequist);
        else if (hasPreRequist.size()==1)
        {
            sortedUserStoryCount+=1;
            sortedUserStoryMap.put(sortedUserStoryCount,hasPreRequist.get(0));

        }



    }



    private void sort_UserStories_HasPreReqisits(List<UserStory> userStories)
    {

        List<UserStory> allPrerequisitdAllocatedList= new ArrayList<UserStory>();

        int newSortedCount=sortedUserStoryCount+userStories.size();

        while (sortedUserStoryCount!=newSortedCount)
        {

            System.out.println("inside has prerequist while loop   allocated user story count"+sortedUserStoryCount);

            for(UserStory userStory:userStories)
            {

                if(checkInSortedList(userStory.getName()))
                    break;


                String[]  preReqistiList= userStory.getPreRequisits().split(",");


                boolean allallocated=true;
                for(String preRequist:preReqistiList)
                {
                    UserStory userStory1=userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), preRequist);

                   // if(!userStory1.getState().equals("allocated"))
                    if(!checkInSortedList(userStory1.getName()))
                    {
                        allallocated=false;
                        break;
                    }

                }

                if (allallocated)
                {
                    allPrerequisitdAllocatedList.add(userStory);

                }

            }

            List<UserStory> hasPreRequisit_NoDependency = new ArrayList<UserStory>();
            List<UserStory> hasPreRequisit_hasDependency = new ArrayList<UserStory>();
            for(UserStory userStory:allPrerequisitdAllocatedList)
            {
                if(userStory.getDependancy().isEmpty())
                    hasPreRequisit_NoDependency.add(userStory);
                else
                    hasPreRequisit_hasDependency.add(userStory);

            }


            if (hasPreRequisit_hasDependency.size()>1)
                sort_UserStories_HasDependency(hasPreRequisit_hasDependency);
            else if (hasPreRequisit_hasDependency.size()==1)
            {
                sortedUserStoryCount+=1;
                sortedUserStoryMap.put(sortedUserStoryCount,hasPreRequisit_hasDependency.get(0));
            }


            if (hasPreRequisit_NoDependency.size()>1)
                sort_UserStories_NoDependency(hasPreRequisit_NoDependency);
            else if (hasPreRequisit_NoDependency.size()==1)
            {

                sortedUserStoryCount+=1;
                sortedUserStoryMap.put(sortedUserStoryCount,hasPreRequisit_NoDependency.get(0));
            }




            allPrerequisitdAllocatedList= new ArrayList<UserStory>();

        }




    }

    private void sort_UserStories_NoDependency(List<UserStory> userStories)
    {
        Map noDependency_WithTime = new HashMap<UserStory,Integer>();

        for(UserStory userStory:userStories)
        {
            int totoalTime=0;

            Collection<Task> userStoryTasks= new ArrayList<Task>();
            userStoryTasks.addAll(userStoryDAO.getUserStoryTaskList(userStory));

            if (userStoryTasks.size()>0)
            for(Task task:userStoryTasks)
            {
                totoalTime=totoalTime+Integer.parseInt(task.getEstimateTime());
            }

            noDependency_WithTime.put(userStory,totoalTime);



        }

        noDependency_WithTime=PrioritizeUserStories.sortUserStory(noDependency_WithTime);

        Iterator it = noDependency_WithTime.entrySet().iterator();
        while (it.hasNext()) {
            {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue());
                sortedUserStoryCount+=1;
                sortedUserStoryMap.put(sortedUserStoryCount,pair.getKey());
                it.remove();

            }
        }

    }

    private void sort_UserStories_HasDependency(List<UserStory> userStories)
    {
        Map withDependencyPriority = new HashMap<UserStory,Integer>();

        for(UserStory userStory:userStories) {

            int highestPriority = 0;

            String[] dependencies = userStory.getDependancy().split(",");

            for (String dependency : dependencies) {
                UserStory userStory1 = userStoryDAO.getUserStoryFormProjectNameAndUserStoryName(userStory.getProject().getName(), dependency);
                if (userStory1.getPriority() > highestPriority)
                    highestPriority = userStory1.getPriority();
            }
            withDependencyPriority.put(userStory, highestPriority);
        }

        withDependencyPriority=PrioritizeUserStories.sortUserStory(withDependencyPriority);




        int priority=1;
        List<UserStory> UserSotyies_HasSameDependencyPriority = new ArrayList<UserStory>();
        Iterator it1 = withDependencyPriority.entrySet().iterator();


        Map userStory_WithTotalTaskTime = new HashMap<UserStory,Integer>();

        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry)it1.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            it1.remove(); // avoids a ConcurrentModificationException

            if((Integer)pair.getValue()==priority)
            {
                UserSotyies_HasSameDependencyPriority.add((UserStory)pair.getKey());

                int totalTaskTime=0;


                Collection<Task> userStoryTasks=((UserStory)pair.getKey()).getUserStoryTasks();

                for(Task task:userStoryTasks)
                {
                    totalTaskTime=totalTaskTime+Integer.parseInt(task.getEstimateTime());
                }
                userStory_WithTotalTaskTime.put(((UserStory)pair.getKey()),totalTaskTime);

            }

            else if(it1.toString().isEmpty())
            {
                userStory_WithTotalTaskTime=PrioritizeUserStories.sortUserStory(userStory_WithTotalTaskTime);

                Iterator it3 = userStory_WithTotalTaskTime.entrySet().iterator();
                while (it3.hasNext()) {
                    {
                        Map.Entry pair1 = (Map.Entry)it3.next();
                        //System.out.println(pair.getKey() + " = " + pair.getValue());
                        sortedUserStoryCount+=1;
                        sortedUserStoryMap.put(sortedUserStoryCount,pair1.getKey());
                        it3.remove();

                    }
                }


            }

            else
            {
                userStory_WithTotalTaskTime=PrioritizeUserStories.sortUserStory(userStory_WithTotalTaskTime);

                Iterator it3 = userStory_WithTotalTaskTime.entrySet().iterator();
                while (it3.hasNext()) {
                    {
                        Map.Entry pair1 = (Map.Entry)it3.next();
                        //System.out.println(pair.getKey() + " = " + pair.getValue());
                        sortedUserStoryCount+=1;
                        sortedUserStoryMap.put(sortedUserStoryCount,pair1.getKey());
                        it3.remove();

                    }
                }




                int totalTaskTime=0;
                Collection<Task> userStoryTasks=(userStoryDAO.getUserStoryTaskList((UserStory)pair.getKey()));

                for(Task task:userStoryTasks)
                {
                    totalTaskTime=totalTaskTime+Integer.parseInt(task.getEstimateTime());
                }

                userStory_WithTotalTaskTime=new HashMap<UserStory,Integer>();
                priority=(Integer)pair.getValue();
                userStory_WithTotalTaskTime.put(((UserStory)pair.getKey()),totalTaskTime);

            }


        }

    }



    private boolean checkInSortedList(String userStoryName)
    {
        for(int x=1;x<=sortedUserStoryCount;x++)
        {
            if(((UserStory)sortedUserStoryMap.get(x)).getName().equals(userStoryName))
                return true;
        }
        return false;
    }







    private static Map<UserStory, Integer> sortUserStory(Map<UserStory, Integer> unsortMap) {

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
        for (Iterator<Map.Entry<UserStory, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<UserStory, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }




}
