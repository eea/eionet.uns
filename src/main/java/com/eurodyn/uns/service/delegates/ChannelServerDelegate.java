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

package com.eurodyn.uns.service.delegates;

import java.util.Vector;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.channelserver.BaseChannelServer;
import com.eurodyn.uns.service.channelserver.DisabledException;
import com.eurodyn.uns.service.channelserver.EEAChannelServer;
import com.eurodyn.uns.service.channelserver.NotFoundException;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.rdf.IChannel;

public class ChannelServerDelegate {
    private static final WDSLogger logger = WDSLogger.getLogger(ChannelServerDelegate.class);

    private BaseChannelServer cs;

    public static final ChannelServerDelegate instance = new ChannelServerDelegate();

    public ChannelServerDelegate() {
        cs = new EEAChannelServer();
    }

    public String getChannelContent(Subscription subs) {
        return cs.getChannelContent(subs, false);
    }

    public String testNewChannel(IChannel channel) {
        return cs.testNewChannel(channel);
    }

    public String createPushChannel(Vector parameters) throws DisabledException, NotFoundException, Exception {
        logger.debug("Invoking Create method...");
        IChannel c = new Channel();
        c.setTitle((String) parameters.get(0));
        c.setDescription((String) parameters.get(1));
        c.setMode("PUSH");
        return cs.createChannel(c, (User) parameters.get(2));
    }

    public void push(Vector parameters) throws DisabledException, NotFoundException, Exception {
        logger.debug("Invoking PUSH method...");
        User u = null;
        if (parameters.size() > 2)
            u = (User) parameters.get(2);
        cs.push((String) parameters.get(0), u, (String) parameters.get(1));
    }

}
