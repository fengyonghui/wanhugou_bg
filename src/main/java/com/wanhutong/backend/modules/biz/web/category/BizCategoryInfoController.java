/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCategoryInfoService;

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
	@RequestMapping(value = {"list", ""})
	public String list(BizCategoryInfo bizCategoryInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCategoryInfo> page = bizCategoryInfoService.findPage(new Page<BizCategoryInfo>(request, response), bizCategoryInfo); 
		model.addAttribute("page", page);
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

}