package com.zf1976.wallpaper.api.verticle;

import com.zf1976.wallpaper.api.constants.JsoupConstants;
import com.zf1976.wallpaper.api.constants.NetbianConstants;
import com.zf1976.wallpaper.datasource.DbStoreUtil;
import com.zf1976.wallpaper.entity.NetbianEntity;
import com.zf1976.wallpaper.enums.NetBianType;
import com.zf1976.wallpaper.property.NetbianProperty;
import com.zf1976.wallpaper.support.PrintProgressBar;
import com.zf1976.wallpaper.util.HttpUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        this.init()
            .compose(property -> {
                // always not null
                that.property = property;
                return this.begin();
            })
            .onSuccess(event -> {
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }

    private Future<NetbianProperty> init() {
        try {
            return Future.succeededFuture(this.config().mapTo(NetbianProperty.class));
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }


    private Future<Void> initWallpaperType(String mainUrl) {
        Connection connect = Jsoup.connect(mainUrl);
        try {
            Elements wallpaperTypeLink = connect.get().select(JsoupConstants.A_HREF);
            if (this.property.getNetbianType().isEmpty()){
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
            } else {
                this.wallpaperType.addAll(this.property.getNetbianType());
                for (Element link : wallpaperTypeLink) {
                    final String url = link.attr(JsoupConstants.ABS_HREF);
                    final String type = trim(link.text());
                    if (this.wallpaperType.contains(type)) {
                        this.wallpaperTypeMap.put(type, url);
                    }
                }
            }
            return Future.succeededFuture();
        } catch (IOException e) {
            return Future.failedFuture(e);
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
        return this.initWallpaperType(this.property.getUrl())
                   .compose(v -> {
                       if (!this.property.getStore()) {
                           for (Map.Entry<String, String> entry : this.wallpaperTypeMap.entrySet()) {
                               final var future = this.beginExecutor(entry.getKey(), entry.getValue());
                               if (!future.succeeded()) {
                                   return future;
                               }
                           }
                       }
                       return Future.succeededFuture();
                   });
    }

    protected Future<Void> beginExecutor(String type, String url) {
        try {
            var pageDocument = Jsoup.connect(url)
                                    .get();
            var pageElement = pageDocument.select(NetbianConstants.HREF_IMAGE);
            for (Element element : pageElement) {
                var wallpaperPageUrl = element.attr(JsoupConstants.ABS_HREF);
                var wallpaperId = Jsoup.connect(wallpaperPageUrl)
                                       .get()
                                       .getElementsByAttribute(NetbianConstants.DATA_ID)
                                       .attr(NetbianConstants.DATA_ID);
                if (DbStoreUtil.checkNetbianWallpaperId(this.property.getSelectDataIdSql(), wallpaperId)) {
                    if (!this.beginDownload(wallpaperId, type)) {
                        return Future.failedFuture("invalid cookie：" + this.property.getCookie());
                    }
                }
            }

            var nextPageUrl = pageDocument.getElementsContainingOwnText(NetbianConstants.NEXT_PAGE)
                                          .attr(JsoupConstants.ABS_HREF);
            if (!StringUtil.isBlank(nextPageUrl)) {
                return this.beginExecutor(type, nextPageUrl);
            }
            return Future.succeededFuture();
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
    }

    protected boolean beginDownload(String wallpaperId, String type) {
        String downloadUri = this.extractDownloadUri(wallpaperId);
        if (StringUtil.isBlank(downloadUri)) {
            return false;
        }
        String url = this.property.getUrl() + downloadUri;
        HttpRequest request = HttpRequest.newBuilder()
                                         .GET()
                                         .uri(URI.create(url))
                                         .headers("cookie", this.property.getCookie())
                                         .build();
        try {
            HttpResponse<InputStream> httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            var filenameHeader = httpResponse.headers()
                                             .firstValue("Content-Disposition")
                                             .orElse(UUID.randomUUID()
                                                         .toString());
            var contentLength = httpResponse.headers()
                                            .firstValue("content-length")
                                            .orElseThrow();
            final var fileNameFromDisposition = HttpUtil.getFileNameFromDisposition(filenameHeader);
            final var filename = fileNameFromDisposition != null ? new String(fileNameFromDisposition.getBytes(StandardCharsets.ISO_8859_1), "GB2312") : filenameHeader;
            var wallpaperFile = this.getWallpaperFile(type, filename);
            try (var inputStream = httpResponse.body();
                 final var bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(wallpaperFile))
            ) {
                log.info("The file：" + filename + "download complete!");
                log.info("Download link：" + url);
                byte[] data = new byte[4 * 1024];
                int len;
                PrintProgressBar printProgressBar = new PrintProgressBar(Long.parseLong(contentLength));
                while ((len = inputStream.read(data)) != -1) {
                    printProgressBar.printAppend(len);
                    bufferedOutputStream.write(data, 0, len);
                }
                var netbianEntity = new NetbianEntity()
                        .setType(type)
                        .setName(filename)
                        .setDataId(wallpaperId);
                if (!DbStoreUtil.insertNetbianEntity(this.property.getInsertNetbianSql(), netbianEntity)) {
                    if (!Files.deleteIfExists(Paths.get(wallpaperFile.getAbsolutePath()))) {
                        log.warn("delete file: " + wallpaperFile.getAbsolutePath());
                    }
                }
                TimeUnit.SECONDS.sleep(6);
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getWallpaperFile(String type, String filename) {
        final var path = Path.of(this.USER_HOME, this.property.getWallpaperDirName(), type);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Paths.get(path.toAbsolutePath().toFile().getAbsolutePath(), filename).toFile();
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
    public void stop() {
        this.wallpaperType.clear();
        this.wallpaperTypeMap.clear();
    }
}
