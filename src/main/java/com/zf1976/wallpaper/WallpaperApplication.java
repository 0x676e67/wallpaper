package com.zf1976.wallpaper;

import com.zf1976.wallpaper.api.verticle.NetbianVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

/**
 * @author mac
 * @date 2021/6/14
 */
public class WallpaperApplication {

    private static final Logger log = Logger.getLogger("[WallpaperVerticle]");

    public static void main(String[] args) {
        final ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setFormat("json")
                .setConfig(new JsonObject()
                        .put("path", "conf/config.json")
                );
        final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
                .addStore(configStoreOptions);
        final Vertx vertx = Vertx.vertx();
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);
        configRetriever.listen(event -> {
            // 更新配置，总线通知
            vertx.eventBus().publish("wallpaper-config", event.getNewConfiguration());
        });
        configRetriever.getConfig(config -> {
            if (config.failed()) {
                log.error("Failed to get configuration");
            } else {
                Future.<Void>succeededFuture()
                      .compose(v -> vertx.deployVerticle(NetbianVerticle.class.getName(), new DeploymentOptions().setWorker(true).setConfig(config.result())))
                      .onSuccess(deployId -> {
                          log.info("NetBianVerticle deploy ID:" + deployId);
                      })
                      .onFailure(err -> {
                          log.error(err.getMessage(), err.getCause());
                      });
            }
        });

    }
}
