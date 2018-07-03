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

    <script src="<%=path %>/js/customer/customers.js"></script>
</head>
<body>
<div class="easyui-navpanel">
    <header>
        <div class="m-toolbar">
            <div class="m-title">
                <b>${sessionScope.admin.email }</b>
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
    <table id="list" class="easyui-datagrid" toolbar="#tb" style="height:100%;"
           data-options="
            url:'<%=path %>/customer/search_pager',
            method:'get',
                    rownumbers:true,
                    singleSelect:true,
                    autoRowHeight:false,
                    pagination:true,
                    border:false,
                    pageSize:50,
                    pageList: [40, 50, 60, 70],
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
            <th field="email" width="150">邮箱</th>
            <th field="name" width="100">姓名</th>
            <th field="phone" width="100">手机号</th>
            <th field="company" width="100">公司</th>
            <th field="address" width="200">地址</th>
            <th field="createTime" width="135" formatter="formatterDate">创建时间</th>
            <th field="loginTime" width="135" formatter="formatterDate">最近登录时间</th>
            <th field="status" width="50" formatter="formatterStatus">状态</th>
        </tr>
        </thead>
    </table>
    <div id="tb">
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true"
           onclick="openWin('addWin');">添加</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
           onclick="showEdit();">修改</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
           onclick="showUpdatePwd();">修改密码</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true"
           onclick="showUpdateCheckPwd();">修改审核密码</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true"
           onclick="inactive()">冻结</a>
        <a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-ok" plain="true"
           onclick="active()">激活</a>
        <div class="input_small">
            <form id="searchForm" modalAttribute="customer">
                邮箱:<input type="email" name="email" class="easyui-textbox"/>
                姓名:<input type="text" name="name" class="easyui-textbox"/>
                <br/>
                手机:<input type="text" name="phone" class="easyui-textbox"/>
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

    <div class="easyui-dialog" id="addWin" data-options="title:'添加客户',resizable:false,mode:true,closed:true">
        <form:form id="addForm" modelAttribute="customer" cssClass="mob-form">
            <div><input type="text" name="email" label="邮箱：" class="easyui-validatebox easyui-textbox"
                               data-options="required:true,validType:'email',novalidate:true" style="width:100%;"/>
            </div>
                <div><input type="password" name="password" label="密码：" class="easyui-validatebox easyui-textbox"
                               data-options="required:true,validType:'length[6,20]',novalidate:true" style="width:100%;"/>
                </div>
                <div><input type="password" name="checkPwd" label="审核密码：" class="easyui-validatebox easyui-textbox"
                               data-options="required:true,validType:'length[6,20]',novalidate:true" style="width:100%;"/>
                </div>
                <div><input type="text" name="name" label="姓名：" class="easyui-validatebox easyui-textbox"
                               data-options="required:false,novalidate:true" style="width:100%;"/>
                </div>
                <div><input type="text" name="phone" label="手机：" class="easyui-validatebox easyui-textbox"
                               data-options="required:false,validType:'length[11,13]',novalidate:true" style="width:100%;"/>
                </div>
                <div><input type="text" name="company" label="公司：" class="easyui-textbox" style="width:100%;"/>
                </div>
                <div><input type="text" name="address" label="地址：" class="easyui-textbox" style="width:100%;"/>
                </div>
            <div>
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('addWin');">取消</a>
                &nbsp;&nbsp;
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="add();">确认</a>
            </div>
        </form:form>
    </div>

    <div class="easyui-dialog" id="editWin" data-options="title:'修改账号信息',resizable:false,mode:true,closed:true">
        <div id="errMsg"></div>
        <form id="editForm" method="post" modelAttribute="customer" class="mob-form">
            <input type="hidden" name="id" />
            <div>
            <input type="text" name="email" label="邮箱：" class="easyui-textbox" readonly="true" style="width:100%;"/>
            </div>
            <div><input type="text" name="name" label="姓名：" class="easyui-validatebox easyui-textbox"
                        data-options="required:false,novalidate:true" style="width:100%;"/>
            </div>
            <div><input type="text" name="phone" label="手机：" class="easyui-validatebox easyui-textbox"
                        data-options="required:false,validType:'length[11,13]',novalidate:true" style="width:100%;"/>
            </div>
            <div><input type="text" name="company" label="公司：" class="easyui-textbox" style="width:100%;"/>
            </div>
            <div><input type="text" name="address" label="地址：" class="easyui-textbox" style="width:100%;"/>
            </div>
            <div>
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('editWin');">取消</a>
                &nbsp;&nbsp;
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="edit();">确认</a>
            </div>
        </form>
    </div>

    <div class="easyui-dialog" id="editPwdWin" data-options="title:'修改客户密码',resizable:false,mode:true,closed:true">
        <div id="errMsg"></div>
        <form:form id="editPwdForm" method="post" modelAttribute="admin" cssClass="mob-form">
            <input type="hidden" name="id" />
            <div><input id="update_password" type="password" name="password" label="新密码：" class="easyui-validatebox easyui-textbox"
                               data-options="required:true,validType:'length[6,20]',novalidate:true" style="width:100%;"/>
            </div>
                <div>
                    <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('editPwdWin');">取消</a>
                    &nbsp;&nbsp;
                    <a href="javascript:void(0);" class="easyui-linkbutton" onclick="updatePwd();">确认</a>
                </div>
        </form:form>
    </div>

    <div class="easyui-dialog" id="editCheckPwdWin" data-options="title:'修改客户审核密码',resizable:false,mode:true,closed:true">
        <div id="errMsg"></div>
        <form:form id="editCheckPwdForm" method="post" modelAttribute="admin" cssClass="mob-form">
            <input type="hidden" name="id" />
            <div><input id="update_checkPwd" type="password" name="checkPwd" label="新审核密码：" class="easyui-validatebox easyui-textbox"
                               data-options="required:true,validType:'length[6,20]',novalidate:true" style="width:100%;"/>
            </div>
            <div>
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="closeWin('editCheckPwdWin');">取消</a>
                &nbsp;&nbsp;
                <a href="javascript:void(0);" class="easyui-linkbutton" onclick="updateCheckPwd();">确认</a>
            </div>
        </form:form>
    </div>
    <footer style="padding:2px 3px;text-align: center;">
        青岛宝瑞媒体发布系统V1.0<br />
        地址：山东省青岛市崂山区株洲路140号&nbsp;&nbsp;<br />技术支持:0532-80678775/80678776
    </footer>
</div>

</body>
</html>
