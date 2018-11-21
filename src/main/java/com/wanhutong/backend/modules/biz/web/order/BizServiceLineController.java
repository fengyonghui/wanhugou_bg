/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

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
import com.wanhutong.backend.modules.biz.entity.order.BizServiceLine;
import com.wanhutong.backend.modules.biz.service.order.BizServiceLineService;

/**
 * 服务费物流线路Controller
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizServiceLine")
public class BizServiceLineController extends BaseController {

	@Autowired
	private BizServiceLineService bizServiceLineService;
	
	@ModelAttribute
	public BizServiceLine get(@RequestParam(required=false) Integer id) {
		BizServiceLine entity = null;
		if (id!=null){
			entity = bizServiceLineService.get(id);
		}
		if (entity == null){
			entity = new BizServiceLine();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizServiceLine:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizServiceLine bizServiceLine, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizServiceLine> page = bizServiceLineService.findPage(new Page<BizServiceLine>(request, response), bizServiceLine); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizServiceLineList";
	}

	@RequiresPermissions("biz:order:bizServiceLine:view")
	@RequestMapping(value = "form")
	public String form(BizServiceLine bizServiceLine, Model model) {
		model.addAttribute("bizServiceLine", bizServiceLine);
		return "modules/biz/order/bizServiceLineForm";
	}

	@RequiresPermissions("biz:order:bizServiceLine:edit")
	@RequestMapping(value = "save")
	public String save(BizServiceLine bizServiceLine, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizServiceLine)){
			return form(bizServiceLine, model);
		}
		bizServiceLineService.save(bizServiceLine);
		addMessage(redirectAttributes, "保存服务费物流线路成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizServiceLine/?repage";
	}
	
	@RequiresPermissions("biz:order:bizServiceLine:edit")
	@RequestMapping(value = "delete")
	public String delete(BizServiceLine bizServiceLine, RedirectAttributes redirectAttributes) {
		bizServiceLineService.delete(bizServiceLine);
		addMessage(redirectAttributes, "删除服务费物流线路成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizServiceLine/?repage";
	}

}