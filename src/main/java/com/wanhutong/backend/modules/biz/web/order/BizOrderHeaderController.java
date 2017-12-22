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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Controller
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeader")
public class BizOrderHeaderController extends BaseController {

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	
	@ModelAttribute
	public BizOrderHeader get(@RequestParam(required=false) Integer id) {
		BizOrderHeader entity = null;
		if (id!=null){
			entity = bizOrderHeaderService.get(id);
		}
		if (entity == null){
			entity = new BizOrderHeader();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderHeaderList";
	}

	@RequiresPermissions("biz:order:bizOrderHeader:view")
	@RequestMapping(value = "form")
	public String form(BizOrderHeader bizOrderHeader, Model model) {
		model.addAttribute("entity", bizOrderHeader);
		return "modules/biz/order/bizOrderHeaderForm";
	}

	@RequiresPermissions("biz:order:bizOrderHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderHeader)){
			return form(bizOrderHeader, model);
		}
		bizOrderHeaderService.save(bizOrderHeader);
		addMessage(redirectAttributes, "保存订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderHeader bizOrderHeader, RedirectAttributes redirectAttributes) {
		bizOrderHeaderService.delete(bizOrderHeader);
		addMessage(redirectAttributes, "删除订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/?repage";
	}

}