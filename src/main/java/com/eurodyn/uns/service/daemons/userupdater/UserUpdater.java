package com.eurodyn.uns.service.daemons.userupdater;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Schedule a job for user updating from LDAP servers.
 * @author George Sofianos
 */
public class UserUpdater implements ServletContextListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserUpdater.class);

  private Scheduler scheduler;

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
      JobDetail job = newJob(UserUpdaterJob.class).withIdentity("userUpdaterJob", "users").build();

      UserUpdaterJobListener listener = new UserUpdaterJobListener();
      scheduler.getListenerManager().addJobListener(listener);

      /*
        Run updating job every night at 02:00AM
       */
      CronTrigger trigger = newTrigger()
              .withIdentity("userTrigger", "users")
              .withSchedule(cronSchedule("0 0 2 1/1 * ? *")).build();

      scheduler.scheduleJob(job, trigger);
      if (!scheduler.isStarted()) {
        scheduler.start();
      }
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    try {
      if (!scheduler.isShutdown()) {
        scheduler.shutdown();
        Thread.sleep(1000);
      }
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
    } catch (InterruptedException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
