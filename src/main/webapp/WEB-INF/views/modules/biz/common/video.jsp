<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/7/17/017
  Time: 11:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>视频</title>
</head>
<body>
<video src="${src}" autoplay="autoplay" controls="controls"></video>
</body>
</html>