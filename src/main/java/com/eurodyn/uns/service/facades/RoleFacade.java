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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eurodyn.uns.Properties;
import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.IRoleDao;
import com.eurodyn.uns.model.Role;

import com.eurodyn.uns.util.common.ConfiguratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleFacade.class);
    private static RoleFacade instance = null;
    public static long SYNC_PERIOD = 2 * 60 * 60 * 1000;    //2 hours in ms
    private static Date lastSync = new Date(0); //January 1, 1970, 00:00:00 GMT.
    private static Object mutex = new Object();
    private DAOFactory daoFactory;

    static {
        SYNC_PERIOD=60 * 1000 * Integer.parseInt(Properties.getStringProperty("ldap.sync_period"));
    }

    public RoleFacade() {
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    }

    public List getRoles() {
        List roles = null;
        try {
            roles = daoFactory.getRoleDao().findAllRoles();
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return roles;
    }

    public Role getRole(Integer id) {
        Role role = null;
        try {
            role = daoFactory.getRoleDao().findByPK(id);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return role;
    }

    public boolean updateRoles(List roles) {
        boolean ret = false;
        try {
            daoFactory.getRoleDao().updateRoles(roles);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }

    public boolean synchronizeRoles() {
        synchronized (mutex) {
            if(new Date().getTime() - lastSync.getTime() < SYNC_PERIOD)
                return true;

            boolean ret = false;
            IRoleDao roleDao = DAOFactory.getDAOFactory(DAOFactory.LDAP).getRoleDao();
            List LDAPRoles = new ArrayList();
            List DBRoles = new ArrayList();
            List newDBRoles = new ArrayList();
            try {
                long time = new Date().getTime();
                DBRoles = getRoles();
                LOGGER.info("Roles LDAP syncronization started");
                LOGGER.info("DB=" + (new Date().getTime() - time) + "ms");
                time = new Date().getTime();
                LDAPRoles = roleDao.findAllRoles();
                LOGGER.info("LDAP=" + (new Date().getTime() - time) + "ms");
                time = new Date().getTime();
                for (int i = 0; i < LDAPRoles.size(); i++) {
                    Role role = (Role)LDAPRoles.get(i);
                    if(!DBRoles.contains(role)){
                        newDBRoles.add(role);
                    }
                }
                LOGGER.info("Compare=" + (new Date().getTime() - time) + "ms");
                time = new Date().getTime();
                LOGGER.info("Found " + newDBRoles.size() + " new roles");
                ret = updateRoles(newDBRoles);
                LOGGER.info("UpdateDB=" + (new Date().getTime() - time) + "ms");
                if(ret)
                    lastSync = new Date();
            } catch (DAOException e) {
                LOGGER.error("Error", e);
            } catch (Exception e) {
                LOGGER.error("Error", e);
            }
            return ret;
        }
    }

    public List getUserRoles(String user) {
        IRoleDao roleDao = DAOFactory.getDAOFactory(DAOFactory.LDAP).getRoleDao();
        List userroles = null;
        try {
            userroles = roleDao.findUserRoles(user);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return userroles;
    }

    public static RoleFacade getInstance() {
        if (null == instance) {
           synchronized (RoleFacade.class) {
              if (null == instance) {
                 try {
                    instance = new RoleFacade();
                 } catch (Exception ce) {
                 }
              }
           }
        }
        return instance;
     }

}
