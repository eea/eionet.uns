package com.eurodyn.uns.dao.hibernate;

import com.eurodyn.uns.ApplicationTestContext;
import com.eurodyn.uns.dao.jdbc.JdbcFeedDao;
import com.eurodyn.uns.model.*;
import com.eurodyn.uns.model.Statement;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RSS;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class EventMetadataTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMetadataTest.class);

    @Autowired
    private DataSource ds;

    /**
     * Returns the string value of the first child node of the given node.
     * Null-safe and safe when there are no child nodes actually.
     * Returns empty string when the child node value cannot be found.
     *
     * @param node The node whose first child is to be checked.
     * @return The first child node's value.
     *
     */
    private String getFirstChildNodeValue(Node node) {

        String result = null;
        if (node != null) {
            NodeList childNodes = node.getChildNodes();
            if (childNodes != null && childNodes.getLength() > 0) {
                Node firstChild = childNodes.item(0);
                if (firstChild != null) {
                    result = firstChild.getNodeValue();
                }
            }
        }

        return result == null ? "" : result;
    }

    @Test
    public void testChoosableElements() {
        // EventMetadataFacade eventMetadataFacade = new EventMetadataFacade();
        Channel channel = new Channel();

        channel.setId(new Integer(82));
        // List elements = eventMetadataFacade.findChoosableStatements(channel);
        // for (Iterator iter = elements.iterator(); iter.hasNext();) {
        // EventMetadata element = (EventMetadata) iter.next();
        // System.out.println(element.getProperty() + " " + element.getValue());
        //
        // }

    }

    @Test
    @Ignore
    public void x_testValues() throws Exception {
        String value = "gas";
        HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
        Channel channel = new Channel();

        channel.setId(new Integer(82));
        List result = (List) hibernateEventMetadataDao.findEventMetadataWithValue(channel,
          "http://purl.org/rss/1.0/description", value).get("list");

        for (Iterator iter = result.iterator(); iter.hasNext();) {
            EventMetadata element = (EventMetadata) iter.next();

            System.out.println(element.getProperty() + " " + element.getValue());

        }

    }

    @Test
    public void x_testFilterDeletion() throws Exception {
        Channel channel = new Channel();

        channel.setId(new Integer(82));
        HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
        Statement st = new Statement();

        st.setProperty("http://purl.org/rss/1.0/title");
        st.setValue("Hronika");
        hibernateEventMetadataDao.deleteFilterStatement(channel, st);

    }

    @Test
    public void testEventDate() throws Exception {
        Channel channel = new Channel();

        channel.setId(new Integer(82));
        // HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
        // hibernateEventMetadataDao.testEventDays();
        // hibernateEventMetadataDao.testEventMetadata();
        // hibernateEventMetadataDao.testFailedNotifications();
        // hibernateEventMetadataDao.testEventDays2();
        // hibernateEventMetadataDao.testEventDays3();
        // hibernateEventMetadataDao.testEventDays7();

        // cal.set(2000, 12, 25, 0, 0, 0);
        // java.util.Date fromDate = cal.getTime();
        // cal.set(2000, 12, 26, 0, 0, 0);
        // java.util.Date toDate = cal.getTime();
        // criteria.addBetween(releaseDate,fromDate,toDate);

    }

    @Test
    public void x_testDeliveryReports() throws Exception {
        // HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
        // Channel channel = new Channel();
        // channel.setId(new Integer(82));
        // User user = new User();
        // user.setId(new Integer(8));
        // Date fromDate = new Date();
        // Date toDate = new Date();
        // Calendar cal = Calendar.getInstance();
        // cal.set(2005, 12, 25, 0, 0, 0);
        // fromDate = cal.getTime();
        // hibernateEventMetadataDao.testDeliveryReport(fromDate, toDate, channel, user);
        // hibernateEventMetadataDao.testDeliveryReport(fromDate,toDate,null,null);
    }

    @Test
    public void x_testRssFeed() {
        String result = "";
        Model rdf = ModelFactory.createDefaultModel();

        JdbcFeedDao jdbcFeedDao = new JdbcFeedDao();

        rdf.setNsPrefix("rss", "http://purl.org/rss/1.0/");
        rdf.setNsPrefix("content", "http://purl.org/rss/1.0/modules/content/");
        rdf.setNsPrefix("slash", "http://purl.org/rss/1.0/modules/slash/");
        Resource rssChannel = rdf.createResource(RSS.channel);
        // rssChannel.addProperty(RSS.title, channel.getTitle());
        // rssChannel.addProperty(RSS.link, channel.getFeedUrl() != null ? channel.getFeedUrl() : "http://testChannel.com");
        // rssChannel.addProperty(RSS.description, channel.getDescription());

        User user = new User();

        user.setId(new Integer(8));
        try {
            Map things = jdbcFeedDao.findAllUserEvents(user);
            // System.out.println("things" + things);
            Collection thingsList = things.values();

            for (Iterator iterator = thingsList.iterator(); iterator.hasNext();) {
                RDFThing rdfThing = (RDFThing) iterator.next();
                Resource item = rdf.createResource(rdfThing.getExt_id(),
                        ResourceFactory.createResource(rdfThing.getType()));

                Iterator iter = rdfThing.getMetadata().entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry pairs2 = (Map.Entry) iter.next();

                    item.addProperty(
                            ResourceFactory.createProperty(
                                    (String) pairs2.getKey()),
                                    (String) pairs2.getValue());
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        StringWriter out = new StringWriter();
        RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");

        writer.write(rdf, new BufferedWriter(out), null);
        result = out.toString();
        System.out.println(" result" + result);

    }
}
