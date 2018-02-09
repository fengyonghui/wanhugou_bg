/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.TreeService;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.category.BizCategoryInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import org.omg.PortableInterceptor.INACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

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
	@Resource
	private CommonImgService commonImgService;
	protected Logger log = LoggerFactory.getLogger(getClass());//日志

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
	public List<BizCategoryInfo> findAllCategory(){
		return UserUtils.getCategoryInfoList(new BizCatelogInfo());

	}
	public Page<BizCategoryInfo> findPage(Page<BizCategoryInfo> page, BizCategoryInfo bizCategoryInfo) {
		return super.findPage(page, bizCategoryInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCategoryInfo bizCategoryInfo) {
		super.save(bizCategoryInfo);
		BizCatePropValue catePropValue = new BizCatePropValue();
			bizCategoryInfoDao.deleteCatePropInfoReal(bizCategoryInfo);

		if (bizCategoryInfo.getPropertyMap() != null) {
			for (Map.Entry<String, BizCatePropertyInfo> entry : bizCategoryInfo.getPropertyMap().entrySet()) {
				Integer propId = Integer.parseInt(entry.getKey());
				BizCatePropertyInfo bizCatePropertyInfo = entry.getValue();
				PropertyInfo propertyInfo = propertyInfoService.get(propId);
				bizCatePropertyInfo.setName(propertyInfo.getName());
				bizCatePropertyInfo.setDescription(propertyInfo.getDescription());
				bizCatePropertyInfo.setCategoryInfo(bizCategoryInfo);
			//	bizCatePropertyInfo.setPropertyInfo(propertyInfo);
				bizCatePropertyInfoService.save(bizCatePropertyInfo);
				String catePropertyValueStr = bizCatePropertyInfo.getCatePropertyValues();
				if (catePropertyValueStr != null && !"".equals(catePropertyValueStr)) {
					String[] catePropertyValues = catePropertyValueStr.split(",");
					for (int j = 0; j < catePropertyValues.length; j++) {
						catePropValue.setId(null);
						Integer propValueId = Integer.parseInt(catePropertyValues[j].trim());
						PropValue propValue = propValueService.get(propValueId);
						catePropValue.setPropertyInfo(propertyInfo);
						catePropValue.setSource("sys");
						catePropValue.setPropName(bizCatePropertyInfo.getName());
						catePropValue.setCatePropertyInfo(bizCatePropertyInfo);
						catePropValue.setValue(propValue.getValue());
						String code= HanyuPinyinHelper.getFirstLetters(propValue.getValue(), HanyuPinyinCaseType.UPPERCASE);
						catePropValue.setCode(code);
						catePropValue.setPropValue(propValue);
						bizCatePropValueService.save(catePropValue);

					}
				}

			}
		}
		CommonImg commonImg=null;
		if(bizCategoryInfo.getImgId()==null){
			commonImg=new CommonImg();
		}else {
			commonImg=commonImgService.get(bizCategoryInfo.getImgId());
		}
		commonImg.setImgType(ImgEnum.CATEGORY_TYPE.getCode());
		commonImg.setObjectName("biz_category_info");
		commonImg.setObjectId(bizCategoryInfo.getId());
		if(StringUtils.isBlank(bizCategoryInfo.getCatePhoto())){
			commonImgService.delete(commonImg);
		}
		else if (StringUtils.isNotBlank(bizCategoryInfo.getCatePhoto())){

			if (bizCategoryInfo.getCatePhoto().contains(DsConfig.getImgServer())) {
				return;
			}else {
				commonImgService.delete(commonImg);
			}
			String categorCatePhoto=null;
			try {
				categorCatePhoto = URLDecoder.decode(bizCategoryInfo.getCatePhoto(), "utf-8");//分类图片转换编码
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				log.error("分类图片转换编码." + e.getMessage(), e);
			}
			String pathFile = Global.getUserfilesBaseDir() + categorCatePhoto;
			String ossPath = AliOssClientUtil.uploadFile(pathFile, true);

			commonImg.setId(null);
			commonImg.setImgPath("/"+ossPath);
			commonImg.setImgSort(10);
			commonImg.setImgServer(DsConfig.getImgServer());
			commonImgService.save(commonImg);
		}

		UserUtils.removeCache(UserUtils.CACHE_CATEGORYINFO_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCategoryInfo bizCategoryInfo) {
		super.delete(bizCategoryInfo);
		UserUtils.removeCache(UserUtils.CACHE_CATEGORYINFO_LIST);
	}

	public List<BizCategoryInfo> findListByBrandId(BizCategoryInfo bizCategoryInfo){
		return bizCategoryInfoDao.findListByBrandId(bizCategoryInfo);
	}
	
}