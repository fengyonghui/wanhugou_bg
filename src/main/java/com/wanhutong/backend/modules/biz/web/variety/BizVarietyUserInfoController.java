/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.variety;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
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
import com.wanhutong.backend.modules.biz.entity.variety.BizVarietyUserInfo;
import com.wanhutong.backend.modules.biz.service.variety.BizVarietyUserInfoService;

import java.util.List;

/**
 * 品类与用户 关联Controller
 * @author Oy
 * @version 2018-05-31
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/variety/bizVarietyUserInfo")
public class BizVarietyUserInfoController extends BaseController {

	@Autowired
	private BizVarietyUserInfoService bizVarietyUserInfoService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	
	@ModelAttribute
	public BizVarietyUserInfo get(@RequestParam(required=false) Integer id) {
		BizVarietyUserInfo entity = null;
		if (id!=null){
			entity = bizVarietyUserInfoService.get(id);
		}
		if (entity == null){
			entity = new BizVarietyUserInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:variety:bizVarietyUserInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizVarietyUserInfo bizVarietyUserInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizVarietyUserInfo> page = bizVarietyUserInfoService.findPage(new Page<BizVarietyUserInfo>(request, response), bizVarietyUserInfo); 
		model.addAttribute("page", page);
		return "modules/biz/variety/bizVarietyUserInfoList";
	}

	@RequiresPermissions("biz:variety:bizVarietyUserInfo:view")
	@RequestMapping(value = "form")
	public String form(BizVarietyUserInfo bizVarietyUserInfo, Model model) {
		model.addAttribute("bizVarietyUserInfo", bizVarietyUserInfo);
		Role role = new Role();
		role.setName(RoleEnNameEnum.SELECTIONOFSPECIALIST.getState());
		User user = new User();
		user.setRole(role);
		List<User> users = systemService.userSelectCompany(user);
		model.addAttribute("usersList", users);
		List<BizVarietyInfo> varietyFactorList = bizVarietyInfoService.findList(new BizVarietyInfo());
		model.addAttribute("varietyList", varietyFactorList);
		return "modules/biz/variety/bizVarietyUserInfoForm";
	}

	@RequiresPermissions("biz:variety:bizVarietyUserInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizVarietyUserInfo bizVarietyUserInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizVarietyUserInfo)){
			return form(bizVarietyUserInfo, model);
		}
		bizVarietyUserInfoService.save(bizVarietyUserInfo);
		addMessage(redirectAttributes, "保存品类与用户 关联成功");
		return "redirect:"+Global.getAdminPath()+"/biz/variety/bizVarietyUserInfo/?repage";
	}
	
	@RequiresPermissions("biz:variety:bizVarietyUserInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizVarietyUserInfo bizVarietyUserInfo, RedirectAttributes redirectAttributes) {
		bizVarietyUserInfoService.delete(bizVarietyUserInfo);
		addMessage(redirectAttributes, "删除品类与用户 关联成功");
		return "redirect:"+Global.getAdminPath()+"/biz/variety/bizVarietyUserInfo/?repage";
	}

}