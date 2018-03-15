package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.ResourceType;
import com.gs.common.Constants;
import com.gs.common.bean.ComboBox4EasyUI;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.EncryptUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.CustomerService;
import com.gs.service.ResourceTypeService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.jms.Session;
import javax.naming.ldap.Control;
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
@RequestMapping("/restype")
public class ResourceTypeController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceTypeController.class);

    @Resource
    private ResourceTypeService resourceTypeService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(ResourceType resourceType, HttpSession session) {
        if (SessionUtil.isAdmin(session) || SessionUtil.isSuperAdmin(session)) {
            resourceTypeService.insert(resourceType);
            return ControllerResult.getSuccessResult("成功添加资源类型");
        }
        return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            return "resource/resource_types";
        } else {
            return "redirect:/admin/redirect_login_page";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            return "resource-mobile/resource_types";
        } else {
            return "redirect:/admin/mob/redirect_login_page";
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_combo/{status}", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> list4Combobox(@PathVariable("status") String status, HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            String theStatus = null;
            if (status.equals("Y")) {
                theStatus = "Y";
            }
            List<ResourceType> resourceTypes = resourceTypeService.queryAll(theStatus);
            for (ResourceType resourceType : resourceTypes) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(resourceType.getId());
                comboBox4EasyUI.setText(resourceType.getName());
                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
        }
        return comboBox4EasyUIs;
    }

    @ResponseBody
    @RequestMapping(value = "list_pager", method = RequestMethod.GET)
    public Pager4EasyUI<ResourceType> listPager(@Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            logger.info("show res types by pager");
            int total = resourceTypeService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<ResourceType> resourceTypes = resourceTypeService.queryByPager(pager);
            return new Pager4EasyUI<ResourceType>(pager.getTotalRecords(), resourceTypes);
        } else {
            logger.info("can not show res types by pager cause admin is nog login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "queryJSON/{id}", method = RequestMethod.GET)
    public ResourceType queryByIdJSON(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("query res type by id: " + id);
            return resourceTypeService.queryById(id);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(ResourceType resourceType, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("update res type info by admin");
            resourceTypeService.update(resourceType);
            return ControllerResult.getSuccessResult("成功更新资源类型信息");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            resourceTypeService.inactive(id);
            return ControllerResult.getSuccessResult("冻结资源类型成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            resourceTypeService.active(id);
            return ControllerResult.getSuccessResult("已解除资源类型冻结");
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
