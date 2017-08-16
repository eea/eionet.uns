package com.eurodyn.uns.dao.hibernate;

import com.eurodyn.uns.ApplicationTestContext;
import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.NotificationFacade;
import com.eurodyn.uns.service.facades.UserFacade;
import com.eurodyn.uns.util.TestUtils;
import eionet.uns.DataSourceSupport;
import eionet.uns.JNDISupport;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for the Report Notifications
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class HibernateNotificationDaoTest {

  @Autowired
  private DataSource ds;

  private static Properties templateMap;

  @BeforeClass
  public static void createDataSource() throws Exception {
    templateMap = new Properties();
    InputStream inStream = HibernateNotificationDaoTest.class.getResourceAsStream("/templates.xml");
    templateMap.loadFromXML(inStream);
    inStream.close();
  }

  @Before
  public void setUpIC() throws Exception {
    TestUtils.setUpDatabase(ds, "seed-event.xml");
 /*   JNDISupport.setUpCore();
    JNDISupport.addSubCtxToTomcat("jdbc");
    JNDISupport.addPropToTomcat("jdbc/UNS_DS", ds);
    JNDISupport.addPropToTomcat("APPLICATION_HOME", "target/test-classes");*/
  }

  @After
  public void cleanUpIC() throws Exception {
    /*JNDISupport.cleanUp();*/
  }

  @Test
  public void testGetNotifications() throws DAOException {
    Calendar fromCal = new GregorianCalendar(2006,0,26);
    Date fromDate = new Date(fromCal.getTimeInMillis());
    Calendar toCal = new GregorianCalendar(2006,0,27);
    Date toDate = new Date(toCal.getTimeInMillis());

    ChannelFacade channelFacade = new ChannelFacade();
    Channel channel = channelFacade.getChannel(4);

    UserFacade userFacade = new UserFacade();
    User user = userFacade.findUser("roug");

    NotificationFacade notificationFacade = new NotificationFacade();
    Notification notification = new Notification();
    notification.setSubject("");
    List notificationsRecords = notificationFacade.getNotifications(fromDate, toDate, channel, user, notification);
    assertTrue("Notifications not found", notificationsRecords.size() > 0);
  }
}