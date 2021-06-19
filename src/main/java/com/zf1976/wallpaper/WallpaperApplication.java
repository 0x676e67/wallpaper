package com.zf1976.wallpaper;

import com.zf1976.wallpaper.api.verticle.NetbianVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * @date 2021/6/14
 */
public class WallpaperApplication {

    private static final Logger log = Logger.getLogger("[WallpaperApplication]");

    public static void main(String[] args) {
        final var localConfigStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setFormat("json")
                .setConfig(new JsonObject()
                        .put("path", "config.json")
                );
        final var customNetbianType = new ConfigStoreOptions()
                .setType("json")
                .setConfig(new JsonObject().put("netbianType", args));
        final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
                .addStore(localConfigStore)
                .addStore(customNetbianType);
        final Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckIntervalUnit(TimeUnit.DAYS));
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);
        configRetriever.listen(event -> {
            // 更新配置，总线通知
            vertx.eventBus().publish("wallpaper-config", event.getNewConfiguration());
        });
        configRetriever.getConfig(config -> {
            if (config.failed()) {
                log.error("Failed to get configuration");
            } else {
                JsonObject netbian = config.result()
                                           .getJsonObject("netbian");
                netbian.put("netbianType", Arrays.asList(args));
                Future.<Void>succeededFuture()
                      .compose(v -> vertx.deployVerticle(NetbianVerticle.class.getName(), new DeploymentOptions().setWorker(true).setConfig(netbian)))
                      .onSuccess(deployId -> {
                          log.info("NetBianVertical deploy ID:" + deployId);
                      })
                      .onFailure(err -> {
                          log.error(err.getMessage(), err.getCause());
                      });
            }
        });

    }
}
