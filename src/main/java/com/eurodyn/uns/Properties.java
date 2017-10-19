package com.eurodyn.uns;

import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 */
public class Properties {

    private static final Logger LOGGER = LoggerFactory.getLogger(Properties.class);
    private static ConfigurationPropertyResolver configurationService;
    private static ConcurrentMap<String, String> concurrentMap;


    static {
        configurationService = (ConfigurationPropertyResolver) SpringApplicationContext.getBean("configurationPropertyResolver");
        concurrentMap = new ConcurrentHashMap<>();
    }
    /**
     * Gets property value from key
     * @param key Key
     * @return Value
     */
    public static String getStringProperty(String key) {
        String lookup = concurrentMap.get(key);
        if (lookup == null) {
            lookup = concurrentMap.computeIfAbsent(key, n -> {
                try {
                    return configurationService.resolveValue(key);
                } catch (UnresolvedPropertyException e) {
                    LOGGER.error(e.getMessage());
                } catch (CircularReferenceException e) {
                    LOGGER.error(e.getMessage());
                }
                return "";
            });
            if (lookup == null) {
                lookup = "";
            }
        }
        return lookup;
    }

    /**
     * Gets property numeric value from key
     * @param key Key
     * @return Value
     */
    public static int getIntProperty(String key) {
        String value = getStringProperty(key);

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException nfe) {
            LOGGER.error(nfe.getMessage());
            return 0;
        }
    }

    public static long getLongProperty(String key) {
        String value = getStringProperty(key);

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException nfe) {
            LOGGER.error(nfe.getMessage());
            return 0L;
        }
    }
}
