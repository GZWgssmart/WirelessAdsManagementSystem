package com.gs.service;

import com.gs.bean.DeviceGroup;
import com.gs.bean.Resource;
import com.gs.common.bean.Pager;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by WangGenshen on 5/25/16.
 */
public interface ResourceService extends BaseService<Resource, String> {

    public List<Resource> queryByPagerAndCustomerId(Pager pager, String customerId);

    public List<Resource> queryByPagerAndCriteria(Pager pager, Resource resource, String customerId);

    public int countByCriteria(Resource resource, String customerId);

}
