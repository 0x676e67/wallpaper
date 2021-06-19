package com.zf1976.wallpaper.entity;

import io.vertx.codegen.annotations.DataObject;


/**
 * @author mac
 * Create by Ant on 2020/8/19 下午6:17
 */
@DataObject
public class NetbianEntity {

    private Integer id;

    private String dataId;

    private String type;

    private String name;

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

    public String getName() {
        return name;
    }

    public NetbianEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public NetbianEntity setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "NetbianEntity{" +
                "id=" + id +
                ", dataId='" + dataId + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
