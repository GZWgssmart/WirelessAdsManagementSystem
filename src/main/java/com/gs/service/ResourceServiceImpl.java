package com.gs.service;

import com.gs.bean.Resource;
import com.gs.bean.ResourceType;
import com.gs.common.bean.Pager;
import com.gs.dao.ResourceDAO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by WangGenshen on 5/26/16.
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @javax.annotation.Resource
    private ResourceDAO resourceDAO;

    @Override
    public List<Resource> queryByPagerAndCustomerId(Pager pager, String customerId) {
        return resourceDAO.queryByPagerAndCustomerId(pager, customerId);
    }

    @Override
    public List<Resource> queryByPagerAndCriteria(Pager pager, Resource resource, String customerId) {
        return resourceDAO.queryByPagerAndCriteria(pager, resource, customerId);
    }

    @Override
    public List<Resource> queryAll() {
        return resourceDAO.queryAll();
    }

    @Override
    public List<Resource> queryAll(String status) {
        return resourceDAO.queryAll(status);
    }

    @Override
    public Resource queryById(String s) {
        return resourceDAO.queryById(s);
    }

    @Override
    public Resource query(Resource resource) {
        return resourceDAO.query(resource);
    }

    @Override
    public int insert(Resource resource) {
        return resourceDAO.insert(resource);
    }

    @Override
    public int update(Resource resource) {
        return resourceDAO.update(resource);
    }

    @Override
    public List<Resource> queryByPager(Pager pager) {
        return resourceDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return resourceDAO.count();
    }

    @Override
    public int countByCriteria(Resource resource, String customerId) {
        return resourceDAO.countByCriteria(resource, customerId);
    }

    @Override
    public int inactive(String s) {
        return resourceDAO.inactive(s);
    }

    @Override
    public int active(String s) {
        return resourceDAO.active(s);
    }

    @Override
    public int batchInsert(List<Resource> resources) {
        return resourceDAO.batchInsert(resources);
    }

    @Override
    public String queryByResourceId(String id) {
        return resourceDAO.queryByResourceId(id);
    }

    @Override
    public Resource queryByName(String name) {
        return resourceDAO.queryByName(name);
    }

    public Resource queryByNameAndCustomer(String name, String customerId) {
        return resourceDAO.queryByNameAndCustomer(name, customerId);
    }

    public List<Resource> queryByNameNotSelf(Resource resource) {
        return resourceDAO.queryByNameNotSelf(resource);
    }
}
