package com.eurodyn.uns;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support functions to set up MySQL data source
 *
 */
public class DataSourceSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceSupport.class);
    private static final DataSource DATASOURCE;

    static {
        DATASOURCE = (DataSource) SpringApplicationContext.getBean("dataSource");
    }

    public static DataSource getDataSource() {
        return DATASOURCE;
    }

    /*public static DataSource getDataSource() {
        Properties props = new Properties();
        InputStream fis = null;
        BasicDataSource ds = new BasicDataSource();
        try {
            fis = DataSourceSupport.class.getClassLoader().getResourceAsStream("liquibase.properties");
            props.load(fis);
            ds.setDriverClassName(props.getProperty("driver"));
            ds.setUrl(props.getProperty("url"));
            ds.setUsername(props.getProperty("username"));
            ds.setPassword(props.getProperty("password"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ds;
    }*/

}
