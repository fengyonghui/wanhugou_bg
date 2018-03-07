<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>订单统计</title>
</head>
<body>
<div id="orderTotalDataChart" style="height: 300px"></div>

</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript">

    $.ajax({
        type: 'GET',
        url: "${adminPath}/biz/statistics/orderData",
        data: {"month":"2018-03"},
        dataType: "json",
        success: function(msg) {
            var salesVolumeChart = echarts.init(document.getElementById('orderTotalDataChart'));
            salesVolumeChart.setOption({
                title: {
                    text: ''
                },
                tooltip : {},
                toolbox: {
                    show: true,
                    right: 30,
                    feature: {
                        saveAsImage: {
                            show:true,
                            excludeComponents :['toolbox'],
                            pixelRatio: 2
                        }
                    }
                },
                legend: {
                    data: msg.monthList
                },
                xAxis: {
                    data: msg.officeNameSet
                },
                yAxis: [
                    {
                        type: 'value',
                        scale: true,
                        name: '销量'
                    }
                ],
                series: msg.seriesList
            });
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert(XMLHttpRequest);
            alert(textStatus);
            alert(errorThrown);
        }
    });




</script>
</html>