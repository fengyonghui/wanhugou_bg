/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web.attribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeInfoV2Service;
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
	private AttributeInfoV2Service attributeInfoService;
	
	@ModelAttribute
	public AttributeInfoV2 get(@RequestParam(required=false) Integer id) {
		AttributeInfoV2 entity = null;
		if (id!=null){
			entity = attributeInfoService.get(id);
		}
		if (entity == null){
			entity = new AttributeInfoV2();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:attribute:attributeInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(AttributeInfoV2 attributeInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AttributeInfoV2> page = attributeInfoService.findPage(new Page<AttributeInfoV2>(request, response), attributeInfo);
		model.addAttribute("page", page);
		return "modules/sys/attribute/attributeInfoList";
	}

	@RequiresPermissions("sys:attribute:attributeInfo:view")
	@RequestMapping(value = "form")
	public String form(AttributeInfoV2 attributeInfo, Model model) {
		model.addAttribute("attributeInfo", attributeInfo);
		return "modules/sys/attribute/attributeInfoForm";
	}

	@RequiresPermissions("sys:attribute:attributeInfo:edit")
	@RequestMapping(value = "save")
	public String save(AttributeInfoV2 attributeInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, attributeInfo)){
			return form(attributeInfo, model);
		}
		attributeInfoService.save(attributeInfo);
		addMessage(redirectAttributes, "保存标签属性成功");
		return "redirect:"+Global.getAdminPath()+"/sys/attribute/attributeInfo/?repage";
	}
	
	@RequiresPermissions("sys:attribute:attributeInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(AttributeInfoV2 attributeInfo, RedirectAttributes redirectAttributes) {
		attributeInfoService.delete(attributeInfo);
		addMessage(redirectAttributes, "删除标签属性成功");
		return "redirect:"+Global.getAdminPath()+"/sys/attribute/attributeInfo/?repage";
	}

}