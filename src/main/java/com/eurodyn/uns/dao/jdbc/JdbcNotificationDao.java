package com.eurodyn.uns.dao.jdbc;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.INotificationDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Notification;
import com.eurodyn.uns.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JdbcNotificationDao extends BaseJdbcDao implements INotificationDao {

    private static final String qNewNotifications = "SELECT N.ID, N.SUBJECT, N.CONTENT, N.HTML_CONTENT, " +
            "S.CHANNEL_ID, N.EVENT_ID, N.EEA_USER_ID, SDT.DELIVERY_TYPE_ID, " +
            "(SELECT ADDRESS FROM DELIVERY_ADDRESS DA WHERE DA.EEA_USER_ID=N.EEA_USER_ID AND " +
            "DA.DELIVERY_TYPE_ID=SDT.DELIVERY_TYPE_ID) AS DELIVERY_ADDRESS, " +
            "0 AS FAILED FROM NOTIFICATION N, SUBSCRIPTION S, SUBSCRIPTION_DT SDT " +
            "WHERE NOT EXISTS (SELECT * FROM DELIVERY WHERE N.ID=NOTIFICATION_ID) " +
            "AND N.EEA_USER_ID=S.EEA_USER_ID AND N.CHANNEL_ID=S.CHANNEL_ID AND S.ID=SDT.SUBSCRIPTION_ID " +
            "ORDER BY N.ID";
    
    public List getNewNotifications() throws DAOException {
        List result = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getDatasource().getConnection();
            ps = conn.prepareStatement(qNewNotifications);
            rs = ps.executeQuery();

            if (rs.isBeforeFirst())
                result = new ArrayList();

            Notification notif;
            while (rs.next()) {
                notif = new Notification();
                notif.setId(rs.getInt("ID"));
                notif.setSubject(rs.getString("SUBJECT"));
                notif.setContent(rs.getString("CONTENT"));
                notif.setHtmlContent(rs.getString("HTML_CONTENT"));
                notif.setChannelId(rs.getInt("CHANNEL_ID"));
                notif.seteventId(rs.getLong("EVENT_ID"));
                notif.setUserId(rs.getInt("EEA_USER_ID"));
                notif.setDeliveryTypeId(rs.getInt("DELIVERY_TYPE_ID"));
                notif.setDeliveryAddress(rs.getString("DELIVERY_ADDRESS"));
                notif.setFailed(rs.getInt("FAILED"));
                result.add(notif);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return result;
    }
    
    private static final String qFailedDeliveries = "SELECT N.ID, N.SUBJECT, N.CONTENT, N.HTML_CONTENT, S.CHANNEL_ID, " +
            "N.EVENT_ID, N.EEA_USER_ID, SDT.DELIVERY_TYPE_ID, " +
            "(SELECT ADDRESS FROM DELIVERY_ADDRESS DA WHERE DA.EEA_USER_ID=N.EEA_USER_ID AND " + 
            "DA.DELIVERY_TYPE_ID=SDT.DELIVERY_TYPE_ID) AS DELIVERY_ADDRESS, " +
            "1 AS FAILED FROM NOTIFICATION N, SUBSCRIPTION S, DELIVERY SDT WHERE " +
            "N.EEA_USER_ID=S.EEA_USER_ID AND N.CHANNEL_ID=S.CHANNEL_ID AND N.ID=SDT.NOTIFICATION_ID " +
            "AND SDT.DELIVERY_STATUS=0 ORDER BY N.ID";
    
    public List getFailedDeliveries() throws DAOException {
        List result = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getDatasource().getConnection();
            ps = conn.prepareStatement(qFailedDeliveries);
            rs = ps.executeQuery();

            if (rs.isBeforeFirst())
                result = new ArrayList();

            Notification notif;
            while (rs.next()) {
                notif = new Notification();
                notif.setId(rs.getInt("ID"));
                notif.setSubject(rs.getString("SUBJECT"));
                notif.setContent(rs.getString("CONTENT"));
                notif.setHtmlContent(rs.getString("HTML_CONTENT"));
                notif.setChannelId(rs.getInt("CHANNEL_ID"));
                notif.seteventId(rs.getLong("EVENT_ID"));
                notif.setUserId(rs.getInt("EEA_USER_ID"));
                notif.setDeliveryTypeId(rs.getInt("DELIVERY_TYPE_ID"));
                notif.setDeliveryAddress(rs.getString("DELIVERY_ADDRESS"));
                notif.setFailed(rs.getInt("FAILED"));
                result.add(notif);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return result;
    }

    public void createNotification(Notification notification)
            throws DAOException {
    }

    public List getFailedNotifications() throws DAOException {
        return null;
    }

    public List getNotificationsThroughput(Date fromDate, Date toDate,
            Channel channel, User user) throws DAOException {
        return null;
    }

    @Override
    public List<Notification> getNotifications(Date fromDate, User user, Notification example) {
        return null;
    }
}
