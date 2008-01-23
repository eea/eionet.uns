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
import java.util.Map;
import java.util.Set;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Statement;
import com.eurodyn.uns.util.common.WDSLogger;

public class EventMetadataFacade {

	private static final WDSLogger logger = WDSLogger.getLogger(EventMetadataFacade.class);

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
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return ret;
	}


	public Map findChoosableStatements(Channel channel) {
		Map elements = null;
		try {
			elements = jdbcDaoFactory.getEventMetadataDao().findChoosableStatements(channel);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return elements;
	}

	
	
	public Set findChannelProperties(Channel channel) {
		Set properties = null;
		try {
			properties = daoFactory.getEventMetadataDao().findChannelProperties(channel);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return properties;
	}

	
	
	public void deleteEventMetadataByValue(Channel channel, String value){
		try {
			daoFactory.getEventMetadataDao().deleteEventMetadataByValue(channel,value);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}

	}
	

	public void deleteEventMetadataByProperty(Channel channel ,String property){
		try {
			daoFactory.getEventMetadataDao().deleteEventMetadataByProperty(channel,property);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		
	}
	
	
	
	public ResultDto findEventMetadataWithValue(Channel channel, String property, String value){
		ResultDto rDto = null;
		try {
			rDto = daoFactory.getEventMetadataDao().findEventMetadataWithValue(channel,property,value);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return rDto;
	}
	
	public void deleteFilterStatement(Channel channel, Statement statement){
		try {
			daoFactory.getEventMetadataDao().deleteFilterStatement(channel,statement);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}

	}

	public Event findEvent(Integer event_id) throws Exception {
		return daoFactory.getEventMetadataDao().findEvent(event_id);
	}
	
	public void deleteOldEvents(){
		try {
			jdbcDaoFactory.getEventMetadataDao().deleteOldEvents();
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		
	}
}
