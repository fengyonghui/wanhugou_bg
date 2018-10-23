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
    <select class="input-medium" id="centerType">
        <option value="8" label="采购中心">采购中心</option>
        <option value="13" label="网供">网供</option>
        <%--<option value="10" label="配资业务">配资业务</option>--%>
    </select>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <input id="exportTable" onclick="exportTable()" class="btn btn-primary" type="button" value="导出表格"/>
    <input type="hidden" name="img" id="img" />
    <input type="hidden" name="img1" id="img1" />
    <div id="orderTotalDataChart" style="height: 300px;"></div>

</div>
<div>
    <div id="orderRateChart" style="height: 300px"></div>
</div>

</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript" src="${ctxStatic}/common/base.js"></script>
<script type="application/javascript">
    function initChart() {
        var salesVolumeChart = echarts.init(document.getElementById('orderTotalDataChart'), 'light');
        salesVolumeChart.clear();
        salesVolumeChart.showLoading($Echarts.showLoadingStyle);

        var orderRateChart = echarts.init(document.getElementById('orderRateChart'), 'light');
        orderRateChart.clear();
        orderRateChart.showLoading($Echarts.showLoadingStyle);


        var centerTypeEle = $("#centerType");
        var centerType = centerTypeEle.find("option:selected").val();

        var startDate = $("#applyDate").val();

        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/profitData",
            data: {"month": startDate, "centerType": centerType},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    salesVolumeChart.hideLoading();
                    orderRateChart.hideLoading();
                    return;
                }

                salesVolumeChart.setOption({
                    title: {
                        text: '销售利润统计(近三月)',
                        textStyle:{
                            fontSize: 16,
                            fontWeight: 'bolder',
                            color: '#6a6a6a'
                        },
                        x:'center'
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
                            },
                            myShowTable: {
                                show: true,
                                title: '显示表格',
                                icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                                onclick: function (){
                                    window.location.href="${adminPath}/biz/statistics/orderTable"
                                }
                            }
                        }
                    },
                    legend: {
                        data: msg.monthList,
                        y : 'bottom'
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
                            name: '利润'
                        }
                    ],
                    series: msg.seriesList
                });
                salesVolumeChart.hideLoading();
                setInterval( function (args) {
                    var imgUrl = salesVolumeChart.getDataURL({
                        pixelRatio: 1,
                        backgroundColor : '#fff'
                    });
                    $('#img').val(imgUrl);
                },1000);

                orderRateChart.setOption({
                    title: {
                        text: '销售利润统计(近三月)',
                        textStyle:{
                            fontSize: 16,
                            fontWeight: 'bolder',
                            color: '#6a6a6a'
                        },
                        x:'center'
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
                            },
                            myShowTable: {
                                show: true,
                                title: '显示表格',
                                icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                                onclick: function (){
                                    window.location.href="${adminPath}/biz/statistics/orderTable"
                                }
                            }
                        }
                    },
                    legend: {
                        data: msg.officeNameSet,
                        y : 'bottom'
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
                            name: '利润'
                        }
                    ],
                    series: msg.rateSeriesList
                });
                orderRateChart.hideLoading();
                setInterval( function (args) {
                    var imgUrl = orderRateChart.getDataURL({
                        pixelRatio: 1,
                        backgroundColor : '#fff'
                    });
                    $('#img1').val(imgUrl);
                },1000);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("未查询到数据!");
            }
        });
    }
    initChart();
    function exportTable() {
        initChart();

        var imgUrl = $('#img').val();
        var imgUrl1 = $('#img1').val();

        var dataTypeEle = $("#dataType");
        var dataType = dataTypeEle.find("option:selected").val();
        var dataTypeDesc = dataTypeEle.find("option:selected").html();

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();

        var startDate = $("#applyDate").val();

        var centerTypeEle = $("#centerType");
        var centerType = centerTypeEle.find("option:selected").val();

        // data: {"month": startDate, "lineChartType": dataType, "barChartType": barChartType},

        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post')
        myform.attr('action',"${adminPath}/biz/statistics/profitDataDownload");

        var myProductId = $("<input type='hidden' name='month' />");
        myProductId.attr('value', startDate);

        var myWarehouseId = $("<input type='hidden' name='dataType' />");
        myWarehouseId.attr('value', dataType);

        var myBarChartType = $("<input type='hidden' name='barChartType' />");
        myWarehouseId.attr('value', barChartType);

        var myUpdateReason = $("<input type='hidden' name='imgUrl' />");
        myUpdateReason.attr('value', imgUrl);

        var myUpdateReason1 = $("<input type='hidden' name='imgUrl1' />");
        myUpdateReason1.attr('value', imgUrl1);

        var centerTypemy = $("<input type='hidden' name='centerType' />");
        centerTypemy.attr('value', centerType);
        myform.append(myProductId);
        myform.append(myWarehouseId);
        myform.append(myBarChartType);
        myform.append(myUpdateReason);
        myform.append(myUpdateReason1);
        myform.append(centerTypemy);
        myform.appendTo('body').submit();

    }

</script>
</html>