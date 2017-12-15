/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.sku;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * sku属性Entity
 * @author zx
 * @version 2017-12-14
 */
public class BizSkuPropValue extends DataEntity<BizSkuPropValue> {
	
	private static final long serialVersionUID = 1L;
	private String skuId;		// biz_sku_info.id
	private String prodPropId;		// prod_prop_id
	private String propName;		// prop_name
	private String prodValueId;		// biz_prod_prop_value.id
	private String propValue;		// biz_prod_prop_value
	
	public BizSkuPropValue() {
		super();
	}

	public BizSkuPropValue(Integer id){
		super(id);
	}

//	@id长度必须介于 1 和 11 之间")
	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	
	@Length(min=0, max=11, message="prod_prop_id长度必须介于 0 和 11 之间")
	public String getProdPropId() {
		return prodPropId;
	}

	public void setProdPropId(String prodPropId) {
		this.prodPropId = prodPropId;
	}
	
	@Length(min=1, max=30, message="prop_name长度必须介于 1 和 30 之间")
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
	
//	@id长度必须介于 1 和 11 之间")
	public String getProdValueId() {
		return prodValueId;
	}

	public void setProdValueId(String prodValueId) {
		this.prodValueId = prodValueId;
	}
	
	@Length(min=1, max=100, message="biz_prod_prop_value长度必须介于 1 和 100 之间")
	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	
}