package com.zf1976.wallpaper.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.zf1976.wallpaper.enums.PropertiesEnum;
import com.zf1976.wallpaper.enums.TypeEnum;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/8/15 下午9:37
 */
public class ApiService {

    private static String cookie;
    private static final Set<String> TYPES;
    private static final Map<String, String> TYPE_MAPS;
    private static final Connection CONN;
    public static final Properties PROPERTIES;
    public static final String BASE_URL;
    public static final String BASE_INFO_URL;
    public static final String ACCEPT;
    public static final String ACCEPT_ENCODING;
    public static final String ACCEPT_LANGUAGE;
    public static final String CACHE_CONTROL;
    public static final String CONNECTION;
    public static final String DNT;
    public static final String UPGRADE_INSECURE_REQUESTS;
    public static final String HOST;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0000");
    private final static int TOTAL_LENGTH = 30;
    static {
        final InputStream is = ApiService.class.getClassLoader()
                                               .getResourceAsStream("config.properties");

        HttpRequest.closeCookie();

        final Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String baseUrl = properties.getProperty("url.base");

        cookie = properties.getProperty(PropertiesEnum.COOKIE.content);
        TYPES = new HashSet<>(12);
        TYPE_MAPS = new HashMap<>(12);
        CONN = Jsoup.connect(baseUrl);
        PROPERTIES = properties;
        BASE_URL = baseUrl;
        BASE_INFO_URL = properties.getProperty("url.base.info");
        ACCEPT = properties.getProperty("header.Accept");
        ACCEPT_ENCODING = properties.getProperty("header.Accept-Encoding");
        ACCEPT_LANGUAGE = properties.getProperty("header.Accept-Language");
        CACHE_CONTROL = properties.getProperty("header.Cache-Control");
        CONNECTION = properties.getProperty("header.Connection");
        DNT = properties.getProperty("header.DNT");
        UPGRADE_INSECURE_REQUESTS = properties.getProperty("Upgrade-Insecure-Requests");
        HOST = properties.getProperty("header.host");

        Document document = null;
        try {
            document = CONN.get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert document != null;
        final Elements links = document.select("a[href]");

        for (TypeEnum value : TypeEnum.values()) {
            TYPES.add(value.description);
        }

        for (Element link : links) {
            final String url = link.attr("abs:href");
            final String text = trim(link.text());
            if (TYPES.contains(text)){
                TYPE_MAPS.put(text,url);
            }
        }

    }

    public static long saveWallpaper(String wallpaperId,String wallpaperType,int index) throws IOException {

        final String downloadUrl = getDownloadUri(wallpaperId);
        if (downloadUrl == null || Objects.equals(downloadUrl,"")){
            return -1L;
        }
        final HttpResponse response = HttpRequest.get(BASE_URL + downloadUrl)
                                                 .cookie(cookie)
                                                 .header(Header.ACCEPT, ACCEPT)
                                                 .header(Header.ACCEPT_ENCODING, ACCEPT_ENCODING)
                                                 .header(Header.ACCEPT_LANGUAGE, ACCEPT_LANGUAGE)
                                                 .header(Header.CACHE_CONTROL, CACHE_CONTROL)
                                                 .header(Header.CONNECTION, CONNECTION)
                                                 .header(Header.HOST, HOST)
                                                 .timeout(20000)
                                                 .execute(false);

        final String doestPath = System.getProperty("user.home");
        final String wallpaperDir = PROPERTIES.getProperty("wallpaper.file.name");
        final String fileName = getFileName(response);
        final int size = response.bodyBytes().length;
        return response.writeBody(FileUtil.getOutputStream(FileUtil.file(doestPath,
                                                                         wallpaperDir,
                                                                         wallpaperType,
                                                                         fileName)), true, new StreamProgress() {
            @Override
            public void start() {
                Console.log("开始下载壁纸:{}\n大小为:{}MB", fileName, DECIMAL_FORMAT.format(size/(float)(1024*1024)));
            }

            @Override
            public void progress(long l) {
                System.out.print("\r下载进度：" + (l) + "/" + size);
            }

            @Override
            public void finish() {
                try {
                    Db.use().insert(Entity.create("index_entity")
                                          .set("data_id",wallpaperId)
                                          .set("name",fileName)
                                          .set("type",wallpaperType)
                                          .set("index",index));
                    Console.log("下载完毕!");
                    TimeUnit.MILLISECONDS.sleep(10000);
                } catch (InterruptedException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String getFileName(HttpResponse response) throws UnsupportedEncodingException {
        assert response != null;
        String fileName = response.header(Header.CONTENT_DISPOSITION);
        fileName = ReUtil.get("filename=\"(.*?)\"", fileName, 1);
        return new String(fileName.getBytes(StandardCharsets.ISO_8859_1), "GB2312");
    }

    private static String getDownloadUri(String wallpaperId) {
        final HashMap<String, Object> form = new HashMap<>(2);
        form.put("t", Math.random());
        form.put("id", wallpaperId);
        final HttpResponse response = HttpRequest.get(BASE_INFO_URL)
                                                 .cookie(cookie)
                                                 .form(form)
                                                 .timeout(20000)
                                                 .execute(false);
        final String body = response.body();
        final String uri = (String) JSONUtil.parse(body).getByPath("pic");
        Console.log("下载链接:{}",BASE_URL + uri);
        return uri;
    }

    public static void setCookie(String cookie) {
        ApiService.cookie = cookie;
    }

    public static Properties getProperties(){
        return PROPERTIES;
    }

    public static String getCookie(){
        return cookie;
    }

    public static Map<String,String> getTypeMaps(){
        return TYPE_MAPS;
    }

    public static Connection getConnection(){
        return CONN;
    }

    private static String trim(String str){
        int width = 35;
        if (str.length() > width){
            return str.substring(0, width - 1) + ".";
        }else {
            return str;
        }
    }

    public static void main(String[] args) {
        final long size = 104857600;
        for (int i = 0; i <= 104857600; i++) {
            System.out.println("\r");
            System.out.print("\r下载进度：" + DECIMAL_FORMAT.format(i/(float)104857600) + "%\t"  + "\t" + (i) + "/" + size);
        }
    }


}
