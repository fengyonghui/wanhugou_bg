/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import com.alibaba.druid.sql.visitor.functions.If;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.BaseEntity;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.supcan.treelist.cols.Col;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoV3Dao;
import com.wanhutong.backend.modules.biz.dao.shelf.BizOpShelfSkuV2Dao;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuViewLog;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoV2Service;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.inventory.BizCollectGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.inventoryviewlog.BizInventoryViewLogService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuV2Service;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV3Service;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuViewLogService;
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
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 产品信息表Service
 *
 * @author zx
 * @version 2017-12-13
 */
@Service
@Transactional(readOnly = true)
public class BizProductInfoV3Service extends CrudService<BizProductInfoV3Dao, BizProductInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizProductInfoV3Service.class);

    @Resource
    private PropertyInfoService propertyInfoService;
    @Resource
    private BizProdPropertyInfoService bizProdPropertyInfoService;
    @Autowired
    private BizProductInfoV3Dao bizProductInfoDao;
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
    private BizSkuInfoV3Service bizSkuInfoV3Service;
    @Autowired
    private BizSkuViewLogService bizSkuViewLogService;
    @Autowired
    private BizOpShelfSkuV2Dao bizOpShelfSkuV2Dao;
    @Autowired
    private BizOpShelfSkuV2Service bizOpShelfSkuV2Service;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizPoDetailService bizPoDetailService;
    @Autowired
    private BizSendGoodsRecordService bizSendGoodsRecordService;
    @Autowired
    private BizCollectGoodsRecordService bizCollectGoodsRecordService;
    @Autowired
    private BizInventorySkuService bizInventorySkuService;
    @Autowired
    private BizInventoryViewLogService bizInventoryViewLogService;

    /**
     * 材质ID 临时解决文案 需优化
     */
    private static final Integer MATERIAL_ATTR_ID = 1;
    private static final Integer SIZE_ATTR_ID = 2;
    private static final Integer COLOR_ATTR_ID = 3;
    public static final String PRODUCT_TABLE = "biz_product_info";
    public static final String SKU_TABLE = "biz_sku_info";

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
        bizProductInfo.setDataStatus("filter");
        return super.findPage(page, bizProductInfo);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void save(BizProductInfo bizProductInfo) {
        save(bizProductInfo, false);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void save(BizProductInfo bizProductInfo, boolean copy) {
        // 取BRAND NAME
        Dict brand = StringUtils.isBlank(bizProductInfo.getBrandId()) ? null : dictService.get(Integer.valueOf(bizProductInfo.getBrandId()));
        bizProductInfo.setBrandName(brand == null ? StringUtils.EMPTY : brand.getLabel());

        String brandPinYin = HanyuPinyinHelper.getFirstLetters(bizProductInfo.getBrandName() , HanyuPinyinCaseType.UPPERCASE);
        String brandCode = addZeroForNum(brandPinYin.substring(0, Math.min(brandPinYin.length(), 4)), false, 2);

        Office office = officeService.get(bizProductInfo.getOffice().getId());
        bizProductInfo.getOffice().setName(office.getName());
        BizVendInfo bizVendInfo = bizVendInfoService.get(office.getId());
        String vFullCode = bizVendInfo != null ? bizVendInfo.getCode() : HanyuPinyinHelper.getFirstLetters(office.getName(), HanyuPinyinCaseType.UPPERCASE);

        String vFullName = HanyuPinyinHelper.getFirstLetters(office.getName(), HanyuPinyinCaseType.UPPERCASE);
        if (!bizProductInfo.getItemNo().startsWith(vFullName)) {
            bizProductInfo.setItemNo(vFullName.concat(bizProductInfo.getItemNo()));
        }

        String vCode = addZeroForNum(vFullCode, true, 3);

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
        saveCommonImg(bizProductInfo, copy);

        //保存产品特有属性
        saveProdAttr(bizProductInfo,varietyInfo);

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

            BizSkuInfo oldSkuEntity = new BizSkuInfo();
            oldSkuEntity.setProductInfo(bizProductInfo);
            List<BizSkuInfo> oldSkuList = bizSkuInfoV3Service.findListIgnoreStatus(oldSkuEntity);
            List<BizSkuInfo> newSkuList = Lists.newArrayList();

            Set<String> skuAttrStrSet = Sets.newHashSet(skuAttrStrList);
            int index = oldSkuList.size() + 1;
            for(String s : skuAttrStrSet) {
                String[] split = s.split("\\|");
                String size = split[0];
                String color = split[1];
                String price = split[2];
                String type = split[3];
                String id = split[4];
                String img = split.length >= 6 ? split[5] : StringUtils.EMPTY;
                BizSkuInfo bizSkuInfo = new BizSkuInfo();
                if (StringUtils.isNotBlank(id) && !"undefined".equals(id) && !"0".equals(id) && !copy) {
                    bizSkuInfo.setId(Integer.valueOf(id));
                }
                if(bizSkuInfo.getId()!=null && !bizSkuInfo.getId().equals("0") && !bizSkuInfo.getId().equals("undefined")){
                    //保存商品出厂价日志表
                    Double aftBuyPrice=StringUtils.isBlank(price)? 0 : Double.valueOf(price);
                    BizSkuViewLog skuViewLog = new BizSkuViewLog();
                    BizSkuInfo skuInfo = bizSkuInfoV3Service.get(bizSkuInfo);
                    if(skuInfo!=null){
                        if(!aftBuyPrice.equals(skuInfo.getBuyPrice())) {
                            skuViewLog.setSkuInfo(skuInfo);//商品名称
                            skuViewLog.setItemNo(skuInfo.getItemNo());//货号
                            skuViewLog.setUpdateDate(skuInfo.getUpdateDate());//商品修改时间
                            skuViewLog.setUpdateBy(skuInfo.getCreateBy());//商品修改人
                            skuViewLog.setSkuType(BizSkuViewLog.SkuType.PLATFORM.getType());
                            Double buyPrice = 0.0;
                            if (skuInfo.getBuyPrice() != null) {
                                buyPrice = skuInfo.getBuyPrice();
                            }
                            skuViewLog.setFrontBuyPrice(buyPrice);//修改前价格
                            skuViewLog.setAfterBuyPrice(aftBuyPrice);//修改后价格
                            skuViewLog.setChangePrice(aftBuyPrice - buyPrice);//改变价格
                            bizSkuViewLogService.save(skuViewLog);
                        }
                    }
                }

                bizSkuInfo.setProductInfo(bizProductInfo);
                bizSkuInfo.setBuyPrice(StringUtils.isBlank(price) ? 0 : Double.valueOf(price));
                bizSkuInfo.setBasePrice(bizSkuInfo.getBuyPrice());
                bizSkuInfo.setSkuType(Integer.valueOf(type));
                bizSkuInfo.setName(bizProductInfo.getName());
                bizSkuInfo.setSort(String.valueOf(index));
                bizSkuInfo.setItemNo(bizProductInfo.getItemNo().concat("/").concat(size).concat("/").concat(color));

                BizSkuInfo oldBizSkuInfo = bizSkuInfoV3Service.getSkuInfoByItemNoProdId(bizSkuInfo.getItemNo(), bizProductInfo.getId());
                if (oldBizSkuInfo != null && !copy) {
                    bizSkuInfo.setId(oldBizSkuInfo.getId());
                }else {
                    index ++;
                }
                bizSkuInfoV3Service.save(bizSkuInfo);
                newSkuList.add(bizSkuInfo);

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

                CommonImg commonImg = new CommonImg();
                commonImg.setImgType(ImgEnum.SKU_TYPE.getCode());
                commonImg.setObjectId(bizSkuInfo.getId());
                commonImg.setObjectName(ImgEnum.SKU_TYPE.getTableName());
                List<CommonImg> list = commonImgService.findList(commonImg);
                commonImg.setImg(img);
                commonImg.setImgSort(index);
                commonImg.setImgServer(StringUtils.isBlank(img) ? StringUtils.EMPTY : DsConfig.getImgServer());
                commonImg.setImgPath(img.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY));

                if (CollectionUtils.isNotEmpty(list)) {
                    commonImg.setId(list.get(0).getId());
                }
                commonImgService.save(commonImg);
            }
            for (BizSkuInfo oldS : oldSkuList) {
                boolean hasDel = true;
                if (StringUtils.isBlank(oldS.getItemNo())) {
                    continue;
                }
                String oldItemNo = oldS.getItemNo().substring(oldS.getItemNo().indexOf("/") + 1);
                for (BizSkuInfo newS : newSkuList) {
                    String newItemNo = newS.getItemNo().substring(newS.getItemNo().indexOf("/") + 1);
                    if (oldItemNo.equals(newItemNo)) {
                        hasDel = false;
                        break;
                    }
                }
                if (hasDel) {
                    bizSkuInfoV3Service.delete(oldS);
                }
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

    @Transactional(readOnly = false)
    public void saveProdAttr(BizProductInfo bizProductInfo,BizVarietyInfo bizVarietyInfo) {
        if (bizProductInfo.getDicts()==null || bizProductInfo.getDicts().isEmpty()) {
            return;
        }
        String dicts = bizProductInfo.getDicts();
        String[] dictArr = StringUtils.split(dicts, ",");
        AttributeValueV2 attributeValue = new AttributeValueV2();
        for (int i = 0; i < dictArr.length; i++) {
            String[] attrArr = StringUtils.split(dictArr[i], "-");
            attributeValue.setValue(attrArr[1]);
            attributeValue.setObjectName(PRODUCT_TABLE);
            attributeValue.setObjectId(bizProductInfo.getId());
            attributeValue.setAttributeInfo(new AttributeInfoV2(Integer.parseInt(attrArr[0])));
            attributeValueV2Service.save(attributeValue);
        }
    }

    private List<CommonImg> getImgList(Integer imgType, Integer prodId) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(prodId);
        commonImg.setObjectName(PRODUCT_TABLE);
        commonImg.setImgType(imgType);
        return commonImgService.findList(commonImg);
    }

    @Transactional(readOnly = false)
    public void saveCommonImg(BizProductInfo bizProductInfo, boolean copy) {
        String photos = null;
        try {
            photos = StringUtils.isNotBlank(bizProductInfo.getPhotos()) ? URLDecoder.decode(bizProductInfo.getPhotos(), "utf-8") : StringUtils.EMPTY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("主图转换编码异常." + e.getMessage(), e);
        }
        String photoDetails = null;
        try {
            photoDetails = StringUtils.isNotBlank(bizProductInfo.getPhotoDetails()) ? URLDecoder.decode(bizProductInfo.getPhotoDetails(), "utf-8") : StringUtils.EMPTY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("详情图转换编码异常." + e.getMessage(), e);
        }
        String photoLists = null;
        String imgPhotosSorts = bizProductInfo.getImgPhotosSorts();
        String[] photosSort = StringUtils.split(imgPhotosSorts, ",");
        String imgDetailSorts = bizProductInfo.getImgDetailSorts();
        String[] detailSort = StringUtils.split(imgDetailSorts, ",");
        if (StringUtils.isNotBlank(photos)) {
            List<String> strings = Arrays.asList(photos.split("\\|"));
            for (String s : strings) {
                if (StringUtils.isNotBlank(s)) {
                    photoLists = s;
                    break;
                }
            }
        }

        if (photoLists != null) {
            String[] photoArr = photoLists.split("\\|");
            saveProdImg(ImgEnum.LIST_PRODUCT_TYPE.getCode(), bizProductInfo, photoArr, copy);
        }

        if (photos != null) {
            String[] photoArr = photos.split("\\|");
            saveProdImg(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizProductInfo, photoArr, copy);
        }

        if (photoDetails != null) {
            String[] photoArr = photoDetails.split("\\|");
            saveProdImg(ImgEnum.SUB_PRODUCT_TYPE.getCode(), bizProductInfo, photoArr, copy);
        }

        //设置主图和图片次序
        List<CommonImg> commonImgs = getImgList(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizProductInfo.getId());
        List<CommonImg> detailCommonImg = getImgList(ImgEnum.SUB_PRODUCT_TYPE.getCode(), bizProductInfo.getId());
        for (int i = 0; i < commonImgs.size(); i++) {
            CommonImg commonImg = commonImgs.get(i);
            if (photosSort != null && photosSort.length > 0) {
                commonImg.setImgSort(Integer.parseInt(photosSort[i]));
            }
            commonImgService.save(commonImg);

            if (i == 0 && StringUtils.isBlank(bizProductInfo.getImgUrl())) {
                bizProductInfo.setImgUrl(commonImg.getImgServer() + commonImg.getImgPath());
                bizProductInfoDao.update(bizProductInfo);
            }
        }
        for (int i = 0; i < detailCommonImg.size(); i++) {
            CommonImg commonImg = detailCommonImg.get(i);
            if (detailSort != null && detailSort.length > 0) {
                commonImg.setImgSort(Integer.parseInt(detailSort[i]));
            }
            commonImgService.save(commonImg);
        }

    }

    public void saveProdImg(Integer imgType, BizProductInfo bizProductInfo, String[] photoArr, boolean copy) {
        if (bizProductInfo.getId() == null) {
            log.error("Can't save product image without product ID!");
            return;
        }

        List<CommonImg> commonImgs = getImgList(imgType, bizProductInfo.getId());

        Set<String> existSet = new LinkedHashSet<>();
        for (CommonImg commonImg1 : commonImgs) {
            existSet.add(commonImg1.getImgServer() + commonImg1.getImgPath());
        }
        Set<String> newSet = new LinkedHashSet<>(Arrays.asList(photoArr));

        Set<String> result = new LinkedHashSet<>();
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
        commonImg.setObjectName(PRODUCT_TABLE);
        commonImg.setImgType(imgType);
        commonImg.setImgSort(20);

        List<CommonImg> oldImgList = null;
        if (ImgEnum.LIST_PRODUCT_TYPE.getCode() == imgType) {
            CommonImg oldCommonImg = new CommonImg();
            oldCommonImg.setImgType(ImgEnum.LIST_PRODUCT_TYPE.getCode());
            oldCommonImg.setObjectId(bizProductInfo.getId());
            oldCommonImg.setObjectName(PRODUCT_TABLE);
            oldImgList = commonImgService.findList(oldCommonImg);
        }

        for (String name : result) {
            if (StringUtils.isNotBlank(name) || (CollectionUtils.isEmpty(oldImgList) && (ImgEnum.LIST_PRODUCT_TYPE.getCode() == imgType))) {
                commonImg.setId(null);
                commonImg.setImgPath(name.replaceAll(DsConfig.getImgServer(), StringUtils.EMPTY).replaceAll(DsConfig.getOldImgServer(), StringUtils.EMPTY));
                commonImg.setImgServer(name.contains(DsConfig.getOldImgServer()) ? DsConfig.getOldImgServer() : DsConfig.getImgServer());
                commonImgService.save(commonImg);
                continue;
            }

            if (name.trim().length() == 0 || name.contains(DsConfig.getImgServer()) || name.contains(DsConfig.getOldImgServer())) {
                continue;
            }
            String pathFile = Global.getUserfilesBaseDir() + name;
            String ossPath = AliOssClientUtil.uploadFile(pathFile, ImgEnum.LIST_PRODUCT_TYPE.getCode() != imgType);

            commonImg.setId(null);
            commonImg.setImgPath("/"+ ossPath);
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

    /**
     * 合并产品
     * @param itemNo
     * @param vendId
     * @param needId
     * @param replaceId
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void mergeSpu(String itemNo, Integer vendId, Integer needId, Integer replaceId) {
        BizProductInfo bizProductInfo = new BizProductInfo();
        bizProductInfo.setItemNo(itemNo);
        bizProductInfo.setOffice(new Office(vendId));
        bizProductInfo.setProdType((byte)1);
        List<BizProductInfo> productList = findList(bizProductInfo);
        //初步规范化商品货号
        updateItemNoSize(productList);
        //被替换的产品ID，替换
        BizSkuInfo skuInfo = new BizSkuInfo();
        skuInfo.setProductInfo(new BizProductInfo(replaceId));
        List<BizSkuInfo> skuInfoList = bizSkuInfoV3Service.findList(skuInfo);
        if (CollectionUtils.isNotEmpty(skuInfoList)) {
            for (BizSkuInfo bizSkuInfo : skuInfoList) {
                bizSkuInfoV3Service.updateProdId(bizSkuInfo.getId(),needId);    //修改prod_id
                updateProdIdForOpShelfSku(bizSkuInfo.getId(),needId);//修改上架商品对应的prod_id
            }
        }

        if (CollectionUtils.isNotEmpty(productList)) {
            for (BizProductInfo productInfo : productList) {
                if (!needId.equals(productInfo.getId()) && !replaceId.equals(productInfo.getId())) {
                    LOGGER.info("产品ID【{}】", productInfo.getId());
                    //对比needId对应的SPU下的SKU
                    contrastSku(productInfo.getId(),needId);
                    //删除无用的SPU
                    BizProductInfo prod = new BizProductInfo();
                    prod.setId(productInfo.getId());
                    prod.setDelFlag(BaseEntity.DEL_FLAG_DELETE);
                    delete(prod);
                }
            }
        }
        BizProductInfo productInfo = new BizProductInfo();
        productInfo.setId(replaceId);
        productInfo.setDelFlag(BaseEntity.DEL_FLAG_DELETE);
        delete(productInfo);
        //替换货号
        updateItemNo(itemNo,needId);
        //删除以图为准和套二套三的商品
        deleteSomeSku(needId);
    }

    /**
     * 初步规范化商品货号
     * @param productInfos
     */
    private void updateItemNoSize(List<BizProductInfo> productInfos) {
        if (CollectionUtils.isNotEmpty(productInfos)) {
            for (BizProductInfo productInfo : productInfos) {
                BizSkuInfo bizSkuInfo = new BizSkuInfo();
                bizSkuInfo.setProductInfo(productInfo);
                List<BizSkuInfo> skuInfos = bizSkuInfoV3Service.findList(bizSkuInfo);
                if (CollectionUtils.isNotEmpty(skuInfos)) {
                    for (BizSkuInfo skuInfo : skuInfos) {
                        String itemNo = skuInfo.getItemNo();
                        itemNo = itemNo.replaceAll("寸", "");
                        itemNo = itemNo.replaceAll("工厂", "装车");
                        itemNo = itemNo.replaceAll("打包价","打包");
                        itemNo = itemNo.replaceAll("装车价","装车");
                        itemNo = itemNo.replaceAll("色","");
                        String start = itemNo.substring(0, itemNo.indexOf("/") + 1);
                        String after = itemNo.substring(itemNo.indexOf("/") + 1, itemNo.length());
                        String middle = after.substring(0, after.indexOf("/"));
                        String end = after.substring(after.indexOf("/"),after.length());
                        StringBuilder sb = new StringBuilder();
                         if (start.contains("打包") && !middle.contains("打包")) {
                             itemNo = sb.append(start).append(middle).append("打包").append(end).toString();
                         }
                         if (start.contains("装车") && !middle.contains("装车")) {
                             itemNo = sb.append(start).append(middle).append("装车").append(end).toString();
                         }
                        bizSkuInfoV3Service.updateItemNo(skuInfo.getId(),itemNo);
                        bizSkuInfoV3Service.updateSizeAndColor(skuInfo);
                    }
                }
            }
        }
    }

    /**
     * 修改上架商品表对应的产品ID
     * @param skuId
     * @param prodId
     */
    private void updateProdIdForOpShelfSku(Integer skuId, Integer prodId) {
        BizOpShelfSku opShelfSku = new BizOpShelfSku();
        opShelfSku.setSkuInfo(new BizSkuInfo(skuId));
        List<BizOpShelfSku> opShelfSkuList = bizOpShelfSkuV2Dao.findList(opShelfSku);
        if (CollectionUtils.isNotEmpty(opShelfSkuList)) {
            for (BizOpShelfSku bizOpShelfSku : opShelfSkuList) {
                bizOpShelfSkuV2Dao.updateProdId(bizOpShelfSku.getId(),prodId);
            }
        }
    }

    private void contrastSku(Integer spuId, Integer needId) {
        BizSkuInfo skuInfo = new BizSkuInfo();
        skuInfo.setProductInfo(new BizProductInfo(needId));
        List<BizSkuInfo> needSkuList = bizSkuInfoV3Service.findList(skuInfo);
        skuInfo.setProductInfo(new BizProductInfo(spuId));
        List<BizSkuInfo> skuInfoList = bizSkuInfoV3Service.findList(skuInfo);
        List<BizSkuInfo> skuList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(needSkuList) && CollectionUtils.isNotEmpty(skuInfoList)) {
            for (BizSkuInfo needSku : needSkuList) {
                String needItemNo = needSku.getItemNo();
                String needProperty = StringUtils.substring(needItemNo, needItemNo.indexOf("/")).replaceAll("\\s*","");
                for (BizSkuInfo bizSkuInfo : skuInfoList) {
                    String itemNo = bizSkuInfo.getItemNo();
                    String property = StringUtils.substring(itemNo, itemNo.indexOf("/")).replaceAll("\\s*","");
                    if (property.equals(needProperty)) {
                        skuList.add(bizSkuInfo);
                        changeSku(needSku.getId(),bizSkuInfo.getId());
                    }
                }
            }
            //不同商品
            skuInfoList.removeAll(skuList);
            if (CollectionUtils.isNotEmpty(skuInfoList)) {
                for (BizSkuInfo bizSkuInfo : skuInfoList) {
                    bizSkuInfoV3Service.updateProdId(bizSkuInfo.getId(),needId);
                    updateProdIdForOpShelfSku(bizSkuInfo.getId(),needId);
                }
            }
        }
    }

    /**
     * 删除多余重复商品，修改其它表对应的skuId
     * @param needSkuId
     * @param skuId
     */
    private void changeSku(Integer needSkuId, Integer skuId) {
        bizSkuInfoV3Service.delete(new BizSkuInfo(skuId));
        bizOpShelfSkuV2Service.updateSkuId(needSkuId,skuId);
        bizOrderDetailService.updateSkuId(needSkuId,skuId);
        bizRequestDetailService.updateSkuId(needSkuId,skuId);
        bizPoDetailService.updateSkuId(needSkuId,skuId);
        bizSendGoodsRecordService.updateSkuId(needSkuId,skuId);
        bizCollectGoodsRecordService.updateSkuId(needSkuId,skuId);
        bizInventoryViewLogService.updateSkuId(needSkuId,skuId);
        bizInventorySkuService.updateSkuId(needSkuId,skuId);
    }

    private void updateItemNo(String itemNo, Integer prodId) {
        StringBuilder sb = new StringBuilder(itemNo);
        bizProductInfoDao.updateItemNo(prodId,sb.append("#").toString());
        BizSkuInfo bizSkuInfo = new BizSkuInfo();
        bizSkuInfo.setProductInfo(new BizProductInfo(prodId));
        List<BizSkuInfo> skuList = bizSkuInfoV3Service.findList(bizSkuInfo);
        if (CollectionUtils.isNotEmpty(skuList)) {
            for (BizSkuInfo skuInfo : skuList) {
                String s = skuInfo.getItemNo().substring(skuInfo.getItemNo().indexOf("/"));
                StringBuilder stringBuilder = new StringBuilder(itemNo);
                bizSkuInfoV3Service.updateItemNo(skuInfo.getId(),stringBuilder.append("#").append(s).toString().replaceAll("\\s*",""));
            }
        }
    }

    /**
     *删除以图为准，套二，套三，没有装车和打包的商品，并记录
     * @param needId
     */
    private void deleteSomeSku(Integer needId) {
        BizSkuInfo bizSkuInfo = new BizSkuInfo();
        bizSkuInfo.setProductInfo(new BizProductInfo(needId));
        List<BizSkuInfo> skuInfos = bizSkuInfoV3Service.findList(bizSkuInfo);
        if (CollectionUtils.isNotEmpty(skuInfos)) {
            for (BizSkuInfo skuInfo : skuInfos) {
                String itemNo = skuInfo.getItemNo();
                if (itemNo.contains("以图为准") || itemNo.contains("套二") || itemNo.contains("套三")) {
                    bizSkuInfoV3Service.delete(skuInfo);
                    LOGGER.info("商品ID为【{}】,货号为【{}】的商品删除成功",skuInfo.getId(),itemNo);
                }
                if (!itemNo.contains("打包") && !itemNo.contains("装车")) {
                    bizSkuInfoV3Service.delete(skuInfo);
                    LOGGER.info("商品ID为【{}】,货号为【{}】的商品删除成功",skuInfo.getId(),itemNo);
                }
            }
        }
    }


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void changePrice(Integer prodId, String itemNo, String size, BigDecimal settlementPrice, BigDecimal marketingPrice) {
        List<BizSkuInfo> bizSkuInfos = bizSkuInfoV3Service.findSkuBySpuAndSize(prodId,itemNo,size);
        if (CollectionUtils.isNotEmpty(bizSkuInfos)) {
            for (BizSkuInfo bizSkuInfo : bizSkuInfos) {
                bizSkuInfoV3Service.updatePrice(bizSkuInfo.getId(),settlementPrice);
                BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
                bizOpShelfSku.setSkuInfo(bizSkuInfo);
                List<BizOpShelfSku> opShelfSkus = bizOpShelfSkuV2Service.findList(bizOpShelfSku);
                if (CollectionUtils.isNotEmpty(opShelfSkus)) {
                    for (BizOpShelfSku opShelfSku : opShelfSkus) {
                        bizOpShelfSkuV2Service.updatePrice(opShelfSku.getId(),settlementPrice,marketingPrice);
                    }
                }
            }
        }
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void changeSpu(Integer prodId) {
        BizProductInfo bizProductInfo = get(prodId);
        BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
        bizOpShelfSku.setProductInfo(new BizProductInfo(prodId));
        List<BizOpShelfSku> opShelfSkuList = bizOpShelfSkuV2Service.findList(bizOpShelfSku);
        if (CollectionUtils.isNotEmpty(opShelfSkuList)) {
            for(int i = 0; i < opShelfSkuList.size(); i++) {
                if (i == 0) {
                    bizProductInfo.setMinPrice(opShelfSkuList.get(i).getSalePrice());
                    bizProductInfo.setMaxPrice(opShelfSkuList.get(i).getSalePrice());
                }
                if (opShelfSkuList.get(i).getSalePrice() < bizProductInfo.getMinPrice()) {
                    bizProductInfo.setMinPrice(opShelfSkuList.get(i).getSalePrice());
                }
                if (opShelfSkuList.get(i).getSalePrice() > bizProductInfo.getMaxPrice()) {
                    bizProductInfo.setMaxPrice(opShelfSkuList.get(i).getSalePrice());
                }
            }
            updateMinAndMaxPrice(prodId,bizProductInfo.getMinPrice(),bizProductInfo.getMaxPrice());
        }
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateMinAndMaxPrice(Integer prodId, Double minPrice, Double maxPrice) {
        bizProductInfoDao.updateMinAndMaxPrice(prodId,minPrice,maxPrice);
    }


}