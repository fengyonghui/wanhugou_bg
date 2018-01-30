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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;

/**
 * 订单地址Controller
 * @author OuyangXiutian
 * @version 2018-01-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderAddress")
public class BizOrderAddressController extends BaseController {

	@Autowired
	private BizOrderAddressService bizOrderAddressService;
	
	@ModelAttribute
	public BizOrderAddress get(@RequestParam(required=false) Integer id) {
		BizOrderAddress entity = null;
		if (id!=null){
			entity = bizOrderAddressService.get(id);
		}
		if (entity == null){
			entity = new BizOrderAddress();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderAddress:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderAddress bizOrderAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderAddress> page = bizOrderAddressService.findPage(new Page<BizOrderAddress>(request, response), bizOrderAddress); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderAddressList";
	}

	@RequiresPermissions("biz:order:bizOrderAddress:view")
	@RequestMapping(value = "form")
	public String form(BizOrderAddress bizOrderAddress, Model model) {
		model.addAttribute("bizOrderAddress", bizOrderAddress);
		return "modules/biz/order/bizOrderAddressForm";
	}

	@RequiresPermissions("biz:order:bizOrderAddress:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderAddress bizOrderAddress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderAddress)){
			return form(bizOrderAddress, model);
		}
		bizOrderAddressService.save(bizOrderAddress);
		addMessage(redirectAttributes, "保存地址成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderAddress/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderAddress:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderAddress bizOrderAddress, RedirectAttributes redirectAttributes) {
		bizOrderAddressService.delete(bizOrderAddress);
		addMessage(redirectAttributes, "删除地址成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderAddress/?repage";
	}

	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderHeader:view")
	@RequestMapping(value = "orderForm")
	public BizOrderAddress orderForm(BizOrderAddress bizOrderAddress, Model model) {
		BizOrderAddress orderAddress = bizOrderAddressService.get(bizOrderAddress.getId());
		return orderAddress;
	}
}