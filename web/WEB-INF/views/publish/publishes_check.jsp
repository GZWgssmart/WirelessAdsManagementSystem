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
            doSearch();
        });

        function toCheck() {
            var row = selectedRow("list");
            if (row) {
                $.get("<%=path %>/devres/check?id=" + row.id + "&checkStatus=checked",
                        function (data) {
                            if (data.result == "success") {
                                $.messager.alert("提示", data.message, "info");
                                dataGridReload("list");
                            } else {
                                $.messager.alert("提示", data.message, "info");
                            }
                        });
            } else {
                $.messager.alert("提示", "请选择需要提交审核的消息发布", "info");
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

        function formatterCode(value, row, index) {
            return row.device.code;
        }

        function formatterName(value) {
            return value.name;
        }

        function formatterArea(value) {
            return "区域" + value;
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
        <th field="code" width="100" formatter="formatterCode">终端号</th>
        <th field="resource" width="150" formatter="formatterName">资源名称</th>
        <th field="publishLog" width="100">发布日志</th>
        <th field="area" width="80" formatter="formatterArea">显示区域</th>
        <th field="showType" width="80" formatter="formatterShowType">播放模式</th>
        <th field="startTimeStr" width="150" formatter="formatterDate">开始时间</th>
        <th field="endTimeStr" width="150" formatter="formatterDate">结束时间</th>
        <th field="stayTime" width="60">停留时间（S）</th>
        <th field="des" width="200">描述</th>
        <th field="checkStatus" width="60" formatter="formatterCheckStatus">审核状态</th>
        <th field="createTime" width="150" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-tip" plain="true"
       onclick="toCheck();">审核</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="deviceResource">
            <input type="hidden" name="checkStatus" value="checking" />
        </form>
    </div>
</div>

</body>
</html>
