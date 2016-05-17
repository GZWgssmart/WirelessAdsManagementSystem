package com.gs.dao;

import com.gs.bean.Customer;

/**
 * Created by WangGenshen on 5/16/16.
 */
public interface CustomerDAO extends BaseDAO<Customer,String> {

    public Customer queryByEmailPwd(Customer customer);

}
