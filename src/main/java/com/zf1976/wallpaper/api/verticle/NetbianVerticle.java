package com.zf1976.wallpaper.api.verticle;

import com.zf1976.wallpaper.api.constants.JsoupConstants;
import com.zf1976.wallpaper.enums.NetBianType;
import com.zf1976.wallpaper.property.NetbianProperty;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.initConfig()
            .onSuccess(property -> {
                if (property != null) {
                    startPromise.complete();
                    this.startDownload(property);
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

    private void initWallpaperWorker(NetbianProperty netbianProperty) {
        Connection connect = Jsoup.connect(netbianProperty.getUrl());
        try {
            Document document = connect.get();
            Elements wallpaperTypeLink = document.select(JsoupConstants.A_HREF);
            for (NetBianType type : NetBianType.values()) {
                this.wallpaperType.add(type.description);
            }
            for (Element link : wallpaperTypeLink) {
            final String url = link.attr(JsoupConstants.ABS_HREF);
            final String type = trim(link.text());
            if (this.wallpaperType.contains(type)){
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

    protected void startDownload(NetbianProperty netbianProperty) {
        this.initWallpaperWorker(netbianProperty);
    }
}
