package eionet.uns.test.util;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.apache.xerces.parsers.DOMParser;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
<<<<<<< HEAD:src/test/java/eionet/uns/test/util/UnsDatabaseTestCase.java
=======
import org.junit.Before;
>>>>>>> dockerize_86798:deprecated/test/UnsDatabaseTestCase.java
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eurodyn.uns.dao.jdbc.BaseJdbcDao;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Abstract base class for test cases that need to load some seed datasets in their setUp() method.
 *
 * @author Jaanus
 */
<<<<<<< HEAD:src/test/java/eionet/uns/test/util/UnsDatabaseTestCase.java
public abstract class UnsDatabaseTestCase extends TestCase {
=======
public abstract class UnsDatabaseTestCase {
>>>>>>> dockerize_86798:deprecated/test/UnsDatabaseTestCase.java

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
<<<<<<< HEAD:src/test/java/eionet/uns/test/util/UnsDatabaseTestCase.java
    @Override
=======
    @Before
>>>>>>> dockerize_86798:deprecated/test/UnsDatabaseTestCase.java
    public void setUp() throws Exception {
        System.setProperty("hibernate-config-file", "hibernate-test.cfg.xml");
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("hibernate-test.cfg.xml");
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
                connectionUrl = getFirstChildNodeValue(node);
            }
            if (attribute.equals("hibernate.connection.username")) {
                connectionUserName = getFirstChildNodeValue(node);
            }
            if (attribute.equals("hibernate.connection.password")) {
                connectionPassword = getFirstChildNodeValue(node);
            }

        }

        MysqlDataSource mds = new MysqlDataSource();

        mds.setUrl(connectionUrl);
        mds.setUser(connectionUserName);
        mds.setPassword(connectionPassword);

        BaseJdbcDao.setDataSouce(mds);

        IDatabaseConnection conn = getConnection();
        IDataSet dataSet = getDataSet();

        try {
            DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
        } finally {
            conn.close();
        }
    }

    /**
     * Returns the string value of the first child node of the given node.
     * Null-safe and safe when there are no child nodes actually.
     * Returns empty string when the child node value cannot be found.
     *
     * @param node The node whose first child is to be checked.
     * @return The first child node's value.
     *
     */
    private String getFirstChildNodeValue(Node node) {

        String result = null;
        if (node != null) {
            NodeList childNodes = node.getChildNodes();
            if (childNodes != null && childNodes.getLength() > 0) {
                Node firstChild = childNodes.item(0);
                if (firstChild != null) {
                    result = firstChild.getNodeValue();
                }
            }
        }

        return result == null ? "" : result;
    }

    /**
     * Provide a connection to the database.
     */
    protected IDatabaseConnection getConnection() throws Exception {
<<<<<<< HEAD:src/test/java/eionet/uns/test/util/UnsDatabaseTestCase.java
        Connection conn = BaseJdbcDao.getDatasource().getConnection();
=======
        Connection conn = BaseJdbcDao.getConnection();
>>>>>>> dockerize_86798:deprecated/test/UnsDatabaseTestCase.java
        return new DatabaseConnection(conn);
    }

    /**
     * Abstract method to be overridden by extending classes. Returns the dataset to be loaded by {@link #setUp()}.
     *
     * @return The dataset.
     * @throws Exception thrown by the implementing class.
     */
    protected abstract IDataSet getDataSet() throws Exception;

    /**
     * Close all given resources. Null-safe.
     *
     * @param rs SQL result set.
     * @param pstmt SQL statement.
     * @param conn JDBC connection.
     */
    protected void closeAllResources(ResultSet rs, Statement pstmt, Connection conn) {
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
            // Ignore deliberately.
        }
    }
}
