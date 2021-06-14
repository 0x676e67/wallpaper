package com.zf1976.wallpaper.datasource;

import com.zf1976.wallpaper.datasource.settings.DataSourceSetting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author ant
 * Create by Ant on 2021/3/16 10:55 AM
 */
public class DbConnectionUtil {

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DataSourceSetting.URL, DataSourceSetting.USERNAME, DataSourceSetting.PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }
}
