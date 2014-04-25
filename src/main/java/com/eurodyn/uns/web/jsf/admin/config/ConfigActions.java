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
 * Action bean for providing configuration values to relevant configuration pages in UI, i.e. general.jsp, database.jsp, etc.
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
