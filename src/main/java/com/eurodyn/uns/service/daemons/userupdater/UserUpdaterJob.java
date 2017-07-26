package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.dao.DAOException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A scheduled job to update users from LDAP Server.
 * @author George Sofianos
 */
public class UserUpdaterJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserUpdaterJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    UserUpdaterService service = new UserUpdaterServiceLdap();
    try {
      service.synchronizeUsers();
    } catch (DAOException e) {
      LOGGER.error("Error while updating users from LDAP Server: " + e);
    }
  }

}
