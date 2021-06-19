package com.zf1976.wallpaper.datasource;

import com.zf1976.wallpaper.datasource.settings.DataSourceSetting;
import com.zf1976.wallpaper.entity.NetbianEntity;

import java.sql.*;

/**
 * @author ant
 * Create by Ant on 2021/3/16 10:55 AM
 */
public class DbStoreUtil {

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DataSourceSetting.URL, DataSourceSetting.USERNAME, DataSourceSetting.PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    public static boolean checkNetbianWallpaperId(String sql, String wallpaperId) {
        try (Connection connection = createConnection();
             final var preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, wallpaperId);
            return preparedStatement.executeQuery()
                                    .next();
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static boolean insertNetbianEntity(String sql, NetbianEntity netbianEntity) {
        try (Connection connection = createConnection();
             final var preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, netbianEntity.getDataId());
            preparedStatement.setString(2, netbianEntity.getName());
            preparedStatement.setString(3, netbianEntity.getType());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
