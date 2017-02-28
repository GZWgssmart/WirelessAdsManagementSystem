package com.gs.bean;

import java.util.Date;

/**
 * Created by WangGenshen on 5/16/16.
 */
public class Customer extends User {

    private String address;
    private String company;
    private Date lastUpdateTime;
    private String lastUpdateByRole;
    private String lastUpdateByAdmin;
    private String checkPwd;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getCheckPwd() {
        return checkPwd;
    }

    public void setCheckPwd(String checkPwd) {
        this.checkPwd = checkPwd;
    }
}
