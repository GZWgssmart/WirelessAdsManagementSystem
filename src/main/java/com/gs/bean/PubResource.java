package com.gs.bean;

/**
 * Created by WangGenshen on 10/9/16.
 */
public class PubResource {

    public static final String DELETED = "已删除";
    public static final String CAN_NOT_DELETED = "不可删除";
    public static final String CAN_DELETED = "可删除";
    public static final String DELETING = "删除中";

    public static final String CAN_DELETE_MSG = "此资源已经完成发布，可以删除";
    public static final String CAN_NOT_DELETE_MSG = "此资源没有完成发布或正在删除中或已删除，无需删除";

    private String id;
    private String name;
    private String deleteStatus;
    private String des;

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
