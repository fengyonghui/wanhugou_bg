/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import java.util.List;
import java.util.Map;

import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropValueService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropertyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuInfoDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;

import javax.annotation.Resource;

/**
 * 商品skuService
 * @author zx
 * @version 2017-12-07
 */
@Service
@Transactional(readOnly = true)
public class BizSkuInfoService extends CrudService<BizSkuInfoDao, BizSkuInfo> {
	@Resource
	private BizProdPropertyInfoService bizProdPropertyInfoService;
	@Resource
	private BizProdPropValueService bizProdPropValueService;
	@Resource
	private BizSkuPropValueService bizSkuPropValueService;
	@Autowired
	private BizSkuInfoDao bizSkuInfoDao;

	public BizSkuInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuInfo> findList(BizSkuInfo bizSkuInfo) {
		if(bizSkuInfo != null) {
			return super.findList(bizSkuInfo);
		}
		return null;
	}
	
	public Page<BizSkuInfo> findPage(Page<BizSkuInfo> page, BizSkuInfo bizSkuInfo) {
		return super.findPage(page, bizSkuInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSkuInfo bizSkuInfo) {
		super.save(bizSkuInfo);
		BizSkuPropValue bizSkuPropValue = new BizSkuPropValue();
		if (bizSkuInfo.getProdPropMap() != null) {
			bizSkuInfoDao.deleteSkuPropInfoReal(bizSkuInfo);
			for (Map.Entry<String, BizProdPropertyInfo> entry : bizSkuInfo.getProdPropMap().entrySet()) {
				Integer propId = Integer.parseInt(entry.getKey());
				BizProdPropertyInfo bizProdPropertyInfo = entry.getValue();
				BizProdPropertyInfo propertyInfo = bizProdPropertyInfoService.get(propId);
				String prodPropertyValueStr = bizProdPropertyInfo.getProdPropertyValues();
				if (prodPropertyValueStr != null && !"".equals(prodPropertyValueStr)) {
					String[] prodPropertyValues = prodPropertyValueStr.split(",");
					for (int j = 0; j < prodPropertyValues.length; j++) {
						bizSkuPropValue.setId(null);
						Integer propValueId = Integer.parseInt(prodPropertyValues[j].trim());
						BizProdPropValue propValue = bizProdPropValueService.get(propValueId);
						bizSkuPropValue.setProdPropertyInfo(propertyInfo);
						bizSkuPropValue.setPropName(propertyInfo.getPropName());
						bizSkuPropValue.setPropValue(propValue.getPropValue());
						bizSkuPropValue.setProdPropValue(propValue);
						bizSkuPropValue.setSource(propValue.getSource());
						bizSkuPropValue.setSkuInfo(bizSkuInfo);
						bizSkuPropValueService.save(bizSkuPropValue);

					}
				}

			}
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSkuInfo bizSkuInfo) {
		super.delete(bizSkuInfo);
	}
	
}