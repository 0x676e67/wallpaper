package com.zf1976.wallpaper.enums;

/**
 * @author ant
 * Create by Ant on 2020/8/15 下午7:48
 */
public enum TypeEnum {

    //风景 scenery
 //   SCENERY(1,"4K风景"),

    //美女 beauty
    BEAUTY(2,"4K美女"),

    //game
    GAME(3,"4K游戏"),

    //Anime
    ANIME(4,"4K动漫"),

    //影视
    FILM_AND_TELEVISION(5,"4K影视"),

    //明星
    STAR(6,"4K明星"),

    //汽车
    CAR(7,"4K汽车"),

    //动物
    ANIMAL(8,"4K动物"),

    //人物
    FIGURE(9,"4K人物"),

    //美食
 //   GOURMET(10,"4K美食"),

    //宗教
    RELIGION(11,"4K宗教");

    //背景
//    BACKGROUND(12,"4K背景");

    public final int index;

    public final String description;

    TypeEnum(int index, String description) {
        this.index = index;
        this.description = description;
    }


    @Override
    public String toString() {
        return this.description;
    }
}
