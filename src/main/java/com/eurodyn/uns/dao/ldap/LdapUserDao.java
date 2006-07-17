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

package com.eurodyn.uns.dao.ldap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IUserDao;
import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.User;

public class LdapUserDao extends BaseLdapDao implements IUserDao {

	protected String usersDn;

	protected String uidAttribute;

	public LdapUserDao() {
		usersDn = conf.getString("ldap.user.dir") + "," + baseDn;
		uidAttribute = conf.getString("ldap.attr.uid");
	}

	public User findUser(String username) throws DAOException {
		return null;
	}

	public User findUser(Integer id) throws DAOException {
		return null;
	}

	public List findAllUsers() throws DAOException {
		return null;
	}

	public void createUser(User user) throws DAOException {
	}

	public void updateUser(User user) throws DAOException {
	}

	public void fillUserAttributes(User user) {

		try {
			DirContext ctx = getDirContext();
			String userDn = uidAttribute + "=" + user.getExternalId() + "," + usersDn;
			NamingEnumeration results = ctx.getAttributes(userDn, new String[] { "mail", "cn" }).getAll();
			while (results != null && results.hasMore()) {
				Attribute attr = (Attribute) results.next();
				if (attr.getID().equals("mail")) {
					DeliveryAddress da = new DeliveryAddress();
					da.setDeliveryType(new DeliveryType(new Integer(1)));
					da.setAddress((String) attr.get());
					Map addresses = user.getDeliveryAddresses();
					if (addresses == null)
						addresses = new HashMap(1);
					addresses.put(new Integer(1), da);
					user.setDeliveryAddresses(addresses);
				}
				if (attr.getID().equals("cn")) {
					user.setFullName((String) (attr.get()));
				}

			}
		} catch (NamingException ne) {
			// ignore it is probably local user
		}

	}

}
