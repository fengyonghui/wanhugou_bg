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
import com.wanhutong.backend.modules.product.entity.BizProdPropValue;
import com.wanhutong.backend.modules.product.service.BizProdPropValueService;

/**
 * 记录产品所有属性值Controller
 * @author zx
 * @version 2017-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/product/bizProdPropValue")
public class BizProdPropValueController extends BaseController {

	@Autowired
	private BizProdPropValueService bizProdPropValueService;
	
	@ModelAttribute
	public BizProdPropValue get(@RequestParam(required=false) Integer id) {
		BizProdPropValue entity = null;
		if (id!=null){
			entity = bizProdPropValueService.get(id);
		}
		if (entity == null){
			entity = new BizProdPropValue();
		}
		return entity;
	}
	
	@RequiresPermissions("product:bizProdPropValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProdPropValue bizProdPropValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProdPropValue> page = bizProdPropValueService.findPage(new Page<BizProdPropValue>(request, response), bizProdPropValue); 
		model.addAttribute("page", page);
		return "modules/product/bizProdPropValueList";
	}

	@RequiresPermissions("product:bizProdPropValue:view")
	@RequestMapping(value = "form")
	public String form(BizProdPropValue bizProdPropValue, Model model) {
		model.addAttribute("bizProdPropValue", bizProdPropValue);
		return "modules/product/bizProdPropValueForm";
	}

	@RequiresPermissions("product:bizProdPropValue:edit")
	@RequestMapping(value = "save")
	public String save(BizProdPropValue bizProdPropValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProdPropValue)){
			return form(bizProdPropValue, model);
		}
		bizProdPropValueService.save(bizProdPropValue);
		addMessage(redirectAttributes, "保存记录产品所有属性值成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProdPropValue/?repage";
	}
	
	@RequiresPermissions("product:bizProdPropValue:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProdPropValue bizProdPropValue, RedirectAttributes redirectAttributes) {
		bizProdPropValueService.delete(bizProdPropValue);
		addMessage(redirectAttributes, "删除记录产品所有属性值成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProdPropValue/?repage";
	}

}