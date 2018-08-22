/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity;

import com.wanhutong.backend.common.persistence.TreeEntity;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;


/**
 * 机构Entity
 * @author ThinkGem
 * @version 2013-05-15
 */
public class Office extends TreeEntity<Office> {

	private static final long serialVersionUID = 1L;
//	private Office parent;	// 父级编号
//	private String parentIds; // 所有父级编号
	private Area area;		// 归属区域
	private String code; 	// 机构编码
	private String name; 	// 机构名称
//	private Integer sort;		// 排序
	private String type; 	// 机构类型（1：公司；2：部门；3：小组）
	private String grade; 	// 机构等级（1：一级；2：二级；3：三级；4：四级）
	private String address; // 联系地址
	private String zipCode; // 邮政编码
	private String master; 	// 负责人
	private String phone; 	// 电话
	private String fax; 	// 传真
	private String email; 	// 邮箱
	private String useable;//是否可用
	private User primaryPerson;//主负责人
	private User deputyPerson;//副负责人
	private List<String> childDeptList;//快速添加子部门
	private String level; //钱包等级
	private String gysMobile;	//供应商联系方式
	private String queryMemberGys;	//列表查询列表 供应商、会员

	private String source;
	private String gysFlag;	//用于供应商保存跳转标识
	private User moblieMoeny;	//用于用户钱包查询手机号，采购商手机号，供应商手机号
	private String customerTypeTen;   //用于客户专员查询采购中心,type=10
	private String customerTypeEleven;   //用于客户专员查询采购中心,type=11
	private String customerTypeThirteen;   //用于客户专员查询采购中心,type=13

	private Integer consultantId;
	private Integer centerId;
	private Integer ccStatus;

	private BizVendInfo bizVendInfo;		//供应商拓展

	/**
	 * 供应商新增页面显示，新增地址
	 * */
	private SysOfficeAddress officeAddress;
	/**
	 * 用于添加货架管理员查询
	 * */
	private User shelfInfoUser;
	private Integer locationId;

	private String delRemark ;

	/**
	 * 厂商视频
	 */
	private String vendVideo;

	public String getDelRemark() {
		return delRemark;
	}

	public void setDelRemark(String delRemark) {
		this.delRemark = delRemark;
	}

	/**
	 * 省份
	 */
	private String province;

	/**
	 * 供应商office Id
	 *
	 */
	private Integer vendorId;

	/**
	 * 供应商
	 */
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Office(){
		super();
//		this.sort = 30;
		this.type = "2";
	}

	public Office(Integer id){
		super(id);
	}
	
	public List<String> getChildDeptList() {
		return childDeptList;
	}

	public void setChildDeptList(List<String> childDeptList) {
		this.childDeptList = childDeptList;
	}

	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public User getPrimaryPerson() {
		return primaryPerson;
	}

	public void setPrimaryPerson(User primaryPerson) {
		this.primaryPerson = primaryPerson;
	}

	public User getDeputyPerson() {
		return deputyPerson;
	}

	public void setDeputyPerson(User deputyPerson) {
		this.deputyPerson = deputyPerson;
	}

//	@JsonBackReference
//	@NotNull
	public Office getParent() {
		return parent;
	}

	public void setParent(Office parent) {
		this.parent = parent;
	}
//
//	@Length(min=1, max=2000)
//	public String getParentIds() {
//		return parentIds;
//	}
//
//	public void setParentIds(String parentIds) {
//		this.parentIds = parentIds;
//	}

	@NotNull
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
//
	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//
//	public Integer getSort() {
//		return sort;
//	}
//
//	public void setSort(Integer sort) {
//		this.sort = sort;
//	}
	
	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=1, max=1)
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	@Length(min=0, max=255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min=0, max=100)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(min=0, max=100)
	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	@Length(min=0, max=200)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min=0, max=200)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Length(min=0, max=200)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Length(min=0, max=100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	//	public String getParentId() {
//		return parent != null && parent.getId() != null ? parent.getId() : "0";
//	}
	
	@Override
	public String toString() {
		return name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getConsultantId() {
		return consultantId;
	}

	public void setConsultantId(Integer consultantId) {
		this.consultantId = consultantId;
	}

	public Integer getCenterId() {
		return centerId;
	}

	public void setCenterId(Integer centerId) {
		this.centerId = centerId;
	}

	public Integer getCcStatus() {
		return ccStatus;
	}

	public void setCcStatus(Integer ccStatus) {
		this.ccStatus = ccStatus;
	}

	public String getGysFlag() {
		return gysFlag;
	}

	public void setGysFlag(String gysFlag) {
		this.gysFlag = gysFlag;
	}

	public String getGysMobile() {
		return gysMobile;
	}

	public void setGysMobile(String gysMobile) {
		this.gysMobile = gysMobile;
	}

	public User getMoblieMoeny() {
		return moblieMoeny;
	}

	public void setMoblieMoeny(User moblieMoeny) {
		this.moblieMoeny = moblieMoeny;
	}

	public String getCustomerTypeTen() {
		return customerTypeTen;
	}

	public void setCustomerTypeTen(String customerTypeTen) {
		this.customerTypeTen = customerTypeTen;
	}

	public String getCustomerTypeEleven() {
		return customerTypeEleven;
	}

	public void setCustomerTypeEleven(String customerTypeEleven) {
		this.customerTypeEleven = customerTypeEleven;
	}

	public String getCustomerTypeThirteen() {
		return customerTypeThirteen;
	}

	public void setCustomerTypeThirteen(String customerTypeThirteen) {
		this.customerTypeThirteen = customerTypeThirteen;
	}

	public String getQueryMemberGys() {
		return queryMemberGys;
	}

	public void setQueryMemberGys(String queryMemberGys) {
		this.queryMemberGys = queryMemberGys;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public SysOfficeAddress getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(SysOfficeAddress officeAddress) {
		this.officeAddress = officeAddress;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	@XmlTransient
	public BizVendInfo getBizVendInfo() {
		return bizVendInfo;
	}

	public void setBizVendInfo(BizVendInfo bizVendInfo) {
		this.bizVendInfo = bizVendInfo;
	}

	public User getShelfInfoUser() {
		return shelfInfoUser;
	}

	public void setShelfInfoUser(User shelfInfoUser) {
		this.shelfInfoUser = shelfInfoUser;
	}

	public String getVendVideo() {
		return vendVideo;
	}

	public void setVendVideo(String vendVideo) {
		this.vendVideo = vendVideo;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendVideo() {
		return vendVideo;
	}

	public void setVendVideo(String vendVideo) {
		this.vendVideo = vendVideo;
	}
}