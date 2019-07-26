package com.eurodyn.uns.dao.hibernate;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IEventMetadataDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.Filter;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateEventMetadataDao extends BaseHibernateDao implements IEventMetadataDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateEventMetadataDao.class);

    private static final int searchLimit = 150;

    @Override
    protected Class getReferenceClass() {
        return com.eurodyn.uns.model.EventMetadata.class;
    }

    @Override
    public Event findEvent(Integer event_id) throws DAOException {
        Event event = null;
        try {
            Session s = getSession();
            event = (Event) s.load(Event.class, event_id);
            Hibernate.initialize(event.getEventMetadata());
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession();
        }

        return event;
    }

    /*
     * (non-Javadoc)
     * @see com.eurodyn.uns.dao.IEventMetadataDao#eventExists(java.lang.String)
     */
    @Override
    public boolean eventExists(String extId) throws DAOException {
        Session session = null;
        try {
            session = getSession();
            Query query = session.createQuery("select e from Event as e where e.extId=:extId");
            query.setString("extId", extId);
            List list = query.list();
            return list.size()>0;
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.eurodyn.uns.dao.IEventMetadataDao#findEventByExtId(java.lang.String)
     */
    @Override
    public Event findEventByExtId(String extId) throws DAOException {

        Session session = null;
        try {
            session = getSession();
            Query query = session.createQuery("select e from Event as e where e.extId=:extId");
            query.setString("extId", extId);
            List list = query.list();
            return (Event) (list != null && !list.isEmpty() ? list.iterator().next() : null);
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void createEventMetadata(EventMetadata em) throws DAOException {
        try {
            save(em);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    @Override
    public void createEvent(Event event) throws DAOException {
        try {
            save(event);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    @Override
    public void deleteEventMetadata(EventMetadata em) throws DAOException {
        try {
            delete(em);
        } catch (HibernateException e) {
            throw new DAOException(e);
        }

    }

    @Override
    public Integer deleteOldEvents() throws DAOException {
        return 0;
    }

    @Override
    public void deleteEventMetadataByValue(Channel channel, String value) throws DAOException {
        Session session = null;
        try {
            session = getSession();
            // Bug in Hibernate 3.1.3 HHH-1765 - HQL Alias Regression
            // Query query = session.createQuery(" delete from EventMetadata em where exists ( select e.id from Event e where e.channel =:channel and em.event = e and em.value = :value)");
            Query query = session.createQuery(" select em from EventMetadata em ,Event e where e.channel =:channel and  em.event = e and em.value = :value)");
            query.setString("value", value);
            query.setEntity("channel", channel);
            List eventMetadataList = query.list();
            for (Iterator iter = eventMetadataList.iterator(); iter.hasNext();) {
                EventMetadata em = (EventMetadata) iter.next();
                deleteEventMetadata(em);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void deleteEventMetadataByProperty(Channel channel, String property) throws DAOException {
        Session session = null;
        try {
            session = getSession();
            // Bug in Hibernate 3.1.3 HHH-1765 - HQL Alias Regression
            // Query query = session.createQuery(" delete from EventMetadata em where exists ( select e.id from Event e where e.channel =:channel and em.event = e and em.property = :property)");
            Query query = session.createQuery(" select em from EventMetadata em ,Event e where e.channel =:channel and  em.event = e and em.property = :property)");
            query.setString("property", property);
            query.setEntity("channel", channel);
            List eventMetadataList = query.list();
            for (Iterator iter = eventMetadataList.iterator(); iter.hasNext();) {
                EventMetadata em = (EventMetadata) iter.next();
                deleteEventMetadata(em);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public ResultDto findEventMetadataWithValue(Channel channel, String property, String value) throws DAOException {
        ResultDto rDto = new ResultDto();
        Session session = null;
        try {
            session = getSession();
            Query countQuery = session.createQuery("select count( distinct em.value) from EventMetadata em, Event as e where e.channel = :channel and em.event = e and em.property = :property and em.value like '%" + value + "%' ");
            countQuery.setString("property", property);
            countQuery.setEntity("channel", channel);
            Long count = (Long) countQuery.uniqueResult();
            if (count.intValue() > searchLimit) {
                rDto.put("limit", searchLimit);
                rDto.setResultAsFailure();
            } else {
                String q = "select em  from EventMetadata as em  , Event as e where e.channel = :channel and em.event = e and em.property = :property and em.value like '%" + value + "%' group by em.value";
                Query query = session.createQuery(q);
                query.setString("property", property);
                query.setEntity("channel", channel);
                rDto.put("list", query.list());
            }
            return rDto;
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }

    }

    public List findAllChoosableStatements(Channel channel) throws DAOException {
        return null;
    }

    @Override
    public Map findChoosableStatements(Channel channel) throws DAOException {
        return null;
    }

    private static final String q_all_channels_properties = "" + " select em.property  " + " from EventMetadata em , Event e " + " where e.channel = :channel " + " and em.event = e " + " group by em.property ";

    @Override
    public Set findChannelProperties(Channel channel) throws DAOException {
        Set result = new HashSet();
        Session session = null;
        try {
            session = getSession();
            Query query = session.createQuery(q_all_channels_properties);
            query.setEntity("channel", channel);
            result.addAll(query.list());
        } catch (HibernateException e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
        return result;

    }

    @Override
    public void deleteFilterStatement(Channel channel, Statement st) throws DAOException {
        Session session = null;
        try {
            String property = st.getProperty();
            String value = st.getValue();
            session = getSession();
            Query query = session.createQuery("select filter from Subscription subscription join subscription.filters as filter join filter.statements as statement   where subscription.channel =:channel and (statement.property = :property or statement.value = :value) )");
            query.setEntity("channel", channel);
            query.setString("property", property);
            query.setString("value", value);
            List list = query.list();

            Transaction tr = session.beginTransaction();
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Filter filter = (Filter) iter.next();
                if (filter.getStatements().size() == 1) {
                    session.delete(filter);
                } else {
                    Set statements = filter.getStatements();
                    for (Iterator iterator = statements.iterator(); iterator.hasNext();) {
                        Statement statement = (Statement) iterator.next();
                        if (statement.getProperty().equals(property) || statement.getValue().equals(value)) {
                            iterator.remove();
                        }
                    }

                    if (filter.getStatements().isEmpty()) {
                        session.delete(filter);
                    } else {
                        session.update(filter);
                    }
                }
            }

            tr.commit();
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeSession(session);
        }
    }
}
