package com.eurodyn.uns.service.daemons.notificator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.info("==================== 1");
			channelFacade.unsetVacations();
			logger.info("==================== 2");
			scan();
			logger.info("==================== 3");
			deliver();
			logger.info("==================== 4");
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new JobExecutionException("Error occured when executing notification job: "+e.toString());
		}
	}
	
	private void scan() throws Exception {
		try {
			logger.info("==================== 5");
			HashMap channels = channelFacade.findUnprocessedEvents();
			logger.info("==================== 6");
			channelFacade.setProcessed();
			logger.info("==================== 7");
			int i = 0;
			for(Iterator it = channels.keySet().iterator(); it.hasNext();){
				logger.info("==================== 8");
				String channel_id = (String)it.next();
				Channel channel = channelFacade.getChannel(new Integer(channel_id));
				logger.info("==================== 9");
				NotificationTemplate template = channel.getNotificationTemplate();
				logger.info("==================== 10");
				List subscriptions = subscriptionFacade.findSubscriptionsForChannel(channel);
				logger.info("==================== 11");
				List events = (List)channels.get(channel_id);
				logger.info("==================== 12");
				for(Iterator it2 = events.iterator();it2.hasNext();){
					logger.info("==================== 13");
					Event event = (Event)it2.next();
					logger.info("==================== EVENT ID:" +event.getId());
					Map emhash = event.getEventMetadata();
					logger.info("==================== 14");
					Date eventdate = event.getCreationDate();
					for(Iterator it3 = subscriptions.iterator(); it3.hasNext();){
						logger.info("==================== 15");
						Subscription subscription = (Subscription)it3.next();
						logger.info("==================== SUBSCRIPTION ID:" +subscription.getId());
						logger.info("==================== 25");
						User user = subscription.getUser();
						logger.info("==================== USER:" +user.getExternalId());
						logger.info("==================== 26");
						Date subdate = subscription.getCreationDate();
						logger.info("==================== 27");
						if(subdate.before(eventdate) && !user.getVacationFlag().booleanValue() && checkFilters(event, subscription)){ //TODO add checkFilters
							logger.info("==================== 28");
							boolean success = generateNotification(event, subscription, template);
							logger.info("==================== 29");
							if(success){
								logger.info("==================== 30");
								i = i + 1;
							}
						}
					}
					logger.info("==================== 17");
					event.setProcessed((new Integer(1)).byteValue());
					logger.info("==================== 18");
					channelFacade.updateEvent(event);
					logger.info("==================== 19");
				}
			}
			logger.info("Generated " +i+ " notifications");
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Exception("Error occured when scanning notifications: "+e.toString());
		}
	}
	
	private boolean checkFilters(Event event, Subscription subscription) throws Exception {
		
		boolean ret = true;
		try{
			Map event_md = event.getEventMetadata();
			logger.info("==================== 31");
			Set event_set = new HashSet();
			for(Iterator it = event_md.keySet().iterator(); it.hasNext();){
				logger.info("==================== 32");
				String key = (String)it.next();
				EventMetadata em = (EventMetadata)event_md.get(key);
				logger.info("==================== 33");
				String property = em.getProperty();
				String val = em.getValue();
				Statement stat = new Statement();
				stat.setProperty(property);
				stat.setValue(val);
				logger.info("==================== 34");
				event_set.add(stat);
				logger.info("==================== 35");
			}
			List filters = subscription.getFilters();
			logger.info("==================== 36");
			if(event_md != null && filters != null && filters.size()>0 && event_set.size() > 0){
				logger.info("==================== 37");
				ret = false;
				for(Iterator it = filters.iterator(); it.hasNext();){
					logger.info("==================== 38");
					Filter filter = (Filter) it.next();
					logger.info("==================== 39");
					if(filter != null){
						logger.info("==================== 40");
						Set statements = filter.getStatements();
						logger.info("==================== 41");
						ret = ret || event_set.containsAll(statements);
						logger.info("==================== 42");
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured when checking filters: "+e.toString());
		}
		return ret;
	}
	
	private boolean generateNotification(Event event, Subscription subscription, NotificationTemplate template) throws Exception {
		boolean ret = true;
		try {
			Notification notification = new Notification();
			NotificationFacade notificationFacade = new NotificationFacade();
			String homeUrl = AppConfigurator.getInstance().getBoundle("uns").getString("home.url");
			logger.info("==================== 61 homeUrl:" +homeUrl);
			HashMap map = PrepareText.prepare(template, event, subscription, homeUrl);
			logger.info("==================== 62");
			
			notification.seteventId(event.getId());
			notification.setEvent(event);
			notification.setChannelId(event.getChannel().getId().intValue());
			notification.setUserId(subscription.getUser().getId().intValue());
			notification.setUser(subscription.getUser());
			notification.setSubject((String)map.get("subj"));
			notification.setContent((String)map.get("plain"));
			notification.setHtmlContent((String)map.get("html"));
			
			ret = notificationFacade.createNotification(notification);
			logger.info("==================== 63");
			
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			ret = false;
			throw new Exception("Error occured when trying to generate notification: "+e.toString());
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
			if(new_items != null)
				for_deliver.addAll(new_items);
			if(failed_items != null)
				for_deliver.addAll(failed_items);
			
			for(Iterator it = for_deliver.iterator(); it.hasNext(); ){
				Notification notif = (Notification)it.next();
				int dtid = notif.getDeliveryTypeId();
				if(dtid == 1)
					email_messages.add(notif);
				else if(dtid == 2)
					jabber_messages.add(notif);
			}
			logger.info("==================== 71");
			Thread emailThread = new Thread(new EMailThread(email_messages));
			logger.info("==================== 72");
			Thread jabberThread = new Thread(new JabberThread(jabber_messages));
			logger.info("==================== 73");
			emailThread.start();
			logger.info("==================== 74");
			jabberThread.start();
			logger.info("==================== 75");
			try{
				emailThread.join();
				logger.info("==================== 76");
				jabberThread.join();
				logger.info("==================== 77");
			} catch(InterruptedException ie){
				ie.printStackTrace();
				logger.error(ie.getMessage());
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Exception("Error occured when delivering notifications: "+e.toString());
		}
	}
	

}
