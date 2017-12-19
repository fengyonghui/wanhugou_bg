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
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.service.PropValueService;

/**
 * 系统属性值Controller
 * @author liuying
 * @version 2017-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/propValue")
public class PropValueController extends BaseController {

	@Autowired
	private PropValueService propValueService;
	
	@ModelAttribute
	public PropValue get(@RequestParam(required=false) Integer id) {
		PropValue entity = null;
		if (id!=null){
			entity = propValueService.get(id);

		}
		if (entity == null){
			entity = new PropValue();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:propValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(PropValue propValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PropValue> page = propValueService.findPage(new Page<PropValue>(request, response), propValue); 
		model.addAttribute("page", page);
		return "modules/sys/propValueList";
	}

	@RequiresPermissions("sys:propValue:view")
	@RequestMapping(value = "form")
	public String form(PropValue propValue, Model model) {
		model.addAttribute("propValue", propValue);
		return "modules/sys/propValueForm";
	}

	@RequiresPermissions("sys:propValue:edit")
	@RequestMapping(value = "save")
	public String save(PropValue propValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, propValue)){
			return form(propValue, model);
		}
		propValueService.save(propValue);
		addMessage(redirectAttributes, "保存系统属性值成功");
		return "redirect:"+Global.getAdminPath()+"/sys/propValue/?repage";
	}
	
	@RequiresPermissions("sys:propValue:edit")
	@RequestMapping(value = "delete")
	public String delete(PropValue propValue, RedirectAttributes redirectAttributes) {
		propValueService.delete(propValue);
		addMessage(redirectAttributes, "删除系统属性值成功");
		return "redirect:"+Global.getAdminPath()+"/sys/propValue/?repage";
	}

}