package com.eurodyn.uns.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IEventMetadataDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.Event;
import com.eurodyn.uns.model.EventMetadata;
import com.eurodyn.uns.model.MetadataElement;
import com.eurodyn.uns.model.ResultDto;
import com.eurodyn.uns.model.Statement;

public class JdbcEventMetadataDao extends BaseJdbcDao implements IEventMetadataDao {

    private static final String all_choosable_statements_for_channel = "" + " (select EVENT_METADATA.PROPERTY, EVENT_METADATA.VALUE " + " from EVENT_METADATA , EVENT , CHANNEL_METADATA_ELEMENTS, METADATA_ELEMENTS " + " where EVENT_METADATA.EVENT_ID= EVENT.ID " + " and EVENT.CHANNEL_ID = ? " + " and CHANNEL_METADATA_ELEMENTS.CHANNEL_ID = EVENT.CHANNEL_ID " + " and EVENT_METADATA.PROPERTY = METADATA_ELEMENTS.NAME " + " and CHANNEL_METADATA_ELEMENTS.METADATA_ELEMENT_ID = METADATA_ELEMENTS.ID " + " and CHANNEL_METADATA_ELEMENTS.FILTERED = 1 " + ") UNION " + " (select distinct STATEMENT.PROPERTY , STATEMENT.VALUE " + " from STATEMENT,FILTER,SUBSCRIPTION,CHANNEL " + " where STATEMENT.FILTER_ID = FILTER.ID " + " and FILTER.SUBSCRIPTION_ID = SUBSCRIPTION.ID " + " and SUBSCRIPTION.CHANNEL_ID = ?  " + ") order by PROPERTY, VALUE ";
    private static final String delete_old_events = " delete from EVENT where DATE_SUB(now(), interval 60 day) > creation_date";

    public Event findEvent(Integer event_id) throws DAOException{
        return null;
    }
    
    public boolean eventExists(String extId) throws DAOException{
        return false;
    }
    
    //Delete events older than 60 days, including records from DELIVERY, NOTIFICATION and EVENT_METADATA
    public void deleteOldEvents() throws DAOException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        
        try {
            conn = getDatasource().getConnection();
            ps = conn.prepareStatement(delete_old_events);
            ps.executeUpdate();         

        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
    }
    
    public Map findChoosableStatements(Channel channel) throws DAOException {
        Map result = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = getDatasource().getConnection();
            ps = conn.prepareStatement(all_choosable_statements_for_channel);
            ps.setInt(1, channel.getId().intValue());
            ps.setInt(2, channel.getId().intValue());
            rs = ps.executeQuery();

            if (rs.isBeforeFirst())
                result = new HashMap();

            MetadataElement me;
            while (rs.next()) {
                me = new MetadataElement();
                me.setName(rs.getString("PROPERTY"));
                List values = (List) result.get(me);
                if (values == null) {
                    values = new ArrayList();
                    result.put(me, values);
                }
                values.add(rs.getString("VALUE"));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeAllResources(rs, ps, conn);
        }
        return result;
    }

    public void createEventMetadata(EventMetadata em) throws DAOException {
    }

    public void deleteEventMetadata(EventMetadata em) throws DAOException {
    }

    public void deleteEventMetadataByValue(Channel channel, String value) throws DAOException {
    }

    public void deleteEventMetadataByProperty(Channel channel, String value) throws DAOException {
    }

    public Set findChannelProperties(Channel channel) throws DAOException {
        return null;
    }

    public ResultDto findEventMetadataWithValue(Channel channel, String property, String value) throws DAOException {
        return null;
    }

    public void deleteFilterStatement(Channel channel, Statement st) throws DAOException {
        
    }
    
    public void createEvent(Event event) throws DAOException {
        
    }
}
