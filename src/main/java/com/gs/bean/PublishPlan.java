package com.gs.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by WangGenshen on 8/17/16.
 */
public class PublishPlan {

    private String id;
    private String planName;
    private String customerId;
    private String groupName;
    private String versionId;
    private String versionName;
    private String resourceId;
    private String resourceName;
    private Resource resource;
    private String name;
    private String type;
    private String des;
    private Date submitCheckTime;
    private String checkComment;
    private Date checkTime;
    private String checkStatus;
    private Date createTime;
    private String status;

    private int devCount;
    private int finishCount;
    private int notFinishCount;


    private String deviceId;
    private String deviceCode;

    private String resourceDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVersionId() {
        return versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Date getSubmitCheckTime() {
        return submitCheckTime;
    }

    public void setSubmitCheckTime(Date submitCheckTime) {
        this.submitCheckTime = submitCheckTime;
    }

    public String getCheckComment() {
        return checkComment;
    }

    public void setCheckComment(String checkComment) {
        this.checkComment = checkComment;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDevCount() {
        return devCount;
    }

    public void setDevCount(int devCount) {
        this.devCount = devCount;
    }

    public int getFinishCount() {
        return finishCount;
    }

    public void setFinishCount(int finishCount) {
        this.finishCount = finishCount;
    }

    public int getNotFinishCount() {
        return notFinishCount;
    }

    public void setNotFinishCount(int notFinishCount) {
        this.notFinishCount = notFinishCount;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getResourceDetails() {
        return resourceDetails;
    }

    public void setResourceDetails(String resourceDetails) {
        this.resourceDetails = resourceDetails;
    }
}
