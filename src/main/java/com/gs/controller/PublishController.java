package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.Publish;
import com.gs.common.Constants;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
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
            return new ModelAndView("redirect:/index");
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager/{planId}", method = RequestMethod.GET)
    public Pager4EasyUI<Publish> searchPager(@Param("page")String page, @Param("rows")String rows, @PathVariable("planId") String planId, Publish publish, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("分页显示消息发布");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            publish.setPublishPlanId(planId);
            int total = publishService.countByCriteria(publish);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Publish> publishs = publishService.queryByPagerAndCriteria(pager, publish);
            return new Pager4EasyUI<Publish>(pager.getTotalRecords(), publishs);
        } else {
            logger.info("客户未登录，不能分页显示消息发布");
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
