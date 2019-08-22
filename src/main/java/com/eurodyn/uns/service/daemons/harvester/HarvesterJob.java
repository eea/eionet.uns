//package com.eurodyn.uns.service.daemons.harvester;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//import com.eurodyn.uns.model.Channel;
//import com.eurodyn.uns.service.facades.ChannelFacade;
//
//import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
//import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class HarvesterJob implements Job {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(HarvesterJob.class);
//
//    private static ChannelFacade channelFacade = null;
//
//    public HarvesterJob() {
//        channelFacade = new ChannelFacade();
//    }
//
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//        try {
//
//            Map configMap = ConfigManager.getInstance().getConfigMap();
//            int parallelPulls = ((Integer)((ConfigElement) configMap.get("daemons/harvester/pull_threads")).getValue()).intValue();
//            List channels = channelFacade.findHarvestChannels();
//            int channels_size = channels.size();
//            int counter = 0;
//            if (channels_size > 0) {
//                LOGGER.info("Harvesting "+channels_size+" channels");
//                int steps = channels_size / parallelPulls;
//                int remained = channels_size % parallelPulls;
//                if (steps == 0) {
//                    steps = 1;
//                    counter = remained;
//                } else {
//                    counter = parallelPulls;
//                    if(remained != 0) {
//                        steps = steps + 1;
//                    }
//                }
//                int cc = 0;
//                for (int i = 0; i < steps; i++) {
//                    List threads = new ArrayList();
//                    if (remained != 0 && remained < counter) {
//                        if(i == (steps-1)) {
//                            counter = remained;
//                        }
//                    }
//                    for (int z = 0; z < counter; z++) {
//                        Channel channel = (Channel)channels.get(cc);
//                        Thread pullerThread = new Thread(new PullerThread(channel));
//                        pullerThread.start();
//                        threads.add(pullerThread);
//                        cc += 1;
//                    }
//                    for (Iterator it = threads.iterator();it.hasNext();) {
//                        Thread thread = (Thread) it.next();
//                        thread.join();
//                    }
//                }
//
//            }
//
//        } catch(Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            throw new JobExecutionException("Error occurred when executing harvester job: " + e.toString());
//        }
//    }
//
//}
