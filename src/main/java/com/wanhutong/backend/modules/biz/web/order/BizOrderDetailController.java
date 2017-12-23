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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;

/**
 * 订单详情(销售订单)Controller
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderDetail")
public class BizOrderDetailController extends BaseController {

	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	
	@ModelAttribute
	public BizOrderDetail get(@RequestParam(required=false) Integer id) {
		BizOrderDetail entity = null;
		if (id!=null){
			entity = bizOrderDetailService.get(id);
		}
		if (entity == null){
			entity = new BizOrderDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderDetail bizOrderDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderDetail> page = bizOrderDetailService.findPage(new Page<BizOrderDetail>(request, response), bizOrderDetail); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderDetailList";
	}

	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = "form")
	public String form(BizOrderDetail bizOrderDetail, Model model) {
		model.addAttribute("bizOrderDetail", bizOrderDetail);
		return "modules/biz/order/bizOrderDetailForm";
	}

	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderDetail bizOrderDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderDetail)){
			return form(bizOrderDetail, model);
		}
		bizOrderDetailService.save(bizOrderDetail);
		addMessage(redirectAttributes, "保存订单详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderDetail/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderDetail bizOrderDetail, RedirectAttributes redirectAttributes) {
		bizOrderDetailService.delete(bizOrderDetail);
		addMessage(redirectAttributes, "删除订单详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderDetail/?repage";
	}

}