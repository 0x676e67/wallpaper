package com.zf1976.wallpaper.api.impl;


import com.zf1976.wallpaper.api.Parser;
import com.zf1976.wallpaper.api.build.BaseBuilder;
import com.zf1976.wallpaper.api.constant.JsoupConstants;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.Proxy;

/**
 * @author mac
 * @date 2021/3/20
 **/
public class WallHavenParser extends Parser {

    private WallHavenParser(Builder builder) {
        super(builder);
    }

    public Elements selectHomePageLatest(){
        return this.selectByClass("latest");
    }

    public Elements selectHomePageTopList(){
        return this.selectByClass("toplist");
    }

    public Elements selectHomePageRandom(){
        return this.selectByClass("random");
    }

    public Element selectHomePageMoreTags(){
        return this.selectHomePageTags().last();
    }

    private Elements selectByClass(String clazz) {
        return this.document().getElementsByClass(clazz);
    }

    /**
     * 获取首页tags元素集合
     *
     * @return elements
     */
    public Elements selectHomePageTags(){
        return this.document()
                   .getElementsByClass("pop-tags")
                   .select(JsoupConstants.A_HREF);
    }

    public static class Builder extends BaseBuilder {

        public String getUrl() {
            return url;
        }

        public Connection.Method getMethod() {
            return method;
        }

        public Proxy getProxy() {
            return proxy;
        }

        public int getTimeout() {
            return timeout;
        }

        public Builder method(Connection.Method method) {
            this.method = method;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public  Builder url(String url) {
            this.url = url;
            return this;
        }

        public  Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public WallHavenParser build(){
            return new WallHavenParser(this);
        }
    }

    public static Builder builder(){
        return new Builder();
    }

}
