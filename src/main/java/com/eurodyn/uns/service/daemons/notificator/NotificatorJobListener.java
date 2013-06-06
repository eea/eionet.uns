package com.eurodyn.uns.service.daemons.notificator;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;

import com.eurodyn.uns.service.daemons.harvester.Harvester;

public class NotificatorJobListener implements JobListener {
    /** */
    private static Log logger = LogFactory.getLog(NotificatorJobListener.class);

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
        logger.info("NOTIFICATOR PROCESS STARTED");
        //JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        
        logger.info("NOTIFICATOR PROCESS COMPLETED");
        
        try{
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            
            String[] jobGroups = sched.getJobGroupNames();

            boolean exists = false;
            for (int i = 0; i < jobGroups.length; i++) {
                String[] jobsInGroup = sched.getJobNames(jobGroups[i]);
                for (int j = 0; j < jobsInGroup.length; j++) {
                    String jobName = jobsInGroup[j];
                    if(jobName != null && jobName.equals("harvesterJob")){
                        exists = true;
                    }
                }
            }
            if(!exists){
                Harvester harvester = new Harvester();
                harvester.start();
            }
            
        } catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        
        if (exception!=null){
            logger.error("Exception thrown when executing job " + context.getJobDetail().getName() + ": " + exception.toString(), exception);
            return;
        }
    }
    
}
