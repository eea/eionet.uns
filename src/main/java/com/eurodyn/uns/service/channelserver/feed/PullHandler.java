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
package com.eurodyn.uns.service.channelserver.feed;

import java.io.File;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.service.channelserver.rendering.GenericRenderer;
import com.eurodyn.uns.service.channelserver.rendering.RenderingEngine;
import com.eurodyn.uns.service.channelserver.rendering.XslRenderer;
import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.ConfiguratorException;
import com.eurodyn.uns.util.common.WDSLogger;

public class PullHandler extends BaseFeedHandler {
    private static final WDSLogger logger = WDSLogger.getLogger(PullHandler.class);

    private BaseFeedHandler successor;
    private static String pathPrefix;

    static {
        try {
            pathPrefix = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar + "rdf" + File.separatorChar;
        } catch (ConfiguratorException e) {
            logger.fatalError(e);
        }
    }
    
    public PullHandler(BaseFeedHandler successor) {
        this.successor = successor;
    }

    public void handleRequest(Dto request, short action) throws Exception {
        Channel channel = (Channel) request.get("channel");
        if (action==BaseChannelServer.PULL) {
            pull(channel, request);
        } else if (successor != null) {
            successor.handleRequest(request, action);
        }
    }

    public void pull(Channel channel, Dto request) throws Exception {
        if(channel.getMode().equals("PULL")){
            channel.setUser(null);
        }
        else{
            String userpath = "";
            if(channel.getUser() != null){
                userpath = "_" + channel.getUser().getExternalId(); 
            }
            String URI = pathPrefix + channel.getSecondaryId() + userpath + "_" + "data.rdf";
            File file = new File(URI);
            if(!file.exists()){
                URI = pathPrefix + channel.getSecondaryId() + "_" + "data.rdf";
                channel.setUser(null);
                file = new File(URI);
                if(!file.exists()){
                    request.put("CONTENT", null);
                    return;
                }
            }
            PushHandler.checkDataLifeTime(channel, channel.getUser(), file);
            channel.setFeedUrl("file:///" + URI);
        }
        
        RenderingEngine re=RenderingEngine.getInstance();
        String result=channel.getTransformation()==null?re.renderContent(channel,null, new GenericRenderer())
                                                       :re.renderContent(channel,null, new XslRenderer());
        request.put("CONTENT", result);
    }

}
