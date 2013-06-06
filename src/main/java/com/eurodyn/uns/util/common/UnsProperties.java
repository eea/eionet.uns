package com.eurodyn.uns.util.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class UnsProperties {

    public void setLdapParams(String url, String context, String userDir, String attrUid) throws Exception {

        String filePath = AppConfigurator.getInstance().getApplicationHome() + File.separatorChar + "eionetdir.properties";
        //String filePath = "C:/work/eclipse-workspaces/UNS2/src/main/resources" + File.separatorChar + "eionetdir.properties";

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        StringBuffer st = new StringBuffer();

        while ((line = reader.readLine()) != null) {
            // process the line
            line = findSetProp(line, "ldap.url", url);
            line = findSetProp(line, "ldap.context", context);
            line = findSetProp(line, "ldap.user.dir", userDir);
            line = findSetProp(line, "ldap.attr.uid", attrUid);
            st.append(line);
            st.append("\n");
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
        out.write(st.toString());
        out.close();
    }

    private String findSetProp(String line, String key, String value) {
        if (line.startsWith(key + "=")) {
            line = key + "=" + value;
        }
        return line;
    }

}
