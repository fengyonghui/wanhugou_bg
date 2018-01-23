/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import java.io.File;
import java.util.*;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.product.BizProdCate;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.product.BizProdCateService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropValueService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropertyInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
	@Resource
	private CommonImgService commonImgService;
	@Autowired
	private BizSkuInfoDao bizSkuInfoDao;
	@Autowired
	private BizProductInfoDao bizProductInfoDao;
	@Autowired
	private BizProdCateService bizProdCateService;
	@Autowired
	private OfficeService officeService;

	public BizSkuInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuInfo> findList(BizSkuInfo bizSkuInfo) {
		if(bizSkuInfo != null) {
			return super.findList(bizSkuInfo);
		}
		return null;
	}

	public Map<String, List<BizSkuInfo>> findListForProd(BizSkuInfo bizSkuInfo) {
		List<BizSkuInfo> skuInfoList=super.findList(bizSkuInfo);
		Map<BizProductInfo,List<BizSkuInfo>> map=new HashMap<BizProductInfo,List<BizSkuInfo>>();
		Map<String,List<BizSkuInfo>> listMap=new HashMap<String, List<BizSkuInfo>>();
		for(BizSkuInfo skuInfo:skuInfoList){
			BizSkuInfo info=findListProd(skuInfo);
			if(skuInfo.getSkuType()!=null && SkuTypeEnum.stateOf(skuInfo.getSkuType())!=null){
				info.setSkuTypeName(SkuTypeEnum.stateOf(skuInfo.getSkuType()).getName());
			}

			BizProductInfo bizProductInfo=info.getProductInfo();
			if(map.containsKey(bizProductInfo)){
				List<BizSkuInfo> skuInfos = map.get(bizProductInfo);
				map.remove(bizProductInfo);
				skuInfos.add(info);
				map.put(bizProductInfo,skuInfos);
			}
			else {
				List<BizSkuInfo>infoList=new ArrayList<BizSkuInfo>();
				infoList.add(info);
				map.put(bizProductInfo,infoList);
			}
		}
		for(BizProductInfo productInfo :map.keySet()) {
			String sKey="";
			if(productInfo.getOffice()==null){
				 sKey = productInfo.getId()+","+productInfo.getName()+","+productInfo.getImgUrl()+","+productInfo.getCateNames()+","
						+productInfo.getProdCode()+","+null+","+productInfo.getBrandName();

			}else {
				 sKey = productInfo.getId()+","+productInfo.getName()+","+productInfo.getImgUrl()+","+productInfo.getCateNames()+","
						+productInfo.getProdCode()+","+productInfo.getOffice().getName()+","+productInfo.getBrandName();

			}
							;
			listMap.put(sKey,map.get(productInfo));
		}

		return listMap;
	}

	/**
	 * 返回SKU获取SKU组合数据
	 * @param skuInfo
	 * @return
	 */
	public BizSkuInfo findListProd(BizSkuInfo skuInfo){
		Integer prodId=	skuInfo.getProductInfo().getId();
		BizProductInfo bizProductInfo=bizProductInfoDao.get(prodId);
		BizSkuPropValue bizSkuPropValue=new BizSkuPropValue();
        bizSkuPropValue.setSkuInfo(skuInfo);
        List<BizSkuPropValue> skuPropValueList=bizSkuPropValueService.findList(bizSkuPropValue);
        StringBuffer skuPropName=new StringBuffer();
        for(BizSkuPropValue skuPropValue:skuPropValueList){
            skuPropName.append("-");
            skuPropName.append(skuPropValue.getPropValue());
        }
        String propNames="";
        if(skuPropName.toString().length()>1){
            propNames =skuPropName.toString().substring(1);
        }

        skuInfo.setSkuPropertyInfos(propNames);
		if(bizProductInfo!=null && bizProductInfo.getOffice()!=null){
			Office	office=officeService.get(bizProductInfo.getOffice().getId());
			bizProductInfo.setOffice(office);
//			BizProdCate bizProdCate=new BizProdCate();
//			bizProdCate.setProductInfo(bizProductInfo);
//			List<BizProdCate> prodCateList=bizProdCateService.findList(bizProdCate);
//			StringBuffer cateName=new StringBuffer("\\/");
//			for(BizProdCate prodCate:prodCateList){
//				cateName.append(prodCate.getCategoryInfo().getName());
//
//			}
//			String cateNames=cateName.toString().substring(2);
//			bizProductInfo.setCateNames(cateNames);
		}

		skuInfo.setProductInfo(bizProductInfo);
		return skuInfo;

	}

 public  List<BizSkuInfo> findAllList(){
		return bizSkuInfoDao.findAllList(new BizSkuInfo());
 }

 public Page<BizSkuInfo> findPage(Page<BizSkuInfo> page, BizSkuInfo bizSkuInfo) {
		return super.findPage(page, bizSkuInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSkuInfo bizSkuInfo) {
 	 BizProductInfo bizProductInfo=bizProductInfoDao.get(bizSkuInfo.getProductInfo().getId());
		String prodCode=bizProductInfo.getProdCode().length()>4?bizProductInfo.getProdCode().substring(0,4):bizProductInfo.getProdCode();
		String vendId=autoGenericCode(bizProductInfo.getOffice().getId().toString(),4);
		logger.info("prodCode======="+prodCode+"======="+vendId);
 		 super.save(bizSkuInfo);
 		 String partNo=prodCode+vendId+autoGenericCode(bizSkuInfo.getId().toString(),6);
		bizSkuInfo.setPartNo(partNo);
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
		//sku图片保存
		saveCommonImg(bizSkuInfo);
	}

	private String autoGenericCode(String code, int num) {
		String result = "";
		// 保留num的位数
    // 0 代表前面补充0
		// num 代表长度为4
		// d 代表参数为正数型
		result = String.format("%0" + num + "d", Integer.parseInt(code) + 1);

		return result;
	}

	@Transactional(readOnly = false)
	public void saveCommonImg(BizSkuInfo bizSkuInfo) {
		String photos=bizSkuInfo.getPhotos();
		CommonImg commonImg=new CommonImg();
		User user = UserUtils.getUser();
		String pahtPrefix = AliOssClientUtil.getPahtPrefix();
		String s = DateUtils.formatDate(new Date()).replaceAll("-", "");
		if(photos!=null && !"".equals(photos)) {
			photos = photos.substring(1);
			commonImg.setImgType(ImgEnum.SKU_TYPE.getCode());
			String[]photoArr=photos.split("\\|");
			if(photoArr.length>=1){
				commonImg.setObjectId(bizSkuInfo.getId());
				commonImg.setObjectName("biz_sku_info");
				commonImgService.deleteCommonImg(commonImg);
				for (int i=0;i<photoArr.length;i++){
					int a = photoArr[i].lastIndexOf("/")+1;
					String photoName = photoArr[i].substring(a);
					String imgType=photoName.substring(photoName.indexOf("."));
					String folder = AliOssClientUtil.getFolder();
					String path =  folder + "/" + pahtPrefix +""+user.getCompany().getId() +"/" + user.getId() +"/" + s +"/" ;
					String  pathFile = Global.getUserfilesBaseDir()+photoArr[i];
					String  pathFile2 = Global.getUserfilesBaseDir() + photoArr[i].substring(0,a-1);
					File file = new File(pathFile);
					String photoNewName=System.currentTimeMillis()+""+(GenerateOrderUtils.getRandomNum())+imgType;
					File file2 = new File(pathFile2+"/"+photoNewName);
					AliOssClientUtil aliOssClientUtil = new AliOssClientUtil();
					if (!photoArr[i].contains(DsConfig.getImgServer())) {
						file.renameTo(file2);
						aliOssClientUtil.uploadObject2OSS(file2, path);
						commonImg.setImgPath("\\"+path+photoNewName);
					}else {
						commonImg.setImgPath("\\"+path+photoName);
					}
					commonImg.setImgSort(i);
					commonImg.setImgServer(DsConfig.getImgServer());
					commonImgService.save(commonImg);

				}
			}
		}

	}
	
	@Transactional(readOnly = false)
	public void delete(BizSkuInfo bizSkuInfo) {
		super.delete(bizSkuInfo);
	}

	
}