import com.zf1976.wallpaper.api.impl.WallHavenParser;
import org.jsoup.nodes.Element;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mac
 * Create by Ant on 2020/8/19 下午5:20
 */
public class ApiTest {

    public static void main(String[] args) throws Exception {
        WallHavenParser wallHavenParser = new WallHavenParser("https://wallhaven.cc/");
        Set<String> collect = wallHavenParser.selectHomePageTags()
                                             .stream()
                                             .map(Element::text)
                                             .collect(Collectors.toSet());
        for (String str : collect) {
            System.out.println(str);
        }
    }

}
