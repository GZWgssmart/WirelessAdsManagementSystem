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
    <title>管理员主页-青岛宝瑞媒体发布系统</title>
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
                <b>${sessionScope.admin.email }</b>
            </div>
            <div class="m-right">
                <a href="javascript:void(0)" class="easyui-menubutton" data-options="iconCls:'icon-more',menu:'#mm',menuAlign:'right',hasDownArrow:false"></a>
            </div>
        </div>
        <div id="mm" class="easyui-menu" style="width:150px;">
            <div>
            <a href="javascript:void(0);" onclick="toPage('<%=path %>/admin/mob/home')">刷新主页</a>
            </div>
            <div>
            <a href="<%=path %>/admin/mob/logout">安全退出</a>
            </div>
        </div>
    </header>

    <div class="easyui-accordion" data-options="border:false">

        <div title="用户管理">
            <ul class="m-list">
                <li><a href="<%=path %>/customer/mob/list_page">用户列表</a></li>
            </ul>
        </div>

        <div title="资源管理">
            <ul class="m-list">
                <li><a href="<%=path %>/customer/mob/list_page_admin/res">资源列表</a></li>
                <li><a href="<%=path %>/restype/mob/list_page">资源类型列表</a></li>
            </ul>
        </div>

        <div title="终端管理">
            <ul class="m-list">
                <li>
                    <a href="<%=path %>/customer/mob/list_page_admin/dev">终端列表</a>
                </li>
                <li>
                    <a href="<%=path %>/customer/mob/list_page_admin/devgroup">终端分组列表</a>
                </li>
            </ul>
        </div>
        <div title="发布计划">
            <ul class="m-list">
                <li><a href="<%=path %>/customer/mob/list_page_admin/pub">发布计划列表</a></li>
            </ul>
        </div>

        <div title="系统管理">
            <ul class="m-list">
                <c:if test="${sessionScope.admin.role == 'super' }">
                    <li><a href="<%=path %>/admin/mob/list_page">管理员列表</a></li>
                </c:if>
                <li><a href="<%=path %>/version/mob/list_page">版本列表</a></li>
                <li><a href="<%=path %>/admin/mob/query/${sessionScope.admin.id }">账号信息</a></li>
                <li><a href="<%=path %>/admin/mob/setting_page">账号设置</a></li>
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
