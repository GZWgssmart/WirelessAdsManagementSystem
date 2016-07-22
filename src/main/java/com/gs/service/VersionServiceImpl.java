package com.gs.service;

import com.gs.bean.Version;
import com.gs.common.bean.Pager;
import com.gs.dao.VersionDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WangGenshen on 6/30/16.
 */
@Service
public class VersionServiceImpl implements VersionService {

    @Resource
    private VersionDAO versionDAO;

    @Override
    public List<Version> queryByPagerAndCriteria(Pager pager, Version version) {
        return versionDAO.queryByPagerAndCriteria(pager, version);
    }

    @Override
    public int countByCriteria(Version version) {
        return versionDAO.countByCriteria(version);
    }

    @Override
    public List<Version> queryAll() {
        return versionDAO.queryAll();
    }

    @Override
    public List<Version> queryAll(String status) {
        return versionDAO.queryAll(status);
    }

    @Override
    public Version queryById(String s) {
        return versionDAO.queryById(s);
    }

    @Override
    public Version query(Version version) {
        return null;
    }

    @Override
    public int insert(Version version) {
        return versionDAO.insert(version);
    }

    @Override
    public int update(Version version) {
        return versionDAO.update(version);
    }

    @Override
    public List<Version> queryByPager(Pager pager) {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public int inactive(String s) {
        return versionDAO.inactive(s);
    }

    @Override
    public int active(String s) {
        return versionDAO.active(s);
    }

    @Override
    public int batchInsert(List<Version> versions) {
        return 0;
    }

    @Override
    public List<Version> queryByCustomerAndGroupById(String customerId) {
        return versionDAO.queryByCustomerAndGroupById(customerId);
    }
}
