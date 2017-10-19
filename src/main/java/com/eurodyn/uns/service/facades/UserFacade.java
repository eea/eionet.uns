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

package com.eurodyn.uns.service.facades;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.User;

import eionet.acl.AccessControlListIF;
import eionet.acl.AccessController;
import eionet.acl.AuthMechanism;
import eionet.acl.SignOnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);

    private DAOFactory hibernateFactory;

    private DAOFactory ldapFactory;

    public UserFacade() {
        hibernateFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
        ldapFactory = DAOFactory.getDAOFactory(DAOFactory.LDAP);

    }

    public List findAllUsers() {
        List users = null;
        try {
            users = hibernateFactory.getUserDao().findAllUsers();
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return users;
    }

    public User findUser(String username) {
        User user = null;
        try {
            user = hibernateFactory.getUserDao().findUser(username);
            if (user != null){
                RoleFacade rf = new RoleFacade();
                user.setUserRoles(rf.getUserRoles(username));
            }
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return user;
    }

    public User findUser(Integer id) {
        User user = null;
        try {
            user = hibernateFactory.getUserDao().findUser(id);
            // RoleFacade rf = new RoleFacade();
            // user.setUserRoles(rf.getUserRoles(user.username));
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return user;
    }

    public User authenticate(String username, String password) {
        User user = null;
        try {
            if (uitAuthenticate(username, password)) {
                user = hibernateFactory.getUserDao().findUser(username);
                // if (user == null && createUser(username)){
                // RoleFacade rf = new RoleFacade();
                // user.setUserRoles(rf.getUserRoles(username));
                // }
            }
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return user;
    }

    public boolean updateUser(User user) {
        boolean success = false;
        try {
            hibernateFactory.getUserDao().updateUser(user);
            success = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return success;
    }

    public boolean uitAuthenticate(String userName, String userPws) {
        boolean authented = false;
        try {

            LOGGER.debug("Authenticating user '" + userName + "'");

            AuthMechanism.sessionLogin(userName, userPws);

            // DirectoryService.sessionLogin(userName, userPws);
            // String fullName = DirectoryService.getFullName(userName);

            /*
             * user was found in LDAP, now check his rights in ACL Logger.log("Authenticating user '" + userName + "'", 5); AccessControlListIF acl = getAcl(ACL_SERVICE_NAME ); boolean isOK = acl.checkPermission(userName, ACL_UPDATE_PRM); if (!isOK) throw new Exception("User " + userName + " does not have " + "this permission: " + ACL_UPDATE_PRM); // no exceptions raised, so we must be authentic
             */
            // Logger.log("Authenticated!", 5);
            authented = true;

        } catch (Exception e) {
            LOGGER.error("User '" + userName + "' not authenticated");
        }

        return authented;
    }

    public static boolean hasPerm(String usr, String aclPath, String prm) {
        if (!aclPath.startsWith("/")) {
            return false;
        }

        boolean has = false;
        AccessControlListIF acl = null;
        int i = aclPath.length() <= 1 ? -1 : aclPath.indexOf("/", 1); // not
        // forgetting
        // root
        // path
        // ("/")

        try {
            while (i != -1 && !has) {
                String subPath = aclPath.substring(0, i);
                try {
                    acl = AccessController.getAcl(subPath);
                } catch (Exception e) {
                    acl = null;
                }

                if (acl != null) {
                    has = acl.checkPermission(usr, prm);
                }

                i = aclPath.indexOf("/", i + 1);
            }
            if (!has) {
                try {
                    acl = AccessController.getAcl(aclPath);
                } catch (Exception e) {
                    if (e instanceof SignOnException && StringUtils.containsIgnoreCase(e.getMessage(), "No ACL")) {
                        LOGGER.info("Found no ACL with such path: " + aclPath + "   (" + e.toString() + ")");
                    } else {
                        LOGGER.error("Error", e);
                    }
                    acl = null;
                }

                if (acl != null) {
                    has = acl.checkPermission(usr, prm);
                }
            }
        } catch (SignOnException e) {
            return false;
        }

        return has;
    }

    public User getUser(String userName, boolean create) {
        User user = null;
        try {
            user = hibernateFactory.getUserDao().findUser(userName);
            if (user == null && create) {
                user = new User(userName);
                user.setVacationFlag(Boolean.FALSE);
                ldapFactory.getUserDao().fillUserAttributes(user);
                if (user.getFullName() == null) { // local user
                    user.setFullName(AuthMechanism.getFullName(userName));
                }

                user.setNumberOfColumns(new Short((short) 2));
                user.setPageRefreshDelay(new Integer(60));
                user.setPreferHtml(Boolean.FALSE);
                user.setPreferDashboard(Boolean.TRUE);
                hibernateFactory.getUserDao().createUser(user);
            }
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return user;
    }

}
