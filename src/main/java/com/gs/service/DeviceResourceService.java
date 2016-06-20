package com.gs.service;

import com.gs.bean.Device;
import com.gs.bean.DeviceResource;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
public interface DeviceResourceService extends BaseService<DeviceResource, String> {

    public List<DeviceResource> queryByPagerAndCustomerId(Pager pager, String customerId);

    public List<DeviceResource> queryByPagerAndCriteria(Pager pager, DeviceResource deviceResource, String customerId);

    public int countByCriteria(DeviceResource deviceResource, String customerId);

    public String queryByDeviceId(String id);

    public int check(String id, String checkStatus);

}
