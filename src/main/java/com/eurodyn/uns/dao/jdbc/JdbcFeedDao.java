package com.eurodyn.uns.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IFeedDao;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.channelserver.EEAChannelServer;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;

public class JdbcFeedDao extends BaseJdbcDao implements IFeedDao {

	private static final WDSLogger logger = WDSLogger.getLogger(EEAChannelServer.class);

	private static int feedInterval = 0;

	static {

		try {

			feedInterval = Integer.parseInt(((ConfigElement) ConfigManager.getInstance().getConfigMap().get("feed/events_feed_age")).getValue().toString());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private final static String subscriptionEventsQuery = "select E.ID,  E.EXT_ID, E.RTYPE, EM.PROPERTY, EM.VALUE , E.CREATION_DATE" + " from NOTIFICATION N, DELIVERY D, EVENT E, EVENT_METADATA EM " + " where N.EEA_USER_ID=? and D.DELIVERY_TYPE_ID=4 " + " and N.ID=D.NOTIFICATION_ID " + "  and DATE_SUB(UTC_TIMESTAMP(),INTERVAL ? DAY) <= E.CREATION_DATE " + " and E.ID=N.EVENT_ID  and E.ID=EM.EVENT_ID " + " and E.CHANNEL_ID = ? ORDER BY E.CREATION_DATE DESC";

	private final static String userEventsQuery = "select E.ID,  E.EXT_ID, E.RTYPE, EM.PROPERTY, EM.VALUE , E.CREATION_DATE" + " from NOTIFICATION N, DELIVERY D, EVENT E, EVENT_METADATA EM " + " where N.EEA_USER_ID=? and D.DELIVERY_TYPE_ID=4 " + " and N.ID=D.NOTIFICATION_ID " + " and DATE_SUB(UTC_TIMESTAMP(),INTERVAL ? DAY) <= E.CREATION_DATE " + " and E.ID=N.EVENT_ID  and E.ID=EM.EVENT_ID " + " ORDER BY E.CREATION_DATE DESC";

	public Map findUserEvents(Subscription subscription) throws DAOException {

		Map things = new HashMap();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getDatasource().getConnection();
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
			conn = getDatasource().getConnection();
			ps = conn.prepareStatement(userEventsQuery);
			ps.setInt(1, user.getId().intValue());
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
		thing.getMetadata().put(property, value);
		if (!hasTitle && property.endsWith("/title")) {
			if (value != null) {
				thing.setTitle(value);
			}
		}

	}

}
