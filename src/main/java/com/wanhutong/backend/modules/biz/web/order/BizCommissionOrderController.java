/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.modules.biz.entity.order.BizCommission;
import com.wanhutong.backend.modules.biz.service.order.BizCommissionService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	protected static final Logger LOGGER = LoggerFactory.getLogger(BizOrderHeaderController.class);

	
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
	public String saveCommission(BizCommission bizCommission, Model model, RedirectAttributes redirectAttributes) {

		String msg = bizCommissionService.createCommissionOrder(bizCommission).getRight();

		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/list?targetPage=COMMISSION_ORDER";
	}

	@RequiresPermissions("biz:order:bizCommissionOrder:edit")
	@RequestMapping(value = "saveCommission4Mobile")
	@ResponseBody
	public String saveCommission4Mobile(HttpServletRequest request, BizCommission bizCommission, Model model, RedirectAttributes redirectAttributes) {

		try {
			Pair<Boolean, String> result = bizCommissionService.createCommissionOrder(bizCommission);
			if (result.getLeft()) {
				return JsonUtil.generateData(result, request.getParameter("callback"));
			}

			return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), null);
		}catch (Exception e) {
			LOGGER.error("audit so error ", e);
		}
		return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "操作失败,发生异常,请联系技术部", null);
	}
	
	@RequiresPermissions("biz:order:bizCommissionOrder:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCommissionOrder bizCommissionOrder, RedirectAttributes redirectAttributes) {
		bizCommissionOrderService.delete(bizCommissionOrder);
		addMessage(redirectAttributes, "删除佣金付款订单关系表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizCommissionOrder/?repage";
	}

}