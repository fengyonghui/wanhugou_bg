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
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropertyInfoService;

/**
 * 记录当前分类下的所有属性Controller
 * @author liuying
 * @version 2017-12-06
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/category/bizCatePropertyInfo")
public class BizCatePropertyInfoController extends BaseController {

	@Autowired
	private BizCatePropertyInfoService bizCatePropertyInfoService;
	
	@ModelAttribute
	public BizCatePropertyInfo get(@RequestParam(required=false) Integer id) {
		BizCatePropertyInfo entity = null;
		if (id!=null){
			entity = bizCatePropertyInfoService.get(id);
		}
		if (entity == null){
			entity = new BizCatePropertyInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:category:bizCatePropertyInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCatePropertyInfo bizCatePropertyInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCatePropertyInfo> page = bizCatePropertyInfoService.findPage(new Page<BizCatePropertyInfo>(request, response), bizCatePropertyInfo); 
		model.addAttribute("page", page);
		return "modules/biz/category/bizCatePropertyInfoList";
	}

	@RequiresPermissions("biz:category:bizCatePropertyInfo:view")
	@RequestMapping(value = "form")
	public String form(BizCatePropertyInfo bizCatePropertyInfo, Model model) {
		model.addAttribute("bizCatePropertyInfo", bizCatePropertyInfo);
		return "modules/biz/category/bizCatePropertyInfoForm";
	}

	@RequiresPermissions("biz:category:bizCatePropertyInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizCatePropertyInfo bizCatePropertyInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCatePropertyInfo)){
			return form(bizCatePropertyInfo, model);
		}
		bizCatePropertyInfoService.save(bizCatePropertyInfo);
		addMessage(redirectAttributes, "保存该分类属性成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCatePropertyInfo/?repage";
	}
	
	@RequiresPermissions("biz:category:bizCatePropertyInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCatePropertyInfo bizCatePropertyInfo, RedirectAttributes redirectAttributes) {
		bizCatePropertyInfoService.delete(bizCatePropertyInfo);
		addMessage(redirectAttributes, "删除该分类属性成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCatePropertyInfo/?repage";
	}

}