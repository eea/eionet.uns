package com.eurodyn.uns.dao.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IMetadataElementDao;
import com.eurodyn.uns.model.MetadataElement;

public class HibernateMetadataElementDao extends BaseHibernateDao implements IMetadataElementDao {

	public List findAllMetadataElements() throws DAOException {
		List result = null;
		try {
			result = findAll();
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
		return result;
	}

	public List findAllMetadataElements(String orderProperty, String order) throws DAOException {
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

	public MetadataElement findByPK(Integer metadataElementId) throws DAOException {
		MetadataElement result = null;
		try {
			result = (MetadataElement) load(getReferenceClass(), metadataElementId);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
		return result;
	}

	protected Class getReferenceClass() {
		return com.eurodyn.uns.model.MetadataElement.class;
	}

	public void updateMetadataElement(MetadataElement me) throws DAOException {
		try {
			saveOrUpdate(me);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public void createMetadataElement(MetadataElement me) throws DAOException {
		try {
			save(me);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public void deleteMetadataElement(MetadataElement me) throws DAOException {
		try {
			delete(me);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public MetadataElement findByName(String name) throws DAOException {
		Session session = null;
		MetadataElement me = null;

		try {
			session = getSession();
			Query query = session.createQuery("select me from MetadataElement as me where me.name=:name");
			query.setString("name", name);
			List list = query.list();
			if (list.size() > 0) {
				me = (MetadataElement) list.get(0);
			}
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
		return me;
	}

}
