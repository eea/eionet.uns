package com.eurodyn.uns.service.daemons.notificator;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.model.Delivery;
import com.eurodyn.uns.model.DeliveryNotification;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.service.facades.DeliveryFacade;
import com.eurodyn.uns.util.SendMail;

import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMailThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EMailThread.class);

    List notifications;

    public EMailThread(List notifications) {
        this.notifications = notifications;
    }

    public void run() {

        try{
            Map configMap = ConfigManager.getInstance().getConfigMap();
            String smtpServer = (String)((ConfigElement) configMap.get("smtpserver/smtp_host")).getValue();
            Integer smtpPort = (Integer)((ConfigElement) configMap.get("smtpserver/smtp_port")).getValue();
            String smtpUsername = (String)((ConfigElement) configMap.get("smtpserver/smtp_username")).getValue();
            String smtpPassword = (String)((ConfigElement) configMap.get("smtpserver/smtp_password")).getValue();
            String smtpSender = (String)((ConfigElement) configMap.get("pop3server/adminmail")).getValue();

            if(notifications != null){

                DeliveryFacade deliveryFacade = new DeliveryFacade();

                for(Iterator it = notifications.iterator(); it.hasNext(); ){
                    Notification notif = (Notification)it.next();

                    DeliveryType dt = new DeliveryType();
                    dt.setId(new Integer(notif.getDeliveryTypeId()));

                    Delivery delivery = new Delivery();
                    delivery.setNotification(notif);
                    delivery.setDeliveryStatus(0);
                    delivery.setDeliveryType(dt);
                    delivery.setDeliveryTime(new Date());

                    DeliveryNotification dtnotif = new DeliveryNotification();
                    dtnotif.setDeliveryType(dt);
                    dtnotif.setNotification(notif);

                    delivery.setId(dtnotif);

                    if(notif.getFailed() == 0){
                        deliveryFacade.createDelivery(delivery);
                    }

                    String to = notif.getDeliveryAddress();
                    String subj = notif.getSubject();
                    String body = notif.getContent();
                    String html = notif.getHtmlContent();
                    String id = new Integer(notif.getId()).toString();
                    try{
                        if(to != null && to.length() > 0){
                            SendMail.sendMail(to, subj, body, html, id, smtpServer, smtpPort, smtpUsername, smtpPassword, smtpSender);
                        } else {
                            LOGGER.error("Not valid e-mail address: "+to);
                        }
                    } catch(Exception e){
                        LOGGER.error("Failed to send notification ID: "+id+" to address "+to);
                        continue;
                    }

                    delivery.setDeliveryStatus(1);
                    delivery.setDeliveryTime(new Date());
                    deliveryFacade.updateDelivery(delivery);
                }
            }
        } catch(Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

}
