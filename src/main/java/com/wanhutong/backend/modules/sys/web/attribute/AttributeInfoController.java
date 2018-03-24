/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web.attribute;

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
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfo;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeInfoService;

/**
 * 标签属性Controller
 * @author zx
 * @version 2018-03-21
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/attribute/attributeInfo")
public class AttributeInfoController extends BaseController {

	@Autowired
	private AttributeInfoService attributeInfoService;
	
	@ModelAttribute
	public AttributeInfo get(@RequestParam(required=false) Integer id) {
		AttributeInfo entity = null;
		if (id!=null){
			entity = attributeInfoService.get(id);
		}
		if (entity == null){
			entity = new AttributeInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:attribute:attributeInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(AttributeInfo attributeInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AttributeInfo> page = attributeInfoService.findPage(new Page<AttributeInfo>(request, response), attributeInfo); 
		model.addAttribute("page", page);
		return "modules/sys/attribute/attributeInfoList";
	}

	@RequiresPermissions("sys:attribute:attributeInfo:view")
	@RequestMapping(value = "form")
	public String form(AttributeInfo attributeInfo, Model model) {
		model.addAttribute("attributeInfo", attributeInfo);
		return "modules/sys/attribute/attributeInfoForm";
	}

	@RequiresPermissions("sys:attribute:attributeInfo:edit")
	@RequestMapping(value = "save")
	public String save(AttributeInfo attributeInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, attributeInfo)){
			return form(attributeInfo, model);
		}
		attributeInfoService.save(attributeInfo);
		addMessage(redirectAttributes, "保存标签属性成功");
		return "redirect:"+Global.getAdminPath()+"/sys/attribute/attributeInfo/?repage";
	}
	
	@RequiresPermissions("sys:attribute:attributeInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(AttributeInfo attributeInfo, RedirectAttributes redirectAttributes) {
		attributeInfoService.delete(attributeInfo);
		addMessage(redirectAttributes, "删除标签属性成功");
		return "redirect:"+Global.getAdminPath()+"/sys/attribute/attributeInfo/?repage";
	}

}