<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/16/16
  Time: 09:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%
    String path = request.getContextPath();
%>
<!doctype html>
<html>
<head>
    <title>管理员登录-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8" />
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/mobile.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>
    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.mobile.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>

    <script src="<%=path %>/js/admin/login.js"></script>
</head>
<body onload="redirectIndexMob('${redirect}');">
<div class="easyui-navpanel">
    <header>
        <div class="m-toolbar">
            <div class="m-title">登录</div>

        </div>
    </header>
    <div class="easyui-panel" title="青岛宝瑞媒体发布系统">
        <div id="errMsg"></div>
        <form:form id="login_form" method="post" cssClass="mob-form">
            <div>
                <input type="text" label="邮箱：" name="email" class="easyui-textbox" style="width:100%;"/>
            </div>
            <div>
                <input type="password" label="密码：" name="password" class="easyui-textbox" style="width:100%;"/>
            </div>
            <div>
                <input type="text" label="验证码：" name="checkCode" class="easyui-textbox" style="width:100%;"/>
            </div>
            <div style="margin-left: 25%;">
                <img src="<%=path %>/captcha" onclick="this.src='<%=path %>/captcha?time=Math.random();'"/>
            </div>
            <div style="width:80px; margin-right:10px;float:right;">
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="loginMob();" style="width:80px;height:35px;">登录</a>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>
