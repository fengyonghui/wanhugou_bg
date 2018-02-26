/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sf.ehcache.util.ProductInfo;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private BizVendInfoService bizVendInfoService;
    @Autowired
    private BizVarietyInfoService bizVarietyInfoService;


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
        String brandCode="";
        String prodCode="";
        String cateCode="";
        if (bizProductInfo.getPropValue() != null && bizProductInfo.getPropValue().getId() != null) {
            PropValue propValue = propValueService.get(bizProductInfo.getPropValue().getId());
            if (propValue != null) {
                bizProductInfo.setBrandName(propValue.getValue());
                brandCode=addZeroForNum(propValue.getCode(),false,2);
                Office office=bizProductInfo.getOffice();
                BizVendInfo bizVendInfo = bizVendInfoService.get(office.getId());
                prodCode=addZeroForNum(bizVendInfo.getCode(),true,3);
                BizVarietyInfo bizVarietyInfo = bizProductInfo.getBizVarietyInfo();
                BizVarietyInfo varietyInfo=bizVarietyInfoService.get(bizVarietyInfo.getId());
                cateCode=addZeroForNum(varietyInfo.getCode(),true,3);
            }

        }
        super.save(bizProductInfo);
        if(bizProductInfo.getProdCode()==null || "0".equals(bizProductInfo.getProdCode())){
            String partNo=brandCode+prodCode+cateCode+autoGenericCode(bizProductInfo.getId().toString(),6);
            bizProductInfo.setProdCode(partNo);
            super.save(bizProductInfo);
        }

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

    private String autoGenericCode(String code, int num) {
        String result = "";
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        result = String.format("%0" + num + "d", Integer.parseInt(code) + 1);

        return result;
    }

    public static String addZeroForNum(String str,boolean flag, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                if(flag){
                    sb.append("0").append(str);// 左补0
                }else {
                    sb.append(str).append("0");//右补0
                }
                str = sb.toString();
                strLen = str.length();
            }
        }else {
            str=str.substring(0,strLength);
        }

        return str;
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
                        bizProdPropValue.setProdPropertyInfoId(bizProdPropertyInfo.getId());
                        bizProdPropValue.setSource("prod");
                        bizProdPropValue.setCode(HanyuPinyinHelper.getFirstLetters(str, HanyuPinyinCaseType.UPPERCASE));
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
        bizProductInfoDao.deleteProdPropInfoReal(bizProductInfo);

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
                        prodPropValue.setProdPropertyInfoId(propertyInfo.getId());
                        prodPropValue.setSource("sys");
                        prodPropValue.setPropName(bizProdPropertyInfo.getPropName());
                        prodPropValue.setProdPropertyInfo(bizProdPropertyInfo);
                        prodPropValue.setPropValue(propValue.getValue());
                        prodPropValue.setCode(propValue.getCode());
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