/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.plan;

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
import com.wanhutong.backend.modules.biz.entity.plan.BizOpPlan;
import com.wanhutong.backend.modules.biz.service.plan.BizOpPlanService;

/**
 * 运营计划Controller
 * @author 张腾飞
 * @version 2018-03-15
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/plan/bizOpPlan")
public class BizOpPlanController extends BaseController {

	@Autowired
	private BizOpPlanService bizOpPlanService;
	
	@ModelAttribute
	public BizOpPlan get(@RequestParam(required=false) Integer id) {
		BizOpPlan entity = null;
		if (id!=null){
			entity = bizOpPlanService.get(id);
		}
		if (entity == null){
			entity = new BizOpPlan();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:plan:bizOpPlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOpPlan bizOpPlan, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpPlan> page = bizOpPlanService.findPage(new Page<BizOpPlan>(request, response), bizOpPlan);
		model.addAttribute("page", page);
		return "modules/biz/plan/bizOpPlanList";
	}

	@RequiresPermissions("biz:plan:bizOpPlan:view")
	@RequestMapping(value = "form")
	public String form(BizOpPlan bizOpPlan, Model model) {
		model.addAttribute("bizOpPlan", bizOpPlan);
		return "modules/biz/plan/bizOpPlanForm";
	}

	@RequiresPermissions("biz:plan:bizOpPlan:edit")
	@RequestMapping(value = "save")
	public String save(BizOpPlan bizOpPlan, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOpPlan)){
			return form(bizOpPlan, model);
		}
		bizOpPlanService.save(bizOpPlan);
		addMessage(redirectAttributes, "保存运营计划成功");
		return "redirect:"+Global.getAdminPath()+"/biz/plan/bizOpPlan/?repage";
	}
	
	@RequiresPermissions("biz:plan:bizOpPlan:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOpPlan bizOpPlan, RedirectAttributes redirectAttributes) {
		bizOpPlanService.delete(bizOpPlan);
		addMessage(redirectAttributes, "删除运营计划成功");
		return "redirect:"+Global.getAdminPath()+"/biz/plan/bizOpPlan/?repage";
	}

}