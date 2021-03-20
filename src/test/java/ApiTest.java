import com.zf1976.wallpaper.api.impl.WallHavenParser;
import org.jsoup.Connection;
import org.jsoup.select.Elements;

import java.net.Proxy;

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

        Elements latest = wallHavenParser.selectHomePageLatest();
        Elements random = wallHavenParser.selectHomePageRandom();
        Elements topList = wallHavenParser.selectHomePageTopList();

    }

}
