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

public class TestJDBC {

    /**
     * @param args
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
                  
                  int day=c.get(Calendar.DAY_OF_MONTH);
                  int month=c.get(Calendar.MONTH);
                  int year=c.get(Calendar.YEAR);
                  int hour=c.get(Calendar.HOUR_OF_DAY);
                  int min=c.get(Calendar.MINUTE);
                  int sec=c.get(Calendar.SECOND);
                  
                  Calendar trt = new GregorianCalendar();
                  trt.set(Calendar.DAY_OF_MONTH, day);            // 0..23
                  trt.set(Calendar.MONTH, month);
                  trt.set(Calendar.YEAR, year);
                  trt.set(Calendar.HOUR_OF_DAY, hour);
                  trt.set(Calendar.MINUTE, min);
                  trt.set(Calendar.SECOND, sec);
                  
                  
                  
                  pstmt.setTimestamp(1, new Timestamp(trt.getTimeInMillis()));
                  Date a=new Date();
                  pstmt.executeUpdate();
                  //conn.commit();
                  

            


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    
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
