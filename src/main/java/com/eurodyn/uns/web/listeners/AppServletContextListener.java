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
 *    							 Nedeljko Pavlovic (ED)
 */

package com.eurodyn.uns.web.listeners;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.eurodyn.uns.service.facades.RoleFacade;
import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.ConfiguratorException;

public class AppServletContextListener implements ServletContextListener, Runnable {

	private Thread thread;
	private boolean contextDestroyed = false;


	/**
	 * Public constuctor
	 */
	public AppServletContextListener() {
	}


	/**
	 * Method that is triggered once on start of application (context
	 * initialization):
	 * 
	 * @param servletContextEvent
	 *            ServletContextEvent
	 */
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		try {
			String hval=AppConfigurator.getInstance().getBoundle("uns").getString("java.awt.headless").trim();
			if(hval.equalsIgnoreCase("true")) {
				System.setProperty("java.awt.headless", "true");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			checkHomeDirectories();
		} catch (Exception e) {
			e.printStackTrace();
		}
		thread = new Thread(this);
		thread.start();
	}


	/**
	 * Checks persistence of all home directories needed for correct WDS work.
	 * Home directory must be present. Rest directories will be created in case
	 * that they don't exist.
	 */
	private void checkHomeDirectories() throws Exception {
		try {
			String pathPrefix = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar;
			File log = new File(pathPrefix + "log");
			if (!log.exists()) {
				if (!log.mkdir()) {
					throw new Exception("Log directory can not be created ! You will stay out of log data !!!");
				}
			}

		} catch (ConfiguratorException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Method that is triggered once on destroy of servlet context
	 * 
	 * @param servletContextEvent
	 *            ServletContextEvent
	 */
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		contextDestroyed = true;
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {

		}

	}


	public void run() {
		RoleFacade rf = new RoleFacade();

		try {
			Thread.sleep(10 * 60 * 1000); //Wait to contextInitialized
			while (!contextDestroyed) {
				rf.synchronizeRoles();
				Thread.sleep(RoleFacade.SYNC_PERIOD);
			}
		} catch (InterruptedException e) {

			return;
		}
	}

}
