/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

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
import com.wanhutong.backend.modules.biz.entity.shelf.BizShelfUser;
import com.wanhutong.backend.modules.biz.service.shelf.BizShelfUserService;

/**
 * 货架用户中间表Controller
 * @author 张腾飞
 * @version 2018-01-11
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizShelfUser")
public class BizShelfUserController extends BaseController {

	@Autowired
	private BizShelfUserService bizShelfUserService;
	
	@ModelAttribute
	public BizShelfUser get(@RequestParam(required=false) Integer id) {
		BizShelfUser entity = null;
		if (id!=null){
			entity = bizShelfUserService.get(id);
		}
		if (entity == null){
			entity = new BizShelfUser();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizShelfUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizShelfUser bizShelfUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizShelfUser> page = bizShelfUserService.findPage(new Page<BizShelfUser>(request, response), bizShelfUser); 
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizShelfUserList";
	}

	@RequiresPermissions("biz:shelf:bizShelfUser:view")
	@RequestMapping(value = "form")
	public String form(BizShelfUser bizShelfUser, Model model) {
		model.addAttribute("bizShelfUser", bizShelfUser);
		return "modules/biz/shelf/bizShelfUserForm";
	}

	@RequiresPermissions("biz:shelf:bizShelfUser:edit")
	@RequestMapping(value = "save")
	public String save(BizShelfUser bizShelfUser, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizShelfUser)){
			return form(bizShelfUser, model);
		}
		bizShelfUserService.save(bizShelfUser);
		addMessage(redirectAttributes, "保存货架用户中间表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizShelfUser/?repage";
	}
	
	@RequiresPermissions("biz:shelf:bizShelfUser:edit")
	@RequestMapping(value = "delete")
	public String delete(BizShelfUser bizShelfUser, RedirectAttributes redirectAttributes) {
		bizShelfUserService.delete(bizShelfUser);
		addMessage(redirectAttributes, "删除货架用户中间表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizShelfUser/?repage";
	}

}