package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.common.Constants;
import com.gs.common.bean.Pager;
import com.gs.common.util.EncryptUtil;
import com.gs.common.util.PagerUtil;
import com.gs.service.CustomerService;
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
import java.util.ArrayList;
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

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(Customer customer, HttpSession session) {
        if (session.getAttribute(Constants.SESSION_CUSTOMER) != null) {
            return "redirect:home";
        }
        customer.setPassword(EncryptUtil.md5Encrypt(customer.getPassword()));
        Customer c = customerService.query(customer);
        if (c != null) {
            session.setAttribute(Constants.SESSION_CUSTOMER, customer);
            return "redirect:home";
        } else {
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.SESSION_CUSTOMER);
        return "redirect:/index";
    }

    @RequestMapping(value = "reg_page", method = RequestMethod.GET)
    public String toReg(Model model) {
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
        if (session.getAttribute(Constants.SESSION_CUSTOMER) == null) {
            return "redirect:/index";
        }
        return "customer/home";
    }

    @ResponseBody
    @RequestMapping("list")
    public List<Customer> list() {
        logger.info("显示所有客户信息");
        return customerService.queryAll();
    }

    @RequestMapping("index/{id}")
    public ModelAndView queryById(@PathVariable("id") String id) {
        ModelAndView mav = new ModelAndView("index");
        Customer customer = customerService.queryById(id);
        mav.addObject("customer", customer);
        return mav;
    }

    @RequestMapping("index")
    public String query(Model model) {
        List<Customer> customers = customerService.queryAll();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
        model.addAttribute("customers", customers);
        return "index";
    }

    @RequestMapping("batchAdd")
    public String batchInsert() {
        List<Customer> customers = new ArrayList<Customer>();
        Customer customer = new Customer();
        customer.setName("123");
        customer.setPassword("123");
        customers.add(customer);
        Customer customer1 = new Customer();
        customer1.setName("234");
        customer1.setPassword("234");
        customers.add(customer1);
        int result = customerService.batchInsert(customers);
        for(int i = 0; i < result; i++) {
            System.out.println("Count: " + result + ", id: " + customers.get(i).getId());
        }
        return "redirect:/index";
    }

    @RequestMapping("page/{pageNo}")
    public String queryByPager(@PathVariable("pageNo") int pageNo) {
        int count = customerService.count();
        Pager pager = PagerUtil.getPager(pageNo);
        PagerUtil.rebuildPager(pager, count);
        System.out.println("Total records: " + pager.getTotalRecords() + ", total pages: " + pager.getTotalPages());
        List<Customer> customers = customerService.queryByPager(pager);
        for(Customer customer : customers) {
            System.out.println(customer);
        }
        return "redirect:/index";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
