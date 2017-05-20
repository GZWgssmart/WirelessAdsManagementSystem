package com.gs.common.web;

import com.gs.common.Constants;
import com.gs.net.server.ADSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by WangGenshen on 7/29/16.
 */
public class ADSServerListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ADSServerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("Application initialized....ADSServer will be started，and saved to application context");
        ServletContext servletContext = servletContextEvent.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        ADSServer adsServer = (ADSServer) webApplicationContext.getBean("adsServer");
        adsServer.startServer();
        servletContextEvent.getServletContext().setAttribute(Constants.ADSSERVER, adsServer);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("Application destroyed....ADSServer will be shutdown，and delete from application context");
        ServletContext servletContext = servletContextEvent.getServletContext();
        ADSServer adsServer = (ADSServer) servletContext.getAttribute(Constants.ADSSERVER);
        adsServer.stopServer();
        servletContext.removeAttribute(Constants.ADSSERVER);
    }
}
