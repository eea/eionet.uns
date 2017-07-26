import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Serializabler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Serializabler.class);
    public static void main(String[] args) {

        try {

            String s = null;
            String classpath = "    c:/work/eclipse-workspaces/UNS/src/main/public_html/WEB-INF/classes;" + "c:/classpath/servlet-api.jar;";
            String libPath = "c:/work/eclipse-workspaces/UNS/src/main/public_html/WEB-INF/lib";

            File dir = new File(libPath);

            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                classpath = classpath + libPath + "/" + children[i] + ";";
            }

            String className = "com.eurodyn.uns.web.servlets.SVGServlet";

            Process p = Runtime.getRuntime().exec("serialver -classpath " + classpath + " " + className);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command

            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command

            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            System.exit(0);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

}
