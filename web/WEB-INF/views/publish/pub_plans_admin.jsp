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
    <title>消息发布列表-青岛宝瑞液晶信息屏发布系统</title>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/publish/pub_plans_admin.js"></script>
</head>
<body>
<div id="tabs" class="easyui-tabs" data-options="fit:true,border:false">
    <div title="计划">
        <table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
               data-options="
                url:'<%=path %>/pubplan/search_pager_admin/${customerId }',
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
            <div class="input_small">
                <form id="searchForm" modalAttribute="pubplan">
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
                       onclick="doSearch('${customerId }');">搜索</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                       onclick="searchAll('${customerId }');">查询所有</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"
                       onclick="refreshAll();">刷新</a>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
