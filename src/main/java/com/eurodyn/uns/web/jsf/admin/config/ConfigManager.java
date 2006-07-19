package com.eurodyn.uns.web.jsf.admin.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.UnsProperties;
import com.eurodyn.uns.util.common.WDSLogger;

public class ConfigManager {

	private static final WDSLogger logger = WDSLogger.getLogger(ConfigManager.class);

	private Document doc;

	private Map configMap;

	private ConfigManager() throws Exception {
		loadConfigElements();
	}

	private static ConfigManager instance = null;

	public static ConfigManager getInstance() throws Exception {
		if (null == instance) {
			synchronized (ConfigManager.class) {
				if (null == instance) {
					try {
						instance = new ConfigManager();
					} catch (Exception ce) {
						throw ce;
					}
				}
			}
		}
		return instance;
	}

	private static String configFilePath = null;

	static {
		try {
			configFilePath = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar + "uns-config.xml";
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void initDoc() throws Exception {
		SAXReader xmlReader = new SAXReader();
		// String filePath = "C:/work/eclipse-workspaces/UNS2/src/main/resources" + File.separatorChar + "uns-config.xml";
		// String filePath = "C:/work/eclipse-workspaces/UNS2/src/main/resources" + File.separatorChar + "eionetdir.properties";
		this.doc = xmlReader.read(configFilePath);

	}

	public void loadConfigElements() throws Exception {

		initDoc();
		configMap = new HashMap();
		// general elements
		String generalPath = "//uns/daemons/notificator/";
		configMap.put(createKey(generalPath, "interval"), createValue(generalPath, "interval", integerType));
		generalPath = "//uns/daemons/harvester/";
		configMap.put(createKey(generalPath, "interval"), createValue(generalPath, "interval", integerType));
		configMap.put(createKey(generalPath, "pull_threads"), createValue(generalPath, "pull_threads", integerType));
		generalPath = "//uns/feed/";
		configMap.put(createKey(generalPath, "events_feed_age"), createValue(generalPath, "events_feed_age", integerType));

		// ldap Elements
		ResourceBundle ldapProps = ResourceBundle.getBundle("eionetdir");
		configMap.put("ldap.url", new ConfigElement("ldap.url", ldapProps.getString("ldap.url")));
		configMap.put("ldap.context", new ConfigElement("ldap.context", ldapProps.getString("ldap.context")));
		configMap.put("ldap.user.dir", new ConfigElement("ldap.user.dir", ldapProps.getString("ldap.user.dir")));
		configMap.put("ldap.attr.uid", new ConfigElement("ldap.attr.uid", ldapProps.getString("ldap.attr.uid")));

		// database Elements
		String databasePath = "//uns/dbserver/";
		configMap.put(createKey(databasePath, "host"), createValue(databasePath, "host", stringType));
		configMap.put(createKey(databasePath, "port"), createValue(databasePath, "port", integerType));
		configMap.put(createKey(databasePath, "username"), createValue(databasePath, "username", stringType));
		configMap.put(createKey(databasePath, "password"), createValue(databasePath, "password", stringType));
		configMap.put(createKey(databasePath, "database"), createValue(databasePath, "database", stringType));
		configMap.put(createKey(databasePath, "connect_timeout"), createValue(databasePath, "connect_timeout", integerType));

		// smtp Elements
		String smtpPath = "//uns/smtpserver/";
		configMap.put(createKey(smtpPath, "smtp_host"), createValue(smtpPath, "smtp_host", stringType));
		configMap.put(createKey(smtpPath, "smtp_port"), createValue(smtpPath, "smtp_port", integerType));
		configMap.put(createKey(smtpPath, "smtp_username"), createValue(smtpPath, "smtp_username", stringType));
		configMap.put(createKey(smtpPath, "smtp_password"), createValue(smtpPath, "smtp_password", stringType));
		configMap.put(createKey(smtpPath, "smtp_useauth"), createValue(smtpPath, "smtp_useauth", booleanType));

		// pop3 Elements
		String pop3Path = "//uns/pop3server/";
		configMap.put(createKey(pop3Path, "pop3_host"), createValue(pop3Path, "pop3_host", stringType));
		configMap.put(createKey(pop3Path, "pop3_port"), createValue(pop3Path, "pop3_port", integerType));
		configMap.put(createKey(pop3Path, "pop3_username"), createValue(pop3Path, "pop3_username", stringType));
		configMap.put(createKey(pop3Path, "pop3_password"), createValue(pop3Path, "pop3_password", stringType));
		configMap.put(createKey(pop3Path, "adminmail"), createValue(pop3Path, "adminmail", stringType));

		String jabberPath = "//uns/jabberserver/";
		configMap.put(createKey(jabberPath, "host"), createValue(jabberPath, "host", stringType));
		configMap.put(createKey(jabberPath, "port"), createValue(jabberPath, "port", integerType));
		configMap.put(createKey(jabberPath, "username"), createValue(jabberPath, "username", stringType));
		configMap.put(createKey(jabberPath, "password"), createValue(jabberPath, "password", stringType));
		configMap.put(createKey(jabberPath, "usessl"), createValue(jabberPath, "usessl", booleanType));


	}

	private static final int stringType = 1;

	private static final int integerType = 2;

	private static final int booleanType = 3;

	private String createKey(String path, String extension) {
		return path.substring(6) + extension;
	}

	private ConfigElement createValue(String path, String extension, int type) {
		ConfigElement element = null;
		switch (type) {
		case stringType:
			element = new ConfigElement(path + extension, doc.selectSingleNode(path + extension).getStringValue());
			break;
		case integerType:
			element = new ConfigElement(path + extension, new Integer(doc.selectSingleNode(path + extension).getStringValue()));
			break;
		case booleanType:
			String value = doc.selectSingleNode(path + extension).getStringValue();
			element = new ConfigElement(path + extension, new Boolean(value.equalsIgnoreCase("on")));
			break;
		default:
			break;
		}

		return element;
	}

	public void updateConfiguration(Map newConfigMap) throws Exception {
		initDoc();
		Iterator it = newConfigMap.values().iterator();
		Map ldapMap = new HashMap();
		while (it.hasNext()) {
			ConfigElement configElement = (ConfigElement) it.next();
			configElement.setValue(configElement.getTempValue());
			Object value = configElement.getValue();
			if (value instanceof Boolean) {
				value = configElement.getValue().toString().equals("true") ? "on" : "off";
			}
			if (configElement.getPath().startsWith("ldap.")) {
				ldapMap.put(configElement.getPath(), configElement.getValue().toString());
			} else
				doc.selectSingleNode(configElement.getPath()).setText(value.toString());
		}

		OutputFormat outformat = OutputFormat.createPrettyPrint();
		outformat.setEncoding("UTF-8");
		XMLWriter writer = new XMLWriter(new FileOutputStream(configFilePath), outformat);
		writer.write(this.doc);
		writer.flush();

		UnsProperties unsProperties = new UnsProperties();
		unsProperties.setLdapParams(ldapMap.get("ldap.url").toString(), ldapMap.get("ldap.context").toString(), ldapMap.get("ldap.user.dir").toString(), ldapMap.get("ldap.attr.uid").toString());

		this.configMap = newConfigMap;

	}

	public Map getConfigMap() {
		return this.configMap;
	}

}
