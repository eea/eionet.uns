import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.dao.hibernate.HibernateEventMetadataDao;
import com.eurodyn.uns.dao.jdbc.JdbcFeedDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.RDFThing;
import com.eurodyn.uns.model.Statement;
import com.eurodyn.uns.model.User;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RSS;

public class ShowEventMetadata {

	
	public static void main(String[] args) {

		try {
			int test = 0;
			InitTestDB.initTestDb();
			if (test == 1) {
				testChoosableElements();
				testValues();

				testEventDate();
				testDeliveryReports();
				testRssFeed();
			}
			testFilterDeletion();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void testChoosableElements() {
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

	private static void testValues() throws Exception {
		String value = "gas";
		HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
		Channel channel = new Channel();
		channel.setId(new Integer(82));
		List result = (List) hibernateEventMetadataDao.findEventMetadataWithValue(channel, "http://purl.org/rss/1.0/description", value).get("list");

		for (Iterator iter = result.iterator(); iter.hasNext();) {
			EventMetadata element = (EventMetadata) iter.next();
			System.out.println(element.getProperty() + " " + element.getValue());

		}

	}

	private static void testFilterDeletion() throws Exception {
		Channel channel = new Channel();
		channel.setId(new Integer(82));
		HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
		Statement st = new Statement();
		st.setProperty("http://purl.org/rss/1.0/title");
		st.setValue("Hronika");
		hibernateEventMetadataDao.deleteFilterStatement(channel, st);

	}

	private static void testEventDate() throws Exception {
		Channel channel = new Channel();
		channel.setId(new Integer(82));
		//HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
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

	private static void testDeliveryReports() throws Exception {
//		HibernateEventMetadataDao hibernateEventMetadataDao = new HibernateEventMetadataDao();
//		Channel channel = new Channel();
//		channel.setId(new Integer(82));
//		User user = new User();
//		user.setId(new Integer(8));
//		Date fromDate = new Date();
//		Date toDate = new Date();
//		Calendar cal = Calendar.getInstance();
//		cal.set(2005, 12, 25, 0, 0, 0);
//		fromDate = cal.getTime();

		// hibernateEventMetadataDao.testDeliveryReport(fromDate, toDate, channel, user);
		// hibernateEventMetadataDao.testDeliveryReport(fromDate,toDate,null,null);

	}

	public static void testRssFeed() {
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
				Resource item = rdf.createResource(rdfThing.getExt_id(), ResourceFactory.createResource(rdfThing.getType()));

				Iterator iter = rdfThing.getMetadata().entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry pairs2 = (Map.Entry) iter.next();
					item.addProperty(ResourceFactory.createProperty((String) pairs2.getKey()), (String) pairs2.getValue());
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringWriter out = new StringWriter();
		RDFWriter writer = rdf.getWriter("RDF/XML-ABBREV");
		writer.write(rdf, new BufferedWriter(out), null);
		result = out.toString();
		System.out.println(" result" + result);

	}
}
