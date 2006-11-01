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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.WDSLogger;

public class ChannelFacade {
	private static final WDSLogger logger = WDSLogger.getLogger(ChannelFacade.class);

	private DAOFactory daoFactory;
	private DAOFactory jdbcDaoFactory;

	public ChannelFacade() {
		daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
		jdbcDaoFactory= DAOFactory.getDAOFactory(DAOFactory.JDBC);
	}

	public ResultDto getChannels() {
		return getSortedChannels("creationDate", "desc");
	}

	public ResultDto getChannels(Dto dto) {
		String orderProperty = dto.getAsString("orderProperty");
		if (orderProperty == null || orderProperty.length() == 0)
			orderProperty = "title";
		String order = dto.getAsString("order");
		return getSortedChannels(orderProperty, order);
	}

	public ResultDto getSortedChannels(String orderProperty, String order) {
		ResultDto result = new ResultDto();
		List channels = null;
		try {
			channels = daoFactory.getChannelDao().findAllChannels(orderProperty, order);
			result.put("list", channels);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return result;
	}

	public ResultDto getPullChannels() {
		return getSortedChannelsByMode("PULL", "creationDate", "desc");
	}

	public ResultDto getPullChannels(Dto dto) {
		String orderProperty = dto.getAsString("orderProperty");
		if (orderProperty == null || orderProperty.length() == 0)
			orderProperty = "title";
		String order = dto.getAsString("order");
		return getSortedChannelsByMode("PULL", orderProperty, order);
	}

	public ResultDto getPushChannels() {
		return getSortedChannelsByMode("PUSH", "creationDate", "desc");
	}

	public ResultDto getPushChannels(Dto dto) {
		String orderProperty = dto.getAsString("orderProperty");
		if (orderProperty == null || orderProperty.length() == 0)
			orderProperty = "title";
		String order = dto.getAsString("order");
		return getSortedChannelsByMode("PUSH", orderProperty, order);
	}

	public ResultDto getSortedChannelsByMode(String mode, String orderProperty, String order) {
		ResultDto result = new ResultDto();
		List channels = null;
		try {
			channels = daoFactory.getChannelDao().findAllChannelsByMode(mode, orderProperty, order);
			result.put("list", channels);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return result;
	}

	public ResultDto getSortedChannelsByModeAndCreator(String mode, User creator, String orderProperty, String order) {
		ResultDto result = new ResultDto();
		List channels = null;
		try {
			channels = daoFactory.getChannelDao().findAllChannelsByModeAndCreator(mode, creator, orderProperty, order);
			result.put("list", channels);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return result;
	}

	public boolean deleteChannel(int channelId) {
		return deleteChannel(new Channel(channelId));
	}

	public Channel getChannel(Integer id) {
		Channel channel = null;
		try {
			channel = daoFactory.getChannelDao().findChannel(id);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return channel;
	}
	
	public Channel getChannelBySecId(String secId) {
		Channel channel = null;
		try {
			channel = daoFactory.getChannelDao().findChannel(secId);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return channel;
	}

	public boolean deleteChannel(Channel channel) {
		boolean ret = false;
		try {
			daoFactory.getChannelDao().deleteChannel(channel);
			ret = true;
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return ret;
	}

	public boolean createChannel(Channel channel) {
		boolean ret = false;
		try {
			daoFactory.getChannelDao().createChannel(channel);
			ret = true;
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return ret;
	}
	
	

	public boolean updateChannel(Channel channel) {
		boolean ret = false;
		try {
			daoFactory.getChannelDao().updateChannel(channel);
			ret = true;
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return ret;
	}

	public ResultDto getRpcUserChannels(Dto dto) {
		String orderProperty = dto.getAsString("orderProperty");
		ResultDto result  = new ResultDto();
		if (orderProperty == null || orderProperty.length() == 0)
			orderProperty = "title";
		String order = dto.getAsString("order");
		User user = (User) dto.get("user");
		try {
			List channels = daoFactory.getChannelDao().findRpcUserChannels(user,orderProperty,order);
			result.put("list", channels);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return result;
	}
	
	
	public List findOneEventForChannel() throws Exception{
		return daoFactory.getChannelDao().findOneEventForChannel();		
	}

	
	public Map findTestEventsForChannel(Channel channel) throws Exception{
		return daoFactory.getChannelDao().findTestEventsForChannel(channel);		
	}
	

	public Date getLastHarvestedDate(Channel channel) throws Exception {
		return jdbcDaoFactory.getChannelDao().getLastHarvestedDate(channel);
	}
}
