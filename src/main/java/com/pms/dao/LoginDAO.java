package com.pms.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.pms.domain.User;

import java.util.List;

/**
 * Created by Damitha on 4/2/2015.
 */
public class LoginDAO {


    private SessionFactory sessionFactory;

    public User authenticateUser(String userName,String password)
    {
        Session session = getSessionFactory().openSession();
        System.out.println("User Name   "+userName);
        List<User> usersList=session.createQuery(" from User u where u.userName=? and u.password=? ").setParameter(0,userName).setParameter(1, password).list();
        if(usersList.size()>0)
        {
            return usersList.get(0);

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
