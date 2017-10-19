/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Unified Notification System
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Nedeljko Pavlovic (ED)
 */

package com.eurodyn.uns.util.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * <p>
 * Description: This utility gives to any application capablility to take its properties defined in appropriate propery files,
 * without need to have knowledge of directory location in file system.<br>
 * Higher APIs may take Property or Resource Boundle objects according to desired action they want to perform.<br>
 * Utility creates boundle for internal use by getting application_home.properties file.<br>
 * That property file will be created at installation time of application, when user chooses desired application home directory.<br>
 * Instantiation of the EurodynConfiguration class is restricted to one object - Singleton pattern.
 * </p>
 *
 * @version 1.0
 */
public class AppConfigurator {

    /** The singleton instance. */
    private static AppConfigurator instance = null;

    /** Bundles cache. */
    @SuppressWarnings("rawtypes")
    private static Map boundlesCache;

    /** Properties cache. */
    @SuppressWarnings("rawtypes")
    private static Map proportiesCache;

    /** Application home. */
    private String applicationHome;

    /**
     * Protected constructor suppresses default public constructor.
     *
     * @throws ConfiguratorException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected AppConfigurator() throws ConfiguratorException {

        String envName = "APPLICATION_HOME";
        try {
            InitialContext ic = new InitialContext();
            Context env = (Context) ic.lookup("java:comp/env");
            applicationHome = (String) env.lookup(envName);
        } catch (NamingException e) {
            if (!isJUnitRuntime()) {
                throw new ConfiguratorException("Failed looking up environment \"" + envName + "\" in java:comp/env", e);
            }
        }

        try {
            boundlesCache = Collections.synchronizedMap(new HashMap());
            proportiesCache = Collections.synchronizedMap(new HashMap());
        } catch (Exception e) {
            throw new ConfiguratorException("Failed loading default boundle: " + e.toString(), e);
        }
    }

    /**
     * Always gets sinlge same instance of AppConfigurator class if it has benn created before,
     * if not it creates instance but only on first call by first client.
     *
     * @throws ConfiguratorException
     * @return AppConfigurator Instance of AppConfigurator class.
     */
    public static AppConfigurator getInstance() throws ConfiguratorException {
        if (null == instance) {
            synchronized (AppConfigurator.class) {
                if (null == instance) {
                    try {
                        instance = new AppConfigurator();
                    } catch (ConfiguratorException ce) {
                        throw ce;
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Getter for the application home.
     *
     * @return The application home.
     */
    public String getApplicationHome() {
        return applicationHome;
    }

    /**
     * Retreives Properties by using specified property file name.
     *
     * @param name Property file name without extension.
     * @throws ConfiguratorException
     * @return Properties
     */
    @SuppressWarnings("unchecked")
    public Properties getProperties(String name) throws ConfiguratorException {

        String propsFileName = name + ".properties";
        Properties current = (Properties) proportiesCache.get(name);

        if (current == null) {

            InputStream inputStream = null;
            try {
                current = new Properties();
                if (StringUtils.isBlank(applicationHome)) {
                    inputStream = AppConfigurator.class.getClassLoader().getResourceAsStream(propsFileName);
                } else {
                    inputStream = new FileInputStream(applicationHome + File.separator + propsFileName);
                }
                current.load(inputStream);
                proportiesCache.put(name, current);
            } catch (IOException ex) {
                throw new ConfiguratorException(ex.getMessage(), ex);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

        return current;
    }

    /**
     * Retrieves ResourceBundle according to specified property file name.
     *
     * @param name Property file name without extension.
     * @throws ConfiguratorException
     * @return ResourceBundle
     */
    @SuppressWarnings("unchecked")
    public ResourceBundle getBoundle(String name) throws ConfiguratorException {

        String propsFileName = name + ".properties";
        ResourceBundle current = (ResourceBundle) boundlesCache.get(name);

        if (current == null) {

            InputStream inputStream = null;
            try {
                if (StringUtils.isBlank(applicationHome)) {
                    inputStream = AppConfigurator.class.getClassLoader().getResourceAsStream(propsFileName);
                } else {
                    inputStream = new FileInputStream(applicationHome + File.separator + propsFileName);
                }
                PropertyResourceBundle tempVar = new PropertyResourceBundle(inputStream);
                current = tempVar;
                boundlesCache.put(name, current);
            } catch (Exception ex) {
                throw new ConfiguratorException(ex.getMessage(), ex);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

        return current;
    }

    /**
     * Returns true if the current thread is running in JUnit runtime.
     *
     * @return The boolean.
     */
    public boolean isJUnitRuntime() {

        String stackTrace = ExceptionUtils.getStackTrace(new Throwable());
        return Boolean.valueOf(stackTrace.indexOf("at junit.framework.TestCase.run") > 0);
    }
}
