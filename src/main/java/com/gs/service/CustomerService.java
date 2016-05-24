package com.gs.service;

import com.gs.bean.Admin;
import com.gs.bean.Customer;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
public interface CustomerService {

    public List<Customer> queryAll();
    public Customer queryById(String id);
    public Customer query(Customer customer);
    public int insert(Customer customer);
    public int update(Customer customer);
    public int batchInsert(List<Customer> customers);
    public List<Customer> queryByPager(Pager pager);
    public int count();

    public int updateLoginTime(String id);
    public int updatePassword(Customer customer);

    public int inactive(String id);
    public int active(String id);

}
