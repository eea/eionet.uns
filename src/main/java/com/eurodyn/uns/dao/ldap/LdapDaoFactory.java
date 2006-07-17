package com.eurodyn.uns.dao.ldap;

import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.IChannelDao;
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

public class LdapDaoFactory extends DAOFactory {
    
    public IChannelDao getChannelDao() {
        return null;
    }
    
    public IUserDao getUserDao() {
        return new LdapUserDao();
    }
    
    /**
     * Will not be implemented.
     */
    public IStylesheetDao getStylesheetDao() {
        return null;
    }

    public IRoleDao getRoleDao() {
        return new LdapRoleDao();
    }
    
    public IMetadataElementDao getMetadataElementDao() {
    	return null;
    }
    

    public IDeliveryTypeDao getDeliveryTypeDao() {
    	return null;
    }
    
    public INotificationTemplateDao getNotificationTemplateDao(){
    	return null;
    }
    
    public ISubscriptionDao getSubscriptionDao(){
    	return null;
    }

    public IFeedDao getFeedDao(){
	    return null;
    }    

    
    public IEventMetadataDao getEventMetadataDao(){
	    return null;
    }    

    
    public INotificationDao getNotificationDao() {
		return null;
	}
}
