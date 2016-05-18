<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/16/16
  Time: 09:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%
    String path = request.getContextPath();
%>
<!doctype html>
<html>
<head>
    <title>管理员登录-青岛宝瑞无线广告管理系统</title>
    <meta charset="UTF-8" />
    <link rel="stylesheet" href="<%=path %>/js/jquery-easyui/themes/default/easyui.css"/>
    <link rel="stylesheet" href="<%=path %>/css/site_main.css"/>
    <script src="<%=path %>/js/jquery.min.js"></script>
    <script src="<%=path %>/js/jquery.form.js"></script>
    <script src="<%=path %>/js/jquery-easyui/jquery.easyui.min.js"></script>
    <script src="<%=path %>/js/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>

    <script>
        function login() {
            $.post("<%=path %>/admin/login",
                    $("#login_form").serialize(),
                    function (data) {
                        var result = data.result;
                        if(result == "success") {
                            window.location.href = "<%=path %>/admin/home";
                        } else {
                            $("#errMsg").html(data.message);
                        }
                    }
            );
        }
    </script>
</head>
<body>
<div class="login_container">
    <img class="login_logo" src="<%=path %>/images/logo.jpg" alt="公司图片" title="公司图片"/>

    <div class="login_div">
        <div class="easyui-panel" title="管理员登录" style="width:240px;padding:10px;">
            <div id="errMsg"></div>
            <form:form id="login_form" method="post" modelAttribute="admin">
                <table>
                    <tr>
                        <td>邮箱:</td>
                        <td><input type="text" name="email" class="easyui-textbox"/></td>
                    </tr>
                    <tr>
                        <td>密码:</td>
                        <td><input type="password" name="password" class="easyui-textbox"/></td>
                    </tr>
                    <tr>
                        <td>验证码:</td>
                        <td><input type="text" name="checkCode" class="easyui-textbox"/></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><img src="<%=path %>/captcha" onclick="this.src='<%=path %>/captcha?time=Math.random();'"/>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <button type="button" onclick="login();">登录</button>
                        </td>
                    </tr>
                </table>
            </form:form>
        </div>
    </div>
    <div style="clear:both;"></div>
</div>
</body>
</html>