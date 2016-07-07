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
    <title>终端列表-青岛宝瑞无线广告管理系统</title>
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
            doSearch();
        });

        function add() {
            toValidate("addForm");
            if (validateForm("addForm")) {
                $('#addForm').ajaxSubmit({
                    url:'<%=path %>/device/add',
                    type:'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.result == "success") {
                            $("#addWin").window("close");
                            dataGridReload("list");
                            $("#addForm").form("clear");
                        } else {
                            $.messager.alert("提示", data.message, "info");
                        }
                    }
                });
            }
        }

        function showEdit() {
            var row = selectedRow("list");
            if (row) {
                $("#deviceGroupId").combobox({
                    url:'<%=path %>/device/list_combo/' + row.id,
                    method:'get',
                    valueField:'id',
                    textField:'text',
                    panelHeight:'auto'
                });
                $("#editForm").form("load", row);
                openWin("editWin");
            } else {
                $.messager.alert("提示", "请选择需要修改的终端设备", "info");
            }
        }

        function edit() {
            toValidate("editForm");
            if (validateForm("editForm")) {
                $('#editForm').ajaxSubmit({
                    url:'<%=path %>/device/update',
                    type:'post',
                    dataType: 'json',
                    success: function (data) {
                        if (data.result == "success") {
                            closeWin("editWin");
                            dataGridReload("list");
                            $("#editForm").form("clear");
                        } else {
                            $.messager.alert("提示", data.message, "info");
                        }
                    }
                });
            }
        }

        function inactive() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'N') {
                    $.messager.alert("提示", "终端设备不可用,无需冻结", "info");
                } else {
                    $.get("<%=path %>/device/inactive?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要冻结的终端设备", "info");
            }
        }

        function active() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'Y') {
                    $.messager.alert("提示", "终端设备可用,无需激活", "info");
                } else {
                    $.get("<%=path %>/device/active?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要激活的终端设备", "info");
            }
        }

        function doSearch() {
            $("#list").datagrid({
                url:'<%=path %>/device/search_pager',
                pageSize:20,
                queryParams:getQueryParams("list", "searchForm")
            });
            setPagination("#list");
        }

        function searchAll() {
            $("#searchForm").form("clear");
            $("#versionId").val("${versionId }");
            $("#list").datagrid({
                url:'<%=path %>/device/search_pager',
                pageSize:20,
                queryParams:getQueryParams("list", "searchForm")
            });
            setPagination("#list");
        }

        function refreshAll() {
            $("#list").datagrid("reload");
        }

        function formatterDevice(value) {
            return value.name;
        }

        function formatterVersion(value) {
            return value.name;
        }
    </script>
</head>
<body>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/device/search_pager',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:20,
				rowStyler: function(index,row){
				    if (row.status == 'N') {
					    return 'color:red;';
					} else if (row.online == 'N') {
					    return 'color:blue;';
					}
				}">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">用户ID</th>
        <th field="name" width="150">名称</th>
        <th field="version" width="80" formatter="formatterVersion">版本</th>
        <th field="deviceGroup" width="80" formatter="formatterDevice">终端分组</th>
        <th field="area" width="150">区域</th>
        <th field="busNo" width="80">车路线</th>
        <th field="busPlateNo" width="80">车牌号</th>
        <th field="des" width="200">描述</th>
        <th field="online" width="60" formatter="formatterOnline">在线状态</th>
        <th field="installTimeStr" width="150">安装时间</th>
        <th field="createTime" width="150" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="openWinFitPos('addWin');">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="showEdit();">修改</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactive()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="active()">激活</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="device">
            <input id="versionId" type="hidden" name="versionId" value="${versionId }" />
            名称:<input type="text" name="name" class="easyui-textbox"/>
            分组:<select name="deviceGroupId" class="easyui-combobox"
                       data-options="url:'<%=path %>/devgroup/list_combo',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
            是否在线:<select name="online" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'Y',
                        text: '在线'
                    },{
                        id: 'N',
                        text: '离线'
                    }]">
        </select>
            状态:<select name="status" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'Y',
                        text: '可用'
                    },{
                        id: 'N',
                        text: '不可用'
                    }]">
        </select>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="doSearch();">搜索</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="searchAll();">查询所有</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"
               onclick="refreshAll();">刷新</a>
        </form>
    </div>
</div>

<div class="easyui-window site_win_normal input_big" id="addWin" data-options="title:'添加设备',resizable:false,mode:true,closed:true">
    <form:form id="addForm" modelAttribute="device">
        <table>
            <tr>
                <td>名称:</td>
                <td><input type="text" name="name" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>分组:</td>
                <td>
                    <select name="deviceGroupId" class="easyui-validatebox easyui-combobox"
                           data-options="url:'<%=path %>/devgroup/list_combo',method:'get',valueField:'id',textField:'text',
                           panelHeight:'auto',editable:false,required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>区域:</td>
                <td><input type="text" name="area" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车路线:</td>
                <td><input type="text" name="busNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车牌号:</td>
                <td><input type="text" name="busPlateNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>安装时间:</td>
                <td><input type="text" name="installTime" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" onclick="add();">确认</button>
                </td>
            </tr>
        </table>
    </form:form>
</div>

<div class="easyui-window site_win_normal input_big" id="editWin" data-options="title:'修改设备',resizable:false,mode:true,closed:true">
    <div id="errMsg"></div>
    <form id="editForm" method="post" modelAttribute="device">
        <input type="hidden" name="id" />
        <table>
            <tr>
                <td>名称:</td>
                <td><input type="text" name="name" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>分组:</td>
                <td>
                    <select id="deviceGroupId" name="deviceGroupId" class="easyui-validatebox easyui-combobox"
                            data-options="editable:false,required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>区域:</td>
                <td><input type="text" name="area" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车路线:</td>
                <td><input type="text" name="busNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车牌号:</td>
                <td><input type="text" name="busPlateNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>安装时间:</td>
                <td><input type="text" name="installTimeStr" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td><button type="button" onclick="closeWin('editWin');">取消</button></td>
                <td>
                    <button type="button" onclick="edit();">确认</button>
                </td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>
