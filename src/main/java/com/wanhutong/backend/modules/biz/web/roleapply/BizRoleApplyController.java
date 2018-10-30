/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.roleapply;

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
import com.wanhutong.backend.modules.biz.entity.roleapply.BizRoleApply;
import com.wanhutong.backend.modules.biz.service.roleapply.BizRoleApplyService;

/**
 * 零售申请表Controller
 * @author wangby
 * @version 2018-10-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/roleapply/bizRoleApply")
public class BizRoleApplyController extends BaseController {

	@Autowired
	private BizRoleApplyService bizRoleApplyService;
	
	@ModelAttribute
	public BizRoleApply get(@RequestParam(required=false) Integer id) {
		BizRoleApply entity = null;
		if (id!=null){
			entity = bizRoleApplyService.get(id);
		}
		if (entity == null){
			entity = new BizRoleApply();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:roleapply:bizRoleApply:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizRoleApply bizRoleApply, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizRoleApply> page = bizRoleApplyService.findPage(new Page<BizRoleApply>(request, response), bizRoleApply); 
		model.addAttribute("page", page);
		return "modules/biz/roleapply/bizRoleApplyList";
	}

	@RequiresPermissions("biz:roleapply:bizRoleApply:view")
	@RequestMapping(value = "form")
	public String form(BizRoleApply bizRoleApply, Model model) {
		model.addAttribute("bizRoleApply", bizRoleApply);
		return "modules/biz/roleapply/bizRoleApplyForm";
	}

	@RequiresPermissions("biz:roleapply:bizRoleApply:edit")
	@RequestMapping(value = "save")
	public String save(BizRoleApply bizRoleApply, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizRoleApply)){
			return form(bizRoleApply, model);
		}
		bizRoleApplyService.save(bizRoleApply);
		addMessage(redirectAttributes, "保存零售申请表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/roleapply/bizRoleApply/?repage";
	}
	
	@RequiresPermissions("biz:roleapply:bizRoleApply:edit")
	@RequestMapping(value = "delete")
	public String delete(BizRoleApply bizRoleApply, RedirectAttributes redirectAttributes) {
		bizRoleApplyService.delete(bizRoleApply);
		addMessage(redirectAttributes, "删除零售申请表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/roleapply/bizRoleApply/?repage";
	}

}