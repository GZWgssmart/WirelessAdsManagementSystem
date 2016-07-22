package com.gs.dao;

import com.gs.bean.DeviceGroup;
import com.gs.bean.ResourceType;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Created by WangGenshen on 5/25/16.
 */
@Repository
public interface DeviceGroupDAO extends BaseDAO<DeviceGroup, String> {

    public List<DeviceGroup> queryByPagerAndCustomerId(@Param("pager") Pager pager, @Param("customerId") String customerId);

    public List<DeviceGroup> queryAllByCustomerId(@Param("customerId") String customerId, @Param("status") String status);

    public int countByCustomerId(String customerId);
}
