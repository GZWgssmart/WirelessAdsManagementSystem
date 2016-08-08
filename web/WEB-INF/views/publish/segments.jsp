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
    <title>时段列表-青岛宝瑞无线广告管理系统</title>
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
            setPagination("#segmentList");
        });
    </script>
</head>
<body>
<table id="segmentList" class="easyui-datagrid" toolbar="#segmenttb" style="height:100%;"
       data-options="
        url:'<%=path %>/segment/list_pager/${pubId }',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:20,
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
        <th field="startTimeStr" width="150">开始时间</th>
        <th field="endTimeStr" width="150">结束时间</th>
        <th field="des" width="200">描述</th>
        <th field="createTime" width="150" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="segmenttb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="openWinFitPos('addSegmentWin');">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="showEditSegment();">修改</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactiveSegment()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="activeSegment()">激活</a>
</div>

<div class="easyui-window site_win_small input_big" id="addSegmentWin" data-options="title:'添加时段',resizable:false,mode:true,closed:true">
    <form:form id="addSegmentForm" modelAttribute="timeSegment">
        <input type="hidden" name="pubId" value="${pubId }" />
        <table>
            <tr>
                <td>开始时间:</td>
                <td><input type="text" name="startTime" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>结束时间:</td>
                <td><input type="text" name="endTime" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" onclick="addSegment();">确认</button>
                </td>
            </tr>
        </table>
    </form:form>
</div>

<div class="easyui-window site_win_small input_big" id="editSegmentWin" data-options="title:'修改时段',resizable:false,mode:true,closed:true">
    <div id="errMsgSegment"></div>
    <form id="editSegmentForm" method="post" modelAttribute="timeSegment">
        <input type="hidden" name="id" />
        <table>
            <tr>
                <td>开始日期:</td>
                <td><input type="text" name="startTimeStr" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>结束日期:</td>
                <td><input type="text" name="endTimeStr" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td><button type="button" onclick="closeWin('editSegmentWin');">取消</button></td>
                <td>
                    <button type="button" onclick="editSegment();">确认</button>
                </td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>
