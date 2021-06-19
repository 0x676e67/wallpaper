import com.zf1976.wallpaper.datasource.DbStoreUtil;
import com.zf1976.wallpaper.entity.NetbianEntity;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

/**
 * @author ant
 * Create by Ant on 2020/8/15 下午7:54
 */
public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                       .GET()
                                       .uri(URI.create("https://pic.netbian.com"))
                                       .build();
        HttpRequest.newBuilder()
                   .GET()
                   .uri(URI.create("https://pic.netbian.com/e/extend/downpic.php"));
        String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                                .body();
        System.out.println(body);
    }

    @org.junit.Test
    public void sqlTest() {

        final var b = DbStoreUtil.checkNetbianWallpaperId("SELECT data_id as dataId,name,type,id from index_entity where data_id = ?","17068");
        final var netbianEntity = new NetbianEntity();
        final var entity = netbianEntity.setDataId("2")
                                                .setName("2")
                                                .setType("2");
        final var insertNetbianEntity = DbStoreUtil.insertNetbianEntity("INSERT INTO `index_entity`(data_id, name, type) VALUES (?, ?, ?)", entity);
        System.out.println(insertNetbianEntity);
        System.out.println(b);
    }

    @org.junit.Test
    public void fileTest() {
        try {
            Files.createDirectories(Paths.get("/Users/ant","netbian","4k"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
