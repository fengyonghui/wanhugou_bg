/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.process.web;

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
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;

/**
 * 通用流程Controller
 * @author Ma.Qiang
 * @version 2018-04-28
 */
@Controller
@RequestMapping(value = "${adminPath}/process/commonProcessEntity")
public class CommonProcessController extends BaseController {

	@Autowired
	private CommonProcessService commonProcessEntityService;
	
	@ModelAttribute
	public CommonProcessEntity get(@RequestParam(required=false) Integer id) {
		CommonProcessEntity entity = null;
		if (id!=null){
			entity = commonProcessEntityService.get(id);
		}
		if (entity == null){
			entity = new CommonProcessEntity();
		}
		return entity;
	}
	
	@RequiresPermissions("process:commonProcessEntity:view")
	@RequestMapping(value = {"list", ""})
	public String list(CommonProcessEntity commonProcessEntity, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CommonProcessEntity> page = commonProcessEntityService.findPage(new Page<CommonProcessEntity>(request, response), commonProcessEntity); 
		model.addAttribute("page", page);
		return "modules/process/commonProcessEntityList";
	}

	@RequiresPermissions("process:commonProcessEntity:view")
	@RequestMapping(value = "form")
	public String form(CommonProcessEntity commonProcessEntity, Model model) {
		model.addAttribute("commonProcessEntity", commonProcessEntity);
		return "modules/process/commonProcessEntityForm";
	}

	@RequiresPermissions("process:commonProcessEntity:edit")
	@RequestMapping(value = "save")
	public String save(CommonProcessEntity commonProcessEntity, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, commonProcessEntity)){
			return form(commonProcessEntity, model);
		}
		commonProcessEntityService.save(commonProcessEntity);
		addMessage(redirectAttributes, "保存通用流程成功");
		return "redirect:"+Global.getAdminPath()+"/process/commonProcessEntity/?repage";
	}
	
	@RequiresPermissions("process:commonProcessEntity:edit")
	@RequestMapping(value = "delete")
	public String delete(CommonProcessEntity commonProcessEntity, RedirectAttributes redirectAttributes) {
		commonProcessEntityService.delete(commonProcessEntity);
		addMessage(redirectAttributes, "删除通用流程成功");
		return "redirect:"+Global.getAdminPath()+"/process/commonProcessEntity/?repage";
	}

}