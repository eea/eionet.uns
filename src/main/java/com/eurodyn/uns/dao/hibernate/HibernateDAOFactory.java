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

package com.eurodyn.uns.dao.hibernate;

import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.IChannelDao;
import com.eurodyn.uns.dao.IDeliveryDao;
import com.eurodyn.uns.dao.IDeliveryTypeDao;
import com.eurodyn.uns.dao.IEventMetadataDao;
import com.eurodyn.uns.dao.IFeedDao;
import com.eurodyn.uns.dao.IMetadataElementDao;
import com.eurodyn.uns.dao.INotificationDao;
import com.eurodyn.uns.dao.INotificationTemplateDao;
import com.eurodyn.uns.dao.IRoleDao;
import com.eurodyn.uns.dao.IStylesheetDao;
import com.eurodyn.uns.dao.ISubscriptionDao;
import com.eurodyn.uns.dao.IUserDao;

public class HibernateDAOFactory extends DAOFactory {

	public IFeedDao getFeedDao() {
		return null;
	}

	public IChannelDao getChannelDao() {
		return new HibernateChannelDao();
	}

	public IUserDao getUserDao() {
		return new HibernateUserDao();
	}

	public IStylesheetDao getStylesheetDao() {
		return new HibernateStylesheetDao();
	}

	public IMetadataElementDao getMetadataElementDao() {
		return new HibernateMetadataElementDao();
	}

	public IRoleDao getRoleDao() {
		return new HibernateRoleDao();
	}

	public IDeliveryTypeDao getDeliveryTypeDao() {
		return new HibernateDeliveryTypeDao();
	}
	
	public IDeliveryDao getDeliveryDao() {
		return new HibernateDeliveryDao();
	}

	public INotificationTemplateDao getNotificationTemplateDao() {
		return new HibernateNotificationTemplateDao();
	}

	public ISubscriptionDao getSubscriptionDao() {
		return new HibernateSubscriptionDao();
	}

	public IEventMetadataDao getEventMetadataDao() {
		return new HibernateEventMetadataDao();
	}

	public INotificationDao getNotificationDao() {
		return new HibernateNotificationDao();
	}

}
