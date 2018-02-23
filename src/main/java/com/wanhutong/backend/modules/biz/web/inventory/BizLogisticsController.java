/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

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
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;

/**
 * 物流商类Controller
 * @author 张腾飞
 * @version 2018-02-21
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizLogistics")
public class BizLogisticsController extends BaseController {

	@Autowired
	private BizLogisticsService bizLogisticsService;
	
	@ModelAttribute
	public BizLogistics get(@RequestParam(required=false) Integer id) {
		BizLogistics entity = null;
		if (id!=null){
			entity = bizLogisticsService.get(id);
		}
		if (entity == null){
			entity = new BizLogistics();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizLogistics:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizLogistics bizLogistics, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizLogistics> page = bizLogisticsService.findPage(new Page<BizLogistics>(request, response), bizLogistics); 
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizLogisticsList";
	}

	@RequiresPermissions("biz:inventory:bizLogistics:view")
	@RequestMapping(value = "form")
	public String form(BizLogistics bizLogistics, Model model) {
		model.addAttribute("bizLogistics", bizLogistics);
		return "modules/biz/inventory/bizLogisticsForm";
	}

	@RequiresPermissions("biz:inventory:bizLogistics:edit")
	@RequestMapping(value = "save")
	public String save(BizLogistics bizLogistics, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizLogistics)){
			return form(bizLogistics, model);
		}
		bizLogisticsService.save(bizLogistics);
		addMessage(redirectAttributes, "保存物流商成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizLogistics/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizLogistics:edit")
	@RequestMapping(value = "delete")
	public String delete(BizLogistics bizLogistics, RedirectAttributes redirectAttributes) {
		bizLogisticsService.delete(bizLogistics);
		addMessage(redirectAttributes, "删除物流商成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizLogistics/?repage";
	}

}