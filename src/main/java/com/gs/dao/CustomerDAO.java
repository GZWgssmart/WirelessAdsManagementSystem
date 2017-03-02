package com.gs.dao;

import com.gs.bean.Customer;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
@Repository
public interface CustomerDAO extends BaseDAO<Customer,String> {

    public int updateLoginTime(String id);
    public int updatePassword(Customer customer);
    public int updateCheckPwd(Customer customer);

    public List<Customer> queryByPagerAndCriteria(@Param("pager")Pager pager,
                                                @Param("customer") Customer customer);

    public int countByCriteria(@Param("customer") Customer customer);

    public String queryCheckPwdByEmail(String email);
}
