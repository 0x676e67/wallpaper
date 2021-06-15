package com.zf1976.wallpaper.entity;

/**
 * @author mac
 * Create by Ant on 2020/8/19 下午6:17
 */

public class NetbianEntity {

    private Integer id;

    private String dataId;

    public String getDataId() {
        return dataId;
    }

    public NetbianEntity setDataId(String dataId) {
        this.dataId = dataId;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public NetbianEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "NetbianEntity{" +
                "id=" + id +
                ", dataId='" + dataId + '\'' +
                '}';
    }
}
