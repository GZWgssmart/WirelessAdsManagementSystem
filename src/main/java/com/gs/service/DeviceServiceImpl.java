package com.gs.service;

import com.gs.bean.Device;
import com.gs.common.bean.Pager;
import com.gs.dao.DeviceDAO;
import com.gs.net.bean.ADSSocket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
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
        return deviceDAO.queryAll();
    }

    @Override
    public List<Device> queryAll(String status) {
        return deviceDAO.queryAll(status);
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
    public Device queryByCode(String code) {
        return deviceDAO.queryByCode(code);
    }

    public void updateDeviceStatus(String devCode, String status) {
        Device device = new Device();
        device.setCode(devCode);
        device.setOnline(status);
        Date time = Calendar.getInstance().getTime();
        if (status.equals("Y")) {
            device.setOnlineTime(time);
        } else if (status.equals("N")) {
            device.setOfflineTime(time);
        }
        deviceDAO.updateStatus(device);
    }

    @Override
    public int updatePublishTime(Device device) {
        return deviceDAO.updatePublishTime(device);
    }

    @Override
    public List<Device> queryByCodeNotSelf(Device device) {
        return deviceDAO.queryByCodeNotSelf(device);
    }
}
