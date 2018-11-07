<%@ page import="java.net.InetAddress" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>System Info</title>
</head>
<body>

    <%
        String os = System.getProperty("os.name");
        request.setAttribute("os", os);

        InetAddress addr = InetAddress.getLocalHost();
        request.setAttribute("addr", addr);

        String address = addr.getHostName();
        request.setAttribute("address", address);
    %>
    os: ${os}<br/>
    <%--addr: ${addr}<br/>--%>
    address: ${address}<br/>

</body>
</html>
