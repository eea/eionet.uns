import java.io.InputStream;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eurodyn.uns.dao.jdbc.BaseJdbcDao;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class InitTestDB {

    public static void initTestDb() throws Exception{
            System.setProperty("hibernate-config-file","/hibernate-test.cfg.xml");
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
                if (attribute.equals("hibernate.connection.url"))
                    connectionUrl = node.getChildNodes().item(0).getNodeValue(); 
                if (attribute.equals("hibernate.connection.username"))
                    connectionUserName = node.getChildNodes().item(0).getNodeValue(); 
                if (attribute.equals("hibernate.connection.password"))
                    connectionPassword = node.getChildNodes().item(0).getNodeValue(); 

            }
                        
            MysqlDataSource mds = new MysqlDataSource();
            mds.setUrl(connectionUrl);
            mds.setUser(connectionUserName);
            mds.setPassword(connectionPassword);
            
            BaseJdbcDao.setDataSouce(mds);
            
    }

    
}
