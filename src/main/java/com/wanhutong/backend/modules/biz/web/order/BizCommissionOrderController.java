/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.order.BizCommission;
import com.wanhutong.backend.modules.biz.service.order.BizCommissionService;
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
import com.wanhutong.backend.modules.biz.entity.order.BizCommissionOrder;
import com.wanhutong.backend.modules.biz.service.order.BizCommissionOrderService;

/**
 * 佣金付款订单关系表Controller
 * @author wangby
 * @version 2018-10-18
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizCommissionOrder")
public class BizCommissionOrderController extends BaseController {

	@Autowired
	private BizCommissionOrderService bizCommissionOrderService;
	@Autowired
	private BizCommissionService bizCommissionService;

	
	@ModelAttribute
	public BizCommissionOrder get(@RequestParam(required=false) Integer id) {
		BizCommissionOrder entity = null;
		if (id!=null){
			entity = bizCommissionOrderService.get(id);
		}
		if (entity == null){
			entity = new BizCommissionOrder();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizCommissionOrder:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCommissionOrder bizCommissionOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCommissionOrder> page = bizCommissionOrderService.findPage(new Page<BizCommissionOrder>(request, response), bizCommissionOrder); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizCommissionOrderList";
	}

	@RequiresPermissions("biz:order:bizCommissionOrder:view")
	@RequestMapping(value = "form")
	public String form(BizCommissionOrder bizCommissionOrder, Model model) {
		model.addAttribute("bizCommissionOrder", bizCommissionOrder);
		return "modules/biz/order/bizCommissionOrderForm";
	}

	@RequiresPermissions("biz:order:bizCommissionOrder:edit")
	@RequestMapping(value = "save")
	public String save(BizCommissionOrder bizCommissionOrder, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCommissionOrder)){
			return form(bizCommissionOrder, model);
		}
		bizCommissionOrderService.save(bizCommissionOrder);
		addMessage(redirectAttributes, "保存佣金付款订单关系表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizCommissionOrder/?repage";
	}

	@RequiresPermissions("biz:order:bizCommissionOrder:edit")
	@RequestMapping(value = "saveCommission")
	public String saveCommission(BizCommission bizCommission, Model model, RedirectAttributes redirectAttributes, String orderId) {

		String msg = bizCommissionService.createCommissionOrder(bizCommission, orderId).getRight();

		return "";
	}
	
	@RequiresPermissions("biz:order:bizCommissionOrder:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCommissionOrder bizCommissionOrder, RedirectAttributes redirectAttributes) {
		bizCommissionOrderService.delete(bizCommissionOrder);
		addMessage(redirectAttributes, "删除佣金付款订单关系表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizCommissionOrder/?repage";
	}

}