package com.eurodyn.uns.dao.jdbc;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.model.Channel;

import eionet.uns.test.util.UnsDatabaseTestCase;

/**
 * Unit tests for actions related to notification events.
 */
public class EventMetadataTest extends UnsDatabaseTestCase {

    /**
     * Test that deletion of old events is working.
     *
     * @throws Exception Any sort of error that happens on the way.
     */
    public void test_deleteOldEvents() throws Exception {

        JdbcEventMetadataDao em = new JdbcEventMetadataDao();
        assertEquals(6, getTableLineCount("DELIVERY"));
        assertEquals(2, getTableLineCount("NOTIFICATION"));
        assertEquals(2, getTableLineCount("EVENT"));
        em.deleteOldEvents();
        assertEquals(3, getTableLineCount("DELIVERY"));
        assertEquals(1, getTableLineCount("NOTIFICATION"));
        assertEquals(1, getTableLineCount("EVENT"));
    }

    /**
     * Test that choosable statements can be found withotu exceptions.
     *
     * @throws DAOException When problem accessing choosable statements.
     */
    public void test_findChoosableStatements() throws DAOException {
        Channel channel = new Channel(4);
        JdbcEventMetadataDao em = new JdbcEventMetadataDao();
        em.findChoosableStatements(channel);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.eurodyn.uns.dao.jdbc.UnsDatabaseTestCase#getDataSet()
     */
    @SuppressWarnings("deprecation")
    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream("seed-event.xml"));
    }

    /**
     * Returns the number of rows in the given table.
     *
     * @param table Given table name.
     * @return Number of rows.
     * @throws SQLException Thrown when accessing the database.
     */
    private int getTableLineCount(String table) throws SQLException {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = BaseJdbcDao.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            closeAllResources(rs, stmt, conn);
        }
    }

}
