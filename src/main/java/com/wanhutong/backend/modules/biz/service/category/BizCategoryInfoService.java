/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.TreeService;
import com.wanhutong.backend.modules.biz.dao.category.BizCategoryInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 垂直商品类目表Service
 * @author liuying
 * @version 2017-12-06
 */
@Service
@Transactional(readOnly = true)
public class BizCategoryInfoService extends TreeService<BizCategoryInfoDao, BizCategoryInfo> {
	@Resource
	private PropertyInfoService propertyInfoService;
	@Resource
	private PropValueService propValueService;
	@Resource
	private BizCatePropertyInfoService bizCatePropertyInfoService;
	@Resource
	private BizCatePropValueService bizCatePropValueService;
	@Autowired
	private BizCategoryInfoDao bizCategoryInfoDao;

	public BizCategoryInfo get(Integer id) {
		return super.get(id);
	}
	

	@Transactional(readOnly = true)
	public List<BizCategoryInfo> findList(BizCategoryInfo bizCategoryInfo){
		if(bizCategoryInfo != null){
			bizCategoryInfo.setParentIds(bizCategoryInfo.getParentIds()+"%");
			return dao.findByParentIdsLike(bizCategoryInfo);
		}
		return  new ArrayList<BizCategoryInfo>();
	}
	public List<BizCategoryInfo> findListInfo(BizCatelogInfo catelogInfo){
		return UserUtils.getCategoryInfoList(catelogInfo);
	}
	public Page<BizCategoryInfo> findPage(Page<BizCategoryInfo> page, BizCategoryInfo bizCategoryInfo) {
		return super.findPage(page, bizCategoryInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCategoryInfo bizCategoryInfo) {
		super.save(bizCategoryInfo);
		BizCatePropValue catePropValue=new BizCatePropValue();
		BizCatePropertyInfo catePropertyInfo=new BizCatePropertyInfo();
		String catePropertyInfoStr=bizCategoryInfo.getCatePropertyInfos();
		String[] catePropertyInfos= catePropertyInfoStr.split(",");
		bizCategoryInfoDao.deleteCatePropInfoReal(bizCategoryInfo);
		for(int i=0;i<catePropertyInfos.length;i++){
			Set<String> keySet=bizCategoryInfo.getPropertyMap().keySet();
			if(!keySet.contains(catePropertyInfos[i])){
				Integer propId=Integer.parseInt(catePropertyInfos[i]);
				PropertyInfo propertyInfo=propertyInfoService.get(propId);
				catePropertyInfo.setName(propertyInfo.getName());
				catePropertyInfo.setDescription(propertyInfo.getDescription());
				catePropertyInfo.setCategoryInfo(bizCategoryInfo);
				catePropertyInfo.setPropertyInfo(propertyInfo);
				bizCatePropertyInfoService.save(catePropertyInfo);

			}

		}
		for (Map.Entry<String, BizCatePropertyInfo> entry : bizCategoryInfo.getPropertyMap().entrySet()) {
			Integer propId=Integer.parseInt(entry.getKey());
			BizCatePropertyInfo bizCatePropertyInfo=entry.getValue();
			PropertyInfo propertyInfo=propertyInfoService.get(propId);
			bizCatePropertyInfo.setName(propertyInfo.getName());
			bizCatePropertyInfo.setDescription(propertyInfo.getDescription());
			bizCatePropertyInfo.setCategoryInfo(bizCategoryInfo);
			bizCatePropertyInfo.setPropertyInfo(propertyInfo);
			bizCatePropertyInfoService.save(bizCatePropertyInfo);
			String catePropertyValueStr=bizCatePropertyInfo.getCatePropertyValues();
			if(catePropertyValueStr!=null && !"".equals(catePropertyValueStr)){
				String[] catePropertyValues=catePropertyValueStr.split(",");
				for(int j=0;j<catePropertyValues.length;j++){
					catePropValue.setId(null);
					Integer propValueId=Integer.parseInt(catePropertyValues[j].trim());
					PropValue propValue=propValueService.get(propValueId);
						catePropValue.setCatePropertyInfo(bizCatePropertyInfo);
						catePropValue.setValue(propValue.getValue());
					catePropValue.setPropValue(propValue);
						bizCatePropValueService.save(catePropValue);


				}
			}

		}

		UserUtils.removeCache(UserUtils.CACHE_CATEGORYINFO_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCategoryInfo bizCategoryInfo) {
		super.delete(bizCategoryInfo);
		UserUtils.removeCache(UserUtils.CACHE_CATEGORYINFO_LIST);
	}
	
}