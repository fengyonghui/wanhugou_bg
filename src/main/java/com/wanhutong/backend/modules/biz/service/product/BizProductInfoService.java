/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropertyInfoService;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoService;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;

import javax.annotation.Resource;


/**
 * 产品信息表Service
 * @author zx
 * @version 2017-12-13
 */
@Service
@Transactional(readOnly = true)
public class BizProductInfoService extends CrudService<BizProductInfoDao, BizProductInfo> {
	@Resource
	private BizCatePropValueService bizCatePropValueService;
	@Resource
	private BizCatePropertyInfoService bizCatePropertyInfoService;
	@Resource
	private PropertyInfoService propertyInfoService;
	@Resource
	private BizProdPropertyInfoService bizProdPropertyInfoService;
	@Autowired
	private BizProductInfoDao bizProductInfoDao;
	@Resource
	private BizProdPropValueService bizProdPropValueService;
	@Resource
	private PropValueService propValueService;


	public BizProductInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizProductInfo> findList(BizProductInfo bizProductInfo) {
		return super.findList(bizProductInfo);
	}
	
	public Page<BizProductInfo> findPage(Page<BizProductInfo> page, BizProductInfo bizProductInfo) {
		return super.findPage(page, bizProductInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizProductInfo bizProductInfo) {
		BizCatePropValue bizCatePropValue=bizCatePropValueService.get(bizProductInfo.getCatePropValue().getId());
		bizProductInfo.setBrandName(bizCatePropValue.getValue());
		super.save(bizProductInfo);
		bizProductInfoDao.deleteProdCate(bizProductInfo);
		if (bizProductInfo.getCategoryInfoList().size() > 0){
			bizProductInfoDao.insertProdCate(bizProductInfo);
		}
		BizProdPropValue prodPropValue = new BizProdPropValue();
		BizProdPropertyInfo prodPropertyInfo = new BizProdPropertyInfo();
		String catePropertyInfoStr = bizProductInfo.getProdPropertyInfos();
		bizProductInfoDao.deleteProdPropInfoReal(bizProductInfo);
		if (catePropertyInfoStr != null && !catePropertyInfoStr.isEmpty()) {
			String[] catePropertyInfos = catePropertyInfoStr.split(",");
			for (int i = 0; i < catePropertyInfos.length; i++) {
				Set<String> keySet = bizProductInfo.getPropertyMap().keySet();
				if (!keySet.contains(catePropertyInfos[i])) {
					Integer propId = Integer.parseInt(catePropertyInfos[i]);
				//	if(bizProductInfo.getSource()!=null && "sys".equals(bizProductInfo.getSource())){
						PropertyInfo propertyInfo = propertyInfoService.get(propId);
						prodPropertyInfo.setPropName(propertyInfo.getName());
						prodPropertyInfo.setPropDescription(propertyInfo.getDescription());
						prodPropValue.setPropertyInfo(propertyInfo);

//					}else if(bizProductInfo.getSource()!=null && "cate".equals(bizProductInfo.getSource())){
//						BizCatePropertyInfo catePropertyInfo = bizCatePropertyInfoService.get(propId);
//						prodPropertyInfo.setPropName(catePropertyInfo.getName());
//						prodPropertyInfo.setPropDescription(catePropertyInfo.getDescription());
//						prodPropValue.setCatePropertyInfo(catePropertyInfo);
//
//					}

					prodPropertyInfo.setProductInfo(bizProductInfo);
					bizProdPropertyInfoService.save(prodPropertyInfo);
					prodPropValue.setId(null);
					prodPropValue.setProdPropertyInfo(prodPropertyInfo);
					prodPropValue.setSource("sys");
					prodPropValue.setPropName(prodPropertyInfo.getPropName());
					bizProdPropValueService.save(prodPropValue);

				}
			}

		}
		if (bizProductInfo.getPropertyMap() != null) {
			for (Map.Entry<String, BizProdPropertyInfo> entry : bizProductInfo.getPropertyMap().entrySet()) {
				Integer propId = Integer.parseInt(entry.getKey());
				BizProdPropertyInfo bizProdPropertyInfo = entry.getValue();
				//if(bizProductInfo.getSource()!=null && "sys".equals(bizProductInfo.getSource())){
					PropertyInfo propertyInfo = propertyInfoService.get(propId);
					bizProdPropertyInfo.setPropName(propertyInfo.getName());
					bizProdPropertyInfo.setPropDescription(propertyInfo.getDescription());
					bizProdPropertyInfo.setProductInfo(bizProductInfo);

				bizProdPropertyInfoService.save(bizProdPropertyInfo);

				String catePropertyValueStr = bizProdPropertyInfo.getProdPropertyValues();
				if (catePropertyValueStr != null && !"".equals(catePropertyValueStr)) {
					String[] catePropertyValues = catePropertyValueStr.split(",");
					for (int j = 0; j < catePropertyValues.length; j++) {
						prodPropValue.setId(null);
						Integer propValueId = Integer.parseInt(catePropertyValues[j].trim());
						PropValue propValue = propValueService.get(propValueId);
						prodPropValue.setPropertyInfo(propertyInfo);
						prodPropValue.setSource("sys");
						prodPropValue.setPropName(bizProdPropertyInfo.getPropName());
						prodPropValue.setProdPropertyInfo(bizProdPropertyInfo);
						prodPropValue.setPropValue(propValue.getValue());
						prodPropValue.setSysPropValue(propValue);
						bizProdPropValueService.save(prodPropValue);
					}
				}
			//	}

			}
		}

	}


	@Transactional(readOnly = false)
	public void delete(BizProductInfo bizProductInfo) {
		super.delete(bizProductInfo);
	}
	
}