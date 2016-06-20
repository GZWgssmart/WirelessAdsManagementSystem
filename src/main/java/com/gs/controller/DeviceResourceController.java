package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.DeviceGroup;
import com.gs.bean.DeviceResource;
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
import com.gs.service.DeviceResourceService;
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
@RequestMapping("/devres")
public class DeviceResourceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceResourceController.class);

    @Resource
    private DeviceResourceService deviceResourceService;
    @Resource
    private DeviceGroupService deviceGroupService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(DeviceResource deviceResource, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            deviceResource.setCustomerId(customer.getId());
            deviceResourceService.insert(deviceResource);
            return ControllerResult.getSuccessResult("成功添加消息发布");
        }
        return null;
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish/publishes";
        } else {
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "list_page_checking", method = RequestMethod.GET)
    public String toListPageChecking(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish/publishes_check";
        } else {
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "list_page_checked", method = RequestMethod.GET)
    public String toListPageChecked(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish/publishes_checked";
        } else {
            return "redirect:/index";
        }
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<DeviceResource> list(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("显示所有消息发布");
            return deviceResourceService.queryAll();
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager", method = RequestMethod.GET)
    public Pager4EasyUI<DeviceResource> listPager(@Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("分页显示消息发布");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            int total = deviceResourceService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<DeviceResource> deviceResources = deviceResourceService.queryByPagerAndCustomerId(pager, customer.getId());
            for (DeviceResource dr : deviceResources) {
                dr.setStartTimeStr(DateFormatUtil.format(dr.getStartTime(), Constants.DATETIME_PATTERN));
                dr.setEndTimeStr(DateFormatUtil.format(dr.getEndTime(), Constants.DATETIME_PATTERN));
            }
            return new Pager4EasyUI<DeviceResource>(pager.getTotalRecords(), deviceResources);
        } else {
            logger.info("客户未登录，不能分页显示消息发布");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<DeviceResource> searchPager(@Param("page")String page, @Param("rows")String rows, DeviceResource deviceResource, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("分页显示消息发布");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            int total = deviceResourceService.countByCriteria(deviceResource, customer.getId());
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<DeviceResource> deviceResources = deviceResourceService.queryByPagerAndCriteria(pager, deviceResource, customer.getId());
            for (DeviceResource dr : deviceResources) {
                dr.setStartTimeStr(DateFormatUtil.format(dr.getStartTime(), Constants.DATETIME_PATTERN));
                dr.setEndTimeStr(DateFormatUtil.format(dr.getEndTime(), Constants.DATETIME_PATTERN));
            }
            return new Pager4EasyUI<DeviceResource>(pager.getTotalRecords(), deviceResources);
        } else {
            logger.info("客户未登录，不能分页显示消息发布");
            return null;
        }
    }

    @RequestMapping(value = "query/{id}", method = RequestMethod.GET)
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("根据消息发布id: " + id + "查询消息发布");
            ModelAndView mav = new ModelAndView("device/device_info");
            DeviceResource deviceResource = deviceResourceService.queryById(id);
            mav.addObject("deviceResource", deviceResource);
            return mav;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(DeviceResource deviceResource, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("更新消息发布");
            deviceResource.setStartTime(DateParseUtil.parseDate(deviceResource.getStartTimeStr(), Constants.DATETIME_PATTERN));
            deviceResource.setEndTime(DateParseUtil.parseDate(deviceResource.getEndTimeStr(), Constants.DATETIME_PATTERN));
            deviceResourceService.update(deviceResource);
            return ControllerResult.getSuccessResult("成功更新消息发布");
        } else {
            return ControllerResult.getFailResult("更新消息发布失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceResourceService.inactive(id);
            return ControllerResult.getSuccessResult("冻结消息发布成功");
        } else {
            return ControllerResult.getFailResult("没有权限冻结消息发布");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceResourceService.active(id);
            return ControllerResult.getSuccessResult("已解除消息发布冻结");
        } else {
            return ControllerResult.getFailResult("没有权限激活消息发布");
        }
    }

    @ResponseBody
    @RequestMapping(value = "check", method = RequestMethod.GET)
    public ControllerResult check(@Param("id")String id, @Param("checkStatus") String checkStatus, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceResourceService.check(id, checkStatus);
            return ControllerResult.getSuccessResult("消息发布" + checkStatus);
        } else {
            return ControllerResult.getFailResult("没有权限提交消息发布审核");
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_combo/{id}", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> listCombo(@PathVariable("id") String id, HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isCustomer(session)) {
            String deviceGroupId = deviceResourceService.queryByDeviceId(id);
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            List<DeviceGroup> deviceGroups = deviceGroupService.queryAll();
            for (DeviceGroup dg : deviceGroups) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(dg.getId());
                comboBox4EasyUI.setText(dg.getName());
                if (dg.getId().equals(deviceGroupId)) {
                    comboBox4EasyUI.setSelected(true);
                }
                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
        }
        return comboBox4EasyUIs;
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
