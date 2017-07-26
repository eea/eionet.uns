package com.eurodyn.uns.web.listeners;

import java.text.DecimalFormat;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple {@link HttpSessionListener} for recording the number of created sessions, logging memory usage, etc.
 *
 * @author EuroDynamics
 * @author jaanus
 */
public class AppSessionListener implements HttpSessionListener {

    /** Static logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AppSessionListener.class);

    /** Number of active sessions. */
    private int sessionCount = 0;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessionCount++;
        LOGGER.debug("New HTTP Session has been created. Sessions count = " + sessionCount);
        logMemory();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessionCount--;
        LOGGER.debug("Existing HTTP Session has been destroyed. Sessions count = " + sessionCount);
        logMemory();
    }

    /**
     * Utility method for logging memory usage.
     */
    private void logMemory() {
        Runtime run = Runtime.getRuntime();
        long free = run.freeMemory();
        long total = run.totalMemory();
        String perc = new DecimalFormat("###.##").format(100.0 * free / total);
        LOGGER.debug("Free memory " + (free >> 10) + "KB (" + perc + "%) of " + (total >> 10) + "KB");
    }
}
