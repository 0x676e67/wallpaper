import com.zf1976.wallpaper.datasource.NetbianStoreStrategy;
import com.zf1976.wallpaper.entity.NetbianEntity;

public class SerializeTest {

    public static void main(String[] args) {
        final var netbianStoreStrategy = new NetbianStoreStrategy();
        for (NetbianEntity netbianEntity : netbianStoreStrategy.read()) {
            System.out.println(netbianEntity);
        }
        System.out.println(netbianStoreStrategy.read()
                                               .size());
//        try (Connection connection = DbStoreUtil.createConnection()) {
//            final var resultSet = connection.prepareStatement("select * from index_entity")
//                                            .executeQuery();
//            final var netbianStoreStrategy = new NetbianStoreStrategy();
//            while (resultSet.next()) {
//                final var netbianEntity = new NetbianEntity()
//                        .setDataId(resultSet.getString("data_id"))
//                        .setName(resultSet.getString("name"))
//                        .setType(resultSet.getString("type"))
//                        .setId(resultSet.getInt("id"));
//                netbianStoreStrategy.store(netbianEntity);
//            }
//        } catch (SQLException | ClassNotFoundException ignored) {
//        }
    }

}
