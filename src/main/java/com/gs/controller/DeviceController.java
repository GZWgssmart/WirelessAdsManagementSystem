package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.Device;
import com.gs.bean.DeviceGroup;
import com.gs.bean.ResourceType;
import com.gs.common.Constants;
import com.gs.common.bean.ComboBox4EasyUI;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.DateFormatUtil;
import com.gs.common.util.DateParseUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.DeviceGroupService;
import com.gs.service.DeviceService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
@RequestMapping("/device")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Resource
    private DeviceService deviceService;
    @Resource
    private DeviceGroupService deviceGroupService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(Device device, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            if (deviceService.queryByCode(device.getCode()) == null) {
                Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
                device.setCustomerId(customer.getId());
                deviceService.insert(device);
                return ControllerResult.getSuccessResult("成功添加终端设备");
            } else {
                return ControllerResult.getFailResult("已经存在该终端号的设备");
            }
        }
        return null;
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device/devices";
        } else {
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "list_page_version/{versionId}", method = RequestMethod.GET)
    public ModelAndView toListPageVersion(@PathVariable("versionId") String versionId, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            ModelAndView mav = new ModelAndView("device/devices_version");
            mav.addObject("versionId", versionId);
            return mav;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdmin(@PathVariable("customerId") String customerId,  HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("device/devices_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "list_page_choose", method = RequestMethod.GET)
    public String toListChoosePage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device/devices_choose";
        } else {
            return "redirect:/index";
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Device> searchPager(@Param("page")String page, @Param("rows")String rows, Device device, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("分页显示终端设备");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            int total = deviceService.countByCriteria(device, customer.getId());
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Device> devices = deviceService.queryByPagerAndCriteria(pager, device, customer.getId());
            for (Device d : devices) {
                if (d.getInstallTime() != null) {
                    d.setInstallTimeStr(DateFormatUtil.format(d.getInstallTime(), Constants.DATETIME_PATTERN));
                }
            }
            return new Pager4EasyUI<Device>(pager.getTotalRecords(), devices);
        } else {
            logger.info("客户未登录，不能分页显示终端设备");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager_admin/{customerId}", method = RequestMethod.GET)
    public Pager4EasyUI<Device> searchPagerAdmin(@PathVariable("customerId") String customerId, @Param("page")String page, @Param("rows")String rows, Device device, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("分页显示终端设备");
            int total = deviceService.countByCriteria(device, customerId);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Device> devices = deviceService.queryByPagerAndCriteria(pager, device, customerId);
            for (Device d : devices) {
                if (d.getInstallTime() != null) {
                    d.setInstallTimeStr(DateFormatUtil.format(d.getInstallTime(), Constants.DATETIME_PATTERN));
                }
            }
            return new Pager4EasyUI<Device>(pager.getTotalRecords(), devices);
        } else {
            logger.info("管理员未登录，不能分页显示终端设备");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(Device device, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("更新终端设备");
            device.setInstallTime(DateParseUtil.parseDate(device.getInstallTimeStr(), Constants.DATETIME_PATTERN));
            deviceService.update(device);
            return ControllerResult.getSuccessResult("成功更新终端设备");
        } else {
            return ControllerResult.getFailResult("更新终端设备失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceService.inactive(id);
            return ControllerResult.getSuccessResult("冻结终端设备成功");
        } else {
            return ControllerResult.getFailResult("没有权限冻结终端设备");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceService.active(id);
            return ControllerResult.getSuccessResult("已解除终端设备冻结");
        } else {
            return ControllerResult.getFailResult("没有权限激活终端设备");
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
