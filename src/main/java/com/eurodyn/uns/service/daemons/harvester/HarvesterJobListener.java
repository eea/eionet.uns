package com.eurodyn.uns.service.daemons.harvester;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvesterJobListener implements JobListener {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(HarvesterJobListener.class);

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#getName()
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    public void jobExecutionVetoed(JobExecutionContext context) {
        if (context.getJobDetail().getKey().getName().equals("harvesterJob")) {
            LOGGER.info("Execution vetoed for job " + context.getJobDetail().getKey().getName());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    public void jobToBeExecuted(JobExecutionContext context) {
        if (context.getJobDetail().getKey().getName().equals("harvesterJob")) {
            LOGGER.info("HARVESTER PROCESS STARTED");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {

        if (context.getJobDetail().getKey().getName().equals("harvesterJob")) {
            LOGGER.info("HARVESTER PROCESS COMPLETED");
        }
        if (exception != null) {
            LOGGER.error("Exception thrown when executing job " + context.getJobDetail().getKey().getName() + ": " + exception.toString(), exception);
            return;
        }
    }
}
