package com.gs.service;

import com.gs.bean.DeviceGroup;
import com.gs.bean.ResourceType;
import com.gs.common.bean.Pager;
import com.gs.dao.DeviceGroupDAO;
import com.gs.dao.ResourceTypeDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WangGenshen on 5/25/16.
 */
@Service
public class DeviceGroupServiceImpl implements DeviceGroupService {

    @Resource
    private DeviceGroupDAO deviceGroupDAO;

    @Override
    public List<DeviceGroup> queryAll() {
        return deviceGroupDAO.queryAll();
    }

    @Override
    public List<DeviceGroup> queryAll(String status) {
        return deviceGroupDAO.queryAll(status);
    }

    @Override
    public DeviceGroup queryById(String s) {
        return null;
    }

    @Override
    public DeviceGroup query(DeviceGroup deviceGroup) {
        return null;
    }

    @Override
    public int insert(DeviceGroup deviceGroup) {
        return deviceGroupDAO.insert(deviceGroup);
    }

    @Override
    public int update(DeviceGroup deviceGroup) {
        return deviceGroupDAO.update(deviceGroup);
    }

    @Override
    public List<DeviceGroup> queryByPager(Pager pager) {
        return deviceGroupDAO.queryByPager(pager);
    }

    @Override
    public List<DeviceGroup> queryByPagerAndCustomerId(Pager pager, String customerId) {
        return deviceGroupDAO.queryByPagerAndCustomerId(pager, customerId);
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public int inactive(String id) {
        return deviceGroupDAO.inactive(id);
    }

    @Override
    public int active(String id) {
        return deviceGroupDAO.active(id);
    }

    @Override
    public int batchInsert(List<DeviceGroup> deviceGroups) {
        return 0;
    }

    @Override
    public List<DeviceGroup> queryAllByCustomerId(String customerId, String status) {
        return deviceGroupDAO.queryAllByCustomerId(customerId, status);
    }

    @Override
    public int countByCustomerId(String customerId) {
        return deviceGroupDAO.countByCustomerId(customerId);
    }
}
