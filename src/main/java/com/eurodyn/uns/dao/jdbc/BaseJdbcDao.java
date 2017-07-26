package com.eurodyn.uns.dao.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


import com.eurodyn.uns.SpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseJdbcDao {

    private static DataSource ds = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJdbcDao.class);

    public static void setDataSouce(DataSource dataSource){
        ds = dataSource;
    }

    private static final DataSource DATASOURCE;

    static {
        DATASOURCE = (DataSource) SpringApplicationContext.getBean("dataSource");
    }

    public static Connection getConnection() throws SQLException {
        return DATASOURCE.getConnection();
    }
    /*public static DataSource getDatasource() {
        try {
            if (ds == null) {
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                String dataSource = AppConfigurator.getInstance().getBoundle("uns").getString("jdbcDataSource");
                ds = (DataSource) envContext.lookup("jdbc/" + dataSource);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ds;
    }*/

    public static void closeAllResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
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
            LOGGER.error(sqle.getMessage(), sqle);
        }
    }

    public static void commit(Connection conn) {
        try {
            if (conn != null) {
                conn.commit();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
