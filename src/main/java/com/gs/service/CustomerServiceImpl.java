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
    public List<Customer> queryAll() {
        return customerDAO.queryAll();
    }

    @Override
    public List<Customer> queryAll(String status) {
        return customerDAO.queryAll(status);
    }

    @Override
    public Customer queryById(String id) {
        return customerDAO.queryById(id);
    }

    @Override
    public Customer query(Customer customer) {
        return customerDAO.query(customer);
    }

    @Override
    public int insert(Customer customer) {
        return customerDAO.insert(customer);
    }

    @Override
    public int update(Customer customer) {
        return customerDAO.update(customer);
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

    @Override
    public int updateLoginTime(String id) {
        return customerDAO.updateLoginTime(id);
    }

    @Override
    public int updatePassword(Customer customer) {
        return customerDAO.updatePassword(customer);
    }

    @Override
    public int updateCheckPwd(Customer customer) {
        return customerDAO.updateCheckPwd(customer);
    }

    public List<Customer> queryByPagerAndCriteria(Pager pager, Customer customer) {
        return customerDAO.queryByPagerAndCriteria(pager, customer);
    }

    public int countByCriteria(Customer customer) {
        return customerDAO.countByCriteria(customer);
    }

    @Override
    public int inactive(String id) {
        return customerDAO.inactive(id);
    }

    @Override
    public int active(String id) {
        return customerDAO.active(id);
    }

    @Override
    public String queryCheckPwdByEmail(String email) {
        return customerDAO.queryCheckPwdByEmail(email);
    }
}
