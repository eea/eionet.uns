package com.eurodyn.uns.service.daemons.userupdater;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * Job Listener for users update from LDAP Server
 * @author George Sofianos
 */
public class UserUpdaterJobListener implements JobListener {

  private static Log logger = LogFactory.getLog(UserUpdaterJobListener.class);

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
    logger.info("USERS UPDATING PROCESS STARTED");
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {
    logger.info("Execution vetoed for job " + jobExecutionContext.getJobDetail().getKey().getName());
  }

  @Override
  public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
    logger.info("USERS UPDATING PROCESS COMPLETED");
  }
}
