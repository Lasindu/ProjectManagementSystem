package com.pms.dao;

import com.pms.domain.Project;
import com.pms.domain.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Created by Upulie on 4/17/2015.
 */
public class ProjectDAO {

    private SessionFactory sessionFactory;


    public void saveNewProject(Project project)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(project);
        session.beginTransaction().commit();
        session.close();
    }

    public void removeProject(Project project)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(project);
        session.getTransaction().commit();
        session.close();

    }

    public Project loadProjectUsers(Project project)
    {
        Project project1;
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        project1=(Project)session.get(Project.class,project.getProjectId());
        int x=project1.getUsers().size();
        for(User user: project1.getUsers())
        {
            int y=user.getProjects().size();
        }
        session.getTransaction().commit();
        session.close();
        return project1;

    }


    public List<Project> getAllProjects()
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Project";
        Query query = session.createQuery(HQL_QUERY);
        List<Project> list = ((org.hibernate.Query) query).list();
        session.close();

        return list;
    }


    public void updateProject(Project project)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(project);
        session.getTransaction().commit();
        session.close();

    }


    public Project getProjectFromProjectName(String projectName)
    {
        Session session = getSessionFactory().openSession();
        String HQL_QUERY = "from Project as project  where project.name='" + projectName + "'";
        Query query = session.createQuery(HQL_QUERY);
        List<Project> list = ((org.hibernate.Query) query).list();


        if(list.size()>0)
        {
            int x =list.get(0).getProjectUserStories().size();
            session.close();
            return list.get(0);
        }
        else
        {
            session.close();
            return null;
        }


    }









    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }




}
