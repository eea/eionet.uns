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
 *                          Dusan Popovic (ED) 
 */

package com.eurodyn.uns.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IUserDao;
import com.eurodyn.uns.model.User;

public class HibernateUserDao extends BaseHibernateDao implements IUserDao {

	protected Class getReferenceClass() {
		return com.eurodyn.uns.model.User.class;
	}

	public List findAllUsers() throws DAOException {
		return findAll();
	}

	public User findUser(Integer id) throws DAOException {
		User user = null;
		Session session = null;
		try {
			session = getSession();			
			user = (User) session.load(getReferenceClass(), id);
			Hibernate.initialize(user.getDeliveryAddresses());			
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
		return user;
	}

	public User findUser(String username) throws DAOException {
		Session session = null;
		User user = null;

		try {
			session = getSession();
			Query query = session.createQuery("select u from User as u where u.externalId=:username");
			query.setString("username", username);
			List list = query.list();
			if (list.size() > 0) {
				user = (User) list.get(0);
				Hibernate.initialize(user.getSubscriptions());
				Hibernate.initialize(user.getDeliveryAddresses());
			}
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
		return user;
	}

	public void createUser(User user) throws DAOException {
		try {
			save(user);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	public void updateUser(User user) throws DAOException {
		try {
			saveOrUpdate(user);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	public void fillUserAttributes(User user) throws DAOException {
	}
}
