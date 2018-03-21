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
    <input name="applyDate" id="applyDate" value="${month}" onchange="initChart()" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <input id="exportTable" onclick="exportTable()" class="btn btn-primary" type="button" value="导出表格"/>
    <input type="hidden" name="img" id="img" />
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
        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/userData",
            data: {"month": startDate},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    salesVolumeChart.hideLoading();
                    return;
                }
                salesVolumeChart.setOption({
                    title: {
                        text: '用户量统计(月)',
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
        var startDate = $("#applyDate").val();

        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post')
        myform.attr('action',"${adminPath}/biz/statistics/userDataDownload");

        var myProductId = $("<input type='hidden' name='month' />");
        myProductId.attr('value', startDate);


        var myUpdateReason = $("<input type='hidden' name='imgUrl' />");
        myUpdateReason.attr('value', imgUrl);


        myform.append(myProductId);
        myform.append(myUpdateReason);
        myform.appendTo('body').submit(); //must add this line for higher html spec

    }

</script>
</html>