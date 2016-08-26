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

        function doSearch() {
            $("#list").datagrid({
                url:'<%=path %>/publish/search_pager/${planId }',
                pageSize:20,
                queryParams:getQueryParams("list", "searchForm")
            });
            setPagination("#list");
        }

        function searchAll() {
            $("#searchForm").form("clear");
            $("#list").datagrid({
                url:'<%=path %>/publish/search_pager/${planId }',
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

        function formatterCode(value, row, index) {
            return row.device.code;
        }

    </script>
</head>
<body>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/publish/search_pager/${planId }',
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
        <th field="code" width="85" formatter="formatterCode">终端号</th>
        <th field="resourceName" width="100">资源名称</th>
        <th field="publishLog" width="100">发布日志</th>
        <th field="publishTime" width="120" formatter="formatterDate">发布时间</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <div class="input_small">
        <form id="searchForm" modalAttribute="publish">
            终端号:<input type="text" name="deviceCode" class="easyui-textbox"/>
            资源名称:<input type="text" name="resourceName" class="easyui-textbox"/>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="doSearch();">搜索</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="searchAll();">查询所有</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"
               onclick="refreshAll();">刷新</a>
        </form>
    </div>
</div>

</body>
</html>
