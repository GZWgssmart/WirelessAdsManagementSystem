package com.gs.controller;

import com.gs.bean.DeviceResource;
import com.gs.bean.ResourceType;
import com.gs.bean.TimeSegment;
import com.gs.common.Constants;
import com.gs.common.bean.ComboBox4EasyUI;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.DateFormatUtil;
import com.gs.common.util.DateParseUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.ResourceTypeService;
import com.gs.service.TimeSegmentService;
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
import javax.swing.text.Segment;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by WangGenshen on 7/13/16.
 */
@Controller
@RequestMapping("/segment")
public class TimeSegmentController {

    private static final Logger logger = LoggerFactory.getLogger(TimeSegmentController.class);

    @Resource
    private TimeSegmentService timeSegmentService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(TimeSegment timeSegment, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            timeSegmentService.insert(timeSegment);
            return ControllerResult.getSuccessResult("成功添加时段信息");
        }
        return null;
    }

    @RequestMapping(value = "list_page/{pubId}", method = RequestMethod.GET)
    public ModelAndView toListPage(@PathVariable("pubId") String pubId, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("publish/segments");
            mav.addObject("pubId", pubId);
            return mav;
        } else {
            return new ModelAndView("redirect:/index");
        }
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<TimeSegment> list(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("显示所有时段信息");
            return timeSegmentService.queryAll();
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager/{pubId}", method = RequestMethod.GET)
    public Pager4EasyUI<TimeSegment> listPager(@PathVariable("pubId") String pubId, @Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            logger.info("分页显示时段信息");
            int total = timeSegmentService.countByPubId(pubId);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<TimeSegment> timeSegments = timeSegmentService.queryByPagerAndPubId(pager, pubId);
            for (TimeSegment timeSegment : timeSegments) {
                timeSegment.setStartTimeStr(DateFormatUtil.format(timeSegment.getStartTime(), Constants.DATETIME_PATTERN));
                timeSegment.setEndTimeStr(DateFormatUtil.format(timeSegment.getEndTime(), Constants.DATETIME_PATTERN));
            }
            return new Pager4EasyUI<TimeSegment>(pager.getTotalRecords(), timeSegments);
        } else {
            logger.info("不能分页显示时段信息列表");
            return null;
        }
    }

    @RequestMapping(value = "query/{id}", method = RequestMethod.GET)
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            logger.info("根据时段id: " + id + "查询时段信息");
            ModelAndView mav = new ModelAndView("publish/segment_info");
            TimeSegment timeSegment = timeSegmentService.queryById(id);
            mav.addObject("timeSegment", timeSegment);
            return mav;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(TimeSegment timeSegment, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("更新时段信息");
            timeSegment.setStartTime(DateParseUtil.parseDate(timeSegment.getStartTimeStr(), Constants.DATETIME_PATTERN));
            timeSegment.setEndTime(DateParseUtil.parseDate(timeSegment.getEndTimeStr(), Constants.DATETIME_PATTERN));
            timeSegmentService.update(timeSegment);
            return ControllerResult.getSuccessResult("成功更新时段信息");
        } else {
            return ControllerResult.getFailResult("更新时段信息失败");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            timeSegmentService.inactive(id);
            return ControllerResult.getSuccessResult("冻结时段信息成功");
        } else {
            return ControllerResult.getFailResult("没有权限冻结时段信息");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            timeSegmentService.active(id);
            return ControllerResult.getSuccessResult("已解除时段信息冻结");
        } else {
            return ControllerResult.getFailResult("没有权限激活时段信息");
        }
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
