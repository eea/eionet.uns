package com.eurodyn.uns.service.daemons.eventupdater;

import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Schedule a job for deleting events, metadata, deliveries and notifications older than 60 days.
 * @author Vladimiros Fotiadis
 */

public class EventUpdater implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventUpdater.class);

    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail job = newJob(EventUpdaterJob.class)
                    .withIdentity("eventUpdaterJob")
                    .build();
            EventUpdaterJobListener listener = new EventUpdaterJobListener();
            scheduler.getListenerManager().addJobListener(listener);

            /*
                Run updating job every night at 03:00AM  :0 0 3 1/1 * ? *
            */
            CronTrigger trigger = newTrigger()
                    .withIdentity("eventTrigger", "events")
                    .withSchedule(cronSchedule("0 0/6 * 1/1 * ? *")).build();

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
