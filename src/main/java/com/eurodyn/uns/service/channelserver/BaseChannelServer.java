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

package com.eurodyn.uns.service.channelserver;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.cache.MemoryCache;
import com.eurodyn.uns.util.rdf.IChannel;

public abstract class BaseChannelServer {

	public static final short DATABASE = 0;

	public static final short PULL = 1;

	public static final short PUSH = 2;

	public static final short QUERY = 3;

	public static final short TEST = 4;

	protected static MemoryCache MemCache;

	static {
		MemCache = new MemoryCache(100, 10);
	}

	public abstract String getChannelContent(Subscription subscription, boolean ignoreCache);

	public abstract Dto queryNewChannel(Channel channel);

	public abstract String testNewChannel(IChannel channel);

	public abstract void invalidateCache();

	public abstract void push(String channelId, User user, String rdf) throws DisabledException, NotFoundException, Exception;

	public abstract String createChannel(IChannel channel, User creator) throws Exception;

}
