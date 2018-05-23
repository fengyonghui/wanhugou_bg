/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoService;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoForVendorService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV2Service;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.enums.TagInfoEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeInfoV2Service;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import net.sf.json.JSONObject;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 产品信息表Controller
 *
 * @author zx
 * @version 2017-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/product/bizProductInfoV2")
public class BizProductInfoV2Controller extends BaseController {

    @Autowired
    private BizProductInfoV2Service bizProductInfoService;
    @Autowired
    private BizProductInfoForVendorService bizProductInfoForVendorService;
    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private AttributeInfoV2Service attributeInfoV2Service;
    @Resource
    private AttributeValueV2Service attributeValueV2Service;
    @Autowired
    private BizVarietyInfoService bizVarietyInfoService;
    @Autowired
    private DictService dictService;
    @Autowired
    private BizCategoryInfoService bizCategoryInfoService;

    @ModelAttribute
    public BizProductInfo get(@RequestParam(required = false) Integer id) {
        BizProductInfo entity = null;
        if (id != null) {
            entity = bizProductInfoService.get(id);
            BizSkuInfo bizSkuInfo = new BizSkuInfo();
            bizSkuInfo.setProductInfo(entity);
            List<BizSkuInfo> skuInfosList = bizSkuInfoService.findList(bizSkuInfo);
            Collections.reverse(skuInfosList);

            skuInfosList.forEach(o -> {
                Map<String, List<AttributeValueV2>> attMap = Maps.newHashMap();
                AttributeValueV2 attributeValueV2 = new AttributeValueV2();
                attributeValueV2.setObjectName(BizProductInfoV2Service.SKU_TABLE);
                attributeValueV2.setObjectId(o.getId());
                List<AttributeValueV2> list = attributeValueV2Service.findList(attributeValueV2);
                for (AttributeValueV2 valueV2 : list) {
                    List<AttributeValueV2> attributeValueV2s = attMap.putIfAbsent(String.valueOf(valueV2.getAttrId()), Lists.newArrayList(valueV2));
                    if(attributeValueV2s != null) {
                        attributeValueV2s.add(valueV2);
                    }
                }
                o.setAttrValueMap(attMap);
            });

            entity.setSkuInfosList(skuInfosList);
        }
        if (entity == null) {
            entity = new BizProductInfo();
        }
        return entity;
    }

    @RequiresPermissions("biz:product:bizProductInfo:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizProductInfo bizProductInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizProductInfo> page = bizProductInfoService.findPage(new Page<BizProductInfo>(request, response), bizProductInfo);
        model.addAttribute("page", page);
        model.addAttribute("prodType",bizProductInfo.getProdType());
        return "modules/biz/product/bizProductInfoListV2";
    }

    @RequiresPermissions("biz:product:bizProductInfo:view")
    @RequestMapping(value = "form")
    public String form(BizProductInfo bizProductInfo, Model model) {
        CommonImg commonImg = new CommonImg();
        commonImg.setImgType(ImgEnum.MAIN_PRODUCT_TYPE.getCode());
        commonImg.setObjectId(bizProductInfo.getId());
        commonImg.setObjectName("biz_product_info");
        if (bizProductInfo.getId() != null) {
            List<CommonImg> imgList = commonImgService.findList(commonImg);
            commonImg.setImgType(ImgEnum.SUB_PRODUCT_TYPE.getCode());
            List<CommonImg> subImgList = commonImgService.findList(commonImg);
            commonImg.setImgType(ImgEnum.LIST_PRODUCT_TYPE.getCode());
            List<CommonImg> itemImgList = commonImgService.findList(commonImg);
            String photos = "";
            String photoDetails = "";
            String photoLists = "";
            Map<String,Integer> photosMap = new LinkedHashMap<>();
            Map<String,Integer> detailsMap = new LinkedHashMap<>();
            for (CommonImg img : imgList) {
                photos += img.getImgServer().concat(img.getImgPath()).concat("|");
                photosMap.put(img.getImgServer()+img.getImgPath(),img.getImgSort());
            }
            if (StringUtils.isNotBlank(photos)) {
                bizProductInfo.setPhotos(photos);
            }
            for (CommonImg img : subImgList) {
                photoDetails += "|" + img.getImgServer() + img.getImgPath();
                detailsMap.put(img.getImgServer()+img.getImgPath(),img.getImgSort());
            }
            if (!"".equals(photoDetails)) {
                bizProductInfo.setPhotoDetails(photoDetails);
            }
            for (CommonImg img : itemImgList) {
                photoLists += "|" + img.getImgServer() + img.getImgPath();
            }
            if (!"".equals(photoLists)) {
                bizProductInfo.setPhotoLists(photoLists);
            }
            model.addAttribute("detailsMap",detailsMap);
            model.addAttribute("photosMap",photosMap);
        }

        List<AttributeInfoV2> tagInfos = Lists.newArrayList();
        List<AttributeInfoV2> skuTagInfos = Lists.newArrayList();
        List<AttributeInfoV2> tagInfoList = attributeInfoV2Service.findList(new AttributeInfoV2());
        Dict dict = new Dict();

        AttributeValueV2 attributeValue = new AttributeValueV2();
        attributeValue.setObjectId(bizProductInfo.getId());
        attributeValue.setObjectName(AttributeInfoV2.Level.PRODUCT.getTableName());
        List<AttributeValueV2> attributeValueList = attributeValueV2Service.findList(attributeValue);
        for (AttributeValueV2 a : attributeValueList) {
            bizProductInfo.setTextureStr(a.getValue());
        }


        for (AttributeInfoV2 attributeInfo : tagInfoList) {
            List<Dict> dictList = null;
            if (attributeInfo.getDict() != null && StringUtils.isNotBlank(attributeInfo.getDict().getType())) {
                dict.setType(attributeInfo.getDict().getType());
                dictList = dictService.findList(dict);
            }
            if (attributeInfo.getLevel() != null && TagInfoEnum.PRODTAG.ordinal() == attributeInfo.getLevel()) {
                attributeInfo.setDictList(dictList);
                tagInfos.add(attributeInfo);
            }
            if (attributeInfo.getLevel() != null && TagInfoEnum.SKUTAG.ordinal() == attributeInfo.getLevel()) {
                attributeInfo.setDictList(dictList);
                skuTagInfos.add(attributeInfo);
            }
        }

        List<BizCategoryInfo> categoryInfoList = bizCategoryInfoService.findAllList();
        List<BizCategoryInfo> categoryInfos = Lists.newArrayList();
        Set<Integer> parentSet = new HashSet<>();
        for (BizCategoryInfo categoryInfo : categoryInfoList) {
            String[] parentIds = categoryInfo.getParentIds().split(",");
            for (String id : parentIds) {
                parentSet.add(Integer.valueOf(id));
            }
        }

        for (BizCategoryInfo categoryInfo : categoryInfoList) {
            StringBuilder pStr = new StringBuilder();
            BizCategoryInfo bizCategoryInfo = new BizCategoryInfo();
            Integer id = categoryInfo.getId();
            if (!parentSet.contains(id)) {
                bizCategoryInfo.setId(categoryInfo.getId());
                bizCategoryInfo.setName(categoryInfo.getName());
                String[] parentIds = categoryInfo.getParentIds().split(",");
                for (String pid : parentIds) {
                    if (Integer.parseInt(pid) != 0) {
                        BizCategoryInfo categoryInfo1 = bizCategoryInfoService.get(Integer.parseInt(pid));
                        pStr.append(categoryInfo1.getName()).append("/");
                    }

                }
                if (StringUtils.isNotBlank(pStr)) {
                    bizCategoryInfo.setParentNames(pStr.toString().substring(0, pStr.length() - 1));
                } else {
                    bizCategoryInfo.setParentNames(pStr.toString());
                }

            }
            if (StringUtils.isNotBlank(bizCategoryInfo.getName())) {
                categoryInfos.add(bizCategoryInfo);
            }

        }
        List<BizVarietyInfo> varietyInfoList = bizVarietyInfoService.findList(new BizVarietyInfo());

        List<Map<String, Object>> skuTypeLit = Lists.newArrayList();
        for (SkuTypeEnum v : SkuTypeEnum.values()) {
            skuTypeLit.add(ImmutableMap.of("type", v.getName(), "code", v.getCode()));
        }

        List<BizSkuInfo> skuInfosList = bizProductInfo.getSkuInfosList();
        Map<Integer, List<String>> skuAttrMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(skuInfosList)) {
            for (BizSkuInfo b : skuInfosList) {
                AttributeValueV2 av = new AttributeValueV2();
                av.setObjectId(b.getId());
                av.setObjectName(AttributeInfoV2.Level.SKU.getTableName());
                List<AttributeValueV2> avList = attributeValueV2Service.findList(av);
                for (AttributeValueV2 a : avList) {
                    List<String> attrList = skuAttrMap.putIfAbsent(a.getAttrId(), Lists.newArrayList(a.getValue()));
                    if (attrList != null) {
                        boolean hasNoValue = true;
                        for (String atbv : attrList) {
                            if (atbv.equals(a.getValue())) {
                                hasNoValue = false;
                            }
                        }
                        if (hasNoValue) {
                            attrList.add(a.getValue());
                        }
                    }
                }
            }
        }

        List<String> prodCategoryIdList = Lists.newArrayList();
        List<BizCategoryInfo> prodCategoryList = bizProductInfo.getCategoryInfoList();
        if (CollectionUtils.isNotEmpty(prodCategoryList)) {
            for (BizCategoryInfo b :prodCategoryList) {
                prodCategoryIdList.add(String.valueOf(b.getId()));
            }
        }

        model.addAttribute("prodPropertyInfo", new BizProdPropertyInfo());
        model.addAttribute("prodCategoryIdList", prodCategoryIdList);
        model.addAttribute("entity", bizProductInfo);
        model.addAttribute("prodTagList", tagInfos);
        model.addAttribute("skuTagList", skuTagInfos);
        model.addAttribute("skuAttrMap", skuAttrMap);
        model.addAttribute("cateList", categoryInfos);
        model.addAttribute("varietyInfoList", varietyInfoList);
        model.addAttribute("skuTypeLit", skuTypeLit);
        return "modules/biz/product/bizProductInfoFormV2";
    }

    @RequiresPermissions("biz:product:bizProductInfo:view")
    @RequestMapping(value = "copy")
    public String copy(BizProductInfo bizProductInfo, Model model) {
        CommonImg commonImg = new CommonImg();
        commonImg.setImgType(ImgEnum.MAIN_PRODUCT_TYPE.getCode());
        commonImg.setObjectId(bizProductInfo.getId());
        commonImg.setObjectName("biz_product_info");
        if (bizProductInfo.getId() != null) {
            List<CommonImg> imgList = commonImgService.findList(commonImg);
            commonImg.setImgType(ImgEnum.SUB_PRODUCT_TYPE.getCode());
            List<CommonImg> subImgList = commonImgService.findList(commonImg);
            commonImg.setImgType(ImgEnum.LIST_PRODUCT_TYPE.getCode());
            List<CommonImg> itemImgList = commonImgService.findList(commonImg);
            String photos = "";
            String photoDetails = "";
            String photoLists = "";
            Map<String,Integer> photosMap = new LinkedHashMap<>();
            Map<String,Integer> detailsMap = new LinkedHashMap<>();
            for (CommonImg img : imgList) {
                photos += "|" + img.getImgServer() + img.getImgPath();
                photosMap.put(img.getImgServer()+img.getImgPath(),img.getImgSort());
            }
            if (!"".equals(photos)) {
                bizProductInfo.setPhotos(photos);
            }
            for (CommonImg img : subImgList) {
                photoDetails += "|" + img.getImgServer() + img.getImgPath();
                detailsMap.put(img.getImgServer()+img.getImgPath(),img.getImgSort());
            }
            if (!"".equals(photoDetails)) {
                bizProductInfo.setPhotoDetails(photoDetails);
            }
            for (CommonImg img : itemImgList) {
                photoLists += "|" + img.getImgServer() + img.getImgPath();
            }
            if (!"".equals(photoLists)) {
                bizProductInfo.setPhotoLists(photoLists);
            }
            model.addAttribute("detailsMap",detailsMap);
            model.addAttribute("photosMap",photosMap);
        }

        List<AttributeInfoV2> tagInfos = Lists.newArrayList();
        List<AttributeInfoV2> skuTagInfos = Lists.newArrayList();
        List<AttributeInfoV2> tagInfoList = attributeInfoV2Service.findList(new AttributeInfoV2());
        Dict dict = new Dict();

        AttributeValueV2 attributeValue = new AttributeValueV2();
        attributeValue.setObjectId(bizProductInfo.getId());
        attributeValue.setObjectName(AttributeInfoV2.Level.PRODUCT.getTableName());
        List<AttributeValueV2> attributeValueList = attributeValueV2Service.findList(attributeValue);
        for (AttributeValueV2 a : attributeValueList) {
            bizProductInfo.setTextureStr(a.getValue());
        }


        for (AttributeInfoV2 attributeInfo : tagInfoList) {
            List<Dict> dictList = null;
            if (attributeInfo.getDict() != null && StringUtils.isNotBlank(attributeInfo.getDict().getType())) {
                dict.setType(attributeInfo.getDict().getType());
                dictList = dictService.findList(dict);
            }
            if (attributeInfo.getLevel() != null && TagInfoEnum.PRODTAG.ordinal() == attributeInfo.getLevel()) {
                attributeInfo.setDictList(dictList);
                tagInfos.add(attributeInfo);
            }
            if (attributeInfo.getLevel() != null && TagInfoEnum.SKUTAG.ordinal() == attributeInfo.getLevel()) {
                attributeInfo.setDictList(dictList);
                skuTagInfos.add(attributeInfo);
            }
        }

        List<BizCategoryInfo> categoryInfoList = bizCategoryInfoService.findAllList();
        List<BizCategoryInfo> categoryInfos = Lists.newArrayList();
        Set<Integer> parentSet = new HashSet<>();
        for (BizCategoryInfo categoryInfo : categoryInfoList) {
            String[] parentIds = categoryInfo.getParentIds().split(",");
            for (String id : parentIds) {
                parentSet.add(Integer.valueOf(id));
            }
        }

        for (BizCategoryInfo categoryInfo : categoryInfoList) {
            StringBuilder pStr = new StringBuilder();
            BizCategoryInfo bizCategoryInfo = new BizCategoryInfo();
            Integer id = categoryInfo.getId();
            if (!parentSet.contains(id)) {
                bizCategoryInfo.setId(categoryInfo.getId());
                bizCategoryInfo.setName(categoryInfo.getName());
                String[] parentIds = categoryInfo.getParentIds().split(",");
                for (String pid : parentIds) {
                    if (Integer.parseInt(pid) != 0) {
                        BizCategoryInfo categoryInfo1 = bizCategoryInfoService.get(Integer.parseInt(pid));
                        pStr.append(categoryInfo1.getName()).append("/");
                    }

                }
                if (StringUtils.isNotBlank(pStr)) {
                    bizCategoryInfo.setParentNames(pStr.toString().substring(0, pStr.length() - 1));
                } else {
                    bizCategoryInfo.setParentNames(pStr.toString());
                }

            }
            if (StringUtils.isNotBlank(bizCategoryInfo.getName())) {
                categoryInfos.add(bizCategoryInfo);
            }

        }
        List<BizVarietyInfo> varietyInfoList = bizVarietyInfoService.findList(new BizVarietyInfo());

        List<Map<String, Object>> skuTypeLit = Lists.newArrayList();
        for (SkuTypeEnum v : SkuTypeEnum.values()) {
            skuTypeLit.add(ImmutableMap.of("type", v.getName(), "code", v.getCode()));
        }

        List<BizSkuInfo> skuInfosList = bizProductInfo.getSkuInfosList();
        Map<Integer, List<String>> skuAttrMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(skuInfosList)) {
            for (BizSkuInfo b : skuInfosList) {
                AttributeValueV2 av = new AttributeValueV2();
                av.setObjectId(b.getId());
                av.setObjectName(AttributeInfoV2.Level.SKU.getTableName());
                List<AttributeValueV2> avList = attributeValueV2Service.findList(av);
                for (AttributeValueV2 a : avList) {
                    List<String> attrList = skuAttrMap.putIfAbsent(a.getAttrId(), Lists.newArrayList(a.getValue()));
                    if (attrList != null) {
                        boolean hasNoValue = true;
                        for (String atbv : attrList) {
                            if (atbv.equals(a.getValue())) {
                                hasNoValue = false;
                            }
                        }
                        if (hasNoValue) {
                            attrList.add(a.getValue());
                        }
                    }
                }
            }
        }

        List<String> prodCategoryIdList = Lists.newArrayList();
        List<BizCategoryInfo> prodCategoryList = bizProductInfo.getCategoryInfoList();
        if (CollectionUtils.isNotEmpty(prodCategoryList)) {
            for (BizCategoryInfo b :prodCategoryList) {
                prodCategoryIdList.add(String.valueOf(b.getId()));
            }
        }
        bizProductInfo.setId(null);
        bizProductInfo.setProdCode(StringUtils.EMPTY);
        bizProductInfo.setItemNo(StringUtils.EMPTY);

        model.addAttribute("prodPropertyInfo", new BizProdPropertyInfo());
        model.addAttribute("prodCategoryIdList", prodCategoryIdList);
        model.addAttribute("entity", bizProductInfo);
        model.addAttribute("prodTagList", tagInfos);
        model.addAttribute("skuTagList", skuTagInfos);
        model.addAttribute("skuAttrMap", skuAttrMap);
        model.addAttribute("cateList", categoryInfos);
        model.addAttribute("varietyInfoList", varietyInfoList);
        model.addAttribute("skuTypeLit", skuTypeLit);
        return "modules/biz/product/bizProductInfoCopyFormV2";
    }


    @RequiresPermissions("biz:product:bizProductInfo:edit")
    @RequestMapping(value = "save")
    public String save(BizProductInfo bizProductInfo, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizProductInfo)) {
            return form(bizProductInfo, model);
        }
        bizProductInfoService.save(bizProductInfo);
        addMessage(redirectAttributes, "保存产品信息表成功");
        return "redirect:" + Global.getAdminPath() + "/biz/product/bizProductInfoV2/?repage&prodType="+bizProductInfo.getProdType();
    }

    @RequiresPermissions("biz:product:bizProductInfo:edit")
    @RequestMapping(value = "saveCopy")
    public String saveCopy(BizProductInfo bizProductInfo, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizProductInfo)) {
            return form(bizProductInfo, model);
        }
        bizProductInfoService.save(bizProductInfo, true);
        addMessage(redirectAttributes, "保存产品信息表成功");
        return "redirect:" + Global.getAdminPath() + "/biz/product/bizProductInfoV2/?repage&prodType="+bizProductInfo.getProdType();
    }

    @RequiresPermissions("biz:product:bizProductInfo:edit")
    @RequestMapping(value = "delete")
    public String delete(BizProductInfo bizProductInfo, RedirectAttributes redirectAttributes) {
        bizProductInfo.setDelFlag(BizProductInfo.DEL_FLAG_DELETE);
        bizProductInfoService.delete(bizProductInfo);
        addMessage(redirectAttributes, "删除产品信息表成功");
        return "redirect:" + Global.getAdminPath() + "/biz/product/bizProductInfoV2/?repage&prodType="+bizProductInfo.getProdType();
    }
    @RequiresPermissions("biz:product:bizProductInfo:edit")
    @RequestMapping(value = "recovery")
    public String recovery(BizProductInfo bizProductInfo, RedirectAttributes redirectAttributes) {
        bizProductInfo.setDelFlag(BizProductInfo.DEL_FLAG_NORMAL);
        bizProductInfoService.delete(bizProductInfo);
        addMessage(redirectAttributes, "恢复产品信息表成功");
        return "redirect:" + Global.getAdminPath() + "/biz/product/bizProductInfoV2/?repage&prodType="+bizProductInfo.getProdType();
    }



    @ResponseBody
    @RequestMapping(value = "querySkuTreeList")
    public List<Map<String, Object>> querySkuTreeList(BizProductInfo bizProductInfo, RedirectAttributes redirectAttributes) {
        List<SkuProd> list = null;
        //list = bizProductInfoService.convertList(bizProductInfo);
        if (list == null || list.size() == 0) {
            addMessage(redirectAttributes, "列表不存在");
        }
        return convertList(list);
    }

    private List<Map<String, Object>> convertList(List<?> list) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof SkuProd) {
                    SkuProd e = (SkuProd) list.get(i);
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", e.getId());
                    map.put("pId", e.getPid());
                    map.put("name", e.getName());
                    mapList.add(map);
                } else if (list.get(i) instanceof BizCategoryInfo) {
                    BizCategoryInfo e = (BizCategoryInfo) list.get(i);
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", e.getId());
                    map.put("pId", e.getParentId());
                    map.put("name", e.getName());
                    mapList.add(map);
                }
            }
        }
        return mapList;
    }

    @RequestMapping(value = "saveColorImg")
    public void saveFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件名
        MultipartFile colorFile = multipartRequest.getFile("colorImg");
        if (colorFile != null) {
            String originalFilename = colorFile.getOriginalFilename();
            String fullName = UUID.randomUUID().toString().replaceAll("-", "").concat(originalFilename.substring(originalFilename.indexOf(".")));
            String msg = "";
            boolean ret = false;
            try {
                String result = AliOssClientUtil.uploadObject2OSS(colorFile.getInputStream(), fullName, colorFile.getSize(), AliOssClientUtil.getOssUploadPath());
                if (StringUtils.isNotBlank(result)) {
                    fullName = DsConfig.getImgServer().concat("/").concat(AliOssClientUtil.getOssUploadPath()).concat(fullName);
                    ret = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultMap.put("fullName", fullName);
            resultMap.put("ret", ret);
            resultMap.put("msg", msg);
        }

        List<MultipartFile> files = multipartRequest.getFiles("productImg");
        if (CollectionUtils.isNotEmpty(files)) {
            List<String> imgList = Lists.newArrayList();
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String fullName = UUID.randomUUID().toString().replaceAll("-", "").concat(originalFilename.substring(originalFilename.indexOf(".")));
                try {
                    String result = AliOssClientUtil.uploadObject2OSS(file.getInputStream(), fullName, file.getSize(), AliOssClientUtil.getOssUploadPath());
                    if (StringUtils.isNotBlank(result)) {
                        fullName = DsConfig.getImgServer().concat("/").concat(AliOssClientUtil.getOssUploadPath()).concat(fullName);
                        imgList.add(fullName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resultMap.put("imgList", imgList);
            resultMap.put("ret", CollectionUtils.isNotEmpty(imgList));
            resultMap.put("msg", "");
        }

        response.getWriter().write(JSONObject.fromObject(resultMap).toString());
    }

    /**
     * 用于列表搜索出结果，删除其中一个商品信息时，停留在搜索商品页面
     * */
    @ResponseBody
    @RequiresPermissions("biz:product:bizProductInfo:edit")
    @RequestMapping(value = "prodDelete")
    public String prodDelete(BizProductInfo bizProductInfo, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response, Model model) {
        String delStatusStr="error";
        try {
            bizProductInfo.setDelFlag(BizProductInfo.DEL_FLAG_DELETE);
            bizProductInfoService.delete(bizProductInfo);
            //删除后传值给list，以展示上一次搜索的结果
            list(bizProductInfo,request,response,model);
            addMessage(redirectAttributes, "删除产品信息表成功");
            delStatusStr="ok";
        }catch (Exception e){
            e.printStackTrace();
        }
        return delStatusStr;
    }
    /**
     * 查询货号是否存在
     * */
    @ResponseBody
    @RequestMapping(value = "getItemNoExist")
    public String getItemNoExist(String itemNo, Integer id, String officeName) {
        String vFullName = HanyuPinyinHelper.getFirstLetters(officeName, HanyuPinyinCaseType.UPPERCASE);

        if (!itemNo.startsWith(vFullName)) {
            itemNo = vFullName.concat(itemNo);
        }

        BizProductInfo b = new BizProductInfo();
        b.setItemNoComplete(itemNo);
        List<BizProductInfo> list = bizProductInfoService.findList(b);
        List<BizProductInfo> listForVendor = bizProductInfoForVendorService.findList(b);
        if(id != null) {
            list.removeIf(o -> o.getId().intValue() == id);
            listForVendor.removeIf(o -> o.getId().intValue() == id);
        }
        return String.valueOf(CollectionUtils.isNotEmpty(list) || CollectionUtils.isNotEmpty(listForVendor));
    }


}