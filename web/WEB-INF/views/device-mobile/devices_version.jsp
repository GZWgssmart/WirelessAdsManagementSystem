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

    <script src="<%=path %>/js/device/device_version.js"></script>
</head>
<body>
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
        <th field="installTimeStr" width="120">安装时间</th>
        <th field="des" width="100">描述</th>
        <th field="createTime" width="130 formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
       onclick="openWinFitPos('addWin');">添加</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
       onclick="showEdit();">修改</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
       onclick="inactive()">冻结</a>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="active()">激活</a>
    <div class="input_small">
        <form id="searchForm" modalAttribute="device">
            <input id="versionId" type="hidden" name="versionId" value="${versionId }" />
            终端号:<input type="text" name="code" class="easyui-textbox"/>
            分组:<select name="deviceGroupId" class="easyui-combobox"
                       data-options="url:'<%=path %>/devgroup/list_combo/all/search',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>

            <br/>
            是否在线:<select name="online" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
                    data: [{
                        id: 'Y',
                        text: '在线'
                    },{
                        id: 'N',
                        text: '离线'
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
            <br/>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="doSearch();">搜索</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
               onclick="searchAll('${versionId }');">查询所有</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"
               onclick="refreshAll();">刷新</a>
        </form>
    </div>
</div>

<div class="easyui-window site_win_big input_big" id="addWin" data-options="title:'添加设备',resizable:false,mode:true,closed:true">
    <form:form id="addForm" modelAttribute="device">
        <input type="hidden" name="versionId" value="${versionId }" />
        <table>
            <tr>
                <td>终端号:</td>
                <td><input type="text" name="code" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>分组:</td>
                <td>
                    <select name="deviceGroupId" class="easyui-validatebox easyui-combobox"
                            data-options="url:'<%=path %>/devgroup/list_combo/Y/add',method:'get',valueField:'id',textField:'text',
                           panelHeight:'auto',editable:false,required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>驾驶员:</td>
                <td><input type="text" name="driver" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>手机:</td>
                <td><input type="text" name="phone" class="easyui-numberbox easyui-textbox"
                           data-options="required:true,validType:'length[11,11]',novalidate:true"/></td>
            </tr>
            <tr>
                <td>车路线:</td>
                <td><input type="text" name="busNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车牌号:</td>
                <td><input type="text" name="busPlateNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>安装时间:</td>
                <td><input type="text" name="installTime" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
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
        <input type="hidden" name="versionId" value="${versionId }" />
        <table>
            <tr>
                <td>终端号:</td>
                <td><input type="text" name="code" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>分组:</td>
                <td>
                    <select id="deviceGroupId" name="deviceGroupId" class="easyui-validatebox easyui-combobox"
                            data-options="editable:false,required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>驾驶员:</td>
                <td><input type="text" name="driver" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>手机:</td>
                <td><input type="text" name="phone" class="easyui-numberbox easyui-textbox"
                           data-options="required:true,validType:'length[11,11]',novalidate:true"/></td>
            </tr>
            <tr>
                <td>车路线:</td>
                <td><input type="text" name="busNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>车牌号:</td>
                <td><input type="text" name="busPlateNo" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true" /></td>
            </tr>
            <tr>
                <td>安装时间:</td>
                <td><input type="text" name="installTimeStr" class="easyui-datetimebox" data-options="editable:false"/></td>
            </tr>
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

</body>
</html>
