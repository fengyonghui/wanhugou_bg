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
import com.wanhutong.backend.modules.biz.entity.order.BizServiceCharge;
import com.wanhutong.backend.modules.biz.service.order.BizServiceChargeService;

/**
 * 服务费--配送方式Controller
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizServiceCharge")
public class BizServiceChargeController extends BaseController {

	@Autowired
	private BizServiceChargeService bizServiceChargeService;
	
	@ModelAttribute
	public BizServiceCharge get(@RequestParam(required=false) Integer id) {
		BizServiceCharge entity = null;
		if (id!=null){
			entity = bizServiceChargeService.get(id);
		}
		if (entity == null){
			entity = new BizServiceCharge();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizServiceCharge:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizServiceCharge bizServiceCharge, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizServiceCharge> page = bizServiceChargeService.findPage(new Page<BizServiceCharge>(request, response), bizServiceCharge); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizServiceChargeList";
	}

	@RequiresPermissions("biz:order:bizServiceCharge:view")
	@RequestMapping(value = "form")
	public String form(BizServiceCharge bizServiceCharge, Model model) {
		model.addAttribute("bizServiceCharge", bizServiceCharge);
		return "modules/biz/order/bizServiceChargeForm";
	}

	@RequiresPermissions("biz:order:bizServiceCharge:edit")
	@RequestMapping(value = "save")
	public String save(BizServiceCharge bizServiceCharge, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizServiceCharge)){
			return form(bizServiceCharge, model);
		}
		bizServiceChargeService.save(bizServiceCharge);
		addMessage(redirectAttributes, "保存服务费--配送方式成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizServiceCharge/?repage";
	}
	
	@RequiresPermissions("biz:order:bizServiceCharge:edit")
	@RequestMapping(value = "delete")
	public String delete(BizServiceCharge bizServiceCharge, RedirectAttributes redirectAttributes) {
		bizServiceChargeService.delete(bizServiceCharge);
		addMessage(redirectAttributes, "删除服务费--配送方式成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizServiceCharge/?repage";
	}

}