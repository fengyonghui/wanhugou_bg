<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>产品统计</title>
</head>
<body>
<div>
    <input name="applyDate" id="applyDate" value="${month}" onchange="initChart()" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <label>
        <select class="input-medium" id="variId">
            <option value="0" label="全部"></option>
            <c:forEach items="${varietyList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <label>
        <select class="input-medium" id="dataType">
            <option value="1" label="销售额">销售额</option>
            <option value="3" label="订单量">订单量</option>
        </select>
    </label>
    <label>
        <select class="input-medium" id="purchasingId">
            <option value="0" label="全部"></option>
            <c:forEach items="${purchasingList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <div id="orderTotalDataChart" style="height: 500px"></div>

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

        var startDate = $("#applyDate").val();

        var variIdEle = $("#variId");
        var variId = variIdEle.find("option:selected").val();

        var dataTypeEle = $("#dataType");
        var dataType = dataTypeEle.find("option:selected").val();
        var dataTypeDesc = dataTypeEle.find("option:selected").html();

        var purchasingIdEle = $("#purchasingId");
        var purchasingId = purchasingIdEle.find("option:selected").val();

        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }

        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/productData",
            data: {"month": startDate, "variId" : variId, "dataType" : dataType, "purchasingId" : purchasingId},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    salesVolumeChart.hideLoading();
                    return;
                }
                salesVolumeChart.setOption({
                    title: {
                        text: '产品销量/销售额统计(月)',
                        textStyle:{
                            fontSize: 16,
                            fontWeight: 'bolder',
                            color: '#6a6a6a'
                        },
                        x:'center'
                    },
                    tooltip: {
                        trigger: 'axis',
                        // axisPointer: {
                        //     type: 'cross',
                        //     crossStyle: {
                        //         color: '#999'
                        //     }
                        // }
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
                                    window.location.href="${adminPath}/biz/statistics/productAnalysisTables";
                                }
                            }
                        }
                    },
                    legend: {
                        data: dataTypeDesc,
                        y : 'bottom'
                    },
                    grid: {
                        bottom:'60%',
                        left: '10%'
                    },
                    xAxis: {
                        data: msg.nameList,
                        show:true,
                        axisPointer: {
                            type: 'shadow'
                        },
                        axisLabel: {
                            interval:0,
                            rotate:-30
                        }
                    },
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: dataTypeDesc,
                            min:0
                        }
                    ],
                    series: msg.seriesList
                });
                salesVolumeChart.hideLoading();
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("未查询到数据!");
            }
        });
    }
    initChart();


</script>
</html>