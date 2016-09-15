package com.gs.service;

import com.gs.bean.Publish;
import com.gs.bean.PublishPlan;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
public interface PublishService extends BaseService<Publish, String> {

    public List<Publish> queryByPagerAndCustomerId(Pager pager, String customerId);

    public List<Publish> queryByPagerAndCriteria(Pager pager, Publish publish);

    public int countByCriteria(Publish publish);

    public String queryByDeviceId(String id);

    public Publish queryByDRId(String drid);

    public List<Publish> queryByCode(String code);

    public int updatePublishLog(String id, String publishLog);

    public int updatePublishLogByPlanId(String pubPlanId, String publishLog);

    public int updateWhenPublished(Publish publish);

    public List<Publish> queryByPlanId(String planId);

    public void deleteByPlanId(String planId);

    public List<Publish> allDevByPlanId(String planId);

}
