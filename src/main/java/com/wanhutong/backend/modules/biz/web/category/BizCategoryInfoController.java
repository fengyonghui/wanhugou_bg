/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.category;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropertyInfoService;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoService;
import com.wanhutong.backend.modules.biz.service.category.BizCatelogInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;
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
 * 垂直商品类目表Controller
 * @author liuying
 * @version 2017-12-06
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/category/bizCategoryInfo")
public class BizCategoryInfoController extends BaseController {

	@Autowired
	private BizCategoryInfoService bizCategoryInfoService;
	@Autowired
	private BizCatelogInfoService bizCatelogInfoService;
	@Autowired
	private PropertyInfoService propertyInfoService;
	@Autowired
	private DefaultPropService defaultPropService;
	@Autowired
	private CommonImgService commonImgService;
	@Autowired
	private BizCatePropertyInfoService bizCatePropertyInfoService;

	@ModelAttribute
	public BizCategoryInfo get(@RequestParam(required=false) Integer id) {
		BizCategoryInfo entity = null;
		if (id!=null){
			entity = bizCategoryInfoService.get(id);

		}
		if (entity == null){
			entity = new BizCategoryInfo();
		}
		return entity;
	}
	@RequiresPermissions("biz:category:bizCategoryInfo:view")
	@RequestMapping(value = {""})
	public String index(BizCategoryInfo bizCategoryInfo, Model model) {
		return "modules/biz/category/bizCategoryInfoIndex";
	}


	@RequiresPermissions("biz:category:bizCategoryInfo:view")
	@RequestMapping(value = {"list"})
	public String list(BizCategoryInfo bizCategoryInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("page", bizCategoryInfoService.pageFindList(new Page<BizCategoryInfo>(request, response),bizCategoryInfo));
		return "modules/biz/category/bizCategoryInfoList";
	}


	@RequiresPermissions("biz:category:bizCategoryInfo:view")
	@RequestMapping(value = "form")
	public String form(BizCategoryInfo bizCategoryInfo, Model model) {
		if (bizCategoryInfo.getParent()==null || bizCategoryInfo.getParent().getId()==null || bizCategoryInfo.getParent().getId()==0){
          DefaultProp defaultProp=new DefaultProp("catalog");
           List<DefaultProp> list= defaultPropService.findList(defaultProp);
           if(list!=null && list.size()>0){
               BizCatelogInfo bizCatelogInfo=bizCatelogInfoService.get(Integer.parseInt(list.get(0).getPropValue()));
               BizCategoryInfo categoryInfo=new BizCategoryInfo();
               categoryInfo.setName(bizCatelogInfo.getName());
               categoryInfo.setId(0);
               categoryInfo.setDescription(bizCatelogInfo.getDescription());
               bizCategoryInfo.setParent(categoryInfo);
           }
		}else {
			bizCategoryInfo.setParent(bizCategoryInfoService.get(bizCategoryInfo.getParent().getId()));
		}
//		PropertyInfo propertyInfo=new PropertyInfo();
//		List<PropertyInfo> propertyInfoList=propertyInfoService.findList(propertyInfo);
//		//Map<Integer,List<PropValue>> map=propertyInfoService.findMapList(propertyInfo);
//		Map<Integer,List<PropValue>> map=propertyInfoService.findMapList(propertyInfo,bizCategoryInfo);
		CommonImg commonImg=new CommonImg();

		commonImg.setImgType(ImgEnum.CATEGORY_TYPE.getCode());
		commonImg.setObjectId(bizCategoryInfo.getId());
		commonImg.setObjectName("biz_category_info");
		if(bizCategoryInfo.getId()!=null){
			List<CommonImg> imgList=commonImgService.findList(commonImg);
			if(imgList!=null && imgList.size()>0){
				bizCategoryInfo.setCatePhoto(imgList.get(0).getImgServer()+imgList.get(0).getImgPath());
				bizCategoryInfo.setImgId(imgList.get(0).getId());
			}
		}
//		BizCatePropertyInfo bizCatePropertyInfo=new BizCatePropertyInfo();
//		bizCatePropertyInfo.setCategoryInfo(bizCategoryInfo);
//		if(bizCategoryInfo.getId()!=null){
//			Map<Integer,List<BizCatePropValue>> catePropValueMap=bizCatePropertyInfoService.findMapList(bizCatePropertyInfo);
//			bizCategoryInfo.setCatePropValueMap(catePropValueMap);
//		//	model.addAttribute("catePropValueMap",catePropValueMap);
//		}
//		bizCategoryInfo.setMap(map);
//		bizCategoryInfo.setPropertyInfoList(propertyInfoList);
		//bizCategoryInfo.setCheckedPropValue(catePropValueMap);
		model.addAttribute("bizCategoryInfo", bizCategoryInfo);
	//	model.addAttribute("propertyInfo",propertyInfo);
	//	model.addAttribute("propertyInfoList", propertyInfoList);
	//	model.addAttribute("map", map);

		return "modules/biz/category/bizCategoryInfoForm";
	}

	@ResponseBody
	@RequestMapping("propertyForm")
	public Map<Integer, List<PropValue>> propertyForm(String value, Integer id) {
		PropertyInfo propertyInfo=propertyInfoService.get(id);
		if (value != null && !"".equals(value)){
			propertyInfo.getPropValue().setValue(value);
		}
		Map<Integer,List<PropValue>> map=propertyInfoService.findMapList(propertyInfo);
		return map;
	}

	@RequiresPermissions("biz:category:bizCategoryInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizCategoryInfo bizCategoryInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCategoryInfo)){
			return form(bizCategoryInfo, model);
		}
		bizCategoryInfoService.save(bizCategoryInfo);
		addMessage(redirectAttributes, "保存商品类别成功");
//		Integer id = bizCategoryInfo.getParentId()==0 ? null : bizCategoryInfo.getParentId();
		return "redirect:" + adminPath + "/biz/category/bizCategoryInfo/list?id="+bizCategoryInfo.getParentId()+"&parentIds="+bizCategoryInfo.getParentIds()+"&cid="+bizCategoryInfo.getId();

	}
	
	@RequiresPermissions("biz:category:bizCategoryInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCategoryInfo bizCategoryInfo, RedirectAttributes redirectAttributes) {
		bizCategoryInfoService.delete(bizCategoryInfo);
		addMessage(redirectAttributes, "删除商品类别成功");
		//Integer id = bizCategoryInfo.getParentId()==0 ? null : bizCategoryInfo.getParentId();
		return "redirect:" + adminPath + "/biz/category/bizCategoryInfo/list?id="+bizCategoryInfo.getParentId()+"&parentIds="+bizCategoryInfo.getParentIds()+"&cid="+bizCategoryInfo.getId();
	}

	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, @RequestParam(required=false) String type, @RequestParam(required=false) Long grade, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<BizCatelogInfo> list=bizCatelogInfoService.findList(new BizCatelogInfo());
		for (BizCatelogInfo catelogInfo:list) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", 0);
			map.put("name", catelogInfo.getName());
			 mapList=treeDataInfo(extId,catelogInfo);
			mapList.add(map);
		}
		return mapList;
	}

	public List<Map<String, Object>> treeDataInfo(String extId,BizCatelogInfo catelogInfo) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<BizCategoryInfo> list = bizCategoryInfoService.findListInfo(catelogInfo);
		for (int i=0; i<list.size(); i++){
			BizCategoryInfo e = list.get(i);
			if ((StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1))
					&& Global.YES.equals(e.getStatus().toString())){
				Map<String, Object> map = Maps.newHashMap();
				Integer pid=e.getParentId();
				map.put("id", e.getId());
				map.put("pId", pid);
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	@ResponseBody
	@RequestMapping(value = "findListByBrandId")
	public List<Map<String, Object>> findListByBrandId(BizCategoryInfo bizCategoryInfo,Integer brandId){
		bizCategoryInfo.setBrandId(brandId);
		List<BizCategoryInfo> list= bizCategoryInfoService.findListByBrandId(bizCategoryInfo);
		return  convertList(list);
	}

	private List<Map<String, Object>> convertList(List<BizCategoryInfo> list){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		if(list != null && list.size() > 0 ){
			for (int i = 0; i < list.size(); i++) {
				BizCategoryInfo e = list.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
}