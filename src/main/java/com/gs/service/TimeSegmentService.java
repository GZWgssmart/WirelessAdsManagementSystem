package com.gs.service;

import com.gs.bean.TimeSegment;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 7/13/16.
 */
public interface TimeSegmentService extends BaseService<TimeSegment, String> {

    public List<TimeSegment> queryByPubId(String pubId);

    public List<TimeSegment> queryByPlanId(String planId);

    public void deleteByPlanId(String planId);

}
