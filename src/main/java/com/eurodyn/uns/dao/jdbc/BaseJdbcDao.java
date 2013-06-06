package com.eurodyn.uns.dao.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.eurodyn.uns.util.common.AppConfigurator;
import com.eurodyn.uns.util.common.WDSLogger;

public class BaseJdbcDao {

    private static DataSource ds = null;

    private static final WDSLogger logger = WDSLogger.getLogger(BaseJdbcDao.class);
    
    public static void setDataSouce(DataSource dataSource){
        ds = dataSource;
    }
    
    public static DataSource getDatasource() {
        try {
            if (ds == null) {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                String dataSource = AppConfigurator.getInstance().getBoundle("uns").getString("jdbcDataSource");
                ds = (DataSource) envContext.lookup("jdbc/" + dataSource);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ds;
    }

    public static void closeAllResources(ResultSet rs, Statement pstmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
            if ((conn != null) && (!conn.isClosed())) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if ((conn != null) && (!conn.isClosed())) {
                conn.close();
                conn = null;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void commit(Connection conn) {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
