package com.zf1976.wallpaper;

import cn.hutool.core.lang.Console;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.zf1976.wallpaper.api.impl.ApiService;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author ant
 * Create by Ant on 2020/8/15 下午7:01
 */
public class Application {

    /**
     *  切换cookie标记
     */
    private static boolean flag = true;
    /**
     * 每页最大数量
     */
    private final static int EACH_PAGE_SUM = 21;
    /**
     * 下一页
     */
    public final static String NEXT_PAGE = "下一页";
    /**
     * 属性key
     */
    public final static String ATTRIBUTE = "data-id";
    /**
     * cookie
     */
    public final static String COOKIE = "Cookie";

    public static void main(String[] args) {
        try {
            if (ApiService.STORE) {
                List<Entity> store = Db.use().findAll("store");
                for (Entity entity : store) {
                    String dataId = String.valueOf(entity.get("data_id")) ;
                    String type = String.valueOf(entity.get("type"));
                    downloadWallpaper(dataId, type);
                }
            } else {
                final Map<String, String> typeMaps = ApiService.getTypeMaps();
                // 八种类型壁纸
                for (String type : typeMaps.keySet()) {
                    // 获取每种壁纸已经下载的壁纸数量
                    Console.log("Download type at startup:{}", type);
                    download(typeMaps.get(type), type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void download(String baseUrl,String wallpaperType) throws Exception {
        final Document baseDocument = ApiService.getConnection()
                                                .url(baseUrl)
                                                .get();
        final String nextPageUrl = baseDocument.getElementsContainingOwnText(NEXT_PAGE)
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
            final String contentUrl = element.attr("abs:href");
            final Document downloadDocument = ApiService.getConnection()
                                                        .url(contentUrl)
                                                        .get();
            // 壁纸id
            final String wallpaperId = downloadDocument.getElementsByAttribute(ATTRIBUTE)
                                                       .attr(ATTRIBUTE);
            String wallpaperName = downloadDocument.select("h1")
                                                   .text();
            // 更新存库模式
            if (ApiService.STORE) {
                if (Db.use().find(Entity.create("store").set("data_id", wallpaperId)).isEmpty()) {
                    Db.use()
                      .insert(Entity.create("store")
                                    .set("data_id", wallpaperId)
                                    .set("file_name", wallpaperName)
                                    .set("type", wallpaperType));
                }
            } else {
                downloadWallpaper(wallpaperId, wallpaperType);
            }
        }
        download(nextPageUrl, wallpaperType);
    }

    private static void downloadWallpaper(String wallpaperId, String wallpaperType) throws IOException, SQLException, InterruptedException {
        // 壁纸未下载，则下载
        if (Db.use()
              .find(Entity.create("index_entity").set("data_id", wallpaperId))
              .isEmpty()
        ) {
            // 被检测到恶意下载 睡眠一天
            while (true){
                if (ApiService.saveWallpaper(wallpaperId, wallpaperType) == -1L){
                    final Properties properties = ApiService.getProperties();
                    if (flag){
                        ApiService.setCookie(properties.getProperty("Cookie2"));
                        Console.log("Cookie 切换为配置文件 -> Cookie2");
                        flag = false;
                    }else {
                        Console.log("Cookie 切换为配置文件 -> Cookie");
                        ApiService.setCookie(properties.getProperty(COOKIE));
                        Console.log("Detected malicious download, sleep a day");
                        TimeUnit.DAYS.sleep(1);
                        flag = true;
                    }
                }else {
                    break;
                }
            }
        }
    }


    private static String getMaxPage(String text){
        final StringBuilder sb = new StringBuilder(text);
        return sb.substring(sb.lastIndexOf("…")+1, sb.indexOf(NEXT_PAGE));
    }

}
