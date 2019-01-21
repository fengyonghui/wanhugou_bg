/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import com.alibaba.druid.sql.visitor.functions.If;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoV3Dao;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuInfoV2Dao;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuInfoV3Dao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV2Service;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
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
	private CommonImgService commonImgService;
	@Autowired
	private BizSkuInfoV3Dao bizSkuInfoDao;
	@Autowired
	private BizProductInfoV3Dao bizProductInfoDao;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private AttributeValueV2Service attributeValueService;
	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@Autowired
	private DefaultPropService defaultPropService;
	@Autowired
	private BizOpShelfInfoService bizOpShelfInfoService;

	protected Logger log = LoggerFactory.getLogger(BizSkuInfoV2Service.class);

	@Override
	public BizSkuInfo get(Integer id) {
		return super.get(id);
	}

	@Override
	public List<BizSkuInfo> findList(BizSkuInfo bizSkuInfo) {
		if(bizSkuInfo != null) {
			return super.findList(bizSkuInfo);
		}
		return null;
	}

	public int findCount(BizSkuInfo bizSkuInfo) {
		if(bizSkuInfo != null) {
			return dao.findCount(bizSkuInfo);
		}
		return 0;
	}

	public List<BizSkuInfo> findListByParam(BizSkuInfo bizSkuInfo){
		return bizSkuInfoDao.findListByParam(bizSkuInfo);
	}

	public Map<String, List<BizSkuInfo>> findListForProd(BizSkuInfo bizSkuInfo) {
		List<BizSkuInfo> skuInfoList=super.findList(bizSkuInfo);
		Map<String,List<BizSkuInfo>> listMap= findSkuListForProd(skuInfoList);
		return listMap;
	}

	public Map<String, List<BizSkuInfo>> findSkuListForProd(List<BizSkuInfo> skuInfoList) {
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
						+productInfo.getProdCode()+","+null+","+productInfo.getBrandName()+","+productInfo.getBizVarietyInfo().getId()+","+productInfo.getBizVarietyInfo().getName();

			}else {
				 sKey = productInfo.getId()+","+productInfo.getName()+","+productInfo.getImgUrl()+","+productInfo.getCateNames()+","
						+productInfo.getProdCode()+","+productInfo.getOffice().getName()+","+productInfo.getBrandName()+","+productInfo.getBizVarietyInfo().getId()+","+productInfo.getBizVarietyInfo().getName();

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
//		AttributeValueV2 attributeValue =new AttributeValueV2();
//		attributeValue.setObjectId(skuInfo.getId());
//		attributeValue.setObjectName("biz_sku_info");
//		List<AttributeValueV2>skuPropValueList= attributeValueService.findList(attributeValue);
//        StringBuffer skuPropName=new StringBuffer();
//        for(AttributeValueV2 skuPropValue:skuPropValueList){
//            skuPropName.append("-");
//            skuPropName.append(skuPropValue.getValue());
//        }
//        String propNames="";
//        if(skuPropName.toString().length()>1){
//            propNames =skuPropName.toString().substring(1);
//        }
//
//        skuInfo.setSkuPropertyInfos(propNames);
		if(bizProductInfo!=null && bizProductInfo.getOffice()!=null){
			Office	office=officeService.get(bizProductInfo.getOffice().getId());
			bizProductInfo.setOffice(office);
		}

		skuInfo.setProductInfo(bizProductInfo);

		//商品已上货架名称
		List<BizOpShelfInfo> shlfNamesList = bizOpShelfInfoService.getShelfNames(skuInfo.getId());
		String shelfNames = "";
		String shelfMinSalePrice = "";
		if (CollectionUtils.isNotEmpty(shlfNamesList)) {
			for (BizOpShelfInfo opShelfInfo : shlfNamesList) {
				shelfNames += opShelfInfo.getName() + ",";
				//已上货架，最低销售价
				shelfMinSalePrice = opShelfInfo.getShelfMinSalePrice();
			}
		}
		if (shelfNames.contains(",")) {
			skuInfo.setShelfNames(shelfNames.substring(0, shelfNames.lastIndexOf(",")));
		}
		skuInfo.setShelfMinSalePrice(shelfMinSalePrice);
		return skuInfo;

	}

 public  List<BizSkuInfo> findAllList(){
		return bizSkuInfoDao.findAllList(new BizSkuInfo());
 }
	@Override
 public Page<BizSkuInfo> findPage(Page<BizSkuInfo> page, BizSkuInfo bizSkuInfo) {
	 	User user= UserUtils.getUser();
		 if(user.isAdmin()){
			bizSkuInfo.setDataStatus("filter");
		 }
		return super.findPage(page, bizSkuInfo);
	}
	
	@Transactional(readOnly = false)
	public void baseSave(BizSkuInfo bizSkuInfo) {
		super.save(bizSkuInfo);
	}
	@Transactional(readOnly = false)
	@Override
	public void save(BizSkuInfo bizSkuInfo) {
		save(bizSkuInfo, Boolean.FALSE);
	}


	public boolean hasEntityByPartNo(String partNo) {
		BizSkuInfo bizSkuInfo = bizSkuInfoDao.getEntityByPartNo(partNo);
		return bizSkuInfo != null;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(BizSkuInfo bizSkuInfo, boolean copy) {

		BizProductInfo bizProductInfo = bizProductInfoDao.get(bizSkuInfo.getProductInfo().getId());
		String prodCode = bizProductInfo.getProdCode();
		int sort = Integer.valueOf(StringUtils.isBlank(bizSkuInfo.getSort()) ? "1" : bizSkuInfo.getSort());
		String partNo = prodCode + sort;

		while (this.hasEntityByPartNo(partNo)) {
			sort ++;
			partNo = prodCode + sort;
		}

		if (StringUtils.isBlank(bizSkuInfo.getPartNo())) {
			bizSkuInfo.setPartNo(partNo);
		}

		super.save(bizSkuInfo);
		//sku保存属性
		saveSkuAttr(bizSkuInfo,copy);
		//sku图片保存
		saveCommonImg(bizSkuInfo, copy);
	}

	@Transactional(readOnly = false)
	public void saveSkuInfo(BizSkuInfo bizSkuInfo) {

		super.save(bizSkuInfo);

	}

	@Transactional(readOnly = false)
	public void saveCommonImg(BizSkuInfo bizSkuInfo, boolean copy) {
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
			saveProdImg(ImgEnum.SKU_TYPE.getCode(), bizSkuInfo, photoArr, copy);
		}

	}


	public void saveProdImg(Integer imgType, BizSkuInfo bizSkuInfo, String[] photoArr, boolean copy) {
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
			if (StringUtils.isNotBlank(name) && copy) {
				commonImg.setId(null);
				commonImg.setImgServer(DsConfig.getImgServer());
				commonImg.setImgPath(name.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY));
				commonImgService.save(commonImg);
				continue;
			}

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

	@Transactional(readOnly = false)
	public void saveSkuAttr(BizSkuInfo bizSkuInfo, boolean copy) {
		if (bizSkuInfo.getId() == null) {
			log.error("Can't save sku image without sku ID!");
			return;
		}
		if (bizSkuInfo.getItemNo() == null) {
			log.error("Can't save sku image without sku itemNo!");
			return;
		}
		if (copy) {
			AttributeValueV2 attributeValueV2 = new AttributeValueV2();
			attributeValueV2.setAttributeInfo(new AttributeInfoV2(Integer.valueOf(defaultPropService.getPropByKey("size"))));
			attributeValueV2.setObjectId(bizSkuInfo.getId());
			attributeValueV2.setObjectName(BizProductInfoV2Service.SKU_TABLE);
			String itemNo = bizSkuInfo.getItemNo();
			String substring = itemNo.substring(itemNo.indexOf("/") + 1, itemNo.length());
			String size = substring.substring(0, substring.indexOf("/"));
			String color = substring.substring(substring.indexOf("/") + 1, substring.length());
			attributeValueV2.setValue(size);
			attributeValueService.save(attributeValueV2);
			attributeValueV2.setAttributeInfo(new AttributeInfoV2(Integer.valueOf(defaultPropService.getPropByKey("color"))));
			attributeValueV2.setValue(color);
			attributeValueService.save(attributeValueV2);
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
		bizSkuInfo.setDelFlag(BizSkuInfo.DEL_FLAG_DELETE);
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

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void recovery(BizSkuInfo bizSkuInfo) {
		bizSkuInfoDao.recovery(bizSkuInfo);
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

	public List<BizSkuInfo> findPurseSkuList(BizSkuInfo bizSkuInfo) {
		return bizSkuInfoDao.findPurseSkuList(bizSkuInfo);
	}

	/**
	 * 获取商品和订单的对应关系列表
	 * @param page
	 * @param bizSkuInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public Page<BizSkuInfo> findPageForSkuInfo(Page<BizSkuInfo> page, BizSkuInfo bizSkuInfo) {
		User user = UserUtils.getUser();

		bizSkuInfo.setPage(page);

		List<Role> roleList = user.getRoleList();
		Role role = new Role();
		role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
		if (roleList.contains(role)) {
			bizSkuInfo.setVendorId(user.getCompany().getId().toString());
		}else {
			bizSkuInfo.getSqlMap().put("chat", BaseService.dataScopeFilter(user, "s", "su"));
		}

		page.setList(bizSkuInfoDao.findPageForSkuInfo(bizSkuInfo));
		return page;
	}

	/**
	 * 查询商品属性
	 * @param objId
	 * @param objName
	 * @param attrInfoName
	 * @return
	 */
	public List<AttributeValueV2> getSkuProperty(Integer objId, String objName, String attrInfoName) {
		AttributeValueV2 attributeValueV2 = new AttributeValueV2();
		attributeValueV2.setObjectId(objId);
		attributeValueV2.setObjectName(objName);
		AttributeInfoV2 attributeInfoV2 = new AttributeInfoV2();
		attributeInfoV2.setName(attrInfoName);
		attributeValueV2.setAttributeInfo(attributeInfoV2);
		return attributeValueService.findList(attributeValueV2);
	}

	public List<CommonImg> getImg(Integer objId, String objName, Integer imgType) {
		CommonImg commonImg = new CommonImg();
		commonImg.setObjectId(objId);
		commonImg.setObjectName(objName);
		commonImg.setImgType(imgType);
		return commonImgService.findList(commonImg);
	}
}