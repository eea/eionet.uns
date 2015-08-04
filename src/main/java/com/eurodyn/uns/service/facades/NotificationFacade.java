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

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.WDSLogger;

import java.util.Date;
import java.util.List;

public class NotificationFacade {

    private static final WDSLogger logger = WDSLogger.getLogger(NotificationFacade.class);

    private DAOFactory daoFactory;
    private DAOFactory jdbcDaoFactory;

    public NotificationFacade() {
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
        jdbcDaoFactory= DAOFactory.getDAOFactory(DAOFactory.JDBC);
    }

    public List getNotificationsThroughput(Date fromDate, Date toDate, Channel channel, User user) throws Exception {
        return daoFactory.getNotificationDao().getNotificationsThroughput(fromDate, toDate, channel, user);
    }

    public List getFailedNotifications() throws Exception {
        return daoFactory.getNotificationDao().getFailedNotifications();
    }

    public List<Notification> getNotifications(Date fromDate, Date toDate, Channel channel, User user, Notification notification) throws DAOException {
        return daoFactory.getNotificationDao().getNotifications(fromDate, toDate, channel, user, notification);
    }

    public boolean createNotification(Notification notification) throws Exception {
        boolean success = false;
        try {
            daoFactory.getNotificationDao().createNotification(notification);
            success = true;
        } catch (DAOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.fatalError(e);
        }
        return success;
    }

    public List getNewNotifications() throws Exception {
        return jdbcDaoFactory.getNotificationDao().getNewNotifications();
    }

    public List getFailedDeliveries() throws Exception {
        return jdbcDaoFactory.getNotificationDao().getFailedDeliveries();
    }

}
