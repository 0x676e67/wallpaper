package com.zf1976.wallpaper;

import cn.hutool.core.lang.Console;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import com.zf1976.wallpaper.enums.PropertiesEnum;
import com.zf1976.wallpaper.http.ApiService;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author ant
 * Create by Ant on 2020/8/15 下午7:01
 */
public class Main {

    /**
     * 迭代位置标记
     */
    private static int index=0;
    /**
     *  切换cookie标记
     */
    private static boolean flag = true;
    /**
     * 每页最大数量
     */
    private final static int EACH_PAGE_SUM = 21;

    static {
        try (final Connection connection = DbUtil.getDs().getConnection();
             final Statement statement = connection.createStatement()){
            final int index = statement.executeUpdate("show tables");
            if (index!=-1){
                statement.executeUpdate("create table index_entity\n" +
                        "(\n" +
                        "\tid int auto_increment,\n" +
                        "\t`data_id` int null,\n" +
                        "\t`name` varchar(255) null,\n" +
                        "\t`type` varchar(20) null,\n" +
                        "\t`index` int null,\n" +
                        "\tconstraint index_entity_pk\n" +
                        "\t\tprimary key (id)\n" +
                        ");");
                Console.log("Created table in given database ok...");
            }
        }catch (Exception ignored){

        }
    }

    public static void main(String[] args) {
        try {
            final Map<String, String> typeMaps = ApiService.getTypeMaps();
            // 八种类型壁纸
            for (String type : typeMaps.keySet()) {
                // 获取每种壁纸已经下载的壁纸数量
                download(typeMaps.get(type), type);
            }
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }


    private static void download(String baseUrl,String wallpaperType) throws IOException, InterruptedException, SQLException {
        final Document baseDocument = ApiService.getConnection()
                                                .url(baseUrl)
                                                .get();
        final String nextPageUrl = baseDocument.getElementsContainingOwnText(PropertiesEnum.NEXT_PAGE.content)
                                               .attr("abs:href");
        // 无下一页时结束
        if (StringUtil.isBlank(nextPageUrl)){
            Console.log("{} -> 下载完毕", wallpaperType);
            return;
        }
        // 筛选下一页元素
        final Elements elements = baseDocument.select("[href*=/tupian/]");
        // 每一页
        for (Element element : elements) {
            ++index;
            final String contentUrl = element.attr("abs:href");
            final Document downloadDocument = ApiService.getConnection()
                                                        .url(contentUrl)
                                                        .get();
            // 壁纸id
            final String wallpaperId = downloadDocument.getElementsByAttribute(PropertiesEnum.ATTRIBUTE.content)
                                                       .attr(PropertiesEnum.ATTRIBUTE.content);
            // 壁纸未下载，则下载
            if (Db.use()
                  .find(Entity.create("index_entity").set("data_id", wallpaperId))
                  .isEmpty()) {
                // 被检测到恶意下载 睡眠一天
               while (true){
                   if (ApiService.saveWallpaper(wallpaperId, wallpaperType,index) == -1L){
                       final Properties properties = ApiService.getProperties();
                       if (flag){
                           ApiService.setCookie(properties.getProperty("Cookie2"));
                           Console.log("Cookie 切换为配置文件 -> Cookie2");
                           flag = false;
                       }else {
                           Console.log("Cookie 切换为配置文件 -> Cookie");
                           ApiService.setCookie(properties.getProperty(PropertiesEnum.COOKIE.content));
                           Console.log("被检测恶意下载，睡眠一天");
                           TimeUnit.DAYS.sleep(1);
                           flag = true;
                       }
                   }else {
                       Console.log("恢复完毕 -> 继续下载");
                       break;
                   }
               }
            }
        }
        download(nextPageUrl,wallpaperType);
    }

    private static String getMaxPage(String text){
        final StringBuilder sb = new StringBuilder(text);
        return sb.substring(sb.lastIndexOf("…")+1,sb.indexOf(PropertiesEnum.NEXT_PAGE.content));
    }

}
