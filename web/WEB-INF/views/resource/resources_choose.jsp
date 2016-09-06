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
				singleSelect:false,
				autoRowHeight:false,
				pagination:true,
				border:false,
				checkbox:false,
				pageSize:20,
				rowStyler: function(index,row){
					if (row.status == 'N') {
					    return 'color:red;';
					}
				},
				onRowContextMenu:resContextMenu
                ">
    <thead>
    <tr>
        <th field="id" checkbox="true" width="50">用户ID</th>
        <th field="name" width="80">名称</th>
        <th field="resourceType" width="80" formatter="formatterType">类型</th>
        <th field="fileName" width="150">文件名</th>
        <th field="path" width="200" formatter="formatterLong">路径</th>
        <th field="des" width="100">描述</th>
        <th field="createTime" width="120" formatter="formatterDate">创建时间</th>
        <th field="status" width="50" formatter="formatterStatus">状态</th>
    </tr>
    </thead>
</table>
<div id="restb">
    <div id="resMenu" class="easyui-menu" style="width:80px;display:none; ">
        <div onclick="addResourceToArea();">添加到区域</div>
        <div onclick="addResourceToArea();">从区域移除</div>
    </div>
    <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
       onclick="chooseRes();">确认</a>
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

<div class="easyui-window site_win_small input_big" id="detailWin" data-options="title:'设置详情',resizable:false,mode:true,closed:true">
    <form:form id="detailForm">
        <table>
            <tr>
                <td>播放模式:</td>
                <td><select id="showType" name="showType" class="easyui-combobox" data-options="editable:false, valueField: 'id',textField: 'text',panelHeight:'auto',
                            data: [{
                                id: 'now',
                                text: '即时播放'
                            },{
                                id: 'order',
                                text: '顺序播放'
                            },{
                                id: 'segment',
                                text: '时段播放'
                            }]"></select>
                    <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-edit'"
                       onclick="showAddSegmentWin('');">设置时段</a>
                </td>
            </tr>
            <tr>
                <td>开始日期:</td>
                <td><input id="startTime" type="text" name="startTime" class="easyui-datebox" data-options="editable:false"/></td>
            </tr>
            <tr>
                <td>结束日期:</td>
                <td><input id="endTime" type="text" name="endTime" class="easyui-datebox" data-options="editable:false"/></td>
            </tr>
            <tr id="stayTimeTR" style="display:none">
                <td>停留时间(S):</td>
                <td><input id="stayTime" type="text" name="stayTime" class="easyui-numberbox easyui-textbox"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button id="addPlan" type="button" onclick="confirmAddResourceToArea();">确认</button>
                </td>
            </tr>
        </table>
    </form:form>
</div>

<div class="easyui-window site_win_normal_wider input_small" id="addSegment" data-options="title:'设置时段',resizable:false,mode:true,closed:true">
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
            <tr>
                <td>
                    <button id="confirmSeg" type="button" onclick="confirmSegment();">确认</button>
                </td>
                <td>
                    <button id="cancelSeg" type="button" onclick="cancelSegment();">取消</button>
                </td>
                <td>
                    <button id="moreSeg" type="button" onclick="moreSegment();">更多</button>
                </td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>
