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
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device/devices";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device-mobile/devices";
        } else {
            return "redirect:/mob/redirect_index";
        }
    }

    @RequestMapping(value = "list_page_version/{versionId}", method = RequestMethod.GET)
    public ModelAndView toListPageVersion(@PathVariable("versionId") String versionId, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            ModelAndView mav = new ModelAndView("device/devices_version");
            mav.addObject("versionId", versionId);
            return mav;
        } else {
            return new ModelAndView("redirect:/redirect_index");
        }
    }

    @RequestMapping(value = "list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdmin(@PathVariable("customerId") String customerId,  HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("device/devices_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/redirect_login_page");
        }
    }

    @RequestMapping(value = "mob/list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdminMob(@PathVariable("customerId") String customerId,  HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("device-mobile/devices_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/mob/redirect_login_page");
        }
    }

    @RequestMapping(value = "list_page_choose", method = RequestMethod.GET)
    public String toListChoosePage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device/devices_choose";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Device> searchPager(@Param("page")String page, @Param("rows")String rows, Device device, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("show devices by pager for customer");
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
            logger.info("can not show devices by pager cause customer is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager_admin/{customerId}", method = RequestMethod.GET)
    public Pager4EasyUI<Device> searchPagerAdmin(@PathVariable("customerId") String customerId, @Param("page")String page, @Param("rows")String rows, Device device, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show devices by pager for admin");
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
            logger.info("can not show devices by pager cause admin is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(Device device, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            List<Device> devices = deviceService.queryByCodeNotSelf(device);
            if (devices == null || devices.size() == 0) {
                device.setInstallTime(DateParseUtil.parseDate(device.getInstallTimeStr() == null ? "" : device.getInstallTimeStr(), Constants.DATETIME_PATTERN));
                deviceService.update(device);
                logger.info("update device info");
                return ControllerResult.getSuccessResult("成功更新终端设备");
            } else {
                return ControllerResult.getFailResult("已经存在该终端号的设备");
            }
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceService.inactive(id);
            return ControllerResult.getSuccessResult("冻结终端设备成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceService.active(id);
            return ControllerResult.getSuccessResult("已解除终端设备冻结");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
