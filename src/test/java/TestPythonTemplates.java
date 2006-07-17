import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.eurodyn.uns.model.NotificationTemplate;
import com.eurodyn.uns.service.facades.NotificationTemplateFacade;
import com.eurodyn.uns.web.jsf.admin.templates.NotificationTemplateActions;

public class TestPythonTemplates {

	/**
	 * @param args
	 */
	
	
	public static String plainTemplateOld = ""
	+ "Dear <# subscription.getUser().getFullName() #> \n\n"
	+ " $EVENT \n\n"
	+ "You may unsubscribe yourself from the <# subscription.getChannel().getTitle() #> channel \n"
	+ "by using the follwing link: $UNSUSCRIBE_LINK \n\n"
	+ "Best Regards, <% print ' Soren roug' %> \n"
	+ "<% print 'European Environment Agency' %>";
	

 
	public static String plainTemplate = ""
		+ "Dear  <# subscription['user']['fullName'] #> \n\n"
		+ " $EVENT \n\n"
		+ "<% for key,value in event['metadata'].items(): print ('%s %s')% (key,value) %>"
		+ "You may unsubscribe yourself from the <# subscription['channel']['title'] #> channel \n"
		+ "by using the follwing link: $UNSUSCRIBE_LINK \n\n"
		+ "Best Regards, <% print ' Soren roug' %> \n"
		+ "<% print 'European Environment Agency' %>";
	
	
	




	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//TestRssReader rssReader = new TestRssReader();
		// rssReader.connect();

		try {
			NotificationTemplateActions notificationTemplateBean = new NotificationTemplateActions();
			NotificationTemplateFacade notificationTemplateFacade = new NotificationTemplateFacade();
			NotificationTemplate notificationTemplate = notificationTemplateFacade.getNotificationTemplate(new Integer(1));
			notificationTemplate.setPlainText(plainTemplate);
			notificationTemplateBean.setNotificationTemplate(notificationTemplate);
			notificationTemplateBean.setId(new Integer(54));
			//notificationTemplateBean.setUser(new Integer(54));
			notificationTemplateBean.test();

			


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

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
