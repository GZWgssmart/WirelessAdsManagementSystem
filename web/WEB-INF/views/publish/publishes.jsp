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
    <title>消息发布列表-青岛宝瑞无线广告管理系统</title>
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
            setPagination("#list");
        });

        function showAdd() {
            $("#addForm").form("clear");
            openWinFitPos('addWin');
        }

        function add() {
            toValidate("addForm");
            if (validateForm("addForm")) {
                $('#addForm').ajaxSubmit({
                    url:'<%=path %>/devres/add',
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
                    url:'<%=path %>/devres/list_combo/' + row.id,
                    method:'get',
                    valueField:'id',
                    textField:'text',
                    panelHeight:'auto'
                });
                $("#editForm").form("load", row);
                $("#editDeviceName").textbox("setValue", row.device.name);
                $("#editResourceName").textbox("setValue", row.resource.name);
                openWin("editWin");
            } else {
                $.messager.alert("提示", "请选择需要修改的消息发布", "info");
            }
        }

        function edit() {
            toValidate("editForm");
            if (validateForm("editForm")) {
                $('#editForm').ajaxSubmit({
                    url:'<%=path %>/devres/update',
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

        function toCheck() {
            var row = selectedRow("list");
            if (row) {
                if (row.checkStatus != "未提交") {
                    $.messager.alert("提示", "请选择未提交审核的消息发布", "info");
                } else {
                    $.get("<%=path %>/devres/check?id=" + row.id + "&checkStatus=审核中",
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要提交审核的消息发布", "info");
            }
        }

        function inactive() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'N') {
                    $.messager.alert("提示", "消息发布不可用,无需冻结", "info");
                } else {
                    $.get("<%=path %>/devres/inactive?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要冻结的消息发布", "info");
            }
        }

        function active() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'Y') {
                    $.messager.alert("提示", "消息发布可用,无需激活", "info");
                } else {
                    $.get("<%=path %>/devres/active?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要激活的消息发布", "info");
            }
        }

        function doSearch() {
            $("#list").datagrid({
                url:'<%=path %>/devres/search_pager',
                pageSize:20,
                queryParams:getQueryParams("list", "searchForm")
            });
            setPagination("#list");
        }

        function searchAll() {
            $("#searchForm").form("clear");
            $("#list").datagrid({
                url:'<%=path %>/devres/search_pager',
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

        function showDevWin() {
            openWinFitPos("devWin");
            $("#devWin").window("refresh", "<%=path %>/device/list_page_choose");
        }

        function doSearchDev() {
            $("#devList").datagrid({
                url:'<%=path %>/device/search_pager',
                pageSize:20,
                queryParams:getQueryParams("devList", "devSearchForm")
            });
            setPagination("#devList");
        }

        function searchAllDev() {
            $("#devSearchForm").form("clear");
            $("#devList").datagrid({
                url:'<%=path %>/device/search_pager',
                pageSize:20,
                queryParams:getQueryParams("devList", "devSearchForm")
            });
            setPagination("#devList");
        }

        function refreshAllDev() {
            $("#devList").datagrid("reload");
        }

        function chooseDev() {
            var row = selectedRow("devList");
            if (row) {
                $("#addDeviceId").val(row.id);
                $("#editDeviceId").val(row.id);
                $("#addDeviceName").textbox("setValue", row.name);
                $("#editDeviceName").textbox("setValue", row.name);
                closeWin("devWin");
            } else {
                $.messager.alert("提示", "请选择设备", "info");
            }
        }

        function showResWin() {
            openWinFitPos("resWin");
            $("#resWin").window("refresh", "<%=path %>/res/list_page_choose");
        }

        function doSearchRes() {
            $("#resList").datagrid({
                url:'<%=path %>/res/search_pager',
                pageSize:20,
                queryParams:getQueryParams("resList", "resSearchForm")
            });
            setPagination("#list");
        }

        function searchAllRes() {
            $("#resSearchForm").form("clear");
            $("#resList").datagrid({
                url:'<%=path %>/res/search_pager',
                pageSize:20,
                queryParams:getQueryParams("resList", "resSearchForm")
            });
            setPagination("#list");
        }

        function chooseRes() {
            var row = selectedRow("resList");
            if (row) {
                $("#addResourceId").val(row.id);
                $("#editResourceId").val(row.id);
                $("#addResourceName").textbox("setValue", row.name);
                $("#editResourceName").textbox("setValue", row.name);
                closeWin("resWin");
            } else {
                $.messager.alert("提示", "请选择设备", "info");
            }
        }

        function formatterType(value) {
            return value.name;
        }

        function formatterName(value) {
            return value.name;
        }

    </script>
</head>
<body>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/devres/search_pager',
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
					} else if (row.checkStatus == '审核中') {
					    return 'color:orange';
                    } else if (row.checkStatus == '已审核') {
                        return 'color:green;';
                    }
				}">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">用户ID</th>
        <th field="device" width="150" formatter="formatterName">终端名称</th>
        <th field="resource" width="150" formatter="formatterName">资源名称</th>
        <th field="showType" width="80">显示方式</th>
        <th field="startTimeStr" width="150" formatter="formatterDate">开始时间</th>
        <th field="endTimeStr" width="150" formatter="formatterDate">结束时间</th>
        <th field="stayTime" width="60">停留时间（S）</th>
        <th field="des" width="200">描述</th>
        <th field="checkStatus" width="60">审核状态</th>
        <th field="createTime" width="150" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="showAdd();">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="showEdit();">修改</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-tip" plain="true"
       onclick="toCheck();">提交审核</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactive()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="active()">激活</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="deviceResource">
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

<div class="easyui-window site_win_big input_big" id="addWin" data-options="title:'添加消息发布',resizable:false,mode:true,closed:true">
    <form:form id="addForm" modelAttribute="deviceResource">
        <table>
            <tr>
                <td>设备:</td>
                <td><input id="addDeviceId" type="hidden" name="deviceId" />
                    <input id="addDeviceName" type="text" name="deviceName" class="easyui-validatebox easyui-textbox"
                           data-options="editable:false,required:true,novalidate:true"/>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                       onclick="showDevWin();">选择设备</a></td>
            </tr>
            <tr>
                <td>资源:</td>
                <td><input id="addResourceId" type="hidden" name="resourceId" />
                    <input id="addResourceName" type="text" name="resourceName" class="easyui-validatebox easyui-textbox"
                    data-options="editable:false,required:true,novalidate:true"/>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                       onclick="showResWin();">选择资源</a></td>
            </tr>
            <tr>
                <td>显示方式:</td>
                <td><select name="showType" class="easyui-combobox" data-options="editable:false, valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'Y',
                        text: '可用'
                    },{
                        id: 'N',
                        text: '不可用'
                    }]"></select></td>
            </tr>
            <tr>
                <td>开始时间:</td>
                <td><input type="text" name="startTime" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>结束时间:</td>
                <td><input type="text" name="endTime" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>停留时间(S):</td>
                <td><input type="text" name="stayTime" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
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

<div class="easyui-window site_win_big input_big" id="editWin" data-options="title:'修改消息发布',resizable:false,mode:true,closed:true">
    <div id="errMsg"></div>
    <form id="editForm" method="post" modelAttribute="deviceResource">
        <input type="hidden" name="id" />
        <table>
            <tr>
                <td>设备:</td>
                <td><input id="editDeviceId" type="hidden" name="deviceId" />
                    <input id="editDeviceName" type="text" name="deviceName" class="easyui-validatebox easyui-textbox"
                           data-options="editable:false,required:true,novalidate:true"/>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                       onclick="showDevWin();">选择设备</a></td>
            </tr>
            <tr>
                <td>资源:</td>
                <td><input id="editResourceId" type="hidden" name="resourceId" />
                    <input id="editResourceName" type="text" name="resourceName" class="easyui-validatebox easyui-textbox"
                           data-options="editable:false,required:true,novalidate:true"/>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                       onclick="showResWin();">选择资源</a></td>
            </tr>
            <tr>
                <td>显示方式:</td>
                <td><select name="showType" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'Y',
                        text: '可用'
                    },{
                        id: 'N',
                        text: '不可用'
                    }]"></select></td>
            </tr>
            <tr>
                <td>开始时间:</td>
                <td><input type="text" name="startTimeStr" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>结束时间:</td>
                <td><input type="text" name="endTimeStr" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>停留时间(S):</td>
                <td><input type="text" name="stayTime" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
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

<div class="easyui-window site_win_big_wider input_big" id="devWin" style="padding:0;" data-options="title:'选择设备',resizable:false,mode:true,closed:true"></div>
<div class="easyui-window site_win_big_wider input_big" id="resWin" style="padding:0;" data-options="title:'选择资源',resizable:false,mode:true,closed:true"></div>

</body>
</html>
