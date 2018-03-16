<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/18/16
  Time: 19:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>管理员账号设置-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/mobile.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.mobile.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/admin/setting.js"></script>


</head>
<body>

<div class="easyui-navpanel">
    <header>
        <div class="m-toolbar">
            <div class="m-title">
                <b>${sessionScope.admin.email }</b>
            </div>
            <div class="m-left">
                <a href="javascript:void(0)" class="easyui-linkbutton m-back" plain="true" outline="true" onclick="goBack();">返回</a>
            </div>
            <div class="m-right">
                <a href="javascript:void(0)" class="easyui-menubutton" data-options="iconCls:'icon-more',menu:'#mm',menuAlign:'right',hasDownArrow:false"></a>
            </div>
        </div>
        <div id="mm" class="easyui-menu" style="width:150px;">
            <div>
                <a href="javascript:void(0);" onclick="toPage('<%=path %>/admin/mob/home')">刷新主页</a>
            </div>
        </div>
    </header>
    <div id="errMsg"></div>
    <form:form id="updateForm" method="post" modelAttribute="admin" cssClass="mob-form">

            <span style="text-align: center; font-weight:bold; font-size: 14px;">修改账号密码</span>
            <div>
                <input type="password" name="password" label="原密码：" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,validType:'length[6,20]',novalidate:true" style="width:100%;"/>
            </div>
            <div><input type="password" name="newPwd" label="新密码：" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,validType:'length[6,20]',novalidate:true" style="width:100%;"/>
            </div>
            <div><input type="password" name="conPwd" label="确认密码：" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,validType:'length[6,20]',novalidate:true" style="width:100%;"/>
            </div>
            <div>
                    <a href="javascript:void(0);" class="easyui-linkbutton" onclick="updatePwd();">确认</a>
            </div>
    </form:form>

    <footer style="padding:2px 3px;text-align: center;">
        青岛宝瑞媒体发布系统V1.0<br />
        地址：山东省青岛市崂山区株洲路140号&nbsp;&nbsp;<br />技术支持:0532-80678775/80678776
    </footer>
</body>
</html>
