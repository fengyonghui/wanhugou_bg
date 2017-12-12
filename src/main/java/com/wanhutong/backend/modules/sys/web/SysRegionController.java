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
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.service.SysRegionService;

/**
 * 角色区域表Controller
 * @author zx
 * @version 2017-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysRegion")
public class SysRegionController extends BaseController {

	@Autowired
	private SysRegionService sysRegionService;
	
	@ModelAttribute
	public SysRegion get(@RequestParam(required=false) Integer id) {
		SysRegion entity = null;
		if (id!=null){
			entity = sysRegionService.get(id);
		}
		if (entity == null){
			entity = new SysRegion();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysRegion:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysRegion sysRegion, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysRegion> page = sysRegionService.findPage(new Page<SysRegion>(request, response), sysRegion); 
		model.addAttribute("page", page);
		return "modules/sys/sysRegionList";
	}

	@RequiresPermissions("sys:sysRegion:view")
	@RequestMapping(value = "form")
	public String form(SysRegion sysRegion, Model model) {
		model.addAttribute("sysRegion", sysRegion);
		return "modules/sys/sysRegionForm";
	}

	@RequiresPermissions("sys:sysRegion:edit")
	@RequestMapping(value = "save")
	public String save(SysRegion sysRegion, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysRegion)){
			return form(sysRegion, model);
		}
		sysRegionService.save(sysRegion);
		addMessage(redirectAttributes, "保存角色区域表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysRegion/?repage";
	}
	
	@RequiresPermissions("sys:sysRegion:edit")
	@RequestMapping(value = "delete")
	public String delete(SysRegion sysRegion, RedirectAttributes redirectAttributes) {
		sysRegionService.delete(sysRegion);
		addMessage(redirectAttributes, "删除角色区域表成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysRegion/?repage";
	}

}