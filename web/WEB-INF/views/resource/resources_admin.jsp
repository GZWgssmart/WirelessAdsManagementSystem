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
    <title>资源列表-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/resource/resources_admin.js"></script>
</head>
<body>
<div id="resAdminLayer" class="layer"></div>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/res/search_pager_admin/${customerId }',
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
					}
				}">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">用户ID</th>
        <th field="name" width="160">名称</th>
        <th field="resourceType" width="80" formatter="formatterName">类型</th>
        <th field="ofileName" width="300">原始文件名</th>
        <th field="showDetailSetting" width="100" formatter="formatterYN">是否显示详情设置</th>
        <th field="fileName" width="150" formatter="formatterLong">文件名</th>
        <th field="path" width="200" formatter="formatterLong">路径</th>
        <th field="des" width="100">描述</th>
        <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <div class="input_small">
        <input type="hidden" id="customerId" value="${customerId }" />
        <form id="searchForm" modalAttribute="resource">
            名称:<input type="text" name="name" class="easyui-textbox"/>
            类型:<select id="resTypeSearch" name="resourceTypeId" class="easyui-combobox"
                       data-options="url:'<%=path %>/restype/list_combo/all',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
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
