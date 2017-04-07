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
    <title>客户列表-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/customer/customers_dev_admin.js"></script>
</head>
<body>
<div id="tabs" class="easyui-tabs" data-options="fit:true,border:false">
    <div title="选择客户">
        <table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
               data-options="
        url:'<%=path %>/customer/search_pager',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:50,
				pageList: [40, 50, 60, 70],
				rowStyler: function(index,row){
					if (row.role == 'super'){
						return 'background-color:#ccc;';
					} else if (row.status == 'N') {
					    return 'color:red;';
					}
				}">
            <thead>
            <tr>
                <th field="id" checkbox="true" width="50">用户ID</th>
                <th field="email" width="150">邮箱</th>
                <th field="name" width="100">姓名</th>
                <th field="phone" width="100">手机号</th>
                <th field="address" width="200">地址</th>
                <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
                <th field="loginTime" width="135" formatter="formatterDate">最近登录时间</th>
                <th field="status" width="50" formatter="formatterStatus">状态</th>
            </tr>
            </thead>
        </table>
        <div id="tb">
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
               onclick="showDev()">查看终端列表</a>
            <div class="input_small">
                <form id="searchForm" modalAttribute="customer">
                    邮箱:<input type="email" name="email" class="easyui-textbox"/>
                    姓名:<input type="text" name="name" class="easyui-textbox"/>
                    手机:<input type="text" name="phone" class="easyui-textbox"/>
                    状态:<select id="statusSearch" name="status" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
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
    </div>
</div>

</body>
</html>
