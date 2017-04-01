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
    <title>客户列表-青岛宝瑞媒体发布系统</title>
    <meta charset="UTF-8"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/icon.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>

    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
    <script src="<%=path %>/js/site_easyui.js"></script>

    <script src="<%=path %>/js/resource/resource_types.js"></script>
</head>
<body>
<table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
       data-options="
        url:'<%=path %>/restype/list_pager',
        method:'get',
				rownumbers:true,
				singleSelect:true,
				autoRowHeight:false,
				pagination:true,
				border:false,
				pageSize:50,
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
        <th field="name" width="80">名称</th>
        <th field="extension" width="100">可用后缀</th>
        <th field="showDetailSetting" width="100" formatter="formatterYN">是否显示详情设置</th>
        <th field="des" width="100">描述</th>
        <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
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
</div>

<div class="easyui-window site_win_normal input_big" id="addWin" data-options="title:'添加资源类型',resizable:false,mode:true,closed:true">
    <form:form id="addForm" modelAttribute="resourceType">
        <table>
            <tr>
                <td>名称:</td>
                <td><input type="text" name="name" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>可用后缀:</td>
                <td><input type="text" name="extension" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>显示详情设置:</td>
                <td>
                    <select name="showDetailSetting" class="easyui-combobox" data-options="editable:false, valueField: 'id',textField: 'text',panelHeight:'auto',
                            data: [{
                                id: 'Y',
                                text: '显示'
                            },{
                                id: 'N',
                                text: '不显示'
                            }],required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <button type="button" onclick="addType();">确认</button>
                </td>
            </tr>
        </table>
    </form:form>
</div>

<div class="easyui-window site_win_normal input_big" id="editWin" data-options="title:'修改资源类型',resizable:false,mode:true,closed:true">
    <div id="errMsg"></div>
    <form id="editForm" method="post" modelAttribute="resourceType">
        <input type="hidden" name="id" />
        <table>
            <tr>
                <td>名称:</td>
                <td><input type="text" name="name" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>可用后缀:</td>
                <td><input type="text" name="extension" class="easyui-validatebox easyui-textbox"
                           data-options="required:true,novalidate:true"/></td>
            </tr>
            <tr>
                <td>显示详情设置:</td>
                <td>
                    <select id="editShowDetailSetting" name="showDetailSetting" class="easyui-combobox" data-options="editable:false, valueField: 'id',textField: 'text',panelHeight:'auto',required:true,novalidate:true"></select>
                </td>
            </tr>
            <tr>
                <td>描述:</td>
                <td><input name="des" class="easyui-textbox" data-options="multiline:true" style="height:100px;"/></td>
            </tr>
            <tr>
                <td><button type="button" onclick="closeWin('editWin');">取消</button></td>
                <td>
                    <button type="button" onclick="editType();">确认</button>
                </td>
            </tr>
        </table>
    </form>
</div>

</body>
</html>
