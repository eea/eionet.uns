import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.Ignore;
import org.junit.Test;

import com.eurodyn.uns.util.common.UnsProperties;
import com.sun.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for external interface connections.
 */
public class TestConfigUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfigUpdater.class);

    /**
     * First test method.
     */
    @Test
    @Ignore
    public void test() {
        // TODO Rewrite this class to have proper tests. Right now it's just a main() method that doesn't check much.
        // @Ignore to prevent the test phase from failing until the tests have been written.
    }

    /**
     * Main executable method.
     *
     * @param args From command line.
     */
    public static void main(String[] args) {
        try {
            // testChangeValue();
            // testJabberConnection();
            // testSMTPConnection();
            // testPop3Connection();
            // testDbConnection();
            testLdapConnection();
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }

    }

    /**
     *
     */
    public static void testJabberConnection() {

        try {
            System.out.println("Enter ");
            XMPPConnection conn1 = new XMPPConnection("jabber.eurodyn.com", 5222);
            XMPPConnection connection = new SSLXMPPConnection("jabber.eionet.europa.eu", 5223);
            System.out.println("It works ");
        } catch (XMPPException e) {
            LOGGER.error("Error", e);
        }
        System.out.println("Exit");
    }

    /**
     *
     */
    public static void testSMTPConnection() {

        try {
            System.out.println("Enter ");
            Properties props = new Properties();
            // props.put("mail.smtp.host", );
            //
            // props.put("mail.smtp.auth", "true");

            props.put("mail.smtp.host", "192.168.0.1");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", "" + 25);
            props.put("mail.smtp.auth", "true");

            SmtpAuthenticator auth = new SmtpAuthenticator("sasam", "sasacc");
            Session mailSession = Session.getDefaultInstance(props, auth);

            // Get a Session object

            mailSession.setDebug(true);
            SMTPTransport t = (SMTPTransport) mailSession.getTransport("smtp");
            t.connect();

            System.out.println("It works ");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        System.out.println("Exit");
    }

    /**
     *
     */
    public static void testPop3Connection() {

        try {
            System.out.println("Enter ");
            Properties props = new Properties();
            // props.put("mail.smtp.host", );
            //
            // props.put("mail.smtp.auth", "true");

            props.put("mail.pop3.host", "192.168.0.1");
            props.put("mail.pop3.port", "110");

            SmtpAuthenticator auth = new SmtpAuthenticator("sasam", "sasacc");
            Session mailSession = Session.getDefaultInstance(props, auth);

            // Get a Session object

            mailSession.setDebug(true);
            // SMTPTransport t = (SMTPTransport) mailSession.getTransport("smtp");
            // t.connect();

            // Store store = session.getStore("imap");
            Store store = mailSession.getStore("pop3");
            store.connect();

            System.out.println("It works ");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        System.out.println("Exit");
    }

    /**
     *
     */
    public static void testDbConnection() {

        try {
            System.out.println("Enter ");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://reportnet:3306/UNS2_DEV";
            Connection con = DriverManager.getConnection(url, "uns_developer", "reportnet");
            // } catch (UnknownHostException uhe) {
            // // TODO Auto-generated catch block
            // //LOGGER.error(e.getMessage(), e)
            // //System.out.println("se.getSQLState()" + se.getSQLState());;
            // //System.out.println("se.getErrorCode()" + se.getErrorCode());
            //
        } catch (SQLException se) {
            // TODO Auto-generated catch block
            // se.getSQLState();
            switch (se.getErrorCode()) {
                case 0:
                    System.out.println("Bad host or port ");
                    break;
                case 1044:
                    System.out.println("Unknown database ");
                    break;
                case 1045:
                    System.out.println("Bad username or password  ");
                    break;

                default:
                    break;
            }
            System.out.println(" se " + se.getErrorCode());
            // System.out.println("se.getSQLState()" + se.getSQLState());;
            // System.out.println("se.getErrorCode()" + se.getErrorCode());
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        System.out.println("Exit");
    }

    /**
     *
     */
    public static void testLdapConnection() {

        try {
            System.out.println("Enter");
            // ResourceBundle ldapProps = AppConfigurator.getInstance().getBoundle("eionetdir");
            ResourceBundle ldapProps = ResourceBundle.getBundle("eionetdir");

            String ldapUrl = ldapProps.getString("ldap.url");
            String ldapContext = ldapProps.getString("ldap.context");
            String ldapUserDir = ldapProps.getString("ldap.user.dir");
            String ldapAttrUid = ldapProps.getString("ldap.attr.uid");

            System.out.println("ldapUrl is " + ldapUrl);
            // ldapUrl = "ldap://ldap.reportnet.eurodyn.com:389";
            // ldapUrl is ldap://ldap.eionet.europa.eu:389/

            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            DirContext ctx = new InitialDirContext(env);

            if (ctx != null) {
                ctx.close();
            }
            // ldapUrl = "ldap://ldap.reportnet.eurodyn.com:389";
            UnsProperties unsProperties = new UnsProperties();
            unsProperties.setLdapParams(ldapUrl, ldapContext, ldapUserDir, ldapAttrUid);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        System.out.println("Exit");
    }

}

<<<<<<< HEAD:src/test/java/TestConfigUpdater.java
/**
 *
 */
class SmtpAuthenticator extends Authenticator {

    /**  */
    private PasswordAuthentication password_auth;

    /**
     * Class constructor.
     *
     * @param smtp_user
     * @param smtp_password
     */
    public SmtpAuthenticator(String smtp_user, String smtp_password) {
        password_auth = new PasswordAuthentication(smtp_user, smtp_password);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return password_auth;
    }
}
=======
>>>>>>> dockerize_86798:deprecated/test/TestConfigUpdater.java
