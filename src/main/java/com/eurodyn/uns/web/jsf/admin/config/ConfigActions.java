package com.eurodyn.uns.web.jsf.admin.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.eurodyn.uns.util.MailAuthenticator;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.BaseBean;
import com.sun.mail.smtp.SMTPTransport;

/**
 * Action bean for taking actions from admin interface's configuration pages like general.jsp, database.jsp, etc.
 * The below update*() methods are invoked by submit buttons in those configuration pages.
 *
 */
public class ConfigActions extends BaseBean {

    /** Static logger for this class. */
    private static final WDSLogger LOGGER = WDSLogger.getLogger(ConfigActions.class);

    /** Instance of {@link ConfigManager} to which the configuration updating calls are delegated. */
    private ConfigManager configManager = null;

    /** Configuration map that is updated with reflection, using input coming from user input submits. */
    @SuppressWarnings("rawtypes")
    private Map configMap = null;

    /**
     * Default constructor.
     */
    public ConfigActions() {
        try {
            configManager = ConfigManager.getInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
    }

    /**
     * Updates LDAP configuration.
     *
     * @return Always null (maybe some convention of JSF that does not allow void methods for submit handlers).
     */
    public String updateLdap() {

        String ldapUrl = null;
        try {

            ldapUrl = (String) ((ConfigElement) configMap.get("ldap.url")).getTempValue();
            // String ldapContext = (String) ((ConfigElement) configMap.get("ldap.context")).getTempValue();
            // String ldapUserDir = (String) ((ConfigElement) configMap.get("ldap.user.dir")).getTempValue();
            // String ldapAttrUid = (String) ((ConfigElement) configMap.get("ldap.attr.uid")).getTempValue();
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            DirContext ctx = new InitialDirContext(env);

            if (ctx != null) {
                ctx.close();
            }

            configManager.updateConfiguration(configMap);
            addInfoMessage(null, "msg.updateSuccess", null);
        } catch (javax.naming.CommunicationException ce) {
            addErrorMessagePlain(null, "Unable to connect " + ldapUrl);
        } catch (Exception e) {
            // logger.error(e.getMessage(), e);
            e.printStackTrace();
            addSystemErrorMessage();
        }

        return null;
    }

    /**
     * Updates database connection configuration.
     *
     * @return Always null (maybe some convention of JSF that does not allow void methods for submit handlers).
     */
    public String updateDatabase() {
        try {

            String host = (String) ((ConfigElement) configMap.get("dbserver/host")).getTempValue();
            Integer port = new Integer((String) ((ConfigElement) configMap.get("dbserver/port")).getTempValue());
            String username = (String) ((ConfigElement) configMap.get("dbserver/username")).getTempValue();
            String password = (String) ((ConfigElement) configMap.get("dbserver/password")).getTempValue();
            String database = (String) ((ConfigElement) configMap.get("dbserver/database")).getTempValue();

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            Connection con = DriverManager.getConnection(url, username, password);
            if (con != null) {
                con.close();
            }

            configManager.updateConfiguration(configMap);
            addInfoMessage(null, "msg.updateSuccess", null);
        } catch (SQLException se) {
            switch (se.getErrorCode()) {
                case 0:
                    addErrorMessagePlain(null, "Bad host or port ");
                    break;
                case 1044:
                    addErrorMessagePlain(null, "Unknown database ");
                    break;
                case 1045:
                    addErrorMessagePlain(null, "Bad username or password  ");
                    break;
                default:
                    addErrorMessagePlain(null, "Unable to connect ");
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return null;
    }

    /**
     * Updates SMTP configuration.
     *
     * @return Always null (maybe some convention of JSF that does not allow void methods for submit handlers).
     */
    public String updateSmtp() {
        try {
            String host = (String) ((ConfigElement) configMap.get("smtpserver/smtp_host")).getTempValue();
            Integer port = new Integer((String) ((ConfigElement) configMap.get("smtpserver/smtp_port")).getTempValue());
            String username = (String) ((ConfigElement) configMap.get("smtpserver/smtp_username")).getTempValue();
            String password = (String) ((ConfigElement) configMap.get("smtpserver/smtp_password")).getTempValue();
            Boolean useauth = (Boolean) ((ConfigElement) configMap.get("smtpserver/smtp_useauth")).getTempValue();

            // Test SMTP connection only when host is blank.
            if (!StringUtils.isBlank(host)) {

                Properties props = new Properties();
                props.put("mail.smtp.host", host);
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.port", port);
                props.put("mail.smtp.auth", useauth.toString());

                MailAuthenticator auth = new MailAuthenticator(username, password);
                Session mailSession = Session.getInstance(props, auth);
                // mailSession.setDebug(true);
                SMTPTransport t = (SMTPTransport) mailSession.getTransport("smtp");
                t.connect();
                returnToOriginal("pop3server");
                if (t != null) {
                    t.close();
                }
            }

            configManager.updateConfiguration(configMap);

            // Success message depends on whether the supplied SMTP host is blank or not.
            if (StringUtils.isNotBlank(host)) {
                addInfoMessage(null, "msg.updateSuccess", null);
            } else {
                addInfoMessage(null, "msg.updateSuccess.smptHostEmpty", null);
            }
        } catch (javax.mail.MessagingException e) {
            addErrorMessagePlain(null, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return null;

    }

    /**
     * Updates POP3 configuration.
     *
     * @return Always null (maybe some convention of JSF that does not allow void methods for submit handlers).
     */
    public String updatePop3() {
        try {

            String host = (String) ((ConfigElement) configMap.get("pop3server/pop3_host")).getTempValue();
            Integer port = new Integer((String) ((ConfigElement) configMap.get("pop3server/pop3_port")).getTempValue());
            String username = (String) ((ConfigElement) configMap.get("pop3server/pop3_username")).getTempValue();
            String password = (String) ((ConfigElement) configMap.get("pop3server/pop3_password")).getTempValue();

            Properties props = new Properties();
            props.put("mail.pop3.host", host);
            props.put("mail.pop3.port", port);

            MailAuthenticator auth = new MailAuthenticator(username, password);
            Session mailSession = Session.getInstance(props, auth);
            // mailSession.setDebug(true);
            Store store = mailSession.getStore("pop3");
            store.connect();
            returnToOriginal("smtpserver");
            configManager.updateConfiguration(configMap);
            addInfoMessage(null, "msg.updateSuccess", null);
            if (store != null) {
                store.close();
            }
        } catch (javax.mail.MessagingException e) {
            addErrorMessagePlain(null, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return null;

    }

    /**
     * Updates Jabber configuration.
     *
     * @return Always null (maybe some convention of JSF that does not allow void methods for submit handlers).
     */
    public String updateJabber() {

        try {
            XMPPConnection conn = null;
            String host = (String) ((ConfigElement) configMap.get("jabberserver/host")).getTempValue();
            Integer port = new Integer((String) ((ConfigElement) configMap.get("jabberserver/port")).getTempValue());
            String username = (String) ((ConfigElement) configMap.get("jabberserver/username")).getTempValue();
            String password = (String) ((ConfigElement) configMap.get("jabberserver/password")).getTempValue();
            Boolean usessl = (Boolean) ((ConfigElement) configMap.get("jabberserver/usessl")).getTempValue();

            // Test connection only if host is not blank.
            if (StringUtils.isNotBlank(host)) {
                if (usessl.booleanValue()) {
                    conn = new SSLXMPPConnection(host, port.intValue());
                } else {
                    conn = new XMPPConnection(host, port.intValue());
                }
                conn.login(username, password);
            }

            configManager.updateConfiguration(configMap);
            if (StringUtils.isNotBlank(host)) {
                addInfoMessage(null, "msg.updateSuccess", null);
            } else {
                addInfoMessage(null, "msg.updateSuccess.jabberHostEmpty", null);
            }
            if (conn != null) {
                conn.close();
            }
        } catch (XMPPException e) {
            addErrorMessagePlain(null, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return null;
    }

    /**
     * Gets the config map.
     *
     * @return the config map
     */
    @SuppressWarnings("rawtypes")
    public Map getConfigMap() {
        if (configMap == null) {
            configMap = configManager.getConfigMap();
        }
        return configMap;
    }

    /**
     * Convenience method for setting configuration element's temporary value to its current value.
     *
     * @param firstKeyPart The element's key.
     */
    @SuppressWarnings("rawtypes")
    private void returnToOriginal(String firstKeyPart) {

        Set configMapKeys = configMap.keySet();
        for (Iterator iter = configMapKeys.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (key.startsWith(firstKeyPart)) {
                ConfigElement configElement = (ConfigElement) configMap.get(key);
                configElement.setTempValue(configElement.getValue());
            }

        }
    }
}
