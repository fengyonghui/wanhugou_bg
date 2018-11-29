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
import com.wanhutong.backend.modules.biz.entity.message.BizMessageOfficeType;
import com.wanhutong.backend.modules.biz.service.message.BizMessageOfficeTypeService;

/**
 * 站内信发送用户类型表Controller
 * @author wangby
 * @version 2018-11-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/message/bizMessageOfficeType")
public class BizMessageOfficeTypeController extends BaseController {

	@Autowired
	private BizMessageOfficeTypeService bizMessageOfficeTypeService;
	
	@ModelAttribute
	public BizMessageOfficeType get(@RequestParam(required=false) Integer id) {
		BizMessageOfficeType entity = null;
		if (id!=null){
			entity = bizMessageOfficeTypeService.get(id);
		}
		if (entity == null){
			entity = new BizMessageOfficeType();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:message:bizMessageOfficeType:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizMessageOfficeType bizMessageOfficeType, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizMessageOfficeType> page = bizMessageOfficeTypeService.findPage(new Page<BizMessageOfficeType>(request, response), bizMessageOfficeType); 
		model.addAttribute("page", page);
		return "modules/biz/message/bizMessageOfficeTypeList";
	}

	@RequiresPermissions("biz:message:bizMessageOfficeType:view")
	@RequestMapping(value = "form")
	public String form(BizMessageOfficeType bizMessageOfficeType, Model model) {
		model.addAttribute("bizMessageOfficeType", bizMessageOfficeType);
		return "modules/biz/message/bizMessageOfficeTypeForm";
	}

	@RequiresPermissions("biz:message:bizMessageOfficeType:edit")
	@RequestMapping(value = "save")
	public String save(BizMessageOfficeType bizMessageOfficeType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizMessageOfficeType)){
			return form(bizMessageOfficeType, model);
		}
		bizMessageOfficeTypeService.save(bizMessageOfficeType);
		addMessage(redirectAttributes, "保存站内信发送用户类型表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/message/bizMessageOfficeType/?repage";
	}
	
	@RequiresPermissions("biz:message:bizMessageOfficeType:edit")
	@RequestMapping(value = "delete")
	public String delete(BizMessageOfficeType bizMessageOfficeType, RedirectAttributes redirectAttributes) {
		bizMessageOfficeTypeService.delete(bizMessageOfficeType);
		addMessage(redirectAttributes, "删除站内信发送用户类型表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/message/bizMessageOfficeType/?repage";
	}

}