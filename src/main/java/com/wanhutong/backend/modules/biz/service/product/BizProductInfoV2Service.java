/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoV2Service;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
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
 * 产品信息表Service
 *
 * @author zx
 * @version 2017-12-13
 */
@Service
@Transactional(readOnly = true)
public class BizProductInfoV2Service extends CrudService<BizProductInfoDao, BizProductInfo> {

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
    @Resource
    private DictService dictService;
    @Resource
    private OfficeService officeService;
    @Resource
    private BizCategoryInfoV2Service bizCategoryInfoV2Service;
    @Resource
    private AttributeValueV2Service attributeValueV2Service;
    @Resource
    private BizSkuInfoV2Service bizSkuInfoV2Service;
    @Resource
    private BizOpShelfSkuService bizOpShelfSkuService;


    /**
     * 材质ID 临时解决文案 需优化
     */
    private static final Integer MATERIAL_ATTR_ID = 1;
    private static final Integer SIZE_ATTR_ID = 2;
    private static final Integer COLOR_ATTR_ID = 3;
    private static final String PRODUCT_TABLE = "biz_product_info";
    private static final String SKU_TABLE = "biz_sku_info";

    protected Logger log = LoggerFactory.getLogger(getClass());//日志

    @Override
    public BizProductInfo get(Integer id) {
        return super.get(id);
    }

    @Override
    public List<BizProductInfo> findList(BizProductInfo bizProductInfo) {
        return super.findList(bizProductInfo);
    }

    @Override
    public Page<BizProductInfo> findPage(Page<BizProductInfo> page, BizProductInfo bizProductInfo) {
        return super.findPage(page, bizProductInfo);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void save(BizProductInfo bizProductInfo) {
        // 取BRAND NAME
        Dict brand = dictService.get(Integer.valueOf(bizProductInfo.getBrandId()));
        bizProductInfo.setBrandName(brand == null ? StringUtils.EMPTY : brand.getValue());

        String brandPinYin = HanyuPinyinHelper.getFirstLetters(bizProductInfo.getBrandName() , HanyuPinyinCaseType.UPPERCASE);
        String brandCode = addZeroForNum(brandPinYin.substring(0, Math.min(brandPinYin.length(), 4)), false, 2);

        Office office = officeService.get(bizProductInfo.getOffice().getId());
        bizProductInfo.getOffice().setName(office.getName());
        BizVendInfo bizVendInfo = bizVendInfoService.get(office.getId());
        String vCode = bizVendInfo != null ? bizVendInfo.getCode() : "0";
        vCode = addZeroForNum(vCode, true, 3);

        BizVarietyInfo bizVarietyInfo = bizProductInfo.getBizVarietyInfo();
        BizVarietyInfo varietyInfo = bizVarietyInfoService.get(bizVarietyInfo.getId());
        String cateCode = addZeroForNum(varietyInfo.getCode(), true, 3);
        bizProductInfo.setProdCode(StringUtils.EMPTY);
        super.save(bizProductInfo);
        if (StringUtils.isBlank(bizProductInfo.getProdCode())) {
            String prodCode = brandCode + vCode + cateCode + autoGenericCode(bizProductInfo.getId().toString(), 6);
            bizProductInfo.setProdCode(prodCode);
            super.save(bizProductInfo);
        }

        //保存产品图片
        saveCommonImg(bizProductInfo);

        // 标签
        if(StringUtils.isNotBlank(bizProductInfo.getTagStr())) {
            List<BizCategoryInfo> byIds = bizCategoryInfoV2Service.findByIds(bizProductInfo.getTagStr());
            bizProductInfo.setCategoryInfoList(byIds);
        }
        if (bizProductInfo.getCategoryInfoList() != null && bizProductInfo.getCategoryInfoList().size() > 0) {
            bizProductInfoDao.deleteProdCate(bizProductInfo);
            bizProductInfoDao.insertProdCate(bizProductInfo);
        }

        // 材质
        if (StringUtils.isNotBlank(bizProductInfo.getTextureStr())) {
            AttributeValueV2 attributeValue = new AttributeValueV2();
            attributeValue.setValue(bizProductInfo.getTextureStr());
            attributeValue.setObjectName(PRODUCT_TABLE);
            attributeValue.setObjectId(bizProductInfo.getId());
            AttributeInfoV2 attributeInfo = new AttributeInfoV2();
            attributeInfo.setId(MATERIAL_ATTR_ID);
            attributeValue.setAttributeInfo(attributeInfo);
            attributeValueV2Service.save(attributeValue);
        }

        // 属性 SKU
        List<String> skuAttrStrList = bizProductInfo.getSkuAttrStrList();
        if (CollectionUtils.isNotEmpty(skuAttrStrList)) {
            // 查询已经上架的SKU
            BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
            bizOpShelfSku.setProductInfo(new BizProductInfo(bizProductInfo.getId()));
            List<BizOpShelfSku> ossList = bizOpShelfSkuService.findList(bizOpShelfSku);
            Map<String, BizOpShelfSku> oldOssMap = Maps.newHashMap();
            for (BizOpShelfSku b : ossList) {
                BizSkuInfo skuInfo = b.getSkuInfo();
                if (skuInfo != null && StringUtils.isNotBlank(skuInfo.getItemNo())) {
                    oldOssMap.put(skuInfo.getItemNo(), b);
                }
            }

//            删除 旧 SKU
            BizSkuInfo oldBizSkuInfo = new BizSkuInfo();
            oldBizSkuInfo.setProductInfo(bizProductInfo);
            bizSkuInfoV2Service.physicalDeleteByProd(oldBizSkuInfo);

            Set<String> skuAttrStrSet = Sets.newHashSet(skuAttrStrList);
            int index = 0;
            for(String s : skuAttrStrSet) {
                String[] split = s.split("\\|");
                String size = split[0];
                String color = split[1];
                String price = split[2];
                String type = split[3];
                String img = split.length >= 5 ? split[4] : StringUtils.EMPTY;

                BizSkuInfo bizSkuInfo = new BizSkuInfo();
                bizSkuInfo.setProductInfo(bizProductInfo);
                bizSkuInfo.setBuyPrice(StringUtils.isBlank(price) ? 0 : Double.valueOf(price));
                bizSkuInfo.setSkuType(Integer.valueOf(type));
                bizSkuInfo.setName(bizProductInfo.getName());
                bizSkuInfo.setSort(String.valueOf(index));
                bizSkuInfo.setItemNo(bizProductInfo.getItemNo().concat("/").concat(size).concat("/").concat(color));
                bizSkuInfoV2Service.save(bizSkuInfo);

                if (oldOssMap.get(bizSkuInfo.getItemNo()) != null) {
                    bizOpShelfSkuService.updateSkuIdById(oldOssMap.get(bizSkuInfo.getItemNo()).getId(), bizSkuInfo.getId());
                }

                AttributeValueV2 sizeAttrVal = new AttributeValueV2();
                sizeAttrVal.setValue(size);
                sizeAttrVal.setObjectName(SKU_TABLE);
                sizeAttrVal.setObjectId(bizSkuInfo.getId());
                AttributeInfoV2 sizeAttributeInfo = new AttributeInfoV2();
                sizeAttributeInfo.setId(SIZE_ATTR_ID);
                sizeAttrVal.setAttributeInfo(sizeAttributeInfo);
                attributeValueV2Service.save(sizeAttrVal);

                AttributeValueV2 colorAttrVal = new AttributeValueV2();
                colorAttrVal.setValue(color);
                colorAttrVal.setObjectName(SKU_TABLE);
                colorAttrVal.setObjectId(bizSkuInfo.getId());
                AttributeInfoV2 colorAttributeInfo = new AttributeInfoV2();
                colorAttributeInfo.setId(COLOR_ATTR_ID);
                colorAttrVal.setAttributeInfo(colorAttributeInfo);
                attributeValueV2Service.save(colorAttrVal);

                if(split.length >= 5) {
                    CommonImg commonImg = new CommonImg();
                    commonImg.setImgType(ImgEnum.SKU_TYPE.getCode());
                    commonImg.setImg(img);
                    commonImg.setObjectId(bizSkuInfo.getId());
                    commonImg.setObjectName(ImgEnum.SKU_TYPE.getTableName());
                    commonImg.setImgSort(index);
                    commonImg.setImgServer(DsConfig.getImgServer());
                    commonImg.setImgPath(img.replaceAll(DsConfig.getImgServer(), ""));
                    commonImgService.save(commonImg);
                }
                index ++;
            }
        }


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
            log.error("详情图转换编码异常." + e.getMessage(), e);
        }
        String photoLists = null;
        try {
//            photoLists = URLDecoder.decode(bizProductInfo.getPhotoLists(), "utf-8");//详情图转换编码
            photoLists = URLDecoder.decode(bizProductInfo.getPhotoDetails(), "utf-8").split("\\|")[0];
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("列表图转换编码异常." + e.getMessage(), e);
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

    /**
     * 商品特有属性与值
     */
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

    /**
     * 保存商品分类属性与属性值
     */
    private void saveCatePropAndValue(BizProductInfo bizProductInfo) {
        BizProdPropValue prodPropValue = new BizProdPropValue();
        bizProductInfoDao.deleteProdPropInfoReal(bizProductInfo);

        /**
         * 选择分类属性（属性和值）
         */
        if (bizProductInfo.getProdPropertyInfos() != null) {
            String[] propInfoValue=bizProductInfo.getProdPropertyInfos().split(",");
            BizProdPropertyInfo bizProdPropertyInfo =new BizProdPropertyInfo();
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
                bizProdPropertyInfo.setId(null);
                PropertyInfo propertyInfo = propertyInfoService.get(propId);
                bizProdPropertyInfo.setPropName(propertyInfo.getName());
                bizProdPropertyInfo.setPropDescription(propertyInfo.getDescription());
                bizProdPropertyInfo.setProductInfo(bizProductInfo);

                bizProdPropertyInfoService.save(bizProdPropertyInfo);

                List<String> prodPropertyValueList=entry.getValue();

                for(int i=0;i<prodPropertyValueList.size();i++){
                    Integer propValueId = Integer.parseInt(prodPropertyValueList.get(i).trim());
                    PropValue propValue = propValueService.get(propValueId);
                    prodPropValue.setId(null);
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

    @Transactional(readOnly = false)
    public void delete(BizProductInfo bizProductInfo) {
        super.delete(bizProductInfo);
    }


}