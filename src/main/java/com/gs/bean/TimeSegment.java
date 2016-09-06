package com.gs.bean;

import java.util.Date;

/**
 * Created by WangGenshen on 7/15/16.
 */
public class TimeSegment {

    private String id;
    private String pubId;
    private String startTime;
    private String endTime;
    private int queueOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getQueueOrder() {
        return queueOrder;
    }

    public void setQueueOrder(int queueOrder) {
        this.queueOrder = queueOrder;
    }
}
