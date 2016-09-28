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

    <script src="<%=path %>/js/publish/publishes.js"></script>
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
        <th field="resource" width="100" formatter="formatterName">资源名称</th>
        <th field="publishLog" width="100">发布日志</th>
        <th field="publishTime" width="120" formatter="formatterDate">发布时间</th>
        <th field="area" width="60" formatter="formatterArea">显示区域</th>
        <th field="showType" width="80" formatter="formatterShowType">播放模式</th>
        <th field="startTime" width="120" formatter="formatterDate">开始日期</th>
        <th field="endTime" width="120" formatter="formatterDate">结束日期</th>
        <th field="stayTime" width="70">停留时间(S)</th>
        <th field="showCount" width="70">播放次数</th>
        <th field="segments" width="200" formatter="formatterLong">时段</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true"
       onclick="showSegments();">查看时段</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true"
       onclick="showAllRes('${planId }');">查看所有资源</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="publish">
            终端号:<input type="text" name="deviceCode" class="easyui-textbox"/>
            资源名称:<input type="text" name="resourceName" class="easyui-textbox"/>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="doSearch('${planId }');">搜索</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="searchAll('${planId }');">查询所有</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"
               onclick="refreshAll();">刷新</a>
        </form>
    </div>
</div>

<div class="easyui-window site_win_big_wider input_big" id="allResWin" style="padding:0;" data-options="title:'所有资源',resizable:false,mode:true,closed:true">
    <table id="resList" class="easyui-datagrid" style="height:100%;"
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
            <th field="resource" width="100" formatter="formatterName">资源名称</th>
            <th field="area" width="60" formatter="formatterArea">显示区域</th>
            <th field="showType" width="80" formatter="formatterShowType">播放模式</th>
            <th field="startTime" width="120" formatter="formatterDate">开始日期</th>
            <th field="endTime" width="120" formatter="formatterDate">结束日期</th>
            <th field="stayTime" width="70">停留时间(S)</th>
            <th field="showCount" width="70">播放次数</th>
        </tr>
        </thead>
    </table>
</div>

<div class="easyui-window site_win_normal_wider input_small" id="addSegment" data-options="title:'所有时段',resizable:false,mode:true,closed:true">
    <form id="addSegmentForm" modelAttribute="publishPlan">
        <table>
            <tr>
                <td>时段1:</td>
                <td>
                    <input id="startTime0" name="segments[0].startTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                    -
                    <input id="endTime0" name="segments[0].endTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                </td>
                <td>时段2:</td>
                <td>
                    <input id="startTime1" name="segments[1].startTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                    -
                    <input id="endTime1" name="segments[1].endTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                </td>
            </tr>
            <tr>
                <td>时段3:</td>
                <td>
                    <input id="startTime2" name="segments[2].startTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                    -
                    <input id="endTime2" name="segments[2].endTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                </td>
                <td>时段4:</td>
                <td>
                    <input id="startTime3" name="segments[3].startTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                    -
                    <input id="endTime3" name="segments[3].endTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                </td>
            </tr>
            <tr>
                <td>时段5:</td>
                <td>
                    <input id="startTime4" name="segments[4].startTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                    -
                    <input id="endTime4" name="segments[4].endTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                </td>
                <td>时段6:</td>
                <td>
                    <input id="startTime5" name="segments[5].startTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                    -
                    <input id="endTime5" name="segments[5].endTime" class="easyui-timespinner" data-options="showSeconds:true" style="width:120px;"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段7:</td>
                <td>
                    <input id="startTime6" name="segments[6].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime6" name="segments[6].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段8:</td>
                <td>
                    <input id="startTime7" name="segments[7].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime7" name="segments[7].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段9:</td>
                <td>
                    <input id="startTime8" name="segments[8].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime8" name="segments[8].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段10:</td>
                <td>
                    <input id="startTime9" name="segments[9].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime9" name="segments[9].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段11:</td>
                <td>
                    <input id="startTime10" name="segments[10].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime10" name="segments[10].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段12:</td>
                <td>
                    <input id="startTime11" name="segments[11].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime11" name="segments[11].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段13:</td>
                <td>
                    <input id="startTime12" name="segments[12].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime12" name="segments[12].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段14:</td>
                <td>
                    <input id="startTime13" name="segments[13].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime13" name="segments[13].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段15:</td>
                <td>
                    <input id="startTime14" name="segments[14].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime14" name="segments[14].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段16:</td>
                <td>
                    <input id="startTime15" name="segments[15].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime15" name="segments[15].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段17:</td>
                <td>
                    <input id="startTime16" name="segments[16].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime16" name="segments[16].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段18:</td>
                <td>
                    <input id="startTime17" name="segments[17].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime17" name="segments[17].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段19:</td>
                <td>
                    <input id="startTime18" name="segments[18].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime18" name="segments[18].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段20:</td>
                <td>
                    <input id="startTime19" name="segments[19].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime19" name="segments[19].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段21:</td>
                <td>
                    <input id="startTime20" name="segments[20].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime20" name="segments[20].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段22:</td>
                <td>
                    <input id="startTime21" name="segments[21].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime21" name="segments[21].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
            <tr style="display:none;">
                <td>时段23:</td>
                <td>
                    <input id="startTime22" name="segments[22].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime22" name="segments[22].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
                <td>时段24:</td>
                <td>
                    <input id="startTime23" name="segments[23].startTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                    -
                    <input id="endTime23" name="segments[23].endTime" class="easyui-timespinner" data-options="showSeconds:true"/>
                </td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>
