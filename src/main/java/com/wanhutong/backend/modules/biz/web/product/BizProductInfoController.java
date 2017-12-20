/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropertyInfoService;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
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
import java.util.List;

/**
 * 产品信息表Controller
 * @author zx
 * @version 2017-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/product/bizProductInfo")
public class BizProductInfoController extends BaseController {

	@Autowired
	private BizProductInfoService bizProductInfoService;
	@Autowired
	private BizCatePropValueService bizCatePropValueService;
	@Autowired
	private BizCatePropertyInfoService bizCatePropertyInfoService;
	@Autowired
	private DefaultPropService defaultPropService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizCategoryInfoService bizCategoryInfoService;
	@Autowired
	private CommonImgService commonImgService;

	@ModelAttribute
	public BizProductInfo get(@RequestParam(required=false) Integer id) {
		BizProductInfo entity = null;

		if (id!=null){
			entity = bizProductInfoService.get(id);
			BizSkuInfo bizSkuInfo=new BizSkuInfo();
			bizSkuInfo.setProductInfo(entity);
			List<BizSkuInfo> skuInfosList = bizSkuInfoService.findList(bizSkuInfo);
			entity.setSkuInfosList(skuInfosList);

			CommonImg commonImg=new CommonImg();
			commonImg.setImgType(ImgEnum.MAIN_PRODUCT_TYPE.getCode());
			commonImg.setObjectId(entity.getId());
			commonImg.setObjectName("biz_product_info");

			List<CommonImg> imgList=commonImgService.findList(commonImg);

			entity.setCommonImgList(imgList);

//			if(imgList!=null&&imgList.size()>0){
//				entity.setProdIndex(imgList.get(0));
//			}
//			BizImg img=new BizImg();
//			img.setImgKey(ImgEnum.PRODUCT_DETAIL_BANNER_IMG.name());
//			img.setObjectId(id.longValue());
//			img.setObjectName(entity.getClass().getSimpleName());
//			List<BizImg> imgs=bizImgService.findList(img);
//			entity.setImgList(imgs);
		}
		if (entity == null){
			entity = new BizProductInfo();
		}

		return entity;
	}


	@RequiresPermissions("biz:product:bizProductInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProductInfo bizProductInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProductInfo> page = bizProductInfoService.findPage(new Page<BizProductInfo>(request, response), bizProductInfo); 
		model.addAttribute("page", page);
		return "modules/biz/product/bizProductInfoList";
	}

	@RequiresPermissions("biz:product:bizProductInfo:view")
	@RequestMapping(value = "form")
	public String form(BizProductInfo bizProductInfo, Model model) {
		List<DefaultProp> list=defaultPropService.findList(new DefaultProp("propBrand"));

		List<BizCatePropValue> catePropValueList=null;
		if(list!=null && list.size()>0){
			DefaultProp defaultProp=list.get(0);
			BizCatePropertyInfo bizCatePropertyInfo=bizCatePropertyInfoService.get(Integer.parseInt(defaultProp.getPropValue()));
			BizCatePropValue bizCatePropValue=new BizCatePropValue();
			bizCatePropValue.setCatePropertyInfo(bizCatePropertyInfo);
			catePropValueList=bizCatePropValueService.findList(bizCatePropValue);
		}
			model.addAttribute("catePropValueList",catePropValueList);
			model.addAttribute("cateList", bizCategoryInfoService.findAllCategory());
			model.addAttribute("prodPropertyInfo",new BizProdPropertyInfo());
			model.addAttribute("entity", bizProductInfo);
		return "modules/biz/product/bizProductInfoForm";
	}

	@RequiresPermissions("biz:product:bizProductInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizProductInfo bizProductInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProductInfo)){
			return form(bizProductInfo, model);
		}
		bizProductInfoService.save(bizProductInfo);
		addMessage(redirectAttributes, "保存产品信息表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProductInfo/?repage";
	}

	
	@RequiresPermissions("biz:product:bizProductInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProductInfo bizProductInfo, RedirectAttributes redirectAttributes) {
		bizProductInfoService.delete(bizProductInfo);
		addMessage(redirectAttributes, "删除产品信息表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProductInfo/?repage";
	}

}