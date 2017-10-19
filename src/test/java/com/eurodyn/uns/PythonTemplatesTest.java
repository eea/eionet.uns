<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java
package eionet.uns;
=======
package com.eurodyn.uns;
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import static org.hamcrest.CoreMatchers.allOf;
<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java
=======
import com.eurodyn.uns.util.TestUtils;
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java

=======
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.web.jsf.admin.templates.NotificationTemplateInterpreter;
import com.hp.hpl.jena.vocabulary.RSS;
<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
=======
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java

/**
 * Tests for the Python templates.
 */
<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java
public class PythonTemplatesTest {

    private static DataSource ds;
=======
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class PythonTemplatesTest {

    @Autowired
    private DataSource ds;
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java

    private static Properties templateMap;

    @BeforeClass
    public static void createDataSource() throws Exception {
<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java
        ds = DataSourceSupport.getDataSource();
=======
        /*ds = DataSourceSupport.getDataSource();*/
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java
        templateMap = new Properties();
        InputStream inStream = PythonTemplatesTest.class.getResourceAsStream("/templates.xml");
        templateMap.loadFromXML(inStream);
        inStream.close();
<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java
        loadData("/seed-event.xml");
    }

    private static void loadData(String seedFileName) throws Exception {
        IDatabaseConnection dbConn = new DatabaseConnection(ds.getConnection());
        IDataSet dataSet = new FlatXmlDataSet(PythonTemplatesTest.class.getResourceAsStream(seedFileName));
        DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
=======
>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java
    }

    @Before
    public void setUpIC() throws Exception {
<<<<<<< HEAD:src/test/java/eionet/uns/PythonTemplatesTest.java
        JNDISupport.setUpCore();
        JNDISupport.addSubCtxToTomcat("jdbc");
        JNDISupport.addPropToTomcat("jdbc/UNS_DS", ds);
        JNDISupport.addPropToTomcat("APPLICATION_HOME", "target/test-classes");
    }

    @After
    public void cleanUpIC() throws Exception {
        JNDISupport.cleanUp();
    }


=======
        TestUtils.setUpDatabase(ds, "seed-event.xml");
    }

>>>>>>> dockerize_86798:src/test/java/com/eurodyn/uns/PythonTemplatesTest.java
    /**
     * Template 1
     */
    @Test
    public void plainTemplate1() {
        Map result = executePlainTemplate("template1.plain");
        //System.out.println(result);
        String resultText = result.get("resultText").toString();
        String resultHtml = result.get("resultHtml").toString();
        assertThat(resultText, allOf(
                        containsString("from the Channel title channel"),
                        containsString("TITLE : Event title as DC title"),
                        containsString("OBLIGATION : Obligation HERE")));
        assertThat(resultHtml, containsString("yourself from the \"Channel title\" channel"));
    }

    /**
     * Template 2 (plain)
     */
    @Test
    public void plainTemplate2() {
        Map result = executePlainTemplate("template2.plain");
        String resultText = result.get("resultText").toString();
        String resultHtml = result.get("resultHtml").toString();
        assertThat(resultText, allOf(
                        containsString("Title               : Event title as DC title"),
                        containsString("Obligation          : Obligation HERE"),
                        containsString("yourself from the \"Channel title\" channel")));
        assertThat(resultHtml, allOf(
                        containsString("yourself from the \"Channel title\" channel"),
                        containsString("TITLE : Event title as DC title<br/>"),
                        containsString("OBLIGATION : Obligation HERE<br/>")));
    }

    /**
     * Template 2 (plain and HTML)
     */
    @Test
    public void htmlTemplate2() {
        Map result = executePlainTemplate("template2.plain", "template2.html");
        String resultText = result.get("resultText").toString();
        String resultHtml = result.get("resultHtml").toString();
        //System.out.println(resultText);
        //System.out.println(resultHtml);
        assertThat(resultText, allOf(
                        containsString("Title               : Event title as DC title"),
                        containsString("Obligation          : Obligation HERE"),
                        containsString("yourself from the \"Channel title\" channel")));
        assertThat(resultHtml, allOf(
                        containsString("yourself from the \"Channel title\" channel"),
                        containsString("<tr><th style=\"vertical-align:top; text-align:right\">"
                            + "Title</th><td>Event title as DC title</td></tr>"),
                        containsString("<tr><th style=\"vertical-align:top; text-align:right\">"
                            + "Obligation</th><td>Obligation HERE</td></tr>")));
    }


    private Map executePlainTemplate(String plainTemplateName) {
        return executePlainTemplate(plainTemplateName, null);
    }

    /**
     * Set up a template and execute Python on it.
     * NOTE that it also loads a notification template with ID=1 from the database, but
     * doesn't use it for anything.
     */
    private Map executePlainTemplate(String plainTemplateName, String htmlTemplateName) {
        NotificationTemplateFacade notificationTemplateFacade = new NotificationTemplateFacade();
        NotificationTemplate notificationTemplate = notificationTemplateFacade.getNotificationTemplate(new Integer(1));
        String plainTemplate = templateMap.getProperty(plainTemplateName);
        notificationTemplate.setPlainText(plainTemplate);
        if (htmlTemplateName != null) {
            String htmlTemplate = templateMap.getProperty(htmlTemplateName);
            notificationTemplate.setHtmlText(htmlTemplate);
        }
        NotificationTemplateInterpreter nti = new NotificationTemplateInterpreter();
        String unsubscribeLink = "UNSUBSCRIBE-LINK";
        User user = new User("dummy");
        user.setFullName("Jimmy Dummy");
        Channel channel = new Channel(1);
        channel.setTitle("Channel title");
        channel.setCreator(user);
        Date creationDate = new Date(1429000000000L); // Milliseconds since January 1, 1970, 00:00:00 GMT.

        Event testEvent = new Event(12, channel, "extid", creationDate, RSS.item.toString(), (byte)0);
        Map eventMetadataMap = new HashMap();
        testEvent.setEventMetadata(eventMetadataMap);
        addEventMetadata(testEvent, "http://purl.org/rss/1.0/title", "Event title as DC title");
        addEventMetadata(testEvent, "http://rod.eionet.europa.eu/schema.rdf#obligation", "Obligation HERE");

        return nti.pyhtonInterpreter(notificationTemplate, testEvent, user, unsubscribeLink);
    }

    private void addEventMetadata(Event testEvent, String property, String value) {
        Map eventMetadataMap = testEvent.getEventMetadata();
        EventMetadata em = new EventMetadata(property, value);
        em.setEvent(testEvent);
        eventMetadataMap.put(property, em);
    }
}
