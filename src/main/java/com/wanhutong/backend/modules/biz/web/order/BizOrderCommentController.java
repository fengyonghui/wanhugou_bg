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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderComment;
import com.wanhutong.backend.modules.biz.service.order.BizOrderCommentService;

/**
 * 订单备注表Controller
 * @author oy
 * @version 2018-06-12
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderComment")
public class BizOrderCommentController extends BaseController {

	@Autowired
	private BizOrderCommentService bizOrderCommentService;
	
	@ModelAttribute
	public BizOrderComment get(@RequestParam(required=false) Integer id) {
		BizOrderComment entity = null;
		if (id!=null){
			entity = bizOrderCommentService.get(id);
		}
		if (entity == null){
			entity = new BizOrderComment();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderComment:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderComment bizOrderComment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderComment> page = bizOrderCommentService.findPage(new Page<BizOrderComment>(request, response), bizOrderComment); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderCommentList";
	}

	@RequiresPermissions("biz:order:bizOrderComment:view")
	@RequestMapping(value = "form")
	public String form(BizOrderComment bizOrderComment, Model model) {
		model.addAttribute("bizOrderComment", bizOrderComment);
		return "modules/biz/order/bizOrderCommentForm";
	}

	@RequiresPermissions("biz:order:bizOrderComment:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderComment bizOrderComment, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderComment)){
			return form(bizOrderComment, model);
		}
		bizOrderCommentService.save(bizOrderComment);
		addMessage(redirectAttributes, "保存订单备注成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderComment/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderComment:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderComment bizOrderComment, RedirectAttributes redirectAttributes) {
		bizOrderCommentService.delete(bizOrderComment);
		addMessage(redirectAttributes, "删除订单备注成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderComment/?repage";
	}

}