//package com.eurodyn.uns.service.daemons.harvester;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import com.eurodyn.uns.ApplicationTestContext;
//import com.eurodyn.uns.util.TestUtils;
//import org.eclipse.jetty.server.Server;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import com.eurodyn.uns.dao.jdbc.BaseJdbcDao;
//import com.eurodyn.uns.dao.jdbc.JdbcEventMetadataDao;
//import com.eurodyn.uns.model.Channel;
//import com.eurodyn.uns.model.Event;
//import com.eurodyn.uns.service.facades.ChannelFacade;
//import com.eurodyn.uns.util.JettyUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import javax.sql.DataSource;
//import static com.eurodyn.uns.dao.jdbc.BaseJdbcDao.closeAllResources;
//import static org.junit.Assert.*;
//
///**
// * Unit tests for the {@link PullerThread}.
// *
// * @author Jaanus
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { ApplicationTestContext.class })
//public class PullerThreadTest {
//
//    /** SQL for finding an event by its external ID. */
//    private static final String FIND_EVENT_BY_EXT_ID_SQL = "SELECT * FROM EVENT WHERE EXT_ID=?";
//
//    @Autowired
//    private DataSource ds;
//
//    @Before
//    public void setUp() throws Exception {
//        TestUtils.setUpDatabase(ds, "seed-puller-thread.xml");
//    }
//
//    /**
//     * Test that new events will be created upon discovery from feed, and already existing events get their LAST_SEEN updated.
//     * @throws Exception
//     */
//    @SuppressWarnings("rawtypes")
//    @Test
//    public void testNewAndSeenEvents() throws Exception {
//
//        // Check the we have expected row counts in EVENT and related tables.
//
//        JdbcEventMetadataDao em = new JdbcEventMetadataDao();
//        assertEquals(6, getTableLineCount("DELIVERY"));
//        assertEquals(2, getTableLineCount("NOTIFICATION"));
//        assertEquals(2, getTableLineCount("EVENT"));
//
//        // Get the last-seen dates of events under test.
//
//        Event event830 = findEventByExtId("http://dataservice.eea.eu.int/dataservice/metadetails.asp?id=830");
//        Event event831 = findEventByExtId("http://dataservice.eea.eu.int/dataservice/metadetails.asp?id=831");
//        assertNotNull("Expected non-null event", event830);
//        assertNotNull("Expected non-null event", event831);
//        long lastSeen830 = event830.getLastSeen().getTime();
//        long lastSeen831 = event831.getLastSeen().getTime();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        assertEquals("Unexpected last-seen date", dateFormat.parse("2006-01-26 09:45:21").getTime(), lastSeen830);
//        assertEquals("Unexpected last-seen date", dateFormat.parse("2999-01-26 09:45:21").getTime(), lastSeen831);
//
//        // Now pull events from dummy feed-file.
//
//        Server server = null;
//        Date harvestDate = null;
//        try {
//            server = JettyUtil.startResourceServerMock(8999, "/testResources", "puller-thread-test-feed.rdf");
//            String url = "http://localhost:8999/testResources/puller-thread-test-feed.rdf";
//
//            ChannelFacade channelFacade = new ChannelFacade();
//            List channels = channelFacade.findHarvestChannels();
//            assertTrue("Expected at least one channel", channels != null && !channels.isEmpty());
//            Channel channel = null;
//            for (Iterator iter = channels.iterator(); iter.hasNext();) {
//                Channel currentChannel = (Channel) iter.next();
//                if (currentChannel != null && url.equals(currentChannel.getFeedUrl())) {
//                    channel = currentChannel;
//                    break;
//                }
//            }
//
//            assertNotNull("Expected non-null channel", channel);
//
//            PullerThread pullerThread = new PullerThread(channel);
//            pullerThread.run();
//            Exception exception = pullerThread.getException();
//            assertNull("Was not expecting this exception: " + exception, exception);
//
//            harvestDate = pullerThread.getHarvestDate();
//            assertNotNull("Was not expecting non-null harvest date", harvestDate);
//        } finally {
//            JettyUtil.close(server);
//        }
//
//        // Now check that we have the new event present and the rest's last-seen properly updated.
//
//        event830 = findEventByExtId("http://dataservice.eea.eu.int/dataservice/metadetails.asp?id=830");
//        event831 = findEventByExtId("http://dataservice.eea.eu.int/dataservice/metadetails.asp?id=831");
//        Event event832 = findEventByExtId("http://dataservice.eea.eu.int/dataservice/metadetails.asp?id=832");
//
//        assertNotNull("Expected non-null event", event830);
//        assertNotNull("Expected non-null event", event831);
//        assertNotNull("Expected non-null event", event832);
//
//        lastSeen830 = event830.getLastSeen().getTime();
//        lastSeen831 = event831.getLastSeen().getTime();
//        long lastSeen832 = event832.getLastSeen().getTime();
//
//        long harvestTime = harvestDate.getTime();
//        harvestTime = (harvestTime / 1000) * 1000;
//
//        assertEquals("Unexpected last-seen date", harvestTime, lastSeen830);
//        assertEquals("Unexpected last-seen date", harvestTime, lastSeen831);
//        assertEquals("Unexpected last-seen date", harvestTime, lastSeen832);
//    }
//
//    /**
//     * Returns the number of rows in the given table.
//     *
//     * @param table Given table name.
//     * @return Number of rows.
//     * @throws SQLException Thrown when accessing the database.
//     */
//    private int getTableLineCount(String table) throws SQLException {
//
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        try {
//            conn = BaseJdbcDao.getConnection();
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
//            return rs.next() ? rs.getInt(1) : 0;
//        } finally {
//            closeAllResources(rs, stmt, conn);
//        }
//    }
//
//    /**
//     *
//     * @param extId
//     * @return
//     * @throws SQLException
//     */
//    private Event findEventByExtId(String extId) throws SQLException {
//
//        Event event = null;
//
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        try {
//            conn = BaseJdbcDao.getConnection();
//            stmt = conn.prepareStatement(FIND_EVENT_BY_EXT_ID_SQL);
//            stmt.setString(1, extId);
//            rs = stmt.executeQuery();
//            if (rs.next()) {
//                event = new Event();
//                event.setId(rs.getInt("ID"));
//                event.setExtId(rs.getString("EXT_ID"));
//                event.setCreationDate(rs.getTimestamp("CREATION_DATE"));
//                event.setProcessed(rs.getByte("PROCESSED"));
//                event.setLastSeen(rs.getTimestamp("LAST_SEEN"));
//            }
//        } finally {
//            closeAllResources(rs, stmt, conn);
//        }
//
//        return event;
//    }
//}
