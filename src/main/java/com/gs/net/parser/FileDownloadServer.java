package com.gs.net.parser;

import java.util.Date;

/**
 * Created by WangGenshen on 7/30/16.
 */
public class FileDownloadServer {

    private String type;
    private String devcode;
    private String filename;
    private String filesize;
    private String url;
    private Date time;
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

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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
        return "FileDownloadServer{" +
                "type='" + type + '\'' +
                ", devcode='" + devcode + '\'' +
                ", filename='" + filename + '\'' +
                ", filesize='" + filesize + '\'' +
                ", url='" + url + '\'' +
                ", time=" + time +
                ", pubid='" + pubid + '\'' +
                '}';
    }
}
