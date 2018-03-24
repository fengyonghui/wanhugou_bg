/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
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
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.SkuTypeEnum;
import com.wanhutong.backend.modules.enums.TagInfoEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfo;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeInfoService;
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
import java.util.*;

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
	private AttributeInfoService attributeInfoService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	@Autowired
	private DictService dictService;
	@Autowired
	private BizCategoryInfoService bizCategoryInfoService;

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
		List<AttributeInfo> tagInfos=Lists.newArrayList();
		List<AttributeInfo> skuTagInfos=Lists.newArrayList();
		List<AttributeInfo> tagInfoList=attributeInfoService.findList(new AttributeInfo());
		Dict dict=new Dict();
		for(AttributeInfo attributeInfo:tagInfoList){
			List<Dict> dictList=null;
			if(attributeInfo.getDict()!=null && StringUtils.isNotBlank(attributeInfo.getDict().getType())){
				dict.setType(attributeInfo.getDict().getType());
				dictList=dictService.findList(dict);
			}
			if(attributeInfo.getLevel()!=null && TagInfoEnum.PRODTAG.ordinal()==attributeInfo.getLevel()){
				attributeInfo.setDictList(dictList);
				tagInfos.add(attributeInfo);
			}
			if(attributeInfo.getLevel()!=null && TagInfoEnum.SKUTAG.ordinal()==attributeInfo.getLevel()){
				attributeInfo.setDictList(dictList);
				skuTagInfos.add(attributeInfo);
			}

		}
			List<BizCategoryInfo> categoryInfoList=bizCategoryInfoService.findAllList();
			List<BizCategoryInfo> categoryInfos=Lists.newArrayList();
			Set<Integer> parentSet = new HashSet<>();
			 for(BizCategoryInfo categoryInfo:categoryInfoList){
				 String[] parentIds = categoryInfo.getParentIds().split(",");
				 for (String id : parentIds) {
					 parentSet.add(Integer.valueOf(id));
				 }
			 }

			for(BizCategoryInfo categoryInfo:categoryInfoList){
				StringBuilder pStr= new StringBuilder();
				BizCategoryInfo bizCategoryInfo=new BizCategoryInfo();
				Integer id = categoryInfo.getId();
				if (!parentSet.contains(id)){
					bizCategoryInfo.setId(categoryInfo.getId());
					bizCategoryInfo.setName(categoryInfo.getName());
					String[] parentIds = categoryInfo.getParentIds().split(",");
					for (String pid : parentIds) {
						if(Integer.parseInt(pid)!=0){
							BizCategoryInfo categoryInfo1=bizCategoryInfoService.get(Integer.parseInt(pid));
							pStr.append(categoryInfo1.getName()).append("/");
						}

					}
					if(StringUtils.isNotBlank(pStr)){
						bizCategoryInfo.setParentNames(pStr.toString().substring(0,pStr.length()-1));
					}else {
						bizCategoryInfo.setParentNames(pStr.toString());
					}

				}
				if(StringUtils.isNotBlank(bizCategoryInfo.getName())){
					categoryInfos.add(bizCategoryInfo);
				}

			}
			List<BizVarietyInfo> varietyInfoList=bizVarietyInfoService.findList(new BizVarietyInfo());

			 List<Map<String, Object>> skuTypeLit = Lists.newArrayList();
			for (SkuTypeEnum v : SkuTypeEnum.values()) {
				skuTypeLit.add(ImmutableMap.of("type", v.getName(), "code", v.getCode()));
			}

			model.addAttribute("prodPropertyInfo",new BizProdPropertyInfo());
			model.addAttribute("entity", bizProductInfo);
			model.addAttribute("prodTagList",tagInfos);
			model.addAttribute("skuTagList",skuTagInfos);
			model.addAttribute("cateList",categoryInfos);
			model.addAttribute("varietyInfoList",varietyInfoList);
			model.addAttribute("skuTypeLit", skuTypeLit);
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

	private List<Map<String, Object>> convertList(List<?> list){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		if(list != null && list.size() > 0 ){
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i)instanceof SkuProd){
					SkuProd e=(SkuProd)list.get(i);
					Map<String, Object> map = Maps.newHashMap();
					map.put("id", e.getId());
					map.put("pId", e.getPid());
					map.put("name", e.getName());
					mapList.add(map);
				}else if(list.get(i)instanceof BizCategoryInfo){
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

}