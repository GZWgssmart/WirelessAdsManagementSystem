package com.gs.service;

import com.gs.bean.PubResource;
import com.gs.bean.Publish;
import com.gs.bean.PublishLog;
import com.gs.common.bean.Pager;
import com.gs.dao.PublishDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by WangGenshen on 5/30/16.
 */
@Service
public class PublishServiceImpl implements PublishService {
    @Resource
    private PublishDAO publishDAO;

    @Override
    public List<Publish> queryAll() {
        return publishDAO.queryAll();
    }

    @Override
    public List<Publish> queryAll(String status) {
        return publishDAO.queryAll(status);
    }

    @Override
    public Publish queryById(String s) {
        return null;
    }

    @Override
    public Publish query(Publish publish) {
        return null;
    }

    @Override
    public int insert(Publish publish) {
        return publishDAO.insert(publish);
    }

    @Override
    public int update(Publish publish) {
        return publishDAO.update(publish);
    }

    @Override
    public List<Publish> queryByPager(Pager pager) {
        return publishDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return publishDAO.count();
    }

    @Override
    public int inactive(String s) {
        return publishDAO.inactive(s);
    }

    @Override
    public int active(String s) {
        return publishDAO.active(s);
    }

    @Override
    public int batchInsert(List<Publish> publishs) {
        return publishDAO.batchInsert(publishs);
    }

    @Override
    public List<Publish> queryByPagerAndCustomerId(Pager pager, String customerId) {
        return publishDAO.queryByPagerAndCustomerId(pager, customerId);
    }

    @Override
    public List<Publish> queryByPagerAndCriteria(Pager pager, Publish publish) {
        return publishDAO.queryByPagerAndCriteria(pager, publish);
    }

    @Override
    public int countByCriteria(Publish publish) {
        return publishDAO.countByCriteria(publish);
    }

    @Override
    public String queryByDeviceId(String id) {
        return publishDAO.queryByDeviceId(id);
    }

    @Override
    public Publish queryByDRId(String drid) {
        return publishDAO.queryByDRId(drid);
    }

    @Override
    public List<Publish> queryByCode(String code) {
        return publishDAO.queryByCode(code);
    }

    @Override
    public int updatePublishLog(String id, String publishLog) {
        return publishDAO.updatePublishLog(id, publishLog);
    }

    @Override
    public int updatePublishLogByPlanId(String pubPlanId, String publishLog) {
        return publishDAO.updatePublishLogByPlanId(pubPlanId, publishLog);
    }

    @Override
    public int updateWhenPublished(Publish publish) {
        return publishDAO.updateWhenPublished(publish);
    }

    @Override
    public List<Publish> queryByPlanId(String planId) {
        return publishDAO.queryByPlanId(planId);
    }

    @Override
    public void deleteByPlanId(String planId) {
        publishDAO.deleteByPlanId(planId);
    }

    @Override
    public List<Publish> allDevByPlanId(String planId) {
        return publishDAO.allDevByPlanId(planId);
    }

    @Override
    public List<Publish> queryResByPager(Pager pager, String planId) {
        return publishDAO.queryResByPager(pager, planId);
    }

    @Override
    public int countRes(String planId) {
        return publishDAO.countRes(planId);
    }

    @Override
    public List<PubResource> queryResByDevId(Pager pager, Publish publish) {
        List<Publish> publishes = publishDAO.queryResByDevId(pager, publish);
        List<PubResource> pubResources = new ArrayList<PubResource>();
        if (publishes != null && publishes.size() > 0) {
            Map<com.gs.bean.Resource, String> resources = getResources(publishes);
            for (Publish p : publishes) {
                if (p.getPublishLog().equals(PublishLog.PUBLISHED)) {
                    resources.put(p.getResource(), PubResource.CAN_DELETED);
                } else if (p.getPublishLog().equals(PublishLog.RESOURCE_DELETED)) {
                    resources.put(p.getResource(), PubResource.DELETED);
                } else if (p.getPublishLog().equals(PublishLog.RESOURCE_DELETING)) {
                    resources.put(p.getResource(), PubResource.CAN_NOT_DELETED);
                } else {
                    resources.put(p.getResource(), PubResource.CAN_NOT_DELETED);
                }
            }
            for (Map.Entry<com.gs.bean.Resource, String> entry : resources.entrySet()) {
                PubResource pubResource = new PubResource();
                com.gs.bean.Resource resource = entry.getKey();
                pubResource.setId(resource.getId());
                pubResource.setName(resource.getName());
                pubResource.setDeleteStatus(entry.getValue());
                if (pubResource.getDeleteStatus().equals(PubResource.CAN_DELETED)) {
                    pubResource.setDes(PubResource.CAN_DELETE_MSG);
                } else {
                    pubResource.setDes(PubResource.CAN_NOT_DELETE_MSG);
                }
                pubResources.add(pubResource);
            }
        }
        return pubResources;
    }

    private Map<com.gs.bean.Resource, String> getResources(List<Publish> publishes) {
        Map<com.gs.bean.Resource, String> resources = new HashMap<com.gs.bean.Resource, String>();
        if (publishes != null && publishes.size() > 0) {
            for (Publish p : publishes) {
                resources.put(p.getResource(), PubResource.CAN_DELETED);
            }
        }
        return resources;
    }

    @Override
    public int countResByDevId(Publish publish) {
        return publishDAO.countResByDevId(publish);
    }

    @Override
    public List<Publish> queryByDevIdAndResIds(String deviceId, String resIds) {
        return publishDAO.queryByDevIdAndResIds(deviceId, resIds);
    }
}
