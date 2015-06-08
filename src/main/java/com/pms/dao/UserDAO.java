package com.pms.dao;

import com.pms.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by Damitha on 6/7/2015.
 */
public class UserDAO {

    private SessionFactory sessionFactory;



    public void updateUser(User user)
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

















    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
