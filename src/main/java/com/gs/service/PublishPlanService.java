package com.gs.service;

import com.gs.bean.Publish;
import com.gs.bean.PublishPlan;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
public interface PublishPlanService extends BaseService<PublishPlan, String> {

    public List<PublishPlan> queryByPagerAndCustomerId(Pager pager, String customerId);

    public List<PublishPlan> queryByPagerAndCriteria(Pager pager, PublishPlan publishPlan, String customerId);

    public int countByCriteria(PublishPlan publishPlan, String customerId);

    public int check(String id, String checkStatus);

    /**
     *
     * @param customerId
     * @param type
     * @param deviceIds 如果是多个设备,则为这些设备的id,如果是分组设备,则为分组的id,如果是全部设备则为空
     * @param versionId
     * @return
     */
    public List<String> getDeviceIds(String customerId, String type, String deviceIds, String versionId);

    public PublishPlan insertBack(PublishPlan publishPlan);

    public PublishPlan queryWithResourceById(String planId);

    public int updateCount(PublishPlan publishPlan);

    public int updateCountByPubId(String pubId);

    public int finishByPubId(String planId);

}
