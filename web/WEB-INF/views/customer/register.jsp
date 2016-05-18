<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/17/16
  Time: 14:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
    String path = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <title>客户注册-青岛宝瑞无线广告管理系统</title>
    <style type="text/css">
        label{
            width:120px;
            display:block;
        }
    </style>
    <link rel="stylesheet" type="text/css" href="../js/jquery-easyui/themes//default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../js/jquery-easyui/themes//icon.css">
    <link rel="stylesheet" type="text/css" href="../js/jquery-easyui/demo.css">
    <script type="text/javascript" src="../js/jquery-easyui/jquery.min.js"></script>
    <script type="text/javascript" src="../js/jquery-easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/jquery-easyui/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript">
        function loadimage(){
            // document.getElementById("randImage").src = "image.jsp";
            // 注意：这里加了?Math.random()因为有些浏览器会认为src没有变化，不会提交！！firefox下不会提交！！
            document.getElementById("randImage").src = "../js/image.jsp?"+Math.random();
        }
        function checkCode(){

            var txtCode=document.getElementById("txtCode").value;
            var codenull = document.getElementById("code    null");
            if(txtCode == null || txtCode == ""){
                codenull.innerHTML = "";
                return false;
            }else
                codenull.innerHTML = "";
            return true;
        }



    </script>
</head>
<body>
<h2>新增管理员</h2>
<div class="demo-info">
    <div class="demo-tip icon-tip"></div>
    <div>请安要求完成管理员注册</div>
</div>

<div style="margin:10px 0;"></div>
<div style="padding:10px;width:300px;height:300px;">
    <form:form action="/customer/reg" method="post" modelAttribute="customer">
        <div>
            <label for="name">用户名:</label>
                <form:input path="email" class="easyui-validatebox" type="email" name="email" data-options="required:true"/>
        </div>
        <div>
            <label>密码:</label>
            <form:password path="password" id="password" name="password" validType="length[4,32]" class="easyui-validatebox" required="true" value=""/>
        </div>
        <div>
           <%--<form:input path="lastLoginTime" val/>--%>
        </div>

        <div>
            <label for="subject">验证码:</label>

            <input name="txtCode" id="txtCode" class="reg_input_tcode_text" type="text" maxlength="4" onblur="checkCode()"/>
            <a href="javascript:loadimage();">
                <img alt="点击换一张" name="randImage" id="randImage" src="../js/image.jsp" style="cursor:pointer;vertical-align:middle;margin-left:1px; margin-top:-3px;height:30px;" />
            </a>
            <font style="color:green;font-size: 17px">${ms}</font>
        </div>


        <div>
            <input type="reset" value="重新填写">
            <input type="submit" value="提交数据">
        </div>
    </form:form>
</div>

</body>
</html>
