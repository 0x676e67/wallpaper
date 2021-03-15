package com.zf1976.wallpaper.enums;

/**
 * @author ant
 * Create by Ant on 2020/8/15 下午9:11
 */
public enum PropertiesEnum {

    //下一页
    NEXT_PAGE("下一页"),
    //属性key
    ATTRIBUTE("data-id"),
    // cookie
    COOKIE("Cookie");

    public final String content;

    PropertiesEnum(String content){

        this.content = content;
    }
}
