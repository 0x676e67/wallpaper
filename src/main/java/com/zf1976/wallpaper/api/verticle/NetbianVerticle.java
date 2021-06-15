package com.zf1976.wallpaper.api.verticle;

import com.zf1976.wallpaper.api.constants.JsoupConstants;
import com.zf1976.wallpaper.api.constants.NetbianConstants;
import com.zf1976.wallpaper.entity.NetbianEntity;
import com.zf1976.wallpaper.enums.NetBianType;
import com.zf1976.wallpaper.property.NetbianProperty;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.initConfig()
            .onSuccess(property -> {
                if (property != null) {
                    this.property = property;
                    this.begin()
                        .onSuccess(event -> {
                            startPromise.complete();
                        }).onFailure(startPromise::fail);
                } else {
                    startPromise.fail("Failed to extract configuration");
                }
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
    private Future<NetbianProperty> demo() {
        var netbian = this.config()
                          .getJsonObject("netbian");
        var promise = Promise.<NetbianProperty>promise();
        var netbianProperty = netbian.mapTo(NetbianProperty.class);
        promise.complete(netbianProperty);
        return promise.future();
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
        if (this.property.getStore()) {

        } else {
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
            var wallpaperDocument = Jsoup.connect(wallpaperPageUrl).get();
            final String wallpaperId = wallpaperDocument.getElementsByAttribute(NetbianConstants.DATA_ID)
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
        System.out.println(wallpaperId);
    }


    protected String extractDocumentType(String wallpaperId){

        return null;
    }
}
