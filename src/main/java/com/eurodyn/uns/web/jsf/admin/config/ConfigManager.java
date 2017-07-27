package com.eurodyn.uns.web.jsf.admin.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.eurodyn.uns.Properties;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class for loading and serving the application's configuration.
 *
 * @author Jaanus
 */
public class ConfigManager {

    /** Static logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

    /** Indicates string-type config element. */
    private static final int STRING_TYPE = 1;

    /** Indicates integer-type config element. */
    private static final int INTEGER_TYPE = 2;

    /** Indicates boolean-type config element. */
    private static final int BOOLEAN_TYPE = 3;

    /** The name of the application's configuration file. */
    private static final String CONFIG_FILE_NAME = "uns-config.xml";

    /** The full path of the application's configuration file. This constant is used if no such path is fed in via constructor. */
    private static final String CONFIG_FILE_PATH = buildConfigFilePath();

    /** The map of properties default values. Classic key-value pairs. */
    private static final Map<String, Object> DEFAULTS = createDefaultsMap();;

    /** The class's singleton instance. */
    private static ConfigManager instance = null;

    /** The map where the configuration is loaded into. Classic key-value pairs. */
    private Map<String, ConfigElement> configMap = new HashMap<String, ConfigElement>();

    /** DOM document where the configuration is loaded into and read from. */
    private Document configDomDocument;

    /** Application's configuration file full path. Comes from {@link #CONFIG_FILE_PATH} or via constructor. */
    private String configFilePath;

    /**
     * Hide singleton constructor.
     *
     * @throws Exception
     */
    private ConfigManager() throws Exception {
        /*this(CONFIG_FILE_PATH);*/
        loadConfigElements();
    }

    /**
     * Singleton constructor, taking the config file path dynamically.
     *
     * @throws Exception when problems with loading the configuration from file.
     */
    protected ConfigManager(String configFilePath) throws Exception {

        if (StringUtils.isBlank(configFilePath)) {
            throw new IllegalArgumentException("Config file path must not blank!");
        }

        this.configFilePath = configFilePath;
        loadConfigElements();
    }

    /**
     * Returns the singleton instance, creating it on first-time call.
     *
     * @return the singleton instance.
     * @throws Exception if any sort of errors happen at the singleton creation and configuration loading from file
     */
    public static synchronized ConfigManager getInstance() throws Exception {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Helper method for initializing the confiuration's DOM document.
     *
     * @throws Exception If any sort of error happens in the process.
     */
    private void initDoc() throws Exception {

/*        SAXReader xmlReader = new SAXReader();
        this.configDomDocument = xmlReader.read(configFilePath);*/

    }

    /**
     * Loads configuration from the XML file and places it into {@link #configMap}.
     *
     * @throws Exception if any sort of error happens in the process
     */
    private void loadConfigElements() throws Exception {

        // Initialize the configuration's DOM document.
        /*initDoc();*/

        // Load general configuration elements.
        String generalPath = "//uns/daemons/notificator/";
        configMap.put(createKey(generalPath, "interval"), createValue(generalPath, "notificator.intervalMinutes", INTEGER_TYPE));
        generalPath = "//uns/daemons/harvester/";
        configMap.put(createKey(generalPath, "interval"), createValue(generalPath, "harvester.intervalMinutes", INTEGER_TYPE));
        configMap.put(createKey(generalPath, "pull_threads"), createValue(generalPath, "harvester.noOfThreads", INTEGER_TYPE));
        generalPath = "//uns/feed/";
        configMap.put(createKey(generalPath, "events_feed_age"), createValue(generalPath, "events.feedAge", INTEGER_TYPE));

        // Load LDAP configuration elements.
        /*ResourceBundle ldapProps = ResourceBundle.getBundle("eionetdir");*/
        configMap.put("ldap.url", new ConfigElement("ldap.url", Properties.getStringProperty("ldap.url")));
        configMap.put("ldap.context", new ConfigElement("ldap.context", Properties.getStringProperty("ldap.context")));
        configMap.put("ldap.user.dir", new ConfigElement("ldap.user.dir", Properties.getStringProperty("ldap.user.dir")));
        configMap.put("ldap.attr.uid", new ConfigElement("ldap.attr.uid", Properties.getStringProperty("ldap.attr.uid")));

        // Load database elements.
        String databasePath = "//uns/dbserver/";
        configMap.put(createKey(databasePath, "host"), createValue(databasePath, "db.host", STRING_TYPE));
        configMap.put(createKey(databasePath, "port"), createValue(databasePath, "db.port", INTEGER_TYPE));
        configMap.put(createKey(databasePath, "username"), createValue(databasePath, "db.user", STRING_TYPE));
        configMap.put(createKey(databasePath, "password"), createValue(databasePath, "db.password", STRING_TYPE));
        configMap.put(createKey(databasePath, "database"), createValue(databasePath, "db.database", STRING_TYPE));
        configMap.put(createKey(databasePath, "connect_timeout"), createValue(databasePath, "db.connectTimeoutMillis", INTEGER_TYPE));

        // Load SMTP configuration elements.
        String smtpPath = "//uns/smtpserver/";
        configMap.put(createKey(smtpPath, "smtp_host"), createValue(smtpPath, "smtp.host", STRING_TYPE));
        configMap.put(createKey(smtpPath, "smtp_port"), createValue(smtpPath, "smtp.port", INTEGER_TYPE));
        configMap.put(createKey(smtpPath, "smtp_username"), createValue(smtpPath, "smtp.username", STRING_TYPE));
        configMap.put(createKey(smtpPath, "smtp_password"), createValue(smtpPath, "smtp.password", STRING_TYPE));
        configMap.put(createKey(smtpPath, "smtp_useauth"), createValue(smtpPath, "smtp.useAuthentication", BOOLEAN_TYPE));

        // Load POP3 configuration elements.
        String pop3Path = "//uns/pop3server/";
        configMap.put(createKey(pop3Path, "pop3_host"), createValue(pop3Path, "pop3.host", STRING_TYPE));
        configMap.put(createKey(pop3Path, "pop3_port"), createValue(pop3Path, "pop3.port", INTEGER_TYPE));
        configMap.put(createKey(pop3Path, "pop3_username"), createValue(pop3Path, "pop3.username", STRING_TYPE));
        configMap.put(createKey(pop3Path, "pop3_password"), createValue(pop3Path, "pop3.password", STRING_TYPE));
        configMap.put(createKey(pop3Path, "adminmail"), createValue(pop3Path, "pop3.adminMail", STRING_TYPE));

        // Load JABBER configuration elements.
        String jabberPath = "//uns/jabberserver/";
        configMap.put(createKey(jabberPath, "host"), createValue(jabberPath, "jabber.host", STRING_TYPE));
        configMap.put(createKey(jabberPath, "port"), createValue(jabberPath, "jabber.port", STRING_TYPE));
        configMap.put(createKey(jabberPath, "username"), createValue(jabberPath, "jabber.username", STRING_TYPE));
        configMap.put(createKey(jabberPath, "password"), createValue(jabberPath, "jabber.password", STRING_TYPE));
        configMap.put(createKey(jabberPath, "usessl"), createValue(jabberPath, "jabber.useSSL", BOOLEAN_TYPE));
        configMap.put(createKey(jabberPath, "jabber_message_type"), createValue(jabberPath, "jabber.message_type", STRING_TYPE));
    }

    /**
     * Utility method for creating full key path from given DOM path and config element name.
     *
     * @param domPath The DOM path.
     * @param elemName The element name.
     * @return The key's full path.
     */
    private String createKey(String domPath, String elemName) {
        return domPath.substring(6) + elemName;
    }


    private ConfigElement createValue(String domPath, String elemName, int valueType) {
        String fullKeyPath = domPath + elemName;
        String strValue = Properties.getStringProperty(elemName);
        Object objValue = null;

        switch (valueType) {
            case STRING_TYPE:
                objValue = strValue == null ? null : strValue;
                break;
            case INTEGER_TYPE:
                try {
                    objValue = Integer.valueOf(strValue);
                } catch (NumberFormatException e) {
                    objValue = null;
                }
                break;
            case BOOLEAN_TYPE:
                objValue = BooleanUtils.toBoolean(strValue);
                break;
            default:
                break;
        }

        if (objValue == null) {
            throw new RuntimeException("Found no value for this configuration property: " + elemName);
        }

        return new ConfigElement(fullKeyPath, objValue);
    }

    /**
     * Utility method for creating config element value for the given domPath+elementName, expecting the given value type.
     *
     * @param domPath The DOM path.
     * @param elemName The element name.
     * @param valueType Expected value type (one of {@link #STRING_TYPE}, {@link #INTEGER_TYPE}, {@link #BOOLEAN_TYPE}).
     * @return The config element value.
     */
/*    private ConfigElement createValue(String domPath, String elemName, int valueType) {

        String fullKeyPath = domPath + elemName;
        String strValue = configDomDocument.selectSingleNode(fullKeyPath).getStringValue();
        Object objValue = null;

        switch (valueType) {
            case STRING_TYPE:
                objValue = strValue == null ? DEFAULTS.get(fullKeyPath) : strValue;
                break;
            case INTEGER_TYPE:
                try {
                    objValue = Integer.valueOf(strValue);
                } catch (NumberFormatException e) {
                    objValue = DEFAULTS.get(fullKeyPath);
                }
                break;
            case BOOLEAN_TYPE:
                objValue = BooleanUtils.toBoolean(strValue);
                break;
            default:
                break;
        }

        if (objValue == null) {
            throw new RuntimeException("Found no value for this configuration property: " + fullKeyPath);
        }

        return new ConfigElement(fullKeyPath, objValue);
    }*/

    /**
     * Returns the loaded configuration map of this instance.
     *
     * @return The map.
     */
    public Map<String, ConfigElement> getConfigMap() {
        return this.configMap;
    }

    /**
     * Construct and return configuration file full path.
     *
     * @return The result.
     */
    private static String buildConfigFilePath() {

        String result = null;
        try {
            String appHome = Properties.getStringProperty("uns.home");
            result = appHome + File.separatorChar + CONFIG_FILE_NAME;
        } catch (Exception e) {
            LOGGER.error("Unable to construct config file path", e);
        }
        return result;
    }

    /**
     * Creates the map of properties default values. There may be none, but if so then returns at least an empty map.
     *
     * @return The map.
     */
    private static Map<String, Object> createDefaultsMap() {

        // NB! Make sure you add the default value in proper type. i.e. for integer property use Integer, etc.
        HashMap<String, Object> map = new HashMap<String, Object>();
        return map;
    }
}
