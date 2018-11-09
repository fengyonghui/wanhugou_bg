/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV3Service;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransfer;
import com.wanhutong.backend.modules.biz.service.inventory.BizSkuTransferService;

import java.util.List;

/**
 * 库存调拨Controller
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizSkuTransfer")
public class BizSkuTransferController extends BaseController {

	@Autowired
	private BizSkuTransferService bizSkuTransferService;
	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizSkuInfoV3Service bizSkuInfoService;
	
	@ModelAttribute
	public BizSkuTransfer get(@RequestParam(required=false) Integer id) {
		BizSkuTransfer entity = null;
		if (id!=null){
			entity = bizSkuTransferService.get(id);
		}
		if (entity == null){
			entity = new BizSkuTransfer();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizSkuTransfer:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuTransfer bizSkuTransfer, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuTransfer> page = bizSkuTransferService.findPage(new Page<BizSkuTransfer>(request, response), bizSkuTransfer);
		model.addAttribute("fromInvList",bizInventoryInfoService.findAllList(new BizInventoryInfo()));
		model.addAttribute("toInvList",bizInventoryInfoService.findList(new BizInventoryInfo()));
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizSkuTransferList";
	}

	@RequiresPermissions("biz:inventory:bizSkuTransfer:view")
	@RequestMapping(value = "form")
	public String form(BizSkuTransfer bizSkuTransfer, Model model) {
		model.addAttribute("bizSkuTransfer", bizSkuTransfer);
		return "modules/biz/inventory/bizSkuTransferForm";
	}

	@RequiresPermissions("biz:inventory:bizSkuTransfer:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuTransfer bizSkuTransfer, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuTransfer)){
			return form(bizSkuTransfer, model);
		}
		bizSkuTransferService.save(bizSkuTransfer);
		addMessage(redirectAttributes, "保存库存调拨成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSkuTransfer/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizSkuTransfer:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuTransfer bizSkuTransfer, RedirectAttributes redirectAttributes) {
		bizSkuTransferService.delete(bizSkuTransfer);
		addMessage(redirectAttributes, "删除库存调拨成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSkuTransfer/?repage";
	}

	@ResponseBody
	@RequestMapping("findInvSkuList")
	public List<BizSkuInfo> findInvSkuList(BizSkuInfo skuInfo, Integer fromInv) {
		return bizSkuInfoService.findInvSkuList(skuInfo,fromInv);
	}

}