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
    <title>发布计划列表-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/mobile.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.mobile.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/publish/pub_plans_finish-mobile.js"></script>
</head>
<body>
<div id="planFinishLayer" class="layer"></div>
<div class="easyui-navpanel">
    <header>
        <div class="m-toolbar">
            <div class="m-title">
                <b>${sessionScope.customer.email }</b>
            </div>
            <div class="m-left">
                <a href="javascript:void(0)" class="easyui-linkbutton m-back" plain="true" outline="true" onclick="goBack();">返回</a>
            </div>
            <div class="m-right">
                <a href="javascript:void(0)" class="easyui-menubutton" data-options="iconCls:'icon-more',menu:'#mm',menuAlign:'right',hasDownArrow:false"></a>
            </div>
        </div>
        <div id="mm" class="easyui-menu" style="width:150px;">
            <div>
                <a href="javascript:void(0);" onclick="toPage('<%=path %>/admin/mob/home')">刷新主页</a>
            </div>
        </div>
    </header>
    <div id="tabs" class="easyui-tabs" data-options="fit:true,border:false">
        <div title="发布计划">
            <table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
                   data-options="
                    method:'get',
                            rownumbers:true,
                            singleSelect:true,
                            autoRowHeight:false,
                            pagination:true,
                            border:false,
                            pageSize:50,
                            pageList: [40, 50, 60, 70]">
                <thead>
                <tr>
                    <th field="id" checkbox="true" width="50">用户ID</th>
                    <th field="planName" width="85">计划名称</th>
                    <th field="name" width="100">计划</th>
                    <th field="type" width="85" formatter="formatterPlanType">计划类型</th>
                    <th field="groupName" width="60">终端分组</th>
                    <th field="versionName" width="60">终端版本</th>
                    <th field="devCount" width="60">总终端数</th>
                    <th field="finishCount" width="60">已完成数</th>
                    <th field="notFinishCount" width="60">未完成数</th>
                    <th field="des" width="100">描述</th>
                    <th field="checkStatus" width="60" formatter="formatterCheckStatus">审核状态</th>
                    <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
                    <th field="status" width="50" formatter="formatterStatus">状态</th>
                    <th field="versionId" width="0"></th>
                </tr>
                </thead>
            </table>
            <div id="tb">
                <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true"
                   onclick="showPlanDetailMob();">计划详情</a>
                <div class="input_small">
                    <form id="searchForm" modalAttribute="publishPlan">
                        <input type="hidden" name="checkStatus" value="finish" />
                    </form>
                </div>
            </div>
        </div>
    </div>
    <footer style="padding:2px 3px;text-align: center;">
        青岛宝瑞媒体发布系统V1.0<br />
        地址：山东省青岛市崂山区株洲路140号&nbsp;&nbsp;<br />技术支持:0532-80678775/80678776
    </footer>
</div>
</body>
</html>
