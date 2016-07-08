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
</head>
<body>
<table id="resList" class="easyui-datagrid" toolbar="#restb" style="height:100%;"
       data-options="
        url:'<%=path %>/res/search_pager',
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
					}
				}">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">用户ID</th>
        <th field="name" width="150">名称</th>
        <th field="resourceType" width="80" formatter="formatterType">类型</th>
        <th field="fileName" width="150">文件名</th>
        <th field="path" width="200" formatter="formatterLong">路径</th>
        <th field="des" width="200">描述</th>
        <th field="createTime" width="150" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="restb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="chooseRes();">选择</a>
    <div class="input_small">
        <form id="resSearchForm" modalAttribute="resource">
            类型:<select name="resourceTypeId" class="easyui-combobox"
                       data-options="url:'<%=path %>/restype/list_combo/all',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
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
               onclick="doSearchRes();">搜索</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="searchAllRes();">查询所有</a>
        </form>
    </div>
</div>
</body>
</html>
