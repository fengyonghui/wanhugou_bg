<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
</head>
<body>
<div>
    起始时间:<input id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    结束时间:<input id="endDate" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <select class="input-medium" id="centerType">
        <option value="8" label="采购中心">采购中心</option>
        <option value="13" label="网供">网供</option>
        <option value="10" label="配资业务">配资业务</option>
    </select>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <%--<input id="exportTable" onclick="exportTable()" class="btn btn-primary" type="button" value="导出表格"/>--%>
    <input type="hidden" name="img" id="img" />
    <input type="hidden" name="img1" id="img1" />
    <div id="orderTotalDataChart" style="height: 300px;"></div>

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

        var centerTypeEle = $("#centerType");
        var centerType = centerTypeEle.find("option:selected").val();

        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }

        $.ajax({
            type: 'GET',
            url: "${ctx}/biz/statistics/platform/receiveData",
            data: {"startDate": startDate,"endDate" : endDate, "centerType": centerType},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    salesVolumeChart.hideLoading();
                    return;
                }

                salesVolumeChart.setOption({
                    title: {
                        text: '回款额\n',
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
                                show: false,
                                title: '显示表格',
                                icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                                onclick: function (){
                                    window.location.href="${adminPath}/biz/statistics/receiveTable"
                                }
                            }
                        }
                    },
                    legend: {
                        data: msg.officeNameSet,
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
                            name: '回款额'
                        }
                    ],
                    series: msg.echartsSeriesDto
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
</script>
</html>