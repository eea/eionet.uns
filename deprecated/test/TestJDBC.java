import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing the JDBC connection.
 */
public class TestJDBC {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestJDBC.class);

    /**
     * First test method.
     */
    @Test
    @Ignore
    public void test() {
        // TODO Rewrite this class to have proper tests. Right now it's just a main() method that doesn't check much.
        // @Ignore to prevent the test phase from failing until the tests have been written.
    }

    /**
     * Main executable method.
     *
     * @param args From command line.
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        TestJDBC testJDB = new TestJDBC();
        // rssReader.connect();

        try {

            Connection conn = getConnection();

            PreparedStatement pstmt = null;
            StringBuffer sql = new StringBuffer("update SUBSCRIPTION set CREATION_DATE=? where ID=51");
            pstmt = conn.prepareStatement(sql.toString());
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.setTimeZone(TimeZone.getTimeZone("UTC"));

            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);

            Calendar trt = new GregorianCalendar();
            trt.set(Calendar.DAY_OF_MONTH, day); // 0..23
            trt.set(Calendar.MONTH, month);
            trt.set(Calendar.YEAR, year);
            trt.set(Calendar.HOUR_OF_DAY, hour);
            trt.set(Calendar.MINUTE, min);
            trt.set(Calendar.SECOND, sec);

            pstmt.setTimestamp(1, new Timestamp(trt.getTimeInMillis()));
            Date a = new Date();
            pstmt.executeUpdate();
            // conn.commit();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    /**
     * Get JDBC connection.
     *
     * @return The JDBC connection.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {

            String userName = "uns_developer";
            String password = "reportnet";
            String url = "jdbc:mysql://reportnet/UNS2_DEV";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }

        return conn;
    }

    /**
     * Close all given resources. Null-safe.
     *
     * @param rs SQL result set.
     * @param pstmt SQL statement.
     * @param conn JDBC connection.
     */
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

}
