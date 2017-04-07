package com.gs.dao;

import com.gs.bean.Publish;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
@Repository
public interface PublishDAO extends BaseDAO<Publish, String> {

    public List<Publish> queryByPagerAndCustomerId(@Param("pager") Pager pager, @Param("customerId") String customerId);

    public List<Publish> queryByPagerAndCriteria(@Param("pager") Pager pager,
                                                 @Param("publish") Publish publish);

    public int countByCriteria(Publish publish);

    public Publish queryByDRId(String drId);

    public List<Publish> queryByCode(String code);

    public int updatePublishLog(@Param("id") String id, @Param("publishLog") String publishLog);

    public int updatePublishLogs(@Param("ids") String[] ids, @Param("publishLog") String publishLog);

    public int updatePublishLogByPlanId(@Param("pubPlanId") String pubPlanId, @Param("publishLog") String pubishLog);

    public int updateWhenPublished(Publish publish);

    public List<Publish> queryByPlanId(String planId);

    public void deleteByPlanId(String planId);

    public List<Publish> allDevByPlanId(String planId);

    public List<Publish> queryResByPager(@Param("pager") Pager pager, @Param("planId") String planId);

    public int countRes(String planId);

    public List<Publish> queryResByDevId(@Param("pager") Pager pager, @Param("publish") Publish publish);

    public int countResByDevId(Publish publish);

    public List<Publish> queryByDevIdAndResIds(@Param("deviceId") String deviceId, @Param("resIds") String[] resIds);

    public List<Publish> queryByDeviceId(String deviceId);

    public List<Publish> queryByResIds(@Param("resIds") String[] resIds, @Param("customerId") String customerId);

    public int updatePublishLogByDevCode(@Param("devCode") String devCode, @Param("publishLog") String publishLog);

}
