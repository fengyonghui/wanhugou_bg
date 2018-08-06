/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.message;

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
import com.wanhutong.backend.modules.biz.entity.message.BizMessageUser;
import com.wanhutong.backend.modules.biz.service.message.BizMessageUserService;

/**
 * 站内信关系Controller
 * @author Ma.Qiang
 * @version 2018-07-27
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/message/bizMessageUser")
public class BizMessageUserController extends BaseController {

	@Autowired
	private BizMessageUserService bizMessageUserService;
	
	@ModelAttribute
	public BizMessageUser get(@RequestParam(required=false) Integer id) {
		BizMessageUser entity = null;
		if (id!=null){
			entity = bizMessageUserService.get(id);
		}
		if (entity == null){
			entity = new BizMessageUser();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:message:bizMessageUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizMessageUser bizMessageUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizMessageUser> page = bizMessageUserService.findPage(new Page<BizMessageUser>(request, response), bizMessageUser); 
		model.addAttribute("page", page);
		return "modules/biz/message/bizMessageUserList";
	}

	@RequiresPermissions("biz:message:bizMessageUser:view")
	@RequestMapping(value = "form")
	public String form(BizMessageUser bizMessageUser, Model model) {
		model.addAttribute("bizMessageUser", bizMessageUser);
		return "modules/biz/message/bizMessageUserForm";
	}

	@RequiresPermissions("biz:message:bizMessageUser:edit")
	@RequestMapping(value = "save")
	public String save(BizMessageUser bizMessageUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizMessageUser)){
			return form(bizMessageUser, model);
		}
		bizMessageUserService.save(bizMessageUser);
		addMessage(redirectAttributes, "保存站内信关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/message/bizMessageUser/?repage";
	}
	
	@RequiresPermissions("biz:message:bizMessageUser:edit")
	@RequestMapping(value = "delete")
	public String delete(BizMessageUser bizMessageUser, RedirectAttributes redirectAttributes) {
		bizMessageUserService.delete(bizMessageUser);
		addMessage(redirectAttributes, "删除站内信关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/message/bizMessageUser/?repage";
	}

}