/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.sku;

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
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;

/**
 * 商品skuController
 * @author zx
 * @version 2017-12-07
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/bizSkuInfo")
public class BizSkuInfoController extends BaseController {

	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	
	@ModelAttribute
	public BizSkuInfo get(@RequestParam(required=false) Integer id) {
		BizSkuInfo entity = null;
		if (id!=null){
			entity = bizSkuInfoService.get(id);
		}
		if (entity == null){
			entity = new BizSkuInfo();
		}
		return entity;
	}

	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuInfo bizSkuInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuInfo> page = bizSkuInfoService.findPage(new Page<BizSkuInfo>(request, response), bizSkuInfo); 
		model.addAttribute("page", page);
		return "modules/biz/sku/bizSkuInfoList";
	}
	
	@RequiresPermissions("biz:sku:bizSkuInfo:view")
	@RequestMapping(value = "form")
	public String form(BizSkuInfo bizSkuInfo, Model model) {
		model.addAttribute("bizSkuInfo", bizSkuInfo);
		return "modules/biz/sku/bizSkuInfoForm";
	}

	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuInfo bizSkuInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuInfo)){
			return form(bizSkuInfo, model);
		}
		bizSkuInfoService.save(bizSkuInfo);
		addMessage(redirectAttributes, "保存商品sku成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuInfo/?repage";
	}
	
	@RequiresPermissions("biz:sku:bizSkuInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuInfo bizSkuInfo, RedirectAttributes redirectAttributes) {
		bizSkuInfoService.delete(bizSkuInfo);
		addMessage(redirectAttributes, "删除商品sku成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuInfo/?repage";
	}

}