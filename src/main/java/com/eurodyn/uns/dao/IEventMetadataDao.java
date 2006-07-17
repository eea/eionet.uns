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
 *                           Dusan Popovic (ED)
 */
package com.eurodyn.uns.dao;

import java.util.Map;
import java.util.Set;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Statement;

public interface IEventMetadataDao {

	public Event findEvent(Integer event_id) throws DAOException;

	public void createEventMetadata(EventMetadata em) throws DAOException;

	public void deleteEventMetadata(EventMetadata em) throws DAOException;

	public Map findChoosableStatements(Channel channel) throws DAOException;

	public Set findChannelProperties(Channel channel) throws DAOException;

	public ResultDto findEventMetadataWithValue(Channel channel, String property, String value) throws DAOException;

	public void deleteEventMetadataByValue(Channel channel, String value) throws DAOException;

	public void deleteEventMetadataByProperty(Channel channel, String value) throws DAOException;

	public void deleteFilterStatement(Channel channel, Statement st) throws DAOException;
	
	public void createEvent(Event event) throws DAOException;
}
