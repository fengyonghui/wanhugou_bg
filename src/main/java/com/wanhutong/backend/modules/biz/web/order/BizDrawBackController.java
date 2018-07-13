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
import com.wanhutong.backend.modules.biz.entity.order.BizDrawBack;
import com.wanhutong.backend.modules.biz.service.order.BizDrawBackService;

/**
 * 退款记录Controller
 * @author 王冰洋
 * @version 2018-07-13
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizDrawBack")
public class BizDrawBackController extends BaseController {

	@Autowired
	private BizDrawBackService bizDrawBackService;
	
	@ModelAttribute
	public BizDrawBack get(@RequestParam(required=false) Integer id) {
		BizDrawBack entity = null;
		if (id!=null){
			entity = bizDrawBackService.get(id);
		}
		if (entity == null){
			entity = new BizDrawBack();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizDrawBack:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizDrawBack bizDrawBack, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizDrawBack> page = bizDrawBackService.findPage(new Page<BizDrawBack>(request, response), bizDrawBack); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizDrawBackList";
	}

	@RequiresPermissions("biz:order:bizDrawBack:view")
	@RequestMapping(value = "form")
	public String form(BizDrawBack bizDrawBack, Model model) {
		model.addAttribute("bizDrawBack", bizDrawBack);
		return "modules/biz/order/bizDrawBackForm";
	}

	@RequiresPermissions("biz:order:bizDrawBack:edit")
	@RequestMapping(value = "save")
	public String save(BizDrawBack bizDrawBack, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizDrawBack)){
			return form(bizDrawBack, model);
		}
		bizDrawBackService.save(bizDrawBack);
		addMessage(redirectAttributes, "保存退款记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizDrawBack/?repage";
	}
	
	@RequiresPermissions("biz:order:bizDrawBack:edit")
	@RequestMapping(value = "delete")
	public String delete(BizDrawBack bizDrawBack, RedirectAttributes redirectAttributes) {
		bizDrawBackService.delete(bizDrawBack);
		addMessage(redirectAttributes, "删除退款记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizDrawBack/?repage";
	}

}