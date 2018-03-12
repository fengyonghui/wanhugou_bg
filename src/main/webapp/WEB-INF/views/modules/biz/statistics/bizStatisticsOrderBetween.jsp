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
    <input id="startDate" value="${startDate}" onchange="initChart()" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input id="endDate" value="${endDate}" onchange="initChart()" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <select class="input-medium" id="barChartType">
        <option value="1" label="销售额">销售额</option>
        <option value="3" label="订单量">订单量</option>
    </select>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <div id="orderTotalDataChart" style="height: 300px;"></div>

</div>
<div>
    <%--<label>--%>
        <%--<select class="input-medium" id="dataType">--%>
            <%--<option value="1" label="销售额">销售额</option>--%>
            <%--<option value="2" label="销售额增长率">销售额增长率(%)</option>--%>
        <%--</select>--%>
    <%--</label>--%>
    <%--<input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>--%>
    <div id="orderRateChart" style="height: 300px"></div>
</div>

</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript">
    function initChart() {
        var salesVolumeChart = echarts.init(document.getElementById('orderTotalDataChart'), 'light');
        salesVolumeChart.clear();
        var orderRateChart = echarts.init(document.getElementById('orderRateChart'), 'light');
        orderRateChart.clear();

        // var dataTypeEle = $("#dataType");
        // var dataType = dataTypeEle.find("option:selected").val();
        // var dataTypeDesc = dataTypeEle.find("option:selected").html();
        var dataType = "1";
        var dataTypeDesc = "销售额";

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();

        var endDate = $("#endDate").val();
        var startDate = $("#startDate").val();
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/between/orderData",
            data: {"startDate": startDate,"endDate": endDate, "lineChartType": dataType, "barChartType": barChartType},
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
                        data: msg.monthList
                    },
                    xAxis: {
                        data: msg.officeNameSet,
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: barChartTypeDesc
                        }
                    ],
                    series: msg.seriesList
                });


                orderRateChart.setOption({
                    title: {
                        text: ''
                    },
                    tooltip : {
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
                                show:true,
                                excludeComponents :['toolbox'],
                                pixelRatio: 2
                            }
                        }
                    },
                    legend: {
                        data: msg.officeNameSet
                    },
                    xAxis: {
                        data: msg.monthList,
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: dataTypeDesc
                        }
                    ],
                    series: msg.rateSeriesList
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