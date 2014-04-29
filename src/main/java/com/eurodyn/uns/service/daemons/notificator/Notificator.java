package com.eurodyn.uns.service.daemons.notificator;

import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;

/**
 * Job that periodically initiates the sending of notifications. Implements {@link ServletContextListener}, as it is started at
 * servlet context initialization and stopped at servlet context destroy.
 *
 * @author EuroDynamics
 * @author jaanus
 */
public class Notificator implements ServletContextListener {

    /** Static logger for this class. */
    private static final WDSLogger LOGGER = WDSLogger.getLogger(Notificator.class);

    /** Job's running interval. Taken from configuration. */
    private Integer intervalSeconds;

    /** The Quartz Scheduler used for creating the job. */
    private Scheduler scheduler;

    /**
     * Utility method for starting the Quartz Scheduler job that executes the business logic.
     *
     * @param repeatInterval Job's running interval to use.
     * @throws Exception if any sort of error happens
     */
    private void start(long repeatInterval) throws Exception {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDetail jobDetail = new JobDetail("notificatorJob", null, NotificatorJob.class);

            NotificatorJobListener listener = new NotificatorJobListener();
            jobDetail.addJobListener(listener.getName());
            scheduler.addJobListener(listener);

            SimpleTrigger trigger = new SimpleTrigger(jobDetail.getName(), null);
            trigger.setRepeatInterval(repeatInterval);
            trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            throw new Exception("Error occured when processing notifications: " + e.toString());
        }
    }

    /**
     * Utility method for lazy-getting the job's running interval from configuration.
     *
     * @return the intervalSeconds
     */
    public Integer getIntervalSeconds() {
        try {
            if (intervalSeconds == null) {
                Map<String, ConfigElement> configMap = ConfigManager.getInstance().getConfigMap();
                intervalSeconds = (Integer) configMap.get("daemons/notificator/interval").getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return intervalSeconds;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        try {
            long interval = (long) getIntervalSeconds().intValue() * (long) 1000 * 60;
            start(interval);
            LOGGER.debug(getClass().getSimpleName() + " scheduled with interval minutes " + getIntervalSeconds());
        } catch (Exception e) {
            LOGGER.fatalError("Error when scheduling " + getClass().getSimpleName() + " with interval minutes "
                    + getIntervalSeconds(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

        if (scheduler != null) {
            try {
                scheduler.shutdown(false);
            } catch (SchedulerException e) {
                LOGGER.error("Failed proper shutdown of " + scheduler.getClass().getSimpleName(), e);
            }
        }
    }
}
