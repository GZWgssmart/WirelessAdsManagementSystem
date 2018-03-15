package com.gs.controller;

import com.gs.bean.Customer;
import com.gs.bean.DeviceGroup;
import com.gs.bean.ResourceType;
import com.gs.bean.Version;
import com.gs.common.Constants;
import com.gs.common.bean.ComboBox4EasyUI;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.FileUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.util.UUIDUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.ResourceService;
import com.gs.service.ResourceTypeService;
import com.gs.service.VersionService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by WangGenshen on 6/30/16.
 */
@Controller
@RequestMapping("/version")
public class VersionController {

    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

    private static final String VERSION_DIR = "system_versions";
    private static final String VERSION_IMG_TYPE = "jpg,bmp,png,jpeg";

    @Resource
    private VersionService versionService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(Version version, MultipartFile file, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            if (file != null) {
                String ofileName = file.getOriginalFilename();
                String fileName = UUIDUtil.uuid() + FileUtil.getExtension(ofileName);
                if (FileUtil.checkExtension(fileName, VERSION_IMG_TYPE)) {
                    File targetFile = new File(FileUtil.uploadPath(session, VERSION_DIR), fileName);
                    try {
                        file.transferTo(targetFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    version.setOfileName(ofileName);
                    version.setFileName(fileName);
                    version.setPath(FileUtil.uploadFilePath(targetFile));
                    version.setFullPath(targetFile.getAbsolutePath());
                } else {
                    return ControllerResult.getFailResult("请选择图片,图片类型为jpg或bmp或png");
                }
            } else {
                return ControllerResult.getFailResult("请选择图片,图片类型为jpg或bmp或png");
            }
            versionService.insert(version);
            return ControllerResult.getSuccessResult("成功添加版本信息");
        }
        return null;
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "version/versions";
        } else {
            return "redirect:/admin/redirect_login_page";
        }
    }

    @RequestMapping(value = "mob/list_page", method = RequestMethod.GET)
    public String toListPageMob(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "version-mobile/versions";
        } else {
            return "redirect:/admin/mob/redirect_login_page";
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Version> searchPager(@Param("page")String page, @Param("rows")String rows, Version version, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show versions by pager");
            int total = versionService.countByCriteria(version);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Version> versions = versionService.queryByPagerAndCriteria(pager, version);
            return new Pager4EasyUI<Version>(pager.getTotalRecords(), versions);
        } else {
            logger.info("can not show version by pager cause admin is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "querybyid/{versionId}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String queryById(@PathVariable("versionId") String versionId, HttpSession session) {
        if (SessionUtil.isCustomer(session)) {
            logger.info("query version by id: " + versionId);
            Version version = versionService.queryById(versionId);
            return version.getPath();
        } else {
            logger.info("can not query version by id cause admin is not login");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "list_combo/{id}/{status}", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> list4Combobox(@PathVariable("id") String id, @PathVariable("status") String status, HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            String theStatus = null;
            if (status.equals("Y")) {
                theStatus = "Y";
            }
            List<Version> versions = versionService.queryAll(theStatus);
            for (Version version : versions) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(version.getId());
                comboBox4EasyUI.setText(version.getName());
                if (version.getId().equals(id)) {
                    comboBox4EasyUI.setSelected(true);
                }
                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
        }
        return comboBox4EasyUIs;
    }

    @ResponseBody
    @RequestMapping(value = "list_combo_area/{id}/{area}", method = RequestMethod.GET)
    public List<ComboBox4EasyUI> listArea4Combobox(@PathVariable("id") String id, @PathVariable("area") int area, HttpSession session) {
        List<ComboBox4EasyUI> comboBox4EasyUIs = null;
        if (SessionUtil.isCustomer(session) || SessionUtil.isAdmin(session)) {
            comboBox4EasyUIs = new ArrayList<ComboBox4EasyUI>();
            Version version = versionService.queryById(id);
            int count = version.getAreaCount();
            for (int i = 1; i <= count; i++) {
                ComboBox4EasyUI comboBox4EasyUI = new ComboBox4EasyUI();
                comboBox4EasyUI.setId(String.valueOf(i));
                comboBox4EasyUI.setText("区域" + i);
                if (i == area) {
                    comboBox4EasyUI.setSelected(true);
                }
                comboBox4EasyUIs.add(comboBox4EasyUI);
            }
        }
        return comboBox4EasyUIs;
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(Version version, MultipartFile file, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("update version info by admin");
            Customer customer = (Customer) session.getAttribute(Constants.SESSION_CUSTOMER);
            if (file != null) {
                String ofileName = file.getOriginalFilename();
                String fileName = UUIDUtil.uuid() + FileUtil.getExtension(ofileName);
                File targetFile = new File(FileUtil.uploadPath(session, VERSION_DIR), fileName);
                try {
                    file.transferTo(targetFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                version.setOfileName(ofileName);
                version.setFileName(fileName);
                version.setPath(FileUtil.uploadFilePath(targetFile));
                version.setFullPath(targetFile.getAbsolutePath());
            }
            versionService.update(version);
            return ControllerResult.getSuccessResult("成功更新版本信息");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "inactive", method = RequestMethod.GET)
    public ControllerResult inactive(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            versionService.inactive(id);
            return ControllerResult.getSuccessResult("冻结版本信息成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @ResponseBody
    @RequestMapping(value = "active", method = RequestMethod.GET)
    public ControllerResult active(@Param("id")String id, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            versionService.active(id);
            return ControllerResult.getSuccessResult("已解除版本信息冻结");
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
