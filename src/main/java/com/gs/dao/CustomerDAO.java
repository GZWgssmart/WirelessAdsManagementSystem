package com.gs.dao;

import com.gs.bean.Customer;
import org.springframework.stereotype.Repository;

/**
 * Created by WangGenshen on 5/16/16.
 */
@Repository
public interface CustomerDAO extends BaseDAO<Customer,String> {

    public Customer queryByEmailPwd(Customer customer);

}
