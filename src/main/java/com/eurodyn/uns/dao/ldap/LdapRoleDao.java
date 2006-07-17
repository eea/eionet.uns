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

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IRoleDao;
import com.eurodyn.uns.model.Role;

public class LdapRoleDao extends BaseLdapDao implements IRoleDao {

	private String rolesDn;
	private String usersDn;
	
	//cn=Manager,o=EIONET,l=Europe 
	//secret

	public LdapRoleDao() {
		rolesDn = conf.getString("ldap.role.dir") + "," + baseDn;
		usersDn = conf.getString("ldap.user.dir") + "," + baseDn;
	}

	public List findAllRoles() throws DAOException {
		List result = new ArrayList(1);
		try {
            DirContext ctx=getDirContext();
			String myFilter = "objectclass=groupOfUniqueNames";
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration results = ctx.search(rolesDn, myFilter, sc);
			while (results != null && results.hasMore()) {
				SearchResult sr = (SearchResult) results.next();
				String dn = sr.getName();
				if(dn!=null && dn.length()>0){
					Role r = new Role();
					r.setFullDn(dn + "," + baseDn);
					r.setName(dn);
					result.add(r);
				}
			}
		} catch (NamingException e) {
			throw new DAOException(e);
		}
		return result;
	}

	public Role findByPK(Integer id) throws DAOException {
		return null;
	}

	public void updateRoles(List roles) throws DAOException {
	}

	public List findUserRoles(String user) throws DAOException {
		List result = new ArrayList();
		try {
            DirContext ctx=getDirContext();
			String myFilter = "(&(objectClass=groupOfUniqueNames)(uniqueMember=uid=" + user + "," + usersDn + "))";
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration results = ctx.search(rolesDn, myFilter, sc);
			while (results != null && results.hasMore()) {
				SearchResult sr = (SearchResult) results.next();
				String dn = sr.getName();
				if(dn!=null && dn.length()>0){
					Role r = new Role();
					r.setFullDn(dn + "," + baseDn);
					r.setName(dn);
					result.add(r);
				}
			}
		} catch (NamingException e) {
			throw new DAOException(e);
		}
		return result;
	}

}
