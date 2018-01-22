/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 平台总钱包Entity
 * @author OuyangXiutian
 * @version 2018-01-20
 */
public class SysPlatWallet extends DataEntity<SysPlatWallet> {
	
	private static final long serialVersionUID = 1L;
	private Double amount;		// 账户金额

	public SysPlatWallet() {
		super();
	}

	public SysPlatWallet(Integer id){
		super(id);
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

}