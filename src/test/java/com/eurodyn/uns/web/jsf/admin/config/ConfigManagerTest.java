package com.eurodyn.uns.web.jsf.admin.config;

import java.io.File;
import java.util.Map;
import com.eurodyn.uns.ApplicationTestContext;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link ConfigManager}.
 *
 * @author Jaanus
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class ConfigManagerTest {

    /** Configuration's test file name. */
    private static final String TEST_UNS_CONFIG_XML = "test-uns-config.xml";

    /** Configuration's test file path for {@link ConfigManager}. */
    private String configFilePath;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
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
