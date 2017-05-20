package com.gs.common.web;

import com.gs.common.Constants;
import com.gs.net.server.ADSServer;

/**
 * Created by WangGenshen on 7/29/16.
 */
public class ADSServerUtil {

    public static ADSServer getADSServerFromServletContext() {
        return (ADSServer) ServletContextUtil.getServletContext().getAttribute(Constants.ADSSERVER);
    }

}
