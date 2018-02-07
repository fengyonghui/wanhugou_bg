/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import com.aliyun.oss.model.ObjectMetadata;
import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sf.ehcache.util.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.annotation.Resource;


/**
 * 产品信息表Service
 *
 * @author zx
 * @version 2017-12-13
 */
@Service
@Transactional(readOnly = true)
public class BizProductInfoService extends CrudService<BizProductInfoDao, BizProductInfo> {
    @Resource
    private BizCatePropValueService bizCatePropValueService;
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
    @Resource
    private CommonImgService commonImgService;
    @Resource
    private BizSkuInfoService bizSkuInfoService;

    protected Logger log = LoggerFactory.getLogger(getClass());//日志


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
        if (bizProductInfo.getCatePropValue() != null && bizProductInfo.getCatePropValue().getId() != null) {
            BizCatePropValue bizCatePropValue = bizCatePropValueService.get(bizProductInfo.getCatePropValue().getId());
            if (bizCatePropValue != null) {
                bizProductInfo.setBrandName(bizCatePropValue.getValue());
            }
        }
        super.save(bizProductInfo);
        if (bizProductInfo.getCategoryInfoList() != null && bizProductInfo.getCategoryInfoList().size() > 0) {
            bizProductInfoDao.deleteProdCate(bizProductInfo);
            bizProductInfoDao.insertProdCate(bizProductInfo);
        }
        /**
         * 保存商品分类属性与属性值
         */
        saveCatePropAndValue(bizProductInfo);
        /**
         * 商品特有属性与值
         */
        saveOwnProp(bizProductInfo);
        //保存产品图片
        saveCommonImg(bizProductInfo);
    }

    @Transactional(readOnly = false)
    public void saveProd(BizProductInfo bizProductInfo) {
        super.save(bizProductInfo);
    }

    private List<CommonImg> getImgList(Integer imgType, Integer prodId) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(prodId);
        commonImg.setObjectName("biz_product_info");
        commonImg.setImgType(imgType);
        return commonImgService.findList(commonImg);
    }

    @Transactional(readOnly = false)
    public void saveCommonImg(BizProductInfo bizProductInfo) {
        String photos = null;
        try {
            photos = URLDecoder.decode(bizProductInfo.getPhotos(), "utf-8");//主图转换编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("主图转换编码异常." + e.getMessage(), e);
        }
        String photoDetails = null;
        try {
            photoDetails = URLDecoder.decode(bizProductInfo.getPhotoDetails(), "utf-8");//列表图转换编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("列表图转换编码异常." + e.getMessage(), e);
        }
        String photoLists = null;
        try {
            photoLists = URLDecoder.decode(bizProductInfo.getPhotoLists(), "utf-8");//详情图转换编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("详情图转换编码异常." + e.getMessage(), e);
        }

        if (photos != null) {
            String[] photoArr = photos.split("\\|");
            saveProdImg(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizProductInfo, photoArr);
        }

        //设置主图和图片次序
        List<CommonImg> commonImgs = getImgList(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizProductInfo.getId());
        for (int i = 0; i < commonImgs.size(); i++) {
            CommonImg commonImg = commonImgs.get(i);
            commonImg.setImgSort(i);
            commonImgService.save(commonImg);

            if (i == 0) {
                bizProductInfo.setImgUrl(commonImg.getImgServer() + commonImg.getImgPath());
                bizProductInfoDao.update(bizProductInfo);
            }
        }

        if (photoLists != null) {
            String[] photoArr = photoLists.split("\\|");
            saveProdImg(ImgEnum.LIST_PRODUCT_TYPE.getCode(), bizProductInfo, photoArr);
        }
        if (photoDetails != null) {
            String[] photoArr = photoDetails.split("\\|");
            saveProdImg(ImgEnum.SUB_PRODUCT_TYPE.getCode(), bizProductInfo, photoArr);
        }

    }

    public void saveProdImg(Integer imgType, BizProductInfo bizProductInfo, String[] photoArr) {
        if (bizProductInfo.getId() == null) {
            log.error("Can't save product image without product ID!");
            return;
        }

        List<CommonImg> commonImgs = getImgList(imgType, bizProductInfo.getId());

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
        commonImg.setObjectId(bizProductInfo.getId());
        commonImg.setObjectName("biz_product_info");
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

    private void saveOwnProp(BizProductInfo bizProductInfo) {
        if (bizProductInfo.getPropOwnValues() != null && !"".equals(bizProductInfo.getPropOwnValues())) {
            String[] valuesArr = bizProductInfo.getPropOwnValues().split("_");
            Map<String, List<String>> map = new HashMap();
            List<String> strList = null;
            for (int i = 0; i < valuesArr.length; i++) {
                if (valuesArr[i] == null || "".equals(valuesArr[i])) {
                    continue;
                }
                if (valuesArr[i].startsWith(",")) {
                    valuesArr[i] = valuesArr[i].substring(1);
                }
                String[] valuesFlag = valuesArr[i].split(",");
                if (valuesFlag.length <= 1) {
                    continue;
                }
                String key = (valuesFlag[1]);
                if (map.containsKey(key)) {
                    List<String> values = map.get(key);
                    map.remove(key);
                    values.add(valuesFlag[0]);
                    map.put(key, values);
                } else {
                    strList = new ArrayList<String>();
                    strList.add(valuesFlag[0]);
                    map.put(key, strList);
                }
            }
            if (bizProductInfo.getPropNames() != null && !"".equals(bizProductInfo.getPropNames())) {
                BizProdPropertyInfo bizProdPropertyInfo = new BizProdPropertyInfo();
                String[] propNameArr = bizProductInfo.getPropNames().split("_");
                for (int i = 0; i < propNameArr.length; i++) {
                    if (propNameArr[i] == null || "".equals(propNameArr[i])) {
                        continue;
                    }
                    if (propNameArr[i].startsWith(",")) {

                        propNameArr[i] = propNameArr[i].substring(1);
                    }
                    String[] nameFlag = propNameArr[i].split(",");
                    if (nameFlag.length <= 1) {
                        continue;
                    }
                    String flag = nameFlag[1];
                    bizProdPropertyInfo.setId(null);
                    bizProdPropertyInfo.setProductInfo(bizProductInfo);
                    bizProdPropertyInfo.setPropName(nameFlag[0]);
                    bizProdPropertyInfoService.save(bizProdPropertyInfo);
                    BizProdPropValue bizProdPropValue = new BizProdPropValue();
                    List<String> stringList = map.get(flag);
                    for (String str : stringList) {
                        bizProdPropValue.setProdPropertyInfo(bizProdPropertyInfo);
                        bizProdPropValue.setSource("prod");
                        bizProdPropValue.setPropValue(str);
                        bizProdPropValue.setPropName(bizProdPropertyInfo.getPropName());

                        bizProdPropValueService.save(bizProdPropValue);
                    }
                }
            }

        }
    }

    private void saveCatePropAndValue(BizProductInfo bizProductInfo) {
        BizProdPropValue prodPropValue = new BizProdPropValue();
        //BizProdPropertyInfo prodPropertyInfo = new BizProdPropertyInfo();
        /**
         * 产品选择分类属性（只选择属性，没有值）
         */
//		String catePropertyInfoStr = bizProductInfo.getProdPropertyInfos();
        bizProductInfoDao.deleteProdPropInfoReal(bizProductInfo);
//		if (catePropertyInfoStr != null && !catePropertyInfoStr.isEmpty()) {
//			String[] catePropertyInfos = catePropertyInfoStr.split(",");
//			for (int i = 0; i < catePropertyInfos.length; i++) {
//				Set<String> keySet = bizProductInfo.getPropertyMap().keySet();
//				if (!keySet.contains(catePropertyInfos[i])) {
//					Integer propId = Integer.parseInt(catePropertyInfos[i]);
//					PropertyInfo propertyInfo = propertyInfoService.get(propId);
//					prodPropertyInfo.setPropName(propertyInfo.getName());
//					prodPropertyInfo.setPropDescription(propertyInfo.getDescription());
//					prodPropValue.setPropertyInfo(propertyInfo);
//
//					prodPropertyInfo.setProductInfo(bizProductInfo);
//					bizProdPropertyInfoService.save(prodPropertyInfo);
//					prodPropValue.setId(null);
//					prodPropValue.setProdPropertyInfo(prodPropertyInfo);
//					prodPropValue.setSource("sys");
//					prodPropValue.setPropName(prodPropertyInfo.getPropName());
//					bizProdPropValueService.save(prodPropValue);
//
//				}
//			}
//
//		}
        /**
         * 选择分类属性（属性和值）
         */
        if (bizProductInfo.getPropertyMap() != null) {
            for (Map.Entry<String, BizProdPropertyInfo> entry : bizProductInfo.getPropertyMap().entrySet()) {
                Integer propId = Integer.parseInt(entry.getKey());
                BizProdPropertyInfo bizProdPropertyInfo = entry.getValue();
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

            }
        }
    }

    @Transactional(readOnly = false)
    public void delete(BizProductInfo bizProductInfo) {
        super.delete(bizProductInfo);
    }


}