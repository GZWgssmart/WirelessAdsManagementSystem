package com.gs.common.web;

import com.gs.common.Constants;
import com.gs.net.server.ADSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

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
        logger.info("应用启动....消息服务器ADSServer即将被启动，并且保存到应用上下文中");
        ServletContext servletContext = servletContextEvent.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        ADSServer adsServer = (ADSServer) webApplicationContext.getBean("adsServer");
        adsServer.startServer();
        servletContextEvent.getServletContext().setAttribute(Constants.ADSSERVER, adsServer);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("应用销毁....消息服务器ADSServer即将被关闭，并且从应用上下文中删除");
        ServletContext servletContext = servletContextEvent.getServletContext();
        ADSServer adsServer = (ADSServer) servletContext.getAttribute(Constants.ADSSERVER);
        adsServer.stopServer();
        servletContext.removeAttribute(Constants.ADSSERVER);
    }
}
