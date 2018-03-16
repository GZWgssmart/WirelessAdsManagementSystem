<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/17/16
  Time: 16:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>客户主页-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8" />
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/mobile.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>
    <script type="text/javascript" src="<%=path %>/js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.mobile.js"></script>
    <script type="text/javascript" src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=path %>/js/site_easyui.js"></script>
</head>
<body>

<div class="easyui-navpanel">
    <header>
        <div class="m-toolbar">
            <div class="m-title">
                <b>${sessionScope.customer.email }</b>
            </div>
            <div class="m-right">
                <a href="javascript:void(0)" class="easyui-menubutton" data-options="iconCls:'icon-more',menu:'#mm',menuAlign:'right',hasDownArrow:false"></a>
            </div>
        </div>
        <div id="mm" class="easyui-menu" style="width:150px;">
            <div>
                <a href="javascript:void(0);" onclick="toPage('<%=path %>/customer/mob/home')">刷新主页</a>
            </div>
            <div>
                <a href="<%=path %>/customer/mob/logout">安全退出</a>
            </div>
        </div>
    </header>
    <div class="easyui-accordion" data-options="border:false">
        <div title="用户管理">
            <ul class="m-list">
                <li><a href="<%=path %>/customer/mob/query/${sessionScope.customer.id }">账号信息</a></li>
                <li><a href="<%=path %>/customer/mob/setting_page">账号设置</a></li>
                <li><a href="<%=path %>/customer/mob/check_pwd_page">审核密码修改</a></li>
            </ul>
        </div>
        <div title="资源管理">
            <ul class="m-list">
                <li><a href="<%=path %>/res/mob/list_page">资源列表</a></li>
            </ul>
        </div>
        <div title="终端管理">
            <ul class="m-list">
                <li><a href="<%=path %>/device/mob/list_page">终端列表</a></li>
                <li><a href="<%=path %>/devgroup/mob/list_page">终端分组列表</a></li>
            </ul>
        </div>
        <div title="发布计划">
            <ul class="m-list">
                <li><a href="<%=path %>/pubplan/mob/list_page">发布计划列表</a></li>
                <li><a href="<%=path %>/pubplan/mob/list_page_checking">发布计划审核</a></li>
                <li><a href="<%=path %>/pubplan/mob/list_page_checked">已审核未完成发布计划列表</a></li>
                <li><a href="<%=path %>/pubplan/mob/list_page_finish">已完成发布计划列表</a></li>
            </ul>
        </div>
    </div>

    <footer style="padding:2px 3px;text-align: center;">
        青岛宝瑞媒体发布系统V1.0<br />
        地址：山东省青岛市崂山区株洲路140号&nbsp;&nbsp;<br />技术支持:0532-80678775/80678776
    </footer>
</div>
</body>
</html>
