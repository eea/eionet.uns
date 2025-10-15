package com.eurodyn.uns.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 * Bean post-processor that logs the database connection configuration and performs diagnostics.
 */
@Component
public class DataSourceLoggingBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceLoggingBeanPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof HikariConfig) {
            HikariConfig config = (HikariConfig) bean;
            String jdbcUrl = config.getDataSourceProperties().getProperty("url");

            logger.info("================================================================================");
            logger.info("=== HikariConfig Bean Created: {} ===", beanName);
            logger.info("================================================================================");
            logger.info("DataSource ClassName: {}", config.getDataSourceClassName());
            logger.info("JDBC URL from properties: {}", jdbcUrl);
            logger.info("Username: {}", config.getDataSourceProperties().getProperty("user"));
            logger.info("Pool Name: {}", config.getPoolName());
            logger.info("Minimum Idle: {}", config.getMinimumIdle());
            logger.info("Maximum Pool Size: {}", config.getMaximumPoolSize());
            logger.info("================================================================================");

            // Parse and diagnose the JDBC URL
            if (jdbcUrl != null) {
                diagnoseJdbcUrl(jdbcUrl);
            }
        }

        if (bean instanceof HikariDataSource) {
            HikariDataSource dataSource = (HikariDataSource) bean;
            logger.info("================================================================================");
            logger.info("=== HikariDataSource Bean Created: {} ===", beanName);
            logger.info("================================================================================");
            logger.info("JDBC URL: {}", dataSource.getJdbcUrl());

            // Log all environment variables related to database
            logEnvironmentVariables();

            // Log system properties related to database
            logSystemProperties();

            logger.info("================================================================================");

            // Test the connection
            testDatabaseConnection(dataSource);
        }

        return bean;
    }

    private void diagnoseJdbcUrl(String jdbcUrl) {
        logger.info("=== Diagnosing JDBC URL ===");

        try {
            // Parse hostname and port from JDBC URL
            // Format: jdbc:mysql://hostname:port/database?params
            String[] parts = jdbcUrl.split("//");
            if (parts.length > 1) {
                String hostPart = parts[1].split("/")[0];
                String host = hostPart.contains(":") ? hostPart.split(":")[0] : hostPart;
                int port = hostPart.contains(":") ? Integer.parseInt(hostPart.split(":")[1]) : 3306;

                logger.info("Extracted Host: {}", host);
                logger.info("Extracted Port: {}", port);

                // Try to resolve hostname
                try {
                    InetAddress address = InetAddress.getByName(host);
                    logger.info("DNS Resolution SUCCESS: {} -> {}", host, address.getHostAddress());

                    // Try to connect to the port
                    testTcpConnection(host, port);

                } catch (Exception e) {
                    logger.error("DNS Resolution FAILED for host '{}': {}", host, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing JDBC URL: {}", e.getMessage());
        }

        logger.info("=== End JDBC URL Diagnosis ===");
    }

    private void testTcpConnection(String host, int port) {
        logger.info("Testing TCP connection to {}:{}...", host, port);
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), 5000);
            logger.info("TCP Connection SUCCESS to {}:{}", host, port);
        } catch (Exception e) {
            logger.error("TCP Connection FAILED to {}:{} - {}", host, port, e.getMessage());
        }
    }

    private void logEnvironmentVariables() {
        logger.info("=== Environment Variables (Database Related) ===");
        Map<String, String> env = System.getenv();

        // Log all environment variables
        env.entrySet().stream()
            .filter(e -> e.getKey().toUpperCase().contains("DB") ||
                        e.getKey().toUpperCase().contains("MYSQL") ||
                        e.getKey().toUpperCase().contains("DATABASE") ||
                        e.getKey().toUpperCase().contains("DOCKER") ||
                        e.getKey().toUpperCase().contains("HOST"))
            .forEach(e -> {
                String key = e.getKey();
                String value = e.getValue();
                // Mask password values
                if (key.toUpperCase().contains("PASSWORD") || key.toUpperCase().contains("PASS")) {
                    value = "***MASKED***";
                }
                logger.info("  {}={}", key, value);
            });

        logger.info("=== End Environment Variables ===");
    }

    private void logSystemProperties() {
        logger.info("=== System Properties (Database Related) ===");
        Properties props = System.getProperties();

        props.entrySet().stream()
            .filter(e -> {
                String key = e.getKey().toString().toLowerCase();
                return key.contains("db") ||
                       key.contains("mysql") ||
                       key.contains("database") ||
                       key.contains("docker") ||
                       key.contains("jdbc");
            })
            .forEach(e -> {
                String key = e.getKey().toString();
                String value = e.getValue().toString();
                // Mask password values
                if (key.toLowerCase().contains("password") || key.toLowerCase().contains("pass")) {
                    value = "***MASKED***";
                }
                logger.info("  {}={}", key, value);
            });

        logger.info("=== End System Properties ===");
    }

    private void testDatabaseConnection(HikariDataSource dataSource) {
        logger.info("=== Testing Database Connection ===");

        try {
            logger.info("Attempting to get connection from pool...");
            Connection conn = dataSource.getConnection();
            logger.info(" Got connection from pool: {}", conn.getClass().getName());

            logger.info("Testing connection validity (timeout: 5 seconds)...");
            boolean isValid = conn.isValid(5);

            if (isValid) {
                logger.info("✓ DATABASE CONNECTION SUCCESSFUL!");
                logger.info("  Connection Catalog: {}", conn.getCatalog());
                logger.info("  Connection Schema: {}", conn.getSchema());
                logger.info("  AutoCommit: {}", conn.getAutoCommit());
                logger.info("  Read Only: {}", conn.isReadOnly());
                logger.info("  Transaction Isolation: {}", conn.getTransactionIsolation());

                // Get database metadata
                try {
                    logger.info("=== Database Metadata ===");
                    logger.info("  Database Product: {} {}",
                        conn.getMetaData().getDatabaseProductName(),
                        conn.getMetaData().getDatabaseProductVersion());
                    logger.info("  Current User: {}", conn.getMetaData().getUserName());
                    logger.info("  JDBC Driver: {} {}",
                        conn.getMetaData().getDriverName(),
                        conn.getMetaData().getDriverVersion());
                } catch (Exception e) {
                    logger.warn("Could not retrieve database metadata: {}", e.getMessage());
                }

                // Execute SQL to show databases
                try {
                    logger.info("=== Executing: SHOW DATABASES ===");
                    var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SHOW DATABASES");
                    int count = 0;
                    while (rs.next() && count < 10) {
                        logger.info("  Database: {}", rs.getString(1));
                        count++;
                    }
                    rs.close();
                    stmt.close();
                } catch (Exception e) {
                    logger.warn("Could not execute SHOW DATABASES: {}", e.getMessage());
                }

                // Execute SQL to show tables in current database
                try {
                    logger.info("=== Executing: SHOW TABLES ===");
                    var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SHOW TABLES");
                    int count = 0;
                    while (rs.next() && count < 20) {
                        logger.info("  Table: {}", rs.getString(1));
                        count++;
                    }
                    if (count == 0) {
                        logger.info("  (No tables found - database is empty)");
                    }
                    rs.close();
                    stmt.close();
                } catch (Exception e) {
                    logger.warn("Could not execute SHOW TABLES: {}", e.getMessage());
                }

                // Check user privileges
                try {
                    logger.info("=== Executing: SHOW GRANTS ===");
                    var stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SHOW GRANTS");
                    int count = 0;
                    while (rs.next() && count < 10) {
                        logger.info("  Grant: {}", rs.getString(1));
                        count++;
                    }
                    rs.close();
                    stmt.close();
                } catch (Exception e) {
                    logger.warn("Could not execute SHOW GRANTS: {}", e.getMessage());
                }
            } else {
                logger.error("✗ DATABASE CONNECTION FAILED - Connection is not valid");
            }

            conn.close();
            logger.info("Connection closed successfully");

        } catch (Exception e) {
            logger.error("DATABASE CONNECTION FAILED: {}", e.getMessage());
            logger.error("Exception class: {}", e.getClass().getName());
            logger.error("Stack trace:", e);

            // Try to get more details about the failure
            if (e.getCause() != null) {
                logger.error("Root cause: {}", e.getCause().getMessage());
                logger.error("Root cause class: {}", e.getCause().getClass().getName());
            }
        }

        logger.info("=== End Database Connection Test ===");
        logger.info("================================================================================");
    }
}
