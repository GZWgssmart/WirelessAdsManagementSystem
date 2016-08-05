package com.gs.net.parser;

import java.util.Date;

/**
 * Created by WangGenshen on 7/30/16.
 */
public class HeartBeatClient {
    private String type;
    private String devcode;
    private String firstbeat;
    private String time;

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

    public String getFirstbeat() {
        return firstbeat;
    }

    public void setFirstbeat(String firstbeat) {
        this.firstbeat = firstbeat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HeartBeatClient{" +
                "type='" + type + '\'' +
                ", devcode='" + devcode + '\'' +
                ", firstbeat='" + firstbeat + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
