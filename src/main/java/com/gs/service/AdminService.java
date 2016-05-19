package com.gs.service;

import com.gs.bean.Admin;
import com.gs.bean.User;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
public interface AdminService {

    public List<Admin> queryAll();
    public Admin queryById(String id);
    public Admin query(Admin admin);
    public int insert(Admin admin);
    public int update(Admin admin);
    public int inactive(String id);
    public int active(String id);
    public int batchInsert(List<Admin> admins);
    public List<Admin> queryByPager(Pager pager);
    public int count();

}
