/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.common.supcan.annotation.treelist.cols.SupCol;
import com.wanhutong.backend.common.utils.Collections3;
import com.wanhutong.backend.common.utils.excel.annotation.ExcelField;
import com.wanhutong.backend.common.utils.excel.fieldtype.RoleListType;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.variety.BizVarietyUserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 用户Entity
 * @author ThinkGem
 * @version 2013-12-05
 */
public class User extends DataEntity<User> {

	private static final long serialVersionUID = 1L;
	private Office company;	// 归属公司
	private Office office;	// 归属部门
	private String loginName;// 登录名
	private String password;// 密码
	private String no;		// 工号
	private String name;	// 姓名
	private String email;	// 邮箱
	private String phone;	// 电话
	private String mobile;	// 手机
	private String userType;// 用户类型
	private String loginIp;	// 最后登陆IP
	private Date loginDate;	// 最后登陆日期
	private String loginFlag;	// 是否允许登陆
	private String photo;	// 头像

	private String oldLoginName;// 原登录名
	private String newPassword;	// 新密码
	
	private String oldLoginIp;	// 上次登陆IP
	private Date oldLoginDate;	// 上次登陆日期
	
	private Role role;	// 根据角色查询用户条件
	private String conn;//用于标识客户专员和仓储专员
	private User user;
	private Office cent;

	/**
	 * 用户列表标识符
	 * */
	private String userFlag;
	/**
	 * 用于品类主管页面选择的品类
	 * */
	private List<BizVarietyInfo> varityList = Lists.newArrayList();

	private Integer consultantId;
	private Integer centerId;
	private Integer ccStatus;

	/**
	 * 会员搜索查看 采购商的下单相关信息，品类主管的商品统计
	 * */
	private BizOrderHeader userOrder;
	private String ordrHeaderStartTime;//日期查询
	private String orderHeaderEedTime;//日期查询
	private BizVarietyInfo varietyInfoId;

	/**
	 * 用户作为主负责人所属的供应商
	 */
	private Office vendor;

	/**
	 * 头像
	 */
	private String headPhotos;

	public String getConn() {
		return conn;
	}

	public void setConn(String conn) {
		this.conn = conn;
	}

	private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

	public User() {
		super();
		this.loginFlag = Global.YES;
	}
	
	public User(Integer id){
		super(id);
	}

	public User(Integer id, String loginName){
		super(id);
		this.loginName = loginName;
	}

	public User(Role role){
		super();
		this.role = role;
	}
	
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

	/**
	 * 系统设置->用户列表页，搜索条件
	 */
	private String searchName;
	private String searchMobile;
	private String searchLoginName;

	private String searchScr;

	/**
	 * 经销店添加页面，通过手机号关联经销店
	 */
	private String officeMobile;// 登录名

	@SupCol(isUnique="true", isHide="true")
	@ExcelField(title="ID", type=1, align=2, sort=1)
	public Integer getId() {
		return id;
	}

	@JsonIgnore
	@NotNull(message="归属公司不能为空")
	@ExcelField(title="归属公司", align=2, sort=20)
	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}
	
	@JsonIgnore
	@NotNull(message="归属部门不能为空")
	@ExcelField(title="归属部门", align=2, sort=25)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Length(min=1, max=100, message="登录名长度必须介于 1 和 100 之间")
	@ExcelField(title="登录名", align=2, sort=30)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@JsonIgnore
	@Length(min=1, max=100, message="密码长度必须介于 1 和 100 之间")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Length(min=1, max=100, message="姓名长度必须介于 1 和 100 之间")
	@ExcelField(title="姓名", align=2, sort=40)
	public String getName() {
		return name;
	}
	
	@Length(min=1, max=100, message="工号长度必须介于 1 和 100 之间")
	@ExcelField(title="工号", align=2, sort=45)
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Email(message="邮箱格式不正确")
	@Length(min=0, max=200, message="邮箱长度必须介于 1 和 200 之间")
	@ExcelField(title="邮箱", align=1, sort=50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Length(min=0, max=200, message="电话长度必须介于 1 和 200 之间")
	@ExcelField(title="电话", align=2, sort=60)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min=0, max=200, message="手机长度必须介于 1 和 200 之间")
	@ExcelField(title="手机", align=2, sort=70)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@ExcelField(title="备注", align=1, sort=900)
	public String getRemarks() {
		return remarks;
	}
	
	@Length(min=0, max=100, message="用户类型长度必须介于 1 和 100 之间")
	@ExcelField(title="用户类型", align=2, sort=80, dictType="sys_user_type")
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@ExcelField(title="创建时间", type=0, align=1, sort=90)
	public Date getCreateDate() {
		return createDate;
	}

	@ExcelField(title="最后登录IP", type=1, align=1, sort=100)
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="最后登录日期", type=1, align=1, sort=110)
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getOldLoginName() {
		return oldLoginName;
	}

	public void setOldLoginName(String oldLoginName) {
		this.oldLoginName = oldLoginName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldLoginIp() {
		if (oldLoginIp == null){
			return loginIp;
		}
		return oldLoginIp;
	}

	public void setOldLoginIp(String oldLoginIp) {
		this.oldLoginIp = oldLoginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOldLoginDate() {
		if (oldLoginDate == null){
			return loginDate;
		}
		return oldLoginDate;
	}

	public void setOldLoginDate(Date oldLoginDate) {
		this.oldLoginDate = oldLoginDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@JsonIgnore
	@ExcelField(title="拥有角色", align=1, sort=800, fieldType=RoleListType.class)
	public List<Role> getRoleList() {
		return roleList;
	}
	
	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	@JsonIgnore
	public List<Integer> getRoleIdList() {
		List<Integer> roleIdList = Lists.newArrayList();
		for (Role role : roleList) {
			roleIdList.add(role.getId());
		}
		return roleIdList;
	}

	public void setRoleIdList(List<String> roleIdList) {
		roleList = Lists.newArrayList();
		for (String roleId : roleIdList) {
			Role role = new Role();
			role.setId(Integer.valueOf(roleId));
			roleList.add(role);
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Office getCent() {
		return cent;
	}

	public void setCent(Office cent) {
		this.cent = cent;
	}

	/**
	 * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
	 */
	public String getRoleNames() {
		return Collections3.extractToString(roleList, "name", ",");
	}
	
	public boolean isAdmin(){
		return isAdmin(this.id);
	}
	
	public static boolean isAdmin(Integer id){
		return id != null && (1==id);
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

	public String getUserFlag() {
		return userFlag;
	}

	public void setUserFlag(String userFlag) {
		this.userFlag = userFlag;
	}

	public BizOrderHeader getUserOrder() {
		return userOrder;
	}

	public void setUserOrder(BizOrderHeader userOrder) {
		this.userOrder = userOrder;
	}

	public Office getVendor() {
		return vendor;
	}

	public void setVendor(Office vendor) {
		this.vendor = vendor;
	}

	public BizVarietyInfo getVarietyInfoId() {
		return varietyInfoId;
	}

	public void setVarietyInfoId(BizVarietyInfo varietyInfoId) {
		this.varietyInfoId = varietyInfoId;
	}

	public String getOrdrHeaderStartTime() {
		return ordrHeaderStartTime;
	}

	public void setOrdrHeaderStartTime(String ordrHeaderStartTime) {
		this.ordrHeaderStartTime = ordrHeaderStartTime;
	}

	public String getOrderHeaderEedTime() {
		return orderHeaderEedTime;
	}

	public void setOrderHeaderEedTime(String orderHeaderEedTime) {
		this.orderHeaderEedTime = orderHeaderEedTime;
	}

	public List<BizVarietyInfo> getVarityList() {
		return varityList;
	}

	public void setVarityList(List<BizVarietyInfo> varityList) {
		this.varityList = varityList;
	}

	public String getHeadPhotos() {
		return headPhotos;
	}

	public void setHeadPhotos(String headPhotos) {
		this.headPhotos = headPhotos;
	}

	@JsonIgnore
	public List<Integer> getVarIdList() {
		List<Integer> varIdList = Lists.newArrayList();
		for (BizVarietyInfo var : varityList) {
			varIdList.add(var.getId());
		}
		return varIdList;
	}

	public void setVarIdList(List<String> varIdList) {
		varityList = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(varIdList)){
			for (String roleId : varIdList) {
				BizVarietyInfo bizVarietyInfo = new BizVarietyInfo();
				bizVarietyInfo.setId(Integer.valueOf(roleId));
				varityList.add(bizVarietyInfo);
			}
		}
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getSearchMobile() {
		return searchMobile;
	}

	public void setSearchMobile(String searchMobile) {
		this.searchMobile = searchMobile;
	}

	public String getSearchLoginName() {
		return searchLoginName;
	}

	public void setSearchLoginName(String searchLoginName) {
		this.searchLoginName = searchLoginName;
	}

	public String getSearchScr() {
		return searchScr;
	}

	public void setSearchScr(String searchScr) {
		this.searchScr = searchScr;
	}

	public String getOfficeMobile() {
		return officeMobile;
	}

	public void setOfficeMobile(String officeMobile) {
		this.officeMobile = officeMobile;
	}
}