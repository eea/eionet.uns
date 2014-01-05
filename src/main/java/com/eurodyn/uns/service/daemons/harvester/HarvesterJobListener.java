package com.eurodyn.uns.service.daemons.harvester;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class HarvesterJobListener implements JobListener {
    /** */
    private static Log logger = LogFactory.getLog(HarvesterJobListener.class);

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
        logger.info("Execution vetoed for job " + context.getJobDetail().getName());
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    public void jobToBeExecuted(JobExecutionContext context) {
        logger.info("HARVESTER PROCESS STARTED");
        //JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {

        //JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        logger.info("HARVESTER PROCESS COMPLETED");

        if (exception!=null) {
            logger.error("Exception thrown when executing job " + context.getJobDetail().getName() + ": " + exception.toString(), exception);
            return;
        }
    }
}
