/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.product.web;

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
import com.wanhutong.backend.modules.product.entity.BizProdPropertyInfo;
import com.wanhutong.backend.modules.product.service.BizProdPropertyInfoService;

/**
 * 属性表Controller
 * @author zx
 * @version 2017-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/product/bizProdPropertyInfo")
public class BizProdPropertyInfoController extends BaseController {

	@Autowired
	private BizProdPropertyInfoService bizProdPropertyInfoService;
	
	@ModelAttribute
	public BizProdPropertyInfo get(@RequestParam(required=false) Integer id) {
		BizProdPropertyInfo entity = null;
		if (id!=null){
			entity = bizProdPropertyInfoService.get(id);
		}
		if (entity == null){
			entity = new BizProdPropertyInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("product:bizProdPropertyInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProdPropertyInfo bizProdPropertyInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProdPropertyInfo> page = bizProdPropertyInfoService.findPage(new Page<BizProdPropertyInfo>(request, response), bizProdPropertyInfo); 
		model.addAttribute("page", page);
		return "modules/product/bizProdPropertyInfoList";
	}

	@RequiresPermissions("product:bizProdPropertyInfo:view")
	@RequestMapping(value = "form")
	public String form(BizProdPropertyInfo bizProdPropertyInfo, Model model) {
		model.addAttribute("bizProdPropertyInfo", bizProdPropertyInfo);
		return "modules/product/bizProdPropertyInfoForm";
	}

	@RequiresPermissions("product:bizProdPropertyInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizProdPropertyInfo bizProdPropertyInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProdPropertyInfo)){
			return form(bizProdPropertyInfo, model);
		}
		bizProdPropertyInfoService.save(bizProdPropertyInfo);
		addMessage(redirectAttributes, "保存属性表成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProdPropertyInfo/?repage";
	}
	
	@RequiresPermissions("product:bizProdPropertyInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProdPropertyInfo bizProdPropertyInfo, RedirectAttributes redirectAttributes) {
		bizProdPropertyInfoService.delete(bizProdPropertyInfo);
		addMessage(redirectAttributes, "删除属性表成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProdPropertyInfo/?repage";
	}

}