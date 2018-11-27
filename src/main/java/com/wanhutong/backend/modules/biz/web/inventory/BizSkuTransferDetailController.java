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
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import com.wanhutong.backend.modules.biz.service.inventory.BizSkuTransferDetailService;

/**
 * 库存调拨详情Controller
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizSkuTransferDetail")
public class BizSkuTransferDetailController extends BaseController {

	@Autowired
	private BizSkuTransferDetailService bizSkuTransferDetailService;
	
	@ModelAttribute
	public BizSkuTransferDetail get(@RequestParam(required=false) Integer id) {
		BizSkuTransferDetail entity = null;
		if (id!=null){
			entity = bizSkuTransferDetailService.get(id);
		}
		if (entity == null){
			entity = new BizSkuTransferDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizSkuTransferDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuTransferDetail bizSkuTransferDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuTransferDetail> page = bizSkuTransferDetailService.findPage(new Page<BizSkuTransferDetail>(request, response), bizSkuTransferDetail); 
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizSkuTransferDetailList";
	}

	@RequiresPermissions("biz:inventory:bizSkuTransferDetail:view")
	@RequestMapping(value = "form")
	public String form(BizSkuTransferDetail bizSkuTransferDetail, Model model) {
		model.addAttribute("bizSkuTransferDetail", bizSkuTransferDetail);
		return "modules/biz/inventory/bizSkuTransferDetailForm";
	}

	@RequiresPermissions("biz:inventory:bizSkuTransferDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuTransferDetail bizSkuTransferDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuTransferDetail)){
			return form(bizSkuTransferDetail, model);
		}
		bizSkuTransferDetailService.save(bizSkuTransferDetail);
		addMessage(redirectAttributes, "保存库存调拨详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSkuTransferDetail/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizSkuTransferDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuTransferDetail bizSkuTransferDetail, RedirectAttributes redirectAttributes) {
		bizSkuTransferDetailService.delete(bizSkuTransferDetail);
		addMessage(redirectAttributes, "删除库存调拨详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSkuTransferDetail/?repage";
	}

}