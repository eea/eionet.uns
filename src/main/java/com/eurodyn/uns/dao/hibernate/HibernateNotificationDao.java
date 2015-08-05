package com.eurodyn.uns.dao.hibernate;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.INotificationDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.util.DateUtil;
import com.eurodyn.uns.web.jsf.admin.reports.FailedNotificationsRecord;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HibernateNotificationDao extends BaseHibernateDao implements INotificationDao {

    private static String failedNotificationsQuery;

    static {
        StringBuffer sb = new StringBuffer();
        sb.append("select subs, deliveryAddress.deliveryType.name, deliveryAddress.address , count(delivery.deliveryType) ");
        sb.append("from Event ev join ev.notifications as notification join notification.deliveries as delivery, Subscription subs, User user join user.deliveryAddresses as deliveryAddress ");
        sb.append("where delivery.deliveryStatus = 0  ");
        sb.append("and  ev.channel = subs.channel ");
        sb.append("and  subs.user = notification.user ");
        sb.append("and  subs.user = user ");
        sb.append("and  delivery.deliveryType = deliveryAddress.deliveryType ");
        sb.append(" group by subs.channel , delivery.deliveryType ");
        failedNotificationsQuery = sb.toString();
    }

    protected Class getReferenceClass() {
        return Notification.class;
    }

    @Override
    public List getNotificationsThroughput(Date fromDate, Date toDate, Channel channel, User user) throws DAOException {
        List result = new ArrayList();
        Session session = null;
        try {
            session = getSession();
            StringBuffer sb = new StringBuffer();
            sb.append("select date_format(delivery.deliveryTime,'%d-%m-%Y'), delivery.deliveryType.name , delivery.deliveryStatus, count(notification) ");
            sb.append("from Event event join event.notifications as notification join notification.deliveries as delivery  ");
            sb.append("where delivery.deliveryType < 3   ");
            sb.append("and (delivery.deliveryTime  between :fromDate and :toDate) ");
            if (channel != null)
                sb.append("and event.channel = :channel ");
            if (user != null)
                sb.append("and notification.user = :user ");
            sb.append("group by date_format(delivery.deliveryTime,'%d-%m-%Y'), delivery.deliveryStatus , delivery.deliveryType ");

            Query query = session.createQuery(sb.toString());
            query.setTimestamp("fromDate", DateUtil.startOfADay(fromDate));
            query.setTimestamp("toDate", DateUtil.secondBeforeMidnight(toDate));
            if (channel != null)
                query.setEntity("channel", channel);
            if (user != null)
                query.setEntity("user", user);

            result.addAll(query.list());

        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }

        return result;

    }

    public List getFailedNotifications() throws DAOException {
        List result = new ArrayList();
        Session session = null;
        try {
            session = getSession();
            Query query = session.createQuery(failedNotificationsQuery);
            List queryResult = query.list();
            for (Iterator iter = queryResult.iterator(); iter.hasNext();) {
                Object[] record = (Object[]) iter.next();
                FailedNotificationsRecord fnr = new FailedNotificationsRecord();
                fnr.setSubscription((Subscription) record[0]);
                fnr.setDeliveryType(record[1].toString());
                fnr.setAddress(record[2].toString());
                fnr.setCount(new Integer(record[3].toString()));
                result.add(fnr);
            }
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }

        return result;
    }

    public void createNotification(Notification notification) throws DAOException {
        try {
            save(notification);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }
    }

    public List getNewNotifications() throws DAOException {
        return null;
    }

    public List getFailedDeliveries() throws DAOException {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Notification> getNotifications(Date fromDate, Date toDate, Channel channel, User user, Notification example) throws DAOException {
        Session session = null;
        List result = new ArrayList();
        try {
            session = getSession();
            StringBuffer sb = new StringBuffer();
            sb.append("FROM Notification n inner join fetch n.deliveries d ");
            sb.append("WHERE n.subject like lower(:subject) ");
            if (user != null) {
                sb.append("AND n.user = :user ");
            }
            if (channel != null) {
                sb.append("AND n.channelId = :channelId ");
            }
            sb.append("AND d.deliveryTime BETWEEN :start AND :end ");
            Query q = session.createQuery(sb.toString());
            if (user != null) {
                q.setEntity("user", user);
            }
            if (channel != null) {
                q.setInteger("channelId", channel.getId());
            }
            q.setString("subject", "%" + example.getSubject() + "%");
            q.setTimestamp("start", DateUtil.startOfADay(fromDate));
            q.setTimestamp("end", DateUtil.secondBeforeMidnight(toDate));
           result = q.list();
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
        return result;
    }
}
