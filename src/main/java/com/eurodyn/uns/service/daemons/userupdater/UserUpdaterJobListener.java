package com.eurodyn.uns.service.daemons.userupdater;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job Listener for users update from LDAP Server.
 * @author George Sofianos
 */
public class UserUpdaterJobListener implements JobListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserUpdaterJobListener.class);

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
    if (jobExecutionContext.getJobDetail().getKey().getName().equals("userUpdaterJob")) {
      LOGGER.info("USERS UPDATING PROCESS STARTED");
    }
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
    if (jobExecutionContext.getJobDetail().getKey().getName().equals("userUpdaterJob")) {
      LOGGER.info("Execution vetoed for job " + jobExecutionContext.getJobDetail().getKey().getName());
    }
  }

  @Override
  public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
    if (jobExecutionContext.getJobDetail().getKey().getName().equals("userUpdaterJob")) {
      LOGGER.info("USERS UPDATING PROCESS COMPLETED");
    }
  }
}
