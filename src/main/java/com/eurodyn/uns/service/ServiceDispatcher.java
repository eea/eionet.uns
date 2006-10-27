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

package com.eurodyn.uns.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.Filter;
import com.eurodyn.uns.model.MetadataElement;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Statement;
import com.eurodyn.uns.model.Stylesheet;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import com.eurodyn.uns.service.facades.MetadataElementFacade;
import com.eurodyn.uns.service.facades.SubscriptionFacade;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.service.facades.XslFacade;
import com.eurodyn.uns.util.DateUtil;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.uid.UidGenerator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.tee.uit.security.AppUser;

public class ServiceDispatcher {
	private static final WDSLogger logger = WDSLogger.getLogger(XslFacade.class);
	
	private ChannelFacade channelFacade=new ChannelFacade();
	private UserFacade userFacade=new UserFacade();
	private SubscriptionFacade subsFacade=new SubscriptionFacade();
	private MetadataElementFacade metaFacade=new MetadataElementFacade();
	
	private User rpcUser;
	
	public ServiceDispatcher() {
		
	}
	
	public ServiceDispatcher(AppUser user) {
		this.rpcUser=userFacade.getUser(user.getUserName(),true);
	}
	
	public String test() {
		return "Nedjo";		
	}
	
	public String test(AppUser ttt) {
		return "Nedjo";		
	}
	
	public String createChannel(String channel_name, String description) throws Exception {
		Channel channel=new Channel();
		try {
			channel.setTitle(channel_name);
			channel.setDescription(description);
			channel.setMode("PUSH");
			channel.setLanguage("EN");
			channel.setCreator(this.rpcUser);
			ArrayList dt=new ArrayList();
			for (int i = 1; i < 5; i++) {
				DeliveryType d1=new DeliveryType();
				d1.setId(new Integer(i));
				dt.add(d1);
			}
			channel.setDeliveryTypes(dt);
			channel.setNotificationTemplate(new NotificationTemplate(new Integer(1)));
			channel.setTransformation(new Stylesheet(1));			
			channelFacade.createChannel(channel);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return channel.getSecondaryId();
	}
	
	public String sendNotification(String channel_id, Vector triples) throws Exception {
		//ArrayList events= new ArrayList();
		HashSet sss=new HashSet();
	
		Event event=new Event();
		
		try {
			Channel channel = getPushChannel(channel_id);
			for (int i = 0; i < triples.size(); i++) {
				Vector trp = (Vector) triples.get(i);
				String s=(String) trp.get(0);
				String p=(String) trp.get(1);
				String o=(String) trp.get(2);
				if (p.equalsIgnoreCase(RDF.type.toString())) {
					event.setExtId(s);
					event.setRtype(o);
				} else {
					EventMetadata emd=new EventMetadata();
					emd.setEvent(event);
					emd.setProperty(p);
					emd.setValue(o);
					sss.add(emd);
				}
			}
			
			event.setEventMetadataSet(sss);
			event.setChannel(channel);
			event.setCreationDate(DateUtil.getCurrentUTCDate());
			new EventMetadataFacade().createEvent(event);
			channel.setLastHarvestDate(DateUtil.getCurrentUTCDate());
			channelFacade.updateChannel(channel);
			
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return "";
	}
	
	public String sendNotificationRDF(String channel_id, String rdf) {
		try {
		} catch (Exception e) {
			logger.error(e);
		}
		return "";
	}
	
	public boolean canSubscribe(String channel_id, String username) throws Exception {
		boolean result = false;
		try {
			Channel channel=getPushChannel(channel_id);
			User userForSubs=userFacade.findUser(username);
			result = canUserSubscribe(channel, userForSubs);
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return result;
	}
	
	private boolean canUserSubscribe(Channel channel, User userForSubs) throws Exception {
		boolean result = false;
		try {
			if ( channel != null && userForSubs != null ) {
				result = userForSubs.getSubscriptions().get(channel.getId()) != null;
				result = result || subsFacade.canUserSubscribe(channel.getSecondaryId(), userForSubs);
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return result;
	}
	
	public String makeSubscription(String channel_id, String username, Vector filters) throws Exception {
		try {
			User userForSubs=userFacade.findUser(username);
			Channel channel=getPushChannel(channel_id);
			Subscription userSubscription = (Subscription) userForSubs.getSubscriptions().get(channel.getId());

			if ( canUserSubscribe(channel,userForSubs) ) {
			
				if (userSubscription==null) {
					userSubscription=new Subscription();
					userSubscription.setChannel(channel);
					userSubscription.setCreationDate(DateUtil.getCurrentUTCDate());
					userSubscription.setSecondaryId(UidGenerator.generate());
					userSubscription.setUser(userForSubs);
					userSubscription.setDeliveryTypes(new ArrayList());
					DeliveryType d1=new DeliveryType(); d1.setId(new Integer(1));
					userSubscription.getDeliveryTypes().add(d1); 
					userForSubs.getSubscriptions().put(channel, userSubscription);
				} else {
					
				}
				
				cleanDuplicateFilters(filters, userSubscription, channel);
					
				
				userFacade.updateUser(userForSubs);
			} else {
				throw new Exception("Subscription is not allowed to the user");
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
		return "";
	}
	
	private Channel getPushChannel(String channel_id) throws Exception {
		Channel channel=null;
		channel=channelFacade.getChannelBySecId(channel_id);
		if (channel==null) throw new Exception("Channel doesn't exist");
		if (channel.getStatus().intValue() == 0) throw new Exception("Channel is disabled");
		if (this.rpcUser == null || (channel.getCreator().getId().intValue() != this.rpcUser.getId().intValue())) throw new Exception("Not channel owner");
		return channel;		
	}
	
	private void cleanDuplicateFilters(Vector filters, Subscription userSubscription, Channel channel) {
		
		for (Iterator iter = filters.iterator(); iter.hasNext();) {
			Map element = (Map) iter.next();
			Filter f=new Filter();
			f.setSubscription(userSubscription);
			for (Iterator statments = element.keySet().iterator(); statments.hasNext();) {
				String property = (String) statments.next();
				setUpChannelMetadataElements(channel, property);
				Statement statement = new Statement();
				statement.setProperty(property);
				statement.setValue((String) element.get(property));
				statement.setMetadataElement(metaFacade.findByName(property));
				f.getStatements().add(statement);
			}
			if (!userSubscription.getFilters().contains(f)) 
				userSubscription.getFilters().add(f);
		}
	}
	
	
	protected void setUpChannelMetadataElements(Channel channel, String metadataElementName){
		
		boolean updatedChannel = false;
		List channelMetadataElements = channel.getMetadataElements();

		boolean hasMetadataElement = false;
		for (Iterator iter1 = channelMetadataElements.iterator(); iter1.hasNext();) {
			ChannelMetadataElement cme = (ChannelMetadataElement) iter1.next();
			if (cme.getMetadataElement().getName().equals(metadataElementName)) {
				hasMetadataElement = true;
				if( !cme.getFiltered().booleanValue()){
					cme.setFiltered(Boolean.TRUE);
					updatedChannel = true;
				}
				break;
			}
		}
		if (!hasMetadataElement) {
			MetadataElement me = metaFacade.findByName(metadataElementName);
			if (me == null) {
				me = new MetadataElement();
				me.setName(metadataElementName);
				metaFacade.createMetadataElement(me);
				me = metaFacade.findByName(metadataElementName);
			}

			ChannelMetadataElement cme = new ChannelMetadataElement();
			cme.setMetadataElement(me);
			cme.setVisible(Boolean.TRUE);
			cme.setAppearanceOrder(new Short((short) 100));
			cme.setObsolete(Boolean.FALSE);
			cme.setFiltered(Boolean.TRUE);
			channelMetadataElements.add(cme);
			updatedChannel = true;
		}
		
		channel.setMetadataElements(channelMetadataElements);
		if (channel.getId().intValue() != -1 && updatedChannel){
			channelFacade.updateChannel(channel);
		}
		
	}


}
