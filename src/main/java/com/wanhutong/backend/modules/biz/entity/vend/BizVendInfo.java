/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.vend;

import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 供应商拓展表Entity
 * @author liuying
 * @version 2018-02-24
 */
public class BizVendInfo extends DataEntity<BizVendInfo> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// sys_office.id,type=vend
	private String vendName;		// vend_name
	private BizCategoryInfo bizCategoryInfo;		// cate_id
	private String cateName;		// cate_name
	private String code;		// code
	/**
	 * 合同图片 多个以|分隔
	 */
	private String compactPhotos;
	/**
	 * 身份证图片 多个以|分隔
	 */
	private String idCardPhotos;

	private String insertNew;	//新增时，已有数据不能重复添加

	/**
	 * 审核状态
	 */
	private int auditStatus;

	public BizVendInfo() {
		super();
	}

	public BizVendInfo(Integer id){
		super(id);
	}

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=1, max=255, message="vend_name长度必须介于 1 和 255 之间")
	public String getVendName() {
		return vendName;
	}

	public void setVendName(String vendName) {
		this.vendName = vendName;
	}

	
	@Length(min=1, max=50, message="cate_name长度必须介于 1 和 50 之间")
	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}
	
	@Length(min=1, max=20, message="code长度必须介于 1 和 20 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BizCategoryInfo getBizCategoryInfo() {
		return bizCategoryInfo;
	}

	public void setBizCategoryInfo(BizCategoryInfo bizCategoryInfo) {
		this.bizCategoryInfo = bizCategoryInfo;
	}

	public String getInsertNew() {
		return insertNew;
	}

	public void setInsertNew(String insertNew) {
		this.insertNew = insertNew;
	}

	public String getCompactPhotos() {
		return compactPhotos;
	}

	public void setCompactPhotos(String compactPhotos) {
		this.compactPhotos = compactPhotos;
	}

	public String getIdCardPhotos() {
		return idCardPhotos;
	}

	public void setIdCardPhotos(String idCardPhotos) {
		this.idCardPhotos = idCardPhotos;
	}


	public enum  AuditStatus {
		UNAUDITED(0, "未审核"),
		AUDIT_PASS(1, "审核通过"),
		AUDIT_FAILED (2, "驳回"),
		;
		private int status;
		private String desc;

		public int getStatus() {
			return status;
		}

		public String getDesc() {
			return desc;
		}

		AuditStatus(int status, String desc) {
			this.status = status;
			this.desc = desc;
		}
	}
}