package com.gs.bean;

import java.util.Date;

/**
 * Created by WangGenshen on 5/17/16.
 */
public class ResourceType {

    private String id;
    private String name;
    private String extension;
    private String des;
    private String showDetailSetting;
    private Date createTime;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getShowDetailSetting() {
        return showDetailSetting;
    }

    public void setShowDetailSetting(String showDetailSetting) {
        this.showDetailSetting = showDetailSetting;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
