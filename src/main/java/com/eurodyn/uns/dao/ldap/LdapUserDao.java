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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.*;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.IUserDao;

import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.daemons.userupdater.UserUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapUserDao extends BaseLdapDao implements IUserDao {

    private static Log logger = LogFactory.getLog(LdapUserDao.class);

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

    public void updateUsers() throws IOException, NamingException, DAOException {
        LdapContext ctx = getPagedLdapContext();
        usersDn = conf.getString("ldap.user.dir") + "," + baseDn;
        uidAttribute = conf.getString("ldap.attr.uid");
        int pageSize = 50;
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setCountLimit(1000);
        ctls.setTimeLimit(10000);
        ctls.setReturningObjFlag(true);
        byte[] cookie = null;
        int total;
        if (ctx != null) {
            do {
                NamingEnumeration results = ctx.search(usersDn, "(objectClass=*)", ctls);
                while (results != null && results.hasMore()) {
                    SearchResult result = (SearchResult) results.next();
                    // Get Attributes and add to user object
                    Attributes attrs = result.getAttributes();
                    Attribute fullnameAttr = attrs.get("cn");
                    Attribute employeeType = attrs.get("employeeType");
                    Attribute mailAttr = attrs.get("mail");
                    Attribute usernameAttr = attrs.get("uid");

                    String fullname = "";
                    if (fullnameAttr != null && fullnameAttr.get() != null) {
                         fullname = (String) fullnameAttr.get();
                    }

                    boolean disabledFlag = false;
                    if (employeeType != null && employeeType.get().equals("disabled")) {
                        disabledFlag = true;
                    }
                    String mail = "";
                    if (mailAttr != null && mailAttr.get() != null) {
                        mail = (String) mailAttr.get();
                    }
                    String username = "";
                    if (usernameAttr != null && usernameAttr.get() != null) {
                        username = (String) usernameAttr.get();
                    };

                    DAOFactory hibernateFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
                    IUserDao dao = hibernateFactory.getUserDao();
                    User user = dao.findUser(username);
                    if (user != null) {
                        User ldapUser = new User();
                        ldapUser.setFullName(fullname);
                        ldapUser.setDisabledFlag(disabledFlag);

                        if (mail != null) {
                            DeliveryAddress da = new DeliveryAddress();
                            da.setDeliveryType(new DeliveryType(new Integer(1), "EMAIL"));
                            da.setAddress(mail);
                            Map addresses = ldapUser.getDeliveryAddresses();
                            if (addresses == null)
                                addresses = new HashMap(1);
                            addresses.put(new Integer(1), da);
                            ldapUser.setDeliveryAddresses(addresses);
                        }

                        if (!UserUtil.equalUsers(user, ldapUser)) {
                            user.setFullName(fullname);
                            if (disabledFlag) {
                                user.setDisabledFlag(true);
                            } else {
                                user.setDisabledFlag(false);
                            }
                            Map deliveryAddresses = user.getDeliveryAddresses();
                            //Map ldapAddresses = ldapUser.getDeliveryAddresses();

                            deliveryAddresses.put(new Integer(1), ldapUser.getDeliveryAddresses().get(1));
                            user.setDeliveryAddresses(ldapUser.getDeliveryAddresses());
                            dao.updateUser(user);
                            logger.info("User " + user.getFullName() + " updated");
                        }
                        else logger.info("User " + user.getFullName() + " has not been updated");
                    }
                }
                Control[] controls = ctx.getResponseControls();
                if (controls != null) {
                    for (int i = 0; i < controls.length; i++) {
                        if (controls[i] instanceof PagedResultsResponseControl) {
                            PagedResultsResponseControl prrc =
                                    (PagedResultsResponseControl) controls[i];
                            total = prrc.getResultSize();
                            //System.out.println("Next Page:");
                            cookie = prrc.getCookie();
                        }
                    }
                }
                // Re-activate paged results
                ctx.setRequestControls(new Control[]{
                        new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
            } while (cookie != null);
        }
    }

    public void createUser(User user) throws DAOException {
    }

    public void updateUser(User user) throws DAOException {
    }

    public void fillUserAttributes(User user) {

        try {
            DirContext ctx = getDirContext();
            String userDn = uidAttribute + "=" + user.getExternalId() + "," + usersDn;
            NamingEnumeration results = ctx.getAttributes(userDn, new String[] {"mail", "cn", "employeeType"}).getAll();
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
                if (attr.getID().equals("employeeType")) {
                    if (attr.get().equals("disabled")) {
                        user.setDisabledFlag(true);
                    }
                }

            }
        } catch (NamingException ne) {
            // ignore it is probably local user
        }

    }

}
