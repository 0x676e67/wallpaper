package com.zf1976.wallpaper.api.build;

import org.jsoup.Connection;

import java.net.Proxy;

/**
 * @author ant
 * Create by Ant on 2021/3/20 8:03 PM
 */
public class BaseBuilder{

    protected String url;
    protected Connection.Method method = Connection.Method.GET;
    protected Proxy proxy = Proxy.NO_PROXY;
    protected int timeout;

}
