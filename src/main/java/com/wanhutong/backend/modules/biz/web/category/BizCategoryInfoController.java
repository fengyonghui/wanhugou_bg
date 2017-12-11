/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.category;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoService;
import com.wanhutong.backend.modules.biz.service.category.BizCatelogInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String list(BizCategoryInfo bizCategoryInfo, Model model) {
		model.addAttribute("list", bizCategoryInfoService.findList(bizCategoryInfo));
		return "modules/biz/category/bizCategoryInfoList";
	}

	@RequiresPermissions("biz:category:bizCategoryInfo:view")
	@RequestMapping(value = "form")
	public String form(BizCategoryInfo bizCategoryInfo, Model model) {
		model.addAttribute("bizCategoryInfo", bizCategoryInfo);
		return "modules/biz/category/bizCategoryInfoForm";
	}

	@RequiresPermissions("biz:category:bizCategoryInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizCategoryInfo bizCategoryInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCategoryInfo)){
			return form(bizCategoryInfo, model);
		}
		bizCategoryInfoService.save(bizCategoryInfo);
		addMessage(redirectAttributes, "保存商品类别成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCategoryInfo/?repage";
	}
	
	@RequiresPermissions("biz:category:bizCategoryInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCategoryInfo bizCategoryInfo, RedirectAttributes redirectAttributes) {
		bizCategoryInfoService.delete(bizCategoryInfo);
		addMessage(redirectAttributes, "删除商品类别成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCategoryInfo/?repage";
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
//
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

}