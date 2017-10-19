package com.eurodyn.uns.configuration;

import com.eurodyn.uns.Properties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 *
 */
@Component
@DependsOn("configurationPostProcessor")
public class FilesystemInitializationBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemInitializationBean.class);
    private String appHome = Properties.getStringProperty("uns.home");

    @PostConstruct
    public void init() throws URISyntaxException, IOException {
        initAclDirectory();
        initXslDirectory();
        initEmptyDirectories();
        initPythonDirectory();
    }

    /**
     * This method is not being used at the moment. The Services file is being read from the classpath
     * @throws IOException
     * @throws URISyntaxException
     */
    private void copyXmlrpcFiles() throws IOException, URISyntaxException {
        URL xmlrpcServicesURL = this.getClass().getClassLoader().getResource("UNSServices.xml");
        File xmlrpcServicesFile = new File(xmlrpcServicesURL.toURI());
        copyFile(xmlrpcServicesURL, appHome + "/" + xmlrpcServicesFile.getName());
    }

    private void initPythonDirectory() throws URISyntaxException, IOException {
        String pythonDirectory = Properties.getStringProperty("uns_python_source.home");
        URL sourceURL = this.getClass().getClassLoader().getResource("python/");

        File sourceDirectory = new File(sourceURL.toURI());
        File destDirectory = new File(pythonDirectory);
        FileUtils.copyDirectory(sourceDirectory, destDirectory);
    }

    private void initAclDirectory() throws URISyntaxException, IOException {
        URL sourceURL = this.getClass().getClassLoader().getResource("acl/");
        String target  = appHome + "/acl/";

        File sourceFolder = new File(sourceURL.toURI());

        File[] files = sourceFolder.listFiles();
        // FIXME this doesn't work as intended, permission files don't get replaced
        // Fix for XMLCONV - copy code from dd
        for (File file:files) {
            if (file.getName().contains(".prms") || file.getName().contains(".permissions") || !((new File(target + (file.getName())).exists()))) {
                copyFile(file.toURI().toURL(), target + (file.getName()));
            }
        }
    }

    private void initXslDirectory() throws URISyntaxException, IOException {
        String target = appHome + "/xsl/";
        URL sourceURL = this.getClass().getClassLoader().getResource("xsl/");

        File sourceFolder = new File(sourceURL.toURI());

        File[] files = sourceFolder.listFiles();
        for (File file:files) {
            copyFile(file.toURI().toURL(), target + (file.getName()));
        }
    }

    private void initEmptyDirectories() {
        String pythonHome = appHome + Properties.getStringProperty("uns_python_source.home");
        String[] folders = {pythonHome};
        for (String folder : folders) {
            File f = new File(appHome + folder);
            if (!f.isDirectory()) {
                if (!f.mkdirs()) {
                    LOGGER.warn("Could not create folder: " + f.getAbsolutePath());
                }
            }
        }
    }



    /**
     * Copy file to target location
     * @param source source url
     * @param target target location
     * @throws IOException - If the file copy fails.
     * @throws URISyntaxException - If the file URL is wrong.
     */
    private void copyFile(URL source, String target) throws IOException, URISyntaxException {
        File sourceFile = new File(source.toURI());
        File targetDirectory = new File(target);
        org.apache.commons.io.FileUtils.copyFile(sourceFile, targetDirectory);
        LOGGER.info("Successfully copied file...{0}", target);
    }
}
