package com.gs.controller;

import com.gs.bean.Admin;
import com.gs.bean.Customer;
import com.gs.common.Constants;
import com.gs.common.util.EncryptUtil;
import com.gs.service.AdminService;
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

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(Admin admin, HttpSession session) {
        if (session.getAttribute(Constants.SESSION_CUSTOMER) != null) {
            return "redirect:home";
        }
        admin.setPassword(EncryptUtil.md5Encrypt(admin.getPassword()));
        Admin a = adminService.query(admin);
        if (a != null) {
            session.setAttribute(Constants.SESSION_ADMIN, admin);
            return "redirect:home";
        } else {
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.SESSION_ADMIN);
        return "redirect:/index";
    }

    @RequestMapping(value = "add_page", method = RequestMethod.GET)
    public String toReg(Model model) {
        model.addAttribute(new Customer());
        return "customer/register";
    }

    @RequestMapping("add")
    public String add(Admin admin, HttpSession session) {
        admin.setPassword(EncryptUtil.md5Encrypt(admin.getPassword()));
        adminService.insert(admin);
        session.setAttribute(Constants.SESSION_ADMIN, admin);
        return "redirect:home";
    }

    @RequestMapping("home")
    public String home(HttpSession session) {
        if (session.getAttribute(Constants.SESSION_ADMIN) == null) {
            return "redirect:/index";
        }
        return "customer/home";
    }

    @ResponseBody
    @RequestMapping("list")
    public List<Admin> list() {
        logger.info("显示所有管理员信息");
        return adminService.queryAll();
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
