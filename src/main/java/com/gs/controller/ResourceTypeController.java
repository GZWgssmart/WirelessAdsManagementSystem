package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.ResourceType;
import com.gs.common.Constants;
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
        return null;
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            return "resource/resource_types";
        } else {
            return "redirect:/index";
        }
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<ResourceType> list(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("显示所有资源类型信息");
            return resourceTypeService.queryAll();
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager", method = RequestMethod.GET)
    public Pager4EasyUI<ResourceType> listPager(@Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            logger.info("分页显示资源类型信息");
            int total = resourceTypeService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<ResourceType> resourceTypes = resourceTypeService.queryByPager(pager);
            return new Pager4EasyUI<ResourceType>(pager.getTotalRecords(), resourceTypes);
        } else {
            logger.info("管理员未登录，不能分页显示资源类型列表");
            return null;
        }
    }

    @RequestMapping(value = "query/{id}", method = RequestMethod.GET)
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("根据资源类型id: " + id + "查询资源类型信息");
            ModelAndView mav = new ModelAndView("resource/restype_info");
            ResourceType resourceType = resourceTypeService.queryById(id);
            mav.addObject("resourceType", resourceType);
            return mav;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(ResourceType resourceType, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("更新资源类型信息");
            resourceTypeService.update(resourceType);
            return ControllerResult.getSuccessResult("成功更新资源类型信息");
        } else {
            return ControllerResult.getFailResult("更新资源类型信息失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            resourceTypeService.inactive(id);
            return ControllerResult.getSuccessResult("冻结资源类型成功");
        } else {
            return ControllerResult.getFailResult("没有权限冻结资源类型");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            resourceTypeService.active(id);
            return ControllerResult.getSuccessResult("已解除资源类型冻结");
        } else {
            return ControllerResult.getFailResult("没有权限激活资源类型");
        }
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
