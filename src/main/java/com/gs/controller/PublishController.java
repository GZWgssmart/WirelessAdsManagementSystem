package com.gs.controller;

import com.gs.bean.*;
import com.gs.common.Constants;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.DateFormatUtil;
import com.gs.common.util.EncryptUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.ADSServerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.net.parser.Common;
import com.gs.net.server.ADSServer;
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
@RequestMapping("/publish")
public class PublishController {

    private static final Logger logger = LoggerFactory.getLogger(PublishController.class);

    @Resource
    private PublishService publishService;

    @RequestMapping(value = "list_page/{planId}", method = RequestMethod.GET)
    public ModelAndView toListPage(@PathVariable("planId") String planId, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("publish/publishes");
            mav.addObject("planId", planId);
            return mav;
        } else {
            return new ModelAndView("redirect:/redirect_index");
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager/{planId}", method = RequestMethod.GET)
    public Pager4EasyUI<Publish> searchPager(@Param("page")String page, @Param("rows")String rows, @PathVariable("planId") String planId, Publish publish, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            logger.info("show publishes by pager for specified planid");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            publish.setPublishPlanId(planId);
            int total = publishService.countByCriteria(publish);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Publish> publishs = publishService.queryByPagerAndCriteria(pager, publish);
            return new Pager4EasyUI<Publish>(pager.getTotalRecords(), publishs);
        } else {
            logger.info("can not show publishes by pager for specified planid cause is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_res_pager/{planId}", method = RequestMethod.GET)
    public Pager4EasyUI<Publish> searchResPager(@Param("page")String page, @Param("rows")String rows, @PathVariable("planId") String planId, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            logger.info("show all resources by pager for specified plan");
            int total = publishService.countRes(planId);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Publish> publishs = publishService.queryResByPager(pager, planId);
            return new Pager4EasyUI<Publish>(pager.getTotalRecords(), publishs);
        } else {
            logger.info("can not show all resources by pager for specified plan cause is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_res_pager_dev/{deviceId}", method = RequestMethod.GET)
    public Pager4EasyUI<PubResource> searchResPagerDev(@Param("page")String page, @Param("rows")String rows, @PathVariable("deviceId") String deviceId, Publish publish, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            logger.info("show resources by pager for specified device");
            publish.setDeviceId(deviceId);
            int total = publishService.countResByDevId(publish);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<PubResource> pubResources = publishService.queryResByDevId(pager, publish);
            return new Pager4EasyUI<PubResource>(pager.getTotalRecords(), pubResources);
        } else {
            logger.info("can not show resources by pager for specified device cause is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "delete_res/{deviceId}/{resIds}", method = RequestMethod.GET)
    public ControllerResult deleteRes(@PathVariable("deviceId") String deviceId, @PathVariable("resIds") String resIds, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("delete resources which published");
            List<Publish> publishes = publishService.queryByDevIdAndResIds(deviceId, resIds);
            ADSServer adsServer = ADSServerUtil.getADSServerFromServletContext();
            for (Publish publish : publishes) {
                adsServer.writeFileDelete(publish);
            }
            return ControllerResult.getSuccessResult("资源删除消息已经开始处理，请关注每个发布的发布日志！");
        } else {
            logger.info("can not show all the resource for the publish cause customer is not login");
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_chosen_res/{planId}/{area}", method = RequestMethod.GET)
    public Pager4EasyUI<PublishResourceDetail> searchChosenPager(@PathVariable("planId") String planId, @PathVariable("area") int area, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("show resources by pager for specified planId and area");
            if (planId.equals("none")) {
                return null;
            } else {
                Publish publish = new Publish();
                publish.setPublishPlanId(planId);
                publish.setArea(area);
                int total = publishService.countByCriteria(publish);
                Pager pager = PagerUtil.getPager(1, 200, total);
                List<Publish> publishes = publishService.queryByPagerAndCriteria(pager, publish);
                List<PublishResourceDetail> publishResourceDetails = new ArrayList<PublishResourceDetail>();
                for (Publish p : publishes) {
                    PublishResourceDetail detail = new PublishResourceDetail();
                    detail.setArea(p.getArea());
                    detail.setSegments(p.getSegments() == null ? "" : p.getSegments());
                    detail.setResourceId(p.getResource().getId());
                    detail.setResourceName(p.getResource().getName());
                    detail.setResourceType(p.getResource().getResourceTypeName());
                    detail.setEndTime(p.getEndTime());
                    if (detail.getEndTime() != null) {
                        detail.setEndTimeStr(DateFormatUtil.format(detail.getEndTime(), "yyyy-MM-dd"));
                    }
                    detail.setShowType(p.getShowType());
                    detail.setStartTime(p.getStartTime());
                    if (detail.getStartTime() != null) {
                        detail.setStartTimeStr(DateFormatUtil.format(detail.getStartTime(), "yyyy-MM-dd"));
                    }
                    detail.setStayTime(p.getStayTime());
                    detail.setShowCount(p.getShowCount());
                    publishResourceDetails.add(detail);
                }
                return new Pager4EasyUI<PublishResourceDetail>(pager.getTotalRecords(), publishResourceDetails);
            }
        } else {
            logger.info("can not show resources by pager for specified planId and area cause customer is not login");
            return null;
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
