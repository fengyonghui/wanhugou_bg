/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shelf;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.dao.shelf.BizOpShelfSkuDao;

/**
 * 商品上架管理Service
 * @author liuying
 * @version 2017-12-19
 */
@Service
@Transactional(readOnly = true)
public class BizOpShelfSkuService extends CrudService<BizOpShelfSkuDao, BizOpShelfSku> {

	public BizOpShelfSku get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOpShelfSku> findList(BizOpShelfSku bizOpShelfSku) {
		return super.findList(bizOpShelfSku);
	}
	
	public Page<BizOpShelfSku> findPage(Page<BizOpShelfSku> page, BizOpShelfSku bizOpShelfSku) {
		return super.findPage(page, bizOpShelfSku);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOpShelfSku bizOpShelfSku) {
		super.save(bizOpShelfSku);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOpShelfSku bizOpShelfSku) {
		super.delete(bizOpShelfSku);
	}
	
}