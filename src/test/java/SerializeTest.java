import com.zf1976.wallpaper.datasource.NetbianStoreStrategy;
import com.zf1976.wallpaper.entity.NetbianEntity;

public class SerializeTest {

    public static void main(String[] args) {
        var netbianStoreStrategy = new NetbianStoreStrategy();
        netbianStoreStrategy.store(new NetbianEntity().setDataId("1111")
                                                      .setName("111")
                                                      .setType("222123"));
        var read = netbianStoreStrategy.read();
        System.out.println(read);
    }
}
