package com.eurodyn.uns.web.jsf.admin.config;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * Unit tests for the {@link ConfigManager}.
 *
 * @author Jaanus
 */
public class ConfigManagerTest extends TestCase {

    /** Configuration's test file name. */
    private static final String TEST_UNS_CONFIG_XML = "test-uns-config.xml";

    /** Configuration's test file path for {@link ConfigManager}. */
    private String configFilePath;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        configFilePath = new File(getClass().getClassLoader().getResource(TEST_UNS_CONFIG_XML).getFile()).toString();
        if (StringUtils.isBlank(configFilePath)) {
            throw new Exception("Was unable to detect config file path for " + ConfigManager.class.getSimpleName());
        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {

        Map<String, ConfigElement> configMap = new ConfigManager(configFilePath).getConfigMap();
        int intervalMinutes = ((Integer) configMap.get("daemons/harvester/interval").getValue()).intValue();
        assertEquals(3, intervalMinutes);

        String jabberPwd = configMap.get("jabberserver/password").getValue().toString();
        assertEquals("sasacc", jabberPwd);

        boolean smtpUseAuth = Boolean.parseBoolean(configMap.get("jabberserver/password").getValue().toString());
        assertFalse(smtpUseAuth);
    }
}
