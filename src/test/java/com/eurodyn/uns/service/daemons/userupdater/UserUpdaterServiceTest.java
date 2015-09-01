package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.IUserDao;
import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.User;
import eionet.uns.DataSourceSupport;
import eionet.uns.JNDISupport;
import org.apache.commons.collections.map.HashedMap;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author George Sofianos
 */
public class UserUpdaterServiceTest {

  private static DataSource ds;

  @BeforeClass
  public static void insertData() throws Exception {
    ds = DataSourceSupport.getDataSource();
    IDatabaseConnection dbConn = new DatabaseConnection(ds.getConnection());
    IDataSet dataSet = new FlatXmlDataSet(UserUpdaterServiceTest.class.getResourceAsStream("/seed-users.xml"));
    DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet);
  }


  @Before
  public void setUp() throws Exception {
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
  public void testSynchronizeUsers() throws Exception {
    DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    IUserDao sourceDao = mock(IUserDao.class);
    IUserDao destinationDao = daoFactory.getUserDao();

    List sourceUsers = new ArrayList<User>();
    User user1 = new User("sofiageo");
    Map ua1 = new HashedMap();
    DeliveryAddress a1 = new DeliveryAddress(1, "gsf@mockmail.com");
    a1.setDeliveryType(new DeliveryType(DeliveryType.EMAIL));
    ua1.put(1, a1);
    user1.setFullName("MockName1 MockSurname");
    user1.setDeliveryAddresses(ua1);
    user1.setDisabledFlag(true);
    sourceUsers.add(user1);

    User user2 = new User("erviszyka");
    Map ua2 = new HashedMap();
    DeliveryAddress a2 = new DeliveryAddress(1, "ervzyka@mockmail.com");
    a2.setDeliveryType(new DeliveryType(DeliveryType.EMAIL));
    ua2.put(1, a2);
    user2.setDeliveryAddresses(ua2);
    user2.setFullName("MockName2 MockSurname");
    user2.setDeliveryAddresses(ua2);
    sourceUsers.add(user2);

    when(sourceDao.findAllUsers()).thenReturn(sourceUsers);

    UserUpdaterService serv = new UserUpdaterServiceLdap(sourceDao, destinationDao);
    serv.synchronizeUsers();

    user1 = destinationDao.findUser("sofiageo");
    user2 = destinationDao.findUser("erviszyka");

    assertEquals("Database has not been synchronized", true, user1.getDisabledFlag());
    DeliveryAddress realA1 = (DeliveryAddress) user2.getDeliveryAddresses().get(1);
    assertEquals("Database has not been synchronized", "ervzyka@mockmail.com", realA1.getAddress());
  }
}