package com.gs.net.parser;

import java.util.Date;

/**
 * Created by WangGenshen on 7/30/16.
 */
public class PublishClient {
    private String type;
    private String devcode;
    private String filename;
    private String restype;
    private String time;
    private String pubid;
    private String result;

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRestype() {
        return restype;
    }

    public void setRestype(String restype) {
        this.restype = restype;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "PublishClient{" +
                "type='" + type + '\'' +
                ", devcode='" + devcode + '\'' +
                ", filename='" + filename + '\'' +
                ", restype='" + restype + '\'' +
                ", time='" + time + '\'' +
                ", pubid='" + pubid + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
