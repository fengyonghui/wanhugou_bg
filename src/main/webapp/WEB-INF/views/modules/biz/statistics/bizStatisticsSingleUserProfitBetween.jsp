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
    <input name="startDate" id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input name="endDate" id="endDate" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
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
    <input type="hidden" name="img" id="img"/>
    <input type="hidden" name="img1" id="img1"/>
    <div id="userTotalDataChart" style="height: 300px"></div>


    <label>
        <select class="input-medium" id="usName">
        </select>
    </label>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <div id="singleUserTotalDataChart" style="height: 300px"></div>

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

        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

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
            url: "${adminPath}/biz/statistics/between/singleUserProfitData",
            data: {"startDate": startDate, "endDate": endDate, "purchasingId": purchasingId, "usName": usName},
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
                        text: '客户专员业绩统计(月)',
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
                                    window.location.href="${adminPath}/biz/statistics/between/singleUserProfitDataTable?startDate=" + startDate + "&endDate=" + endDate;
                                }
                            }
                        }
                    },
                    legend: {
                        data: ['利润'],
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
                            name: '利润',
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
                        text: '客户专员利润统计(个人/月)',
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
                        data: ['利润'],
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
                            name: '利润',
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
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }

        var purchasingIdEle = $("#purchasingId");
        var purchasingId = purchasingIdEle.find("option:selected").val();


        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post')
        myform.attr('action',"${adminPath}/biz/statistics/between/singleUserProfitDataDownload");

        var myProductId = $("<input type='hidden' name='startDate' />");
        myProductId.attr('value', startDate);
        var myEndDate = $("<input type='hidden' name='endDate' />");
        myEndDate.attr('value', endDate);

        var myWarehouseId = $("<input type='hidden' name='purchasingId' />");
        myWarehouseId.attr('value', purchasingId);

        var myUpdateReason = $("<input type='hidden' name='imgUrl' />");
        myUpdateReason.attr('value', imgUrl);

        var myUpdateReason1 = $("<input type='hidden' name='imgUrl1' />");
        myUpdateReason1.attr('value', imgUrl1);

        myform.append(myProductId);
        myform.append(myWarehouseId);
        myform.append(myUpdateReason);
        myform.append(myUpdateReason1);
        myform.append(myEndDate);
        myform.appendTo('body').submit(); //must add this line for higher html spec

    }

</script>
</html>