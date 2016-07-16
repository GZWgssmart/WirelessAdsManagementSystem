package com.gs.service;

import com.gs.bean.TimeSegment;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 7/13/16.
 */
public interface TimeSegmentService extends BaseService<TimeSegment, String> {

    public List<TimeSegment> queryByPagerAndPubId(Pager pager, String pubId);
    public int countByPubId(String pubId);

}
