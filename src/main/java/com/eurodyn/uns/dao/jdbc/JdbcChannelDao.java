package com.eurodyn.uns.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.IChannelDao;
import com.eurodyn.uns.model.Channel;
import com.eurodyn.uns.model.User;

public class JdbcChannelDao extends BaseJdbcDao implements IChannelDao {

    private static final String q_last_harvest_date = "select LAST_HARVEST from CHANNEL where ID = ? ";

    
	public Date getLastHarvestedDate(Channel channel) throws DAOException {
		Date result = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = getDatasource().getConnection();
			ps = conn.prepareStatement(q_last_harvest_date);
			ps.setInt(1, channel.getId().intValue());
			rs = ps.executeQuery();
			rs.first();
			result = rs.getTimestamp(1);
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeAllResources(rs, ps, conn);
		}
		return result;
	}

	
	public List findAllChannels() throws DAOException {return null;}

	public List findAllChannels(String orderProperty, String order) throws DAOException { return null;}

	public List findAllChannelsByMode(String mode, String orderProperty, String order) throws DAOException { return null;}

	public List findAllChannelsByModeAndCreator(String mode, User creator, String orderProperty, String order) throws DAOException { return null;}

	public Channel findChannel(Channel channel) throws DAOException { return null;}

	public Channel findChannel(Integer id) throws DAOException { return null;}

	public Channel findChannel(String secondaryId) throws DAOException { return null;}

	public void deleteChannel(Channel channel) throws DAOException {}

	public void createChannel(Channel channel) throws DAOException {}

	public void updateChannel(Channel channel) throws DAOException {}

	public List findRpcUserChannels(User user, String orderProperty, String order) throws DAOException { return null;}

	public List findOneEventForChannel() throws DAOException { return null;}

	public Map findTestEventsForChannel(Channel channel) throws DAOException { return null;}

}
