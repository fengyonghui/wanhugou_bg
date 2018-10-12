/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.logistic;

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
import com.wanhutong.backend.modules.biz.entity.logistic.BizOrderLogistics;
import com.wanhutong.backend.modules.biz.service.logistic.BizOrderLogisticsService;

/**
 * 运单配置Controller
 * @author wangby
 * @version 2018-09-26
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/logistic/bizOrderLogistics")
public class BizOrderLogisticsController extends BaseController {

	@Autowired
	private BizOrderLogisticsService bizOrderLogisticsService;
	
	@ModelAttribute
	public BizOrderLogistics get(@RequestParam(required=false) Integer id) {
		BizOrderLogistics entity = null;
		if (id!=null){
			entity = bizOrderLogisticsService.get(id);
		}
		if (entity == null){
			entity = new BizOrderLogistics();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:logistic:bizOrderLogistics:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderLogistics bizOrderLogistics, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderLogistics> page = bizOrderLogisticsService.findPage(new Page<BizOrderLogistics>(request, response), bizOrderLogistics); 
		model.addAttribute("page", page);
		return "modules/biz/logistic/bizOrderLogisticsList";
	}

	@RequiresPermissions("biz:logistic:bizOrderLogistics:view")
	@RequestMapping(value = "form")
	public String form(BizOrderLogistics bizOrderLogistics, Model model) {
		model.addAttribute("bizOrderLogistics", bizOrderLogistics);
		return "modules/biz/logistic/bizOrderLogisticsForm";
	}

	@RequiresPermissions("biz:logistic:bizOrderLogistics:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderLogistics bizOrderLogistics, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderLogistics)){
			return form(bizOrderLogistics, model);
		}
		bizOrderLogisticsService.save(bizOrderLogistics);
		addMessage(redirectAttributes, "保存运单配置成功");
		return "redirect:"+Global.getAdminPath()+"/biz/logistic/bizOrderLogistics/?repage";
	}
	
	@RequiresPermissions("biz:logistic:bizOrderLogistics:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderLogistics bizOrderLogistics, RedirectAttributes redirectAttributes) {
		bizOrderLogisticsService.delete(bizOrderLogistics);
		addMessage(redirectAttributes, "删除运单配置成功");
		return "redirect:"+Global.getAdminPath()+"/biz/logistic/bizOrderLogistics/?repage";
	}

}