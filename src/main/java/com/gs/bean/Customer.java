package com.gs.bean;

import java.util.Date;

/**
 * Created by WangGenshen on 5/16/16.
 */
public class Customer extends User {

    private String address;
    private Date lastUpdateTime;
    private String lastUpdateByRole;
    private String lastUpdateByAdmin;
    private String status;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateByRole() {
        return lastUpdateByRole;
    }

    public void setLastUpdateByRole(String lastUpdateByRole) {
        this.lastUpdateByRole = lastUpdateByRole;
    }

    public String getLastUpdateByAdmin() {
        return lastUpdateByAdmin;
    }

    public void setLastUpdateByAdmin(String lastUpdateByAdmin) {
        this.lastUpdateByAdmin = lastUpdateByAdmin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
