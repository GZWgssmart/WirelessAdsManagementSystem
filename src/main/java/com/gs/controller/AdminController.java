package com.gs.controller;

import com.gs.bean.Admin;
import com.gs.bean.Customer;
import com.gs.common.Constants;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.EncryptUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.AdminService;
import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Resource
    private AdminService adminService;

    @RequestMapping(value = "login_page", method = RequestMethod.GET)
    public String toLoginPage(Model model) {
        model.addAttribute(new Admin());
        return "admin/login";
    }

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ControllerResult login(Admin admin, @Param("checkCode")String checkCode, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return ControllerResult.getSuccessResult("登录成功");
        }
        String codeInSession = (String) session.getAttribute(Constants.SESSION_CHECK_CODE);
        if(checkCode != null && checkCode.equals(codeInSession)) {
            admin.setPassword(EncryptUtil.md5Encrypt(admin.getPassword()));
            Admin a = adminService.query(admin);
            if (a != null) {
                session.setAttribute(Constants.SESSION_ADMIN, a);
                return ControllerResult.getSuccessResult("登录成功");
            } else {
                return ControllerResult.getFailResult("登录失败,请检查邮箱或密码");
            }
        } else {
            return ControllerResult.getFailResult("验证码错误");
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.SESSION_ADMIN);
        return "redirect:login_page";
    }

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(Admin admin, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            admin.setPassword(EncryptUtil.md5Encrypt(admin.getPassword()));
            adminService.insert(admin);
            logger.info("成功添加管理员");
            return ControllerResult.getSuccessResult("成功添加管理员");
        } else {
            return ControllerResult.getFailResult("没有权限添加管理员");
        }
    }

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "admin/home";
        } else {
            return "redirect:login_page";
        }
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            return "admin/admins";
        } else {
            return "redirect:login_page";
        }
    }

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<Admin> list(HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            logger.info("显示所有管理员信息");
            return adminService.queryAll();
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Admin> listPager(@Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            logger.info("分页显示管理员信息");
            int total = adminService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Admin> admins = adminService.queryByPager(pager);
            Pager4EasyUI<Admin> pager4EasyUI = new Pager4EasyUI<Admin>(pager.getTotalRecords(), admins);
            return pager4EasyUI;
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            adminService.inactive(id);
            return ControllerResult.getSuccessResult("冻结管理员成功");
        } else {
            return ControllerResult.getFailResult("没有权限冻结管理员");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            adminService.active(id);
            return ControllerResult.getSuccessResult("已解除管理员冻结");
        } else {
            return ControllerResult.getFailResult("没有权限激活管理员");
        }
    }

    @RequestMapping("index/{id}")
    public ModelAndView queryById(@PathVariable("id") String id) {
        ModelAndView mav = new ModelAndView("index");
        Admin admin = adminService.queryById(id);
        mav.addObject("admin", admin);
        return mav;
    }

    @RequestMapping("index")
    public String query(Model model) {
        List<Admin> admins = adminService.queryAll();
        for (Admin admin : admins) {
            System.out.println(admin);
        }
        model.addAttribute("admins", admins);
        return "index";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
