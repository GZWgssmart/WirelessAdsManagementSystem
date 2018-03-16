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
    <title>管理员信息-青岛宝瑞媒体发布系统</title>
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

    <script src="<%=path %>/js/customer/info.js"></script>


</head>
<body>
<div class="easyui-navpanel">
    <header>
        <div class="m-toolbar">
            <div class="m-title">
                <b>${sessionScope.customer.email }</b>
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
                <a href="javascript:void(0);" onclick="toPage('<%=path %>/mob/home')">刷新主页</a>
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
            <td>${requestScope.customer.email }</td>
        </tr>
        <tr>
            <td>姓名</td>
            <td>${requestScope.customer.name }</td>
        </tr>
        <tr>
            <td>手机</td>
            <td>${requestScope.customer.phone }</td>
        </tr><tr>
            <td>公司</td>
            <td>${requestScope.customer.company }</td>
        </tr>

        <tr>
            <td>地址</td>
            <td>${requestScope.customer.address }</td>
        </tr>
        <tr>
            <td>状态</td>
            <td>
                <c:choose>
                    <c:when test="${requestScope.customer.status == 'Y' }">
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
            <td><fmt:formatDate value="${requestScope.customer.createTime }" pattern="yyyy/MM/dd HH:mm:ss" /></td>
        </tr>
        <tr>
            <td>上一次登录时间</td>
            <td><fmt:formatDate value="${requestScope.customer.lastLoginTime }" pattern="yyyy/MM/dd HH:mm:ss" /></td>
        </tr>
        <tr>
            <td>登录时间</td>
            <td><fmt:formatDate value="${requestScope.customer.loginTime }" pattern="yyyy/MM/dd HH:mm:ss" /></td>
        </tr>
        <tr>
            <td>上一次修改时间</td>
            <td><fmt:formatDate value="${requestScope.customer.lastUpdateTime }" pattern="yyyy/MM/dd HH:mm:ss" /></td>
        </tr>
        </thead>
    </table>
    <div>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
           onclick="openWin('editWin');">修改</a>
    </div>

    <div class="easyui-dialog" id="editWin" data-options="title:'修改账号信息',resizable:false,mode:true,closed:true">
        <div id="errMsg"></div>
        <form:form id="editForm" method="post" modelAttribute="customer" cssClass="mob-form">
            <form:hidden path="id" />
            <div><form:input type="text" path="email" label="邮箱：" class="easyui-textbox" readonly="true" style="width:100%;"/>
            </div>
                <div><form:input type="text" path="name" label="姓名：" class="easyui-validatebox easyui-textbox"
                                    data-options="required:true,novalidate:true" style="width:100%;"/>
                </div>
                <div><form:input type="text" path="phone" label="手机：" class="easyui-validatebox easyui-textbox"
                                    data-options="required:true,validType:'length[11,13]',novalidate:true" style="width:100%;"/>
                </div>
                <div><form:input type="text" path="company" label="公司：" class="easyui-textbox" style="width:100%;"/>
                </div>
                <div><form:input type="text" path="address" label="地址：" class="easyui-textbox" style="width:100%;"/>
                </div>
                    <div>
                        <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('editWin');">取消</a>
                        &nbsp;&nbsp;
                        <a href="javascript:void(0);" class="easyui-linkbutton" onclick="editMob('${requestScope.customer.id }');">确认</a>
                        &nbsp;&nbsp;
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
