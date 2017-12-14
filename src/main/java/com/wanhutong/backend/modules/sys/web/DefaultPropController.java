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
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;

/**
 * 系统属性默认表Controller
 * @author liuying
 * @version 2017-12-14
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/defaultProp")
public class DefaultPropController extends BaseController {

	@Autowired
	private DefaultPropService defaultPropService;
	
	@ModelAttribute
	public DefaultProp get(@RequestParam(required=false) Integer id) {
		DefaultProp entity = null;
		if (id!=null){
			entity = defaultPropService.get(id);
		}
		if (entity == null){
			entity = new DefaultProp();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:defaultProp:view")
	@RequestMapping(value = {"list", ""})
	public String list(DefaultProp defaultProp, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DefaultProp> page = defaultPropService.findPage(new Page<DefaultProp>(request, response), defaultProp); 
		model.addAttribute("page", page);
		return "modules/sys/defaultPropList";
	}

	@RequiresPermissions("sys:defaultProp:view")
	@RequestMapping(value = "form")
	public String form(DefaultProp defaultProp, Model model) {
		model.addAttribute("defaultProp", defaultProp);
		return "modules/sys/defaultPropForm";
	}

	@RequiresPermissions("sys:defaultProp:edit")
	@RequestMapping(value = "save")
	public String save(DefaultProp defaultProp, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, defaultProp)){
			return form(defaultProp, model);
		}
		defaultPropService.save(defaultProp);
		addMessage(redirectAttributes, "保存系统属性默认表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/defaultProp/?repage";
	}
	
	@RequiresPermissions("sys:defaultProp:edit")
	@RequestMapping(value = "delete")
	public String delete(DefaultProp defaultProp, RedirectAttributes redirectAttributes) {
		defaultPropService.delete(defaultProp);
		addMessage(redirectAttributes, "删除系统属性默认表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/defaultProp/?repage";
	}

}