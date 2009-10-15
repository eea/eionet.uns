package com.eurodyn.uns.dao.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.ISubscriptionDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.Filter;
import com.eurodyn.uns.model.Role;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;

public class HibernateSubscriptionDao extends BaseHibernateDao implements ISubscriptionDao {

	private static String availableChannelsQuery = "" + " select c  " + "from Channel as c left outer join c.roles as roles" + " where c.status = 1 " + " and roles is null " + "and c not in ( select s.channel from Subscription as s where s.user =:user) ";

	private static String availableChannelsQueryRoles = "" + " select c  " + "from Channel as c left outer join c.roles as roles" + " where c.status = 1 " + "and c not in ( select s.channel from Subscription as s where s.user =:user) ";

	public List findAllSubscriptions() throws DAOException {
		List result = null;
		try {
			result = findAll();
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
		return result;
	}

	public List findAllSubscriptions(String orderProperty, String order) throws DAOException {
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

	public Subscription findByPK(Integer SubscriptionId) throws DAOException {
		Subscription subscription = null;
		Session s = getSession();
		try {
			subscription = (Subscription) s.load(getReferenceClass(), SubscriptionId);
			// Hibernate.initialize(subscription.getDeliveryTypes());
			Hibernate.initialize(subscription.getFilters());
			Hibernate.initialize(subscription.getChannel().getDeliveryTypes());

		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}

		return subscription;
	}

	public Subscription findBySecondaryId(String secondaryId) throws DAOException {
		Subscription subscription = null;

		try {
			Session s = getSession();			
			Query query = s.createQuery("from Subscription s where s.secondaryId = :secondaryId");
			query.setString("secondaryId",secondaryId);
 			if (query.list().size() > 0) {
 				subscription = (Subscription) query.list().get(0);
 			}			
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
		
		return subscription;
	}

	protected Class getReferenceClass() {
		return com.eurodyn.uns.model.Subscription.class;
	}

	public void updateSubscription(Subscription sb) throws DAOException {
		try {
			saveOrUpdate(sb);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public void createSubscription(Subscription sb) throws DAOException {
		try {
			save(sb);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public void deleteSubscription(Subscription sb) throws DAOException {
		try {
			delete(sb);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}

	}

	public List findAvailableChannels(User user, List roles, String orderProperty, String order) throws DAOException {

		List result = null;
		Session session = null;
		try {
			session = getSession();
			String query = null;
			if (roles != null && roles.size() > 0) {
				StringBuffer rolesSB = new StringBuffer();
				int index = 0;
				for (Iterator iter = roles.iterator(); iter.hasNext();) {
					index++;
					Role role = (Role) iter.next();
					rolesSB.append("'" + role.getName() + "'" + (index != roles.size() ? "," : ""));
				}
				query = availableChannelsQueryRoles + " " + "and roles is null or roles in ( " + rolesSB.toString() + " )" + " order by c." + orderProperty + "  " + order;
			} else {
				query = availableChannelsQuery + " order by c." + orderProperty + "  " + order;
			}

			Query q = session.createQuery(query);
			q.setEntity("user", user);
			result = q.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
		return result;
	}

	public List findSubscriptionsForChannel(Channel channel) throws DAOException {
		List result = null;
		Session session = null;
		try {
			session = getSession();
			Query query = session.createQuery("from Subscription s where s.channel = :channel");
			query.setEntity("channel", channel);
			List list = query.list();
			List retlist = new ArrayList();
			for(Iterator it = list.iterator(); it.hasNext();){
				Subscription sub = (Subscription) it.next();
				Hibernate.initialize(sub.getFilters());
				for(Iterator it2 = sub.getFilters().iterator(); it2.hasNext();){
					Filter filter = (Filter) it2.next();
					Hibernate.initialize(filter.getStatements());
				}
				retlist.add(sub);
			}
			result = retlist;
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(session);
		}
		return result;
	}
}
