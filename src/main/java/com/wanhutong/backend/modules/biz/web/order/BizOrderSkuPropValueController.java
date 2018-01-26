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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderSkuPropValue;
import com.wanhutong.backend.modules.biz.service.order.BizOrderSkuPropValueService;

/**
 * 订单详情商品属性Controller
 * @author OuyangXiutian
 * @version 2018-01-25
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderSkuPropValue")
public class BizOrderSkuPropValueController extends BaseController {

	@Autowired
	private BizOrderSkuPropValueService bizOrderSkuPropValueService;
	
	@ModelAttribute
	public BizOrderSkuPropValue get(@RequestParam(required=false) Integer id) {
		BizOrderSkuPropValue entity = null;
		if (id!=null){
			entity = bizOrderSkuPropValueService.get(id);
		}
		if (entity == null){
			entity = new BizOrderSkuPropValue();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderSkuPropValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderSkuPropValue bizOrderSkuPropValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderSkuPropValue> page = bizOrderSkuPropValueService.findPage(new Page<BizOrderSkuPropValue>(request, response), bizOrderSkuPropValue); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderSkuPropValueList";
	}

	@RequiresPermissions("biz:order:bizOrderSkuPropValue:view")
	@RequestMapping(value = "form")
	public String form(BizOrderSkuPropValue bizOrderSkuPropValue, Model model) {
		model.addAttribute("bizOrderSkuPropValue", bizOrderSkuPropValue);
		return "modules/biz/order/bizOrderSkuPropValueForm";
	}

	@RequiresPermissions("biz:order:bizOrderSkuPropValue:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderSkuPropValue bizOrderSkuPropValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderSkuPropValue)){
			return form(bizOrderSkuPropValue, model);
		}
		bizOrderSkuPropValueService.save(bizOrderSkuPropValue);
		addMessage(redirectAttributes, "保存属性成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderSkuPropValue/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderSkuPropValue:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderSkuPropValue bizOrderSkuPropValue, RedirectAttributes redirectAttributes) {
		bizOrderSkuPropValueService.delete(bizOrderSkuPropValue);
		addMessage(redirectAttributes, "删除属性成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderSkuPropValue/?repage";
	}

}