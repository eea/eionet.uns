package com.eurodyn.uns.dao.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.INotificationTemplateDao;
import com.eurodyn.uns.model.NotificationTemplate;

public class HibernateNotificationTemplateDao extends BaseHibernateDao implements INotificationTemplateDao {

    public List findAllNotificationTemplates() throws DAOException {
        List result = null;
        try {
            result = findAll();
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
        return result;
    }

    public List findAllNotificationTemplates(String orderProperty, String order) throws DAOException {
        List result = null;
        Session session = null;
        try {
            session = getSession();
            result = findAll(session, orderProperty, order);
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
        return result;
    }

    
    public List findNotificationTemplatesForAssigment() throws DAOException {
        List result = null;
        Session session = null;
        try {
            session = getSession();
            Query query = session.createQuery("select nt from NotificationTemplate as nt where nt.id not in (2,3)");
            result = query.list();
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
        return result;
    }

    
    public NotificationTemplate findByPK(Integer metadataElementId) throws DAOException {
        NotificationTemplate result = null;
        try {
            result = (NotificationTemplate) load(getReferenceClass(), metadataElementId);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
        return result;
    }

    protected Class getReferenceClass() {
        return com.eurodyn.uns.model.NotificationTemplate.class;
    }

    public void updateNotificationTemplate(NotificationTemplate nt) throws DAOException {
        try {
            saveOrUpdate(nt);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    public void createNotificationTemplate(NotificationTemplate nt) throws DAOException {
        try {
            save(nt);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    public boolean deleteNotificationTemplate(NotificationTemplate nt) throws DAOException {
        try {
            delete(nt);
        } catch (ConstraintViolationException ce) {
            return false;
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
        return true;
    }

}
