package com.zf1976.wallpaper.property;

import java.util.Arrays;
import java.util.List;

/**
 * @author mac
 * @date 2021/6/14
 */
public class NetbianProperty {

    /**
     * URL
     */
    private String url;
    /**
     * info URL
     */
    private String infoUrl;
    /**
     * downloadUrl
     */
    private String downloadUrl;
    /**
     * beginExecutor directory filename
     */
    private String wallpaperDirName;
    /**
     * cookie
     */
    private String cookie;
    /**
     * store
     */
    private Boolean store;
    /**
     * 记录
     */
    private Boolean record;

    private String selectDataIdSql;

    private String insertNetbianSql;

    private List<String> netbianType;

    public List<String> getNetbianType() {
        return netbianType;
    }

    public void setNetbianType(List<String> netbianType) {
        this.netbianType = netbianType;
    }

    public String getInsertNetbianSql() {
        return insertNetbianSql;
    }

    public NetbianProperty setInsertNetbianSql(String insertNetbianSql) {
        this.insertNetbianSql = insertNetbianSql;
        return this;
    }

    public String getSelectDataIdSql() {
        return selectDataIdSql;
    }

    public NetbianProperty setSelectDataIdSql(String selectDataIdSql) {
        this.selectDataIdSql = selectDataIdSql;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getStore() {
        return store;
    }

    public NetbianProperty setStore(Boolean store) {
        this.store = store;
        return this;
    }

    public NetbianProperty setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public NetbianProperty setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
        return this;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public NetbianProperty setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public String getWallpaperDirName() {
        return wallpaperDirName;
    }

    public NetbianProperty setWallpaperDirName(String wallpaperDirName) {
        this.wallpaperDirName = wallpaperDirName;
        return this;
    }

    public String getCookie() {
        return cookie;
    }

    public NetbianProperty setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public Boolean getRecord() {
        return record;
    }

    public NetbianProperty setRecord(Boolean record) {
        this.record = record;
        return this;
    }

    @Override
    public String toString() {
        return "NetbianProperty{" +
                "url='" + url + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", wallpaperDirName='" + wallpaperDirName + '\'' +
                ", cookie='" + cookie + '\'' +
                ", store=" + store +
                ", record=" + record +
                ", selectDataIdSql='" + selectDataIdSql + '\'' +
                ", insertNetbianSql='" + insertNetbianSql + '\'' +
                ", netbianType=" + netbianType +
                '}';
    }
}
