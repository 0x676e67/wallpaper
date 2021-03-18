import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author ant
 * Create by Ant on 2021/3/16 8:12 AM
 */
public class SqlTest {

    public static void main(String[] args) {
        try {
            Class<?> aClass = Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test");
            boolean show_schemas = connection.createStatement()
                                             .execute("show schemas");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
