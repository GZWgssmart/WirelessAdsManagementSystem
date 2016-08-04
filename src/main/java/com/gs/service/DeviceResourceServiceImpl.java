package com.gs.service;

import com.gs.bean.Device;
import com.gs.bean.DeviceResource;
import com.gs.common.bean.Pager;
import com.gs.dao.DeviceResourceDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
@Service
public class DeviceResourceServiceImpl implements DeviceResourceService {
    @Resource
    private DeviceResourceDAO deviceResourceDAO;

    @Override
    public List<DeviceResource> queryAll() {
        return deviceResourceDAO.queryAll();
    }

    @Override
    public List<DeviceResource> queryAll(String status) {
        return deviceResourceDAO.queryAll(status);
    }

    @Override
    public DeviceResource queryById(String s) {
        return null;
    }

    @Override
    public DeviceResource query(DeviceResource deviceResource) {
        return null;
    }

    @Override
    public int insert(DeviceResource deviceResource) {
        return deviceResourceDAO.insert(deviceResource);
    }

    @Override
    public int update(DeviceResource deviceResource) {
        return deviceResourceDAO.update(deviceResource);
    }

    @Override
    public List<DeviceResource> queryByPager(Pager pager) {
        return deviceResourceDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return deviceResourceDAO.count();
    }

    @Override
    public int inactive(String s) {
        return deviceResourceDAO.inactive(s);
    }

    @Override
    public int active(String s) {
        return deviceResourceDAO.active(s);
    }

    @Override
    public int batchInsert(List<DeviceResource> deviceResources) {
        return deviceResourceDAO.batchInsert(deviceResources);
    }

    @Override
    public List<DeviceResource> queryByPagerAndCustomerId(Pager pager, String customerId) {
        return deviceResourceDAO.queryByPagerAndCustomerId(pager, customerId);
    }

    @Override
    public List<DeviceResource> queryByPagerAndCriteria(Pager pager, DeviceResource deviceResource, String customerId) {
        return deviceResourceDAO.queryByPagerAndCriteria(pager, deviceResource, customerId);
    }

    @Override
    public int countByCriteria(DeviceResource deviceResource, String customerId) {
        return deviceResourceDAO.countByCriteria(deviceResource, customerId);
    }

    @Override
    public String queryByDeviceId(String id) {
        return deviceResourceDAO.queryByDeviceId(id);
    }

    @Override
    public int check(String id, String checkStatus) {
        return deviceResourceDAO.check(id, checkStatus);
    }

    @Override
    public DeviceResource queryWithDeviceResourceById(String drid) {
        return deviceResourceDAO.queryWithDeviceResourceById(drid);
    }

    @Override
    public List<DeviceResource> queryWithDeviceResourceByCode(String code) {
        return deviceResourceDAO.queryWithDeviceResourceByCode(code);
    }

    @Override
    public int updatePublishLog(String id, String publishLog) {
        return deviceResourceDAO.updatePublishLog(id, publishLog);
    }
}
