package com.gs.controller;

import com.gs.bean.Admin;
import com.gs.bean.Customer;
import com.gs.bean.DeviceGroup;
import com.gs.bean.Version;
import com.gs.common.Constants;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.EncryptUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.CustomerService;
import com.gs.service.DeviceGroupService;
import com.gs.service.VersionService;
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

    @Resource
    private VersionService versionService;

    @Resource
    private DeviceGroupService deviceGroupService;

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
                // 当用户第一次登录时,loginTime的值为空,此时需要为用户创建一个终端的默认分组
                if (c.getLoginTime() == null) {
                    DeviceGroup deviceGroup = new DeviceGroup();
                    deviceGroup.setCustomerId(c.getId());
                    deviceGroup.setName("默认分组");
                    deviceGroup.setDes("默认分组");
                    deviceGroupService.insert(deviceGroup);
                }
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

    @RequestMapping(value = "mob/logout", method = RequestMethod.GET)
    public String logoutMob(HttpSession session) {
        session.removeAttribute(Constants.SESSION_CUSTOMER);
        return "redirect:/mob/index";
    }

    @RequestMapping(value = "reg_page", method = RequestMethod.GET)
    public String toRegPage(Model model) {
        model.addAttribute(new Customer());
        return "customer/register";
    }

    @RequestMapping(value = "reg", method = RequestMethod.POST)
    public String reg(Customer customer, HttpSession session) {
        customer.setPassword(EncryptUtil.md5Encrypt(customer.getPassword()));
        customerService.insert(customer);
        session.setAttribute(Constants.SESSION_CUSTOMER, customer);
        return "redirect:home";
    }

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(Customer customer, HttpSession session) {
        if (SessionUtil.isAdmin(session) || SessionUtil.isSuperAdmin(session)) {
            customer.setPassword(EncryptUtil.md5Encrypt(customer.getPassword()));
            customer.setCheckPwd(EncryptUtil.md5Encrypt(customer.getCheckPwd()));
            customerService.insert(customer);
            return ControllerResult.getSuccessResult("成功添加客户信息");
        }
        return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
    }

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ModelAndView home(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            ModelAndView mav = new ModelAndView("customer/home");
            List<Version> versions = versionService.queryByCustomerAndGroupById(customer.getId()); // 只需要查询该客户下的所有终端版本
            mav.addObject("versions", versions);
            return mav;
        } else {
            return new ModelAndView("redirect:/index");
        }
    }

    @RequestMapping(value = "mob/home", method = RequestMethod.GET)
    public ModelAndView homeMob(HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            ModelAndView mav = new ModelAndView("customer-mobile/home");
            List<Version> versions = versionService.queryByCustomerAndGroupById(customer.getId()); // 只需要查询该客户下的所有终端版本
            mav.addObject("versions", versions);
            return mav;
        } else {
            return new ModelAndView("redirect:/mob/index");
        }
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "customer/customers";
        } else {
            return "redirect:/admin/redirect_login_page";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "customer-mobile/customers";
        } else {
            return "redirect:/admin/mob/redirect_login_page";
        }
    }

    @RequestMapping(value = "list_page_admin/{type}", method = RequestMethod.GET)
    public String toListPageAdmin(@PathVariable("type") String type, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            if (type.equals("res")) {
                return "customer/customers_res_admin";
            } else if (type.equals("dev")) {
                return "customer/customers_dev_admin";
            } else if(type.equals("devgroup")) {
                return "customer/customers_devg_admin";
            } else if (type.equals("pub")) {
                return "customer/customers_pubplan_admin";
            }
        }
        return "redirect:/admin/redirect_login_page";
    }

    @RequestMapping(value = "/mob/list_page_admin/{type}", method = RequestMethod.GET)
    public String toListPageAdminMob(@PathVariable("type") String type, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            if (type.equals("res")) {
                return "customer-mobile/customers_res_admin";
            } else if (type.equals("dev")) {
                return "customer-mobile/customers_dev_admin";
            } else if(type.equals("devgroup")) {
                return "customer-mobile/customers_devg_admin";
            } else if (type.equals("pub")) {
                return "customer-mobile/customers_pubplan_admin";
            }
        }
        return "redirect:/admin/mob/redirect_login_page";
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Customer> searchPager(@Param("page")String page, @Param("rows")String rows, Customer customer, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show customers by pager");
            int total = customerService.countByCriteria(customer);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Customer> customers = customerService.queryByPagerAndCriteria(pager, customer);
            return new Pager4EasyUI<Customer>(pager.getTotalRecords(), customers);
        } else {
            logger.info("can not show customer by pager cause admin is no login");
            return null;
        }
    }

    @RequestMapping(value = "query/{id}", method = RequestMethod.GET)
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("query customer info by id: " + id);
            ModelAndView mav = new ModelAndView("customer/info");
            Customer customer = customerService.queryById(id);
            mav.addObject("customer", customer);
            return mav;
        }
        return new ModelAndView("redirect:/index");
    }

    @RequestMapping(value = "mob/query/{id}", method = RequestMethod.GET)
    public ModelAndView queryByIdMob(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("query customer info by id: " + id);
            ModelAndView mav = new ModelAndView("customer-mobile/info");
            Customer customer = customerService.queryById(id);
            mav.addObject("customer", customer);
            return mav;
        }
        return new ModelAndView("redirect:/mob/index");
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(Customer customer, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session) || SessionUtil.isCustomer(session)) {
            logger.info("update customer info");
            customerService.update(customer);
            return ControllerResult.getSuccessResult("成功更新用户信息");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @RequestMapping(value = "setting_page", method = RequestMethod.GET)
    public String settingPage(Customer customer, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "customer/setting";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/setting_page", method = RequestMethod.GET)
    public String settingPageMob(Customer customer, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "customer-mobile/setting";
        } else {
            return "redirect:/mob/redirect_index";
        }
    }

    @RequestMapping(value = "check_pwd_page", method = RequestMethod.GET)
    public String checkPwdPage(Customer customer, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "customer/check_pwd";
        } else {
            return "redirect:/redirect_index";
        }
    }

    @RequestMapping(value = "mob/check_pwd_page", method = RequestMethod.GET)
    public String checkPwdPageMob(Customer customer, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            return "customer-mobile/check_pwd";
        } else {
            return "redirect:/mob/redirect_index";
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
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "update_check_pwd", method = RequestMethod.POST)
    public ControllerResult updateCheckPwd(@Param("checkPwd")String checkPwd, @Param("newPwd")String newPwd, @Param("conPwd")String conPwd, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            if (customer.getCheckPwd().equals(EncryptUtil.md5Encrypt(checkPwd)) && newPwd != null && conPwd != null && newPwd.equals(conPwd)) {
                customer.setCheckPwd(EncryptUtil.md5Encrypt(newPwd));
                customerService.updateCheckPwd(customer);
                session.setAttribute(Constants.SESSION_CUSTOMER, customer);
                return ControllerResult.getSuccessResult("更新用户审核密码成功");
            } else {
                return ControllerResult.getFailResult("原密码错误,或新密码与确认密码不一致");
            }
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "update_other_pwd", method = RequestMethod.POST)
    public ControllerResult updateOtherPwd(Customer customer, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            customer.setPassword(EncryptUtil.md5Encrypt(customer.getPassword()));
            customerService.updatePassword(customer);
            return ControllerResult.getSuccessResult("更新用户密码成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "update_other_chkpwd", method = RequestMethod.POST)
    public ControllerResult updateOtherCheckPwd(Customer customer, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            customer.setCheckPwd(EncryptUtil.md5Encrypt(customer.getCheckPwd()));
            customerService.updateCheckPwd(customer);
            return ControllerResult.getSuccessResult("更新用户审核密码成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            customerService.inactive(id);
            return ControllerResult.getSuccessResult("冻结客户账号成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isSuperAdmin(session) || SessionUtil.isAdmin(session)) {
            customerService.active(id);
            return ControllerResult.getSuccessResult("已解除客户账号冻结");
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
