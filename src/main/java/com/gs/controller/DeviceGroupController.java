package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.Device;
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
        return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device/device_groups";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "device-mobile/device_groups";
        } else {
            return "redirect:/mob/redirect_index";
        }
    }

    @RequestMapping(value = "list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdmin(@PathVariable("customerId") String customerId, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("device/device_groups_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/redirect_login_page");
        }
    }

    @RequestMapping(value = "mob/list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdminMob(@PathVariable("customerId") String customerId, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("device-mobile/device_groups_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/mob/redirect_login_page");
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager", method = RequestMethod.GET)
    public Pager4EasyUI<DeviceGroup> listPager(@Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("show device group by pager for customer");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            int total = deviceGroupService.countByCustomerId(customer.getId());
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<DeviceGroup> deviceGroups = deviceGroupService.queryByPagerAndCustomerId(pager, customer.getId());
            return new Pager4EasyUI<DeviceGroup>(pager.getTotalRecords(), deviceGroups);
        } else {
            logger.info("can not show device group by pager cause customer is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager_admin/{customerId}", method = RequestMethod.GET)
    public Pager4EasyUI<DeviceGroup> listPager(@PathVariable("customerId") String customerId, @Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show device group by pager for admin");
            int total = deviceGroupService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<DeviceGroup> deviceGroups = deviceGroupService.queryByPagerAndCustomerId(pager, customerId);
            return new Pager4EasyUI<DeviceGroup>(pager.getTotalRecords(), deviceGroups);
        } else {
            logger.info("can not show device group by pager cause admin is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_combo/{status}/{id}", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> list4Combobox(@PathVariable("status") String status, @PathVariable("id") String id, HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isCustomer(session)) {
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            String theStatus = null;
            if (status.equals("Y")) {
                theStatus = "Y";
            }
            List<DeviceGroup> deviceGroups = deviceGroupService.queryAllByCustomerId(customer.getId(), theStatus);
            boolean defaultGroup = false;
            boolean editDefaultGroup = true;
            if (id.equals("add")) {
                defaultGroup = true;
            }
            for (DeviceGroup deviceGroup : deviceGroups) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(deviceGroup.getId());
                comboBox4EasyUI.setText(deviceGroup.getName());

                if (deviceGroup.getId().equals(id)) { // 如果分组id在可用分组里,则不需要显示默认分组
                    editDefaultGroup = false;
                }

                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
            if (!id.equals("search")) {
                if (defaultGroup || editDefaultGroup) {
                    for (ComboBox4EasyUI comboBox4EasyUI : comboBox4EasyUIs) {
                        if (comboBox4EasyUI.getText().equals("默认分组")) {
                            comboBox4EasyUI.setSelected(true);
                            break;
                        }
                    }
                }
            }

        }
        return comboBox4EasyUIs;
    }

    @ResponseBody
    @RequestMapping(value = "list_combo_admin/{customerId}/{status}", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> list4ComboboxAdmin(@PathVariable("customerId") String customerId, @PathVariable("status") String status, HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isAdmin(session)) {
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            String theStatus = null;
            if (status.equals("Y")) {
                theStatus = "Y";
            }
            List<DeviceGroup> deviceGroups = deviceGroupService.queryAllByCustomerId(customerId, theStatus);
            for (DeviceGroup deviceGroup : deviceGroups) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(deviceGroup.getId());
                comboBox4EasyUI.setText(deviceGroup.getName());
                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
        }
        return comboBox4EasyUIs;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(DeviceGroup deviceGroup, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("update device group info by customer");
            deviceGroupService.update(deviceGroup);
            return ControllerResult.getSuccessResult("成功更新终端分组信息");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceGroupService.inactive(id);
            return ControllerResult.getSuccessResult("冻结终端分组成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            deviceGroupService.active(id);
            return ControllerResult.getSuccessResult("已解除终端分组冻结");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
