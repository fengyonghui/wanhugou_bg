/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shop;

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
import com.wanhutong.backend.modules.biz.entity.shop.BizWeShopMark;
import com.wanhutong.backend.modules.biz.service.shop.BizWeShopMarkService;

/**
 * 收藏微店Controller
 * @author Oytang
 * @version 2018-04-10
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shop/bizWeShopMark")
public class BizWeShopMarkController extends BaseController {

	@Autowired
	private BizWeShopMarkService bizWeShopMarkService;
	
	@ModelAttribute
	public BizWeShopMark get(@RequestParam(required=false) Integer id) {
		BizWeShopMark entity = null;
		if (id!=null){
			entity = bizWeShopMarkService.get(id);
		}
		if (entity == null){
			entity = new BizWeShopMark();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shop:bizWeShopMark:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizWeShopMark bizWeShopMark, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizWeShopMark> page = bizWeShopMarkService.findPage(new Page<BizWeShopMark>(request, response), bizWeShopMark); 
		model.addAttribute("page", page);
		return "modules/biz/shop/bizWeShopMarkList";
	}

	@RequiresPermissions("biz:shop:bizWeShopMark:view")
	@RequestMapping(value = "form")
	public String form(BizWeShopMark bizWeShopMark, Model model) {
		model.addAttribute("bizWeShopMark", bizWeShopMark);
		return "modules/biz/shop/bizWeShopMarkForm";
	}

	@RequiresPermissions("biz:shop:bizWeShopMark:edit")
	@RequestMapping(value = "save")
	public String save(BizWeShopMark bizWeShopMark, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizWeShopMark)){
			return form(bizWeShopMark, model);
		}
		bizWeShopMarkService.save(bizWeShopMark);
		addMessage(redirectAttributes, "保存收藏微店成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shop/bizWeShopMark/?repage";
	}
	
	@RequiresPermissions("biz:shop:bizWeShopMark:edit")
	@RequestMapping(value = "delete")
	public String delete(BizWeShopMark bizWeShopMark, RedirectAttributes redirectAttributes) {
		bizWeShopMarkService.delete(bizWeShopMark);
		addMessage(redirectAttributes, "删除收藏微店成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shop/bizWeShopMark/?repage";
	}

}