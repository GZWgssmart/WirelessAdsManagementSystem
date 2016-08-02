package com.gs.service;

import com.gs.bean.ResourceType;
import com.gs.bean.TimeSegment;
import com.gs.common.bean.Pager;
import com.gs.dao.ResourceTypeDAO;
import com.gs.dao.TimeSegmentDAO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WangGenshen on 5/25/16.
 */
@Service
public class TimeSegmentServiceImpl implements TimeSegmentService {

    @Resource
    private TimeSegmentDAO timeSegmentDAO;

    @Override
    public List<TimeSegment> queryAll() {
        return timeSegmentDAO.queryAll();
    }

    @Override
    public List<TimeSegment> queryAll(String status) {
        return timeSegmentDAO.queryAll(status);
    }

    @Override
    public TimeSegment queryById(String s) {
        return null;
    }

    @Override
    public TimeSegment query(TimeSegment timeSegment) {
        return null;
    }

    @Override
    public int insert(TimeSegment timeSegment) {
        return timeSegmentDAO.insert(timeSegment);
    }

    @Override
    public int update(TimeSegment timeSegment) {
        return timeSegmentDAO.update(timeSegment);
    }

    @Override
    public List<TimeSegment> queryByPager(Pager pager) {
        return timeSegmentDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return timeSegmentDAO.count();
    }

    @Override
    public int inactive(String id) {
        return timeSegmentDAO.inactive(id);
    }

    @Override
    public int active(String id) {
        return timeSegmentDAO.active(id);
    }

    @Override
    public int batchInsert(List<TimeSegment> timeSegments) {
        return 0;
    }

    @Override
    public List<TimeSegment> queryByPagerAndPubId(Pager pager, String pubId) {
        return timeSegmentDAO.queryByPagerAndPubId(pager, pubId);
    }

    @Override
    public int countByPubId(String pubId) {
        return timeSegmentDAO.countByPubId(pubId);
    }

    @Override
    public List<TimeSegment> queryByPubId(String pubId) {
        return timeSegmentDAO.queryByPubId(pubId);
    }
}
