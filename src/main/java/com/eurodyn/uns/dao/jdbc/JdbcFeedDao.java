package com.eurodyn.uns.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IFeedDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.channelserver.EEAChannelServer;

import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcFeedDao extends BaseJdbcDao implements IFeedDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EEAChannelServer.class);

    private static int feedInterval = 0;

    static {

        try {

            feedInterval = Integer.parseInt(((ConfigElement) ConfigManager.getInstance().getConfigMap().get("feed/events_feed_age")).getValue().toString());
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

    private final static String subscriptionEventsQuery = "select E.ID,  E.EXT_ID, E.RTYPE, EM.PROPERTY, EM.VALUE , E.CREATION_DATE" + " from NOTIFICATION N, EVENT E, EVENT_METADATA EM " + " where N.EEA_USER_ID=? " + "  and DATE_SUB(UTC_TIMESTAMP(),INTERVAL ? DAY) <= E.CREATION_DATE " + " and E.ID=N.EVENT_ID  and E.ID=EM.EVENT_ID " + " and E.CHANNEL_ID = ? ORDER BY E.CREATION_DATE DESC";

    private final static String userEventsQuery = "select E.ID,  E.EXT_ID, E.RTYPE, EM.PROPERTY, EM.VALUE , E.CREATION_DATE" +
    " from NOTIFICATION N, EVENT E, EVENT_METADATA EM " +
    " where N.EEA_USER_ID=? " + " and DATE_SUB(UTC_TIMESTAMP(),INTERVAL ? DAY) <= E.CREATION_DATE " +
    " and E.ID=N.EVENT_ID  and E.ID=EM.EVENT_ID and N.CHANNEL_ID in" +
    " (select S.CHANNEL_ID from SUBSCRIPTION S, SUBSCRIPTION_DT SD where S.ID=SD.SUBSCRIPTION_ID and S.EEA_USER_ID=? and SD.DELIVERY_TYPE_ID=4)" +
    " ORDER BY E.CREATION_DATE DESC";

    private final static String channelEvents = "select E.ID,  E.EXT_ID, E.RTYPE, EM.PROPERTY, EM.VALUE , E.CREATION_DATE" + " from EVENT E, EVENT_METADATA EM " + " where E.CHANNEL_ID = ? and E.ID=EM.EVENT_ID " + " and DATE_SUB(UTC_TIMESTAMP(),INTERVAL ? DAY) <= E.CREATION_DATE "  + " ORDER BY E.CREATION_DATE DESC";



    public Map findUserEvents(Subscription subscription) throws DAOException {

        Map things = new HashMap();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(subscriptionEventsQuery);
            ps.setInt(1, subscription.getUser().getId().intValue());
            ps.setInt(2, feedInterval);
            ps.setInt(3, subscription.getChannel().getId().intValue());
            rs = ps.executeQuery();

            while (rs.next())
                populateThing(things, rs);

        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return things;

    }

    public Map findAllUserEvents(User user) throws DAOException {

        Map things = new HashMap();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(userEventsQuery);
            ps.setInt(1, user.getId().intValue());
            ps.setInt(2, feedInterval);
            ps.setInt(3, user.getId().intValue());
            rs = ps.executeQuery();

            while (rs.next())
                populateThing(things, rs);

        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return things;

    }


    public Map findChannelsEvents(Channel channel) throws DAOException {

        Map things = new HashMap();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(channelEvents);
            ps.setInt(1, channel.getId().intValue());
            ps.setInt(2, feedInterval);
            rs = ps.executeQuery();
            while (rs.next())
                populateThing(things, rs);

        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return things;

    }




    private void populateThing(Map things, ResultSet rs) throws Exception {
        boolean hasTitle = false;
        String ext_id = rs.getString("EXT_ID");
        RDFThing thing = (RDFThing) things.get(ext_id);
        if (thing == null) {
            hasTitle = false;
            thing = new RDFThing(ext_id, rs.getString("RTYPE"));
            thing.setEventId(new Integer(rs.getInt("ID")));
            thing.setReceivedDate(rs.getTimestamp("CREATION_DATE"));
            things.put(ext_id, thing);
        }
        String property = rs.getString("PROPERTY");
        String value = rs.getString("VALUE");

        ArrayList vals=(ArrayList) thing.getMetadata().get(property);

        if (vals != null) {
            vals.add(value);
        } else {
            ArrayList nar=new ArrayList();
            nar.add(value);
            thing.getMetadata().put(property, nar);
        }

        if (!hasTitle && property.endsWith("/title")) {
            if (value != null) {
                thing.setTitle(value);
            }
        }

    }

}
