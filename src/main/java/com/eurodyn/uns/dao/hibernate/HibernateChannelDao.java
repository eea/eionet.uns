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

package com.eurodyn.uns.dao.hibernate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IChannelDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.DateUtil;

public class HibernateChannelDao extends BaseHibernateDao implements IChannelDao {

	protected Class getReferenceClass() {
		return com.eurodyn.uns.model.Channel.class;
	}

	public List findAllChannels() throws DAOException {
		List result = null;
		try {
			result = findAll();
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
		return result;
	}

	public List findAllChannels(String orderProperty, String order) throws DAOException {
		List result = null;
		Session session = null;
		try {
			session = getSession();
			result = findAll(session, orderProperty, order);
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
		return result;
	}

	public List findAllChannelsByMode(String mode, String orderProperty, String order) throws DAOException {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery("select c from Channel as c where c.mode=:mode order by c." + orderProperty + " " + order);
			query.setString("mode", mode);
			return query.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
	}

	public List findAllChannelsByModeAndCreator(String mode, User creator, String orderProperty, String order) throws DAOException {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery("select c from Channel as c where c.mode=:mode and c.creator=:creator order by c." + orderProperty + " " + order);
			query.setString("mode", mode);
			query.setEntity("creator", creator);
			return query.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
	}

	public List findAllChannelsWithEvents() throws DAOException {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery("select c from Channel as c, Event e where exist( e.channel =:c) )");
			return query.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
	}

	public Channel findChannel(Channel channel) throws DAOException {
		return findChannel(channel.getId());

	}

	public Channel findChannel(Integer id) throws DAOException {
		Channel channel = null;
		try {
			Session s = getSession();
			channel = (Channel) s.load(getReferenceClass(), id);
			Hibernate.initialize(channel.getDeliveryTypes());
			Hibernate.initialize(channel.getRoles());
			Hibernate.initialize(channel.getMetadataElements());
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}

		return channel;
	}

	public Channel findChannel(String secondaryId) throws DAOException {
		Session session = null;
		Channel result = null;
		try {
			session = getSession();
			Query query = session.createQuery("select c from Channel as c where c.secondaryId=:secondaryId");
			query.setString("secondaryId", secondaryId);
			List list = query.list();
			if (list.size() > 0) {
				result = (Channel) list.get(0);
			}
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
		return result;
	}

	public void deleteChannel(Channel channel) throws DAOException {
		try {
			delete(channel);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	public void createChannel(Channel channel) throws DAOException {
		try {
			channel.setCreationDate(DateUtil.getCurrentUTCDate());
			// channel.setSecondaryId(UidGenerator.generate());
			String sid = String.valueOf(System.currentTimeMillis()) + channel.getCreator().getId().toString();
			channel.setSecondaryId(sid);
			save(channel);
		} catch (HibernateException e) {
			throw new DAOException(e);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public void updateChannel(Channel channel) throws DAOException {
		try {
			saveOrUpdate(channel);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	public List findRpcUserChannels(User user, String orderProperty, String order) throws DAOException {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery("select c from Channel as c where c.creator=:user order by c." + orderProperty + " " + order);
			query.setEntity("user", user);
			return query.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
	}

	public List findOneEventForChannel() throws DAOException {
		Session session = null;
		try {
			session = getSession();

			Query query = session.createQuery("select e  from Event as e , Channel c group by e.channel ) ");
			List result = query.list();
			return result;
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
	}

	public Map findTestEventsForChannel(Channel channel) throws DAOException {
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery("select e  from Event as e where e.channel=:channel order by e.id desc) ");
			query.setEntity("channel", channel);
			query.setMaxResults(10);
			Map things = new HashMap();
			List eventList = query.list();
			for (Iterator iter = eventList.iterator(); iter.hasNext();) {
				Event event = (Event) iter.next();
				boolean hasTitle = false;
				String ext_id = event.getExtId();
				RDFThing thing = (RDFThing) things.get(ext_id);
				if (thing == null) {
					hasTitle = false;
					thing = new RDFThing(ext_id, event.getRtype());
					thing.setEventId(new Integer(event.getId()));
					thing.setReceivedDate(event.getCreationDate());
					things.put(ext_id, thing);
				}
				Collection event_metadata = event.getEventMetadata().values();
				for (Iterator iterator = event_metadata.iterator(); iterator.hasNext();) {
					EventMetadata em = (EventMetadata) iterator.next();
					String property = em.getProperty();
					String value = em.getValue();
					thing.getMetadata().put(property, value);
					if (!hasTitle && property.endsWith("/title")) {
						if (value != null) {
							thing.setTitle(value);
						}
					}

				}

			}

			return things;
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
	}

}
