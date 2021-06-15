import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;

/**
 * @author ant
 * Create by Ant on 2021/6/15 4:25 下午
 */
public class SqlTest {

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

        client.getConnection()
              .compose(sqlConnection -> {
                  System.out.println("Got a connection from the pool");
                  return sqlConnection.query("select * from store")
                                      .execute()
                                      .compose(rows ->
                                              sqlConnection.query("select * from index_entity;")
                                                           .execute()
                                                           .onComplete(event -> {
                                                               if (event.succeeded()) {
                                                                   var result = event.result();
                                                                   var property = result.property(MySQLClient.LAST_INSERTED_ID);
                                                                   for (Row row : result) {
                                                                       System.out.println(row.getString("name") + "-" + row.getString("type"));
                                                                   }
                                                               }
                                                           }))
                                      .onComplete(event -> {
                                          sqlConnection.close();
                                      });
              }).onComplete(event -> {
                  if (event.succeeded()) {
                      client.close();
                      System.out.println("Done");
                  } else {
                      System.out.println("Something went wrong " + event.cause()
                                                                        .getMessage());
                  }
        });
    }
}
