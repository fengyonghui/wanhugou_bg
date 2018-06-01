/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 产品查看日志Entity
 * @author zx
 * @version 2018-02-22
 */
public class BizProdViewLog extends DataEntity<BizProdViewLog> {
	
	private static final long serialVersionUID = 1L;
	private BizOpShelfInfo opShelfInfo;		// 货架ID
	private Office center;		// 采购中心ID
	private BizProductInfo productInfo;		// 产品ID
	private User user;		// 用户ID

	//产品点击量
	private Integer prodChick;
	
	public BizProdViewLog() {
		super();
	}

	public BizProdViewLog(Integer id){
		super(id);
	}

	public BizOpShelfInfo getOpShelfInfo() {
		return opShelfInfo;
	}

	public void setOpShelfInfo(BizOpShelfInfo opShelfInfo) {
		this.opShelfInfo = opShelfInfo;
	}

	public Office getCenter() {
		return center;
	}

	public void setCenter(Office center) {
		this.center = center;
	}

	public BizProductInfo getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(BizProductInfo productInfo) {
		this.productInfo = productInfo;
	}
	
	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getProdChick() {
		return prodChick;
	}

	public void setProdChick(Integer prodChick) {
		this.prodChick = prodChick;
	}
}