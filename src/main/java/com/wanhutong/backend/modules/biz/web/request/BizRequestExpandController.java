/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

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
import com.wanhutong.backend.modules.biz.entity.request.BizRequestExpand;
import com.wanhutong.backend.modules.biz.service.request.BizRequestExpandService;

/**
 * 备货单扩展Controller
 * @author TengFei.zhang
 * @version 2018-07-26
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestExpand")
public class BizRequestExpandController extends BaseController {

	@Autowired
	private BizRequestExpandService bizRequestExpandService;
	
	@ModelAttribute
	public BizRequestExpand get(@RequestParam(required=false) Integer id) {
		BizRequestExpand entity = null;
		if (id!=null){
			entity = bizRequestExpandService.get(id);
		}
		if (entity == null){
			entity = new BizRequestExpand();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:request:bizRequestExpand:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizRequestExpand bizRequestExpand, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizRequestExpand> page = bizRequestExpandService.findPage(new Page<BizRequestExpand>(request, response), bizRequestExpand); 
		model.addAttribute("page", page);
		return "modules/biz/request/bizRequestExpandList";
	}

	@RequiresPermissions("biz:request:bizRequestExpand:view")
	@RequestMapping(value = "form")
	public String form(BizRequestExpand bizRequestExpand, Model model) {
		model.addAttribute("bizRequestExpand", bizRequestExpand);
		return "modules/biz/request/bizRequestExpandForm";
	}

	@RequiresPermissions("biz:request:bizRequestExpand:edit")
	@RequestMapping(value = "save")
	public String save(BizRequestExpand bizRequestExpand, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizRequestExpand)){
			return form(bizRequestExpand, model);
		}
		bizRequestExpandService.save(bizRequestExpand);
		addMessage(redirectAttributes, "保存备货单扩展成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestExpand/?repage";
	}
	
	@RequiresPermissions("biz:request:bizRequestExpand:edit")
	@RequestMapping(value = "delete")
	public String delete(BizRequestExpand bizRequestExpand, RedirectAttributes redirectAttributes) {
		bizRequestExpandService.delete(bizRequestExpand);
		addMessage(redirectAttributes, "删除备货单扩展成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestExpand/?repage";
	}

}