package com.gs.controller;

import com.gs.bean.TimeSegment;
import com.gs.common.Constants;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.DateFormatUtil;
import com.gs.common.util.DateParseUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    @RequestMapping(value = "querybyplanid/{id}", method = RequestMethod.GET)
    public List<TimeSegment> queryByPlanId(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            logger.info("根据计划id: " + id + "查询时段信息");
            return timeSegmentService.queryByPlanId(id);
        }
        return null;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
