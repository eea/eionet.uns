package com.eurodyn.uns.util.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for working with UNS's configured properties.
 *
 * @author Jaanus
 */
public class UnsProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnsProperties.class);

    /** Events that UNS hasn't seen this much days in any feed shall be purged. So it's a number and the unit is days. */
    public static final int OLD_EVENTS_THRESHOLD = getOldEventsThreshold();

    /**
     * Sets LDAP properties in "eionetdir.properties", based on the given inputs.
     *
     * @param url LDAP host URL.
     * @param context LDAP context.
     * @param userDir LDAP user directory.
     * @param attrUid LDAP attribute UUID.
     * @throws Exception If any sort of error happens.
     */
    public void setLdapParams(String url, String context, String userDir, String attrUid) throws Exception {

        String filePath = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar + "eionetdir.properties";

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        StringBuffer st = new StringBuffer();

        while ((line = reader.readLine()) != null) {
            // process the line
            line = findSetProp(line, "ldap.url", url);
            line = findSetProp(line, "ldap.context", context);
            line = findSetProp(line, "ldap.user.dir", userDir);
            line = findSetProp(line, "ldap.attr.uid", attrUid);
            st.append(line);
            st.append("\n");
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        out.write(st.toString());
        out.close();
    }

    /**
     * Utility method for finding and overwriting a property in a given line.
     *
     * @param line The line.
     * @param key The property key.
     * @param value The property value.
     * @return Processed line.
     */
    private String findSetProp(String line, String key, String value) {
        if (line.startsWith(key + "=")) {
            line = key + "=" + value;
        }
        return line;
    }

    /**
     * Gets the value of OLD_EVENTS_THRESHOLD from configuration. Resolves to default (60 days) if not found.
     *
     * @return The value.
     */
    private static int getOldEventsThreshold() {

        int defaultValue = 60;

        try {
            String strValue = AppConfigurator.getInstance().getBoundle("uns").getString("oldEventsThreshold");
            System.out.println("strValue = " + strValue);
            return NumberUtils.toInt(strValue, defaultValue);
        } catch (ConfiguratorException e) {
            System.out.println("****************************************************");
            LOGGER.error(e.getMessage(), e);
            System.out.println("****************************************************");
            return defaultValue;
        }
    }
}
