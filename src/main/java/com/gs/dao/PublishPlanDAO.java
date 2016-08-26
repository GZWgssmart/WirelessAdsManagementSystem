package com.gs.dao;

import com.gs.bean.PublishPlan;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
@Repository
public interface PublishPlanDAO extends BaseDAO<PublishPlan, String> {

    public List<PublishPlan> queryByPagerAndCustomerId(@Param("pager") Pager pager, @Param("customerId") String customerId);

    public List<PublishPlan> queryByPagerAndCriteria(@Param("pager") Pager pager,
                                                        @Param("publishPlan") PublishPlan PublishPlan,
                                                        @Param("customerId") String customerId);

    public int countByCriteria(@Param("publishPlan") PublishPlan ublishPlan, @Param("customerId") String customerId);

    public int check(@Param("id") String id, @Param("checkStatus") String checkStatus);

    public PublishPlan queryWithResourceById(String planId);

    public int updateCount(PublishPlan publishPlan);

    public int updateCountByPubId(String pubId);

    public int finishByPubId(String planId);

}
