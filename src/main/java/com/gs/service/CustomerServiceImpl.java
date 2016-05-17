package com.gs.service;

import com.gs.bean.Admin;
import com.gs.bean.Customer;
import com.gs.bean.User;
import com.gs.common.bean.Pager;
import com.gs.dao.AdminDAO;
import com.gs.dao.CustomerDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerDAO customerDAO;

    @Override
    public List<Customer> query() {
        return customerDAO.query();
    }

    @Override
    public Customer queryById(String id) {
        return customerDAO.queryById(id);
    }

    @Override
    public Customer queryByEmailPwd(Customer customer) {
        return customerDAO.queryByEmailPwd(customer);
    }

    @Override
    public int insert(Customer customer) {
        return customerDAO.insert(customer);
    }

    @Override
    public int batchInsert(List<Customer> customers) {
        return customerDAO.batchInsert(customers);
    }

    @Override
    public List<Customer> queryByPager(Pager pager) {
        return customerDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return customerDAO.count();
    }
}
