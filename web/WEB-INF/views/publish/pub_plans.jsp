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

            $('#list').datagrid({
                onDblClickCell: function(rowIndex, rowData){
                    showPlanDetail();
                }
            });
            $("#list").datagrid("hideColumn", 'versionId');
        });

        function showAdd() {
            $("#addForm").form("clear");
            $("#stayTime").textbox({"required":false,"novalidate":true});
            $("#stayTimeTR").attr("style", "display:none");
            $("#confirmSeg").removeAttr("disabled");
            $("#cancelSeg").removeAttr("disabled");
            $("#moreSeg").removeAttr("disabled");
            openWinFitPos('addWin');
        }

        function add() {
            toValidate("addForm");
            if (validateForm("addForm")) {
                $("#addPlan").attr("disabled", "true");
                $.post('<%=path %>/pubplan/add',
                        $("#addForm").serialize() + "&" + $("#addSegmentForm").serialize(),
                        function (data) {
                            $("#addPlan").removeAttr("disabled");
                            $("#addSegmentForm").form("clear");
                            if (data.result == "success") {
                                $("#addWin").window("close");
                                dataGridReload("list");
                                $("#addForm").form("clear");
                            } else {
                                $.messager.alert("提示", data.message, "info");
                            }
                        }, "json");
            }
        }

        function showEdit() {
            var row = selectedRow("list");
            if (row) {
                $("#editForm").form("load", row);
                $("#stayTime1").textbox({"required":false,"novalidate":true});
                $("#stayTimeTR1").attr("style", "display:none");
                if (row.stayTime != '') {
                    $("#stayTime1").textbox({"required":true,"novalidate":true});
                    $("#stayTimeTR1").attr("style", "");
                } else {
                    $("#stayTime1").textbox("setValue", "");
                }
                $("#editArea").combobox({
                    url:'<%=path %>/version/list_combo_area/' + row.versionId + '/' + row.area,
                    method:'get',
                    valueField:'id',
                    textField:'text',
                    panelHeight:'auto'
                });
                $("#editDeviceCode").textbox("setValue", row.name);
                $("#editResourceName").textbox("setValue", row.resourceName);
                $("#confirmSeg").removeAttr("disabled");
                $("#cancelSeg").removeAttr("disabled");
                $("#moreSeg").removeAttr("disabled");
                openWin("editWin");
            } else {
                $.messager.alert("提示", "请选择需要修改的计划", "info");
            }
        }

        function edit() {
            toValidate("editForm");
            if (validateForm("editForm")) {
                $("#editPlan").attr("disabled", "true");
                $.post('<%=path %>/pubplan/update',
                    $("#editForm").serialize() + "&" + $("#addSegmentForm").serialize(),
                        function (data) {
                            $("#editPlan").removeAttr("disabled");
                            if (data.result == "success") {
                                closeWin("editWin");
                                dataGridReload("list");
                                $("#editForm").form("clear");
                            } else {
                                $.messager.alert("提示", data.message, "info");
                            }
                        },
                        'json'
                );
            }
        }

        function showPlanDetail() {
            var row = selectedRow("list");
            if (row) {
                addTab(row.name + "计划详情", "<%=path %>/publish/list_page/" + row.id);
            } else {
                $.messager.alert("提示", "请先选择计划", "info");
            }
        }

        function toCheck() {
            var row = selectedRow("list");
            if (row) {
                if (row.checkStatus != "not_submit") {
                    $.messager.alert("提示", "请选择未提交审核的计划", "info");
                } else {
                    $.get("<%=path %>/pubplan/check?id=" + row.id + "&checkStatus=checking",
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
                $.messager.alert("提示", "请选择需要提交审核的计划", "info");
            }
        }

        function inactive() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'N') {
                    $.messager.alert("提示", "计划不可用,无需冻结", "info");
                } else {
                    $.get("<%=path %>/pubplan/inactive?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要冻结的计划", "info");
            }
        }

        function active() {
            var row = selectedRow("list");
            if (row) {
                if (row.status == 'Y') {
                    $.messager.alert("提示", "计划可用,无需激活", "info");
                } else {
                    $.get("<%=path %>/pubplan/active?id=" + row.id,
                            function (data) {
                                if (data.result == "success") {
                                    $.messager.alert("提示", data.message, "info");
                                    dataGridReload("list");
                                }
                            });
                }
            } else {
                $.messager.alert("提示", "请选择需要激活的计划", "info");
            }
        }

        function doSearch() {
            $("#list").datagrid({
                url:'<%=path %>/pubplan/search_pager',
                pageSize:20,
                queryParams:getQueryParams("list", "searchForm")
            });
            setPagination("#list");
        }

        function searchAll() {
            $("#searchForm").form("clear");
            $("#list").datagrid({
                url:'<%=path %>/pubplan/search_pager',
                pageSize:20,
                queryParams:getQueryParams("list", "searchForm")
            });
            setPagination("#list");
        }

        function refreshAll() {
            $("#list").datagrid("reload");
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

        function chooseDev(type) {
            $("#addType").val(type);
            $("#editType").val(type);
            if (type == "multiple") {
                var rows = selectedRows("devList");
                if (rows) {
                    var deviceIds = "";
                    var deviceCodes = "";
                    var versionId = '';
                    var versionName = '';
                    var sameVersion = true;
                    $.each(rows, function (index, row) {
                        if (versionId == '') {
                            versionId = row.version.id;
                        } else {
                            if (versionId != row.version.id) {
                                sameVersion = false;
                            }
                        }
                        if (versionName == '') {
                            $("#addVersionName").val(row.version.name);
                        }
                        if (deviceIds == "") {
                            deviceIds = row.id;
                        } else {
                            deviceIds += "," + row.id;
                        }
                        if (deviceCodes == "") {
                            deviceCodes = row.code;
                        } else {
                            deviceCodes += "," + row.code;
                        }
                    });
                    if (sameVersion) {
                        $("#addDeviceId").val(deviceIds);
                        $("#editDeviceId").val(deviceIds);
                        $("#addDeviceCode").textbox("setValue", deviceCodes);
                        $("#editDeviceCode").textbox("setValue", deviceCodes);
                        $("#addVersionId").val(versionId);
                        showArea(versionId);
                        closeWin("devWin");
                    } else {
                        $.messager.alert("提示", "请选择相同版本的设备", "info");
                    }
                } else {
                    $.messager.alert("提示", "请选择设备", "info");
                }
            } else if (type == "group") {
                var groupId = $("#deviceGroupId").combobox("getValue");
                var versionId = $("#versionId").combobox("getValue");
                var versionName = $("#versionId").combobox("getText");
                if (groupId != undefined && groupId != '' && versionId != undefined && versionId != '') {
                    $("#addDeviceId").val(groupId);
                    $("#editDeviceId").val(groupId);
                    $("#addVersionId").val(versionId);
                    var groupName = $("#deviceGroupId").combobox("getText");
                    $("#addDeviceCode").textbox("setValue", groupName + "及" + versionName + "的全部终端");
                    $("#editDeviceCode").textbox("setValue", groupName + "及" + versionName + "的全部终端");
                    $("#addGroupName").val(groupName);
                    showArea(versionId);
                    $("#addVersionName").val(versionName);
                    closeWin("devWin");
                } else {
                    $.messager.alert("提示", "请选择分组及版本", "info");
                }
            } else if (type == "all") {
                var groupId = $("#deviceGroupId").combobox("getValue");
                if (groupId != undefined && groupId != '') {
                    $.messager.alert("提示", "请点击查询所有后,只选择版本", "info");
                } else {
                    var versionId = $("#versionId").combobox("getValue");
                    var versionName = $("#versionId").combobox("getText");
                    if (versionId != undefined && versionId != '') {
                        $("#addVersionId").val(versionId);
                        $("#addDeviceCode").textbox("setValue", versionName + "的全部终端");
                        $("#editDeviceCode").textbox("setValue", versionName + "的全部终端");
                        showArea(versionId);
                        $("#addVersionName").val(versionName);
                        closeWin("devWin");
                    } else {
                        $.messager.alert("提示", "请选择版本", "info");
                    }
                }
            }
        }

        function showArea(versionId) {
            $.get('<%=path %>/version/list_combo_area/' + versionId + '/0',
                function (data) {
                    $("#addAreaR").html("");
                    $.each(data, function(idx, item) {
                        if ((idx + 1) % 4 == 0) {
                            $("#addAreaR").append("<br />");
                        }
                       $("#addAreaR").append("<a href='javascript:;' onclick='showResWin(" + item.id + ")'>区域" +  item.id + "资源</a>&nbsp;&nbsp;&nbsp;&nbsp;");
                    });
                }, "json"
            );
        }

        var currentArea;

        function showResWin(area) {
            openWinFitPos("resWin");
            $("#resWin").window("refresh", "<%=path %>/res/list_page_choose/");
            currentArea = area;
        }

        var currentChoosedResource;

        function resContextMenu(e, rowIndex, rowData) {
            $('#resList').datagrid('selectRow', rowIndex);
            currentChoosedResource = rowData;
            e.preventDefault();
            $('#resMenu').menu('show', {
                left: e.pageX,
                top: e.pageY
            })
        }

        function addResourceToArea() {
            openWinFitPos("detailWin");
        }

        function confirmAddResourceToArea() {
            var detail = "{'resourceId':'" + currentChoosedResource.id + "','resourceName':'" + currentChoosedResource.name + "','push':true,"
                    + "'area':" + currentArea + ",'showType':'" + $("#showType").combobox("getValue") + "','startTime':'" + $("#startTime").datebox("getValue") + "','"
                    + "endTime':'" + $("#endTime").datebox("getValue") + "','stayTime':'" + $("#stayTime").textbox("getValue") + "','segments':'" + getSegments() + "'}";
            var details = $("#addResourceDetails").val();
            if (details != undefined && details != "") {
                details += "," + detail;
            } else {
                details = detail;
            }
            $("#addResourceDetails").val(details);
            $("#detailForm").form("clear");
            $("#addSegmentForm").form("clear");
            closeWin("detailWin")
        }

        function getSegments() {
            var segments = "";
            for (var i = 0; i < 24; i++) {
                var start = $("#startTime" + i).timespinner("getValue");
                var end = $("#endTime" + i).timespinner("getValue");
                if (segments == "") {
                    if (start != "" && end != "") {
                        segments = start + "-" + end;
                    }
                } else {
                    if (start != "" && end != "") {
                        segments += "," + start + "-" + end;
                    }
                }
            }
            return segments;
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

        function showAddSegmentWin(type) {
            var v = $("#" + type + "ShowType").combobox("getValue");
            if (v == "segment") {
                if (type == "edit") {
                    showAlreadySegments($("#planId").val())
                }
                openWinFitPos("addSegment");
            } else {
                $.messager.alert("提示", "时段播放模式才可设置时段", "info");
            }
        }

        function showAlreadySegments(pubId) {
            $.get('<%=path %>/segment/querybypubid/' + pubId, function (data) {
                if (data) {
                    if (data.lengh > 6 && data.length <= 12) {
                        moreSegment();
                    } else if (data.length > 12 && data.length <= 18) {
                        moreSegment();
                        moreSegment();
                    } else if (data.length > 18) {
                        moreSegment();
                        moreSegment();
                        moreSegment();
                    }
                    $.each(data, function (index, item) {
                        $("#startTime" + index).timespinner("setValue", item.startTime);
                        $("#endTime" + index).timespinner("setValue", item.endTime);
                    })
                }
            }, "json");
        }

        function showSegments() {
            var row = selectedRow("list");
            if (row) {
                if (row.showType == "segment") {
                     showAlreadySegments(row.id);
                    $("#confirmSeg").attr("disabled", "true");
                    $("#cancelSeg").attr("disabled", "true");
                    $("#moreSeg").attr("disabled", "true");
                    openWinFitPos("addSegment")
                } else {
                    $.messager.alert("提示", "请选择时段播放的计划查看", "info");
                }
            } else {
                $.messager.alert("提示", "请选择时段播放的计划查看", "info");
            }
        }

        function confirmSegment() {
            closeWin("addSegment");
        }

        function cancelSegment() {
            $("#addSegmentForm").form("clear");
            closeWin("addSegment")
        }

        function moreSegment() {
            var trs = $("tr[style='display:none;']");
            if (trs.length > 0) {
                for (var i = 0; i < 3; i++) {
                    var tr = trs[i];
                    $(tr).removeAttr("style")
                }
            } else {
                $.messager.alert("提示", "最多支持设置24个时段", "info");
            }
        }

        function formatterDevice(value) {
            return value.name;
        }

        function formatterType(value) {
            return value.name;
        }

        function formatterCode(value, row, index) {
            return row.device.code;
        }

        function formatterName(value) {
            return value.name;
        }

        function formatterVersion(value) {
            return value.name;
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

        function formatterPlanType(value) {
            if (value == 'one') {
                return '单个';
            } else if (value == 'multiple') {
                return '多个';
            } else if (value == 'group') {
                return '分组';
            } else if (value == 'all') {
                return '全部';
            }
        }

    </script>
</head>
<body>
<div id="tabs" class="easyui-tabs" data-options="fit:true,border:false">
    <div title="计划">
        <table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
               data-options="
                url:'<%=path %>/pubplan/search_pager',
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
                <th field="planName" width="85">计划名称</th>
                <th field="name" width="85">计划</th>
                <th field="type" width="85" formatter="formatterPlanType">计划类型</th>
                <th field="groupName" width="60">终端分组</th>
                <th field="versionName" width="60">终端版本</th>
                <th field="devCount" width="60">总终端数</th>
                <th field="finishCount" width="60">已完成数</th>
                <th field="notFinishCount" width="60">未完成数</th>
                <th field="des" width="100">描述</th>
                <th field="checkStatus" width="60" formatter="formatterCheckStatus">审核状态</th>
                <th field="createTime" width="120" formatter="formatterDate">创建时间</th>
                <th field="status" width="50" formatter="formatterStatus">状态</th>
                <th field="versionId" width="0"></th>
            </tr>
            </thead>
        </table>
        <div id="tb">
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
               onclick="showAdd();">添加</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
               onclick="showEdit();">修改</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true"
               onclick="showPlanDetail();">计划详情</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-tip" plain="true"
               onclick="toCheck();">提交审核</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
               onclick="inactive()">冻结</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
               onclick="active()">激活</a>
            <div class="input_small">
                <form id="searchForm" modalAttribute="publishPlan">
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

        <div class="easyui-window site_win_big input_big" id="addWin" data-options="title:'添加计划',resizable:false,mode:true,closed:true">
            <form:form id="addForm" modelAttribute="publishPlan">
                <input id="addType" type="hidden" name="type" />
                <input id="addGroupName" type="hidden" name="groupName" />
                <input id="addVersionId" type="hidden" name="versionId" />
                <input id="addVersionName" type="hidden" name="versionName" />
                <input id="addResourceDetails" type="hidden" name="resourceDetails" />
                <table>
                    <tr>
                        <td>计划名称:</td>
                        <td><input type="text" name="planName" class="easyui-validatebox easyui-textbox" data-options="required:true,novalidate:true"/>
                        </td>
                    </tr>
                    <tr>
                        <td>设备:</td>
                        <td><input id="addDeviceId" type="hidden" name="deviceId" />
                            <input id="addDeviceCode" type="text" name="deviceCode" class="easyui-validatebox easyui-textbox"
                                   data-options="editable:false,required:true,novalidate:true"/>
                            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                               onclick="showDevWin();">选择设备</a></td>
                    </tr>
                    <tr>
                        <td>区域资源:</td>
                        <td id="addAreaR"></td>
                    </tr>

                    <tr>
                        <td>描述:</td>
                        <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <button id="addPlan" type="button" onclick="add();">确认</button>
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>

        <div class="easyui-window site_win_big input_big" id="editWin" data-options="title:'修改消息发布',resizable:false,mode:true,closed:true">
            <div id="errMsg"></div>
            <form id="editForm" method="post" modelAttribute="publishPlan">
                <input id="planId" type="hidden" name="id" />
                <input id="editType" type="hidden" name="type" />
                <input id="editGroupName" type="hidden" name="groupName" />
                <input id="editVersionId" type="hidden" name="versionId" />
                <input id="editVersionName" type="hidden" name="versionName" />
                <table>
                    <tr>
                        <td>设备:</td>
                        <td><input id="editDeviceId" type="hidden" name="deviceId" />
                            <input id="editDeviceCode" type="text" name="deviceCode" class="easyui-validatebox easyui-textbox"
                                   data-options="editable:false,required:true,novalidate:true"/>
                            <!--
                            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                               onclick="showDevWin();">选择设备</a></td>
                               -->
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
                        <td>描述:</td>
                        <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
                    </tr>
                    <tr>
                        <td><button type="button" onclick="closeWin('editWin');">取消</button></td>
                        <td>
                            <button id="editPlan" type="button" onclick="edit();">确认</button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="easyui-window site_win_big_wider input_big" id="devWin" style="padding:0;" data-options="title:'选择设备',resizable:false,mode:true,closed:true"></div>
<div class="easyui-window site_win_big_wider input_big" id="resWin" style="padding:0;" data-options="title:'选择资源',resizable:false,mode:true,closed:true"></div>
</body>
</html>
