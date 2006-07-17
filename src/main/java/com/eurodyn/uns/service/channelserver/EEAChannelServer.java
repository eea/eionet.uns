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
 *    Original code: Dusan Popovic (ED)
 *    				      	 Nedeljko Pavlovic (ED) 
 */
package com.eurodyn.uns.service.channelserver;

import java.util.ArrayList;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.channelserver.feed.BaseFeedHandler;
import com.eurodyn.uns.service.channelserver.feed.DatabaseHandler;
import com.eurodyn.uns.service.channelserver.feed.PullHandler;
import com.eurodyn.uns.service.channelserver.feed.PushHandler;
import com.eurodyn.uns.service.channelserver.feed.QueryHandler;
import com.eurodyn.uns.service.channelserver.feed.TestHandler;
import com.eurodyn.uns.util.cache.CacheItem;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.rdf.IChannel;

public class EEAChannelServer extends BaseChannelServer {
	private static final WDSLogger logger = WDSLogger.getLogger(EEAChannelServer.class);

	private BaseFeedHandler handler;

	private DAOFactory daoFactory;

	public EEAChannelServer() {
		handler = new DatabaseHandler(new PullHandler(new PushHandler(new TestHandler(new QueryHandler()))));
		daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
	}

	public void invalidateChannelCache(IChannel channel, int type) {
		String id = channel.getId().toString();
		ArrayList keys = new ArrayList(MemCache.getKeys());
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			if (key.equals(id) || key.startsWith(id + "_"))
				MemCache.remove(key);
		}
	}

	public String getChannelContentOld(IChannel channel) {
		String content = null;
		try {
			CacheItem entry = null;
			String id;
			if (channel.getUser() != null) {
				id = channel.getId().toString() + "_" + channel.getUser().getExternalId();
				entry = MemCache.get(id);
			}
			if (entry == null) {
				id = channel.getId().toString();
				entry = MemCache.get(id);
				if (entry != null)
					channel.setUser(null);
			}
			if (entry != null) {
				content = (String) entry.getContent();
			} else {
				Channel newChannel = daoFactory.getChannelDao().findChannel(channel.getId());
				newChannel.setUser(channel.getUser());

				Dto request = new Dto();
				request.put("channel", newChannel);
				handler.handleRequest(request, BaseChannelServer.PULL);
				content = (String) request.get("CONTENT");
				if (content != null && content.length() > 0)
					channel.setUser(newChannel.getUser());
				if (newChannel.getUser() != null)
					id = newChannel.getId().toString() + "_" + newChannel.getUser().getExternalId();
				else
					id = newChannel.getId().toString();
				if (content != null && content.length() > 0)
					MemCache.put(id, content, newChannel.getRefreshDelay().intValue() * 60);
			}
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception ex) {
			logger.fatalError(ex);
		}
		return content;
	}

	public String getChannelContent(IChannel channel) {
		String content = null;
		try {
			CacheItem entry = null;
			String id;
			if (channel.getUser() != null) {
				id = channel.getId().toString() + "_" + channel.getUser().getExternalId();
				entry = MemCache.get(id);
			}
			if (entry == null) {
				id = channel.getId().toString();
				entry = MemCache.get(id);
				if (entry != null)
					channel.setUser(null);
			}
			if (entry != null) {
				content = (String) entry.getContent();
			} else {
				Channel newChannel = daoFactory.getChannelDao().findChannel(channel.getId());
				newChannel.setUser(channel.getUser());

				Dto request = new Dto();
				request.put("channel", newChannel);
				handler.handleRequest(request, BaseChannelServer.DATABASE);
				content = (String) request.get("CONTENT");
				if (content != null && content.length() > 0)
					channel.setUser(newChannel.getUser());
				if (newChannel.getUser() != null)
					id = newChannel.getId().toString() + "_" + newChannel.getUser().getExternalId();
				else
					id = newChannel.getId().toString();
				if (content != null && content.length() > 0)
					MemCache.put(id, content, newChannel.getRefreshDelay().intValue() * 60);
			}
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception ex) {
			logger.fatalError(ex);
		}
		return content;
	}

	public String getChannelContent(Subscription subs, boolean ignoreCache) {
		String content = null;
		try {

			if (ignoreCache){
				Dto request = new Dto();
				request.put("subscription", subs);
				handler.handleRequest(request, BaseChannelServer.DATABASE);
				content = (String) request.get("CONTENT");				
			}else{
				Channel channel = subs.getChannel();
				CacheItem entry = MemCache.get(subs.getId());
				if (entry != null) {
					content = (String) entry.getContent();
				} else {
					Dto request = new Dto();
					request.put("subscription", subs);
					handler.handleRequest(request, BaseChannelServer.DATABASE);
					content = (String) request.get("CONTENT");
					if (content != null && content.length() > 12) {
						if (content.indexOf("</svg>") > 0) {
							content = "<div style=\"overflow:auto; width: 100%; height:180px\">";
							content += "<img src=\"../svg.unsvg?subs_id="+ subs.getId() +"\" alt=\"Generated SVG\" />";
							content += "</div>";
						}
					} else {
						content = "<p class=\"nocontent\">CONTENT IS NOT AVAILABLE !</p>";
					}
					MemCache.put(subs.getId(), content, channel.getRefreshDelay().intValue() * 60);
				}
				
			}
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception ex) {
			logger.fatalError(ex);
		}
		return content;
	}

	public Dto queryNewChannel(Channel channel) {
		Dto parameters = null;
		try {
			parameters = new Dto();
			parameters.put("channel", channel);
			handler.handleRequest(parameters, BaseChannelServer.QUERY);
		} catch (Exception ex) {
			logger.fatalError(ex);
		}
		return parameters;
	}

	public String testNewChannel(IChannel channel) {
		Dto parameters = null;
		try {
			parameters = new Dto();
			parameters.put("channel", channel);
			handler.handleRequest(parameters, BaseChannelServer.TEST);
		} catch (Exception ex) {
			logger.fatalError(ex);
		}
		return (String) parameters.get("CONTENT");

	}

	public void push(String id, User user, String rdf) throws DisabledException, NotFoundException, Exception {
		Dto request = new Dto();
		request.put("secondaryId", id);
		request.put("RDF", rdf);
		request.put("user", user);
		handler.handleRequest(request, BaseChannelServer.PUSH);
	}

	public String createChannel(IChannel channel, User creator) throws Exception {
		Channel c = (Channel) channel;
		if (creator.getId() == null) {
			DAOFactory.getDAOFactory(DAOFactory.HIBERNATE).getUserDao().createUser(creator);
		}
		c.setCreator(creator);
		c.setStatus(new Integer(0));
		DAOFactory.getDAOFactory(DAOFactory.HIBERNATE).getChannelDao().createChannel((Channel) channel);

		return c.getSecondaryId();
	}

	public void invalidateCache() {
		// TODO Auto-generated method stub

	}
}
