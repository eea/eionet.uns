package com.eurodyn.uns.service.daemons.harvester;

import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class Harvester {

    private Integer intervalSeconds;

    private static final WDSLogger logger = WDSLogger.getLogger(Harvester.class);

    public Harvester() {
    }

    public void start() throws Exception {
        try {
            long repeatInterval = (long) getIntervalSeconds().intValue() * (long) 1000 * (long) 60;

            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.start();

            JobDetail jobDetail = newJob(HarvesterJob.class)
                    .withIdentity("harvesterJob").build();

            HarvesterJobListener listener = new HarvesterJobListener();
            //jobDetail.addJobListener(listener.getName());
            sched.getListenerManager().addJobListener(listener);

            SimpleTrigger trigger = newTrigger()
                    .withSchedule(simpleSchedule()
                        .withIntervalInMilliseconds(repeatInterval)
                        .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY))
                    .build();
            sched.scheduleJob(jobDetail, trigger);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Exception("Error occured when processing harvester: " + e.toString());
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    /*public void contextInitialized(ServletContextEvent servletContextEvent) {

        try{
            long interval = (long)getIntervalSeconds().intValue()*(long)1000*(long)60;
            start(interval);
            logger.debug(getClass().getSimpleName() + " scheduled with interval minutes " + getIntervalSeconds());
        }
        catch (Exception e) {
            logger.fatalError("Error when scheduling " + getClass().getSimpleName() + " with interval minutes " + getIntervalSeconds(), e);
        }
    }*/

    /**
     * @return the intervalSeconds
     */
    public Integer getIntervalSeconds() {
        try {
            if (intervalSeconds == null) {
                Map configMap = ConfigManager.getInstance().getConfigMap();
                intervalSeconds = (Integer)((ConfigElement) configMap.get("daemons/harvester/interval")).getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return intervalSeconds;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    /*public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }*/
}
