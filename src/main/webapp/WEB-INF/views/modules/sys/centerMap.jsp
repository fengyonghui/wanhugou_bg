<%--
  Created by IntelliJ IDEA.
  User: liuying
  Date: 2018/3/13
  Time: 10:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <style type="text/css">
        body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;font-family:"微软雅黑";}
        #l-map{height:100%;width:78%;float:left;border-right:2px solid #bcbcbc;}
        #r-result{height:100%;width:20%;float:left;}
    </style>
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=fa5lmPaAvLnpv6Ucl05M9rm4"></script>
    <script type="text/javascript" src="http://api.map.baidu.com/library/SearchInfoWindow/1.4/src/SearchInfoWindow_min.js"></script>
    <link rel="stylesheet" href="http://api.map.baidu.com/library/SearchInfoWindow/1.4/src/SearchInfoWindow_min.css" />
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>

</head>
<body>
<%--<span id="totalCount">11111</span>--%>
<div id="allmap"></div>
</body>
</html>

<script type="text/javascript">


    var map = new BMap.Map("allmap");
    var point = new BMap.Point(116.376589,39.921108);
    map.centerAndZoom(point, 12);
    map.enableScrollWheelZoom(true);

    map.setViewport({center:new BMap.Point(116.043549,39.132556),zoom:6})

    // 编写自定义函数,创建标注
    function addMarker(vectorMarker){
        //   var marker = new BMap.Marker(point);
        map.addOverlay(vectorMarker);

    }
    // var bs = map.getBounds();   //获取可视区域
    // var bssw = bs.getSouthWest();   //可视区域左下角
    // var bsne = bs.getNorthEast();   //可视区域右上角
    // alert("当前地图可视范围是：" + bssw.lng + "," + bssw.lat + "到" + bsne.lng + "," + bsne.lat);
    $.ajax({
        type:"post",
        url:"${ctx}/sys/viewMap/findOfficeAddress",
        data:{type:8},
        success:function (data) {
            // $("#totalCount").text(data.length)
            $.each(data,function (index,offAdress) {
                var vectorMarker = new BMap.Marker(new BMap.Point(offAdress.bizLocation.longitude, offAdress.bizLocation.latitude), {
                    // 指定Marker的icon属性为Symbol
                    icon: new BMap.Symbol(BMap_Symbol_SHAPE_POINT, {
                        scale: 1,//图标缩放大小
                        fillColor: "red",//填充颜色
                        fillOpacity: 0.8//填充透明度
                    })
                });

                var label = new BMap.Label(" "+offAdress.office.name+" ", { offset: new BMap.Size(10, -25) });
                label.setStyle({
                    color: "#fff",
                    border: "0",
                    padding: "0",
                    display: "none",
                    background: "rgba(66,117,202,0.9)",
                    fontSize: "12px",
                    height: "20px",
                    lineHeight: "20px",
                    fontFamily: "微软雅黑"
                });

                vectorMarker.setLabel(label);
                addMarker(vectorMarker);

                vectorMarker.addEventListener("mouseover", function(){
                    var label = this.getLabel();
                    label.setStyle({display:"block"});
                });
                vectorMarker.addEventListener("mouseout",function(e){
                    var label = this.getLabel();
                    label.setStyle({display:"none"});
                });
            });

        }
    });





</script>
