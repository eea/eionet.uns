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

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.*;

public class WDSLogger {
   private final static String caller = WDSLogger.class.getName();
   private Logger logger = null;

   static {
      try {
          ResourceBundle bundle =ResourceBundle.getBundle("log4j");
          Properties p = new Properties();
          Enumeration e=bundle.getKeys();
          while(e.hasMoreElements()) {
            String key = (String) e.nextElement();
            p.put(key, bundle.getObject(key));
          }
          p.put("log4j.appender.BusinessLog.File",AppConfigurator.getInstance().getApplicationHome()+"/log/uns.log");
          PropertyConfigurator.configure(p);
      } catch (Exception ex) {
          //ex.printStackTrace();
      }

   }


   /**
    *  Constructor for the WDSLogger object
    *
    * @param  logger  Logger instance.
    */
   public WDSLogger(Logger logger) {
      this.logger = logger; 
   }
   
   public boolean isDebugEnabled() {
       return logger.isDebugEnabled();
   }


   /**
    *  Gets the logger attribute of the WDSLogger class
    *
    * @param  userID      ID of user which has invoked logger.
    * @param  loggerName  Name of class wich invokes logging.
    * @return             WDSLogger instance.
    */
   public static WDSLogger getLogger(String loggerName) {
      return new WDSLogger(Logger.getLogger(loggerName));
   }



   /**
    *  Gets the logger attribute of the WDSLogger class
    *
    * @param  userID  ID of user which has invoked logger.
    * @param  classs  Class wich invokes logging.
    * @return         The logger value
    */
   public static WDSLogger getLogger(Class classs) {
      return new WDSLogger(Logger.getLogger(classs));
   }

   public void debug(Object message) {
      logger.log(caller, Level.DEBUG, message, null);
   }



   /**
    *  Inserts information message in system log file.
    *
    * @param  message  String that contains message for log.
    */
   public void info(Object message) {
      logger.log(caller, Level.INFO, message, null);
   }



   /**
    *  Description of the Method
    *
    * @param  message  String that contains message for log.
    * @param  t        Instance of some Throwable.
    */
   public void error(Object message, Throwable t) {
      logger.log(caller, Level.ERROR, message, t);
   }

   public void error(Throwable t) {
      logger.log(caller, Level.ERROR, null, t);
   }




   /**
    *  Description of the Method
    *
    * @param  message  String that contains message for log.
    */
   public void error(Object message) {
      logger.log(caller, Level.ERROR, message, null);
   }



   /**
    *  Inserts fatal error message in sstem log file with exception
    *  message.
    *
    * @param  message  String that contains message for log.
    * @param  t        Instance of some Throwable.
    */
   public void fatalError(Object message, Throwable t) {
      logger.log(caller, Level.FATAL, message, t);
   }

   public void fatalError(Throwable t) {
      logger.log(caller, Level.FATAL, null, t);
   }




   /**
    *  Inserts fatal error message in system log file without exception
    *  message.
    *
    * @param  message  String that contains message for log.
    */
   public void fatalError(Object message) {
      logger.log(caller, Level.FATAL, message, null);
   }
}
