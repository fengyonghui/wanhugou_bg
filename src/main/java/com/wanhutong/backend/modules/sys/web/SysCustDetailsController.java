/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.sys.entity.SysCustDetails;
import com.wanhutong.backend.modules.sys.service.SysCustDetailsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 采购商店铺Controller
 * @author zx
 * @version 2018-03-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysCustDetails")
public class SysCustDetailsController extends BaseController {

	@Autowired
	private SysCustDetailsService sysCustDetailsService;
	
	@ModelAttribute
	public SysCustDetails get(@RequestParam(required=false) Integer id) {
		SysCustDetails entity = null;
		if (id!=null){
			entity = sysCustDetailsService.get(id);
		}
		if (entity == null){
			entity = new SysCustDetails();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysCustDetails:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysCustDetails sysCustDetails, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysCustDetails> page = sysCustDetailsService.findPage(new Page<SysCustDetails>(request, response), sysCustDetails);
		model.addAttribute("page", page);
		return "modules/sys/sysCustDetailsList";
	}

	@RequiresPermissions("sys:sysCustDetails:view")
	@RequestMapping(value = "form")
	public String form(SysCustDetails sysCustDetails, Model model) {
		model.addAttribute("sysCustDetails", sysCustDetails);
		return "modules/sys/sysCustDetailsForm";
	}

	@RequiresPermissions("sys:sysCustDetails:edit")
	@RequestMapping(value = "save")
	public String save(SysCustDetails sysCustDetails, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysCustDetails)){
			return form(sysCustDetails, model);
		}
		sysCustDetailsService.save(sysCustDetails);
		addMessage(redirectAttributes, "保存采购商店铺成功");
		return "redirect:"+ Global.getAdminPath()+"/sys/sysCustDetails/?repage";
	}
	
	@RequiresPermissions("sys:sysCustDetails:edit")
	@RequestMapping(value = "delete")
	public String delete(SysCustDetails sysCustDetails, RedirectAttributes redirectAttributes) {
		sysCustDetailsService.delete(sysCustDetails);
		addMessage(redirectAttributes, "删除采购商店铺成功");
		return "redirect:"+ Global.getAdminPath()+"/sys/sysCustDetails/?repage";
	}

}