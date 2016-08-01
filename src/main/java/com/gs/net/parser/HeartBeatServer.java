package com.gs.net.parser;

import java.util.Date;

/**
 * Created by WangGenshen on 7/30/16.
 */
public class HeartBeatServer {
    private String type;
    private String devcode;
    private Date time;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevcode() {
        return devcode;
    }

    public void setDevcode(String devcode) {
        this.devcode = devcode;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HeartBeatServer{" +
                "type='" + type + '\'' +
                ", devcode='" + devcode + '\'' +
                ", time=" + time +
                '}';
    }
}
