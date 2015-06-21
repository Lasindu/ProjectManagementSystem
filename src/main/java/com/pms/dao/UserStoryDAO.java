package com.pms.dao;

import com.pms.domain.Project;
import com.pms.domain.User;
import com.pms.domain.UserStory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collection;
import java.util.List;

/**
 * Created by Upulie on 4/19/2015.
 */
public class UserStoryDAO {

    private SessionFactory sessionFactory;


    public void saveNewUserStory(UserStory userStory)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(userStory);
        session.beginTransaction().commit();
        session.close();

    }

    public void removeUserStory(UserStory userStory)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(userStory);
        session.getTransaction().commit();
        session.close();

    }

    public UserStory getUserStoryFormProjectNameAndUserStoryName(String projectName,String userStoryName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Project as project  where project.name='" + projectName + "'";
        Query query = session.createQuery(HQL_QUERY);
        List<Project> list = ((org.hibernate.Query) query).list();



        if(list.size()>0)
        {
            int x =list.get(0).getProjectUserStories().size();

            Collection<UserStory> projectUserStories=list.get(0).getProjectUserStories();

            for(UserStory userStory:projectUserStories)
            {
                if(userStory.getName().equals(userStoryName))
                {
                    int y =userStory.getUserStoryTasks().size();
                    session.close();
                    return userStory;
                }


            }
            session.close();
            return null;
        }

        else
            return null;
    }

    public void updateUserStory(UserStory userStory)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(userStory);
        session.getTransaction().commit();
        session.close();


    }


    public Collection<UserStory> getAllUserSeriesOfProject(Project project)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        Project project1=(Project)session.get(Project.class,project.getProjectId());
        int x= project1.getProjectUserStories().size();
        session.getTransaction().commit();
        session.close();

        if(x>0)
            return project1.getProjectUserStories();
        else
        return null;
    }








    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
