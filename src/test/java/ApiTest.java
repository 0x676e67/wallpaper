
import com.zf1976.wallpaper.api.DocumentParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author mac
 * Create by Ant on 2020/8/19 下午5:20
 */
public class ApiTest {

    public static void main(String[] args) throws Exception {
        Connection connect = Jsoup.connect("https://wallhaven.cc/");
        Connection.Response execute = connect.execute();
        Document homePageDocument = execute.parse();
        Elements homeTagsDocument = homePageDocument.getElementsByClass("pop-tags");
        homeTagsDocument.select("link[href]")
                        .attr("href");

        Elements metaDate = DocumentParser.builder()
                                          .noProxy()
                                          .getMetaDate("https://wallhaven.cc/", "link[href]");
    }

}
