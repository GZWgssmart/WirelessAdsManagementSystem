package com.gs.controller;

import com.alibaba.fastjson.JSON;
import com.gs.bean.*;
import com.gs.common.Constants;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.DateParseUtil;
import com.gs.common.util.EncryptUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.ADSServerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.net.server.ADSServer;
import com.gs.service.CustomerService;
import com.gs.service.PublishPlanService;
import com.gs.service.PublishService;
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
@RequestMapping("/pubplan")
public class PublishPlanController {

    private static final Logger logger = LoggerFactory.getLogger(PublishPlanController.class);

    @Resource
    private PublishPlanService publishPlanService;
    @Resource
    private PublishService publishService;
    @Resource
    private CustomerService customerService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(PublishPlan publishPlan, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            addOrEditPubPlan("add", customer, publishPlan);
            return ControllerResult.getSuccessResult("成功添加计划");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    private void addOrEditPubPlan(String addOrEdit, Customer customer, PublishPlan publishPlan) {
        publishPlan.setCustomerId(customer.getId());
        List<String> deviceIds = publishPlanService.getDeviceIds(customer.getId(), publishPlan.getType(), publishPlan.getDeviceId(), publishPlan.getVersionId());
        String name = "";
        String addType = publishPlan.getType();
        if (addType.equals("one")) {
            name = publishPlan.getDeviceCode();
        } else if (addType.equals("multiple")) {
            if (deviceIds.size() == 1) {
                name = publishPlan.getDeviceCode();
                publishPlan.setType("one");
            } else {
                name = "多个终端";
            }
        } else if (addType.equals("group")) {
            name = "分组终端";
        } else if (addType.equals("all")) {
            name = "全部终端";
        }
        publishPlan.setName(name);
        PublishPlan pp = null;
        if (addOrEdit.equals("add")) {
            pp = publishPlanService.insertBack(publishPlan);
        } else if (addOrEdit.equals("edit")) { // 如果是修改计划，则直接把该计划下原有的所有发布信息删除掉,重新添加发布信息到此计划下
            pp = publishPlan;
            publishPlanService.update(publishPlan);
            publishService.deleteByPlanId(publishPlan.getId());
        }
        List<PublishResourceDetail> details = JSON.parseArray("[" + publishPlan.getResourceDetails() + "]", PublishResourceDetail.class);
        List<Publish> publishs = new ArrayList<Publish>();
        for (String deviceId : deviceIds) {
            for (PublishResourceDetail detail : details) {
                Publish publish = copyFromDetail(detail);
                publish.setDeviceId(deviceId);
                publish.setPublishPlanId(pp.getId());
                publishs.add(publish);
            }
        }
        publishService.batchInsert(publishs);
        publishPlan.setDevCount(publishs.size());
        publishPlan.setNotFinishCount(publishs.size());
        publishPlan.setFinishCount(0);
        publishPlanService.updateCount(publishPlan);
    }

    private Publish copyFromDetail(PublishResourceDetail detail) {
        Publish publish = new Publish();
        publish.setPublishLog(PublishLog.NOT_SUBMIT_TO_CHECK);
        publish.setResourceId(detail.getResourceId());
        publish.setArea(detail.getArea());
        publish.setShowType(detail.getShowType());
        if (detail.getStartTimeStr() != null && !detail.getStartTimeStr().equals("")) {
            detail.setStartTime(DateParseUtil.parseDate(detail.getStartTimeStr(), "yyyy-MM-dd"));
        }
        publish.setStartTime(detail.getStartTime());
        if (detail.getEndTimeStr() != null && !detail.getEndTimeStr().equals("")) {
            detail.setEndTime(DateParseUtil.parseDate(detail.getEndTimeStr(), "yyyy-MM-dd"));
        }
        publish.setEndTime(detail.getEndTime());
        publish.setStayTime(detail.getStayTime());
        publish.setShowCount(detail.getShowCount());
        publish.setSegments(detail.getSegments());
        return publish;
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish/pub_plans";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish-mobile/pub_plans";
        } else {
            return "redirect:/mob/redirect_index";
        }
    }

    @RequestMapping(value = "list_page_checking", method = RequestMethod.GET)
    public String toListPageChecking(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish/pub_plans_check";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/list_page_checking", method = RequestMethod.GET)
    public String toListPageCheckingMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish-mobile/pub_plans_check";
        } else {
            return "redirect:/mob/redirect_index";
        }
    }

    @RequestMapping(value = "list_page_checked", method = RequestMethod.GET)
    public String toListPageChecked(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish/pub_plans_checked";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/list_page_checked", method = RequestMethod.GET)
    public String toListPageCheckedMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish-mobile/pub_plans_checked";
        } else {
            return "redirect:/mob/redirect_index";
        }
    }

    @RequestMapping(value = "list_page_finish", method = RequestMethod.GET)
    public String toListPageFinish(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish/pub_plans_finish";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/list_page_finish", method = RequestMethod.GET)
    public String toListPageFinishMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "publish-mobile/pub_plans_finish";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdmin(@PathVariable("customerId") String customerId, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("publish/pub_plans_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/redirect_login_page");
        }
    }

    @RequestMapping(value = "mob/list_page_admin/{customerId}", method = RequestMethod.GET)
    public ModelAndView toListPageAdminMob(@PathVariable("customerId") String customerId, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("publish-mobile/pub_plans_admin");
            mav.addObject("customerId", customerId);
            return mav;
        } else {
            return new ModelAndView("redirect:/admin/mob/redirect_login_page");
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<PublishPlan> searchPager(@Param("page")String page, @Param("rows")String rows, PublishPlan publishPlan, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("show plan by pager for customer");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            int total = publishPlanService.countByCriteria(publishPlan, customer.getId());
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<PublishPlan> publishPlans = publishPlanService.queryByPagerAndCriteria(pager, publishPlan, customer.getId());
            return new Pager4EasyUI<PublishPlan>(pager.getTotalRecords(), publishPlans);
        } else {
            logger.info("can not show plan cause customer is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager_admin/{customerId}", method = RequestMethod.GET)
    public Pager4EasyUI<PublishPlan> searchPagerAdmin(@PathVariable("customerId") String customerId, @Param("page")String page, @Param("rows")String rows, PublishPlan publishPlan, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show plan for admin");
            int total = publishPlanService.countByCriteria(publishPlan, customerId);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<PublishPlan> publishPlans = publishPlanService.queryByPagerAndCriteria(pager, publishPlan, customerId);
            return new Pager4EasyUI<PublishPlan>(pager.getTotalRecords(), publishPlans);
        } else {
            logger.info("can not show plan for admin cause admin is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "all_dev/{planId}", method = RequestMethod.GET)
    public ControllerResult queryAllDevices(@PathVariable("planId") String planId, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("query all devices for specified plan");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            List<Publish> publishs = publishService.allDevByPlanId(planId);
            String devIds = "";
            if (publishs != null) {
                for (Publish p : publishs) {
                    if (devIds.equals("")) {
                        devIds = p.getDeviceId();
                    } else {
                        if (devIds.indexOf(p.getDeviceId()) < 0) {
                            devIds += "," + p.getDeviceId();
                        }
                    }
                }
            }
            return new ControllerResult(200, devIds, "成功查询计划下所有设备");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(PublishPlan publishPlan, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("update plan by customer");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            addOrEditPubPlan("edit", customer, publishPlan);
            return ControllerResult.getSuccessResult("成功更新消息计划");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            publishPlanService.inactive(id);
            return ControllerResult.getSuccessResult("冻结消息计划成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            publishPlanService.active(id);
            return ControllerResult.getSuccessResult("已解除消息计划冻结");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "check", method = RequestMethod.POST)
    public ControllerResult check(@Param("id")String id, @Param("checkStatus") String checkStatus, @Param("checkPwd") String checkPwd, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            if (checkStatus != null && checkStatus.equals("checking")) { // 正在审核中
                publishPlanService.check(id, checkStatus);
                publishService.updatePublishLogByPlanId(id, PublishLog.SUBMIT_TO_CHECK); // 该计划下的所有device都设置成提交审核
                return ControllerResult.getSuccessResult("计划已提交审核");
            } else if (checkStatus != null && checkStatus.equals("checked")){ // 已审核通过
                String checkPwdDB = customerService.queryCheckPwdByEmail(customer.getEmail());
                if (checkPwdDB.equals(EncryptUtil.md5Encrypt(checkPwd))) {
                    publishPlanService.check(id, checkStatus);
                    publishService.updatePublishLogByPlanId(id, PublishLog.ALREADY_CHECKED); // 该计划下的所有device都设置成提交审核
                    // 一旦审核,则需要通知客户端下载文件,并完成发布操作，只有完成发布操作后，整个审核才算完毕
                    // 查找单个计划，及此计划下的所有终端,每一个终端都要开始发送文件下载通知
                    List<Publish> publishs = publishService.queryByPlanId(id);
                    ADSServer adsServer = ADSServerUtil.getADSServerFromServletContext();
                    for (Publish publish : publishs) {
                        adsServer.writeFileDownload(null, publish);
                    }
                    return ControllerResult.getSuccessResult("此计划已经审核通过并开始处理消息,请关注计划下每个终端的发布日志");
                } else {
                    return ControllerResult.getFailResult("审核密码不正确");
                }
            }
            return ControllerResult.getFailResult("您可能正在尝试其他操作,请联系技术支持");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "reject", method = RequestMethod.POST)
    public ControllerResult reject(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            publishPlanService.check(id, "not_submit");
            publishService.updatePublishLogByPlanId(id, PublishLog.NOT_SUBMIT_TO_CHECK);
            return ControllerResult.getSuccessResult("计划已驳回");
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
