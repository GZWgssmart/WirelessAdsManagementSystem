package com.gs.service;

import com.gs.bean.Device;
import com.gs.bean.PublishPlan;
import com.gs.common.bean.Pager;
import com.gs.dao.DeviceDAO;
import com.gs.dao.PublishPlanDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
@Service
public class PublishPlanServiceImpl implements PublishPlanService {
    @Resource
    private PublishPlanDAO publishPlanDAO;
    @Resource
    private DeviceDAO deviceDAO;

    @Override
    public List<PublishPlan> queryAll() {
        return publishPlanDAO.queryAll();
    }

    @Override
    public List<PublishPlan> queryAll(String status) {
        return publishPlanDAO.queryAll(status);
    }

    @Override
    public PublishPlan queryById(String s) {
        return publishPlanDAO.queryById(s);
    }

    @Override
    public PublishPlan query(PublishPlan publishPlan) {
        return null;
    }

    @Override
    public int insert(PublishPlan publishPlan) {
        return publishPlanDAO.insert(publishPlan);
    }

    @Override
    public int update(PublishPlan publishPlan) {
        return publishPlanDAO.update(publishPlan);
    }

    @Override
    public List<PublishPlan> queryByPager(Pager pager) {
        return publishPlanDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return publishPlanDAO.count();
    }

    @Override
    public int inactive(String s) {
        return publishPlanDAO.inactive(s);
    }

    @Override
    public int active(String s) {
        return publishPlanDAO.active(s);
    }

    @Override
    public int batchInsert(List<PublishPlan> publishPlans) {
        return publishPlanDAO.batchInsert(publishPlans);
    }

    @Override
    public List<PublishPlan> queryByPagerAndCustomerId(Pager pager, String customerId) {
        return publishPlanDAO.queryByPagerAndCustomerId(pager, customerId);
    }

    @Override
    public List<PublishPlan> queryByPagerAndCriteria(Pager pager, PublishPlan publishPlan, String customerId) {
        return publishPlanDAO.queryByPagerAndCriteria(pager, publishPlan, customerId);
    }

    @Override
    public int countByCriteria(PublishPlan publishPlan, String customerId) {
        return publishPlanDAO.countByCriteria(publishPlan, customerId);
    }

    @Override
    public PublishPlan insertBack(PublishPlan publishPlan) {
        publishPlanDAO.insert(publishPlan);
        return publishPlan;
    }

    @Override
    public int check(String id, String checkStatus) {
        return publishPlanDAO.check(id, checkStatus);
    }

    @Override
    public List<String> getDeviceIds(String customerId, String type, String deviceIds, String versionId) {
        List<String> allDeviceIds = new ArrayList<String>();
        if (type.equals("multiple") || type.equals("one")) {
            String[] strs = deviceIds.split(",");
            for (String id : strs) {
                allDeviceIds.add(id);
            }
        } else if (type.equals("group")) {
            List<Device> devices = deviceDAO.queryByGroupIdAndVersionId(deviceIds, versionId);
            for (Device d : devices) {
                allDeviceIds.add(d.getId());
            }
        } else if (type.equals("all")) {
            List<Device> devices = deviceDAO.queryByCustomerIdAndVersionId(customerId, versionId);
            for (Device d : devices) {
                allDeviceIds.add(d.getId());
            }
        }
        return allDeviceIds;
    }

    @Override
    public PublishPlan queryWithResourceById(String planId) {
        return publishPlanDAO.queryWithResourceById(planId);
    }

    @Override
    public int updateCount(PublishPlan publishPlan) {
        return publishPlanDAO.updateCount(publishPlan);
    }

    @Override
    public int updateCountByPubId(String pubId) {
        return publishPlanDAO.updateCountByPubId(pubId);
    }

    @Override
    public int finishByPubId(String pubId) {
        return publishPlanDAO.finishByPubId(pubId);
    }
}
