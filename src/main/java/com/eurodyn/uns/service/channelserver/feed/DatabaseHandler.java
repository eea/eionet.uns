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

import java.util.Map;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.service.channelserver.rendering.GenericRenderer;
import com.eurodyn.uns.service.channelserver.rendering.RenderingEngine;
import com.eurodyn.uns.service.channelserver.rendering.XslRenderer;
import com.eurodyn.uns.service.facades.FeedFacade;

public class DatabaseHandler extends BaseFeedHandler {
	

    private BaseFeedHandler successor;

    public DatabaseHandler(BaseFeedHandler successor) {
        this.successor = successor;
    }

    public void handleRequest(Dto request, short action) throws Exception {
        if (action==BaseChannelServer.DATABASE) {
            feed(request);
        } else if (successor != null) {
            successor.handleRequest(request, action);
        }
    }

    
    protected void feed(Dto request)throws Exception {
	    FeedFacade feedFacade = new FeedFacade();
	    Map things = null;
	    Channel channel = null;
	    Subscription subs = (Subscription) request.get("subscription");
	    if (subs != null){
		    channel = subs.getChannel();
		    things = feedFacade.findUserEvents(subs);		    
	    }else{
		    channel = (Channel) request.get("channel");
		    things = feedFacade.findChannelEvents(channel);
	    }
		
	    if (things == null || things.size() == 0){
		    request.put("CONTENT", "");
		    return;
	    }
	    
	    RenderingEngine re=RenderingEngine.getInstance();

	    String result=channel.getTransformation()==null?re.renderContent(channel,things, new GenericRenderer())
	                                                       :re.renderContent(channel,things, new XslRenderer());
	    request.put("CONTENT", result);	    
	    
    }
    
    

    
}
