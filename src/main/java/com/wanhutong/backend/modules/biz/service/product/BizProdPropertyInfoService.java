/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.List;

import com.wanhutong.backend.modules.biz.dao.product.BizProdPropertyInfoDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * 属性表Service
 * @author zx
 * @version 2017-12-14
 */
@Service
@Transactional(readOnly = true)
public class BizProdPropertyInfoService extends CrudService<BizProdPropertyInfoDao, BizProdPropertyInfo> {
	@Resource
	private BizProdPropValueService bizProdPropValueService;

	public BizProdPropertyInfo get(Integer id) {
		return super.get(id);
	}

	public List<BizProdPropertyInfo> findList(BizProdPropertyInfo bizProdPropertyInfo) {
		return super.findList(bizProdPropertyInfo);
	}

	public Page<BizProdPropertyInfo> findPage(Page<BizProdPropertyInfo> page, BizProdPropertyInfo bizProdPropertyInfo) {
		return super.findPage(page, bizProdPropertyInfo);
	}

	@Transactional(readOnly = false)
	public void save(BizProdPropertyInfo bizProdPropertyInfo) {
		super.save(bizProdPropertyInfo);
		List<BizProdPropValue>prodPropValueList=bizProdPropertyInfo.getProdPropValueList();
		if(prodPropValueList!=null){
			for(BizProdPropValue prodPropValue:prodPropValueList){
				prodPropValue.setProdPropertyInfo(bizProdPropertyInfo);
				bizProdPropValueService.save(prodPropValue);
			}
		}
	}

	@Transactional(readOnly = false)
	public void delete(BizProdPropertyInfo bizProdPropertyInfo) {
		super.delete(bizProdPropertyInfo);
	}

}