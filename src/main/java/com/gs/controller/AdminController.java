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

    @RequestMapping(value = "mob/login_page", method = RequestMethod.GET)
    public String toLoginPageMob(Model model) {
        model.addAttribute(new Admin());
        return "admin-mobile/login";
    }

    @RequestMapping(value = "redirect_login_page", method = RequestMethod.GET)
    public String redirectLoginPage(Model model) {
        model.addAttribute(new Admin());
        model.addAttribute("redirect", "redirect");
        return "admin/login";
    }

    @RequestMapping(value = "mob/redirect_login_page", method = RequestMethod.GET)
    public String redirectLoginPageMob(Model model) {
        model.addAttribute(new Admin());
        model.addAttribute("redirect", "redirect");
        return "admin-mobile/login";
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
                adminService.updateLoginTime(a.getId());
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

    @RequestMapping(value = "mob/logout", method = RequestMethod.GET)
    public String logoutMob(HttpSession session) {
        session.removeAttribute(Constants.SESSION_ADMIN);
        return "redirect:login_page";
    }

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(Admin admin, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            admin.setPassword(EncryptUtil.md5Encrypt(admin.getPassword()));
            adminService.insert(admin);
            logger.info("Add admin successfully");
            return ControllerResult.getSuccessResult("成功添加管理员");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
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

    @RequestMapping(value = "mob/home", method = RequestMethod.GET)
    public String homeMob(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "admin-mobile/home";
        } else {
            return "redirect:login_page";
        }
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            return "admin/admins";
        } else {
            return "redirect:redirect_login_page";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            return "admin-mobile/admins";
        } else {
            return "redirect:redirect_login_page";
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Admin> searchPager(@Param("page")String page, @Param("rows")String rows, Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show admin info by pager");
            int total = adminService.countByCriteria(admin);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Admin> admins = adminService.queryByPagerAndCriteria(pager, admin);
            return new Pager4EasyUI<Admin>(pager.getTotalRecords(), admins);
        } else {
            logger.info("can not show admin info by pager cause admin is not login");
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
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session)) {
            adminService.active(id);
            return ControllerResult.getSuccessResult("已解除管理员冻结");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @RequestMapping(value = "query/{id}", method = RequestMethod.GET)
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("admin/info");
            Admin admin = adminService.queryById(id);
            mav.addObject("admin", admin);
            return mav;
        }
        return new ModelAndView("redirect:/admin/redirect_login_page");
    }

    @RequestMapping(value = "mob/query/{id}", method = RequestMethod.GET)
    public ModelAndView queryByIdMob(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("admin-mobile/info");
            Admin admin = adminService.queryById(id);
            mav.addObject("admin", admin);
            return mav;
        }
        return new ModelAndView("redirect:/admin/mob/redirect_login_page");
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("update admin info successfully");
            adminService.update(admin);
            return ControllerResult.getSuccessResult("成功更新管理员信息");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @RequestMapping(value = "setting_page", method = RequestMethod.GET)
    public String settingPage(Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "admin/setting";
        } else {
            return "redirect:redirect_login_page";
        }
    }

    @RequestMapping(value = "mob/setting_page", method = RequestMethod.GET)
    public String settingPageMob(Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "admin-mobile/setting";
        } else {
            return "redirect:redirect_login_page";
        }
    }

    @ResponseBody
    @RequestMapping(value = "update_pwd", method = RequestMethod.POST)
    public ControllerResult updatePwd(@Param("password")String password, @Param("newPwd")String newPwd, @Param("conPwd")String conPwd, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            Admin admin = (Admin) session.getAttribute(Constants.SESSION_ADMIN);
            if (admin.getPassword().equals(EncryptUtil.md5Encrypt(password)) && newPwd != null && conPwd != null && newPwd.equals(conPwd)) {
                admin.setPassword(EncryptUtil.md5Encrypt(newPwd));
                adminService.updatePassword(admin);
                session.setAttribute(Constants.SESSION_ADMIN, admin);
                return ControllerResult.getSuccessResult("更新管理员密码成功");
            } else {
                return ControllerResult.getFailResult("原密码错误,或新密码与确认密码不一致");
            }
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "update_other_pwd", method = RequestMethod.POST)
    public ControllerResult updateOtherPwd(Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            admin.setPassword(EncryptUtil.md5Encrypt(admin.getPassword()));
            adminService.updatePassword(admin);
            return ControllerResult.getSuccessResult("更新管理员密码成功");
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
