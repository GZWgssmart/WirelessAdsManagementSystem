package com.gs.service;

import com.gs.bean.Device;
import com.gs.bean.Resource;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
public interface DeviceService extends BaseService<Device, String> {

    public List<Device> queryByPagerAndCustomerId(Pager pager, String customerId);

    public List<Device> queryByPagerAndCriteria(Pager pager, Device device, String customerId);

    public int countByCriteria(Device device, String customerId);

    public Device queryByCode(String code);

    public void updateDeviceStatus(String devCode, String status);

    public int updatePublishTime(Device device);

    public List<Device> queryByCodeNotSelf(Device device);

}
