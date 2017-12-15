/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.List;

import com.wanhutong.backend.modules.biz.dao.product.BizProdCateDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProdCate;


/**
 * 产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多Service
 * @author zx
 * @version 2017-12-14
 */
@Service
@Transactional(readOnly = true)
public class BizProdCateService extends CrudService<BizProdCateDao, BizProdCate> {

	public BizProdCate get(Integer id) {
		return super.get(id);
	}

	public List<BizProdCate> findList(BizProdCate bizProdCate) {
		return super.findList(bizProdCate);
	}

	public Page<BizProdCate> findPage(Page<BizProdCate> page, BizProdCate bizProdCate) {
		return super.findPage(page, bizProdCate);
	}

	@Transactional(readOnly = false)
	public void save(BizProdCate bizProdCate) {
		super.save(bizProdCate);
	}

	@Transactional(readOnly = false)
	public void delete(BizProdCate bizProdCate) {
		super.delete(bizProdCate);
	}

}