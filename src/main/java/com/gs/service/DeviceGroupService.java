package com.gs.service;

import com.gs.bean.DeviceGroup;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/25/16.
 */
public interface DeviceGroupService extends BaseService<DeviceGroup, String> {

    public List<DeviceGroup> queryByPagerAndCustomerId(Pager pager, String customerId);

    public List<DeviceGroup> queryAllByCustomerId(String customerId, String status);

    public int countByCustomerId(String customerId);

}
