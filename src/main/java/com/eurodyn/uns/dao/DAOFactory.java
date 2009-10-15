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

package com.eurodyn.uns.dao;

import com.eurodyn.uns.dao.hibernate.HibernateDAOFactory;
import com.eurodyn.uns.dao.jdbc.JdbcDaoFactory;
import com.eurodyn.uns.dao.ldap.LdapDaoFactory;

public abstract class DAOFactory {
    
	
    public static final int HIBERNATE = 1;
    public static final int LDAP = 2;
    public static final int JDBC = 3;
    
    public static DAOFactory getDAOFactory(int whichFactory) {
        switch (whichFactory) {
           case HIBERNATE:
              return new HibernateDAOFactory();
           case LDAP:
               return new LdapDaoFactory();
           case JDBC:
      	     return new JdbcDaoFactory();
           default:
              return null;
        }
     }


    public abstract IUserDao getUserDao();
    public abstract IChannelDao getChannelDao();
    public abstract IStylesheetDao getStylesheetDao();
    public abstract IMetadataElementDao getMetadataElementDao();
    public abstract IRoleDao getRoleDao();
    public abstract IDeliveryTypeDao getDeliveryTypeDao();
    public abstract IDeliveryDao getDeliveryDao();
    public abstract INotificationTemplateDao getNotificationTemplateDao();
    public abstract ISubscriptionDao getSubscriptionDao();
    public abstract IFeedDao getFeedDao();
    public abstract IEventMetadataDao getEventMetadataDao();
    public abstract INotificationDao getNotificationDao();
    

    

}
