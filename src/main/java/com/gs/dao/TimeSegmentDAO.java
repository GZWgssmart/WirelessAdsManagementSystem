package com.gs.dao;

import com.gs.bean.TimeSegment;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 7/13/16.
 */
@Repository
public interface TimeSegmentDAO extends BaseDAO<TimeSegment, String> {

    public List<TimeSegment> queryByPagerAndPubId(@Param("pager") Pager pager, @Param("pubId") String pubId);
    public int countByPubId(String pubId);

    public List<TimeSegment> queryByPubId(String pubId);

}
