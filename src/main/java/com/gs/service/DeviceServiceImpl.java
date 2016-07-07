package com.gs.service;

import com.gs.bean.Device;
import com.gs.common.bean.Pager;
import com.gs.dao.DeviceDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
@Service
public class DeviceServiceImpl implements DeviceService {
    @Resource
    private DeviceDAO deviceDAO;

    @Override
    public List<Device> queryAll() {
        return null;
    }

    @Override
    public Device queryById(String s) {
        return null;
    }

    @Override
    public Device query(Device device) {
        return null;
    }

    @Override
    public int insert(Device device) {
        return deviceDAO.insert(device);
    }

    @Override
    public int update(Device device) {
        return deviceDAO.update(device);
    }

    @Override
    public List<Device> queryByPager(Pager pager) {
        return deviceDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return deviceDAO.count();
    }

    @Override
    public int inactive(String s) {
        return deviceDAO.inactive(s);
    }

    @Override
    public int active(String s) {
        return deviceDAO.active(s);
    }

    @Override
    public int batchInsert(List<Device> devices) {
        return 0;
    }

    @Override
    public List<Device> queryByPagerAndCustomerId(Pager pager, String customerId) {
        return deviceDAO.queryByPagerAndCustomerId(pager, customerId);
    }

    @Override
    public List<Device> queryByPagerAndCriteria(Pager pager, Device device, String customerId) {
        return deviceDAO.queryByPagerAndCriteria(pager, device, customerId);
    }

    @Override
    public int countByCriteria(Device device, String customerId) {
        return deviceDAO.countByCriteria(device, customerId);
    }

    @Override
    public String queryByDeviceId(String id) {
        return deviceDAO.queryByDeviceId(id);
    }

    @Override
    public Device queryByCode(String code) {
        return deviceDAO.queryByCode(code);
    }
}
