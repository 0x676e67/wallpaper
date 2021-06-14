package com.zf1976.wallpaper;

import com.zf1976.wallpaper.verticle.NetBianVerticle;
import io.vertx.core.*;
import org.apache.log4j.Logger;

/**
 * @author mac
 * @date 2021/6/14
 */
public class VertxApplication {

    private static final Logger log = Logger.getLogger("[MainVerticle]");

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Future.<Void>succeededFuture()
              .compose(v -> vertx.deployVerticle(new NetBianVerticle(), new DeploymentOptions().setWorker(true)))
              .onSuccess(deployId -> {
                  log.info("NetBianVerticle deploy ID:" + deployId);
              });
    }
}
