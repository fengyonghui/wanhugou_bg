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
    <input name="applyDate" id="applyDate" value="${month}" onchange="initChart()" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <select class="input-medium" id="barChartType">
        <option value="1" label="销售额">销售额</option>
        <option value="3" label="订单量">订单量</option>
    </select>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <input onclick="switchover()" class="btn btn-primary" type="button" value="图表"/>
    <div id="orderTotalDataTable" style="">

    </div>
    <div id="orderTotalDataChart" style="height: 300px;"></div>

</div>
<div>
    <label>
        <select class="input-medium" id="dataType">
            <option value="1" label="销售额">销售额</option>
            <option value="2" label="销售额增长率">销售额增长率(%)</option>
        </select>
    </label>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
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

        var dataTypeEle = $("#dataType");
        var dataType = dataTypeEle.find("option:selected").val();
        var dataTypeDesc = dataTypeEle.find("option:selected").html();

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();

        var orderTotalDataTable = $("#orderTotalDataTable");

        var applyDate = $("#applyDate").val();
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/orderData",
            data: {"month": applyDate, "lineChartType": dataType, "barChartType": barChartType},
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

                // var monthListTable = "";
                // for(var i = 0; i < msg.monthList.length; i ++) {
                //     monthListTable = monthListTable + "<th>" + msg.monthList[i] + "销售额</th><th>" + msg.monthList[i] + "利润</th><th>" + msg.monthList[i] + "订单量</th>";
                // }
                // var dataTable = "";
                // for(var i = 0; i < msg.officeNameSet.length; i ++) {
                //     var currentMonthData = "";
                //     for (var j = 0; j < msg.monthList.length; j ++) {
                //     console.info(msg.dataMap[msg.monthList[j]][msg.officeNameSet[i]].totalMoney);
                //         var currentData = msg.dataMap[msg.monthList[j]][msg.officeNameSet[i]];
                //         debugger;
                //         if (currentData) {
                //             currentMonthData = currentMonthData + "<td>" + currentData.totalMoney + "</td>";
                //             currentMonthData = currentMonthData + "<td>" + currentData.profitPrice + "</td>";
                //             currentMonthData = currentMonthData + "<td>" + currentData.orderCount + "</td>";
                //         }else {
                //             currentMonthData = currentMonthData + "<td>-</td>";
                //             currentMonthData = currentMonthData + "<td>-</td>";
                //             currentMonthData = currentMonthData + "<td>-</td>";
                //         }
                //
                //     }
                //     dataTable = dataTable +
                //         "<tr>\n" +
                //         "                <td>" + msg.officeNameSet[i] + "</td>\n" +
                //         "                <td>" + currentMonthData + "</td>\n" +
                //         "            </tr>\n";
                // }
                // console.info(dataTable);
                // console.info(dataTable);

                // var table1 =
                //     "<table style=\"border-style: solid; border-width: 1px; margin: 50px 20px 50px 20px\">\n" +
                //     "            <tr>\n" +
                //     "                <th>采购中心</th>\n" +
                //     monthListTable +
                //     "            </tr>\n" +
                //     dataTable +
                //     "        </table>"
                // ;
                //
                // orderTotalDataTable.html("");
                // orderTotalDataTable.html(table1);

            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("未查询到数据!");
            }
        });
    }
    initChart();


</script>
</html>