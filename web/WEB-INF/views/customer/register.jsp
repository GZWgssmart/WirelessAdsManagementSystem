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
    <title>用户注册</title>
</head>
<body>
    <form:form action="/customer/reg" method="post" modelAttribute="customer">
        邮箱：<form:input path="email" type="email" value="aaaa"/>
        密码：<form:password path="password" />
        <form:input path="lastLoginTime" />
        <input type="submit" value="注册" />
    </form:form>
</body>
</html>
