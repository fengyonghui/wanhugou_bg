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
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValue;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueService;

/**
 * 标签属性值Controller
 * @author zx
 * @version 2018-03-21
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/attribute/attributeValue")
public class AttributeValueController extends BaseController {

	@Autowired
	private AttributeValueService attributeValueService;
	
	@ModelAttribute
	public AttributeValue get(@RequestParam(required=false) Integer id) {
		AttributeValue entity = null;
		if (id!=null){
			entity = attributeValueService.get(id);
		}
		if (entity == null){
			entity = new AttributeValue();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:attribute:attributeValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(AttributeValue attributeValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AttributeValue> page = attributeValueService.findPage(new Page<AttributeValue>(request, response), attributeValue); 
		model.addAttribute("page", page);
		return "modules/sys/attribute/attributeValueList";
	}

	@RequiresPermissions("sys:attribute:attributeValue:view")
	@RequestMapping(value = "form")
	public String form(AttributeValue attributeValue, Model model) {
		model.addAttribute("attributeValue", attributeValue);
		return "modules/sys/attribute/attributeValueForm";
	}

	@RequiresPermissions("sys:attribute:attributeValue:edit")
	@RequestMapping(value = "save")
	public String save(AttributeValue attributeValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, attributeValue)){
			return form(attributeValue, model);
		}
		attributeValueService.save(attributeValue);
		addMessage(redirectAttributes, "保存标签属性值成功");
		return "redirect:"+Global.getAdminPath()+"/sys/attribute/attributeValue/?repage";
	}
	
	@RequiresPermissions("sys:attribute:attributeValue:edit")
	@RequestMapping(value = "delete")
	public String delete(AttributeValue attributeValue, RedirectAttributes redirectAttributes) {
		attributeValueService.delete(attributeValue);
		addMessage(redirectAttributes, "删除标签属性值成功");
		return "redirect:"+Global.getAdminPath()+"/sys/attribute/attributeValue/?repage";
	}

}