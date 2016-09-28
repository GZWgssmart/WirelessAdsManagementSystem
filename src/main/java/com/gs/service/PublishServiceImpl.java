package com.gs.service;

import com.gs.bean.Publish;
import com.gs.bean.PublishPlan;
import com.gs.common.bean.Pager;
import com.gs.dao.PublishDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

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
        // 开始剔除已经排好序的重复的资源
        List<Publish> publishes = publishDAO.queryResByPager(pager, planId);
        if (publishes != null && publishes.size() > 0) {
            Publish currentPublish = publishes.get(0);
            Iterator<Publish> ite = publishes.iterator();
            int i = 0;
            while (ite.hasNext()) {
                Publish p = ite.next();
                if (i == 0) {
                    i++;
                    continue;
                }
                if (p.getResource().getId().equals(currentPublish.getResource().getId())) {
                    ite.remove();
                } else {
                    currentPublish = p;
                }
            }
        }
        return publishes;
    }

    @Override
    public int countRes(String planId) {
        return publishDAO.countRes(planId);
    }
}
