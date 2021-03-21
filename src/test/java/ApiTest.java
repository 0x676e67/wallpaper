import com.zf1976.wallpaper.api.constant.JsoupConstants;
import com.zf1976.wallpaper.api.support.impl.WallHavenParser;
import com.zf1976.wallpaper.util.HttpUtil;
import org.jsoup.Connection;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author mac
 * Create by Ant on 2020/8/19 下午5:20
 */
public class ApiTest {

    public static void main(String[] args) throws Exception {

        WallHavenParser wallHavenParser = WallHavenParser.builder()
                                                         .method(Connection.Method.GET)
                                                         .proxy(Proxy.NO_PROXY)
                                                         .url("https://wallhaven.cc")
                                                         .build();
        Elements elements = wallHavenParser.selectHomePageFeatRow();
        String href = elements.attr(JsoupConstants.ATTR_HREF);
        String imgUrl = wallHavenParser.connection(href)
                                          .getElementById("wallpaper")
                                          .attr(JsoupConstants.ATTR_SRC);
        HttpRequest httpRequest = HttpRequest.newBuilder(new URI(imgUrl))
                                       .GET()
                                       .build();
        HttpResponse<InputStream> send = HttpClient.newHttpClient()
                                                   .send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        String path = send.uri()
                          .getPath();
        String fileNameFromPath = HttpUtil.getFileNameFromPath(path);
        byte[] date = new byte[4*1024];
        int len = 0;
        try (OutputStream outputStream = Files.newOutputStream(Path.of("/Users/mac/desktop",fileNameFromPath));
             InputStream inputStream = send.body()) {
            while ((len = inputStream.read(date)) != -1) {
                outputStream.write(date, 0, len);
            }
        }
    }

}
