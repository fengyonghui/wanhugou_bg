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
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.service.inventory.BizDetailInvoiceService;

/**
 * 发货单和订单详情关系Controller
 * @author 张腾飞
 * @version 2018-03-06
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizDetailInvoice")
public class BizDetailInvoiceController extends BaseController {

	@Autowired
	private BizDetailInvoiceService bizDetailInvoiceService;
	
	@ModelAttribute
	public BizDetailInvoice get(@RequestParam(required=false) Integer id) {
		BizDetailInvoice entity = null;
		if (id!=null){
			entity = bizDetailInvoiceService.get(id);
		}
		if (entity == null){
			entity = new BizDetailInvoice();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizDetailInvoice:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizDetailInvoice bizDetailInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizDetailInvoice> page = bizDetailInvoiceService.findPage(new Page<BizDetailInvoice>(request, response), bizDetailInvoice); 
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizDetailInvoiceList";
	}

	@RequiresPermissions("biz:inventory:bizDetailInvoice:view")
	@RequestMapping(value = "form")
	public String form(BizDetailInvoice bizDetailInvoice, Model model) {
		model.addAttribute("bizDetailInvoice", bizDetailInvoice);
		return "modules/biz/inventory/bizDetailInvoiceForm";
	}

	@RequiresPermissions("biz:inventory:bizDetailInvoice:edit")
	@RequestMapping(value = "save")
	public String save(BizDetailInvoice bizDetailInvoice, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizDetailInvoice)){
			return form(bizDetailInvoice, model);
		}
		bizDetailInvoiceService.save(bizDetailInvoice);
		addMessage(redirectAttributes, "保存发货单和订单详情关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizDetailInvoice/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizDetailInvoice:edit")
	@RequestMapping(value = "delete")
	public String delete(BizDetailInvoice bizDetailInvoice, RedirectAttributes redirectAttributes) {
		bizDetailInvoiceService.delete(bizDetailInvoice);
		addMessage(redirectAttributes, "删除发货单和订单详情关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizDetailInvoice/?repage";
	}

}