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
 *                           Nedeljko Pavlovic (ED) 
 */
package com.eurodyn.uns.service.channelserver;

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
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.util.cache.CacheItem;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.rdf.IChannel;

public class EEAChannelServer extends BaseChannelServer {
    private static final WDSLogger logger = WDSLogger.getLogger(EEAChannelServer.class);

    private BaseFeedHandler handler;
    private ChannelFacade channelFacade = new ChannelFacade();


    public EEAChannelServer() {
        handler = new DatabaseHandler(new PullHandler(new PushHandler(new TestHandler(new QueryHandler()))));
    }


    public String getChannelContent(Subscription subs, boolean ignoreCache) {
        String content = null;
        try {

            if (ignoreCache || subs.getChannel().getMode().equals("PUSH")){
                Dto request = new Dto();
                request.put("subscription", subs);
                handler.handleRequest(request, BaseChannelServer.DATABASE);
                content = (String) request.get("CONTENT");              
            }else{
                Channel channel = subs.getChannel();
                CacheItem entry = MemCache.get(subs.getId(),channelFacade.getLastHarvestedDate(channel));
                if (entry != null) {
                    logger.debug("Found entry in cache for subscription on channel" + subs.getChannel().getTitle());
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
                    }       
                    MemCache.put(subs.getId(), content, channel.getLastHarvestDate());
                }
                
            }
            if(content == null  || content.length() < 13){
                content = "<p class=\"nocontent\">CONTENT IS NOT AVAILABLE !</p>";
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
