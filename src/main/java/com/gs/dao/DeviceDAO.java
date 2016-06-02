package com.gs.dao;

import com.gs.bean.Device;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
public interface DeviceDAO extends BaseDAO<Device, String> {

    public List<Device> queryByPagerAndCustomerId(@Param("pager") Pager pager, @Param("customerId") String customerId);

    public List<Device> queryByPagerAndCriteria(@Param("pager")Pager pager,
                                                  @Param("device") Device device,
                                                  @Param("customerId")String customerId);

    public int countByCriteria(@Param("device") Device device, @Param("customerId") String customerId);

    public String queryByDeviceId(String id);

}
