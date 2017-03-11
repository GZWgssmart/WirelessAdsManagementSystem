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
    public List<Publish> queryByDeviceId(String deviceId) {
        return publishDAO.queryByDeviceId(deviceId);
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
    public int updatePublishLogs(String[] ids, String publishLog) {
        return publishDAO.updatePublishLogs(ids, publishLog);
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
        List<Publish> publishes = publishDAO.queryResByPager(pager, planId);
        List<Publish> retPubs = new ArrayList<Publish>();
        Publish currentPublish = null;
        if (publishes != null && publishes.size() > 0) {
            currentPublish = publishes.get(0);
            retPubs.add(currentPublish);
            for (Publish p : publishes) {
                if (!currentPublish.getResource().getId().equals(p.getResource().getId())) {
                    currentPublish = p;
                    retPubs.add(currentPublish);
                }
            }
        }
        return retPubs;
    }

    @Override
    public int countRes(String planId) {
        return publishDAO.countRes(planId);
    }

    @Override
    public List<PubResource> queryResByDevId(Pager pager, Publish publish) {
        List<Publish> publishes = publishDAO.queryResByDevId(pager, publish);
        List<PubResource> pubResources = new ArrayList<PubResource>();
        /** 以下逻辑被弃用
        List<com.gs.bean.Resource> resources = getResources(publishes);
        pubResources.addAll(getCanDeleteResources(resources, publishes));
        pubResources.addAll(getDeletingResources(resources, publishes));
        pubResources.addAll(getDeletedResources(resources, publishes));
        pubResources.addAll(getCanNotDeleteResources(resources, publishes));
         */
        for (Publish p : publishes) {
            PubResource pr = new PubResource();
            pr.setId(p.getResource().getId());
            pr.setName(p.getResource().getName());
            pr.setPublishTime(p.getPublishTime());
            pr.setStartTime(p.getStartTime());
            pr.setEndTime(p.getEndTime());
            pr.setResType(p.getResource().getResourceTypeName());
            pr.setShowType(p.getShowType());
            pr.setShowCount(p.getShowCount());
            pr.setStayTime(p.getStayTime());
            pr.setDeleteTime(p.getDeleteTime());
            if (p.getPublishLog().equals(PublishLog.PUBLISHED) || p.getPublishLog().equals(PublishLog.RESOURCE_NOT_DELETED)) {
                pr.setDeleteStatus(PubResource.CAN_DELETED);
                pr.setDes("此资源已经完成发布，可以删除");
            } else if (p.getPublishLog().equals(PublishLog.RESOURCE_DELETED)) {
                pr.setDeleteStatus(PubResource.DELETED);
                pr.setDes("此资源已经删除，无需再次删除");
            } else if (p.getPublishLog().equals(PublishLog.RESOURCE_DELETING)) {
                pr.setDeleteStatus(PubResource.CAN_NOT_DELETED);
                pr.setDes("此资源正在删除，无需再次删除");
            } else {
                pr.setDeleteStatus(PubResource.CAN_NOT_DELETED);
                pr.setDes("此资源还在发布过程中，不能删除");
            }
            pubResources.add(pr);
        }
        return pubResources;
    }

    private List<PubResource> getCanDeleteResources(List<com.gs.bean.Resource> resources, List<Publish> publishes) {
        List<PubResource> pubResources = new ArrayList<PubResource>();
        Iterator<com.gs.bean.Resource> iterator = resources.iterator();
        while (iterator.hasNext()) {
            com.gs.bean.Resource resource = iterator.next();
            boolean canDelete = true;
            for (Publish p : publishes) {
                if (p.getResource().getId().equals(resource.getId()) && (!p.getPublishLog().equals(PublishLog.PUBLISHED) && !p.getPublishLog().equals(PublishLog.RESOURCE_NOT_DELETED))) {
                    canDelete = false;
                }
            }
            if (canDelete) {
                iterator.remove();
                PubResource pubResource = new PubResource();
                pubResource.setId(resource.getId());
                pubResource.setName(resource.getName());
                pubResource.setDeleteStatus(PubResource.CAN_DELETED);
                pubResource.setDes(PubResource.CAN_DELETE_MSG);
                pubResources.add(pubResource);
            }
        }
        return pubResources;
    }

    private List<PubResource> getDeletedResources(List<com.gs.bean.Resource> resources, List<Publish> publishes) {
        List<PubResource> pubResources = new ArrayList<PubResource>();
        Iterator<com.gs.bean.Resource> iterator = resources.iterator();
        while (iterator.hasNext()) {
            com.gs.bean.Resource resource = iterator.next();
            boolean deleted = true;
            for (Publish p : publishes) {
                if (p.getResource().getId().equals(resource.getId()) && !p.getPublishLog().equals(PublishLog.RESOURCE_DELETED)) {
                    deleted = false;
                }
            }
            if (deleted) {
                iterator.remove();
                PubResource pubResource = new PubResource();
                pubResource.setId(resource.getId());
                pubResource.setName(resource.getName());
                pubResource.setDeleteStatus(PubResource.DELETED);
                pubResource.setDes(PubResource.CAN_NOT_DELETE_MSG);
                pubResources.add(pubResource);
            }
        }
        return pubResources;
    }

    private List<PubResource> getDeletingResources(List<com.gs.bean.Resource> resources, List<Publish> publishes) {
        List<PubResource> pubResources = new ArrayList<PubResource>();
        Iterator<com.gs.bean.Resource> iterator = resources.iterator();
        while (iterator.hasNext()) {
            com.gs.bean.Resource resource = iterator.next();
            boolean deleting = true;
            for (Publish p : publishes) {
                if (p.getResource().getId().equals(resource.getId()) && !p.getPublishLog().equals(PublishLog.RESOURCE_DELETING)) {
                    deleting = false;
                }
            }
            if (deleting) {
                iterator.remove();
                PubResource pubResource = new PubResource();
                pubResource.setId(resource.getId());
                pubResource.setName(resource.getName());
                pubResource.setDeleteStatus(PubResource.DELETING);
                pubResource.setDes(PubResource.CAN_NOT_DELETE_MSG);
                pubResources.add(pubResource);
            }
        }
        return pubResources;
    }

    private List<PubResource> getCanNotDeleteResources(List<com.gs.bean.Resource> resources, List<Publish> publishes) {
        List<PubResource> pubResources = new ArrayList<PubResource>();
        Iterator<com.gs.bean.Resource> iterator = resources.iterator();
        while (iterator.hasNext()) {
            com.gs.bean.Resource resource = iterator.next();
            PubResource pubResource = new PubResource();
            pubResource.setId(resource.getId());
            pubResource.setName(resource.getName());
            pubResource.setDeleteStatus(PubResource.CAN_NOT_DELETED);
            pubResource.setDes(PubResource.CAN_NOT_DELETE_MSG);
            pubResources.add(pubResource);
        }
        return pubResources;
    }

    private List<com.gs.bean.Resource> getResources(List<Publish> publishes) {
        List<com.gs.bean.Resource> resources = new ArrayList<com.gs.bean.Resource>();
        if (publishes != null && publishes.size() > 0) {
            for (Publish p : publishes) {
                com.gs.bean.Resource resource = p.getResource();
                if (!resources.contains(resource)) {
                    resources.add(resource);
                }
            }
        }
        return resources;
    }

    @Override
    public int countResByDevId(Publish publish) {
        return publishDAO.countResByDevId(publish);
    }

    @Override
    public List<Publish> queryByDevIdAndResIds(String deviceId, String[] resIds) {
        return publishDAO.queryByDevIdAndResIds(deviceId, resIds);
    }

    @Override
    public List<Publish> queryByResIds(String[] resIds, String customerId) {
        return publishDAO.queryByResIds(resIds, customerId);
    }

    @Override
    public int updatePublishLogByDevCode(String devCode, String publishLog) {
        return publishDAO.updatePublishLogByDevCode(devCode, publishLog);
    }
}
