package com.eurodyn.uns.service.daemons.notificator;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import com.eurodyn.uns.model.Delivery;
import com.eurodyn.uns.model.DeliveryNotification;
import com.eurodyn.uns.model.DeliveryType;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.service.facades.DeliveryFacade;

import com.eurodyn.uns.web.jsf.admin.config.ConfigElement;
import com.eurodyn.uns.web.jsf.admin.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JabberThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JabberThread.class);

    List notifications;

    public JabberThread(List notifications) {
        this.notifications = notifications;
    }

    @Override
    public void run() {

        try {
            XMPPConnection conn = null;

            Map configMap = ConfigManager.getInstance().getConfigMap();
            String jabberServer = (String) ((ConfigElement) configMap.get("jabberserver/host")).getValue();
            Integer jabberPort = new Integer((String) ((ConfigElement) configMap.get("jabberserver/port")).getValue());
            String jabberUsername = (String) ((ConfigElement) configMap.get("jabberserver/username")).getValue();
            String jabberPassword = (String) ((ConfigElement) configMap.get("jabberserver/password")).getValue();
            Boolean usessl = (Boolean) ((ConfigElement) configMap.get("jabberserver/usessl")).getValue();
            String msg_type = (String) ((ConfigElement) configMap.get("jabberserver/jabber_message_type")).getValue();

            // Do not connect of the server if host is blank!
            if (StringUtils.isNotBlank(jabberServer)) {
                if (usessl.booleanValue()) {
                    conn = new SSLXMPPConnection(jabberServer, jabberPort.intValue());
                } else {
                    conn = new XMPPConnection(jabberServer, jabberPort.intValue());
                }
                conn.login(jabberUsername, jabberPassword);
            } else {
                LOGGER.info("Jabber server URL is blank, no notifications will be sent!");
            }

            boolean debugEnabled = LOGGER.isDebugEnabled();

            if (notifications != null) {

                DeliveryFacade deliveryFacade = new DeliveryFacade();

                for (Iterator it = notifications.iterator(); it.hasNext();) {
                    Notification notif = (Notification) it.next();

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

                    if (notif.getFailed() == 0) {
                        deliveryFacade.createDelivery(delivery);
                    }

                    String to = notif.getDeliveryAddress();
                    String subj = notif.getSubject();
                    String body = notif.getContent();

                    if (to != null && to.length() > 0) {
                        Message message = new Message();
                        message.setTo(to);
                        message.setSubject(subj);
                        message.setBody(body);
                        message.setType(Message.Type.fromString(msg_type));
                        try {
                            // If connection is not null then send, otherwise log message about emulation.
                            if (conn != null) {
                                conn.sendPacket(message);
                            } else if (debugEnabled) {
                                LOGGER.debug("Skipping this message because no Jabber host specified: TO <" + to + ">, SUBJECT: "
                                        + subj);
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage());
                            continue;
                        }
                    } else {
                        LOGGER.error("Not valid jabber address: " + to);
                    }

                    delivery.setDeliveryStatus(1);
                    delivery.setDeliveryTime(new Date());
                    deliveryFacade.updateDelivery(delivery);
                }
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
