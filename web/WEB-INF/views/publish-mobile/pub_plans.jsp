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
    <title>消息发布列表-青岛宝瑞媒体发布系统</title>
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

    <script src="<%=path %>/js/publish/segment.js"></script>
    <script src="<%=path %>/js/publish/pub_plans-mobile.js"></script>
</head>
<body>
<div id="planLayer" class="layer"></div>
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
                <a href="javascript:void(0);" onclick="toPage('<%=path %>/mob/home')">刷新主页</a>
            </div>
        </div>
    </header>
    <div id="tabs" class="easyui-tabs" data-options="fit:true,border:false">
        <div title="计划">
            <table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
                   data-options="
                    url:'<%=path %>/pubplan/search_pager',
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
                <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
                   onclick="showAdd();">添加</a>
                <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
                   onclick="showEdit();">修改</a>
                <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true"
                   onclick="showPlanDetailMob();">计划详情</a>
                <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-tip" plain="true"
                   onclick="toCheck();">提交审核</a>
                <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
                   onclick="inactive()">冻结</a>
                <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
                   onclick="active()">激活</a>
                <div class="input_small">
                    <form id="searchForm" modalAttribute="publishPlan">
                        终端号:<input type="text" name="deviceCode" class="easyui-textbox"/>
                        资源名称:<input type="text" name="resourceName" class="easyui-textbox"/>
                        <br/>
                        审核状态:<select id="checkSearch" name="checkStatus" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
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
                        状态:<select id="statusSearch" name="status" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
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
                           onclick="searchAll();">查询所有</a>
                        <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"
                           onclick="refreshAll();">刷新</a>
                    </form>
                </div>
            </div>

            <div class="easyui-dialog" id="addWin" data-options="title:'添加计划',resizable:false,mode:true,closed:true">
                <form:form id="addForm" modelAttribute="publishPlan" cssClass="mob-form">
                    <input id="addType" type="hidden" name="type" />
                    <input id="addGroupName" type="hidden" name="groupName" />
                    <input id="addVersionId" type="hidden" name="versionId" />
                    <input id="addVersionName" type="hidden" name="versionName" />
                    <input id="resourceDetails" type="hidden" name="resourceDetails" />
                    <div id="resourceDetailDiv" style="display:none;"></div>
                        <div><input type="text" name="planName" label="计划名称：" class="easyui-validatebox easyui-textbox" data-options="required:true,novalidate:true" style="width: 100%;"/>
                        </div>
                        <input id="addDeviceId" type="hidden" name="deviceId" />
                        <div>
                                <input id="addDeviceCode" type="text" name="deviceCode" label="设备：" class="easyui-validatebox easyui-textbox"
                                       data-options="editable:false,required:true,novalidate:true" style="width: 100%;"/>
                        </div>
                        <div>
                                <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                                   onclick="showDevWin();">选择设备</a>
                        </div>
                        <div>
                        <img id="addVersionImg" onclick="openWin('imgWin');"/>
                        </div>
                        <div>
                            <span>区域资源:</span>
                            <span id="addAreaR"></span>
                        </div>
                        <div><input name="des" label="描述：" class="easyui-textbox" data-options="multiline:true" style="height:100px;width:100%;"/>
                        </div>
                        <div>
                            <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('addWin');">取消</a>
                            &nbsp;&nbsp;
                            <a href="javascript:void(0);" class="easyui-linkbutton" onclick="add();">确认</a>
                        </div>
                </form:form>
            </div>

            <div class="easyui-dialog" id="editWin" data-options="title:'修改消息发布',resizable:false,mode:true,closed:true">
                <div id="errMsg"></div>
                <form id="editForm" method="post" modelAttribute="publishPlan" class="mob-form">
                    <input id="planId" type="hidden" name="id" />
                    <input id="editType" type="hidden" name="type" />
                    <input id="editGroupName" type="hidden" name="groupName" />
                    <input id="editVersionId" type="hidden" name="versionId" />
                    <input id="editVersionName" type="hidden" name="versionName" />
                    <div><input type="text" name="planName" label="计划名称：" class="easyui-validatebox easyui-textbox" data-options="required:true,novalidate:true" style="width: 100%;"/>
                    </div>
                        <input id="editDeviceId" type="hidden" name="deviceId" />
                                <div><input id="editDeviceCode" type="text" name="deviceCode" label="设备：" class="easyui-validatebox easyui-textbox"
                                       data-options="editable:false,required:true,novalidate:true" style="width: 100%;"/>
                                </div>
                    <div>
                                <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                                   onclick="showDevWin();">选择设备</a>
                    </div>
                       <div><img id="editVersionImg" onclick="openWin('imgWin');"/></div>
                        <div>
                            <span>区域资源:</span>
                            <span id="editAreaR"></span>
                        </div>

                        <div><input name="des" label="描述：" class="easyui-textbox" data-options="multiline:true" style="height:100px;width:100%;"/>
                        </div>
                        <div>
                            <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('editWin');">取消</a>
                            &nbsp;&nbsp;
                            <a href="javascript:void(0);" class="easyui-linkbutton" onclick="edit();">确认</a>
                        </div>

                </form>
            </div>
        </div>
    </div>
    <div class="easyui-dialog" id="imgWin" style="width:100%;min-height:400px;" data-options="title:'查看大图',resizable:false,mode:true,closed:true">
        <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('imgWin');">关闭</a>
        <div style="text-align: center; width: 100%;">
            <img id="bigPic" style="max-width: 400px; max-height: 400px;">
        </div>
    </div>
    <div class="easyui-dialog" id="devWin" style="padding:0;min-height:400px;" data-options="title:'选择设备',resizable:false,mode:true,closed:true">
        <table id="devList" class="easyui-datagrid" toolbar="#devtb" style="height:100%;"
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
                <th field="installTimeStr" width="125">安装时间</th>
                <th field="des" width="100">描述</th>
                <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
                <th field="status" width="50" formatter="formatterStatus">状态</th>
            </tr>
            </thead>
        </table>
        <div id="devtb">
            <a href="javascript:void(0);" class="easyui-linkbutton" plain="true"
               onclick="closeWin('devWin');">关闭</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
               onclick="chooseDev('multiple');">确定已选终端</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
               onclick="chooseDev('group');">确定分组终端</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
               onclick="chooseDev('all');">确定指定版本的全部终端</a>
            <div class="input_small">
                <form id="devSearchForm" modalAttribute="device">
                    终端号:<input type="text" name="code" class="easyui-textbox"/>
                    分组:<select id="deviceGroupId" name="deviceGroupId" class="easyui-combobox"
                               data-options="url:'<%=path %>/devgroup/list_combo/all/search',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
                    <br/>
                    版本:<select id="versionId" name="versionId" class="easyui-combobox"
                               data-options="url:'<%=path %>/version/list_combo/0/all',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
                    <%--
                    <br />
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
                --%>
                    <br/>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                       onclick="doSearchDev();">搜索</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search'"
                       onclick="searchAllDev();">查询所有</a>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"
                       onclick="refreshAllDev();">刷新</a>
                </form>
            </div>
        </div>
    </div>
    <div class="easyui-dialog" id="chosenResWin" style="padding:0;min-height:400px;" data-options="title:'已选资源',resizable:false,mode:true,closed:true">
        <table id="chresList" class="easyui-datagrid" toolbar="#chrestb" style="height:100%;"
               data-options="
                    rownumbers:true,
                    singleSelect:true,
                    autoRowHeight:false,
                    pagination:false,
                    border:false,
                    checkbox:false,
                    pageSize:50,
                    rowStyler: function(index,row){
                        if (row.status == 'N') {
                            return 'color:red;';
                        }
                    }
                    ">
            <thead>
            <tr>
                <th field="resourceId" checkbox="true" width="50">用户ID</th>
                <th field="resourceName" width="80">名称</th>
                <th field="resourceType" width="80">类型</th>
                <th field="area" width="60" formatter="formatterArea">显示区域</th>
                <th field="showType" width="80" formatter="formatterShowType">播放模式</th>
                <th field="startTimeStr" width="120" formatter="formatterDate1">开始日期</th>
                <th field="endTimeStr" width="120" formatter="formatterDate1">结束日期</th>
                <th field="stayTime" width="70">停留时间(S)</th>
                <th field="showCount" width="70">播放次数</th>
                <th field="segments" width="150">时段</th>
            </tr>
            </thead>
        </table>
        <div id="chrestb">
            <a href="javascript:void(0);" class="easyui-linkbutton" plain="true" onclick="closeWin('chosenResWin');">关闭</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
               onclick="showResWin();">添加</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
               onclick="showResEdit();">修改</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
               onclick="deleteRes();">删除</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
               onclick="confirmAllResAndClose();">确认所有资源</a>
        </div>
    </div>
    <div class="easyui-dialog" id="resWin" style="padding:0;min-height:400px;" data-options="title:'选择资源',resizable:false,mode:true,closed:true">
        <table id="resList" class="easyui-datagrid" toolbar="#restb" style="height:100%;"
               data-options="
            method:'get',
                    rownumbers:true,
                    singleSelect:true,
                    autoRowHeight:false,
                    pagination:true,
                    border:false,
                    checkbox:false,
                    pageSize:50,
                    rowStyler: function(index,row){
                        if (row.status == 'N') {
                            return 'color:red;';
                        }
                    }
                    ">
            <thead>
            <tr>
                <th field="id" checkbox="true" width="50">用户ID</th>
                <th field="name" width="80">名称</th>
                <th field="resourceType" width="80" formatter="formatterName">类型</th>
                <th field="fileName" width="150" formatter="formatterLong">文件名</th>
                <th field="path" width="200" formatter="formatterLong">路径</th>
                <th field="showDetailSetting" formatter="formatterYN">是否显示详情设置</th>
                <th field="des" width="100">描述</th>
                <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
                <th field="status" width="50" formatter="formatterStatus">状态</th>
            </tr>
            </thead>
        </table>
        <div id="restb">
            <a href="javascript:void(0);" class="easyui-linkbutton" plain="true" onclick="closeWin('resWin');">关闭</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
               onclick="addResourceToArea();">添加到区域</a>
            <div class="input_small">
                <form id="resSearchForm" modalAttribute="resource">
                    类型:<select id="resTypeSearch" name="resourceTypeId" class="easyui-combobox"
                               data-options="url:'<%=path %>/restype/list_combo/all',method:'get',valueField:'id',textField:'text',panelHeight:'auto',editable:false"></select>
                    状态:<select id="resStatusSearch" name="status" class="easyui-combobox" data-options="valueField: 'id',textField: 'text',panelHeight:'auto',
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
    </div>

    <div class="easyui-dialog" style="min-height:400px;" id="detailWin" data-options="title:'设置详情',resizable:false,mode:true,closed:true">
        <form:form id="detailForm" class="mob-form">
            <input id="resourceId" type="hidden" name="resourceId" />
            <input id="segments" type="hidden" />
            <div><select id="showType" name="showType" label="播放模式：" class="easyui-combobox" data-options="editable:false, valueField: 'id',textField: 'text',panelHeight:'auto',
                                data: [{
                                    id: 'order',
                                    text: '顺序播放'
                                },{
                                    id: 'segment',
                                    text: '时段播放'
                                }],required:true,novalidate:true" style="width:100%;"></select>
            </div>
            <div>
                        <a id="setSegment" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
                           onclick="showAddSegmentWin();" style="display: none;">设置时段</a>
            </div>

                <div><input id="startTimeStr" type="text" name="startTimeStr" label="开始日期：" class="easyui-datebox" data-options="editable:false,required:true,novalidate:true" style="width:100%;"/>
                </div>
                <div><input id="endTimeStr" type="text" name="endTimeStr" label="结束日期：" class="easyui-datebox" data-options="editable:false,required:true,novalidate:true" style="width:100%;"/>
                </div>
                        <div id="stayTimeTR" style="display: none;"><input id="stayTime" type="text" name="stayTime" label="停留时间（S）：" class="easyui-validatebox easyui-numberbox" style="width:100%;"/>
                        </div>
                        <div id="showCountTR" style="display: none;"><input id="showCount" type="text" name="showCount" label="播放次数：" class="easyui-validatebox easyui-numberbox" style="width:100%;"/>
                        </div>
            <div>
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('detailWin');">取消</a>
                &nbsp;&nbsp;
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="confirmAddResourceToArea(true);">确认</a>
            </div>

        </form:form>
    </div>

    <div class="easyui-window" id="addSegment" style="width:90%;min-height:400px;overflow: scroll;" data-options="title:'设置时段',resizable:false,mode:true,closed:true">
        <input type="hidden" id="segBeginIndex" value="0" />
        <form id="addSegmentForm" modelAttribute="publishPlan">
            <table>
                <tr style="display:none;">
                    <td>时段1:</td>
                    <td>
                        <input id="startTime0" name="segments[0].startTime" style="width:120px;"/>
                        -
                        <input id="endTime0" name="segments[0].endTime" style="width:120px;"/>
                    </td>
                    <td>时段2:</td>
                    <td>
                        <input id="startTime1" name="segments[1].startTime" style="width:120px;"/>
                        -
                        <input id="endTime1" name="segments[1].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段3:</td>
                    <td>
                        <input id="startTime2" name="segments[2].startTime" style="width:120px;"/>
                        -
                        <input id="endTime2" name="segments[2].endTime" style="width:120px;"/>
                    </td>
                    <td>时段4:</td>
                    <td>
                        <input id="startTime3" name="segments[3].startTime" style="width:120px;"/>
                        -
                        <input id="endTime3" name="segments[3].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段5:</td>
                    <td>
                        <input id="startTime4" name="segments[4].startTime" style="width:120px;"/>
                        -
                        <input id="endTime4" name="segments[4].endTime" style="width:120px;"/>
                    </td>
                    <td>时段6:</td>
                    <td>
                        <input id="startTime5" name="segments[5].startTime" style="width:120px;"/>
                        -
                        <input id="endTime5" name="segments[5].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段7:</td>
                    <td>
                        <input id="startTime6" name="segments[6].startTime" style="width:120px;"/>
                        -
                        <input id="endTime6" name="segments[6].endTime" style="width:120px;"/>
                    </td>
                    <td>时段8:</td>
                    <td>
                        <input id="startTime7" name="segments[7].startTime" style="width:120px;"/>
                        -
                        <input id="endTime7" name="segments[7].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段9:</td>
                    <td>
                        <input id="startTime8" name="segments[8].startTime" style="width:120px;"/>
                        -
                        <input id="endTime8" name="segments[8].endTime" style="width:120px;"/>
                    </td>
                    <td>时段10:</td>
                    <td>
                        <input id="startTime9" name="segments[9].startTime" style="width:120px;"/>
                        -
                        <input id="endTime9" name="segments[9].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段11:</td>
                    <td>
                        <input id="startTime10" name="segments[10].startTime" style="width:120px;"/>
                        -
                        <input id="endTime10" name="segments[10].endTime" style="width:120px;"/>
                    </td>
                    <td>时段12:</td>
                    <td>
                        <input id="startTime11" name="segments[11].startTime" style="width:120px;"/>
                        -
                        <input id="endTime11" name="segments[11].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段13:</td>
                    <td>
                        <input id="startTime12" name="segments[12].startTime" style="width:120px;"/>
                        -
                        <input id="endTime12" name="segments[12].endTime" style="width:120px;"/>
                    </td>
                    <td>时段14:</td>
                    <td>
                        <input id="startTime13" name="segments[13].startTime" style="width:120px;"/>
                        -
                        <input id="endTime13" name="segments[13].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段15:</td>
                    <td>
                        <input id="startTime14" name="segments[14].startTime" style="width:120px;"/>
                        -
                        <input id="endTime14" name="segments[14].endTime" style="width:120px;"/>
                    </td>
                    <td>时段16:</td>
                    <td>
                        <input id="startTime15" name="segments[15].startTime" style="width:120px;"/>
                        -
                        <input id="endTime15" name="segments[15].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段17:</td>
                    <td>
                        <input id="startTime16" name="segments[16].startTime" style="width:120px;"/>
                        -
                        <input id="endTime16" name="segments[16].endTime" style="width:120px;"/>
                    </td>
                    <td>时段18:</td>
                    <td>
                        <input id="startTime17" name="segments[17].startTime" style="width:120px;"/>
                        -
                        <input id="endTime17" name="segments[17].endTime" style="width:120px;" />
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段19:</td>
                    <td>
                        <input id="startTime18" name="segments[18].startTime" style="width:120px;"/>
                        -
                        <input id="endTime18" name="segments[18].endTime" style="width:120px;"/>
                    </td>
                    <td>时段20:</td>
                    <td>
                        <input id="startTime19" name="segments[19].startTime" style="width:120px;"/>
                        -
                        <input id="endTime19" name="segments[19].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段21:</td>
                    <td>
                        <input id="startTime20" name="segments[20].startTime" style="width:120px;"/>
                        -
                        <input id="endTime20" name="segments[20].endTime" style="width:120px;"/>
                    </td>
                    <td>时段22:</td>
                    <td>
                        <input id="startTime21" name="segments[21].startTime" style="width:120px;"/>
                        -
                        <input id="endTime21" name="segments[21].endTime" style="width:120px;"/>
                    </td>
                </tr>
                <tr style="display:none;">
                    <td>时段23:</td>
                    <td>
                        <input id="startTime22" name="segments[22].startTime" style="width:120px;"/>
                        -
                        <input id="endTime22" name="segments[22].endTime" style="width:120px;"/>
                    </td>
                    <td>时段24:</td>
                    <td>
                        <input id="startTime23" name="segments[23].startTime" style="width:120px;"/>
                        -
                        <input id="endTime23" name="segments[23].endTime" style="width:120px;"/>
                    </td>
                </tr>
            </table>
            <div>
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="confirmSegment();">确认</a>
                &nbsp;&nbsp;

                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="cancelSegment();">取消</a>
                &nbsp;&nbsp;
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="moreSegment();">更多</a>

            </div>
        </form>
    </div>
    <footer style="padding:2px 3px;text-align: center;">
        青岛宝瑞媒体发布系统V1.0<br />
        地址：山东省青岛市崂山区株洲路140号&nbsp;&nbsp;<br />技术支持:0532-80678775/80678776
    </footer>
</div>
</body>
</html>
