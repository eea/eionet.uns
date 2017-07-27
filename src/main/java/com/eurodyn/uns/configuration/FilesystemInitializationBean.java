package com.eurodyn.uns.configuration;

import com.eurodyn.uns.Properties;
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

    @PostConstruct
    public void init() throws URISyntaxException, IOException {
        initAclDirectory();
        initXslDirectory();
        initEmptyDirectories();
    }

    private void initAclDirectory() throws URISyntaxException, IOException {
        String appHome = Properties.getStringProperty("app.home");
        appHome = appHome + "/acl/";
        URL sourceURL = this.getClass().getClassLoader().getResource("acl/");

        File sourceFolder = new File(sourceURL.toURI());

        File[] files = sourceFolder.listFiles();
        for (File file:files) {
            if (file.getName().contains(".prms") || file.getName().contains(".permissions") || !((new File(appHome + (file.getName())).exists()))) {
                copyFile(file.toURI().toURL(), appHome + (file.getName()));
            }
        }
    }

    private void initXslDirectory() throws URISyntaxException, IOException {
        String appHome = Properties.getStringProperty("app.home");
        appHome = appHome + "/xsl/";
        URL sourceURL = this.getClass().getClassLoader().getResource("xsl/");

        File sourceFolder = new File(sourceURL.toURI());

        File[] files = sourceFolder.listFiles();
        for (File file:files) {
            copyFile(file.toURI().toURL(), appHome + (file.getName()));
        }
    }

    private void initEmptyDirectories() {
        String appHome = Properties.getStringProperty("app.home");
        String pythonHome = appHome + Properties.getStringProperty("uns_python_source.home");
        String[] folders = {"/log/", pythonHome };
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
