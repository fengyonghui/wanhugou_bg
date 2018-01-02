/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;

/**
 * 商品库存详情Controller
 * @author 张腾飞
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInventorySku")
public class BizInventorySkuController extends BaseController {

	@Autowired
	private BizInventorySkuService bizInventorySkuService;

	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;

	@Autowired
	private BizSkuInfoService bizSkuInfoService;

	@ModelAttribute
	public BizInventorySku get(@RequestParam(required=false) Integer id) {
		BizInventorySku entity = null;
		if (id!=null){
			entity = bizInventorySkuService.get(id);
		}
		if (entity == null){
			entity = new BizInventorySku();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizInventorySku:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInventorySku bizInventorySku, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInventorySku> page = bizInventorySkuService.findPage(new Page<BizInventorySku>(request, response), bizInventorySku);
		String zt = request.getParameter("zt");
		model.addAttribute("zt",zt);
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizInventorySkuList";
	}

	@RequiresPermissions("biz:inventory:bizInventorySku:view")
	@RequestMapping(value = "form")
	public String form(BizInventorySku bizInventorySku,HttpServletRequest request, Model model) {
//		bizInventorySku = bizInventorySkuService.get(bizInventorySku.getId());
		BizInventoryInfo bizInventoryInfo = bizInventoryInfoService.get(bizInventorySku.getInvInfo().getId());
		bizInventorySku.setInvInfo(bizInventoryInfo);
		String zt = request.getParameter("zt");
		model.addAttribute("zt",zt);
		model.addAttribute("entity", bizInventorySku);
		return "modules/biz/inventory/bizInventorySkuForm";
	}

	@RequiresPermissions("biz:inventory:bizInventorySku:edit")
	@RequestMapping(value = "save")
	public String save(BizInventorySku bizInventorySku, Model model,HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInventorySku)){
			return form(bizInventorySku,request,model);
		}
		bizInventorySkuService.save(bizInventorySku);
		String zt = request.getParameter("zt");
		addMessage(redirectAttributes, "保存商品库存详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventorySku/?repage&invInfo.id="
				+bizInventorySku.getInvInfo().getId()+"&zt="+zt;
	}
	
	@RequiresPermissions("biz:inventory:bizInventorySku:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInventorySku bizInventorySku, RedirectAttributes redirectAttributes) {
		bizInventorySkuService.delete(bizInventorySku);
		addMessage(redirectAttributes, "删除商品库存详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInventorySku/?repage";
	}

}