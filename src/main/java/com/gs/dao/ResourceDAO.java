package com.gs.dao;

import com.gs.bean.Resource;
import com.gs.bean.ResourceType;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 5/26/16.
 */
@Repository
public interface ResourceDAO extends BaseDAO<Resource, String> {

    public List<Resource> queryByPagerAndCustomerId(@Param("pager") Pager pager, @Param("customerId") String customerId);

    public List<Resource> queryByPagerAndCriteria(@Param("pager")Pager pager,
                                                  @Param("resource") Resource resource,
                                                  @Param("customerId")String customerId);

    public int countByCriteria(@Param("resource") Resource resource, @Param("customerId") String customerId);

    public String queryByResourceId(String id);

    public Resource queryByName(String name);

    public Resource queryByNameAndCustomer(@Param("name") String name, @Param("customerId") String customerId);

    public List<Resource> queryByNameNotSelf(@Param("resource") Resource resource);

}
