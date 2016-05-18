<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/18/16
  Time: 19:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

        function toAddUser() {
            $("#addUser").window("open");
        }

        function addUser() {
            $.post("<%=path %>/userAction/add",
                    $("#addUserForm").serialize(),
                    function (data) {
                        if (data.result == "ok") {
                            $("#addUser").window("close");
                            $("#list").datagrid("reload");
                        } else {
                            $.messager.alert("提示", data.errMsg, "info");
                        }
                    }
            );
        }

        function formatterDefaultRole(value, rec) {
            return rec.defaultRole.name;
        }

        $(function() {
            $('#roles').combobox({
                url:'<%=path %>/roleAction/all',
                method:'get',
                valueField:'id',
                textField:'text'
            });
            $('#defaultRole').combobox({
                url:'<%=path %>/roleAction/all',
                method:'get',
                valueField:'id',
                textField:'text'
            });
        });

    </script>
</head>
<body style="margin:0;padding:0;">
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/customer/list',
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
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="toAddUser();">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-cut" plain="true"
       onclick="javascript:alert('Cut')">Cut</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-save" plain="true"
       onclick="javascript:alert('Save')">Save</a>
</div>

<div class="easyui-window" title="添加用户" id="addUser" resizable="false" style="width:400px; height:250px;" mode="true"
     closed="true">
    <form id="addUserForm">
        <table>
            <tr>
                <td>用户名称:</td>
                <td><input type="text" name="name" class="easyui-textbox"/></td>
            </tr>
            <tr>
                <td>用户密码:</td>
                <td><input type="password" name="password" class="easyui-textbox"/></td>
            </tr>
            <tr>
                <td>用户角色:</td>
                <td>
                    <select id="roles" class="easyui-combobox" name="roles" multiple style="width:200px;">
                        <option value="请选择">请选择</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>默认角色:</td>
                <td>
                    <select id="defaultRole" class="easyui-combobox" name="defaultRole" style="width:200px;">
                        <option value="请选择" selected>请选择</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" onclick="addUser();">确认</button>
                </td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>
