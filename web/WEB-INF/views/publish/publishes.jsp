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
            $("#stayTime").textbox({"required":false,"novalidate":true});
            $("#stayTimeTR").attr("style", "display:none");
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
                $("#stayTime1").textbox({"required":false,"novalidate":true});
                $("#stayTimeTR1").attr("style", "display:none");
                if (row.stayTime != '') {
                    $("#stayTime1").textbox({"required":true,"novalidate":true});
                    $("#stayTimeTR1").attr("style", "");
                } else {
                    $("#stayTime1").textbox("setValue", "");
                }
                $("#editDeviceCode").textbox("setValue", row.device.code);
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
                if (row.checkStatus != "not_submit") {
                    $.messager.alert("提示", "请选择未提交审核的消息发布", "info");
                } else {
                    $.get("<%=path %>/devres/check?id=" + row.id + "&checkStatus=checking",
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                } else {
                                    $.messager.alert("提示", data.message, "info");
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
                if (row.status == 'Y') {
                    $("#addDeviceId").val(row.id);
                    $("#editDeviceId").val(row.id);
                    $("#addDeviceCode").textbox("setValue", row.code);
                    $("#editDeviceCode").textbox("setValue", row.code);
                    closeWin("devWin");
                } else {
                    $.messager.alert("提示", "必须选择可用状态的设备", "info");
                }
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
                if (row.status == 'Y') {
                    if (row.resourceType.name == '图片' || row.resourceType.name == '文字') {
                        $("#stayTime").textbox({"required":true,"novalidate":true});
                        $("#stayTimeTR").attr("style", "");
                        $("#stayTime1").textbox({"required":true,"novalidate":true});
                        $("#stayTimeTR1").attr("style", "");
                    }
                    $("#addResourceId").val(row.id);
                    $("#editResourceId").val(row.id);
                    $("#addResourceName").textbox("setValue", row.name);
                    $("#editResourceName").textbox("setValue", row.name);
                    closeWin("resWin");
                } else {
                    $.messager.alert("提示", "必须选择可用状态的资源", "info");
                }
            } else {
                $.messager.alert("提示", "请选择设备", "info");
            }
        }

        ///////////////////////segment///////////////////
        function showSegmentWin() {
            var row = selectedRow("list");
            if (row) {
                if (row.showType == 'segment') {
                    openWinFitPos("segmentWin");
                    $("#segmentWin").window("refresh", "<%=path %>/segment/list_page/" + row.id);
                } else {
                    $.messager.alert('提示', '只有时段播放模式的消息发布可以设置时段', 'info');
                }
            } else {
                $.messager.alert("提示", "请先选择需要设置时段的消息发布", "info");
            }
        }

        function addSegment() {
            toValidate("addSegmentForm");
            if (validateForm("addSegmentForm")) {
                $.post("<%=path %>/segment/add",
                        $("#addSegmentForm").serialize(),
                        function (data) {
                            if (data.result == "success") {
                                $("#addSegmentWin").window("close");
                                dataGridReload("segmentList");
                                $("#addSegmentForm").form("clear");
                            } else {
                                $.messager.alert("提示", data.message, "info");
                            }
                        }
                );
            }
        }

        function showEditSegment() {
            var row = selectedRow("segmentList");
            if (row) {
                $("#editSegmentForm").form("load", row);
                openWin("editSegmentWin");
            } else {
                $.messager.alert("提示", "请选择需要修改的时段信息", "info");
            }
        }

        function editSegment() {
            toValidate("editSegmentForm");
            if (validateForm("editSegmentForm")) {
                $.post("<%=path %>/segment/update",
                        $("#editSegmentForm").serialize(),
                        function (data) {
                            if (data.result == "success") {
                                closeWin("editSegmentWin");
                                $.messager.alert("提示", data.message, "info", function () {
                                    dataGridReload("segmentList");
                                });
                            } else {
                                $("#errMsgSegment").html(data.message);
                            }
                        }
                );
            }
        }

        function inactiveSegment() {
            var row = selectedRow("segmentList");
            if (row) {
                if (row.status == 'N') {
                    $.messager.alert("提示", "时段信息不可用,无需冻结", "info");
                } else {
                    $.get("<%=path %>/segment/inactive?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("segmentList");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要冻结的时段信息", "info");
            }
        }

        function activeSegment() {
            var row = selectedRow("segmentList");
            if (row) {
                if (row.status == 'Y') {
                    $.messager.alert("提示", "时段信息可用,无需激活", "info");
                } else {
                    $.get("<%=path %>/segment/active?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("segmentList");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要激活的时段信息", "info");
            }
        }
        ///////////////////////segment//////////////////

        function formatterType(value) {
            return value.name;
        }

        function formatterCode(value, row, index) {
            return row.device.code;
        }

        function formatterName(value) {
            return value.name;
        }

        function formatterArea(value) {
            return "区域" + value;
        }

        function formatterVersion(value) {
            return value.name;
        }

        function formatterShowType(value) {
            if (value == 'order') {
                return "顺序播放";
            } else if (value == 'now') {
                return "即时播放";
            } else if (value == "segment") {
                return "时段播放";
            }

        }

        function formatterCheckStatus(value) {
            if (value == 'not_submit') {
                return "未提交";
            } else if (value == 'checking') {
                return "审核中";
            } else if (value == "checked") {
                return "已审核";
            } else if (value == "finish") {
                return "已完成";
            }

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
					} else if (row.checkStatus == 'checking') {
					    return 'color:orange';
                    } else if (row.checkStatus == 'checked') {
                        return 'color:green;';
                    }
				}">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">用户ID</th>
        <th field="code" width="85" formatter="formatterCode">终端号</th>
        <th field="resource" width="100" formatter="formatterName">资源名称</th>
        <th field="publishLog" width="100">发布日志</th>
        <th field="publishTime" width="120" formatter="formatterDate">发布时间</th>
        <th field="area" width="60" formatter="formatterArea">显示区域</th>
        <th field="showType" width="80" formatter="formatterShowType">播放模式</th>
        <th field="startTimeStr" width="120" formatter="formatterDate">开始日期</th>
        <th field="endTimeStr" width="120" formatter="formatterDate">结束日期</th>
        <th field="stayTime" width="70">停留时间(S)</th>
        <th field="des" width="100">描述</th>
        <th field="checkStatus" width="60" formatter="formatterCheckStatus">审核状态</th>
        <th field="createTime" width="120" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="showAdd();">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="showEdit();">修改</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="showSegmentWin();">设置时段</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-tip" plain="true"
       onclick="toCheck();">提交审核</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactive()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="active()">激活</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="deviceResource">
            终端号:<input type="text" name="deviceCode" class="easyui-textbox"/>
            资源名称:<input type="text" name="resourceName" class="easyui-textbox"/>
            审核状态:<select name="checkStatus" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'not_submit',
                        text: '未提交'
                    },{
                        id: 'checking',
                        text: '审核中'
                    },{
                        id: 'checked',
                        text: '已审核'
                    },{
                        id: 'finish',
                        text: '已完成'
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
                    <input id="addDeviceCode" type="text" name="deviceCode" class="easyui-validatebox easyui-textbox"
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
                <td>显示区域:</td>
                <td>
                    <select name="area" class="easyui-validatebox easyui-combobox"
                            data-options="panelHeight:'auto',editable:false,required:true,novalidate:true">
                        <option value="1">区域1</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>播放模式:</td>
                <td><select name="showType" class="easyui-combobox" data-options="editable:false, valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'now',
                        text: '即时播放'
                    },{
                        id: 'order',
                        text: '顺序播放'
                    },{
                        id: 'segment',
                        text: '时段播放'
                    }]"></select></td>
            </tr>
            <tr>
                <td>开始日期:</td>
                <td><input type="text" name="startTime" class="easyui-datebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>结束日期:</td>
                <td><input type="text" name="endTime" class="easyui-datebox" data-options="editable:false"/></td>
            </tr>
            <tr id="stayTimeTR" style="display:none">
                <td>停留时间(S):</td>
                <td><input id="stayTime" type="text" name="stayTime" class="easyui-numberbox easyui-textbox"/>
                </td>
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
                    <input id="editDeviceCode" type="text" name="deviceCode" class="easyui-validatebox easyui-textbox"
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
                <td>显示区域:</td>
                <td>
                    <select name="area" class="easyui-validatebox easyui-combobox"
                            data-options="panelHeight:'auto',editable:false,required:true,novalidate:true">
                        <option value="1">区域1</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>播放模式:</td>
                <td><select name="showType" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'now',
                        text: '即时播放'
                    },{
                        id: 'order',
                        text: '顺序播放'
                    },{
                        id: 'segment',
                        text: '时段播放'
                    }]"></select></td>
            </tr>
            <tr>
                <td>开始日期:</td>
                <td><input type="text" name="startTimeStr" class="easyui-datebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>结束日期:</td>
                <td><input type="text" name="endTimeStr" class="easyui-datebox" data-options="editable:false"/></td>
            </tr>
            <tr id="stayTimeTR1" style="display:none">
                <td>停留时间(S):</td>
                <td><input id="stayTime1" type="text" name="stayTime" class="easyui-numberbox easyui-textbox"/>
                </td>
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
<div class="easyui-window site_win_big_wider input_big" id="segmentWin" style="padding: 0;" data-options="title:'时段',resizable:false,mode:true,closed:true"></div>

</body>
</html>
