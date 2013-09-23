import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.InputSource;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.ChannelMetadataElement;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.util.xml.XSLTransformer;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RSS;

/**
 * Unit tests for the event reading.
 */
public class TestRssReader {

    /** Static logger for this class. */
    private static final WDSLogger LOGGER = WDSLogger.getLogger(TestRssReader.class);

    /** User events retrieval query. */
    private final static String USER_EVENTS_QUERY = "select E.ID,  E.EXT_ID, E.RTYPE, EM.PROPERTY, EM.VALUE , E.CREATION_DATE"
            + " from NOTIFICATION N, DELIVERY D, EVENT E, EVENT_METADATA EM " + " where N.EEA_USER_ID=? and D.DELIVERY_TYPE_ID=4 "
            + " and N.ID=D.NOTIFICATION_ID " + "  and DATE_SUB(UTC_TIMESTAMP(),INTERVAL ? DAY) <= E.CREATION_DATE "
            + " and E.ID=N.EVENT_ID  and E.ID=EM.EVENT_ID " + " and E.CHANNEL_ID = ? ";

    /**
     * First test method.
     */
    @Test
    @Ignore
    public void test() {
        // TODO Rewrite this class to have proper tests. Right now it's just a main() method that doesn't really check anything.
        // @Ignore to prevent the test phase from failing until the tests have been written.
    }

    /**
     * Main executable method.
     *
     * @param args From command line.
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        TestRssReader rssReader = new TestRssReader();

        // rssReader.connect();

        try {

            UserFacade userFacade = new UserFacade();
            User user = userFacade.getUser("pavlone", false);

            List subscriptions = new ArrayList(user.getSubscriptions().values());
            Channel channel = ((Subscription) subscriptions.get(0)).getChannel();
            // Map events =
            // feedFacade.findUserEvents(channel.getId().intValue(),
            // user.getId().intValue(), 5);

            // channel.setTransformation(XslFacade.getInstance().getStylesheet(new Integer(2)));
            channel.setTransformation(null);

            List properties = new ArrayList();
            Map parameters = new HashMap();
            if (channel.getTransformation() == null) {
                List elements = new ArrayList(channel.getMetadataElements());
                Collections.sort(elements);
                if (elements != null) {
                    for (Iterator iter = elements.iterator(); iter.hasNext();) {
                        ChannelMetadataElement element = (ChannelMetadataElement) iter.next();
                        if (element.isVisible().booleanValue())
                            properties.add(element.getMetadataElement().getName());
                    }
                } else {
                    properties.add("http://www.w3.org/2000/01/rdf-schema#label");
                }
            } else {
                parameters.put("openinpopup", "true");
                parameters.put("showdescription", "true");
                parameters.put("showtitle", "true");
            }

            Map events = rssReader.findUserEvents(channel.getId().intValue(), user.getId().intValue(), 5);
            Iterator iterator = events.values().iterator();
            while (iterator.hasNext()) {
                RDFThing rdfThing = (RDFThing) iterator.next();
                // logger.info(rdfThing.getExt_id());
                // logger.info(rdfThing.getType());
                // rssReader.toRss(channel,rdfThing);
                // logger.info("Title is \"" + rdfThing.getTitle() + "\"");
                if (channel.getTransformation() != null) {
                    String rssContent = rssReader.toRss(channel, rdfThing);
                    InputSource source = new InputSource(new BufferedReader(new StringReader(rssContent)));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    XSLTransformer transform = new XSLTransformer();
                    transform.transform(channel.getTransformation().getName(), channel.getTransformation().getContent(), source,
                            baos, parameters);
                    String transformedContent = baos.toString();
                    LOGGER.info(transformedContent);
                    rdfThing.setTransformedContent(transformedContent);
                    channel.getTransformedEvents().put(rdfThing.getEventId(), rdfThing);
                } else {

                    String transformedContent = rssReader.renderVisbleElements(rdfThing, properties);
                    LOGGER.info(transformedContent);
                    rdfThing.setTransformedContent(transformedContent);
                    channel.getTransformedEvents().put(rdfThing.getEventId(), rdfThing);
                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param thing
     * @param properties
     * @return
     */
    private String renderVisbleElements(RDFThing thing, List properties) {
        StringBuffer result = new StringBuffer();

        result.append("<ul>\n");

        String subject = thing.getExt_id();
        Map metadata = thing.getMetadata();
        result.append("\t<li>\n");
        for (int i = 0; i < properties.size(); i++) {
            String property = (String) properties.get(i);
            String value = (String) metadata.get(property);
            if (value == null || value.trim().equals(""))
                continue;
            if (i == 0) {
                result.append("<span>");
                result.append(getLablel(property).toUpperCase()).append(": ");
                result.append("</span>");
                result.append("<a href=\"").append(subject).append("\" target=\"_blank\">").append(value).append("</a>\n");
                result.append("\n");
            } else if (value != null) {
                result.append("<p>");
                result.append("<span>");
                result.append(getLablel(property).toUpperCase()).append(": ");
                result.append("</span>");
                result.append(value);
                result.append("</p>\n");
            }
        }
        result.append("\t</li>\n");

        result.append("</ul>\n");

        return result.toString();
    }

    /**
     * @param uri
     * @return
     */
    private String getLablel(String uri) {
        String result = "";
        int i1 = uri.lastIndexOf("#");
        int i2 = uri.lastIndexOf("/");
        if (i1 < 0) {
            result = uri.substring(i2 + 1);
        } else {
            result = uri.substring(i1 + 1);
        }
        return result;
    }

    /**
     *
     * @param channel
     * @param rdfThing
     * @return
     * @throws Exception
     */
    public String toRss(Channel channel, RDFThing rdfThing) throws Exception {
        String result = "";
        Model rdf = ModelFactory.createDefaultModel();
        synchronized (rdf) {
            try {

                rdf.setNsPrefix("rss", "http://purl.org/rss/1.0/");
                rdf.setNsPrefix("content", "http://purl.org/rss/1.0/modules/content/");
                rdf.setNsPrefix("slash", "http://purl.org/rss/1.0/modules/slash/");
                Resource rssChannel = rdf.createResource(RSS.channel);
                rssChannel.addProperty(RSS.title, "channel title");
                rssChannel.addProperty(RSS.link, "http://foo.bar/channel/");
                rssChannel.addProperty(RSS.description, "channel description");
                // Seq items = rdf.createSeq();

                // rssChannel.addProperty(RSS.items, items);
                LOGGER.info("*****************************");
                LOGGER.info("Type is " + rdfThing.getType());
                LOGGER.info("Ext id  is " + rdfThing.getExt_id());
                LOGGER.info("*****************************");
                Resource item = rdf.createResource(rdfThing.getExt_id(), ResourceFactory.createResource(rdfThing.getType()));

                Iterator iter = rdfThing.getMetadata().entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry pairs2 = (Map.Entry) iter.next();
                    LOGGER.info("Dodao property " + pairs2.getKey() + " value " + pairs2.getValue());
                    item.addProperty(ResourceFactory.createProperty((String) pairs2.getKey()), (String) pairs2.getValue());

                }

                // items.add(item);
                StringWriter out = new StringWriter();
                RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");
                writer.write(rdf, new BufferedWriter(out), null);
                result = out.toString();
                LOGGER.info(result);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return result;

    }

    /**
     *
     * @param channelId
     * @param userId
     * @param interval
     * @return
     * @throws DAOException
     */
    public Map findUserEvents(int channelId, int userId, int interval) throws DAOException {

        Map things = new HashMap();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(USER_EVENTS_QUERY);
            ps.setInt(1, userId);
            ps.setInt(2, interval);
            ps.setInt(3, channelId);
            rs = ps.executeQuery();

            while (rs.next()) {
                boolean hasTitle = false;
                String ext_id = rs.getString("EXT_ID");
                RDFThing thing = (RDFThing) things.get(ext_id);
                if (thing == null) {
                    hasTitle = false;
                    thing = new RDFThing(ext_id, rs.getString("RTYPE"));
                    // thing.setReceptionDate(rs.getDate("CREATION_DATE")));
                    thing.setEventId(new Integer(rs.getInt("ID")));
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(rs.getDate("CREATION_DATE"));
                    thing.setReceivedDate(calendar.getTime());

                    things.put(ext_id, thing);
                }
                String property = rs.getString("PROPERTY");
                String value = rs.getString("VALUE");
                Map metadata = thing.getMetadata();
                metadata.put(property, value);
                if (!hasTitle && property.endsWith("/title")) {
                    thing.setTitle(value);
                    hasTitle = true;
                }

            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return things;

    }

    /**
     * Get JDBC connection.
     *
     * @return The JDBC connection.
     */
    public Connection getConnection() {
        Connection conn = null;
        try {

            String userName = "uns_developer";
            String password = "reportnet";
            String url = "jdbc:mysql://reportnet/UNS2_DEV";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
            LOGGER.info("Database connection established");
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }

        return conn;
    }

    /**
     * Close all given resources. Null-safe.
     *
     * @param rs SQL result set.
     * @param pstmt SQL statement.
     * @param conn JDBC connection.
     */
    public static void closeAllResources(ResultSet rs, Statement pstmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
            if ((conn != null) && (!conn.isClosed())) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
        }
    }

}
