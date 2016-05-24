<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/18/16
  Time: 19:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>客户列表-青岛宝瑞无线广告管理系统</title>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script>
        $(function() {
            setPagination("#list")
        });

        function addCustomer() {
            $.post("<%=path %>/customer/add",
                    $("#addForm").serialize(),
                    function (data) {
                        if (data.result == "success") {
                            $("#addCustomer").window("close");
                            dataGridReload("list");
                            $("#addForm").form("clear");
                        } else {
                            $.messager.alert("提示", data.message, "info");
                        }
                    }
            );
        }
    </script>
</head>
<body style="margin:0;padding:0;">
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/customer/list_pager',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:20">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">用户ID</th>
        <th field="email" width="150">邮箱</th>
        <th field="name" width="100">姓名</th>
        <th field="phone" width="80">手机号</th>
        <th field="address" width="200">地址</th>
        <th field="createTime" width="150" formatter="formatterDate">创建时间</th>
        <th field="loginTime" width="150" formatter="formatterDate">最近登录时间</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="openWin('addCustomer');">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="openWin('addAdmin');">修改</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactiveAdmin()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="activeAdmin()">激活</a>
</div>

<div class="easyui-window site_win_small input_big" id="addCustomer" data-options="title:'添加客户',resizable:false,mode:true,closed:true">
    <form:form id="addForm" modelAttribute="customer">
        <table>
            <tr>
                <td>邮箱:</td>
                <td><input type="text" name="email" class="easyui-textbox"/></td>
            </tr>
            <tr>
                <td>密码:</td>
                <td><input type="password" name="password" class="easyui-textbox"/></td>
            </tr>
            <tr>
                <td>姓名:</td>
                <td><input type="text" name="name" class="easyui-textbox"/></td>
            </tr>
            <tr>
                <td>手机:</td>
                <td><input type="text" name="phone" class="easyui-textbox"/></td>
            </tr>
            <tr>
                <td>地址:</td>
                <td><input type="text" name="address" class="easyui-textbox"/></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" onclick="addCustomer();">确认</button>
                </td>
            </tr>
        </table>
    </form:form>
</div>

</body>
</html>
