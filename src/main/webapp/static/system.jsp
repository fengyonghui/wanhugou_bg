<%@ page import="java.net.InetAddress" %>
<%@ page import="java.util.concurrent.ThreadPoolExecutor" %>
<%@ page import="java.util.concurrent.ExecutorService" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ page import="java.util.concurrent.LinkedBlockingQueue" %>
<%@ page import="java.util.concurrent.ThreadFactory" %>
<%@ page import="com.wanhutong.backend.common.thread.ThreadPoolManager" %>
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

    ThreadPoolExecutor tpe = ThreadPoolManager.getDefaultThreadPool();

    // 默认线程池当前排队线程数
    int queueSize = tpe.getQueue().size();
    request.setAttribute("queueSize", queueSize);

    // 默认线程池当前活动线程数
    int activeCount = tpe.getActiveCount();
    request.setAttribute("activeCount", activeCount);

    // 默认线程池执行完成线程数
    long completedTaskCount = tpe.getCompletedTaskCount();
    request.setAttribute("completedTaskCount", completedTaskCount);

    // 默认线程池总线程数
    long taskCount = tpe.getTaskCount();
    request.setAttribute("taskCount", taskCount);

    // 剩余内存 
    Runtime r = Runtime.getRuntime();
    long freeMemory = r.freeMemory();
    request.setAttribute("freeMemory", freeMemory / (1024 * 1024) + "M");
    long totalMemory = r.totalMemory();
    request.setAttribute("totalMemory", totalMemory / (1024 * 1024) + "M");
    long maxMemory = r.maxMemory();
    request.setAttribute("maxMemory", maxMemory / (1024 * 1024) + "M");
    request.setAttribute("usedMemory", (r.totalMemory() - r.freeMemory()) / (1024 * 1024) + "M");
%>

os: ${os}<br/>
<%--addr: ${addr}<br/>--%>
address: ${address}<br/>
queueSize: ${queueSize}<br/>
activeCount: ${activeCount}<br/>
completedTaskCount: ${completedTaskCount}<br/>
taskCount: ${taskCount}<br/>
freeMemory: ${freeMemory}<br/>
totalMemory: ${totalMemory}<br/>
usedMemory: ${usedMemory}<br/>
maxMemory: ${maxMemory}<br/>
cpu: ${cpuInfo}<br/>

</body>
</html>
