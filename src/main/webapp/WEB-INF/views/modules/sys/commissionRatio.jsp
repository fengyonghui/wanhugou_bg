<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>字典管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        function updateCommissionRatio() {
            if(confirm("确定修改佣金比例吗？")){
                var dictId = $("#id").val();
                var type = $("#type").val();
                var value = $("#value").val();
                console.log(value);
                $.ajax({
                    type:"get",
                    url:"${ctx}/sys/dict/save4Mobile",
                    data:{id:dictId, type:type, value:value},
                    success:function (result) {
                        var msg = JSON.parse(result)
						var flg = msg.data.left;

                        if (flg == true || flg == "true") {
                            window.location.href="${ctx}/sys/dict/commissionRatioView?type=commission_ratio"
                        } else {
                            alert("修改佣金比例失败")
                            window.location.href="${ctx}/sys/dict/commissionRatioView?type=commission_ratio"
                        }
                    }
                });
            }
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/dict/commissionRatioView">佣金比例设置</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="dict" method="post" class="breadcrumb form-search">
		<input id="id" name="id" type="hidden" value="${dict.id}"/>
		<input id="type" name="type" type="hidden" value="${dict.type}"/>
		<label>代销商获得的佣金：（零售价-工厂价）：</label>
		&nbsp;&nbsp;
		<input id="value" path="value" style="width: 50px" value="${dict.value}" class="input-mini" type='text'/><span>%</span>
	</form:form>

	<div class="form-actions">
		<input type="button"
			   onclick="updateCommissionRatio()"
			   class="btn btn-primary"
			   value="保存"/>
		<%--<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>--%>
	</div>

	<div class="breadcrumb form-search">
		&nbsp;&nbsp;示例：</br>
		&nbsp;&nbsp;零售价90元；</br>
		&nbsp;&nbsp;工厂价50元；</br>
		&nbsp;&nbsp;佣金：（90-50）*50%=20元；
	</div>


</body>
</html>