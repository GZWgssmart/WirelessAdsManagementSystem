package com.gs.net.parser;

import java.util.Date;

/**
 * Created by WangGenshen on 7/30/16.
 */
public class PublishServer {
    private String type;
    private String devcode;
    private String filename;
    private String restype;
    private int area;
    private String showtype;
    private Date startdate;
    private Date enddate;
    private int showcount;
    private int staytime;
    private int segcount;
    private String segments;
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

    public String getRestype() {
        return restype;
    }

    public void setRestype(String restype) {
        this.restype = restype;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getShowtype() {
        return showtype;
    }

    public void setShowtype(String showtype) {
        this.showtype = showtype;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public int getShowcount() {
        return showcount;
    }

    public void setShowcount(int showcount) {
        this.showcount = showcount;
    }

    public int getStaytime() {
        return staytime;
    }

    public void setStaytime(int staytime) {
        this.staytime = staytime;
    }

    public int getSegcount() {
        return segcount;
    }

    public void setSegcount(int segcount) {
        this.segcount = segcount;
    }

    public String getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = segments;
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
        return "PublishServer{" +
                "type='" + type + '\'' +
                ", devcode='" + devcode + '\'' +
                ", filename='" + filename + '\'' +
                ", restype='" + restype + '\'' +
                ", area=" + area +
                ", showtype='" + showtype + '\'' +
                ", startdate=" + startdate +
                ", enddate=" + enddate +
                ", showcount=" + showcount +
                ", staytime=" + staytime +
                ", segcount=" + segcount +
                ", segments='" + segments + '\'' +
                ", time=" + time +
                ", pubid='" + pubid + '\'' +
                '}';
    }
}
