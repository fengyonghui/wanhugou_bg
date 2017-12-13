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
import com.wanhutong.backend.modules.product.entity.BizProductInfo;
import com.wanhutong.backend.modules.product.service.BizProductInfoService;

/**
 * 产品信息表Controller
 * @author zx
 * @version 2017-12-13
 */
@Controller
@RequestMapping(value = "${adminPath}/product/bizProductInfo")
public class BizProductInfoController extends BaseController {

	@Autowired
	private BizProductInfoService bizProductInfoService;
	
	@ModelAttribute
	public BizProductInfo get(@RequestParam(required=false) Integer id) {
		BizProductInfo entity = null;
		if (id!=null){
			entity = bizProductInfoService.get(id);
		}
		if (entity == null){
			entity = new BizProductInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("product:bizProductInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProductInfo bizProductInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProductInfo> page = bizProductInfoService.findPage(new Page<BizProductInfo>(request, response), bizProductInfo); 
		model.addAttribute("page", page);
		return "modules/product/bizProductInfoList";
	}

	@RequiresPermissions("product:bizProductInfo:view")
	@RequestMapping(value = "form")
	public String form(BizProductInfo bizProductInfo, Model model) {
		model.addAttribute("bizProductInfo", bizProductInfo);
		return "modules/product/bizProductInfoForm";
	}

	@RequiresPermissions("product:bizProductInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizProductInfo bizProductInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProductInfo)){
			return form(bizProductInfo, model);
		}
		bizProductInfoService.save(bizProductInfo);
		addMessage(redirectAttributes, "保存产品信息表成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProductInfo/?repage";
	}
	
	@RequiresPermissions("product:bizProductInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProductInfo bizProductInfo, RedirectAttributes redirectAttributes) {
		bizProductInfoService.delete(bizProductInfo);
		addMessage(redirectAttributes, "删除产品信息表成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProductInfo/?repage";
	}

}