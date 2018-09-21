<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
</head>
<style type="text/css">
    html,body,ul,li {
        padding: 0;
        margin: 0;
    }

    ul,li {
        list-style: none;
    }
    .htmlMenu {
        padding: 10px;
    }

    .htmlMenu li {
        float: left;
        width: 150px;
        height: 80px;
        background-color: #e9e9e9;
        margin: 10px;
        position: relative;
        text-align: center;
        line-height: 80px;
    }

    .htmlMenu li>span {
        width: 30px;
        height: 25px;
        color: #fff;
        background-color: #f73f39;
        display: inline-block;
        text-align: center;
        line-height: 25px;
        position: absolute;
        top: 0;
        right: 0;
        box-shadow: 4px 4px 5px #888888;
        border-radius: 10px;
    }
</style>

<body>
<div class="contentMain">
    <ul class="htmlMenu">
        <li>
            1156165
            <span>1</span>
        </li>
        <li>
            2
            <span>10</span>
        <li>3</li>
        <li>4</li>
        <li>5</li>
        <li>6</li>
        <li>7</li>
        <li>8</li>
    </ul>
</div>
</body>

</html>