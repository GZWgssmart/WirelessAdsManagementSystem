package com.gs.service;

import com.gs.bean.Customer;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
public interface CustomerService extends BaseService<Customer, String> {

    public int updateLoginTime(String id);
    public int updatePassword(Customer customer);
    public int updateCheckPwd(Customer customer);

    public List<Customer> queryByPagerAndCriteria(Pager pager, Customer customer);

    public int countByCriteria(Customer customer);

    public String queryCheckPwdByEmail(String email);

}
