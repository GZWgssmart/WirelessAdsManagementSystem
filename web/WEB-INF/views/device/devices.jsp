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

    <script src="<%=path %>/js/device/devices.js"></script>
</head>
<body>
<div id="devLayer" class="layer"></div>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/device/search_pager',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:50,
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
        <!--
        <th field="installTimeStr" width="120">安装时间</th>
        -->
        <th field="des" width="100">描述</th>
        <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="showAdd();">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="showEdit();">修改</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactive()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="active()">激活</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true"
       onclick="showAllRes();">查看所有资源</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="device">
            终端号:<input type="text" name="code" class="easyui-textbox"/>
            车牌号:<input type="text" name="busPlateNo" class="easyui-textbox"/>
            分组:<select id="devGroupSearch" name="deviceGroupId" class="easyui-combobox"
                       data-options="url:'<%=path %>/devgroup/list_combo/all/search',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
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

<div class="easyui-window site_win_big input_big" id="addWin" data-options="title:'添加设备',resizable:false,mode:true,closed:true">
    <form:form id="addForm" modelAttribute="device">
        <table>
            <tr>
                <td>终端号:</td>
                <td><input type="text" name="code" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>版本:</td>
                <td>
                    <select name="versionId" class="easyui-validatebox easyui-combobox"
                            data-options="url:'<%=path %>/version/list_combo/0/Y',method:'get',valueField:'id',textField:'text',
                           panelHeight:'auto',editable:false,required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>分组:</td>
                <td>
                    <select id="addDeviceGroupId" name="deviceGroupId" class="easyui-validatebox easyui-combobox"
                           data-options="editable:false,required:false,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>驾驶员:</td>
                <td><input type="text" name="driver" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,novalidate:true" /></td>
            </tr>
            <tr>
                <td>手机:</td>
                <td><input type="text" name="phone" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,validType:'length[11,13]',novalidate:true"/></td>
            </tr>
            <tr>
                <td>车路线:</td>
                <td><input type="text" name="busNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车牌号:</td>
                <td><input type="text" name="busPlateNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,novalidate:true" /></td>
            </tr>
            <!--
            <tr>
                <td>安装时间:</td>
                <td><input type="text" name="installTime" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            -->
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" onclick="add();">确认</button>
                </td>
            </tr>
        </table>
    </form:form>
</div>

<div class="easyui-window site_win_big input_big" id="editWin" data-options="title:'修改设备',resizable:false,mode:true,closed:true">
    <div id="errMsg"></div>
    <form id="editForm" method="post" modelAttribute="device">
        <input type="hidden" name="id" />
        <table>
            <tr>
                <td>终端号:</td>
                <td><input type="text" name="code" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>版本:</td>
                <td>
                    <select id="versionId" name="versionId" class="easyui-validatebox easyui-combobox"
                            data-options="editable:false,required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>分组:</td>
                <td>
                    <select id="deviceGroupId" name="deviceGroupId" class="easyui-validatebox easyui-combobox"
                            data-options="editable:false,required:false,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>驾驶员:</td>
                <td><input type="text" name="driver" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,novalidate:true" /></td>
            </tr>
            <tr>
                <td>手机:</td>
                <td><input type="text" name="phone" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,validType:'length[11,13]',novalidate:true"/></td>
            </tr>
            <tr>
                <td>车路线:</td>
                <td><input type="text" name="busNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车牌号:</td>
                <td><input type="text" name="busPlateNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:false,novalidate:true" /></td>
            </tr>
            <!--
            <tr>
                <td>安装时间:</td>
                <td><input type="text" name="installTimeStr" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
            -->
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td><button type="button" onclick="closeWin('editWin');">取消</button></td>
                <td>
                    <button type="button" onclick="edit();">确认</button>
                </td>
            </tr>
        </table>
    </form>
</div>

<div class="easyui-window site_win_big_wider input_big" id="allResWin" style="padding:0;" data-options="title:'所有资源',resizable:false,mode:true,closed:true">
    <table id="resList" class="easyui-datagrid" toolbar="#restb" style="height:100%;"
           data-options="
        method:'get',
                idField:'id',
				rownumbers:true,
				singleSelect:false,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:50,
                rowStyler: function(index,row){
					if (row.deleteStatus == '可删除') {
					    return 'color:green;';
					}
				}">
        <thead>
        <tr>
            <th field="id" checkbox="true" width="50">用户ID</th>
            <th field="resId" width="50">资源ID</th>
            <th field="name" width="200">资源名称</th>
            <th field="resType" width="80">资源类型</th>
            <th field="deleteStatus" width="60">删除状态</th>
            <th field="publishTime" width="135" formatter="formatterDate">发布时间</th>
            <th field="showType" width="80" formatter="formatterShowType">播放模式</th>
            <th field="showCount" width="80">播放次数</th>
            <th field="stayTime" width="80">停留时间(S)</th>
            <th field="startTime" width="80" formatter="formatterDate1">开始日期</th>
            <th field="endTime" width="80" formatter="formatterDate1">结束日期</th>
            <th field="deleteTime" width="135" formatter="formatterDate">删除时间</th>
            <th field="des" width="300">说明</th>
        </tr>
        </thead>
    </table>
    <div id="restb">
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
           onclick="deleteResFromDevice();">从终端删除选择的资源</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
           onclick="deleteAllResFromDevice();">从终端删除所有资源</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
           onclick="deleteResFromAllDevice();">从所有终端删除选择的资源</a>
        <div class="input_small">
            <input type="hidden" id="deviceId" name="deviceId" />
        </div>
    </div>
</div>

</body>
</html>
