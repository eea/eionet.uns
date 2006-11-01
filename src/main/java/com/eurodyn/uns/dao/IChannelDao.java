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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.User;

public interface IChannelDao {

	public List findAllChannels() throws DAOException;

	public List findAllChannels(String orderProperty, String order) throws DAOException;

	public List findAllChannelsByMode(String mode, String orderProperty, String order) throws DAOException;

	public List findAllChannelsByModeAndCreator(String mode, User creator, String orderProperty, String order) throws DAOException;

	public Channel findChannel(Channel channel) throws DAOException;

	public Channel findChannel(Integer id) throws DAOException;

	public Channel findChannel(String secondaryId) throws DAOException;

	public void deleteChannel(Channel channel) throws DAOException;

	public void createChannel(Channel channel) throws DAOException;

	public void updateChannel(Channel channel) throws DAOException;

	public List findRpcUserChannels(User user, String orderProperty, String order) throws DAOException;

	public List findOneEventForChannel() throws DAOException;

	public Map findTestEventsForChannel(Channel channel) throws DAOException;
	
	public Date getLastHarvestedDate(Channel channel) throws DAOException ;
}
