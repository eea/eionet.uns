package com.eurodyn.uns.dao.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IDeliveryTypeDao;
import com.eurodyn.uns.model.DeliveryType;

public class HibernateDeliveryTypeDao extends BaseHibernateDao implements IDeliveryTypeDao {

	public List findAllDeliveryTypes() throws DAOException {
		List result = null;
		try {
			result = findAll();
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
		return result;
	}

	public List findAllDeliveryTypes(String orderProperty, String order) throws DAOException {
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

	public DeliveryType findByPK(Integer deliveryTypeId) throws DAOException {
		DeliveryType result = null;
		try {
			result = (DeliveryType) load(getReferenceClass(), deliveryTypeId);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
		return result;
	}

	protected Class getReferenceClass() {
		return com.eurodyn.uns.model.DeliveryType.class;
	}

	public void updateDeliveryType(DeliveryType dt) throws DAOException {
		try {
			saveOrUpdate(dt);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public void createDeliveryType(DeliveryType dt) throws DAOException {
		try {
			save(dt);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public void deleteDeliveryType(DeliveryType dt) throws DAOException {
		try {
			delete(dt);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public DeliveryType findByName(String name) throws DAOException {
		Session session = null;
		DeliveryType dt = null;

		try {
			session = getSession();
			Query query = session.createQuery("select dt from DeliveryType as dt where dt.name=:name");
			query.setString("name", name);
			List list = query.list();
			if (list.size() > 0) {
				dt = (DeliveryType) list.get(0);
			}
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
		return dt;
	}

}
