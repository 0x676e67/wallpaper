package com.zf1976.wallpaper.datasource.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author ant
 * Create by Ant on 2021/3/16 10:42 AM
 */
public class DataSourceSetting {

    public static final String URL;
    public static final String USERNAME;
    public static final String PASSWORD;
    public static  Integer MAX_CAPACITY = 5;
    public static  Integer MIN_CAPACITY = 1;
    private static final Integer DEFAULT_MAX_CAPACITY= 2;
    private static final Integer DEFAULT_MIN_CAPACITY= 1;

    static {
        URL = getProperties("url");
        USERNAME = getProperties("username");
        PASSWORD = getProperties("password");
        MAX_CAPACITY = getMaxCapacity();
        MIN_CAPACITY = getMinCapacity();
    }

    private static String getProperties(String key) {
        return loadProperties().getProperty(key);
    }

    private static Properties loadProperties() {
        InputStream resourceAsStream = DataSourceSetting.class.getClassLoader()
                                                              .getResourceAsStream("db.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static Integer getMaxCapacity() {
        String property = loadProperties().getProperty("max-capacity");
        return extract(property, DEFAULT_MAX_CAPACITY);
    }

    private static Integer getMinCapacity() {
        String property = loadProperties().getProperty("min-capacity");
        return extract(property, DEFAULT_MIN_CAPACITY);
    }

    private static Integer extract(String property, Integer defaultCapacity) {
        if (Objects.isNull(property) || property.length() <= 0) {
            return defaultCapacity;
        }
        try {
            int i = Integer.parseInt(property);
            if (i <= 0) {
                return defaultCapacity;
            }
            return i;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultCapacity;
    }

    public static void main(String[] args) {
        System.out.println(DataSourceSetting.MIN_CAPACITY);
    }
}