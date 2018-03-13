<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>订单统计</title>
</head>
<body>
<div>
    <input id="startDate" value=""  onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <input id="endDate" value="" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <select class="input-medium" id="barChartType">
        <option value="0" label="年"></option>
        <option value="1" label="月"></option>
    </select>

    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    (如查询年报无需选择月份)
    <div id="orderTotalDataChart" style="height: 300px;"></div>
    <div id="orderTotalDataCountChart" style="height: 300px;"></div>
</div>
</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript">
    function initChart() {
        var salesVolumeChart = echarts.init(document.getElementById('orderTotalDataChart'), 'light');
        var orderTotalDataCountChart = echarts.init(document.getElementById('orderTotalDataCountChart'), 'light');
        salesVolumeChart.clear();
        orderTotalDataCountChart.clear();

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();


        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

        if (barChartType == '1' && (startDate == '' || startDate == null)) {
            alert("请选择日期");
            return;
        }
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/platform/orderDataCategoryByCenter",
            data: {"startDate" : startDate, "endDate" : endDate, "type" : barChartType},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    return;
                }

                salesVolumeChart.setOption({
                    title: {
                        text: ''
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#999'
                            }
                        }
                    },
                    toolbox: {
                        show: true,
                        right: 30,
                        feature: {
                            saveAsImage: {
                                show: true,
                                excludeComponents: ['toolbox'],
                                pixelRatio: 2
                            }
                        }
                    },
                    legend: {
                        data: ''
                    },
                    xAxis: {
                        data: msg.dateStrList,
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: '销售额',
                            min:0
                        }
                    ],
                    series: msg.seriesList
                });

                orderTotalDataCountChart.setOption({
                    title: {
                        text: ''
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            crossStyle: {
                                color: '#999'
                            }
                        }
                    },
                    toolbox: {
                        show: true,
                        right: 30,
                        feature: {
                            saveAsImage: {
                                show: true,
                                excludeComponents: ['toolbox'],
                                pixelRatio: 2
                            }
                        }
                    },
                    legend: {
                        data: ''
                    },
                    xAxis: {
                        data: msg.dateStrList,
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: '订单量',
                            min:0
                        }
                    ],
                    series: msg.seriesCountList
                });


            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("未查询到数据!");
            }
        });
    }
    initChart();


</script>
</html>