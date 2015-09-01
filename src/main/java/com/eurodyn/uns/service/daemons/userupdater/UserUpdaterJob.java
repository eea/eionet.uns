package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.ldap.LdapUserDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.naming.NamingException;
import java.io.IOException;

/**
 * A scheduled job to update users from LDAP Server
 * @author George Sofianos
 */
public class UserUpdaterJob implements Job {

  private static Log logger = LogFactory.getLog(UserUpdaterJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LdapUserDao ldap = new LdapUserDao();
    try {
      ldap.updateUsers();
    } catch (NamingException e) {
      logger.error("A naming Exception occured: " + e);
    } catch (IOException e) {
      logger.error("An IO Exception occured: " + e);
    } catch (DAOException e) {
      logger.error("A DAO Exception occured: " + e);
    }
  }

}
