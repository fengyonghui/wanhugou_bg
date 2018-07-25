/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

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
import com.wanhutong.backend.modules.biz.entity.po.BizCompletePaln;
import com.wanhutong.backend.modules.biz.service.po.BizCompletePalnService;

/**
 * 确认排产表Controller
 * @author 王冰洋
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizCompletePaln")
public class BizCompletePalnController extends BaseController {

	@Autowired
	private BizCompletePalnService bizCompletePalnService;
	
	@ModelAttribute
	public BizCompletePaln get(@RequestParam(required=false) Integer id) {
		BizCompletePaln entity = null;
		if (id!=null){
			entity = bizCompletePalnService.get(id);
		}
		if (entity == null){
			entity = new BizCompletePaln();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:po:bizCompletePaln:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCompletePaln bizCompletePaln, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCompletePaln> page = bizCompletePalnService.findPage(new Page<BizCompletePaln>(request, response), bizCompletePaln); 
		model.addAttribute("page", page);
		return "modules/biz/po/bizCompletePalnList";
	}

	@RequiresPermissions("biz:po:bizCompletePaln:view")
	@RequestMapping(value = "form")
	public String form(BizCompletePaln bizCompletePaln, Model model) {
		model.addAttribute("bizCompletePaln", bizCompletePaln);
		return "modules/biz/po/bizCompletePalnForm";
	}

	@RequiresPermissions("biz:po:bizCompletePaln:edit")
	@RequestMapping(value = "save")
	public String save(BizCompletePaln bizCompletePaln, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCompletePaln)){
			return form(bizCompletePaln, model);
		}
		bizCompletePalnService.save(bizCompletePaln);
		addMessage(redirectAttributes, "保存确认排产表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizCompletePaln/?repage";
	}
	
	@RequiresPermissions("biz:po:bizCompletePaln:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCompletePaln bizCompletePaln, RedirectAttributes redirectAttributes) {
		bizCompletePalnService.delete(bizCompletePaln);
		addMessage(redirectAttributes, "删除确认排产表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizCompletePaln/?repage";
	}

}