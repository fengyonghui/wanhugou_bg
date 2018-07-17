/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

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
import com.wanhutong.backend.modules.biz.entity.po.BizSchedulingPlan;
import com.wanhutong.backend.modules.biz.service.po.BizSchedulingPlanService;

/**
 * 排产计划Controller
 * @author 王冰洋
 * @version 2018-07-17
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizSchedulingPlan")
public class BizSchedulingPlanController extends BaseController {

	@Autowired
	private BizSchedulingPlanService bizSchedulingPlanService;
	
	@ModelAttribute
	public BizSchedulingPlan get(@RequestParam(required=false) Integer id) {
		BizSchedulingPlan entity = null;
		if (id!=null){
			entity = bizSchedulingPlanService.get(id);
		}
		if (entity == null){
			entity = new BizSchedulingPlan();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:po:bizSchedulingPlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSchedulingPlan bizSchedulingPlan, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSchedulingPlan> page = bizSchedulingPlanService.findPage(new Page<BizSchedulingPlan>(request, response), bizSchedulingPlan); 
		model.addAttribute("page", page);
		return "modules/biz/po/bizSchedulingPlanList";
	}

	@RequiresPermissions("biz:po:bizSchedulingPlan:view")
	@RequestMapping(value = "form")
	public String form(BizSchedulingPlan bizSchedulingPlan, Model model) {
		model.addAttribute("bizSchedulingPlan", bizSchedulingPlan);
		return "modules/biz/po/bizSchedulingPlanForm";
	}

	@RequiresPermissions("biz:po:bizSchedulingPlan:edit")
	@RequestMapping(value = "save")
	public String save(BizSchedulingPlan bizSchedulingPlan, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSchedulingPlan)){
			return form(bizSchedulingPlan, model);
		}
		bizSchedulingPlanService.save(bizSchedulingPlan);
		addMessage(redirectAttributes, "保存排产计划成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizSchedulingPlan/?repage";
	}
	
	@RequiresPermissions("biz:po:bizSchedulingPlan:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSchedulingPlan bizSchedulingPlan, RedirectAttributes redirectAttributes) {
		bizSchedulingPlanService.delete(bizSchedulingPlan);
		addMessage(redirectAttributes, "删除排产计划成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizSchedulingPlan/?repage";
	}

}