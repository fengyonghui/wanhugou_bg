/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropertyInfoService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.entity.tag.TagInfo;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.PropValueService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
import com.wanhutong.backend.modules.sys.service.tag.TagInfoService;
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
import java.util.Map;

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
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private CommonImgService commonImgService;
	@Autowired
	private TagInfoService tagInfoService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	@Autowired
	private DictService dictService;

	@ModelAttribute
	public BizProductInfo get(@RequestParam(required=false) Integer id) {
		BizProductInfo entity = null;
		if (id!=null){
			entity = bizProductInfoService.get(id);
			BizSkuInfo bizSkuInfo=new BizSkuInfo();
			bizSkuInfo.setProductInfo(entity);
			List<BizSkuInfo> skuInfosList = bizSkuInfoService.findList(bizSkuInfo);
			entity.setSkuInfosList(skuInfosList);
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
		CommonImg commonImg=new CommonImg();
		commonImg.setImgType(ImgEnum.MAIN_PRODUCT_TYPE.getCode());
		commonImg.setObjectId(bizProductInfo.getId());
		commonImg.setObjectName("biz_product_info");
		if(bizProductInfo.getId()!=null){
			List<CommonImg> imgList=commonImgService.findList(commonImg);
			commonImg.setImgType(ImgEnum.SUB_PRODUCT_TYPE.getCode());
			List<CommonImg> subImgList=commonImgService.findList(commonImg);
			commonImg.setImgType(ImgEnum.LIST_PRODUCT_TYPE.getCode());
			List<CommonImg> itemImgList=commonImgService.findList(commonImg);
			String photos="";
			String photoDetails="";
			String photoLists="";
            for(CommonImg img:imgList){
				photos+="|"+img.getImgServer()+img.getImgPath();
			}
			if(!"".equals(photos)){
				bizProductInfo.setPhotos(photos);
			}
			for(CommonImg img:subImgList){
				photoDetails+="|"+img.getImgServer()+img.getImgPath();
			}
			if(!"".equals(photoDetails)){
				bizProductInfo.setPhotoDetails(photoDetails);
			}
			for(CommonImg img:itemImgList){
				photoLists+="|"+img.getImgServer()+img.getImgPath();
			}
			if(!"".equals(photoLists)){
				bizProductInfo.setPhotoLists(photoLists);
			}
		}
		List<TagInfo> tagInfos=Lists.newArrayList();
		List<TagInfo> tagInfoList=tagInfoService.findList(new TagInfo());
		Dict dict=new Dict();
		for(TagInfo tagInfo:tagInfoList){
			if(tagInfo.getDict()!=null && StringUtils.isNotBlank(tagInfo.getDict().getType())){
				dict.setType(tagInfo.getDict().getType());
				List<Dict> dictList=dictService.findList(dict);
				tagInfo.setDictList(dictList);
				tagInfos.add(tagInfo);
			}

		}

//		if(bizProductInfo.getId()!=null){
//			BizProdPropertyInfo bizProdPropertyInfo=new BizProdPropertyInfo();
//			bizProdPropertyInfo.setProductInfo(bizProductInfo);
//			Map<String, List<BizProdPropValue>> prodPropValueMap=bizProdPropertyInfoService.findMapList(bizProdPropertyInfo);
//			model.addAttribute("prodPropValueMap",prodPropValueMap);
//		}

			List<BizVarietyInfo> varietyInfoList=bizVarietyInfoService.findList(new BizVarietyInfo());

			model.addAttribute("prodPropertyInfo",new BizProdPropertyInfo());
			model.addAttribute("entity", bizProductInfo);
			model.addAttribute("tagInfoList",tagInfos);
			model.addAttribute("varietyInfoList",varietyInfoList);
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

	@ResponseBody
	@RequestMapping(value = "querySkuTreeList")
	public List<Map<String, Object>> querySkuTreeList(BizProductInfo bizProductInfo, RedirectAttributes redirectAttributes) {
		List<SkuProd> list = null;
		//list = bizProductInfoService.convertList(bizProductInfo);
		if(list == null || list.size() == 0){
			addMessage(redirectAttributes, "列表不存在");
		}
		return convertList(list);
	}

	private List<Map<String, Object>> convertList(List<SkuProd> list){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		if(list != null && list.size() > 0 ){
			for (int i = 0; i < list.size(); i++) {
				SkuProd e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getPid());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}

}