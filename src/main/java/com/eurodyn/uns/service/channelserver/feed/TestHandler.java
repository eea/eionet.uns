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
import java.util.Map;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.service.channelserver.rendering.GenericRenderer;
import com.eurodyn.uns.service.channelserver.rendering.RenderingEngine;
import com.eurodyn.uns.service.channelserver.rendering.XslRenderer;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.ConfiguratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHandler extends BaseFeedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestHandler.class);

    private BaseFeedHandler successor;

    private static String pathPrefix;
    static {
        try {
            pathPrefix = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar + "rdf" + File.separatorChar;
        } catch (ConfiguratorException e) {
            LOGGER.error("Error", e);
        }
    }

    public TestHandler() {
    }

    public TestHandler(BaseFeedHandler successor) {
        this.successor = successor;
    }

    public void handleRequest(Dto request, short action) throws Exception {
        Channel channel = (Channel) request.get("channel");
        if (action == BaseChannelServer.TEST) {
            collectDetails(channel, request);
        } else if (successor != null) {
            successor.handleRequest(request, action);
        }
    }

    public void collectDetails(Channel channel, Dto request) throws Exception {
        String result = null;
        RenderingEngine re = RenderingEngine.getInstance();
        ChannelFacade channelFacade = new ChannelFacade();
        if (channel.getMode().equals("PUSH")) {
            Map things = channelFacade.findTestEventsForChannel(channel);
            if (things != null && things.size() > 0)
                result = channel.getTransformation() == null ? re.renderContent(channel, things, new GenericRenderer()) : re.renderContent(channel, things, new XslRenderer());
        } else {
            result = channel.getTransformation() == null ? re.renderContent(channel, null, new GenericRenderer()) : re.renderContent(channel, null, new XslRenderer());
        }
        request.put("CONTENT", result);
    }

    /**
     * Find push channel file that belongs to any user Filename begins with SECONDARYID_
     * 
     * @param channel
     * @return path to push channel file
     */
     private String findPushChannelAnyFile(Channel channel) {
     File folder = new File(pathPrefix);
     String fileNames[] = folder.list();
     String find = channel.getSecondaryId() + "_";
     for (int i = 0; i < fileNames.length; i++) {
     if (fileNames[i].startsWith(find))
     return pathPrefix + fileNames[i];
     }
     return null;
     }
}
