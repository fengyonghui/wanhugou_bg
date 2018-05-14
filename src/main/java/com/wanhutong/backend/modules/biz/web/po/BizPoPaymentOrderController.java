/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

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
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.service.po.BizPoPaymentOrderService;

/**
 * 采购付款单Controller
 * @author Ma.Qiang
 * @version 2018-05-04
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizPoPaymentOrder")
public class BizPoPaymentOrderController extends BaseController {

	@Autowired
	private BizPoPaymentOrderService bizPoPaymentOrderService;
	
	@ModelAttribute
	public BizPoPaymentOrder get(@RequestParam(required=false) Integer id) {
		BizPoPaymentOrder entity = null;
		if (id!=null){
			entity = bizPoPaymentOrderService.get(id);
		}
		if (entity == null){
			entity = new BizPoPaymentOrder();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:po:bizPoPaymentOrder:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, Model model, Integer poId) {
		bizPoPaymentOrder.setPoHeaderId(poId);
		Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder); 
		model.addAttribute("page", page);
		return "modules/biz/po/bizPoPaymentOrderList";
	}

	@RequiresPermissions("biz:po:bizPoPaymentOrder:view")
	@RequestMapping(value = "form")
	public String form(BizPoPaymentOrder bizPoPaymentOrder, Model model) {
		model.addAttribute("bizPoPaymentOrder", bizPoPaymentOrder);
		return "modules/biz/po/bizPoPaymentOrderForm";
	}

	@RequiresPermissions("biz:po:bizPoPaymentOrder:edit")
	@RequestMapping(value = "save")
	public String save(BizPoPaymentOrder bizPoPaymentOrder, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPoPaymentOrder)){
			return form(bizPoPaymentOrder, model);
		}
		bizPoPaymentOrderService.save(bizPoPaymentOrder);
		addMessage(redirectAttributes, "保存采购付款单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoPaymentOrder/?repage";
	}
	
	@RequiresPermissions("biz:po:bizPoPaymentOrder:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPoPaymentOrder bizPoPaymentOrder, RedirectAttributes redirectAttributes) {
		bizPoPaymentOrderService.delete(bizPoPaymentOrder);
		addMessage(redirectAttributes, "删除采购付款单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoPaymentOrder/?repage";
	}

}