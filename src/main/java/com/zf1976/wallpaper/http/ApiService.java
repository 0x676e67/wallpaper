package com.zf1976.wallpaper.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.zf1976.wallpaper.enums.TypeEnum;
import com.zf1976.wallpaper.support.PrintProgressBar;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

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
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final PrintProgressBar PRINT_PROGRESS_BAR = new PrintProgressBar(0);

    static {
        final InputStream is = ApiService.class.getClassLoader().getResourceAsStream("config.properties");
        HttpRequest.closeCookie();
        final Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String baseUrl = properties.getProperty("url.base");
        cookie = properties.getProperty("Cookie");
        TYPES = new HashSet<>(12);
        TYPE_MAPS = new HashMap<>(12);
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
        CONN = Jsoup.connect(baseUrl);

        Document document = null;
        try {
            document = CONN.get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Elements links = Objects.requireNonNull(document).select("a[href]");

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
        final String downloadUri = getDownloadUri(wallpaperId);
        if (downloadUri == null || Objects.equals(downloadUri,"")){
            return -1L;
        }
        final HttpResponse response = HttpRequest.get(BASE_URL + downloadUri)
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
        InputStream bodyStream = response.bodyStream();
        //文件大小
        PRINT_PROGRESS_BAR.setSize(response.bodyBytes().length);
        Console.log("开始下载壁纸：{}", fileName);
        byte[] data = new byte[response.bodyBytes().length];
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(FileUtil.file(doestPath, wallpaperDir, wallpaperType, fileName)));
        int len;
        while ((len = bodyStream.read()) != -1) {
            PRINT_PROGRESS_BAR.printAppend(len);
            outputStream.write(data, 0, len);
        }
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.bodyBytes().length;
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

}
