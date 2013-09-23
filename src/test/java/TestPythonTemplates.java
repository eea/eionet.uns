import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Ignore;
import org.junit.Test;

import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.web.jsf.admin.templates.NotificationTemplateActions;

/**
 * Tests for the Python templates.
 */
public class TestPythonTemplates {

    /** Plain notification template. */
    private static final String PLAIN_TEMPLATE = "" + "Dear  <# subscription['user']['fullName'] #> \n\n" + " $EVENT \n\n"
            + "<% for key,value in event['metadata'].items(): print ('%s %s')% (key,value) %>"
            + "You may unsubscribe yourself from the <# subscription['channel']['title'] #> channel \n"
            + "by using the follwing link: $UNSUSCRIBE_LINK \n\n" + "Best Regards, <% print ' Soren roug' %> \n"
            + "<% print 'European Environment Agency' %>";

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
     * Executable main() method.
     *
     * @param args From command line.
     */
    public static void main(String[] args) {

        try {
            NotificationTemplateActions notificationTemplateBean = new NotificationTemplateActions();
            NotificationTemplateFacade notificationTemplateFacade = new NotificationTemplateFacade();
            NotificationTemplate notificationTemplate = notificationTemplateFacade.getNotificationTemplate(new Integer(1));
            notificationTemplate.setPlainText(PLAIN_TEMPLATE);
            notificationTemplateBean.setNotificationTemplate(notificationTemplate);
            notificationTemplateBean.setId(new Integer(54));
            // notificationTemplateBean.setUser(new Integer(54));
            notificationTemplateBean.test();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Get JDBC connection.
     *
     * @return The JDBC connection.
     */
    public Connection getConnection() {
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
