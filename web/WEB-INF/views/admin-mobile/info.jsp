<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/16/16
  Time: 09:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>
    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.mobile.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=path %>/js/site_easyui.js"></script>
    <script src="<%=path %>/js/admin/info.js"></script>
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

    <table id="info" class="site_table site_info_table">
        <caption>我的基本信息</caption>
        <thead>
        <tr>
            <th>名称</th>
            <th class="site_value_td">值</th>
        </tr>
        <tr>
            <td>邮箱</td>
            <td>${requestScope.admin.email }</td>
        </tr>
        <tr>
            <td>姓名</td>
            <td>${requestScope.admin.name }</td>
        </tr>
        <tr>
            <td>手机</td>
            <td>${requestScope.admin.phone }</td>
        </tr>
        <tr>
            <td>角色</td>
            <td>
                <c:choose>
                    <c:when test="${requestScope.admin.role == 'super' }">
                        超级管理员
                    </c:when>
                    <c:otherwise>
                        普通管理员
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>状态</td>
            <td>
                <c:choose>
                    <c:when test="${requestScope.admin.status == 'Y' }">
                        可用
                    </c:when>
                    <c:otherwise>
                        不可用
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>创建时间</td>
            <td><fmt:formatDate value="${requestScope.admin.createTime }" pattern="yyyy/MM/dd HH:mm:ss" /></td>
        </tr>
        <tr>
            <td>上一次登录时间</td>
            <td><fmt:formatDate value="${requestScope.admin.lastLoginTime }" pattern="yyyy/MM/dd HH:mm:ss" /></td>
        </tr>
        <tr>
            <td>登录时间</td>
            <td><fmt:formatDate value="${requestScope.admin.loginTime }" pattern="yyyy/MM/dd HH:mm:ss" /></td>
        </tr>
        </thead>
    </table>
    <div>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
           onclick="openWin('editWin');">修改</a>
    </div>

    <div class="easyui-dialog" style="width:100%;" id="editWin" data-options="title:'修改账号信息',resizable:false,mode:true,closed:true">
        <div id="errMsg"></div>
        <form:form id="editForm" method="post" modelAttribute="admin" cssClass="mob-form">
            <form:hidden path="id" />
                <div>
                    <form:input type="text" path="email" label="邮箱：" class="easyui-textbox" readonly="true" style="width:100%;"/>
                </div>
                </tr>
                <div><form:input type="text" label="姓名：" path="name" class="easyui-validatebox easyui-textbox"
                                    data-options="required:true,novalidate:true" style="width:100%;"/>
                </div>
                </tr>
                <div><form:input type="text" label="手机号：" path="phone" class="easyui-validatebox easyui-textbox"
                                    data-options="required:true,validType:'length[11,13]',novalidate:true" style="width:100%;"/>
                </div>
                <div>
                    <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('editWin');">取消</a>
                    &nbsp;&nbsp;
                    <a href="javascript:void(0);" class="easyui-linkbutton" onclick="editMob('${requestScope.admin.id }');">确认</a>
                </div>
        </form:form>
    </div>

    <footer style="padding:2px 3px;text-align: center;">
        青岛宝瑞媒体发布系统V1.0<br />
        地址：山东省青岛市崂山区株洲路140号&nbsp;&nbsp;<br />技术支持:0532-80678775/80678776
    </footer>

</div>
</body>
</html>
