/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity;

import com.wanhutong.backend.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 采购商店铺Entity
 * @author zx
 * @version 2018-03-05
 */
public class SysCustDetails extends DataEntity<SysCustDetails> {
	
	private static final long serialVersionUID = 1L;
	private Office cust;		// 采购商.id ; sys_office.id = sys_user.company_id
	private String custAcreage;		// 商铺面积
	private String custType;		// 商铺类型：1.批发商2.零售商3.微商4.其他
	private String custCate;		// cust_cate
	
	public SysCustDetails() {
		super();
	}

	public SysCustDetails(Integer id){
		super(id);
	}

	public Office getCust() {
		return cust;
	}

	public void setCust(Office cust) {
		this.cust = cust;
	}

	public String getCustAcreage() {
		return custAcreage;
	}

	public void setCustAcreage(String custAcreage) {
		this.custAcreage = custAcreage;
	}
	

	public String getCustType() {
		return custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}
	
	@Length(min=0, max=255, message="cust_cate长度必须介于 0 和 255 之间")
	public String getCustCate() {
		return custCate;
	}

	public void setCustCate(String custCate) {
		this.custCate = custCate;
	}
	
}