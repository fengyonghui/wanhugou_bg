/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

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
import com.wanhutong.backend.modules.biz.entity.order.BizCommission;
import com.wanhutong.backend.modules.biz.service.order.BizCommissionService;

/**
 * 佣金付款表Controller
 * @author wangby
 * @version 2018-10-18
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizCommission")
public class BizCommissionController extends BaseController {

	@Autowired
	private BizCommissionService bizCommissionService;
	
	@ModelAttribute
	public BizCommission get(@RequestParam(required=false) Integer id) {
		BizCommission entity = null;
		if (id!=null){
			entity = bizCommissionService.get(id);
		}
		if (entity == null){
			entity = new BizCommission();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizCommission:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCommission bizCommission, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCommission> page = bizCommissionService.findPage(new Page<BizCommission>(request, response), bizCommission); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizCommissionList";
	}

	@RequiresPermissions("biz:order:bizCommission:view")
	@RequestMapping(value = "form")
	public String form(BizCommission bizCommission, Model model) {
		model.addAttribute("bizCommission", bizCommission);
		return "modules/biz/order/bizCommissionForm";
	}

	@RequiresPermissions("biz:order:bizCommission:edit")
	@RequestMapping(value = "save")
	public String save(BizCommission bizCommission, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCommission)){
			return form(bizCommission, model);
		}
		bizCommissionService.save(bizCommission);
		addMessage(redirectAttributes, "保存佣金付款表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizCommission/?repage";
	}
	
	@RequiresPermissions("biz:order:bizCommission:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCommission bizCommission, RedirectAttributes redirectAttributes) {
		bizCommissionService.delete(bizCommission);
		addMessage(redirectAttributes, "删除佣金付款表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizCommission/?repage";
	}

}