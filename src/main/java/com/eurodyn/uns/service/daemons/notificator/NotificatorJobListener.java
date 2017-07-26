package com.eurodyn.uns.service.daemons.notificator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.quartz.*;

import com.eurodyn.uns.service.daemons.harvester.Harvester;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificatorJobListener implements JobListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificatorJobListener.class);

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
        if (context.getJobDetail().getKey().getName().equals("notificatorJob")) {
            LOGGER.info("Execution vetoed for job " + context.getJobDetail().getKey().getName());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    public void jobToBeExecuted(JobExecutionContext context) {
        if (context.getJobDetail().getKey().getName().equals("notificatorJob")) {
            LOGGER.info("NOTIFICATOR PROCESS STARTED");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {

        if (context.getJobDetail().getKey().getName().equals("notificatorJob")) {
            LOGGER.info("NOTIFICATOR PROCESS COMPLETED");
        }

        try{
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();

            List<String> jobGroups = sched.getJobGroupNames();

            boolean exists = false;
            for (int i = 0; i < jobGroups.size(); i++) {
                Set<JobKey> jobsInGroup = sched.getJobKeys(GroupMatcher.jobGroupEquals(jobGroups.get(i)));
                for (JobKey key : jobsInGroup) {
                    if (key.getName() != null && key.getName().equals("harvesterJob")) {
                        exists = true;
                    }
                }
            }
            if (!exists) {
                Harvester harvester = new Harvester();
                harvester.start();
            }

        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }

        if (exception != null){
            LOGGER.error("Exception thrown when executing job " + context.getJobDetail().getKey().getName() + ": " + exception.toString(), exception);
            return;
        }
    }

}
