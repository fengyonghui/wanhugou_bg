/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustomerInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomerInfoService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
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
import com.wanhutong.backend.modules.biz.entity.order.BizCommission;
import com.wanhutong.backend.modules.biz.service.order.BizCommissionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 佣金付款表Controller
 * @author wangby
 * @version 2018-10-18
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizCommission")
public class BizCommissionController extends BaseController {

	@Autowired
	private BizCommissionService bizCommissionService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@Autowired
	private BizCustomerInfoService bizCustomerInfoService;
	
	@ModelAttribute
	public BizCommission get(@RequestParam(required=false) Integer id) {
		BizCommission entity = null;
		if (id!=null){
			entity = bizCommissionService.get(id);
		}
		if (entity == null){
			entity = new BizCommission();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizCommission:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCommission bizCommission, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCommission> page = bizCommissionService.findPage(new Page<BizCommission>(request, response), bizCommission); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizCommissionList";
	}

	@RequiresPermissions("biz:order:bizCommission:view")
	@RequestMapping(value = "form")
	public String form(BizCommission bizCommission, Model model) {
		model.addAttribute("entity", bizCommission);
		return "modules/biz/order/bizCommissionForm";
	}

	@RequiresPermissions("biz:order:bizCommission:view")
	@RequestMapping(value = "applyCommissionForm")
	public String applyCommissionForm(BizCommission bizCommission, Model model) {
		String orderIds = bizCommission.getOrderIds();
		List<Integer> orderIdList = new ArrayList<Integer>();
		if (orderIds != null && orderIds.length() > 0 && !orderIds.contains(",")) {
			Integer orderId = Integer.valueOf(orderIds);
			orderIdList.add(orderId);
		} else if (orderIds != null && orderIds.length() > 0 && orderIds.contains(",")) {
			String[] orderIdArr = orderIds.split(",");
			for (int i=0; i<(orderIdArr.length); i++) {
				orderIdList.add(Integer.valueOf(orderIdArr[i]));
			}
		}

		List<BizOrderHeader> orderHeaderList = new ArrayList<BizOrderHeader>();
		if (CollectionUtils.isNotEmpty(orderIdList)) {
			for (Integer id : orderIdList) {
				BizOrderHeader entity = bizOrderHeaderService.get(id);

				BizOrderDetail bizOrderDetail = new BizOrderDetail();
				bizOrderDetail.setOrderHeader(entity);
				List<BizOrderDetail> bizOrderDetails = bizOrderDetailService.findList(bizOrderDetail);
				List<BizOrderDetail> bizOrderDetailsNew = new ArrayList<BizOrderDetail>();
				BigDecimal detailCommission = BigDecimal.ZERO;
				if (CollectionUtils.isNotEmpty(bizOrderDetails)) {
					for (BizOrderDetail orderDetail : bizOrderDetails) {
						BizSkuInfo bizSkuInfo = orderDetail.getSkuInfo();
						BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
						bizOpShelfSku.setSkuInfo(bizSkuInfo);
						List<BizOpShelfSku> opShelfSkuList = bizOpShelfSkuService.findList(bizOpShelfSku);
						if (CollectionUtils.isNotEmpty(opShelfSkuList)) {
							bizOpShelfSku = opShelfSkuList.get(0);
							BigDecimal orgPrice = new BigDecimal(bizOpShelfSku.getOrgPrice()).setScale(0, BigDecimal.ROUND_HALF_UP);
							BigDecimal salePrice = new BigDecimal(bizOpShelfSku.getSalePrice()).setScale(0, BigDecimal.ROUND_HALF_UP);
							Integer ordQty = orderDetail.getOrdQty();
							BigDecimal commissionRatio = bizOpShelfSku.getCommissionRatio();
							if (commissionRatio == null || commissionRatio.compareTo(BigDecimal.ZERO) <= 0) {
								commissionRatio = BigDecimal.ZERO;
							}
							detailCommission = (salePrice.subtract(orgPrice)).multiply(BigDecimal.valueOf(ordQty)).multiply(commissionRatio).divide(BigDecimal.valueOf(100));
							orderDetail.setSalePrice(salePrice);
							orderDetail.setDetailCommission(detailCommission);
						}
						bizOrderDetailsNew.add(orderDetail);
					}
				}
				entity.setOrderDetailList(bizOrderDetailsNew);
				orderHeaderList.add(entity);
			}
		}

		model.addAttribute("orderHeaderList", orderHeaderList);
		BizCustomerInfo bizCustomerInfo = bizCustomerInfoService.getByOfficeId(bizCommission.getSellerId());
		bizCommission.setCustomerInfo(bizCustomerInfo);
		model.addAttribute("entity", bizCommission);
		return "modules/biz/order/bizCommissionOrderHeaderForm";
	}

	@RequiresPermissions("biz:order:bizCommission:edit")
	@RequestMapping(value = "save")
	public String save(BizCommission bizCommission, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCommission)){
			return form(bizCommission, model);
		}
		bizCommissionService.save(bizCommission);
		addMessage(redirectAttributes, "保存佣金付款表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizCommission/?repage";
	}
	
	@RequiresPermissions("biz:order:bizCommission:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCommission bizCommission, RedirectAttributes redirectAttributes) {
		bizCommissionService.delete(bizCommission);
		addMessage(redirectAttributes, "删除佣金付款表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizCommission/?repage";
	}

	@RequiresPermissions("biz:order:bizCommission:audit")
	@RequestMapping(value = "auditPay")
	@ResponseBody
	public String auditPay(HttpServletRequest request, Integer commId, String currentType, int auditType, String description, BigDecimal money) {
		Pair<Boolean, String> result = bizCommissionService.auditPay(commId, currentType, auditType, description, money);
		if (result.getLeft()) {
			return JsonUtil.generateData(result, request.getParameter("callback"));
		}
		return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
	}

	@RequiresPermissions("biz:po:bizPoHeader:audit")
	@RequestMapping(value = "payOrder")
	@ResponseBody
	public String payOrder(Integer commId, String img, String remark) {
		return bizCommissionService.payOrder(commId, img, remark);
	}

}