package com.eurodyn.uns.dao.hibernate;

import java.util.List;

import org.hibernate.HibernateException;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IRoleDao;
import com.eurodyn.uns.model.Role;

public class HibernateRoleDao extends BaseHibernateDao implements IRoleDao {

    public List findAllRoles() throws DAOException {
        List result=null;
        try {
            result=findAll();
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
        return result;
    }

	public Role findByPK(Integer id) throws DAOException {
        Role result=null;
        try {
            result= (Role) load(getReferenceClass(), id);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
        return result;
	}

	public void updateRoles(List roles) throws DAOException {
        try {
			for (int i = 0; i < roles.size(); i++) {
				Role role = (Role)roles.get(i);
				saveOrUpdate(role);
			}
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
	}

	protected Class getReferenceClass() {
        return com.eurodyn.uns.model.Role.class;
    }

	public List findUserRoles(String user) throws DAOException {
		return null;
	}

}
