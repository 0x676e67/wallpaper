package com.zf1976.wallpaper.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

/**
 * @author mac
 * @date 2021/6/14
 */
public class NetBianVerticle extends AbstractVerticle {

    private EventBus eventBus;
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.eventBus = vertx.eventBus();
        Vertx vertx = super.getVertx();
        Context orCreateContext = vertx.getOrCreateContext();
        System.out.println(orCreateContext.config());
        startPromise.complete();
    }
}
