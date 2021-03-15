import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.zf1976.wallpaper.enums.PropertiesEnum;
import com.zf1976.wallpaper.http.ApiService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author ant
 * Create by Ant on 2020/8/15 下午7:54
 */
public class Test {

    public static void main(String[] args) throws IOException {

        String url = "http://pic.netbian.com/4kfengjing/index_19.html";
        print("Fetching %s...", url);

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");


        final Elements text = doc.getElementsByClass("page");
        System.out.println(text.text());
        String str = text.text();
        final String substring = str.substring(str.lastIndexOf("…")+1,str.indexOf(PropertiesEnum.NEXT_PAGE.content));
        System.out.println(substring);
        System.out.println(str.substring(str.lastIndexOf(PropertiesEnum.NEXT_PAGE.content)));
    }

    private static void printMedia(Elements media){
        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img"))
                print(" * %s: <%s> %sx%s (%s)",
                      src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                      trim(src.attr("alt"), 50));
            else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }
    }

    private static void printImports(Elements imports){
        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }
    }

    private static void printLinks(Elements links){
        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }


    @org.junit.Test
    public void demo() throws IOException {
        final InputStream resourceAsStream = Test.class.getClassLoader()
                                                       .getResourceAsStream("config.properties");
        final Properties properties = new Properties();
        properties.load(resourceAsStream);
        final Object o = properties.get(PropertiesEnum.COOKIE.content);
        System.out.println((String)o);

    }


    @org.junit.Test
    public void downloadTest() throws IOException, URISyntaxException {

        final Properties properties = ApiService.getProperties();
        final String apiInfoUrl= (String) properties.get("url.base.info");
        final String baseUrl = (String) properties.get("url.base");
        final String accept = (String) properties.get("header.Accept");
        final String acceptEncoding = (String) properties.get("header.Accept-Encoding");
        final String acceptLanguage = (String) properties.get("header.Accept-Language");
        final String cacheControl = (String) properties.get("header.Cache-Control");
        final String connection = (String) properties.get("header.Connection");
        final String dnt = (String) properties.get("header.DNT");
        final String wallpaperContentPath = (String) properties.get("wallpaper.file.name");
        final String cookie = ApiService.getCookie();

        final HashMap<String, Object> param = new HashMap<>();
        param.put("id",24851);
        param.put("t",Math.random());


        final String body = HttpRequest.get(apiInfoUrl)
                                       .header(Header.COOKIE, cookie)
                                       .form(param)
                                       .execute()
                                       .body();

        final JSON result = JSONUtil.parse(body);
        final String downloadUrl = (String) result.getByPath("pic");
        final String workingPath = System.getProperty("user.dir");
        final HttpResponse response = HttpRequest.get(baseUrl + downloadUrl)
                                                 .header(Header.COOKIE, cookie)
                                                 .header(Header.ACCEPT, accept)
                                                 .header(Header.ACCEPT_ENCODING, acceptEncoding)
                                                 .header(Header.CONNECTION, connection)
                                                 .header(Header.CACHE_CONTROL, cacheControl)
                                                 .header(Header.ACCEPT_LANGUAGE, acceptLanguage)
                                                 .header("DNT", dnt)
                                                 .execute();


        final String headerContent = response.header("Content-Disposition");

        String fileName = ReUtil.get("filename=\"(.*?)\"", headerContent, 1);
        final String newFileName = new String(fileName.getBytes("ISO_8859_1"), "GB2312");
        final File destFile = FileUtil.file(workingPath, newFileName);
        response.writeBody(FileUtil.getOutputStream(destFile),true,null);
    }


    @org.junit.Test
    public void fileTest() throws IOException, InterruptedException {
        final long size =   ApiService.saveWallpaper("24851", "4K美女",0);

        int GB = 1024 * 1024 * 1024;
        int MB = 1024 * 1024;
        int KB = 1024;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "";
        if (size / GB >= 1) {
            resultSize = df.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            resultSize = df.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            resultSize = df.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        Console.log(resultSize);
    }

    @org.junit.Test
    public void Test() throws InterruptedException {
        System.out.print("Progress:");
        for (int i = 1; i <= 100; i++) {
            System.out.print(i + "%");
            for (int j = 0; j <= String.valueOf(i).length(); j++) {
                System.out.print("\r");
                Thread.sleep(1000);
            }
        }
    }

    @org.junit.Test
    public void sqlTest(){
        try (final Connection connection = DbUtil.getDs()
                                                 .getConnection()){

            final Statement statement = connection.createStatement();
            final int index = statement.executeUpdate("show tables");
            if (index!=-1){
                statement.executeUpdate("create table index_entity\n" +
                                                "(\n" +
                                                "\tid int auto_increment,\n" +
                                                "\t`index` int null,\n" +
                                                "\tconstraint index_entity_pk\n" +
                                                "\t\tprimary key (id)\n" +
                                                ");");
                Console.log("Created table in given database...");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void tableTest() throws SQLException {
        Db.use().insert(Entity.create("index_entity")
                              .set("index",1));
        final List<Entity> entities = Db.use().find(Entity.create("index_entity").set("index", 1));
        entities.forEach(System.out::println);
        final boolean empty = entities.isEmpty();
        System.out.println(empty);
        System.out.println(-1 == -1L);
    }

    @org.junit.Test
    public void readTest() throws IOException {

    }
}
