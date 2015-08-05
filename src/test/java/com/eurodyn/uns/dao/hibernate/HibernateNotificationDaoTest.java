package com.eurodyn.uns.dao.hibernate;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.NotificationFacade;
import com.eurodyn.uns.service.facades.UserFacade;
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

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for the Report Notifications
 * @author George Sofianos
 */
public class HibernateNotificationDaoTest {

  private static DataSource ds;

  private static Properties templateMap;

  @BeforeClass
  public static void createDataSource() throws Exception {
    ds = DataSourceSupport.getDataSource();
    templateMap = new Properties();
    InputStream inStream = HibernateNotificationDaoTest.class.getResourceAsStream("/templates.xml");
    templateMap.loadFromXML(inStream);
    inStream.close();
    loadData("/seed-event.xml");
  }

  private static void loadData(String seedFileName) throws Exception {
    IDatabaseConnection dbConn = new DatabaseConnection(ds.getConnection());
    IDataSet dataSet = new FlatXmlDataSet(HibernateNotificationDaoTest.class.getResourceAsStream(seedFileName));
    DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
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

  @Test
  public void testGetNotifications() throws Exception {
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