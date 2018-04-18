/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuInfoV2Dao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropValueService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropertyInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * 商品skuService
 * @author zx
 * @version 2017-12-07
 */
@Service
@Transactional(readOnly = true)
public class BizSkuInfoV2Service extends CrudService<BizSkuInfoV2Dao, BizSkuInfo> {
	@Resource
	private BizProdPropertyInfoService bizProdPropertyInfoService;
	@Resource
	private BizProdPropValueService bizProdPropValueService;
	@Resource
	private BizSkuPropValueService bizSkuPropValueService;
	@Resource
	private CommonImgService commonImgService;
	@Autowired
	private BizSkuInfoV2Dao bizSkuInfoDao;
	@Autowired
	private BizProductInfoDao bizProductInfoDao;
	@Resource
	private PropValueService propValueService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private PropertyInfoService propertyInfoService;
	@Autowired
	private AttributeValueV2Service attributeValueService;
	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;

	protected Logger log = LoggerFactory.getLogger(BizSkuInfoV2Service.class);//日志

	public BizSkuInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuInfo> findList(BizSkuInfo bizSkuInfo) {
		if(bizSkuInfo != null) {
			return super.findList(bizSkuInfo);
		}
		return null;
	}

	public List<BizSkuInfo> findListByParam(BizSkuInfo bizSkuInfo){
		return bizSkuInfoDao.findListByParam(bizSkuInfo);
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
		if(skuInfo.getProductInfo()==null){
			return null;
		}
		Integer prodId=	skuInfo.getProductInfo().getId();
		BizProductInfo bizProductInfo=bizProductInfoDao.get(prodId);
		AttributeValueV2 attributeValue =new AttributeValueV2();
		attributeValue.setObjectId(skuInfo.getId());
		attributeValue.setObjectName("biz_sku_info");
		List<AttributeValueV2>skuPropValueList= attributeValueService.findList(attributeValue);
        StringBuffer skuPropName=new StringBuffer();
        for(AttributeValueV2 skuPropValue:skuPropValueList){
            skuPropName.append("-");
            skuPropName.append(skuPropValue.getValue());
        }
        String propNames="";
        if(skuPropName.toString().length()>1){
            propNames =skuPropName.toString().substring(1);
        }

        skuInfo.setSkuPropertyInfos(propNames);
		if(bizProductInfo!=null && bizProductInfo.getOffice()!=null){
			Office	office=officeService.get(bizProductInfo.getOffice().getId());
			bizProductInfo.setOffice(office);
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

		BizProductInfo bizProductInfo = bizProductInfoDao.get(bizSkuInfo.getProductInfo().getId());
		String prodCode = bizProductInfo.getProdCode();
		String partNo = prodCode + bizSkuInfo.getSort();

		if (StringUtils.isBlank(bizSkuInfo.getPartNo())) {
			bizSkuInfo.setPartNo(partNo);
		}

		super.save(bizSkuInfo);
 		 BizSkuPropValue bizSkuPropValue = new BizSkuPropValue();
		if (bizSkuInfo.getProdPropMap() != null) {
			bizSkuInfoDao.deleteSkuPropInfoReal(bizSkuInfo);
			String sizeStr="";
			String colorStr="";
			for (Map.Entry<String, BizProdPropertyInfo> entry : bizSkuInfo.getProdPropMap().entrySet()) {
				Integer propId = Integer.parseInt(entry.getKey());
				BizProdPropertyInfo bizProdPropertyInfo = entry.getValue();

				String prodPropertyValueStr = bizProdPropertyInfo.getProdPropertyValues();
				if (prodPropertyValueStr != null && !"".equals(prodPropertyValueStr)) {
					String[] prodPropertyValues = prodPropertyValueStr.split("-");
					String source=prodPropertyValues[1].trim();
					Integer propValueId=Integer.parseInt(prodPropertyValues[0].trim());

					bizSkuPropValue.setId(null);
					if("sys".equals(source)){
						PropertyInfo propertyInfo = propertyInfoService.get(propId);
						bizSkuPropValue.setPropertyInfo(propertyInfo);
						bizSkuPropValue.setPropName(propertyInfo.getName());
						PropValue propValue= propValueService.get(propValueId);
						bizSkuPropValue.setPropValue(propValue.getValue());
						bizSkuPropValue.setPropValueObj(propValue);
						bizSkuPropValue.setSource("sys");
						bizSkuPropValue.setCode(propValue.getCode());

					}else if("prod".equals(source)){
						BizProdPropertyInfo prodPropertyInfo = bizProdPropertyInfoService.get(propId);
						BizProdPropValue bizProdPropValue = bizProdPropValueService.get(propValueId);
						bizSkuPropValue.setProdPropertyInfo(prodPropertyInfo);
						bizSkuPropValue.setPropName(prodPropertyInfo.getPropName());
						bizSkuPropValue.setPropValue(bizProdPropValue.getPropValue());
						bizSkuPropValue.setProdPropValue(bizProdPropValue);
						bizSkuPropValue.setSource(bizProdPropValue.getSource());
						bizSkuPropValue.setCode(bizProdPropValue.getCode());
					}
						if(StringUtils.isNotBlank(bizSkuPropValue.getPropName()) && bizSkuPropValue.getPropName().contains("尺寸")){
							sizeStr="/"+bizSkuPropValue.getPropValue();
						}
						if(StringUtils.isNotBlank(bizSkuPropValue.getPropName()) && bizSkuPropValue.getPropName().contains("颜色")){
							colorStr="/"+bizSkuPropValue.getPropValue();
						}
						bizSkuPropValue.setSkuInfo(bizSkuInfo);

						bizSkuPropValueService.save(bizSkuPropValue);


					}
				}

			bizSkuInfo.setItemNo(bizProductInfo.getItemNo()+(bizProductInfo.getVendorName()==null?0:bizProductInfo.getVendorName())+sizeStr+colorStr);
			super.save(bizSkuInfo);
			}
		//sku图片保存
		saveCommonImg(bizSkuInfo);
	}

	@Transactional(readOnly = false)
	public void saveCommonImg(BizSkuInfo bizSkuInfo) {
		if (StringUtils.isBlank(bizSkuInfo.getPhotos())) {
			return;
		}
		String photos=null;
		try {
			photos = URLDecoder.decode(bizSkuInfo.getPhotos(), "utf-8");//SKU商品图片转换编码
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.error("SKU商品图片转换编码异常." + e.getMessage(), e);
		}
		if (photos != null) {
			String[] photoArr = photos.split("\\|");
			saveProdImg(ImgEnum.SKU_TYPE.getCode(), bizSkuInfo, photoArr);
		}

	}


	public void saveProdImg(Integer imgType, BizSkuInfo bizSkuInfo, String[] photoArr) {
		if (bizSkuInfo.getId() == null) {
			log.error("Can't save sku image without sku ID!");
			return;
		}

		List<CommonImg> commonImgs = getImgList(imgType, bizSkuInfo.getId());

		Set<String> existSet = new HashSet<>();
		for (CommonImg commonImg1 : commonImgs) {
			existSet.add(commonImg1.getImgServer() + commonImg1.getImgPath());
		}
		Set<String> newSet = new HashSet<>(Arrays.asList(photoArr));

		Set<String> result = new HashSet<String>();
		//差集，结果做删除操作
		result.clear();
		result.addAll(existSet);
		result.removeAll(newSet);
		for (String url : result) {
			for (CommonImg commonImg1 : commonImgs) {
				if (url.equals(commonImg1.getImgServer() + commonImg1.getImgPath())) {
					commonImg1.setDelFlag("0");
					commonImgService.delete(commonImg1);
				}
			}
		}
		//差集，结果做插入操作
		result.clear();
		result.addAll(newSet);
		result.removeAll(existSet);

		CommonImg commonImg = new CommonImg();
		commonImg.setObjectId(bizSkuInfo.getId());
		commonImg.setObjectName("biz_sku_info");
		commonImg.setImgType(imgType);
		commonImg.setImgSort(20);
		for (String name : result) {
			if (name.trim().length() == 0 || name.contains(DsConfig.getImgServer())) {
				continue;
			}
			String pathFile = Global.getUserfilesBaseDir() + name;
			String ossPath = AliOssClientUtil.uploadFile(pathFile, true);

			commonImg.setId(null);
			commonImg.setImgPath("/"+ossPath);
			commonImg.setImgServer(DsConfig.getImgServer());
			commonImgService.save(commonImg);
		}
	}

	private List<CommonImg> getImgList(Integer imgType, Integer skuId) {
		CommonImg commonImg = new CommonImg();
		commonImg.setObjectId(skuId);
		commonImg.setObjectName("biz_sku_info");
		commonImg.setImgType(imgType);
		return commonImgService.findList(commonImg);
	}
	@Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
	public void delete(BizSkuInfo bizSkuInfo) {
		super.delete(bizSkuInfo);
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizSkuInfo.getId());
        commonImg.setObjectName("biz_sku_info");
        commonImgService.delete(commonImg);
	}

	public BizSkuInfo getSkuInfoByItemNoProdId(String itemNo, Integer prodId) {
		return bizSkuInfoDao.getSkuInfoByItemNoProdId(itemNo, prodId);
	}

	public List<BizSkuInfo> findListIgnoreStatus(BizSkuInfo oldSkuEntity) {
		return bizSkuInfoDao.findListIgnoreStatus(oldSkuEntity);
	}

	/**
	 * C端商品上下架form查询商品为 已经上架的商品
	 * */
	public Map<String, List<BizSkuInfo>> findListForCendProd(BizSkuInfo bizSkuInfo) {
		List<BizSkuInfo> skuInfoList=super.findList(bizSkuInfo);
		BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
		Map<BizProductInfo,List<BizSkuInfo>> map=new HashMap<BizProductInfo,List<BizSkuInfo>>();
		Map<String,List<BizSkuInfo>> listMap=new HashMap<String, List<BizSkuInfo>>();
		for(BizSkuInfo skuInfo:skuInfoList){
			bizOpShelfSku.setSkuInfo(skuInfo);
			List<BizOpShelfSku> list = bizOpShelfSkuService.findList(bizOpShelfSku);
			if(list.size()!=0){
				for (BizOpShelfSku opShelfSku : list) {
					if(opShelfSku.getSkuInfo().getId().equals(skuInfo.getId())){
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
				}
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
			listMap.put(sKey,map.get(productInfo));
		}
		return listMap;
	}
}