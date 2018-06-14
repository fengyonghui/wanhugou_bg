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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAppointedTime;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAppointedTimeService;

/**
 * 代采订单约定付款时间表Controller
 * @author ZhangTengfei
 * @version 2018-05-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderAppointedTime")
public class BizOrderAppointedTimeController extends BaseController {

	@Autowired
	private BizOrderAppointedTimeService bizOrderAppointedTimeService;
	
	@ModelAttribute
	public BizOrderAppointedTime get(@RequestParam(required=false) Integer id) {
		BizOrderAppointedTime entity = null;
		if (id!=null){
			entity = bizOrderAppointedTimeService.get(id);
		}
		if (entity == null){
			entity = new BizOrderAppointedTime();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderAppointedTime:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderAppointedTime bizOrderAppointedTime, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderAppointedTime> page = bizOrderAppointedTimeService.findPage(new Page<BizOrderAppointedTime>(request, response), bizOrderAppointedTime); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderAppointedTimeList";
	}

	@RequiresPermissions("biz:order:bizOrderAppointedTime:view")
	@RequestMapping(value = "form")
	public String form(BizOrderAppointedTime bizOrderAppointedTime, Model model) {
		model.addAttribute("bizOrderAppointedTime", bizOrderAppointedTime);
		return "modules/biz/order/bizOrderAppointedTimeForm";
	}

	@RequiresPermissions("biz:order:bizOrderAppointedTime:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderAppointedTime bizOrderAppointedTime, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderAppointedTime)){
			return form(bizOrderAppointedTime, model);
		}
		bizOrderAppointedTimeService.save(bizOrderAppointedTime);
		addMessage(redirectAttributes, "保存代采订单约定付款时间表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderAppointedTime/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderAppointedTime:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderAppointedTime bizOrderAppointedTime, RedirectAttributes redirectAttributes) {
		bizOrderAppointedTimeService.delete(bizOrderAppointedTime);
		addMessage(redirectAttributes, "删除代采订单约定付款时间表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderAppointedTime/?repage";
	}

}