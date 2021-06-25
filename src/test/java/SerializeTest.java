import com.zf1976.wallpaper.datasource.DbStoreUtil;
import com.zf1976.wallpaper.datasource.NetbianStoreStrategy;
import com.zf1976.wallpaper.entity.NetbianEntity;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.impl.FileSystemImpl;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SerializeTest {

    public static void main(String[] args) {

        try (Connection connection = DbStoreUtil.createConnection()) {
            final var resultSet = connection.prepareStatement("select * from index_entity")
                                            .executeQuery();
            final var netbianStoreStrategy = new NetbianStoreStrategy();
            while (resultSet.next()) {
                final var netbianEntity = new NetbianEntity()
                        .setDataId(resultSet.getString("data_id"))
                        .setName(resultSet.getString("name"))
                        .setType(resultSet.getString("type"))
                        .setId(resultSet.getInt("id"));
                netbianStoreStrategy.store(netbianEntity);
            }
        } catch (SQLException | ClassNotFoundException ignored) {
        }
    }
    @Test
    public void readTest() {
        final var netbianStoreStrategy = new NetbianStoreStrategy();
        netbianStoreStrategy.read().forEach(System.out::println);
        System.out.println(netbianStoreStrategy.container("5055"));
    }
}
