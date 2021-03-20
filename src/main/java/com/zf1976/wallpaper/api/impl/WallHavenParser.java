package com.zf1976.wallpaper.api.impl;

import com.zf1976.wallpaper.api.AbstractParser;
import com.zf1976.wallpaper.api.constant.JsoupConstants;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author mac
 * @date 2021/3/20
 **/
public class WallHavenParser extends AbstractParser {

    public WallHavenParser(String url) {
        super(url);
    }

    /**
     * 获取首页tags元素集合
     *
     * @return elements
     */
    public Elements selectHomePageTags(){
        try {
            return this.connection.get()
                                  .getElementsByClass("pop-tags")
                                  .select(JsoupConstants.A_HREF);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
