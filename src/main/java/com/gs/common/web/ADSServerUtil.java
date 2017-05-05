package com.gs.common.web;

import com.gs.common.Constants;
import com.gs.net.server.ADSServer;
import com.gs.net.server.ADSServerV2;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;

/**
 * Created by WangGenshen on 7/29/16.
 */
public class ADSServerUtil {

    public static ADSServerV2 getADSServerFromServletContext() {
        return (ADSServerV2) ServletContextUtil.getServletContext().getAttribute(Constants.ADSSERVER);
    }

}
