package com.eurodyn.uns.web.listeners;

import java.text.DecimalFormat;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.eurodyn.uns.util.common.WDSLogger;


public class AppSessionListener implements HttpSessionListener {

   private static final WDSLogger logger = WDSLogger.getLogger(AppServletContextListener.class);
   private int sessionCount = 0;

   /**
    * Method called on event: session created
    * @param se HttpSessionEvent
    */
   public void sessionCreated(HttpSessionEvent se) {
      sessionCount++;
      logger.debug("New HTTP Session has been created. Sessions count = " + sessionCount);
      logMemory();
   }


   /**
    * Method called on event: session destroyed
    * @param se HttpSessionEvent
    */
   public void sessionDestroyed(HttpSessionEvent se) {
      sessionCount--;
      logger.debug("Existing HTTP Session has been destroyed. Sessions count = " + sessionCount);
      logMemory();
   }

   private void logMemory(){
      Runtime run = Runtime.getRuntime();
      long free = run.freeMemory();
      long total = run.totalMemory();
      String perc = new DecimalFormat("###.##").format(100.0*free/total);
      logger.debug("Free memory " + (free>>10) + "KB (" + perc + "%) of " + (total>>10) + "KB");      
   }
}
