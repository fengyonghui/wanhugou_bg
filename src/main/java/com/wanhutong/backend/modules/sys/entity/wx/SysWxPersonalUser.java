/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.wx;

import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 注册用户Entity
 * @author Oy
 * @version 2018-04-10
 */
public class SysWxPersonalUser extends DataEntity<SysWxPersonalUser> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户
	private String nickname;		// 昵称
	private String headImgUrl;		// 头像
	private String openid;		// openid
	private Integer subscribe;		// subscribe
	private String sex;		// 性别

	public SysWxPersonalUser() {
		super();
	}

	public SysWxPersonalUser(Integer id){
		super(id);
	}

	@NotNull(message="user_id不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=50, message="nickname长度必须介于 1 和 50 之间")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	@Length(min=1, max=255, message="head_img_url长度必须介于 1 和 255 之间")
	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	
	@Length(min=1, max=50, message="openid长度必须介于 1 和 50 之间")
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	@Length(min=1, max=2, message="sex长度必须介于 1 和 2 之间")
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Integer subscribe) {
		this.subscribe = subscribe;
	}
}