/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

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
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryOrderRequest;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryOrderRequestService;

/**
 * 订单备货单出库关系Controller
 * @author ZhangTengfei
 * @version 2018-09-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInventoryOrderRequest")
public class BizInventoryOrderRequestController extends BaseController {

	@Autowired
	private BizInventoryOrderRequestService bizInventoryOrderRequestService;
	
	@ModelAttribute
	public BizInventoryOrderRequest get(@RequestParam(required=false) Integer id) {
		BizInventoryOrderRequest entity = null;
		if (id!=null){
			entity = bizInventoryOrderRequestService.get(id);
		}
		if (entity == null){
			entity = new BizInventoryOrderRequest();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizInventoryOrderRequest:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInventoryOrderRequest bizInventoryOrderRequest, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInventoryOrderRequest> page = bizInventoryOrderRequestService.findPage(new Page<BizInventoryOrderRequest>(request, response), bizInventoryOrderRequest); 
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizInventoryOrderRequestList";
	}

	@RequiresPermissions("biz:inventory:bizInventoryOrderRequest:view")
	@RequestMapping(value = "form")
	public String form(BizInventoryOrderRequest bizInventoryOrderRequest, Model model) {
		model.addAttribute("bizInventoryOrderRequest", bizInventoryOrderRequest);
		return "modules/biz/inventory/bizInventoryOrderRequestForm";
	}

	@RequiresPermissions("biz:inventory:bizInventoryOrderRequest:edit")
	@RequestMapping(value = "save")
	public String save(BizInventoryOrderRequest bizInventoryOrderRequest, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInventoryOrderRequest)){
			return form(bizInventoryOrderRequest, model);
		}
		bizInventoryOrderRequestService.save(bizInventoryOrderRequest);
		addMessage(redirectAttributes, "保存订单备货单出库关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventoryOrderRequest/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizInventoryOrderRequest:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInventoryOrderRequest bizInventoryOrderRequest, RedirectAttributes redirectAttributes) {
		bizInventoryOrderRequestService.delete(bizInventoryOrderRequest);
		addMessage(redirectAttributes, "删除订单备货单出库关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventoryOrderRequest/?repage";
	}

}