/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
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
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatelogInfoService;

/**
 * 目录分类表Controller
 * @author liuying
 * @version 2017-12-06
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/category/bizCatelogInfo")
public class BizCatelogInfoController extends BaseController {

	@Autowired
	private BizCatelogInfoService bizCatelogInfoService;
	
	@ModelAttribute
	public BizCatelogInfo get(@RequestParam(required=false) Integer id) {
		BizCatelogInfo entity = null;
		if (id!=null){
			entity = bizCatelogInfoService.get(id);
		}
		if (entity == null){
			entity = new BizCatelogInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:category:bizCatelogInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCatelogInfo bizCatelogInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCatelogInfo> page = bizCatelogInfoService.findPage(new Page<BizCatelogInfo>(request, response), bizCatelogInfo); 
		model.addAttribute("page", page);
		return "modules/biz/category/bizCatelogInfoList";
	}

	@RequiresPermissions("biz:category:bizCatelogInfo:view")
	@RequestMapping(value = "form")
	public String form(BizCatelogInfo bizCatelogInfo, Model model) {
		model.addAttribute("bizCatelogInfo", bizCatelogInfo);
		return "modules/biz/category/bizCatelogInfoForm";
	}

	@RequiresPermissions("biz:category:bizCatelogInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizCatelogInfo bizCatelogInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCatelogInfo)){
			return form(bizCatelogInfo, model);
		}
		bizCatelogInfoService.save(bizCatelogInfo);
		addMessage(redirectAttributes, "保存目录分类表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCatelogInfo/?repage";
	}
	
	@RequiresPermissions("biz:category:bizCatelogInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCatelogInfo bizCatelogInfo, RedirectAttributes redirectAttributes) {
		bizCatelogInfoService.delete(bizCatelogInfo);
		addMessage(redirectAttributes, "删除目录分类表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCatelogInfo/?repage";
	}

}