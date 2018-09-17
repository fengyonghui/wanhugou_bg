/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.integration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
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
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import com.wanhutong.backend.modules.biz.service.integration.BizIntegrationActivityService;

import java.util.List;

/**
 * 积分活动Controller
 * @author LX
 * @version 2018-09-16
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/integration/bizIntegrationActivity")
public class BizIntegrationActivityController extends BaseController {

	@Autowired
	private BizIntegrationActivityService bizIntegrationActivityService;

	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public BizIntegrationActivity get(@RequestParam(required=false) Integer id) {
		BizIntegrationActivity entity = null;
		if (id!=null){
			entity = bizIntegrationActivityService.get(id);
		}
		if (entity == null){
			entity = new BizIntegrationActivity();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizIntegrationActivity bizIntegrationActivity, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizIntegrationActivity> page = bizIntegrationActivityService.findPage(new Page<BizIntegrationActivity>(request, response), bizIntegrationActivity); 
		model.addAttribute("page", page);
		return "modules/biz/integration/bizIntegrationActivityList";
	}


	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = "form")
	public String form(BizIntegrationActivity bizIntegrationActivity, Model model) {
		model.addAttribute("bizIntegrationActivity", bizIntegrationActivity);
		List<Office> officeList = officeService.filerOffice(null, null, OfficeTypeEnum.CUSTOMER);
		model.addAttribute("officeList",officeList);
		return "modules/biz/integration/bizIntegrationActivityForm";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = "formA")
	public String formZhu(BizIntegrationActivity bizIntegrationActivity, Model model) {
		model.addAttribute("bizIntegrationActivity", bizIntegrationActivity);
		return "modules/biz/integration/bizIntegrationActivityAForm";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:view")
	@RequestMapping(value = "formB")
	public String formZhi(BizIntegrationActivity bizIntegrationActivity, Model model) {
		model.addAttribute("bizIntegrationActivity", bizIntegrationActivity);
		return "modules/biz/integration/bizIntegrationActivityBForm";
	}

	@RequiresPermissions("biz:integration:bizIntegrationActivity:edit")
	@RequestMapping(value = "save")
	public String save(BizIntegrationActivity bizIntegrationActivity, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizIntegrationActivity)){
			return form(bizIntegrationActivity, model);
		}
		bizIntegrationActivityService.save(bizIntegrationActivity);
		addMessage(redirectAttributes, "保存积分活动成功");
		return "redirect:"+Global.getAdminPath()+"/biz/integration/bizIntegrationActivity/?repage";
	}
	
	@RequiresPermissions("biz:integration:bizIntegrationActivity:edit")
	@RequestMapping(value = "delete")
	public String delete(BizIntegrationActivity bizIntegrationActivity, RedirectAttributes redirectAttributes) {
		bizIntegrationActivityService.delete(bizIntegrationActivity);
		addMessage(redirectAttributes, "删除积分活动成功");
		return "redirect:"+Global.getAdminPath()+"/biz/integration/bizIntegrationActivity/?repage";
	}
	@ResponseBody
	@RequestMapping("systemActivity")
    public BizIntegrationActivity getIntegrationByCode(@RequestParam("code") String code){
		return bizIntegrationActivityService.getIntegrationByCode(code);
	}
}