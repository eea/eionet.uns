package com.eurodyn.uns.dao.hibernate;

import org.hibernate.HibernateException;
import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IDeliveryDao;
import com.eurodyn.uns.model.Delivery;

public class HibernateDeliveryDao extends BaseHibernateDao implements IDeliveryDao {

    protected Class getReferenceClass() {
        return com.eurodyn.uns.model.Delivery.class;
    }


    public void updateDelivery(Delivery delivery) throws DAOException {
        try {
            saveOrUpdate(delivery);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    public void createDelivery(Delivery delivery) throws DAOException {
        try {
            save(delivery);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

}
