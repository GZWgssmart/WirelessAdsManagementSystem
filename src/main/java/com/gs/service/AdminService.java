package com.gs.service;

import com.gs.bean.Admin;
import com.gs.bean.User;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
public interface AdminService {

    public List<Admin> query();
    public Admin queryById(String id);
    public int insert(Admin admin);
    public int batchInsert(List<Admin> admins);
    public List<Admin> queryByPager(Pager pager);
    public int count();

}
