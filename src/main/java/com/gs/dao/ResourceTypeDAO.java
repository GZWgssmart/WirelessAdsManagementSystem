package com.gs.dao;

import com.gs.bean.ResourceType;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 5/25/16.
 */
@Repository
public interface ResourceTypeDAO extends BaseDAO<ResourceType, String> {

    public List<ResourceType> queryByPagerAndCustomerId(@Param("pager") Pager pager, @Param("customerId") String customerId);
}
