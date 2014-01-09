package com.eurodyn.uns.dao.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IStylesheetDao;
import com.eurodyn.uns.model.Stylesheet;

public class HibernateStylesheetDao extends BaseHibernateDao implements IStylesheetDao {

    public List findAllStylesheets() throws DAOException {
        List result=null;
        try {
            result=findAll();
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
        return result;
    }

    public List findAllStylesheets(String orderProperty, String order) throws DAOException {
        List result=null;
        Session session=null;
        try {
            session=getSession();
            result=findAll(session, orderProperty, order);
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
        return result;
    }

    public Stylesheet findByPK(Integer stylesheetId) throws DAOException {
        Stylesheet result=null;
        try {
            result= (Stylesheet) load(getReferenceClass(),stylesheetId);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
        return result;
    }

    protected Class getReferenceClass() {
        return com.eurodyn.uns.model.Stylesheet.class;
    }

    public void updateStylesheet(Stylesheet xsl) throws DAOException {
        try {
            saveOrUpdate(xsl);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    public void createStylesheet(Stylesheet xsl) throws DAOException {
        try {
            save(xsl);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    public void deleteStylesheetl(Stylesheet xsl) throws DAOException {
        try {
            delete(xsl);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

}
