/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanhutong.backend.modules.biz.dao.product.BizProdCateDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProdCate;

import javax.annotation.Resource;


/**
 * 产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多Service
 * @author zx
 * @version 2017-12-14
 */
@Service
@Transactional(readOnly = true)
public class BizProdCateService extends CrudService<BizProdCateDao, BizProdCate> {
	@Resource
	private BizCatePropValueService bizCatePropValueService;
	@Resource
	private PropertyInfoService propertyInfoService;
	@Resource
	private DefaultPropService defaultPropService;

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

	public Map<Integer,List<BizCatePropValue>> findCatePropMap(BizProdCate bizProdCate){
		List<BizCatePropValue> catePropValueList=bizCatePropValueService.findCatePropInfoValue(bizProdCate);
		Map<Integer,List<BizCatePropValue>> map = new HashMap<Integer,List<BizCatePropValue>>();

		List<BizCatePropValue>  propValueList=null;
		for(BizCatePropValue bizCatePropValue:catePropValueList){
			if(bizCatePropValue.getSource()!=null && bizCatePropValue.getSource().equals("sys")){
				Integer key=bizCatePropValue.getPropertyInfo().getId();
				if(map.containsKey(key)){
					List<BizCatePropValue> catePropValues = map.get(key);
					map.remove(key);
					catePropValues.add(bizCatePropValue);
					map.put(key,catePropValues);
				}
				else {
					propValueList=new ArrayList<BizCatePropValue>();
					propValueList.add(bizCatePropValue);
					map.put(key,propValueList);
				}
			}
		}
		return map;
	}

	public Map<String,List<BizCatePropValue>> findCatePropMap4Page(BizProdCate bizProdCate){
		Map<String,List<BizCatePropValue>> propCateMap = new HashMap<String,List<BizCatePropValue>>();
		Map<Integer,List<BizCatePropValue>> map=findCatePropMap( bizProdCate);
		for(Integer key :map.keySet()) {
			PropertyInfo propertyInfo=propertyInfoService.get(key);
			String sKey = propertyInfo.getId()+","+propertyInfo.getName();
			propCateMap.put(sKey,map.get(key));
		}
		return propCateMap;
	}
	public List<BizCatePropValue> findCatePropMap4Brand(BizProdCate bizProdCate){
		List<BizCatePropValue> brandList= new ArrayList<BizCatePropValue>();
		Map<Integer,List<BizCatePropValue>> map=findCatePropMap( bizProdCate);
		List<DefaultProp> list=defaultPropService.findList(new DefaultProp("prop_brand"));
		if(list!=null && list.size()>0) {
			DefaultProp defaultProp = list.get(0);
			for (Integer key :map.keySet()) {
			//	for(BizCatePropValue catePropValue:catePropValueList){
					if(key==Integer.parseInt(defaultProp.getPropValue())){

						brandList=	map.get(key);
					}
			//	}
			}
		}

		return brandList;
	}

}