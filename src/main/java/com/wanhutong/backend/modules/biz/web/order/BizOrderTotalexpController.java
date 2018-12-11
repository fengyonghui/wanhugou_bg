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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderTotalexp;
import com.wanhutong.backend.modules.biz.service.order.BizOrderTotalexpService;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 服务费记录表Controller
 * @author wangby
 * @version 2018-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderTotalexp")
public class BizOrderTotalexpController extends BaseController {

	@Autowired
	private BizOrderTotalexpService bizOrderTotalexpService;
	
	@ModelAttribute
	public BizOrderTotalexp get(@RequestParam(required=false) Integer id) {
		BizOrderTotalexp entity = null;
		if (id!=null){
			entity = bizOrderTotalexpService.get(id);
		}
		if (entity == null){
			entity = new BizOrderTotalexp();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderTotalexp:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderTotalexp bizOrderTotalexp, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderTotalexp> page = bizOrderTotalexpService.findPage(new Page<BizOrderTotalexp>(request, response), bizOrderTotalexp); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderTotalexpList";
	}

	@RequiresPermissions("biz:order:bizOrderTotalexp:view")
	@RequestMapping(value = "form")
	public String form(BizOrderTotalexp bizOrderTotalexp, Model model) {
		model.addAttribute("bizOrderTotalexp", bizOrderTotalexp);
		return "modules/biz/order/bizOrderTotalexpForm";
	}

	@RequiresPermissions("biz:order:bizOrderTotalexp:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderTotalexp bizOrderTotalexp, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderTotalexp)){
			return form(bizOrderTotalexp, model);
		}
		bizOrderTotalexpService.save(bizOrderTotalexp);
		addMessage(redirectAttributes, "保存服务费记录表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderTotalexp/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderTotalexp:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderTotalexp bizOrderTotalexp, RedirectAttributes redirectAttributes) {
		bizOrderTotalexpService.delete(bizOrderTotalexp);
		addMessage(redirectAttributes, "删除服务费记录表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderTotalexp/?repage";
	}

	@RequestMapping(value = "batchSave")
	@ResponseBody
	public String batchSave(String amountStr) {
		String[] amountArr = amountStr.split(",");
		List<String> amountList = Arrays.asList(amountArr);



		return "ok";
	}

}