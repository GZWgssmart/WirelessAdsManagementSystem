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
        return null;
    }

    @Override
    public ResourceType queryById(String s) {
        return null;
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
        return 0;
    }

    @Override
    public List<ResourceType> queryByPager(Pager pager) {
        return resourceTypeDAO.queryByPager(pager);
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public int inactive(String s) {
        return 0;
    }

    @Override
    public int active(String s) {
        return 0;
    }

    @Override
    public int batchInsert(List<ResourceType> resourceTypes) {
        return 0;
    }

}
