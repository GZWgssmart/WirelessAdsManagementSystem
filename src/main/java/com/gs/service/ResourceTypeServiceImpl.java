package com.gs.service;

import com.gs.bean.ResourceType;
import com.gs.common.bean.Pager;
import com.gs.dao.ResourceTypeDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by WangGenshen on 5/25/16.
 */
@Service
public class ResourceTypeServiceImpl implements ResourceTypeService {

    @Resource
    private ResourceTypeDAO resourceTypeDAO;

    @Override
    public List<ResourceType> queryAll() {
        return resourceTypeDAO.queryAll();
    }

    @Override
    public List<ResourceType> queryAll(String status) {
        return resourceTypeDAO.queryAll(status);
    }

    @Override
    public ResourceType queryById(String s) {
        return resourceTypeDAO.queryById(s);
    }

    @Override
    public ResourceType query(ResourceType resourceType) {
        return null;
    }

    @Override
    public int insert(ResourceType resourceType) {
        return resourceTypeDAO.insert(resourceType);
    }

    @Override
    public int update(ResourceType resourceType) {
        return resourceTypeDAO.update(resourceType);
    }

    @Override
    public List<ResourceType> queryByPager(Pager pager) {
        return resourceTypeDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return resourceTypeDAO.count();
    }

    @Override
    public int inactive(String id) {
        return resourceTypeDAO.inactive(id);
    }

    @Override
    public int active(String id) {
        return resourceTypeDAO.active(id);
    }

    @Override
    public int batchInsert(List<ResourceType> resourceTypes) {
        return 0;
    }

}
