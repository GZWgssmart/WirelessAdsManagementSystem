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
import com.gs.service.CustomerService;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by WangGenshen on 5/16/16.
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Resource
    private CustomerService customerService;

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ControllerResult login(Customer customer, @Param("checkCode")String checkCode, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return ControllerResult.getSuccessResult("登录成功");
        }
        String codeInSession = (String) session.getAttribute(Constants.SESSION_CHECK_CODE);
        if(checkCode != null && checkCode.equals(codeInSession)) {
            customer.setPassword(EncryptUtil.md5Encrypt(customer.getPassword()));
            Customer c = customerService.query(customer);
            if (c != null) {
                customerService.updateLoginTime(c.getId());
                session.setAttribute(Constants.SESSION_CUSTOMER, c);
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
        session.removeAttribute(Constants.SESSION_CUSTOMER);
        return "redirect:/index";
    }

    @RequestMapping(value = "reg_page", method = RequestMethod.GET)
    public String toRegPage(Model model) {
        model.addAttribute(new Customer());
        return "customer/register";
    }

    @RequestMapping("reg")
    public String reg(Customer customer, HttpSession session) {
        customer.setPassword(EncryptUtil.md5Encrypt(customer.getPassword()));
        customerService.insert(customer);
        session.setAttribute(Constants.SESSION_CUSTOMER, customer);
        return "redirect:home";
    }

    @RequestMapping("home")
    public String home(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "customer/home";
        } else {
            return "redirect:/index";
        }
    }

    @RequestMapping("list_page")
    public String toListPage(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "customer/customers";
        } else {
            return "redirect:/admin/login_page";
        }
    }

    @ResponseBody
    @RequestMapping("list")
    public List<Customer> list(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("显示所有客户信息");
            return customerService.queryAll();
        } else {
            logger.info("管理员未登录，不能显示客户列表");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Customer> listPager(@Param("page")String page, @Param("rows")String rows, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("分页显示客户信息");
            int total = customerService.count();
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Customer> customers = customerService.queryByPager(pager);
            return new Pager4EasyUI<Customer>(pager.getTotalRecords(), customers);
        } else {
            logger.info("管理员未登录，不能分页显示客户列表");
            return null;
        }
    }

    @RequestMapping("query/{id}")
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("根据客户id: " + id + "查询客户信息");
            ModelAndView mav = new ModelAndView("customer/info");
            Customer customer = customerService.queryById(id);
            mav.addObject("customer", customer);
            return mav;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(Customer customer, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("更新客户信息");
            logger.info(customer.getAddress());
            customerService.update(customer);
            return ControllerResult.getSuccessResult("成功更新用户信息");
        } else {
            return ControllerResult.getFailResult("更新用户信息失败");
        }
    }

    @RequestMapping(value = "setting_page", method = RequestMethod.GET)
    public String settingPage(Customer customer, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "customer/setting";
        } else {
            return "redirect:/index";
        }
    }

    @ResponseBody
    @RequestMapping(value = "update_pwd", method = RequestMethod.POST)
    public ControllerResult updatePwd(@Param("password")String password, @Param("newPwd")String newPwd, @Param("conPwd")String conPwd, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            if (customer.getPassword().equals(EncryptUtil.md5Encrypt(password)) && newPwd != null && conPwd != null && newPwd.equals(conPwd)) {
                customer.setPassword(EncryptUtil.md5Encrypt(newPwd));
                customerService.updatePassword(customer);
                session.setAttribute(Constants.SESSION_CUSTOMER, customer);
                return ControllerResult.getSuccessResult("更新用户密码成功");
            } else {
                return ControllerResult.getFailResult("原密码错误,或新密码与确认密码不一致");
            }
        } else {
            return ControllerResult.getFailResult("无法更新用户密码");
        }
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
