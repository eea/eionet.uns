package com.eurodyn.uns.service.daemons.userupdater;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

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

  private Scheduler scheduler;

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
      JobDetail job = newJob(UserUpdaterJob.class).withIdentity("userUpdater", "users").build();

      UserUpdaterJobListener listener = new UserUpdaterJobListener();
      scheduler.getListenerManager().addJobListener(listener);

      CronTrigger trigger = newTrigger()
              .withIdentity("userTrigger", "users")
              .withSchedule(cronSchedule("0 0/1 * 1/1 * ? *")).build();

      scheduler.scheduleJob(job, trigger);
      if (!scheduler.isStarted()) {
        scheduler.start();
      }
    } catch (SchedulerException e) {
      e.printStackTrace();
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
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
