/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.biz.web.farm;

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

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.biz.entity.farm.BizFarm;
import com.thinkgem.jeesite.modules.biz.service.farm.BizFarmService;

/**
 * 单表生成Controller
 * @author ThinkGem
 * @version 2016-07-08
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/farm/bizFarm")
public class BizFarmController extends BaseController {

	@Autowired
	private BizFarmService bizFarmService;
	
	@ModelAttribute
	public BizFarm get(@RequestParam(required=false) Integer id) {
		BizFarm entity = null;
		if (id!=null){
			entity = bizFarmService.get(id);
		}
		if (entity == null){
			entity = new BizFarm();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:farm:bizFarm:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizFarm bizFarm, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizFarm> page = bizFarmService.findPage(new Page<BizFarm>(request, response), bizFarm); 
		model.addAttribute("page", page);
		return "modules/biz/farm/bizFarmList";
	}

	@RequiresPermissions("biz:farm:bizFarm:view")
	@RequestMapping(value = "form")
	public String form(BizFarm bizFarm, Model model) {
		model.addAttribute("bizFarm", bizFarm);
		return "modules/biz/farm/bizFarmForm";
	}

	@RequiresPermissions("biz:farm:bizFarm:edit")
	@RequestMapping(value = "save")
	public String save(BizFarm bizFarm, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizFarm)){
			return form(bizFarm, model);
		}
		bizFarmService.save(bizFarm);
		addMessage(redirectAttributes, "保存单表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/farm/bizFarm/?repage";
	}
	
	@RequiresPermissions("biz:farm:bizFarm:edit")
	@RequestMapping(value = "delete")
	public String delete(BizFarm bizFarm, RedirectAttributes redirectAttributes) {
		bizFarmService.delete(bizFarm);
		addMessage(redirectAttributes, "删除单表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/farm/bizFarm/?repage";
	}

}