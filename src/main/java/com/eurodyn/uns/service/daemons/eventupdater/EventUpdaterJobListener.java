package com.eurodyn.uns.service.daemons.eventupdater;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job Listener for deleting events, metadata, deliveries and notifications older than 60 days.
 * @author Vladimiros Fotiadis
 */

public class EventUpdaterJobListener implements JobListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventUpdaterJobListener.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        if (context.getJobDetail().getKey().getName().equals("eventUpdaterJob")) {
            LOGGER.info("EVENT UPDATER PROCESS STARTED");
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        if (context.getJobDetail().getKey().getName().equals("eventUpdaterJob")) {
            LOGGER.info("Execution vetoed for job " + context.getJobDetail().getKey().getName());
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        if (context.getJobDetail().getKey().getName().equals("eventUpdaterJob")) {
            LOGGER.info("EVENT UPDATER PROCESS COMPLETED");
        }
        if (exception != null) {
            LOGGER.error("Exception thrown when executing job " + context.getJobDetail().getKey().getName() + ": " + exception.toString(), exception);
            return;
        }
    }
}
