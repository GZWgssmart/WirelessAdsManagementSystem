package com.gs.common.web;

import com.gs.common.Constants;

import javax.servlet.http.HttpSession;

/**
 * Created by WangGenshen on 5/19/16.
 */
public class SessionUtil {

    public static boolean isAdminLogin(HttpSession session) {
        return session.getAttribute(Constants.SESSION_ADMIN) != null;
    }

    public static boolean isCustomerLogin(HttpSession session) {
        return session.getAttribute(Constants.SESSION_CUSTOMER) != null;
    }
}
