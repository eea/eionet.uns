package eionet.uns;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.eurodyn.uns.model.User;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.web.jsf.admin.templates.NotificationTemplateInterpreter;

/**
 * Tests for the Python templates.
 */
public class PythonTemplatesTest {

    private static DataSource ds;

    private static Properties templateMap;

    @BeforeClass
    public static void createDataSource() throws Exception {
        ds = DataSourceSupport.getDataSource();
        templateMap = new Properties();
        InputStream inStream = PythonTemplatesTest.class.getResourceAsStream("/templates.xml");
        templateMap.loadFromXML(inStream);
        inStream.close();
    }

    @Before
    public void setUpIC() throws Exception {
        JNDISupport.setUpCore();
        JNDISupport.addSubCtxToTomcat("jdbc");
        JNDISupport.addPropToTomcat("jdbc/UNS_DS", ds);
        JNDISupport.addPropToTomcat("APPLICATION_HOME", "target/test-classes");
    }

    @After
    public void cleanUpIC() throws Exception {
        JNDISupport.cleanUp();
    }


    /**
     * First test method.
     */
    @Test
    public void plainTemplate() {
        NotificationTemplateFacade notificationTemplateFacade = new NotificationTemplateFacade();
        NotificationTemplate notificationTemplate = notificationTemplateFacade.getNotificationTemplate(new Integer(1));
        String plainTemplate = templateMap.getProperty("template1.plain");
        notificationTemplate.setPlainText(plainTemplate);
        NotificationTemplateInterpreter nti = new NotificationTemplateInterpreter();
        String unsubscribeLink = "UNSUBSCRIBE-LINK";
        User user = new User("dummy");
        user.setFullName("Jimmy Dummy");
        Channel channel = new Channel(1);
        channel.setTitle("Channel title");
        channel.setCreator(user);
        Date creationDate = new Date(1429000000000L); // Milliseconds since January 1, 1970, 00:00:00 GMT.

        Event testEvent = new Event(12, channel, "extid", creationDate, "rtype", (byte)0);
        Map result = nti.pyhtonInterpreter(notificationTemplate, testEvent, user, unsubscribeLink);
        //System.out.println(result);
        String resultText = result.get("resultText").toString();
        String resultHtml = result.get("resultHtml").toString();
        assertTrue(resultText.contains("from the Channel title channel"));
        assertTrue(resultHtml.contains("yourself from the \"Channel title\" channel"));
    }
}
