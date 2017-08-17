package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.ApplicationTestContext;
import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

/**
 *
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class UserUtilTest {

  /**
   * Tests if two users are different
   * @throws Exception
   */
  @Test
  public void testEqualUsers() throws Exception {
    User user1 = new User("test1");
    Map addrs1 = new HashMap();
    DeliveryType mailType = new DeliveryType(DeliveryType.EMAIL);
    DeliveryAddress addr1 = new DeliveryAddress(1, "mock@mockmail.com");
    addr1.setDeliveryType(mailType);
    addrs1.put(1, addr1);
    user1.setDeliveryAddresses(addrs1);
    user1.setDisabledFlag(false);
    user1.setFullName("Test User1");
    User user2 = new User("test2");
    Map addrs2 = new HashMap();
    addrs2.put(1, addr1);
    user2.setDeliveryAddresses(addrs2);
    user2.setFullName("Test User2");
    user2.setDisabledFlag(true);
    assertTrue("Users must not be equal", UserUtil.equalUsers(user1,user2) == false);
  }

}