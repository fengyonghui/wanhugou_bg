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
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopReceiverAddress;
import com.wanhutong.backend.modules.biz.service.shop.BizShopReceiverAddressService;

/**
 * 收货地址Controller
 * @author Oy
 * @version 2018-04-10
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shop/bizShopReceiverAddress")
public class BizShopReceiverAddressController extends BaseController {

	@Autowired
	private BizShopReceiverAddressService bizShopReceiverAddressService;
	
	@ModelAttribute
	public BizShopReceiverAddress get(@RequestParam(required=false) Integer id) {
		BizShopReceiverAddress entity = null;
		if (id!=null){
			entity = bizShopReceiverAddressService.get(id);
		}
		if (entity == null){
			entity = new BizShopReceiverAddress();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shop:bizShopReceiverAddress:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizShopReceiverAddress bizShopReceiverAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizShopReceiverAddress> page = bizShopReceiverAddressService.findPage(new Page<BizShopReceiverAddress>(request, response), bizShopReceiverAddress); 
		model.addAttribute("page", page);
		return "modules/biz/shop/bizShopReceiverAddressList";
	}

	@RequiresPermissions("biz:shop:bizShopReceiverAddress:view")
	@RequestMapping(value = "form")
	public String form(BizShopReceiverAddress bizShopReceiverAddress, Model model) {
		model.addAttribute("entity", bizShopReceiverAddress);
		return "modules/biz/shop/bizShopReceiverAddressForm";
	}

	@RequiresPermissions("biz:shop:bizShopReceiverAddress:edit")
	@RequestMapping(value = "save")
	public String save(BizShopReceiverAddress bizShopReceiverAddress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizShopReceiverAddress)){
			return form(bizShopReceiverAddress, model);
		}
		bizShopReceiverAddressService.save(bizShopReceiverAddress);
		addMessage(redirectAttributes, "保存收货地址成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shop/bizShopReceiverAddress/?repage";
	}
	
	@RequiresPermissions("biz:shop:bizShopReceiverAddress:edit")
	@RequestMapping(value = "delete")
	public String delete(BizShopReceiverAddress bizShopReceiverAddress, RedirectAttributes redirectAttributes) {
		bizShopReceiverAddressService.delete(bizShopReceiverAddress);
		addMessage(redirectAttributes, "删除收货地址成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shop/bizShopReceiverAddress/?repage";
	}

}