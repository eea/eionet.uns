package com.eurodyn.uns.service.daemons.userupdater;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 *
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

      //TODO - Change to CronTrigger for production
      SimpleTrigger trigger = newTrigger()
              .withIdentity("userTrigger", "users")
              .withSchedule(simpleSchedule()
                      //.withIntervalInHours(24)
                      .withIntervalInMinutes(1)
                      .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY))
              .build();
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
      }
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }
}
