package com.eurodyn.uns.web.jsf.admin.reports;

import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.model.User;
import com.eurodyn.uns.service.facades.ChannelFacade;
import com.eurodyn.uns.service.facades.NotificationFacade;
import com.eurodyn.uns.service.facades.UserFacade;

import com.eurodyn.uns.web.jsf.SortableTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportActions extends ReportForm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportActions.class);

    public ReportActions() {
        try {
            userFacade = new UserFacade();
            channelFacade = new ChannelFacade();
            notificationFacade = new NotificationFacade();
            channel = new Channel();
            user = new User();
            notification = new Notification();
            st = new SortableTable("realDay");
            st1 = new SortableTable("subscription.user.externalId");
            notificationsSortTable = new SortableTable("user.fullName");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
    }

    public boolean isPreparedForm() {
        try {
            if (isRenderPhase()) {
                LOGGER.debug("Reports initialisation ");
                channels = (new SortableTable("title")).sort((List) channelFacade.getChannels().get("list"));
                users = (new SortableTable("externalId")).sort(userFacade.findAllUsers());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return true;
    }

    public boolean isPreparedFailedNotifications() {
        try {
            if (isRenderPhase()) {
                if (notificationsRecords == null)
                    notificationsRecords = notificationFacade.getFailedNotifications();
                st1.sort(notificationsRecords);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }
        return true;
    }

    public String generateNotificationsReport() {
        if (channel.getId().intValue() == -1)
            channel = null;
        else
            channel = channelFacade.getChannel(channel.getId());
        if (user.getId().intValue() == -1)
            user = null;
        else
            user = userFacade.findUser(user.getId());
        try {
            notificationsRecords = notificationFacade.getNotifications(fromDate, toDate, channel, user, notification);
            notificationsSortTable.sort(notificationsRecords);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            addSystemErrorMessage();
        }
        return "notificationsReport";
    }

    public String generateTrouthputReport() {

        if (channel.getId().intValue() == -1)
            channel = null;
        else
            channel = channelFacade.getChannel(channel.getId());
        if (user.getId().intValue() == -1)
            user = null;
        else
            user = userFacade.findUser(user.getId());
        try {
            List resultList = notificationFacade.getNotificationsThroughput(fromDate, toDate, channel, user);

            ThroughputReportRecord record = new ThroughputReportRecord();
            throuthputRecords = new ArrayList();
            for (Iterator iter = resultList.iterator(); iter.hasNext();) {
                Object[] array = (Object[]) iter.next();
                if (!array[0].equals(record.getDay())) {
                    record = new ThroughputReportRecord();
                    throuthputRecords.add(record);
                    record.setDay(array[0].toString());
                }

                String deliveryType = array[1].toString();
                int status = Integer.parseInt(array[2].toString());
                int numberOfDeliveries = Integer.parseInt(array[3].toString());

                if (deliveryType.equals("EMAIL")) {
                    if (status == 0) {
                        record.setDailyEmailFailed(numberOfDeliveries);
                    } else {
                        record.setDailyEmailSuccess(numberOfDeliveries);
                    }
                } else {
                    if (status == 0) {
                        record.setDailyJabberFailed(numberOfDeliveries);
                    } else {
                        record.setDailyJabberSuccess(numberOfDeliveries);
                    }
                }

                if (status == 0)
                    totalFailed += numberOfDeliveries;
                else
                    totalSuccess += numberOfDeliveries;

            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return "notificationsThroughput";
    }


    public String unsubscribeUser() {
        try {
            User user = userFacade.findUser(subscription.getUser().getExternalId());
            user.getSubscriptions().remove(subscription.getChannel().getId());
            userFacade.updateUser(user);
            addInfoMessage(null, "messages.subscription.success.delete", new Object[] {subscription.getChannel().getTitle()});
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addSystemErrorMessage();
        }

        return null;
    }

}
