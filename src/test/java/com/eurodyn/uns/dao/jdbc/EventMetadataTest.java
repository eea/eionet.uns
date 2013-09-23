package com.eurodyn.uns.dao.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eurodyn.uns.model.Channel;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;



public class EventMetadataTest extends TestCase {

    private FlatXmlDataSet loadedDataSet;

    @Override
    public void setUp() throws Exception {
        System.setProperty("hibernate-config-file", "/hibernate-test.cfg.xml");
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(
                "hibernate-test.cfg.xml");
        DOMParser parser = new DOMParser();

        parser.parse(new InputSource(in));
        Document doc = parser.getDocument();

        String connectionUrl = "";
        String connectionUserName = "";
        String connectionPassword = "";

        NodeList nodeList = doc.getElementsByTagName("property");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String attribute = node.getAttributes().getNamedItem("name").getNodeValue();

            if (attribute.equals("hibernate.connection.url")) {
                connectionUrl = node.getChildNodes().item(0).getNodeValue();
            }
            if (attribute.equals("hibernate.connection.username")) {
                connectionUserName = node.getChildNodes().item(0).getNodeValue();
            }
            if (attribute.equals("hibernate.connection.password")) {
                connectionPassword = node.getChildNodes().item(0).getNodeValue();
            }

        }

        MysqlDataSource mds = new MysqlDataSource();

        mds.setUrl(connectionUrl);
        mds.setUser(connectionUserName);
        mds.setPassword(connectionPassword);

        BaseJdbcDao.setDataSouce(mds);

        IDatabaseConnection conn = getConnection();
        IDataSet dataSet = getDataSet();

        try
        {
            DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
        }
        finally
        {
            conn.close();
        }

    }

   /**
    * Provide a connection to the database.
    */
    protected IDatabaseConnection getConnection() throws Exception {
        Connection conn = BaseJdbcDao.getDatasource().getConnection();
        return new DatabaseConnection(conn);
    }

    /**
     * Load the data which will be inserted for the test
     * seed-attributes has some fixed values
     */
    protected IDataSet getDataSet() throws Exception {
        loadedDataSet = new FlatXmlDataSet(
                getClass().getClassLoader().getResourceAsStream(
                        "seed-event.xml"));
        return loadedDataSet;
    }

    private int getLines(String table) throws Exception {
        Connection connection = BaseJdbcDao.getDatasource().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public void test_deleteOldEvents() throws Exception {
        JdbcEventMetadataDao em = new JdbcEventMetadataDao();
        assertEquals(6, getLines("DELIVERY"));
        assertEquals(2, getLines("NOTIFICATION"));
        assertEquals(2, getLines("EVENT"));
        em.deleteOldEvents();
        assertEquals(3, getLines("DELIVERY"));
        assertEquals(1, getLines("NOTIFICATION"));
        assertEquals(1, getLines("EVENT"));
    }

    public void test_findChoosableStatements() throws Exception {
        Channel channel = new Channel(4);
        JdbcEventMetadataDao em = new JdbcEventMetadataDao();
        Map res = em.findChoosableStatements(channel);
        System.out.println(res);
    }

}
