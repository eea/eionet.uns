package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.dao.DAOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * A scheduled job to update users from LDAP Server
 * @author George Sofianos
 */
public class UserUpdaterJob implements Job {

  private static Log logger = LogFactory.getLog(UserUpdaterJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    UserUpdaterService service = new UserUpdaterServiceLdap();
    try {
      service.synchronizeUsers();
    } catch (DAOException e) {
      logger.error("Error while updating users from LDAP Server: " + e);
    }
  }

}
