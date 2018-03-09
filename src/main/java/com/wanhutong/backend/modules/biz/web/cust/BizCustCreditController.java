/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.cust;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
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
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;

/**
 * 用户钱包Controller
 * @author Ouyang
 * @version 2018-03-09
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/cust/bizCustCredit")
public class BizCustCreditController extends BaseController {

	@Autowired
	private BizCustCreditService bizCustCreditService;


	@ModelAttribute
	public BizCustCredit get(@RequestParam(required=false) Integer id) {
		BizCustCredit entity = null;
		if (id!=null){
			entity = bizCustCreditService.get(id);
		}
		if (entity == null){
			entity = new BizCustCredit();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:cust:bizCustCredit:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCustCredit bizCustCredit, HttpServletRequest request, HttpServletResponse response, Model model) {
		bizCustCredit.setCgsType(OfficeTypeEnum.CUSTOMER.getType());//采购商电话查询类型，采购商
		Page<BizCustCredit> page = bizCustCreditService.findPage(new Page<BizCustCredit>(request, response), bizCustCredit);
		model.addAttribute("page", page);
		return "modules/biz/cust/bizCustCreditList";
	}

	@RequiresPermissions("biz:cust:bizCustCredit:view")
	@RequestMapping(value = "form")
	public String form(BizCustCredit bizCustCredit, Model model) {
		model.addAttribute("bizCustCredit", bizCustCredit);
		return "modules/biz/cust/bizCustCreditForm";
	}

	@RequiresPermissions("biz:cust:bizCustCredit:edit")
	@RequestMapping(value = "save")
	public String save(BizCustCredit bizCustCredit, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCustCredit)){
			return form(bizCustCredit, model);
		}
		bizCustCreditService.save(bizCustCredit);
		addMessage(redirectAttributes, "保存钱包成功");
		return "redirect:"+Global.getAdminPath()+"/biz/cust/bizCustCredit/?repage";
	}
	
	@RequiresPermissions("biz:cust:bizCustCredit:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCustCredit bizCustCredit, RedirectAttributes redirectAttributes) {
		bizCustCreditService.delete(bizCustCredit);
		addMessage(redirectAttributes, "删除钱包成功");
		return "redirect:"+Global.getAdminPath()+"/biz/cust/bizCustCredit/?repage";
	}

}