package com.gs.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by WangGenshen on 8/30/16.
 */
public class PublishResourceDetail {

    private String resourceId;
    private int area;
    private String showType;
    private Date startTime;
    private Date endTime;
    private String stayTime;
    private String segments;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
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

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime;
    }

    public String getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = segments;
    }
}
