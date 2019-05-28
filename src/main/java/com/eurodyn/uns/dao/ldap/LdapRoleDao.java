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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import com.eurodyn.uns.Properties;
import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IRoleDao;
import com.eurodyn.uns.model.Role;

public class LdapRoleDao extends BaseLdapDao implements IRoleDao {

    private String rolesDn;
    private String usersDn;

    //cn=Manager,o=EIONET,l=Europe
    //secret

    public LdapRoleDao() {
        rolesDn = Properties.getStringProperty("ldap.role.dir") + "," + baseDn;
        usersDn = Properties.getStringProperty("ldap.user.dir") + "," + baseDn;
    }

    public List findAllRoles() throws DAOException {
        List result = new ArrayList(1);
        LdapContext ctx = null;
        try {
            ctx = getPagedLdapContext();
            String myFilter = "objectclass=groupOfUniqueNames";
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            sc.setCountLimit(0);
            sc.setTimeLimit(0);
            sc.setReturningObjFlag(true);

            byte[] cookie = null;
            int total;
            do {
            NamingEnumeration results = ctx.search(rolesDn, myFilter, sc);
            while (results != null && results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                String dn = sr.getName();
                if (dn != null && dn.length() > 0) {
                    Role r = new Role();
                    r.setFullDn(dn + "," + baseDn);
                    r.setName(dn);
                    result.add(r);
                }
            }
            Control[] controls = ctx.getResponseControls();
            if (controls != null) {
                for (int i = 0; i < controls.length; i++) {
                    if (controls[i] instanceof PagedResultsResponseControl) {
                        PagedResultsResponseControl prrc =
                                (PagedResultsResponseControl) controls[i];
                        total = prrc.getResultSize();
                        cookie = prrc.getCookie();
                    }
                }
            }
            // Re-activate paged results
            ctx.setRequestControls(new Control[]{
                    new PagedResultsControl(PAGE_SIZE, cookie, Control.CRITICAL)});
            } while (cookie != null);
        } catch (NamingException e) {
            throw new DAOException(e);
        } catch (IOException e) {
            throw new DAOException("Error: " + e);
        } finally {
            closeContext(ctx);
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
        DirContext ctx = null;
        try {
            ctx = getDirContext();
            String myFilter = "(&(objectClass=groupOfUniqueNames)(uniqueMember=uid=" + user + "," + usersDn + "))";
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration results = ctx.search(rolesDn, myFilter, sc);
            while (results != null && results.hasMore()) {
                SearchResult sr = (SearchResult) results.next();
                String dn = sr.getName();
                if (dn != null && dn.length() > 0){
                    Role r = new Role();
                    r.setFullDn(dn + "," + baseDn);
                    r.setName(dn);
                    result.add(r);
                }
            }
        } catch (NamingException e) {
            throw new DAOException(e);
        } finally {
            closeContext(ctx);
        }
        return result;
    }

}
