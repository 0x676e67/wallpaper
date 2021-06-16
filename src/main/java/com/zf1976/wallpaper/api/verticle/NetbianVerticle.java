package com.zf1976.wallpaper.api.verticle;

import com.zf1976.wallpaper.api.constants.JsoupConstants;
import com.zf1976.wallpaper.api.constants.NetbianConstants;
import com.zf1976.wallpaper.enums.NetBianType;
import com.zf1976.wallpaper.property.NetbianProperty;
import com.zf1976.wallpaper.util.HttpUtil;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author mac
 * @date 2021/6/14
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class NetbianVerticle extends AbstractVerticle {

    private final Logger log = Logger.getLogger("[NetbianVerticle]");
    private final Set<String> wallpaperType = new HashSet<>();
    private final Map<String, String> wallpaperTypeMap = new HashMap<>();
    private NetbianProperty property;
    protected HttpClient httpClient = HttpClient.newHttpClient();
    protected final String USER_HOME = System.getProperty("user.home");

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        NetbianVerticle that = this;
        this.initConfig()
            .compose(property -> {
                // always not null
                that.property = property;
                return this.begin();
            })
            .onComplete(event -> {
                startPromise.complete();
            })
            .onFailure(err -> {
                log.error(err.getMessage(), err.getCause());
                startPromise.fail(err);
            });
    }

    private Future<NetbianProperty> initConfig() {
        JsonObject netbian = this.config().getJsonObject("netbian");
        try {
            return Future.succeededFuture(netbian.mapTo(NetbianProperty.class));
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }


    private void initWallpaperType(String mainUrl) {
        Connection connect = Jsoup.connect(mainUrl);
        try {
            Elements wallpaperTypeLink = connect.get().select(JsoupConstants.A_HREF);
            for (NetBianType type : NetBianType.values()) {
                this.wallpaperType.add(type.description);
            }
            for (Element link : wallpaperTypeLink) {
                final String url = link.attr(JsoupConstants.ABS_HREF);
                final String type = trim(link.text());
                if (this.wallpaperType.contains(type)) {
                    this.wallpaperTypeMap.put(type, url);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    private static String trim(String str) {
        int width = 35;
        if (str.length() > width) {
            return str.substring(0, width - 1) + ".";
        } else {
            return str;
        }
    }

    protected Future<Void> begin() {
        this.initWallpaperType(this.property.getUrl());
        if (!this.property.getStore()) {
            for (Map.Entry<String, String> entry : this.wallpaperTypeMap.entrySet()) {
                try {
                    this.beginExecutor(entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    log.error(e);
                    return Future.failedFuture(e);
                }
            }
        }
        return Future.succeededFuture();
    }

    protected void beginExecutor(String type, String url) throws IOException {
        var pageDocument = Jsoup.connect(url).get();
        var pageElement = pageDocument.select(NetbianConstants.HREF_IMAGE);
        for (Element element : pageElement) {
            var wallpaperPageUrl = element.attr(JsoupConstants.ABS_HREF);
            final String wallpaperId = Jsoup.connect(wallpaperPageUrl)
                                            .get().getElementsByAttribute(NetbianConstants.DATA_ID)
                                            .attr(NetbianConstants.DATA_ID);
            this.beginDownload(wallpaperId, type);
        }

        var nextPageUrl = pageDocument.getElementsContainingOwnText(NetbianConstants.NEXT_PAGE)
                                      .attr(JsoupConstants.ABS_HREF);
        if (!StringUtil.isBlank(nextPageUrl)) {
            this.beginExecutor(type, nextPageUrl);
        }
    }

    protected void beginDownload(String wallpaperId, String type) {
        String downloadUri = this.extractDownloadUri(wallpaperId);
        if (StringUtil.isBlank(downloadUri)) {
            return;
        }
        String url = this.property.getUrl() + downloadUri;
        log.info("Download link：" + url);
        HttpRequest request = HttpRequest.newBuilder()
                                         .GET()
                                         .uri(URI.create(url))
                                         .headers("cookie", this.property.getCookie())
                                         .build();
        try {
            HttpResponse<InputStream> httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            var header = httpResponse.headers()
                                     .firstValue("Content-Disposition")
                                     .orElse(UUID.randomUUID().toString());
            String fileNameFromDisposition = HttpUtil.getFileNameFromDisposition(header);
            if (fileNameFromDisposition != null) {
                try {
                    fileNameFromDisposition = new String(fileNameFromDisposition.getBytes(StandardCharsets.ISO_8859_1), "GB2312");
                    var path = this.getWallpaperFile(type, fileNameFromDisposition);
                    try(var inputStream = httpResponse.body();
                        final var bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path.toFile()))
                    ) {
                        byte[] data = new byte[4 * 1024];
                        int len;
                        while ((len = inputStream.read(data)) != -1) {
                            bufferedOutputStream.write(data, 0, len);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("download failed");
        }

    }

    private Path getWallpaperFile(String type, String filename) {
        return Paths.get(this.USER_HOME, this.property.getWallpaperDirName(), type, filename);
    }


    protected String extractDownloadUri(String wallpaperId){
        String url = this.property.getInfoUrl() + "?t=" + Math.random() + "&id=" + wallpaperId;
        HttpRequest request = HttpRequest.newBuilder()
                                        .GET()
                                        .uri(URI.create(url))
                                        .headers("cookie", this.property.getCookie())
                                        .build();
        try {
            String body = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                                         .body();
            return new JsonObject(body).getString(NetbianConstants.PIC);
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    @Override
    public void stop() throws Exception {
        this.wallpaperType.clear();
        this.wallpaperTypeMap.clear();
    }
}
