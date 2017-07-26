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

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.ResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationTemplateFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTemplateFacade.class);
    private DAOFactory daoFactory;

    public NotificationTemplateFacade() {
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    }

    public ResultDto getNotificationTemplates() {
        return getSortedNotificationTemplates("name", null);
    }

    public ResultDto getNotificationTemplates(Dto dto) {
        String orderProperty = dto.getAsString("orderProperty");
        if(orderProperty == null || orderProperty.length()==0)
            orderProperty = "name";     
        String order = dto.getAsString("order");
        return getSortedNotificationTemplates(orderProperty, order);
    }
    
    public ResultDto getSortedNotificationTemplates(String orderProperty, String order) {
        ResultDto result=new ResultDto();
        List stylesheets = null;
        try {
            stylesheets = daoFactory.getNotificationTemplateDao().findAllNotificationTemplates(orderProperty, order);
            result.put("list",stylesheets);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return result;
    }
    
    public NotificationTemplate getNotificationTemplate(Integer id) {
        NotificationTemplate notificationTemplate = null;
        try {
            notificationTemplate = daoFactory.getNotificationTemplateDao().findByPK(id);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return notificationTemplate;
    }

    
    public boolean updateNotificationTemplate(NotificationTemplate notificationTemplate) {
        boolean ret = false;
        try {
            daoFactory.getNotificationTemplateDao().updateNotificationTemplate(notificationTemplate);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }

    public boolean createNotificationTemplate(NotificationTemplate notificationTemplate) {
        boolean ret = false;
        try {
            daoFactory.getNotificationTemplateDao().createNotificationTemplate(notificationTemplate);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }


    public boolean deleteNotificationTemplate(NotificationTemplate notificationTemplate) throws Exception {
        return daoFactory.getNotificationTemplateDao().deleteNotificationTemplate(notificationTemplate);

    }

    public List findNotificationTemplatesForAssigment() throws Exception {
        return daoFactory.getNotificationTemplateDao().findNotificationTemplatesForAssigment();
        
    }
    

}
