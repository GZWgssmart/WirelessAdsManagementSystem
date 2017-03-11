package com.gs.service;

import com.gs.bean.PubResource;
import com.gs.bean.Publish;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
public interface PublishService extends BaseService<Publish, String> {

    public List<Publish> queryByPagerAndCustomerId(Pager pager, String customerId);

    public List<Publish> queryByPagerAndCriteria(Pager pager, Publish publish);

    public int countByCriteria(Publish publish);

    public List<Publish> queryByDeviceId(String deviceId);

    public Publish queryByDRId(String drid);

    public List<Publish> queryByCode(String code);

    public int updatePublishLog(String id, String publishLog);

    public int updatePublishLogs(String[] ids, String publishLog);

    public int updatePublishLogByPlanId(String pubPlanId, String publishLog);

    public int updateWhenPublished(Publish publish);

    public List<Publish> queryByPlanId(String planId);

    public void deleteByPlanId(String planId);

    public List<Publish> allDevByPlanId(String planId);

    public List<Publish> queryResByPager(Pager pager, String planId);

    public int countRes(String planId);

    public List<PubResource> queryResByDevId(Pager pager, Publish publish);

    public int countResByDevId(Publish publish);

    public List<Publish> queryByDevIdAndResIds(String deviceId, String[] resIds);

    public List<Publish> queryByResIds(String[] resIds, String customerId);

    public int updatePublishLogByDevCode(String devCode, String publishLog);

}
