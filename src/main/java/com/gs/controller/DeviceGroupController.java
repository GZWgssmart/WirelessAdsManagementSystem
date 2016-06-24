package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.DeviceGroup;
import com.gs.common.Constants;
import com.gs.common.bean.ComboBox4EasyUI;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.DeviceGroupService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
@Controller
@RequestMapping("/devgroup")
public class DeviceGroupController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceGroupController.class);

    @Resource
    private DeviceGroupService deviceGroupService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(DeviceGroup deviceGroup, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            deviceGroup.setCustomerId(customer.getId());
            deviceGroupService.insert(deviceGroup);
            return ControllerResult.getSuccessResult("成功添加终端分组");
        }
        return null;
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device/device_groups";
        } else {
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdmin(@PathVariable("customerId") String customerId, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("device/device_groups_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<DeviceGroup> list(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("显示所有终端分组信息");
            return deviceGroupService.queryAll();
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager", method = RequestMethod.GET)
    public Pager4EasyUI<DeviceGroup> listPager(@Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("分页显示资源分组信息");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            int total = deviceGroupService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<DeviceGroup> deviceGroups = deviceGroupService.queryByPagerAndCustomerId(pager, customer.getId());
            return new Pager4EasyUI<DeviceGroup>(pager.getTotalRecords(), deviceGroups);
        } else {
            logger.info("客户未登录，不能分页显示终端分组列表");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager_admin/{customerId}", method = RequestMethod.GET)
    public Pager4EasyUI<DeviceGroup> listPager(@PathVariable("customerId") String customerId, @Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("分页显示资源分组信息");
            int total = deviceGroupService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<DeviceGroup> deviceGroups = deviceGroupService.queryByPagerAndCustomerId(pager, customerId);
            return new Pager4EasyUI<DeviceGroup>(pager.getTotalRecords(), deviceGroups);
        } else {
            logger.info("管理员未登录，不能分页显示终端分组列表");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_combo", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> list4Combobox(HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            List<DeviceGroup> deviceGroups = deviceGroupService.queryAll();
            for (DeviceGroup deviceGroup : deviceGroups) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(deviceGroup.getId());
                comboBox4EasyUI.setText(deviceGroup.getName());
                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
        }
        return comboBox4EasyUIs;
    }

    @RequestMapping(value = "query/{id}", method = RequestMethod.GET)
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("根据终端分组id: " + id + "查询终端分组信息");
            ModelAndView mav = new ModelAndView("device/devgroup_info");
            DeviceGroup deviceGroup = deviceGroupService.queryById(id);
            mav.addObject("deviceGroup", deviceGroup);
            return mav;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(DeviceGroup deviceGroup, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("更新终端分组信息");
            deviceGroupService.update(deviceGroup);
            return ControllerResult.getSuccessResult("成功更新终端分组信息");
        } else {
            return ControllerResult.getFailResult("更新终端分组信息失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceGroupService.inactive(id);
            return ControllerResult.getSuccessResult("冻结终端分组成功");
        } else {
            return ControllerResult.getFailResult("没有权限冻结终端分组");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceGroupService.active(id);
            return ControllerResult.getSuccessResult("已解除终端分组冻结");
        } else {
            return ControllerResult.getFailResult("没有权限激活终端分组");
        }
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
