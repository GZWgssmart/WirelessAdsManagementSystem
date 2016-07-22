package com.gs.service;

import com.gs.bean.Customer;
import com.gs.bean.Version;
import com.gs.common.bean.Pager;

import java.util.List;

/**
 * Created by WangGenshen on 6/30/16.
 */
public interface VersionService extends BaseService<Version, String> {

    public List<Version> queryByPagerAndCriteria(Pager pager, Version version);

    public int countByCriteria(Version version);

    public List<Version> queryByCustomerAndGroupById(String customerId);

}
