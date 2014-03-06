package com.eurodyn.uns.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IEventMetadataDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.MetadataElement;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Statement;
import com.eurodyn.uns.util.common.UnsProperties;
import com.eurodyn.uns.util.common.WDSLogger;

/**
 * JDBC implementation of {@link IEventMetadataDao}.
 */
public class JdbcEventMetadataDao extends BaseJdbcDao implements IEventMetadataDao {

    /** Static logger for this class. */
    private static final WDSLogger LOGGER = WDSLogger.getLogger(JdbcEventMetadataDao.class);

    /** SQL for selecting all choosable statements for a channel. */
    private static final String ALL_CHOOSABLE_STATEMENTS_FOR_CHANNEL = ""
            + " (select EVENT_METADATA.PROPERTY, EVENT_METADATA.VALUE "
            + " from EVENT_METADATA , EVENT , CHANNEL_METADATA_ELEMENTS, METADATA_ELEMENTS "
            + " where EVENT_METADATA.EVENT_ID= EVENT.ID " + " and EVENT.CHANNEL_ID = ? "
            + " and CHANNEL_METADATA_ELEMENTS.CHANNEL_ID = EVENT.CHANNEL_ID "
            + " and EVENT_METADATA.PROPERTY = METADATA_ELEMENTS.NAME "
            + " and CHANNEL_METADATA_ELEMENTS.METADATA_ELEMENT_ID = METADATA_ELEMENTS.ID "
            + " and CHANNEL_METADATA_ELEMENTS.FILTERED = 1 " + ") UNION "
            + " (select distinct STATEMENT.PROPERTY , STATEMENT.VALUE " + " from STATEMENT,FILTER,SUBSCRIPTION,CHANNEL "
            + " where STATEMENT.FILTER_ID = FILTER.ID " + " and FILTER.SUBSCRIPTION_ID = SUBSCRIPTION.ID "
            + " and SUBSCRIPTION.CHANNEL_ID = ?  " + ") order by PROPERTY, VALUE ";

    /** SQL for deleting all events older than 60 days. */
    private static final String DELETE_OLD_EVENTS = " delete from EVENT where LAST_SEEN is not null and DATE_SUB(now(), interval "
            + UnsProperties.OLD_EVENTS_THRESHOLD + " day) > LAST_SEEN";

    /** SQL for deleting all metadata of events older than 60 days. */
    private static final String DELETE_OLD_EVENTS_METADATA = "delete EVENT_METADATA from EVENT_METADATA"
            + " JOIN EVENT on EVENT_ID=EVENT.ID where LAST_SEEN is not null and DATE_SUB(now(), interval "
            + UnsProperties.OLD_EVENTS_THRESHOLD + " day) > LAST_SEEN";

    /** SQL for deleting all deliveries of notifications of events older than 60 days. */
    private static final String DELETE_OLD_DELIVERIES = "delete DELIVERY from DELIVERY"
            + " JOIN NOTIFICATION on NOTIFICATION_ID=NOTIFICATION.ID"
            + " JOIN EVENT on EVENT_ID=EVENT.ID where LAST_SEEN is not null and DATE_SUB(now(), interval "
            + UnsProperties.OLD_EVENTS_THRESHOLD + " day) > LAST_SEEN";

    /** SQL for deleting all notifications of events older than 60 days. */
    private static final String DELETE_OLD_NOTIFICATIONS = "delete NOTIFICATION from NOTIFICATION"
            + " join EVENT on EVENT_ID=EVENT.ID where LAST_SEEN is not null and DATE_SUB(now(), interval "
            + UnsProperties.OLD_EVENTS_THRESHOLD + " day) > LAST_SEEN";

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#findEvent(java.lang.Integer)
     */
    @Override
    public Event findEvent(Integer event_id) throws DAOException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#eventExists(java.lang.String)
     */
    @Override
    public boolean eventExists(String extId) throws DAOException {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#deleteOldEvents()
     */
    @Override
    public void deleteOldEvents() throws DAOException {

        // Delete events older than 60 days, including records from DELIVERY, NOTIFICATION and EVENT_METADATA.

        Connection conn = null;
        PreparedStatement ps = null;
        try {

            // NB! The following code assumes no referential integrity and cascade delete on the related tables, hence
            // doing it programmatically and in a transaction.

            conn = getDatasource().getConnection();
            conn.setAutoCommit(false);

            int days = UnsProperties.OLD_EVENTS_THRESHOLD;

            // Delete all related deliveries as the most distantly related table.
            LOGGER.debug("Deleting all deliveries of notifications of events that haven't been seen for " + days + " days");
            ps = conn.prepareStatement(DELETE_OLD_DELIVERIES);
            ps.executeUpdate();
            closeAllResources(null, ps, null);

            // Delete all related notifications.
            LOGGER.debug("Deleting all notifications of events that haven't been seen for " + days + " days");
            ps = conn.prepareStatement(DELETE_OLD_NOTIFICATIONS);
            ps.executeUpdate();
            closeAllResources(null, ps, null);

            // Delete all related event metadata.
            LOGGER.debug("Deleting metadata of all events that haven't been seen for " + days + " days");
            ps = conn.prepareStatement(DELETE_OLD_EVENTS_METADATA);
            ps.executeUpdate();
            closeAllResources(null, ps, null);

            // Finally, delete all events themselves.
            LOGGER.debug("Deleting all events that haven't been seen for " + days + " days");
            ps = conn.prepareStatement(DELETE_OLD_EVENTS);
            ps.executeUpdate();

            // Now do the commit.
            LOGGER.debug("Committing the deletions of metadata, notifications and deliveries of events that haven't been seen for "
                    + days + " days");
            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            throw new DAOException(e);
        } finally {
            closeAllResources(null, ps, conn);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#findChoosableStatements(com.eurodyn.uns.model.Channel)
     */
    @Override
    public Map findChoosableStatements(Channel channel) throws DAOException {
        Map result = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getDatasource().getConnection();
            ps = conn.prepareStatement(ALL_CHOOSABLE_STATEMENTS_FOR_CHANNEL);
            ps.setInt(1, channel.getId().intValue());
            ps.setInt(2, channel.getId().intValue());
            rs = ps.executeQuery();

            if (rs.isBeforeFirst()) {
                result = new HashMap();
            }

            MetadataElement me;
            while (rs.next()) {
                me = new MetadataElement();
                me.setName(rs.getString("PROPERTY"));
                List values = (List) result.get(me);
                if (values == null) {
                    values = new ArrayList();
                    result.put(me, values);
                }
                values.add(rs.getString("VALUE"));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#createEventMetadata(com.eurodyn.uns.model.EventMetadata)
     */
    @Override
    public void createEventMetadata(EventMetadata em) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#deleteEventMetadata(com.eurodyn.uns.model.EventMetadata)
     */
    @Override
    public void deleteEventMetadata(EventMetadata em) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#deleteEventMetadataByValue(com.eurodyn.uns.model.Channel, java.lang.String)
     */
    @Override
    public void deleteEventMetadataByValue(Channel channel, String value) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#deleteEventMetadataByProperty(com.eurodyn.uns.model.Channel, java.lang.String)
     */
    @Override
    public void deleteEventMetadataByProperty(Channel channel, String value) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#findChannelProperties(com.eurodyn.uns.model.Channel)
     */
    @Override
    public Set findChannelProperties(Channel channel) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#findEventMetadataWithValue(com.eurodyn.uns.model.Channel, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ResultDto findEventMetadataWithValue(Channel channel, String property, String value) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#deleteFilterStatement(com.eurodyn.uns.model.Channel,
     * com.eurodyn.uns.model.Statement)
     */
    @Override
    public void deleteFilterStatement(Channel channel, Statement st) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#createEvent(com.eurodyn.uns.model.Event)
     */
    @Override
    public void createEvent(Event event) throws DAOException {
        // TODO implement this method or find out if implementation not necessary, but then write proper comment about that
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.IEventMetadataDao#findEventByExtId(java.lang.String)
     */
    @Override
    public Event findEventByExtId(String extId) throws DAOException {
        throw new UnsupportedOperationException("Method not implemented!");
    }
}
