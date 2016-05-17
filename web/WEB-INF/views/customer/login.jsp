<%--
  Created by IntelliJ IDEA.
  User: WangGenshen
  Date: 5/16/16
  Time: 09:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
  <head>
      <meta charset="utf-8" />
    <title>客户登录</title>
  </head>
  <body>
    <form:form action="/customer/login" method="post" modelAttribute="customer">
        邮箱:<form:input path="email" />
        密码：<form:password path="password" />
        <input type="submit" value="登录" />
        <a href="/customer/reg_page">注册</a>
    </form:form>

  </body>
</html>
