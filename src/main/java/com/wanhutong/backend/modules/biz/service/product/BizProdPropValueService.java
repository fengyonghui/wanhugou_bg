/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanhutong.backend.modules.biz.dao.product.BizProdPropValueDao;
import com.wanhutong.backend.modules.biz.dao.product.BizProdPropertyInfoDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;

import javax.annotation.Resource;


/**
 * 记录产品所有属性值Service
 * @author zx
 * @version 2017-12-14
 */
@Service
@Transactional(readOnly = true)
public class BizProdPropValueService extends CrudService<BizProdPropValueDao, BizProdPropValue> {

	//@Resource
	//private BizProdPropertyInfoService bizProdPropertyInfoService;
	@Autowired
	private BizProdPropertyInfoDao bizProdPropertyInfoDao;

	public BizProdPropValue get(Integer id) {
		return super.get(id);
	}
	
	public Map<String,List<BizProdPropValue>> findListMap(BizProdPropValue bizProdPropValue) {
		List<BizProdPropValue> list= super.findList(bizProdPropValue);
		Map<Integer,List<BizProdPropValue>> map = new HashMap<Integer,List<BizProdPropValue>>();
		Map<String,List<BizProdPropValue>> propCateMap = new HashMap<String,List<BizProdPropValue>>();
		List<BizProdPropValue>  propValueList=null;
		for(BizProdPropValue prodPropValue:list){
			Integer key=prodPropValue.getProdPropertyInfo().getId();
			if(map.containsKey(key)){
				List<BizProdPropValue> catePropValues = map.get(key);
				map.remove(key);
				catePropValues.add(prodPropValue);
				map.put(key,catePropValues);
			}
			else {
				propValueList=new ArrayList<BizProdPropValue>();
				propValueList.add(prodPropValue);
				map.put(key,propValueList);
			}
		}
		for(Integer key :map.keySet()) {
			BizProdPropertyInfo propertyInfo=bizProdPropertyInfoDao.get(key);
			String sKey = propertyInfo.getPropName()+","+propertyInfo.getPropDescription();
			propCateMap.put(sKey,map.get(key));
		}
		return propCateMap;
	}
	
	public Page<BizProdPropValue> findPage(Page<BizProdPropValue> page, BizProdPropValue bizProdPropValue) {
		return super.findPage(page, bizProdPropValue);
	}
	
	@Transactional(readOnly = false)
	public void save(BizProdPropValue bizProdPropValue) {
		super.save(bizProdPropValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizProdPropValue bizProdPropValue) {
		super.delete(bizProdPropValue);
	}
	
}