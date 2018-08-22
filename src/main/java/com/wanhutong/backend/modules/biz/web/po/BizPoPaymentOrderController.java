/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.service.po.BizPoPaymentOrderService;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	@Autowired
	private BizPoHeaderService bizPoHeaderService;
	@Autowired
	private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	
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
		User user = UserUtils.getUser();
		List<Role> roleList = user.getRoleList();
		Set<String> roleSet = Sets.newHashSet();
		for (Role r : roleList) {
			RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
			if (parse != null) {
				roleSet.add(parse.name());
			}
		}
		model.addAttribute("roleSet", roleSet);
		if (bizPoPaymentOrder.getOrderType() != null && PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
			BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(poId);
			model.addAttribute("bizRequestHeader",bizRequestHeader);
		} else if (bizPoPaymentOrder.getOrderType() != null && PoPayMentOrderTypeEnum.ORDER_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
			bizOrderHeaderService.get(poId);
		} else {
			BizPoHeader bizPoHeader = bizPoHeaderService.get(poId);
			model.addAttribute("bizPoHeader", bizPoHeader);
		}
		if (poId == null) {
			bizPoPaymentOrder.setPoHeaderId(-1);
		} else {
			bizPoPaymentOrder.setPoHeaderId(poId);
		}
		Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
		model.addAttribute("page", page);
		String fromPage = request.getParameter("fromPage");
		String orderId = request.getParameter("orderId");
		model.addAttribute("fromPage", fromPage);
		model.addAttribute("orderId", orderId);
		return "modules/biz/po/bizPoPaymentOrderList";
	}

	@RequiresPermissions("biz:po:bizPoPaymentOrder:view")
	@RequestMapping(value = {"listData4Mobile"})
	@ResponseBody
	public String listData4Mobile(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, int poId) {
		bizPoPaymentOrder.setPoHeaderId(poId);
		BizPoHeader bizPoHeader = bizPoHeaderService.get(poId);
		Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("bizPoHeader", bizPoHeader);
		resultMap.put("page", page);
		return JsonUtil.generateData(resultMap, request.getParameter("callback"));
	}

	@RequiresPermissions("biz:po:bizPoPaymentOrder:view")
	@RequestMapping(value = "form")
	public String form(BizPoPaymentOrder bizPoPaymentOrder, Model model) {
		if (bizPoPaymentOrder.getPoHeaderId() != null) {
			bizPoHeaderService.get(bizPoPaymentOrder.getId());
			BizRequestHeader bizRequestHeader = new BizRequestHeader();
			bizRequestHeader.setBizPoHeader(new BizPoHeader(bizPoPaymentOrder.getPoHeaderId()));
			List<BizRequestHeader> requestHeaderList = bizRequestHeaderForVendorService.findList(bizRequestHeader);
			BizRequestHeader requestHeader = requestHeaderList.get(0);
			model.addAttribute("requestHeader",requestHeader);
		}
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
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoPaymentOrder/?repage&poId=" + bizPoPaymentOrder.getPoHeaderId() + "&orderType=" + bizPoPaymentOrder.getOrderType();
	}
	
	@RequiresPermissions("biz:po:bizPoPaymentOrder:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPoPaymentOrder bizPoPaymentOrder, RedirectAttributes redirectAttributes) {
		bizPoPaymentOrderService.delete(bizPoPaymentOrder);
		addMessage(redirectAttributes, "删除采购付款单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoPaymentOrder/?repage";
	}

}