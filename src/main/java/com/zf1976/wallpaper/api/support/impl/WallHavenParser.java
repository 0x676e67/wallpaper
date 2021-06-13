package com.zf1976.wallpaper.api.support.impl;


import com.zf1976.wallpaper.api.support.AbstractParser;
import com.zf1976.wallpaper.api.build.BaseBuilder;
import com.zf1976.wallpaper.api.constants.JsoupConstants;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.Proxy;

/**
 * @author mac
 * @date 2021/3/20
 **/
public class WallHavenParser extends AbstractParser {

    private WallHavenParser(Builder builder) {
        super(builder);
    }

    /**
     * 获取首页latest元素
     *
     * @date 2021-03-20 23:23:15
     * @return org.jsoup.nodes.Element
     */
    public Element selectHomePageLatest() {
        return this.selectByClass("latest").first();
    }

    /**
     * 获取首页排行榜元素
     *
     * @date 2021-03-20 23:20:59
     * @return org.jsoup.nodes.Element
     */
    public Element selectHomePageTopList() {
        return this.selectByClass("toplist").first();
    }

    /**
     * 获取首页随机元素
     *
     * @date 2021-03-20 23:18:50
     * @return org.jsoup.nodes.Element
     */
    public Element selectHomePageRandom(){
        return this.selectByClass("random").first();
    }

    /**
     * 获取MoreTags
     *
     * @date 2021-03-20 23:18:36
     * @return org.jsoup.nodes.Element
     */
    public Element selectHomePageMoreTags(){
        return this.selectHomePageTags().last();
    }

    /**
     * 获取首页活跃元素
     *
     * @date 2021-03-20 23:43:58
     * @return org.jsoup.select.Elements
     */
    public Elements selectHomePageFeatRow() {
        return this.selectByClass("feat-row")
                   .select(JsoupConstants.A_HREF);
    }

    /**
     * 获取首页tagList，排除MoreTags
     *
     * @date 2021-03-20 23:18:20
     * @return org.jsoup.select.Elements
     */
    public Elements selectHomePageTagList() {
        Elements elements = this.selectHomePageTags();
        if (elements.remove(selectHomePageMoreTags())) {
            return elements;
        }
        return null;
    }

    /**
     * 获取首页tags元素集合
     *
     * @author mac
     * @date 2021-03-20 23:15:42
     * @return org.jsoup.select.Elements
     */
    private Elements selectHomePageTags(){
        return this.document()
                   .getElementsByClass("pop-tags")
                   .select(JsoupConstants.A_HREF);
    }

    /**
     * 获取包含css
     *
     * @date 2021-03-20 23:24:48
     * @param clazz clazz
     * @return org.jsoup.select.Elements
     */
    private Elements selectByClass(String clazz) {
        return this.document().getElementsByClass(clazz);
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

    /**
     * 构建
     *
     * @author mac
     * @date 2021-03-20 23:17:37
     * @return com.zf1976.wallpaper.api.support.impl.WallHavenParser.Builder
     */
    public static Builder builder(){
        return new Builder();
    }

}
