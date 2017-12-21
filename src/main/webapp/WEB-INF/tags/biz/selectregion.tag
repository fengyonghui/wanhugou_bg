<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="false" description="地区ID"%>
<%@ attribute name="code" type="java.lang.String" required="false" description="地区编码"%>
<%@ attribute name="name" type="java.lang.String" required="false" description="地区名称"%>
<%@ attribute name="selectedId" type="java.lang.String" required="false" description="所选区域ID"%>

<a id="AreaButton" href="javascript:" class="btn">选择地区</a>
&nbsp;&nbsp;
<script type="text/javascript">
	$("#AreaButton").click(
			function() {
				top.$.jBox.open("iframe:${ctx}/sys/sysRegion/tag/selectRegion"
						, "选择地区", 600, $(top.document)
						.height() - 180, {
					buttons : {
						"确定" : "ok",
						"清除" : "clear",
						"关闭" : true
					},
					submit : function(v, h, f) {
						if (v == "ok") {
							var id = h.find("iframe")[0].contentWindow.$("#id").val();
							$("#${id}").val(id);
							var code = h.find("iframe")[0].contentWindow.$("#code").val();
							$("#${code}").val(code);

                            var name = h.find("iframe")[0].contentWindow.$("#name").val();
                            $("#${name}").val(name);
							var selectedId = h.find("iframe")[0].contentWindow.$("#selectedId").val();
							$("#${selectedId}").val(selectedId);
						} 
						else if (v=="clear"){
							$("#${id}").val("");
							$("#${code}").val("");
							$("#${name}").val("");
							$("#${selectedId}").val("");
		                }
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css("overflow-y",
								"hidden");
					}
				});
			});
</script>