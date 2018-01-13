/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.sku;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropertyInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;

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
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizProdPropertyInfoService bizProdPropertyInfoService;
	@Autowired
	private CommonImgService commonImgService;

	@ModelAttribute
	public BizSkuInfo get(@RequestParam(required=false) Integer id) {
		BizSkuInfo entity = null;
		if (id!=null){
			entity = bizSkuInfoService.get(id);
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
		return "modules/biz/sku/bizSkuInfoList";
	}
	
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "form")
	public String form(BizSkuInfo bizSkuInfo, Model model) {
		model.addAttribute("bizSkuInfo", bizSkuInfo);
		BizProdPropertyInfo bizProdPropertyInfo =new BizProdPropertyInfo();
		bizProdPropertyInfo.setProductInfo(bizSkuInfo.getProductInfo());
		List<BizProdPropertyInfo> prodPropertyInfoList= bizProdPropertyInfoService.findList(bizProdPropertyInfo);
		Map<Integer,List<BizProdPropValue>> map=bizProdPropertyInfoService.findMapList(bizProdPropertyInfo);

		CommonImg commonImg=new CommonImg();
		commonImg.setImgType(ImgEnum.SKU_TYPE.getCode());
		commonImg.setObjectId(bizSkuInfo.getId());
		commonImg.setObjectName("biz_sku_info");
		if(bizSkuInfo.getId()!=null){
			List<CommonImg> imgList=commonImgService.findList(commonImg);
			commonImg.setImgType(ImgEnum.SKU_TYPE.getCode());
			String photos="";
			for(CommonImg img:imgList){
				photos+="|"+img.getImgPath();
			}
			if(!"".equals(photos)){
				bizSkuInfo.setPhotos(photos);
			}
		}
		model.addAttribute("prodPropInfoList", prodPropertyInfoList);
		model.addAttribute("map", map);
		return "modules/biz/sku/bizSkuInfoForm";
	}

	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuInfo bizSkuInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuInfo)){
			return form(bizSkuInfo, model);
		}
		bizSkuInfoService.save(bizSkuInfo);
		addMessage(redirectAttributes, "保存商品sku成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProductInfo/form?id="+bizSkuInfo.getProductInfo().getId();
	}

	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuInfo bizSkuInfo, RedirectAttributes redirectAttributes) {
		bizSkuInfoService.delete(bizSkuInfo);
		addMessage(redirectAttributes, "删除商品sku成功");
		if(bizSkuInfo.getSign()==0){
			return "redirect:"+Global.getAdminPath()+"//biz/sku/bizSkuInfo/?repage";
        }
		return "redirect:"+Global.getAdminPath()+"//biz/product/bizProductInfo/form?id="+bizSkuInfo.getProductInfo().getId();
	}
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSkuList")
	public Map<String, List<BizSkuInfo>> findSkuList(BizSkuInfo bizSkuInfo, String skuIds){
		if (skuIds != null && !"".equals(skuIds)){
			String[] ids =StringUtils.split(skuIds, ",");
			bizSkuInfo.setSkuIds(Lists.newArrayList(ids));
		}
		Map<String, List<BizSkuInfo>> listMap = bizSkuInfoService.findListForProd(bizSkuInfo);
		return listMap;
	}
	@ResponseBody
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "findSysBySku")
	public BizSkuInfo findSysBySku(Integer skuId, HttpServletRequest request, HttpServletResponse response, Model model) {
        BizSkuInfo bizSkuInfo = bizSkuInfoService.get(skuId);
		return bizSkuInfo;
	}


}