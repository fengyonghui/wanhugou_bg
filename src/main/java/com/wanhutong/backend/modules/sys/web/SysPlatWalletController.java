/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

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
import com.wanhutong.backend.modules.sys.entity.SysPlatWallet;
import com.wanhutong.backend.modules.sys.service.SysPlatWalletService;

/**
 * 平台总钱包Controller
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysPlatWallet")
public class SysPlatWalletController extends BaseController {

	@Autowired
	private SysPlatWalletService sysPlatWalletService;
	
	@ModelAttribute
	public SysPlatWallet get(@RequestParam(required=false) Integer id) {
		SysPlatWallet entity = null;
		if (id!=null){
			entity = sysPlatWalletService.get(id);
		}
		if (entity == null){
			entity = new SysPlatWallet();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysPlatWallet:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysPlatWallet sysPlatWallet, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysPlatWallet> page = sysPlatWalletService.findPage(new Page<SysPlatWallet>(request, response), sysPlatWallet); 
		model.addAttribute("page", page);
		return "modules/sys/sysPlatWalletList";
	}

	@RequiresPermissions("sys:sysPlatWallet:view")
	@RequestMapping(value = "form")
	public String form(SysPlatWallet sysPlatWallet, Model model) {
		model.addAttribute("sysPlatWallet", sysPlatWallet);
		return "modules/sys/sysPlatWalletForm";
	}

	@RequiresPermissions("sys:sysPlatWallet:edit")
	@RequestMapping(value = "save")
	public String save(SysPlatWallet sysPlatWallet, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysPlatWallet)){
			return form(sysPlatWallet, model);
		}
		sysPlatWalletService.save(sysPlatWallet);
		addMessage(redirectAttributes, "保存金额成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysPlatWallet/?repage";
	}
	
	@RequiresPermissions("sys:sysPlatWallet:edit")
	@RequestMapping(value = "delete")
	public String delete(SysPlatWallet sysPlatWallet, RedirectAttributes redirectAttributes) {
		sysPlatWalletService.delete(sysPlatWallet);
		addMessage(redirectAttributes, "删除金额成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysPlatWallet/?repage";
	}

}