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
import com.wanhutong.backend.modules.product.entity.BizProdCate;
import com.wanhutong.backend.modules.product.service.BizProdCateService;

/**
 * 产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多Controller
 * @author zx
 * @version 2017-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/product/bizProdCate")
public class BizProdCateController extends BaseController {

	@Autowired
	private BizProdCateService bizProdCateService;
	
	@ModelAttribute
	public BizProdCate get(@RequestParam(required=false) Integer id) {
		BizProdCate entity = null;
		if (id!=null){
			entity = bizProdCateService.get(id);
		}
		if (entity == null){
			entity = new BizProdCate();
		}
		return entity;
	}
	
	@RequiresPermissions("product:bizProdCate:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProdCate bizProdCate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProdCate> page = bizProdCateService.findPage(new Page<BizProdCate>(request, response), bizProdCate); 
		model.addAttribute("page", page);
		return "modules/product/bizProdCateList";
	}

	@RequiresPermissions("product:bizProdCate:view")
	@RequestMapping(value = "form")
	public String form(BizProdCate bizProdCate, Model model) {
		model.addAttribute("bizProdCate", bizProdCate);
		return "modules/product/bizProdCateForm";
	}

	@RequiresPermissions("product:bizProdCate:edit")
	@RequestMapping(value = "save")
	public String save(BizProdCate bizProdCate, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProdCate)){
			return form(bizProdCate, model);
		}
		bizProdCateService.save(bizProdCate);
		addMessage(redirectAttributes, "保存产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProdCate/?repage";
	}
	
	@RequiresPermissions("product:bizProdCate:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProdCate bizProdCate, RedirectAttributes redirectAttributes) {
		bizProdCateService.delete(bizProdCate);
		addMessage(redirectAttributes, "删除产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多成功");
		return "redirect:"+Global.getAdminPath()+"/product/bizProdCate/?repage";
	}

}