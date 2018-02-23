/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.biz.dao.category.BizCatePropertyInfoDao;

/**
 * 记录当前分类下的所有属性Service
 * @author liuying
 * @version 2017-12-06
 */
@Service
@Transactional(readOnly = true)
public class BizCatePropertyInfoService extends CrudService<BizCatePropertyInfoDao, BizCatePropertyInfo> {
	@Autowired
	private BizCatePropValueService bizCatePropValueService;

	public BizCatePropertyInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCatePropertyInfo> findList(BizCatePropertyInfo bizCatePropertyInfo) {
		return super.findList(bizCatePropertyInfo);
	}
	
	public Page<BizCatePropertyInfo> findPage(Page<BizCatePropertyInfo> page, BizCatePropertyInfo bizCatePropertyInfo) {
		return super.findPage(page, bizCatePropertyInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCatePropertyInfo bizCatePropertyInfo) {
		super.save(bizCatePropertyInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCatePropertyInfo bizCatePropertyInfo) {
		super.delete(bizCatePropertyInfo);
	}


	public Map<Integer,List<BizCatePropValue>> findMapList(BizCatePropertyInfo bizCatePropertyInfo){
		BizCatePropValue catePropValue=new BizCatePropValue();

		List<BizCatePropertyInfo> list=findList(bizCatePropertyInfo);
		Map<Integer,List<BizCatePropValue>> map=new HashMap<Integer,List<BizCatePropValue>>();
			for(BizCatePropertyInfo info:list){
				catePropValue.setId(null);
				catePropValue.setCatePropertyInfo(info);
				List<BizCatePropValue> valueList=bizCatePropValueService.findList(catePropValue);
			for(BizCatePropValue bizCatePropValue:valueList){
				Integer key=bizCatePropValue.getPropertyInfo().getId();
				if(map.containsKey(key)){
					List<BizCatePropValue> propValues=map.get(key);
					map.remove(key);
					propValues.add(bizCatePropValue);
					map.put(key,propValues);
				}else {
					List<BizCatePropValue> catePropValueList= Lists.newArrayList();
					catePropValueList.add(bizCatePropValue);
					map.put(key,catePropValueList);
				}


			}

		}

		return map;
	}

}