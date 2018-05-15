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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;

/**
 * 订单状态修改日志Controller
 * @author Oy
 * @version 2018-05-15
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderStatus")
public class BizOrderStatusController extends BaseController {

	@Autowired
	private BizOrderStatusService bizOrderStatusService;
	
	@ModelAttribute
	public BizOrderStatus get(@RequestParam(required=false) Integer id) {
		BizOrderStatus entity = null;
		if (id!=null){
			entity = bizOrderStatusService.get(id);
		}
		if (entity == null){
			entity = new BizOrderStatus();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderStatus:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderStatus bizOrderStatus, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderStatus> page = bizOrderStatusService.findPage(new Page<BizOrderStatus>(request, response), bizOrderStatus); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderStatusList";
	}

	@RequiresPermissions("biz:order:bizOrderStatus:view")
	@RequestMapping(value = "form")
	public String form(BizOrderStatus bizOrderStatus, Model model) {
		model.addAttribute("bizOrderStatus", bizOrderStatus);
		return "modules/biz/order/bizOrderStatusForm";
	}

	@RequiresPermissions("biz:order:bizOrderStatus:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderStatus bizOrderStatus, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderStatus)){
			return form(bizOrderStatus, model);
		}
		bizOrderStatusService.save(bizOrderStatus);
		addMessage(redirectAttributes, "保存订单状态成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderStatus/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderStatus:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderStatus bizOrderStatus, RedirectAttributes redirectAttributes) {
		bizOrderStatusService.delete(bizOrderStatus);
		addMessage(redirectAttributes, "删除订单状态成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderStatus/?repage";
	}

}