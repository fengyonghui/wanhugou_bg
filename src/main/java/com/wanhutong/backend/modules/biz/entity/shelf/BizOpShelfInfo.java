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
	private Integer type;	//1. banner 2，货架；3:本地备货-对应采购中心
	private List<BizOpShelfSku> opShelfSkusList;
	private String OpShelfSkus;
	private List<User> userList;
	private User user;
	private String userIds;
	private Integer columID;	//用于根据货架ID查询type

	
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

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

	public Integer getColumID() {
		return columID;
	}

	public void setColumID(Integer columID) {
		this.columID = columID;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}