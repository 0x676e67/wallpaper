import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zf1976.wallpaper.entity.NetbianEntity;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;

/**
 * @author ant
 * Create by Ant on 2021/6/16 4:19 下午
 */
public class SqlTemplateTest {
    public static void main(String[] args) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("127.0.0.1")
                .setDatabase("wallpaper")
                .setUser("root")
                .setPassword("itcast")
                .setCollation("utf8mb4_unicode_ci")
                .setCharset("utf8mb4");
        var poolOptions = new PoolOptions();
        poolOptions.setMaxSize(10)
                   .setMaxWaitQueueSize(5);
        var client = MySQLPool.pool(connectOptions, poolOptions);
        DatabindCodec.mapper().registerModule(new JavaTimeModule());
        SqlTemplate.forQuery(client,"select id,data_id as dataId,type,name from index_entity")
                   .mapTo(NetbianEntity.class)
                   .execute(Collections.emptyMap())
                   .onSuccess(event -> {
                       for (NetbianEntity netbianEntity : event) {
                           System.out.println(netbianEntity);
                       }
                       client.close();
                   })
                   .onFailure(event -> {
                       System.out.println(event.getCause());
                       client.close();
                   });

    }
}
