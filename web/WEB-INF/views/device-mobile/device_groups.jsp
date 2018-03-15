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

    <script src="<%=path %>/js/device/device_groups.js"></script>
</head>
<body>
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
            <div>
                <a href="<%=path %>/mob/logout">安全退出</a>
            </div>
        </div>
    </header>
    <table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
           data-options="
            url:'<%=path %>/devgroup/list_pager',
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
            <th field="name" width="100">名称</th>
            <th field="des" width="100">描述</th>
            <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
            <th field="status" width="50" formatter="formatterStatus">状态</th>
        </tr>
        </thead>
    </table>
    <div id="tb">
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
           onclick="openWin('addWin');">添加</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
           onclick="showEdit();">修改</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
           onclick="inactive()">冻结</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
           onclick="active()">激活</a>
    </div>

    <div class="easyui-dialog" id="addWin" data-options="title:'添加终端分组',resizable:false,mode:true,closed:true">
        <form:form id="addForm" modelAttribute="deviceGroup" cssClass="mob-form">
            <div><input type="text" name="name" label="名称：" class="easyui-validatebox easyui-textbox"
                               data-options="required:true,novalidate:true" style="width: 100%;"/>
            </div>
                <div><input name="des" label="描述：" class="easyui-textbox" data-options="multiline:true" style="height:100px;width:100%;"/>
                </div>
            <div>
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('addWin');">取消</a>
                &nbsp;&nbsp;
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="addGroup();">确认</a>
            </div>
        </form:form>
    </div>

    <div class="easyui-dialog" id="editWin" data-options="title:'修改终端分组',resizable:false,mode:true,closed:true">
        <div id="errMsg"></div>
        <form id="editForm" method="post" modelAttribute="deviceGroup" class="mob-form">
            <input type="hidden" name="id" />
            <div><input type="text" name="name" label="名称：" class="easyui-validatebox easyui-textbox"
                               data-options="required:true,novalidate:true" style="width: 100%;"/>
            </div>
                <div><input name="des" label="描述：" class="easyui-textbox" data-options="multiline:true" style="height:100px;width:100%;"/>
                </div>
            <div>
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('editWin');">取消</a>
                &nbsp;&nbsp;
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="editGroup();">确认</a>
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
