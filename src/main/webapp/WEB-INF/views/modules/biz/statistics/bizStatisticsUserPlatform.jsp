<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>用户统计</title>
</head>
<body>
<div>
    <input id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input id="endDate" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <label>
        <select class="input-medium" id="type">
            <option value="0" label="年"></option>
            <option value="1" label="月"></option>
        </select>
    </label>
    <label>
        <select class="input-medium" id="centerType">
            <option value="0" label="全部"></option>
            <option value="8" label="采购中心"></option>
            <option value="11" label="网供"></option>
            <option value="10" label="配资业务"></option>
        </select>
    </label>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    (如查询年报无需选择月份)
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

        var typeEle = $("#type");
        var type = typeEle.find("option:selected").val();

        var centerTypeEle = $("#centerType");
        var centerType = centerTypeEle.find("option:selected").val();

        var endDate = $("#endDate").val();
        var startDate = $("#startDate").val();

        if (startDate == '' || startDate == null || endDate == '' || endDate == null) {
            alert("请选择日期!");
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
            url: "${adminPath}/biz/statistics/platform/userData",
            data: {"startDate": startDate, "endDate": endDate, "type" : type, "centerType" : centerType},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    salesVolumeChart.hideLoading();
                    return;
                }
                salesVolumeChart.setOption({
                    title: {
                        text: '平台用户量统计',
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
                        data: '用户量',
                        y : 'bottom'
                    },
                    // grid: {
                    //     bottom:'60%',
                    //     left: '10%'
                    // },
                    xAxis: {
                        data: msg.nameList,
                        axisPointer: {
                            type: 'shadow'
                        },
                        // axisLabel: {
                        //     interval: 0,
                        //     formatter:function(value)
                        //     {
                        //         return value.split("").join("\n");
                        //     }
                        // }
                    },
                    yAxis: [
                        {
                            type: 'value',
                            scale: true,
                            name: '用户量',
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