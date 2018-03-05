<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>用户统计</title>
</head>
<body>
<div id="userCountChart" style="height: 300px"></div>
<div id="purchaseFrequencyChart" style="height: 300px"></div>
<div id="monthSalesVolumeChart" style="height: 300px"></div>
<div id="echartsTest" style="height: 300px"></div>

</body>
<script type="application/javascript" src="/static/echarts/echarts.min.js"></script>
<script type="application/javascript">
    var salesVolumeChart = echarts.init(document.getElementById('echartsTest'));
    salesVolumeChart.setOption({
        title: {
            text: '1月销量达成表'
        },
        tooltip : {},
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
            data: ['销量', '达成率']
        },
        xAxis: {
            data: ${districtlist}
        },
        yAxis: [
            {
                type: 'value',
                scale: true,
                name: '销量',
                max: 150,
                min: 0,
            },
            {
                type: 'value',
                scale: true,
                name: '达成率',
                max: 100,
                min: 0,
            }
        ],
        series: [{
            yAxisIndex: 0,
            name: '销量',
            type: 'bar',
            color: '#5CACEE',
            data: ${salesVolumeList}
        }, {
            yAxisIndex: 1,
            name: '达成率',
            type: 'line',
            color: '#FF8C00',
            data: ${yieldRateList}
        }]
    });

    var monthSalesVolumeChart = echarts.init(document.getElementById('monthSalesVolumeChart'));
    monthSalesVolumeChart.setOption({
        title: {
            text: '11月-1月销售额'
        },
        tooltip : {},
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
            data: ['11月销售额', '12月销售额', '1月销售额']
        },
        xAxis: {
            data: ${districtlist}
        },
        yAxis: [
            {
                type: 'value',
                scale: true,
                name: '销售额',
                max: 1200000,
                min: 0,
            }
        ],
        series: [{
            name: '11月销售额',
            type: 'line',
            color: '#5CACEE',
            itemStyle: {normal: {label: {show: true}}},
            data: ${novSaleroomList}
        }, {
            name: '12月销售额',
            type: 'line',
            color: '#FF8C00',
            itemStyle: {normal: {label: {show: true}}},
            data: ${decSaleroomList}
        }, {
            name: '1月销售额',
            type: 'line',
            color: '#dad6da',
            itemStyle: {normal: {label: {show: true}}},
            data: ${janSaleroomList}
        }]
    });

    var purchaseFrequencyChart = echarts.init(document.getElementById('purchaseFrequencyChart'));
    purchaseFrequencyChart.setOption({
        title: {
            text: '采购频次分析'
        },
        tooltip : {},
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
            data: [
                '11月采购次数',
                '12月采购次数',
                '1月采购次数',
                '12月增长量',
                '1月增长量'
            ]
        },
        xAxis: {
            data: ${districtlist}
        },
        yAxis: [
            {
                type: 'value',
                scale: true,
                name: '',
                max: 250,
                min: -150
            }
        ],
        series: [{
            name: '11月采购次数',
            type: 'bar',
            data: [161, 223, 36, 1, 3]
        }, {
            name: '12月采购次数',
            type: 'bar',
            data: [210, 153, 166, 57, 37]
        }, {
            name: '1月采购次数',
            type: 'bar',
            data: [234, 176, 190, 97, 143]
        }, {
            name: '12月增长量',
            type: 'line',
            data: [49, -80, 130, 46, 34]
        }, {
            name: '1月增长量',
            type: 'line',
            data: [24, 23, 24, 50, 106]
        }]
    });

    var userCountChart = echarts.init(document.getElementById('userCountChart'));
    userCountChart.setOption({
        title: {
            text: '11月-1月用户量分析'
        },
        tooltip : {},
        toolbox: {
            show: true,
            right: 30,
            itemGap:30,
            orient:'vertical',
            feature: {
                saveAsImage: {
                    show:true,
                    excludeComponents :['toolbox'],
                    pixelRatio: 2
                }
            }
        },
        legend: {
            data: [
                '11月',
                '12月',
                '1月'
            ]
        },
        xAxis: {
            data: ${districtlist}
        },
        yAxis: [
            {
                type: 'value',
                scale: true,
                name: '',
            }
        ],
        series: [{
            name: '11月',
            type: 'line',
            symbol:"circle",
            symbolSize:24,
            color:'#63d2ee',
            itemStyle: {normal: {
                label: {
                    show: true,
                    position: 'inside',
                    color:'#000000'
                }
            }},
            data: [57, 83, 2, 1, 3]
        }, {
            name: '12月',
            type: 'line',
            symbol:"circle",
            symbolSize:24,
            color:'#cfee96',
            itemStyle: {normal: {
                    label: {
                        show: true,
                        position: 'inside',
                        color:'#000000'
                    }
                }},
            data: [70, 36, 35, 3, 10]
        }, {
            name: '1月',
            type: 'line',
            symbol:"circle",
            symbolSize:24,
            color:'#76eeaf',
            itemStyle: {normal: {
                    label: {
                        show: true,
                        position: 'inside',
                        color:'#000000'
                    }
                }},
            data: [22, 54, 32, 12, 73]
        }]
    });


</script>
</html>