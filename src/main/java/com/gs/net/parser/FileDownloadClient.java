package com.gs.net.parser;

/**
 * Created by WangGenshen on 7/30/16.
 */
public class FileDownloadClient {
    private String type;
    private String devcode;
    private String filename;
    private String url;
    private String result;
    private String time;
    private String pubid;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    @Override
    public String toString() {
        return "FileDownloadClient{" +
                "type='" + type + '\'' +
                ", devcode='" + devcode + '\'' +
                ", filename='" + filename + '\'' +
                ", url='" + url + '\'' +
                ", result='" + result + '\'' +
                ", time='" + time + '\'' +
                ", pubid='" + pubid + '\'' +
                '}';
    }
}
