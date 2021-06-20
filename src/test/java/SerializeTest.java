import com.zf1976.wallpaper.datasource.NetbianStoreStrategy;

public class SerializeTest {

    public static void main(String[] args) {
        var netbianStoreStrategy = new NetbianStoreStrategy();
        var read = netbianStoreStrategy.read();
        System.out.println(read);

    }
}
