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

import java.util.Map;
import java.util.Set;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventMetadataFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMetadataFacade.class);

    private DAOFactory daoFactory;
    private DAOFactory jdbcDaoFactory;


    public EventMetadataFacade() {
        jdbcDaoFactory = DAOFactory.getDAOFactory(DAOFactory.JDBC);
        daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    }

    public boolean createEvent(Event event) {
        boolean ret = false;
        try {
            daoFactory.getEventMetadataDao().createEvent(event);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }

    public boolean createEventMetadata(EventMetadata em) {
        boolean ret = false;
        try {
            daoFactory.getEventMetadataDao().createEventMetadata(em);
            ret = true;
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return ret;
    }


    public Map findChoosableStatements(Channel channel) {
        Map elements = null;
        try {
            elements = jdbcDaoFactory.getEventMetadataDao().findChoosableStatements(channel);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return elements;
    }



    public Set findChannelProperties(Channel channel) {
        Set properties = null;
        try {
            properties = daoFactory.getEventMetadataDao().findChannelProperties(channel);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return properties;
    }



    public void deleteEventMetadataByValue(Channel channel, String value){
        try {
            daoFactory.getEventMetadataDao().deleteEventMetadataByValue(channel,value);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }

    }


    public void deleteEventMetadataByProperty(Channel channel ,String property){
        try {
            daoFactory.getEventMetadataDao().deleteEventMetadataByProperty(channel,property);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }

    }



    public ResultDto findEventMetadataWithValue(Channel channel, String property, String value){
        ResultDto rDto = null;
        try {
            rDto = daoFactory.getEventMetadataDao().findEventMetadataWithValue(channel,property,value);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return rDto;
    }

    public void deleteFilterStatement(Channel channel, Statement statement){
        try {
            daoFactory.getEventMetadataDao().deleteFilterStatement(channel,statement);
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }

    }

    public Event findEvent(Integer event_id) throws Exception {
        return daoFactory.getEventMetadataDao().findEvent(event_id);
    }

    public Event findEventByExtId(String extId) throws Exception {
        return daoFactory.getEventMetadataDao().findEventByExtId(extId);
    }

    public boolean eventExists(String extId) throws Exception {
        return daoFactory.getEventMetadataDao().eventExists(extId);
    }

    public Integer deleteOldEvents() throws DAOException {
        int count = 0;
        try {
            count = jdbcDaoFactory.getEventMetadataDao().deleteOldEvents();
        } catch (DAOException e) {
            LOGGER.error("Error", e);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return count;
    }
}
