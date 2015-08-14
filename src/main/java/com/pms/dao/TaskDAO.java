package com.pms.dao;

import com.pms.domain.Project;
import com.pms.domain.Task;
import com.pms.domain.UserStory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

/**
 * Created by Upulie on 6/2/2015.
 */
public class TaskDAO {

    private SessionFactory sessionFactory;



    public Task getTaskByTaskId(int id)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Task as task  where task.id=id ";
        Query query = session.createQuery(HQL_QUERY);
        List<Task> list = ((org.hibernate.Query) query).list();

        if(list.size()>0)
        {
            return list.get(0);
        }
        return null;

    }

    public void removeTask(Task task)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(task);
        session.getTransaction().commit();
        session.close();

    }

    public void updateTask(Task task)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(task);
        session.getTransaction().commit();
        session.close();
    }


    public Task getTaskFromUserStroyNameAndTaskName(String userStoryName,String taskName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from UserStory as userStory  where userStory.name='" + userStoryName + "'";
        Query query = session.createQuery(HQL_QUERY);
        List<UserStory> list = ((org.hibernate.Query) query).list();

        if(list.size()>0) {

            Collection<Task> userStoryTasks=list.get(0).getUserStoryTasks();

            for(Task task:userStoryTasks)
            {
                if (task.getName().equals(taskName))
                {
                    session.close();
                    return task;

                }

            }
            return null;
        }

        return null;
    }
















    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


}
