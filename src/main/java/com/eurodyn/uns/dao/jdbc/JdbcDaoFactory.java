package com.eurodyn.uns.dao.jdbc;

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

public class JdbcDaoFactory extends DAOFactory {

    public IChannelDao getChannelDao() {
        return new JdbcChannelDao();
    }

    public IUserDao getUserDao() {
        return null;
    }

    public IStylesheetDao getStylesheetDao() {
        return null;
    }

    public IRoleDao getRoleDao() {
        return null;
    }

    public IMetadataElementDao getMetadataElementDao() {
        return null;
    }

    public IDeliveryTypeDao getDeliveryTypeDao() {
        return null;
    }

    public IDeliveryDao getDeliveryDao() {
        return null;
    }

    public INotificationTemplateDao getNotificationTemplateDao() {
        return null;
    }

    public ISubscriptionDao getSubscriptionDao() {
        return null;
    }

    public IFeedDao getFeedDao() {
        return new JdbcFeedDao();
    }

    public IEventMetadataDao getEventMetadataDao() {
        return new JdbcEventMetadataDao();
    }

    public INotificationDao getNotificationDao() {
        return new JdbcNotificationDao();
    }

}
