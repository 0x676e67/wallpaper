package com.zf1976.wallpaper.property;

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
     * download directory filename
     */
    private String wallpaperDirName;
    /**
     * cookie
     */
    private String cookie;

    public String getUrl() {
        return url;
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

    @Override
    public String toString() {
        return "NetbianProperty{" +
                "url='" + url + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", wallpaperDirName='" + wallpaperDirName + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}
