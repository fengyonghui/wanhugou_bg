<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<!-- Form中关联address表字段添加 -->
<div class="accordion-heading">
    <%--<a class="accordion-toggle" href="javascript:void(0);">详细地址</a>--%>
</div>
<div class="control-group">
    <label class="control-label">所在地区:</label>
    <div class="controls">
        <input type="hidden" id="locationId" name="locationId" value="${entity.bizLocation.id}"/>
        <form:hidden path="bizLocation.selectedRegionId" id="regionId" value="${entity.bizLocation.selectedRegionId}"/>
        <input type="text" id="regionName" value="${entity.bizLocation.pcrName}" readonly="readonly" required="true"/>
        <biz:selectregion id="region_id" name="regionName" selectedId="regionId"/>
        <span class="help-inline"><font color="red">*</font> </span>
    </div>
</div>


<div class="control-group">
    <label class="control-label">详细地址：</label>
    <div class="controls">
        <form:input path="bizLocation.address" value="${entity.bizLocation.address }" htmlEscape="false" maxlength="255" class="input-xlarge "/>
        <span class="help-inline"><font color="red">*</font> </span>
    </div>
</div>

</div>