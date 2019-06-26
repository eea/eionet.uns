package com.eurodyn.uns.service.daemons.eventupdater;

import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.service.facades.EventMetadataFacade;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A scheduled job to delete events, metadata, deliveries and notifications older than 60 days.
 * @author Vladimiros Fotiadis
 */

public class EventUpdaterJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventUpdaterJob.class);

    private static EventMetadataFacade eventFacade = null;

    public EventUpdaterJob() {
        eventFacade = new EventMetadataFacade();
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            eventFacade.deleteOldEvents();
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new JobExecutionException("Error occurred when executing event updater job: " + e.toString());
        }
    }
}
