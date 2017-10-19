package com.eurodyn.uns.dao.ldap;

import com.eurodyn.uns.ApplicationTestContext;
import com.eurodyn.uns.model.User;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.persist.LDAPDNField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class LdapUserDaoTest {

  private InMemoryDirectoryServer ds;
  @LDAPDNField
  private String baseDn = "o=Eionet, l=EUROPE";

  /**
   * Sets up in memory LDAP Server
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    InMemoryListenerConfig listenerConfig = new InMemoryListenerConfig("test",InetAddress.getLocalHost(), 10389, null, null, null);

    InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(baseDn);
    config.setSchema(null);
    config.addAdditionalBindCredentials("cn=Test User", "pass");

    config.setListenerConfigs(listenerConfig);
    ds = new InMemoryDirectoryServer(config);
    ds.importFromLDIF(false, getClass().getResource("/init.ldif").getPath());
    ds.startListening();
  }

  /**
   * Tests if an LDAP connection can be established and a search for users method can be completed.
   * @throws Exception
   */
  @Test
  public void testFindAllUsers() throws Exception {
    LdapUserDao ldap = mock(LdapUserDao.class);

    Hashtable env = new Hashtable();
    env.put(LdapContext.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(LdapContext.PROVIDER_URL, "ldap://" + InetAddress.getLocalHost().getHostName() + ":10389");
    env.put(LdapContext.SECURITY_AUTHENTICATION, "simple");
    env.put(LdapContext.SECURITY_PRINCIPAL, "cn=Test User");
    env.put(LdapContext.SECURITY_CREDENTIALS, "pass");
    LdapContext ctx = new InitialLdapContext(env, null);
    ctx.setRequestControls(new Control[]{
            new PagedResultsControl(50, Control.CRITICAL)
    });

    when(ldap.getPagedLdapContext()).thenReturn(ctx);
    when(ldap.findAllUsers()).thenCallRealMethod();
    List<User> users = ldap.findAllUsers();
    assertTrue("Expecting some users", users.size() > 0);
  }

  /**
   * Shuts down in memory LDAP Server
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
    ds.shutDown(true);
  }
}