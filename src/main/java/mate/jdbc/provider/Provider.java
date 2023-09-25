package mate.jdbc.provider;

import mate.jdbc.exception.DataProcessingException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Provider {

    public static final String SCHEMA_FILENAME = "init_db.sql";
    private static final String PROPERTIES_FILENAME = "database.properties";
    private static final String PROPERTY_KEY_URL = "db.url";
    private static final String PROPERTY_KEY_USER = "db.user";
    private static final String PROPERTY_KEY_PASSWORD = "db.password";
    private String url;
    private String user;
    private String password;

    public Provider() {
        Properties properties = loadProperties();
        this.url = properties.getProperty(PROPERTY_KEY_URL);
        this.user = properties.getProperty(PROPERTY_KEY_USER);
        this.password = properties.getProperty(PROPERTY_KEY_PASSWORD);
    }

    public Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            //conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            throw new DataProcessingException("getConnection failed: ", e);
        }
    }

    private Properties loadProperties() {
        try(InputStream input = Provider.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME)) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new DataProcessingException("loadProperties failed: " , e);
        }

    }

}
