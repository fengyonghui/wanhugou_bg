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
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;

/**
 * 备货清单Controller
 * @author liuying
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestHeader")
public class BizRequestHeaderController extends BaseController {

	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;
	
	@ModelAttribute
	public BizRequestHeader get(@RequestParam(required=false) Integer id) {
		BizRequestHeader entity = null;
		if (id!=null){
			entity = bizRequestHeaderService.get(id);
		}
		if (entity == null){
			entity = new BizRequestHeader();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizRequestHeader bizRequestHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizRequestHeader> page = bizRequestHeaderService.findPage(new Page<BizRequestHeader>(request, response), bizRequestHeader); 
		model.addAttribute("page", page);
		return "modules/biz/request/bizRequestHeaderList";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "form")
	public String form(BizRequestHeader bizRequestHeader, Model model) {
		model.addAttribute("entity", bizRequestHeader);
		return "modules/biz/request/bizRequestHeaderForm";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizRequestHeader bizRequestHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizRequestHeader)){
			return form(bizRequestHeader, model);
		}
		bizRequestHeaderService.save(bizRequestHeader);
		addMessage(redirectAttributes, "保存备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeader/?repage";
	}
	
	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizRequestHeader bizRequestHeader, RedirectAttributes redirectAttributes) {
		bizRequestHeaderService.delete(bizRequestHeader);
		addMessage(redirectAttributes, "删除备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeader/?repage";
	}

}