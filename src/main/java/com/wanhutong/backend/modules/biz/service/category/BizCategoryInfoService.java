/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.TreeService;
import com.wanhutong.backend.common.utils.DsConfig;
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
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public List<BizCategoryInfo> findAllList(){
		return super.findList(new BizCategoryInfo());
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

		if (bizCategoryInfo.getCatePropertyInfos() != null) {
			String[] propInfoValue=bizCategoryInfo.getCatePropertyInfos().split(",");
			BizCatePropertyInfo bizCatePropertyInfo =new BizCatePropertyInfo();
			Map<Integer,List<String>> map=new HashMap<>();
			for(int i=0;i<propInfoValue.length;i++) {
				String[] infoValue = propInfoValue[i].split("-");
				Integer key = Integer.parseInt(infoValue[0]);
				if (map.containsKey(key)) {
					List<String> list = map.get(key);
					map.remove(key);
					list.add(infoValue[1]);
					map.put(key, list);
				} else {
					List<String> list = Lists.newArrayList();
					list.add(infoValue[1]);
					map.put(Integer.parseInt(infoValue[0]), list);
				}
			}

			for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
				Integer propId = entry.getKey();
				PropertyInfo propertyInfo = propertyInfoService.get(propId);
				bizCatePropertyInfo.setId(null);
				bizCatePropertyInfo.setName(propertyInfo.getName());
				bizCatePropertyInfo.setDescription(propertyInfo.getDescription());
				bizCatePropertyInfo.setCategoryInfo(bizCategoryInfo);
				bizCatePropertyInfoService.save(bizCatePropertyInfo);

				List<String> catePropertyValueList=entry.getValue();

				for(int i=0;i<catePropertyValueList.size();i++){
					Integer propValueId = Integer.parseInt(catePropertyValueList.get(i).trim());
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
	@Override
	public void delete(BizCategoryInfo bizCategoryInfo) {
		super.delete(bizCategoryInfo);
		UserUtils.removeCache(UserUtils.CACHE_CATEGORYINFO_LIST);
	}

	public List<BizCategoryInfo> findListByBrandId(BizCategoryInfo bizCategoryInfo){
		return bizCategoryInfoDao.findListByBrandId(bizCategoryInfo);
	}

	public List<BizCategoryInfo> findByIds(String tagIdStr) {
		String[] tagIdArr = tagIdStr.split(",");
		@SuppressWarnings("unchecked")
		List<BizCategoryInfo> categoryInfoList = (List<BizCategoryInfo>)UserUtils.getCache(UserUtils.CACHE_CATEGORYINFO_LIST);
		if (categoryInfoList == null) {
			categoryInfoList = bizCategoryInfoDao.findAllList(new BizCategoryInfo());
			UserUtils.putCache(UserUtils.CACHE_CATEGORYINFO_LIST, categoryInfoList);
		}
		List<BizCategoryInfo> result = Lists.newArrayList();
		for (BizCategoryInfo b : categoryInfoList) {
			if (ArrayUtils.contains(tagIdArr, String.valueOf(b.getId()))) {
				result.add(b);
			}
		}
		return result;
	}
}