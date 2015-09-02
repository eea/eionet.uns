package com.eurodyn.uns.service.daemons.notificator;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.Filter;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.model.Statement;
import com.eurodyn.uns.model.Subscription;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.NotificationFacade;
import com.eurodyn.uns.service.facades.SubscriptionFacade;
import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.WDSLogger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificatorJob implements Job {

    private static final WDSLogger logger = WDSLogger.getLogger(NotificatorJob.class);

    private static ChannelFacade channelFacade = null;
    private static SubscriptionFacade subscriptionFacade = null;
    private static NotificationFacade notificationFacade = null;

    public NotificatorJob() {
        channelFacade = new ChannelFacade();
        subscriptionFacade = new SubscriptionFacade();
        notificationFacade = new NotificationFacade();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            channelFacade.unsetVacations();
            scan();
            deliver();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new JobExecutionException("Error occured when executing notification job: "+ e.toString());
        }
    }

    private void scan() throws Exception {
        try {
            logger.info("Start generating notifications");
            HashMap channels = channelFacade.findUnprocessedEvents();
            channelFacade.setProcessed();
            int i = 0;
            for (Iterator it = channels.keySet().iterator(); it.hasNext();){
                String channel_id = (String) it.next();
                Channel channel = channelFacade.getChannel(new Integer(channel_id));
                NotificationTemplate template = channel.getNotificationTemplate();
                List subscriptions = subscriptionFacade.findSubscriptionsForChannel(channel);
                List events = (List) channels.get(channel_id);
                for (Iterator it2 = events.iterator(); it2.hasNext();){
                    Event event = (Event) it2.next();
                    Map emhash = event.getEventMetadata();
                    Date eventdate = event.getCreationDate();
                    for (Iterator it3 = subscriptions.iterator(); it3.hasNext();){
                        Subscription subscription = (Subscription) it3.next();
                        User user = subscription.getUser();
                        Date subdate = subscription.getCreationDate();
                        if (subdate.before(eventdate) && !user.getVacationFlag().booleanValue() && !user.getDisabledFlag() && checkFilters(event, subscription)){
                            boolean success = generateNotification(event, subscription, template);
                            if (success){
                                i = i + 1;
                            }
                        }
                    }
                    event.setProcessed((new Integer(1)).byteValue());
                    channelFacade.updateEvent(event);
                }
            }
            logger.info("Generated " + i + " notifications");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Exception("Error occured when scanning notifications: " + e.toString());
        }
    }

    private boolean checkFilters(Event event, Subscription subscription) throws Exception {

        boolean ret = true;
        try{
            Map event_md = event.getEventMetadata();
            Set event_set = new HashSet();
            for (Iterator it = event_md.keySet().iterator(); it.hasNext();){
                String key = (String) it.next();
                EventMetadata em = (EventMetadata) event_md.get(key);
                String property = em.getProperty();
                String val = em.getValue();
                Statement stat = new Statement();
                stat.setProperty(property);
                stat.setValue(val);
                event_set.add(stat);
            }
            List filters = subscription.getFilters();
            if (event_md != null && filters != null && filters.size() > 0 && event_set.size() > 0){
                ret = false;
                for (Iterator it = filters.iterator(); it.hasNext();){
                    Filter filter = (Filter) it.next();
                    if (filter != null){
                        Set statements = filter.getStatements();
                        ret = ret || event_set.containsAll(statements);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error occured when checking filters: " + e.toString());
        }
        return ret;
    }

    private boolean generateNotification(Event event, Subscription subscription, NotificationTemplate template) throws Exception {
        boolean ret = true;
        try {
            Notification notification = new Notification();
            NotificationFacade notificationFacade = new NotificationFacade();
            String homeUrl = AppConfigurator.getInstance().getBoundle("uns").getString("home.url");
            HashMap map = PrepareText.prepare(template, event, subscription, homeUrl);

            notification.seteventId(event.getId());
            notification.setEvent(event);
            notification.setChannelId(event.getChannel().getId().intValue());
            notification.setUserId(subscription.getUser().getId().intValue());
            notification.setUser(subscription.getUser());
            notification.setSubject((String) map.get("subj"));
            notification.setContent((String) map.get("plain"));
            notification.setHtmlContent((String) map.get("html"));

            ret = notificationFacade.createNotification(notification);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            ret = false;
            throw new Exception("Error occured when trying to generate notification: " + e.toString());
        }
        return ret;
    }

    private void deliver() throws Exception {
        try{
            List email_messages = new ArrayList();
            List jabber_messages = new ArrayList();

            List new_items = notificationFacade.getNewNotifications();
            List failed_items = notificationFacade.getFailedDeliveries();
            List for_deliver = new ArrayList();
            if (new_items != null) {
                for_deliver.addAll(new_items);
            }
            if (failed_items != null) {
                for_deliver.addAll(failed_items);
            }

            for (Iterator it = for_deliver.iterator(); it.hasNext();) {
                Notification notif = (Notification) it.next();
                int dtid = notif.getDeliveryTypeId();
                if (dtid == 1) {
                    email_messages.add(notif);
                } else if (dtid == 2) {
                    jabber_messages.add(notif);
                }
            }
            logger.info("Notifications prepared. " +
                    "e-mails=" + email_messages.size() + ", jabber=" + jabber_messages.size());
            Thread emailThread = new Thread(new EMailThread(email_messages));
            Thread jabberThread = new Thread(new JabberThread(jabber_messages));

            logger.info("Start sending notifications.");
            emailThread.start();
            jabberThread.start();
            try{
                logger.info("Sending e-mails.");
                emailThread.join();
                logger.info("Done sending e-mails.");

                logger.info("Sending jabber messages.");
                jabberThread.join();
                logger.info("Done sending jabber messages.");
            } catch (InterruptedException ie){
                ie.printStackTrace();
                logger.error(ie.getMessage());
            }
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new Exception("Error occurred when delivering notifications: " + e.toString());
        }
    }


}
