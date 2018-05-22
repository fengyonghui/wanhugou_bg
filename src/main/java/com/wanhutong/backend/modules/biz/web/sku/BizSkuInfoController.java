/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.sku;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizVarietyFactor;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropertyInfoService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV2Service;
import com.wanhutong.backend.modules.biz.service.shelf.BizVarietyFactorService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuViewLogService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.ProdTypeEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品skuController
 * @author zx
 * @version 2017-12-07
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/bizSkuInfo")
public class BizSkuInfoController extends BaseController {

	@Autowired
	private BizSkuInfoV2Service bizSkuInfoService;
	@Autowired
	private BizProdPropertyInfoService bizProdPropertyInfoService;
	@Autowired
	private CommonImgService commonImgService;
	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private AttributeValueV2Service attributeValueV2Service;
	@Autowired
	private BizSkuViewLogService bizSkuViewLogService;
	@Autowired
	private BizProductInfoV2Service bizProductInfoV2Service;
	@Autowired
    private BizVarietyFactorService bizVarietyFactorService;



	@ModelAttribute
	public BizSkuInfo get(@RequestParam(required=false) Integer id) {
		BizSkuInfo entity = null;
		if (id!=null){
			entity = bizSkuInfoService.get(id);
			CommonImg commonImg=new CommonImg();
			commonImg.setImgType(ImgEnum.SKU_TYPE.getCode());
			commonImg.setObjectId(id);
			commonImg.setObjectName("biz_sku_info");
			List<CommonImg> imgList=commonImgService.findList(commonImg);
			String photos = "";
			for(CommonImg img:imgList){
				photos+="|"+img.getImgServer()+img.getImgPath();
			}
			entity.setPhotos(photos);
		}
		if (entity == null){
			entity = new BizSkuInfo();
		}
		return entity;
	}

	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuInfo bizSkuInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuInfo> page = bizSkuInfoService.findPage(new Page<BizSkuInfo>(request, response), bizSkuInfo); 
		model.addAttribute("page", page);
		model.addAttribute("prodType", bizSkuInfo.getProductInfo().getProdType());
		return "modules/biz/sku/bizSkuInfoList";
	}
	
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "form")
	public String form(BizSkuInfo bizSkuInfo, Model model) {
		model.addAttribute("bizSkuInfo", bizSkuInfo);
		model.addAttribute("prodType",bizSkuInfo.getProductInfo().getProdType());
//		BizProdPropertyInfo bizProdPropertyInfo =new BizProdPropertyInfo();
//		bizProdPropertyInfo.setProductInfo(bizSkuInfo.getProductInfo());
		//Map<String,List<BizProdPropValue>> map=bizProdPropertyInfoService.findMapList(bizProdPropertyInfo);
		AttributeValueV2 attributeValueV2 =new AttributeValueV2();
		attributeValueV2.setObjectId(bizSkuInfo.getId());
		attributeValueV2.setObjectName("biz_sku_info");
		List<AttributeValueV2> attributeValueList=attributeValueV2Service.findList(attributeValueV2);

	//	model.addAttribute("map", map);
		model.addAttribute("attributeValueList",attributeValueList);
		return "modules/biz/sku/bizSkuInfoFormV2";
	}

	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuInfo bizSkuInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuInfo)){
			return form(bizSkuInfo, model);
		}
		bizSkuInfoService.save(bizSkuInfo);
		addMessage(redirectAttributes, "保存商品sku成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProductInfo/form?id="+bizSkuInfo.getProductInfo().getId()+"&productInfo.prodType="+bizSkuInfo.getProductInfo().getProdType();
	}

	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuInfo bizSkuInfo, RedirectAttributes redirectAttributes) {
		//bizSkuInfo.setDelFlag(BizSkuInfo.DEL_FLAG_DELETE);
		bizSkuInfoService.delete(bizSkuInfo);
		addMessage(redirectAttributes, "删除商品sku成功");
		if(bizSkuInfo.getSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuInfo/?repage&productInfo.prodType="+bizSkuInfo.getProductInfo().getProdType();
        }
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProductInfo/form?id="+bizSkuInfo.getProductInfo().getId()+"&productInfo.prodType="+bizSkuInfo.getProductInfo().getProdType();
	}



	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizSkuInfo bizSkuInfo, RedirectAttributes redirectAttributes) {
		bizSkuInfo.setDelFlag(BizSkuInfo.DEL_FLAG_NORMAL);
		bizSkuInfoService.recovery(bizSkuInfo);
		addMessage(redirectAttributes, "恢复商品sku成功");
		if(bizSkuInfo.getSign()==0){
			return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuInfo/?repage&productInfo.prodType="+bizSkuInfo.getProductInfo().getProdType();
		}
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProductInfo/form?id="+bizSkuInfo.getProductInfo().getId()+"&productInfo.prodType="+bizSkuInfo.getProductInfo().getProdType();
	}


	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSkuList")
	public Map<String, List<BizSkuInfo>> findSkuList(BizSkuInfo bizSkuInfo, String skuIds){
		if (skuIds != null && !"".equals(skuIds)){
			String[] ids =StringUtils.split(skuIds, ",");
			bizSkuInfo.setSkuIds(Lists.newArrayList(ids));
		}
		bizSkuInfo.setSkuType(SkuTypeEnum.OWN_PRODUCT.getCode());
		Map<String, List<BizSkuInfo>> listMap = bizSkuInfoService.findListForProd(bizSkuInfo);
		return listMap;

	}
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSkuListV2")
	public Map<String,Object> findSkuListV2(BizSkuInfo bizSkuInfo, String skuIds){
		if (skuIds != null && !"".equals(skuIds)){
			String[] ids =StringUtils.split(skuIds, ",");
			bizSkuInfo.setSkuIds(Lists.newArrayList(ids));
		}
		Map<String,Object> map =new HashMap<>();
		bizSkuInfo.setSkuType(SkuTypeEnum.OWN_PRODUCT.getCode());
		Map<String, List<BizSkuInfo>> listMap = bizSkuInfoService.findListForProd(bizSkuInfo);
		List<BizVarietyFactor> varietyInfoList=bizVarietyFactorService.findList(new BizVarietyFactor());
		Map<Integer,List<String>> factorMap=new HashMap<>();
		for(BizVarietyFactor varietyFactor:varietyInfoList){
			Integer key = varietyFactor.getVarietyInfo().getId();

			if(factorMap.containsKey(key)){
				List<String> stringList=factorMap.get(key);
				stringList.add((varietyFactor.getServiceFactor().toString().length()<2?"0"+varietyFactor.getServiceFactor():varietyFactor.getServiceFactor())+":["+varietyFactor.getMinQty()+"~"+varietyFactor.getMaxQty()+"]");
				factorMap.remove(key);
				factorMap.put(key,stringList);
			}else {
				List<String> lists=Lists.newArrayList();
				lists.add((varietyFactor.getServiceFactor().toString().length()<2?"0"+varietyFactor.getServiceFactor():varietyFactor.getServiceFactor())+":["+varietyFactor.getMinQty()+"~"+varietyFactor.getMaxQty()+"]");
				factorMap.put(key,lists);
			}
		}
		map.put("skuMap",listMap);
		map.put("serviceFactor",factorMap);
		return map;

	}
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSysBySku")
	public BizSkuInfo findSysBySku(Integer skuId, HttpServletRequest request, HttpServletResponse response, Model model) {
        BizSkuInfo bizSkuInfo = bizSkuInfoService.get(skuId);
		return bizSkuInfo;
	}

	//根据多个id选择商品
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSkuNameList")
	public List<BizSkuInfo> findSkuNameList(String ids,BizSkuInfo bizSkuInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<BizSkuInfo> bizSkuInfoList = new ArrayList<>();
	    if (ids != null && !"".equals(ids)){
            String[] id = ids.split(",");
			for (int i = 0; i < id.length; i++) {
				if(id[i].trim().equals("on")){
					System.out.println(id[i].trim()+" 不是skuID值不操作");
				}else {
					BizSkuInfo bizSkuInfo1 = bizSkuInfoService.get(Integer.parseInt(id[i].trim()));
					bizSkuInfoList.add(bizSkuInfo1);
				}
			}
        }
        return bizSkuInfoList;
	}

	//根据多个id选择商品
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSkuNameListV2")
	public List<BizSkuInfo> findSkuNameListV2(String ids,BizSkuInfo bizSkuInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<BizSkuInfo> bizSkuInfoList = new ArrayList<>();
		if (ids != null && !"".equals(ids)){
			String[] id = ids.split(",");
			for (int i = 0; i < id.length; i++) {
				if(id[i].trim().equals("on")){
					System.out.println(id[i].trim()+" 不是skuID值不操作");
				}else {
					BizSkuInfo bizSkuInfo1 = bizSkuInfoService.get(Integer.parseInt(id[i].trim()));
                    BizProductInfo bizProductInfo = bizProductInfoV2Service.get(bizSkuInfo1.getProductInfo().getId());
                    BizVarietyFactor bizVarietyFactor = new BizVarietyFactor();
                    bizVarietyFactor.setVarietyInfo(new BizVarietyInfo(bizProductInfo.getBizVarietyInfo().getId()));
                    List<BizVarietyFactor> bvFactorList = bizVarietyFactorService.findList(bizVarietyFactor);
                    DecimalFormat df = new DecimalFormat("0.00");
                    for (BizVarietyFactor varietyFactor:bvFactorList) {
                        Double salePrice = bizSkuInfo1.getBuyPrice()*(1+(float)varietyFactor.getServiceFactor()/100);
                        String price = df.format(salePrice);
                        varietyFactor.setSalePrice(Double.parseDouble(price));
                    }
                    bizSkuInfo1.setBvFactorList(bvFactorList);
                    bizSkuInfoList.add(bizSkuInfo1);
				}
			}
		}
		return bizSkuInfoList;
	}

	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSkuInfoList")
	public Map<String,Object> findSkuInfoList(BizSkuInfo bizSkuInfo,Model model){
        Map<String,Object> map=new HashMap<String, Object>();
		List<BizSkuInfo> list=bizSkuInfoService.findListByParam(bizSkuInfo);
		List<BizSkuInfo> skuInfoList=Lists.newArrayList();
		for(BizSkuInfo skuInfo:list){
			List<AttributeValueV2> skuPropValueList=skuInfo.getAttrValueList();
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

			CommonImg commonImg=new CommonImg();
			commonImg.setImgType(ImgEnum.SKU_TYPE.getCode());
			commonImg.setObjectId(skuInfo.getId());
			commonImg.setObjectName("biz_sku_info");
			List<CommonImg> imgList = commonImgService.findList(commonImg);
			if (CollectionUtils.isNotEmpty(imgList)) {
				skuInfo.setDefaultImg(imgList.get(0).getImgServer().concat(imgList.get(0).getImgPath()));
			}
			skuInfoList.add(skuInfo);

		}
        map.put("skuInfoList",skuInfoList);
		List<BizInventoryInfo> inventoryInfoList=bizInventoryInfoService.findList(new BizInventoryInfo());
        map.put("inventoryInfoList",inventoryInfoList);
        List<Dict> dictList=DictUtils.getDictList("inv_type");
        map.put("dictList",dictList);
			return map;
	}

	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "saveSkuInfo")
	public String saveBizSkuInfo(Integer skuId, Double money) {
		String flag = "";
		try {
			BizSkuInfo skuInfo = bizSkuInfoService.get(skuId);
			skuInfo.setBuyPrice(money);
			bizSkuInfoService.saveSkuInfo(skuInfo);
			flag = "ok";

		} catch (Exception e) {
			flag = "error";
			logger.error(e.getMessage());
		}
		return flag;
	}

	/**
	 * C端商品上下架form 搜索商品
	 * */
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "cendFindSkuList")
	public Map<String, List<BizSkuInfo>> cendFindSkuList(BizSkuInfo bizSkuInfo, String skuIds){
		if (skuIds != null && !"".equals(skuIds)){
			String[] ids =StringUtils.split(skuIds, ",");
			bizSkuInfo.setSkuIds(Lists.newArrayList(ids));
		}
		//	bizSkuInfo.setSkuType(SkuTypeEnum.OWN_PRODUCT.getCode());
		Map<String, List<BizSkuInfo>> listMap = bizSkuInfoService.findListForCendProd(bizSkuInfo);
		return listMap;
	}

    /**
     * 代采订单添加详情时。搜索商品
     * @param bizSkuInfo
     * @return
     */
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findPurseSkuList")
	public List<BizSkuInfo> findPurseSkuList(BizSkuInfo bizSkuInfo) {
//        BizProductInfo bizProductInfo = new BizProductInfo();
//        bizProductInfo.setProdType(Byte.parseByte(ProdTypeEnum.CUSTPROD.getType()));
//        bizSkuInfo.setProductInfo(bizProductInfo);
//        List<BizSkuInfo> skuList = bizSkuInfoService.findList(bizSkuInfo);
        List<BizSkuInfo> purseSkuList = bizSkuInfoService.findPurseSkuList(bizSkuInfo);
        for (BizSkuInfo skuInfo:purseSkuList) {
            AttributeValueV2 valueV2 = new AttributeValueV2();
            valueV2.setObjectId(skuInfo.getId());
            valueV2.setObjectName("biz_sku_info");
            List<AttributeValueV2> attributeValueV2List = attributeValueV2Service.findList(valueV2);
            skuInfo.setAttrValueList(attributeValueV2List);
        }
        return purseSkuList;
    }

}