package com.gs.bean;

import java.util.Date;

/**
 * Created by WangGenshen on 6/30/16.
 */
public class Version {

    private String id;
    private String name;
    private int areaCount;
    private String path;
    private String fullPath;
    private String ofileName;
    private String fileName;
    private String des;
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

    public int getAreaCount() {
        return areaCount;
    }

    public void setAreaCount(int areaCount) {
        this.areaCount = areaCount;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getOfileName() {
        return ofileName;
    }

    public void setOfileName(String ofileName) {
        this.ofileName = ofileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
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
