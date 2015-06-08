package com.pms.dao;

import com.pms.domain.Project;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.pms.domain.User;

import java.util.List;

/**
 * Created by Upulie on 4/2/2015.
 */
public class LoginDAO {

    private SessionFactory sessionFactory;


    public User authenticateUser(String userName,String password)
    {


        //original code
        Session session = getSessionFactory().openSession();
        System.out.println("User Name   "+userName);
        //List<User> usersList=session.createQuery(" from User u where u.userName=? and u.password=? ").setParameter(0,userName).setParameter(1, password).list();
        List<User> usersList=session.createQuery(" from User u where u.userName=? ").setParameter(0,userName).list();

        //following code for retrieve project users of project otherwise will give error when try to delete projects
        if(usersList.size()>0)
        {
            //int x = usersList.get(0).getProjects().size();

 /*           for(Project project: usersList.get(0).getProjects())
            {
                int y=project.getUsers().size();
            }*/
            session.close();
            return usersList.get(0);

        }
        session.close();



/*        Session session = getSessionFactory().openSession();
        System.out.println("User Name   "+userName);
        //List<User> usersList=session.createQuery(" from User u where u.userName=? and u.password=? ").setParameter(0,userName).setParameter(1, password).list();
        List<User> usersList=session.createQuery(" from User u ").list();
        if(usersList.size()>0)
        {
            return usersList.get(0);

        }*/


        return null;
    }










    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
