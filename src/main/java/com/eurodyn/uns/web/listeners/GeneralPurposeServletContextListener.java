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
 *    Original code: Dusan Popovic (ED)
 *                               Nedeljko Pavlovic (ED)
 */

package com.eurodyn.uns.web.listeners;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.eurodyn.uns.Properties;
import com.eurodyn.uns.service.facades.RoleFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * A general-purpose listener for servlet context startup and destroy. Does various things: checks app-home directories, starts
 * role synchronizer thread, etc.
 *
 * @author EuroDynamics
 * @author jaanus
 */
public class GeneralPurposeServletContextListener implements ServletContextListener, Runnable {

    /** Name of properties bundle file. */
    private static final String BUNDLE_FILE = "uns";

    /** Static logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurposeServletContextListener.class);

    /** Name of Java system property indicating if AWT headless mode should be used. */
    private static final String AWT_HEADLESS_PROPERTY = "java.awt.headless";

    /** Thread that syncs roles from LDAP periodically. */
    private Thread roleSyncronizerThread;

    /** Flag indicating if the servlet context has been destroyed. */
    private boolean contextDestroyed;

    /**
     * Public simple constructor.
     */
    public GeneralPurposeServletContextListener() {
        // No body yet.
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        // Try to detect if AWT headless mode should be used.
        try {
            String useAwtHeadless = Properties.getStringProperty(AWT_HEADLESS_PROPERTY);
            if (useAwtHeadless != null && useAwtHeadless.trim().equalsIgnoreCase("true")) {
                System.setProperty(AWT_HEADLESS_PROPERTY, "true");
            }
        } catch (Exception e) {
            LOGGER.error("Failure when trying to detect \"" + AWT_HEADLESS_PROPERTY + "\" from " + BUNDLE_FILE + ".properties", e);
        }

        // Checking existence of home directories.
        try {
            checkHomeDirectories();
        } catch (Exception e) {
            LOGGER.error("Error when checking home directories existence", e);
        }

        // Start role syncronizer thread.
        roleSyncronizerThread = new Thread(this);
        roleSyncronizerThread.start();
    }

    /**
     * Checks persistence of all application home directories needed for correct WDS work. The application home directory itself
     * must be present. The rest of directories under it will be created in case that they don't exist.
     *
     * @throws Exception general exception
     */
    private void checkHomeDirectories() throws Exception {

        String pathPrefix = Properties.getStringProperty("uns.home") + File.separatorChar;
        File log = new File(pathPrefix + "log");
        if (!log.exists()) {
            if (!log.mkdir()) {
                throw new Exception("Log directory can not be created ! You will stay out of log data !!!");
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        contextDestroyed = true;
        roleSyncronizerThread.interrupt();
        try {
            roleSyncronizerThread.join();
        } catch (InterruptedException e) {
            return;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        RoleFacade roleFacade = new RoleFacade();
        try {
            // Wait to contextInitialized.
            Thread.sleep(10 * 60 * 1000);

            // Unless context destroyed, perform role synchronization.
            while (!contextDestroyed) {

                roleFacade.synchronizeRoles();
                Thread.sleep(RoleFacade.SYNC_PERIOD);
            }
        } catch (InterruptedException e) {
            return;
        }
    }

}
