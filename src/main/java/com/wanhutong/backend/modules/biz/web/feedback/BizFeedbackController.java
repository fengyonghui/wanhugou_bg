/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.feedback;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.feedback.BizFeedback;
import com.wanhutong.backend.modules.biz.service.feedback.BizFeedbackService;
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
 * 意见反馈Controller
 * @author zx
 * @version 2018-03-06
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/feedback/bizFeedback")
public class BizFeedbackController extends BaseController {

	@Autowired
	private BizFeedbackService bizFeedbackService;
	
	@ModelAttribute
	public BizFeedback get(@RequestParam(required=false) Integer id) {
		BizFeedback entity = null;
		if (id!=null){
			entity = bizFeedbackService.get(id);
		}
		if (entity == null){
			entity = new BizFeedback();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:feedback:bizFeedback:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizFeedback bizFeedback, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizFeedback> page = bizFeedbackService.findPage(new Page<BizFeedback>(request, response), bizFeedback);
		model.addAttribute("page", page);
		return "modules/biz/feedback/bizFeedbackList";
	}

	@RequiresPermissions("biz:feedback:bizFeedback:view")
	@RequestMapping(value = "form")
	public String form(BizFeedback bizFeedback, Model model) {
		model.addAttribute("bizFeedback", bizFeedback);
		return "modules/biz/feedback/bizFeedbackForm";
	}

	@RequiresPermissions("biz:feedback:bizFeedback:edit")
	@RequestMapping(value = "save")
	public String save(BizFeedback bizFeedback, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizFeedback)){
			return form(bizFeedback, model);
		}
		bizFeedbackService.save(bizFeedback);
		addMessage(redirectAttributes, "保存意见成功");
		return "redirect:"+ Global.getAdminPath()+"/biz/feedback/bizFeedback/?repage";
	}
	
	@RequiresPermissions("biz:feedback:bizFeedback:edit")
	@RequestMapping(value = "delete")
	public String delete(BizFeedback bizFeedback, RedirectAttributes redirectAttributes) {
		bizFeedbackService.delete(bizFeedback);
		addMessage(redirectAttributes, "删除意见成功");
		return "redirect:"+ Global.getAdminPath()+"/biz/feedback/bizFeedback/?repage";
	}

}