<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>商品新增数量统计</title>
</head>
<body>
<div>
    <input name="applyDate" id="applyDate" value="${month}" onchange="initChart()" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <label>
        <select class="input-medium" id="type">
            <option value="0" label="年"></option>
            <option value="1" label="月"></option>
        </select>
    </label>
    <label>
        <select class="input-medium" id="variId">
            <option value="0" label="全部"></option>
            <option value="1" label="拉杆箱"></option>
            <option value="2" label="双肩包"></option>
            <option value="3" label="钱包"></option>
            <%--<c:forEach items="${varietyList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>--%>
        </select>
    </label>
    <input onclick="initChart()" class="btn btn-primary" type="button" value="查询"/>
    <div id="skuTotalDataChart" style="height: 500px"></div>

</div>


</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript">
    function initChart() {
        var skuTotalDataChart = echarts.init(document.getElementById('skuTotalDataChart'), 'light');
        skuTotalDataChart.clear();
        var applyDate = $("#applyDate").val();

        var typeEle = $("#type");
        var type = typeEle.find("option:selected").val();

        var variIdEle = $("#variId");
        var variId = variIdEle.find("option:selected").val();

        if (type == '1' && (applyDate == '' || applyDate == null)) {
            alert("请选择月份");
            return;
        }
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/between/skuData",
            data: {"month": applyDate, "type" : type, "variId" : variId},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    return;
                }
                skuTotalDataChart.setOption({
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
                    /*legend: {
                        data: msg.officeNameSet
                    },*/
                    //['第一周','第二周','第三周','第四周','第五周']
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
                            name: msg.seriesList.name
                        }
                    ],
                    series: [{
                        name:'新增商品数量',
                        type:'line',
                        symbolSize:4,   //拐点圆的大小
                        color:['red'],  //折线条的颜色
                        data:msg.seriesList.data,
                        smooth:false,   //关键点，为true是不支持虚线的，实线就用true
                        itemStyle:{
                            normal:{
                                lineStyle:{
                                    width:2,
                                    type:'dotted'  //'dotted'虚线 'solid'实线
                                }
                            }
                        }
                    }]
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