/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Unified Notification System
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED)
 */

package com.eurodyn.uns.dao.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.SpringApplicationContext;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseHibernateDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseHibernateDao.class);

    protected static Map sessionFactoryMap = new HashMap();

    private static SessionFactory sessionFactory = null;

    protected static ThreadLocal threadedSessions = new ThreadLocal();

    static {
        sessionFactory = (SessionFactory) SpringApplicationContext.getBean("sessionFactory");
        /*String configFileLocation = System.getProperty("hibernate-config-file");
        if (configFileLocation == null)
            sessionFactory = new Configuration().configure().buildSessionFactory();
        else{
            sessionFactory = new Configuration().configure(configFileLocation).buildSessionFactory();
        }
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage(), ex);
        }*/
        }

    /**
     * Return the specific Object class that will be used for class-specific
     * implementation of this DAO.
     */
    protected abstract Class getReferenceClass();

    protected Session getSession() throws HibernateException {
        return createSession();
    }

    public static Session createSession() throws HibernateException {
        Session session = (Session) threadedSessions.get();
        if (session == null) {
            session = sessionFactory.openSession();
            threadedSessions.set(session);
        }
        return session;
    }

    public void closeSession(Session session) throws HibernateException {
        if (session != null) {
            closeSession();
        }
    }

    public void closeSession() throws HibernateException {
        Session session = (Session) threadedSessions.get();
        threadedSessions.set(null);
        if (session != null) {
            session.close();
        }
    }

    public Transaction beginTransaction(Session session) throws HibernateException {
        return session.beginTransaction();
    }

    public void commitTransaction(Transaction t) throws HibernateException {
        t.commit();
    }

    public List findAll() throws HibernateException {
        Session session = null;
        try {
            session = getSession();
            return findAll(session);
        } finally {
            closeSession();
        }
    }

    public List findAll(Session s) throws HibernateException {
        return findAll(s, null, null);
    }

    public List findAll(Session s, String orderProperty, String order) throws HibernateException {
        Criteria crit = createCriteria(s);
        if (null != orderProperty) {
            if(order!=null && order.equalsIgnoreCase("desc")) {
                crit.addOrder(Order.desc(orderProperty));
            } else {
                crit.addOrder(Order.asc(orderProperty));
            }
        }
        return crit.list();
    }

    protected Criteria createCriteria(Session s) throws HibernateException {
        return s.createCriteria(getReferenceClass());
    }

    protected Serializable save(Object obj) throws HibernateException {
        Transaction t = null;
        Session sesson = null;
        try {
            sesson = getSession();
            t = beginTransaction(sesson);
            Serializable rtn = save(obj, sesson);
            commitTransaction(t);
            return rtn;
        } catch (HibernateException e) {
            if (null != t)
                t.rollback();
            throw e;
        } finally {
            closeSession();
        }
    }

    protected Serializable save(Object obj, Session sesson) throws HibernateException {
        return sesson.save(obj);
    }

    protected void delete(Object obj) throws HibernateException {
        Transaction t = null;
        Session s = null;
        try {
            s = getSession();
            t = beginTransaction(s);
            delete(obj, s);
            commitTransaction(t);
        } catch (HibernateException e) {
            if (null != t)
                t.rollback();
            throw e;
        } finally {
            closeSession();
        }
    }

    protected void delete(Object obj, Session s) throws HibernateException {
        s.delete(obj);
    }

    protected Object load(Class refClass, Serializable key) throws HibernateException {
        Session s = null;
        try {
            s = getSession();
            return load(refClass, key, s);
        } finally {
            closeSession();
        }
    }

    protected Object load(Class refClass, Serializable key, Session s) throws HibernateException {
        return s.load(refClass, key);
    }

    protected void saveOrUpdate(Object obj) throws HibernateException {
        Transaction t = null;
        Session s = null;
        try {
            s = getSession();
            t = beginTransaction(s);
            saveOrUpdate(obj, s);
            commitTransaction(t);
        }
        catch (HibernateException e) {
            if (null != t) t.rollback();
            throw e;
        }
        finally {
            closeSession();
        }
    }

    protected void saveOrUpdate(Object obj, Session s) throws HibernateException {
        s.saveOrUpdate(obj);
    }

}
