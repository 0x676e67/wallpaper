package com.zf1976.wallpaper;

import com.zf1976.wallpaper.api.verticle.NetbianVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

import java.nio.file.Paths;
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
        final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
                .addStore(localConfigStore);
        final Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckIntervalUnit(TimeUnit.DAYS));
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);
        configRetriever.getConfig(config -> {
            if (config.failed()) {
                log.error("Failed to get configuration");
            } else {
                vertx.fileSystem()
                     .readFile(Paths.get(System.getProperty("user.home"), "config.json").toFile().getAbsolutePath())
                     .onComplete(bufferAsyncResult -> {
                         JsonObject conf = config.result();
                         if (bufferAsyncResult.succeeded()) {
                             conf = new JsonObject(bufferAsyncResult.result());
                         }
                         deployNetbian(vertx, conf);
                     });
            }
        });
    }

    private static void deployNetbian(Vertx vertx, JsonObject jsonObject) {
        final var netbian = jsonObject.getJsonObject("netbian");
        Future.<Void>succeededFuture()
              .compose(v -> vertx.deployVerticle(NetbianVerticle.class.getName(), new DeploymentOptions().setWorker(true).setConfig(netbian)))
              .onSuccess(deployId -> {
                  log.info("NetBianVertical deploy ID:" + deployId);
              })
              .onFailure(err -> {
                  log.error(err.getMessage(), err.getCause());
                  System.exit(0);
              });
    }
}
