package com.gs.dao;

import com.gs.bean.Customer;
import com.gs.bean.Version;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 6/30/16.
 */
@Repository
public interface VersionDAO extends BaseDAO<Version,String> {

    public List<Version> queryByPagerAndCriteria(@Param("pager") Pager pager,
                                                  @Param("version") Version version);

    public int countByCriteria(@Param("version") Version version);

    public List<Version> queryByCustomerAndGroupById(String customerId);
}
