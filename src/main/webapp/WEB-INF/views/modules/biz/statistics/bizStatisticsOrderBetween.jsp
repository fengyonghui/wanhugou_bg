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
    <input id="exportTable" onclick="exportTable()" class="btn btn-primary" type="button" value="导出表格"/>
    <div id="orderTotalDataChart" style="height: 300px;"></div>
    <input type="hidden" name="img" id="img" />
</div>

</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript" src="/static/common/base.js"></script>
<script type="application/javascript">
    function initChart() {
        var salesVolumeChart = echarts.init(document.getElementById('orderTotalDataChart'), 'light');
        salesVolumeChart.clear();
        salesVolumeChart.showLoading($Echarts.showLoadingStyle);

        var dataType = "1";

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();

        var endDate = $("#endDate").val();
        var startDate = $("#startDate").val();

        if (startDate == '' || startDate == null || endDate == '' || endDate == null) {
            alert("请选择日期");
            return;
        }
        if(!$DateUtil.CompareDate(endDate,startDate)) {
            alert("日期选择错误!");
            return;
        }
        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/between/orderData",
            data: {"startDate": startDate,"endDate": endDate, "lineChartType": dataType, "barChartType": barChartType},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    salesVolumeChart.hideLoading();
                    return;
                }

                salesVolumeChart.setOption({
                    title: {
                        text: '销售额/订单量统计(区间)',
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
                                    window.location.href="${adminPath}/biz/statistics/between/orderTable?startDate=" + startDate + "&endDate=" + endDate
                                }
                            }
                        }
                    },
                    legend: {
                        data: '区间',
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
                            name: barChartTypeDesc
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
        var dataType = "1";

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();

        var endDate = $("#endDate").val();
        var startDate = $("#startDate").val();

        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post')
        myform.attr('action',"${adminPath}/biz/statistics/between/centOrderTableDownload");

        var myProductId = $("<input type='hidden' name='startDate' />")
        myProductId.attr('value', startDate);

        var myPurchaseOrderId = $("<input type='hidden' name='endDate' />")
        myPurchaseOrderId.attr('value', endDate);

        var myWarehouseId = $("<input type='hidden' name='dataType' />")
        myWarehouseId.attr('value', dataType);

        var myRelatedOrderId = $("<input type='hidden' name='barChartType' />")
        myRelatedOrderId.attr('value', barChartType);

        var myUpdateReason = $("<input type='hidden' name='imgUrl' />")
        myUpdateReason.attr('value', imgUrl);

        myform.append(myProductId);
        myform.append(myPurchaseOrderId);
        myform.append(myWarehouseId);
        myform.append(myRelatedOrderId);
        myform.append(myUpdateReason);
        myform.appendTo('body').submit();

    }
</script>
</html>