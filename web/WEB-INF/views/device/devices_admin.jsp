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
    <title>终端列表-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/device/devices_admin.js"></script>
</head>
<body>
<div id="devAdminLayer" class="layer"></div>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/device/search_pager_admin/${customerId }',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:50,
				pageList: [40, 50, 60, 70],
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
        <th field="code" width="100">终端号</th>
        <th field="version" width="50" formatter="formatterName">版本</th>
        <th field="deviceGroup" width="60" formatter="formatterName">终端分组</th>
        <th field="driver" width="60">驾驶员</th>
        <th field="phone" width="100">手机号</th>
        <th field="busNo" width="60">车路线</th>
        <th field="busPlateNo" width="75">车牌号</th>
        <th field="online" width="60" formatter="formatterOnline">在线状态</th>
        <th field="onlineTime" width="135" formatter="formatterDate">上线时间</th>
        <th field="offlineTime" width="135" formatter="formatterDate">离线时间</th>
        <th field="adsUpdateTime" width="135" formatter="formatterDate">广告更新时间</th>
        <th field="installTimeStr" width="120">安装时间</th>
        <th field="des" width="100">描述</th>
        <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <div class="input_small">
        <input type="hidden" id="customerId" value="${customerId }" />
        <form id="searchForm" modalAttribute="device">
            终端号:<input type="text" name="code" class="easyui-textbox"/>
            车牌号:<input type="text" name="busPlateNo" class="easyui-textbox"/>
            分组:<select id="groupSearch" name="deviceGroupId" class="easyui-combobox"
                       data-options="url:'<%=path %>/devgroup/list_combo_admin/${customerId }/all',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
            版本:<select id="versionSearch" name="versionId" class="easyui-combobox"
                       data-options="url:'<%=path %>/version/list_combo/0/all',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
            是否在线:<select id="onlineSearch" name="online" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'Y',
                        text: '在线'
                    },{
                        id: 'N',
                        text: '离线'
                    }]">
        </select>
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

</body>
</html>
