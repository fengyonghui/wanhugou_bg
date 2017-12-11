/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web;

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
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.service.PropertyInfoService;

/**
 * 系统属性Controller
 * @author liuying
 * @version 2017-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/propertyInfo")
public class PropertyInfoController extends BaseController {

	@Autowired
	private PropertyInfoService propertyInfoService;
	
	@ModelAttribute
	public PropertyInfo get(@RequestParam(required=false) Integer id) {
		PropertyInfo entity = null;
		if (id!=null){
			entity = propertyInfoService.get(id);
		}
		if (entity == null){
			entity = new PropertyInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:propertyInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(PropertyInfo propertyInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PropertyInfo> page = propertyInfoService.findPage(new Page<PropertyInfo>(request, response), propertyInfo); 
		model.addAttribute("page", page);
		return "modules/sys/propertyInfoList";
	}

	@RequiresPermissions("sys:propertyInfo:view")
	@RequestMapping(value = "form")
	public String form(PropertyInfo propertyInfo, Model model) {
		model.addAttribute("propertyInfo", propertyInfo);
		return "modules/sys/propertyInfoForm";
	}

	@RequiresPermissions("sys:propertyInfo:edit")
	@RequestMapping(value = "save")
	public String save(PropertyInfo propertyInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, propertyInfo)){
			return form(propertyInfo, model);
		}
		propertyInfoService.save(propertyInfo);
		addMessage(redirectAttributes, "保存系统属性成功");
		return "redirect:"+Global.getAdminPath()+"/sys/propertyInfo/?repage";
	}
	
	@RequiresPermissions("sys:propertyInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(PropertyInfo propertyInfo, RedirectAttributes redirectAttributes) {
		propertyInfoService.delete(propertyInfo);
		addMessage(redirectAttributes, "删除系统属性成功");
		return "redirect:"+Global.getAdminPath()+"/sys/propertyInfo/?repage";
	}

}