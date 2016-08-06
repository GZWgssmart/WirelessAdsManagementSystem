package com.gs.dao;

import com.gs.bean.Device;
import com.gs.bean.DeviceResource;
import com.gs.common.bean.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangGenshen on 5/30/16.
 */
@Repository
public interface DeviceResourceDAO extends BaseDAO<DeviceResource, String> {

    public List<DeviceResource> queryByPagerAndCustomerId(@Param("pager") Pager pager, @Param("customerId") String customerId);

    public List<DeviceResource> queryByPagerAndCriteria(@Param("pager") Pager pager,
                                                @Param("deviceResource") DeviceResource deviceResource,
                                                @Param("customerId") String customerId);

    public int countByCriteria(@Param("deviceResource") DeviceResource deviceResource, @Param("customerId") String customerId);

    public String queryByDeviceId(String id);

    public int check(@Param("id") String id, @Param("checkStatus") String checkStatus);

    public DeviceResource queryWithDeviceResourceById(String drId);

    public List<DeviceResource> queryWithDeviceResourceByCode(String code);

    public int updatePublishLog(@Param("id") String id, @Param("publishLog") String publishLog);

    public int updateWhenPublished(DeviceResource deviceResource);

}
