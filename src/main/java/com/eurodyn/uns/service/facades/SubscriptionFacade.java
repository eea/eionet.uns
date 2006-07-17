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

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Dto;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.uid.UidGenerator;

public class SubscriptionFacade {

	private static final WDSLogger logger = WDSLogger.getLogger(SubscriptionFacade.class);

	private DAOFactory daoFactory;

	public SubscriptionFacade() {
		daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
	}

	public ResultDto getSubscriptions() {
		return getSortedSubscriptions("name", null);
	}

	public ResultDto getSubscriptions(Dto dto) {
		String orderProperty = dto.getAsString("orderProperty");
		if (orderProperty == null || orderProperty.length() == 0)
			orderProperty = "name";
		String order = dto.getAsString("order");
		return getSortedSubscriptions(orderProperty, order);
	}

	public ResultDto getAvailableChannels(Dto dto) {
		String orderProperty = dto.getAsString("orderProperty");
		if (orderProperty == null || orderProperty.length() == 0)
			orderProperty = "name";
		String order = dto.getAsString("order");
		return getSortedChannels((User) dto.get("user"), (List) dto.get("roles"), orderProperty, order);
	}

	public ResultDto getSortedChannels(User user, List roles, String orderProperty, String order) {
		ResultDto result = new ResultDto();
		List channels = null;
		try {
			channels = daoFactory.getSubscriptionDao().findAvailableChannels(user, roles, orderProperty, order);
			result.put("list", channels);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return result;
	}

	public ResultDto getSortedSubscriptions(String orderProperty, String order) {
		ResultDto result = new ResultDto();
		List subscriptions = null;
		try {
			subscriptions = daoFactory.getSubscriptionDao().findAllSubscriptions(orderProperty, order);
			result.put("list", subscriptions);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return result;
	}

	public Subscription getSubscription(Integer id) {
		Subscription subscription = null;
		try {
			subscription = daoFactory.getSubscriptionDao().findByPK(id);
			// XmlContext x = new XmlContext();
			// x.setWellFormednessChecking();
			// x.checkFromInputStream(new ByteArrayInputStream(subscription.getContent().getBytes()));
			// subscription.setContent(x.serializeToString());
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return subscription;
	}

	public boolean updateSubscription(Subscription subscription) {
		boolean ret = false;
		try {
			daoFactory.getSubscriptionDao().updateSubscription(subscription);
			ret = true;
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return ret;
	}

	public boolean createSubscription(Subscription subscription) {
		boolean ret = false;
		try {

			subscription.setCreationDate(new GregorianCalendar(TimeZone.getTimeZone("UTC")).getTime());
			subscription.setSecondaryId(UidGenerator.generate());
			daoFactory.getSubscriptionDao().createSubscription(subscription);
			ret = true;
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return ret;
	}

	public boolean deleteSubscription(Subscription subscription) throws Exception {
		boolean ret = false;
		try {
			daoFactory.getSubscriptionDao().deleteSubscription(subscription);
			ret = true;
		} catch (DAOException e) {
			logger.error(e);
			throw (e);
		} catch (Exception e) {
			logger.fatalError(e);
			throw (e);
		}
		return ret;
	}

	public List findSubscriptionsForChannel(Channel channel) {
		List subscriptions = null;
		try {
			subscriptions = daoFactory.getSubscriptionDao().findSubscriptionsForChannel(channel);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return subscriptions;
	}

	public Subscription findBySecondaryId(String secondaryId) {
		Subscription subscription = null;
		try {
			subscription = daoFactory.getSubscriptionDao().findBySecondaryId(secondaryId);
		} catch (DAOException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.fatalError(e);
		}
		return subscription;
	}

	// secondaryId channel , String userName

	public boolean canUserSubscribe(String secondaryChannelId, User user) throws Exception {
		boolean approved = false;
		RoleFacade roleFacade = new RoleFacade();

		if (user != null) {
			List roles = roleFacade.getUserRoles(user.getExternalId());

			List channels = daoFactory.getSubscriptionDao().findAvailableChannels(user, roles, "title", "asc");
			if (channels != null) {
				for (Iterator iter = channels.iterator(); iter.hasNext();) {
					Channel channel = (Channel) iter.next();
					if (channel.getSecondaryId().equalsIgnoreCase(secondaryChannelId)) {
						approved = true;
						break;
					}
				}
			}

		}
		return approved;
	}
}
