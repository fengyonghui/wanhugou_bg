/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shelf;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 运营货架信息Entity
 * @author liuying
 * @version 2017-12-19
 */
public class BizOpShelfInfo extends DataEntity<BizOpShelfInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 货架名称
	private String description;		// 货架描述
	private List<BizOpShelfSku> opShelfSkusList;
	private String OpShelfSkus;

	
	public BizOpShelfInfo() {
		super();
	}

	public BizOpShelfInfo(Integer id){
		super(id);
	}

	@Length(min=1, max=50, message="货架名称长度必须介于 1 和 50 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<BizOpShelfSku> getOpShelfSkusList() {
		return opShelfSkusList;
	}

	public void setOpShelfSkusList(List<BizOpShelfSku> opShelfSkusList) {
		this.opShelfSkusList = opShelfSkusList;
	}

	public String getOpShelfSkus() {
		return OpShelfSkus;
	}

	public void setOpShelfSkus(String opShelfSkus) {
		OpShelfSkus = opShelfSkus;
	}
}