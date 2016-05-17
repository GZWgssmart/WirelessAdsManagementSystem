package com.gs.service;

import com.gs.bean.Admin;
import com.gs.bean.Customer;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
public interface CustomerService {

    public List<Customer> query();
    public Customer queryById(String id);
    public Customer queryByEmailPwd(Customer customer);
    public int insert(Customer customer);
    public int batchInsert(List<Customer> customers);
    public List<Customer> queryByPager(Pager pager);
    public int count();

}
