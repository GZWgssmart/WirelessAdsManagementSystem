package com.gs.bean;

import java.util.Date;

/**
 * Created by WangGenshen on 10/9/16.
 */
public class PubResource {

    public static final String DELETED = "已删除";
    public static final String CAN_NOT_DELETED = "不可删除";
    public static final String CAN_DELETED = "可删除";
    public static final String DELETING = "删除中";

    public static final String CAN_DELETE_MSG = "此资源已经完成发布，可以删除";
    public static final String CAN_NOT_DELETE_MSG = "此资源没有完成发布或已提交删除或正在删除中或已删除，无需删除";

    private String id;
    private String resId;
    private String name;
    private String deleteStatus;
    private String des;

    private String resType;
    private Date startTime;
    private Date endTime;
    private String showType;
    private String showCount;
    private String stayTime;
    private Date publishTime;
    private Date deleteTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getShowCount() {
        return showCount;
    }

    public void setShowCount(String showCount) {
        this.showCount = showCount;
    }

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PubResource that = (PubResource) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
