package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.ResourceType;
import com.gs.common.Constants;
import com.gs.common.bean.ComboBox4EasyUI;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.FileUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.util.UUIDUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.ResourceService;
import com.gs.service.ResourceTypeService;
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
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
@Controller
@RequestMapping("/res")
public class ResourceController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @Resource
    private ResourceService resourceService;
    @Resource
    private ResourceTypeService resourceTypeService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(com.gs.bean.Resource resource, MultipartFile file, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            if (resourceService.queryByNameAndCustomer(resource.getName(), customer.getId()) == null) {
                resource.setCustomerId(customer.getId());
                if (file != null) {
                    String ofileName = file.getOriginalFilename();
                    String fileName = UUIDUtil.uuid() + FileUtil.getExtension(ofileName);
                    File targetFile = new File(FileUtil.uploadPath(session, customer.getId()), fileName);
                    try {
                        file.transferTo(targetFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    resource.setOfileName(ofileName);
                    resource.setFileName(fileName);
                    resource.setPath(FileUtil.uploadFilePath(targetFile));
                    resource.setFullPath(targetFile.getAbsolutePath());
                    resource.setFileSize(targetFile.length());
                } else {
                    resource.setOfileName("无");
                    resource.setFileName("无");
                    resource.setPath("无");
                    resource.setFullPath("无");
                    resource.setFileSize(0L);
                }
                resourceService.insert(resource);
                return ControllerResult.getSuccessResult("成功添加资源");
            } else {
                return ControllerResult.getFailResult("已经存在该名称的资源");
            }
        }
        return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "resource/resources";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "resource-mobile/resources";
        } else {
            return "redirect:/mob/redirect_index";
        }
    }

    @RequestMapping(value = "list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdmin(@PathVariable("customerId") String customerId, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("resource/resources_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/redirect_login_page");
        }
    }

    @RequestMapping(value = "mob/list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdminMob(@PathVariable("customerId") String customerId, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("resource-mobile/resources_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/mob/redirect_login_page");
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<com.gs.bean.Resource> searchPager(@Param("page")String page, @Param("rows")String rows, com.gs.bean.Resource resource, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("show resources by pager");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            int total = resourceService.countByCriteria(resource, customer.getId());
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<com.gs.bean.Resource> resources = resourceService.queryByPagerAndCriteria(pager, resource, customer.getId());
            return new Pager4EasyUI<com.gs.bean.Resource>(pager.getTotalRecords(), resources);
        } else {
            logger.info("can not show resources by pager cause customer is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager_admin/{customerId}", method = RequestMethod.GET)
    public Pager4EasyUI<com.gs.bean.Resource> searchPagerAdmin(@PathVariable("customerId") String customerId, @Param("page")String page, @Param("rows")String rows, com.gs.bean.Resource resource, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show resources for admin");
            int total = resourceService.countByCriteria(resource, customerId);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<com.gs.bean.Resource> resources = resourceService.queryByPagerAndCriteria(pager, resource, customerId);
            return new Pager4EasyUI<com.gs.bean.Resource>(pager.getTotalRecords(), resources);
        } else {
            logger.info("can not show resources cause admin is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(com.gs.bean.Resource resource, MultipartFile file, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            List<com.gs.bean.Resource> resources = resourceService.queryByNameNotSelf(resource);
            if (resources == null || resources.size() == 0) {
                logger.info("update resource info");
                Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
                if (file != null) {
                    String ofileName = file.getOriginalFilename();
                    String fileName = UUIDUtil.uuid() + FileUtil.getExtension(ofileName);
                    File targetFile = new File(FileUtil.uploadPath(session, customer.getId()), fileName);
                    try {
                        file.transferTo(targetFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    resource.setOfileName(ofileName);
                    resource.setFileName(fileName);
                    resource.setPath(FileUtil.uploadFilePath(targetFile));
                    resource.setFullPath(targetFile.getAbsolutePath());
                    resource.setFileSize(targetFile.length());
                }
                resourceService.update(resource);
                return ControllerResult.getSuccessResult("成功更新资源信息");
            } else {
                return ControllerResult.getFailResult("已经存在该名称的资源");
            }
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            resourceService.inactive(id);
            return ControllerResult.getSuccessResult("冻结资源信息成功");
        } else {
            return ControllerResult.getFailResult("没有权限冻结资源信息");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            resourceService.active(id);
            return ControllerResult.getSuccessResult("已解除资源信息冻结");
        } else {
            return ControllerResult.getFailResult("没有权限激活资源信息");
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_combo/{id}/{status}", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> listCombo(@PathVariable("id") String id, @PathVariable("status") String status, HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isCustomer(session)) {
            String resourceTypeId = resourceService.queryByResourceId(id);
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            String theStatus = null;
            if (status.equals("Y")) {
                theStatus = "Y";
            }
            List<ResourceType> resourceTypes = resourceTypeService.queryAll(theStatus);
            for (ResourceType rt : resourceTypes) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(rt.getId());
                comboBox4EasyUI.setText(rt.getName());
                if (rt.getId().equals(resourceTypeId)) {
                    comboBox4EasyUI.setSelected(true);
                }
                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
        }
        return comboBox4EasyUIs;
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
