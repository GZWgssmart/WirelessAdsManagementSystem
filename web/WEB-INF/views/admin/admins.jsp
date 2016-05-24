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

        function addAdmin() {
            $.post("<%=path %>/admin/add",
                    $("#addForm").serialize(),
                    function (data) {
                        if (data.result == "success") {
                            $("#addAdmin").window("close");
                            dataGridReload("list");
                            $("#addForm").form("clear");
                        } else {
                            $.messager.alert("提示", data.message, "info");
                        }
                    }
            );
        }

        function inactiveAdmin() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'N') {
                    $.messager.alert("提示", "管理员不可用,无需冻结", "info");
                } else {
                    $.get("<%=path %>/admin/inactive?id=" + row.id,
                        function (data) {
                            if (data.result == "success") {
                                $.messager.alert("提示", data.message, "info");
                                dataGridReload("list");
                            }
                        });
                }
            } else {
                $.messager.alert("提示", "请选择需要冻结的管理员", "info");
            }
        }

        function activeAdmin() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'Y') {
                    $.messager.alert("提示", "管理员可用,无需激活", "info");
                } else {
                    $.get("<%=path %>/admin/active?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要激活的管理员", "info");
            }
        }
    </script>
</head>
<body>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/admin/list_pager',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:20,
				rowStyler: function(index,row){
					if (row.role == 'super'){
						return 'background-color:#ccc;';
					} else if (row.status == 'N') {
					    return 'color:red;';
					}
				}">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">管理员ID</th>
        <th field="email" width="150">邮箱</th>
        <th field="name" width="100">姓名</th>
        <th field="phone" width="80">手机号</th>
        <th field="createTime" width="150" formatter="formatterDate">创建时间</th>
        <th field="lastLoginTime" width="150" formatter="formatterDate">上一次登录时间</th>
        <th field="role" width="80" formatter="formatterRole">角色</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="openWin('addAdmin');">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactiveAdmin()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="activeAdmin()">激活</a>
</div>

<div class="easyui-window site_win_small input_big" id="addAdmin" data-options="title:'添加管理员',resizable:false,mode:true,closed:true">
    <form:form id="addForm" modelAttribute="admin">
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
                <td>角色:</td>
                <td>
                    <select id="role" class="easyui-combobox" name="role">
                        <option value="super">超级管理员</option>
                        <option value="normal" selected>普通管理员</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" onclick="addAdmin();">确认</button>
                </td>
            </tr>
        </table>
    </form:form>
</div>

</body>
</html>
