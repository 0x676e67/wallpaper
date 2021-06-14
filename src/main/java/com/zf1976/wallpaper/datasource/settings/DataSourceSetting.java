package com.zf1976.wallpaper.datasource.settings;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author ant
 * Create by Ant on 2021/3/16 10:42 AM
 */
public class DataSourceSetting {

    private static final Logger logger = Logger.getLogger("[DataSourceSetting]");
    public static final String URL;
    public static final String USERNAME;
    public static final String PASSWORD;

    static {
        URL = getProperties("url");
        USERNAME = getProperties("username");
        PASSWORD = getProperties("password");
    }

    private static String getProperties(String key) {
        return loadProperties().getProperty(key);
    }

    private static Properties loadProperties() {
        InputStream resourceAsStream = DataSourceSetting.class.getClassLoader().getResourceAsStream("db.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
            return properties;
        } catch (IOException e) {
           logger.error(e.getMessage(), e.getCause());
        }
        return properties;
    }

}
