/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.sku;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;

import java.util.List;

/**
 * 商品skuDAO接口
 * @author zx
 * @version 2017-12-07
 */
@MyBatisDao
public interface BizSkuInfoDao extends CrudDao<BizSkuInfo> {
	
	/*
	 * 获取sku商品的id
	 */
	BizSkuInfo get(Integer id);
	
	/*
	 * 进行sku商品的查找
	 */
	List<BizSkuInfo> findList(BizSkuInfo bizSkuInfo);
	
	/*
	 * 进行sku商品的增加
	 */
	int insert(BizSkuInfo bizSkuInfo);
	
	/*
	 * 进行sku商品的更新
	 */
	int update(BizSkuInfo bizSkuInfo);
	
	/*
	 * 进行sku商品的删除
	 */
	int delete(BizSkuInfo bizSkuInfo);
}