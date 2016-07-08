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
                $.get("<%=path %>/devres/check?id=" + row.id + "&checkStatus=已审核",
                        function (data) {
                            if (data.result == "success") {
                                $.messager.alert("提示", data.message, "info");
                                dataGridReload("list");
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
        <th field="device" width="100" formatter="formatterCode">终端号</th>
        <th field="device" width="150" formatter="formatterName">终端名称</th>
        <th field="resource" width="150" formatter="formatterName">资源名称</th>
        <th field="area" width="80" formatter="formatterArea">显示区域</th>
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
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-tip" plain="true"
       onclick="toCheck();">审核</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="deviceResource">
            <input type="hidden" name="checkStatus" value="审核中" />
        </form>
    </div>
</div>

</body>
</html>
