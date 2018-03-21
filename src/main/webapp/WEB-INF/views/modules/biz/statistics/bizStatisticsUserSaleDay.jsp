<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>业绩统计</title>
</head>
<body>
<div>
    <input name="applyDate" id="applyDate" value="${month}" onchange="initChart()"
           onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <label>
        <select class="input-medium" id="purchasingId">
            <option value="0" label="全部"></option>
            <c:forEach items="${purchasingList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <input id="exportTable" onclick="exportTable()" class="btn btn-primary" type="button" value="导出表格"/>
    <div id="userTotalDataChart" style="height: 300px"></div>


    <label>
        <select class="input-medium" id="usName">
        </select>
    </label>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <div id="singleUserTotalDataChart" style="height: 300px"></div>
    <input type="hidden" name="img" id="img" />
    <input type="hidden" name="img1" id="img1" />
</div>


</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript" src="/static/common/base.js"></script>
<script type="application/javascript">
    function initChart() {
        var salesVolumeChart = echarts.init(document.getElementById('userTotalDataChart'), 'light');
        var singleSalesVolumeChart = echarts.init(document.getElementById('singleUserTotalDataChart'), 'light');
        salesVolumeChart.clear();
        singleSalesVolumeChart.clear();

       salesVolumeChart.showLoading($Echarts.showLoadingStyle);
       singleSalesVolumeChart.showLoading($Echarts.showLoadingStyle);

        var startDate = $("#applyDate").val();
        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }
        var purchasingIdEle = $("#purchasingId");
        var purchasingId = purchasingIdEle.find("option:selected").val();

        var usNameEle = $("#usName");
        var usName = usNameEle.find("option:selected").val();
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/day/userSaleData",
            data: {"day": startDate, "purchasingId": purchasingId, "usName": usName},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    salesVolumeChart.hideLoading();
                    singleSalesVolumeChart.hideLoading();
                    return;
                }
                // <option value="0" label="全部"></option>
                usNameEle.html('');
                usNameEle.append("<option value=\"" + msg.usName + "\" label=\"" + msg.usName + "\"></option>");
                for (var i = 0; i < msg.selectNameList.length; i++) {
                    var child = "<option value=\"" + msg.selectNameList[i] + "\" label=\"" + msg.selectNameList[i] + "\"></option>";
                    usNameEle.append(child);
                }

                salesVolumeChart.setOption({
                    title: {
                        text: '采购顾问业绩统计(日)',
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
                            }
                        }
                    },
                    legend: {
                        data: ['订单量', '销售额'],
                        y : 'bottom'
                    },
                    xAxis: {
                        data: msg.nameList,
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: '订单量',
                            min: 0
                        }, {
                            type: 'value',
                            scale: true,
                            name: '销售额',
                            min: 0
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

                singleSalesVolumeChart.setOption({
                    title: {
                        text: '采购顾问业绩统计(个人/日)',
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
                            }
                        }
                    },
                    legend: {
                        data: ['订单量', '销售额'],
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
                            name: '订单量',
                            min: 0
                        }, {
                            type: 'value',
                            scale: true,
                            name: '销售额',
                            min: 0
                        }
                    ],
                    series: msg.singleSeriesList
                });
                singleSalesVolumeChart.hideLoading();
                setInterval( function (args) {
                    var imgUrl = singleSalesVolumeChart.getDataURL({
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
        var dataType = "1";

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();

        var startDate = $("#applyDate").val();

        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post')
        myform.attr('action',"${adminPath}/biz/statistics/day/userSaleDataDownload");

        var myProductId = $("<input type='hidden' name='day' />")
        myProductId.attr('value', startDate);

        var myWarehouseId = $("<input type='hidden' name='dataType' />")
        myWarehouseId.attr('value', barChartType);

        var myUpdateReason = $("<input type='hidden' name='imgUrl' />")
        myUpdateReason.attr('value', imgUrl);

        var myUpdateReason1 = $("<input type='hidden' name='imgUrl1' />")
        myUpdateReason1.attr('value', imgUrl1);

        myform.append(myProductId);
        myform.append(myWarehouseId);
        myform.append(myUpdateReason);
        myform.append(myUpdateReason1);
        myform.appendTo('body').submit(); //must add this line for higher html spec

    }
</script>
</html>