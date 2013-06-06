package com.eurodyn.uns.service.daemons.notificator;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.GoogleTalkConnection;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import com.eurodyn.uns.model.Delivery;
import com.eurodyn.uns.model.DeliveryNotification;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.service.facades.DeliveryFacade;
import com.eurodyn.uns.util.common.WDSLogger;
import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;

public class JabberThread implements Runnable {
    
    private static final WDSLogger logger = WDSLogger.getLogger(JabberThread.class);
    
    List notifications;
    
    public JabberThread(List notifications) {
        this.notifications = notifications;
    }
    
    public void run() {
        
        try{
            XMPPConnection conn = null;
            
            Map configMap = ConfigManager.getInstance().getConfigMap();
            String jabberServer = (String)((ConfigElement) configMap.get("jabberserver/host")).getValue();
            Integer jabberPort = new Integer((String) ((ConfigElement) configMap.get("jabberserver/port")).getValue());
            String jabberUsername = (String)((ConfigElement) configMap.get("jabberserver/username")).getValue();
            String jabberPassword = (String)((ConfigElement) configMap.get("jabberserver/password")).getValue();
            Boolean usessl = (Boolean) ((ConfigElement) configMap.get("jabberserver/usessl")).getValue();
            String msg_type = (String)((ConfigElement) configMap.get("jabberserver/jabber_message_type")).getValue();
            
            if (usessl.booleanValue())
                conn = new SSLXMPPConnection(jabberServer, jabberPort.intValue());
            else
                conn = new XMPPConnection(jabberServer, jabberPort.intValue());

            //GoogleTalkConnection conn = new GoogleTalkConnection();
            
            conn.login(jabberUsername, jabberPassword);
            
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
                    
                    if(to != null && to.length() > 0){
                        Message message = new Message();
                        message.setTo(to);
                        message.setSubject(subj);
                        message.setBody(body);
                        message.setType(Message.Type.fromString(msg_type));
                        try {
                            conn.sendPacket(message);
                        }catch(Exception e){
                            logger.error(e.getMessage());
                            e.printStackTrace();
                            continue;
                        }
                    } else {
                        logger.error("Not valid jabber address: "+to);
                    }
                    
                    delivery.setDeliveryStatus(1);
                    delivery.setDeliveryTime(new Date());
                    deliveryFacade.updateDelivery(delivery);
                }
            }
            conn.close();
        } catch(Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
    
}
