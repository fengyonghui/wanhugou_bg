/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.category;

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
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;

/**
 * 记录分类下所有属性值Controller
 * @author liuying
 * @version 2017-12-06
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/category/bizCatePropValue")
public class BizCatePropValueController extends BaseController {

	@Autowired
	private BizCatePropValueService bizCatePropValueService;
	
	@ModelAttribute
	public BizCatePropValue get(@RequestParam(required=false) Integer id) {
		BizCatePropValue entity = null;
		if (id!=null){
			entity = bizCatePropValueService.get(id);
		}
		if (entity == null){
			entity = new BizCatePropValue();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:category:bizCatePropValue:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCatePropValue bizCatePropValue, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCatePropValue> page = bizCatePropValueService.findPage(new Page<BizCatePropValue>(request, response), bizCatePropValue); 
		model.addAttribute("page", page);
		return "modules/biz/category/bizCatePropValueList";
	}

	@RequiresPermissions("biz:category:bizCatePropValue:view")
	@RequestMapping(value = "form")
	public String form(BizCatePropValue bizCatePropValue, Model model) {
		model.addAttribute("bizCatePropValue", bizCatePropValue);
		return "modules/biz/category/bizCatePropValueForm";
	}

	@RequiresPermissions("biz:category:bizCatePropValue:edit")
	@RequestMapping(value = "save")
	public String save(BizCatePropValue bizCatePropValue, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCatePropValue)){
			return form(bizCatePropValue, model);
		}
		bizCatePropValueService.save(bizCatePropValue);
		addMessage(redirectAttributes, "保存分类下的属性值成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCatePropValue/?repage";
	}
	
	@RequiresPermissions("biz:category:bizCatePropValue:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCatePropValue bizCatePropValue, RedirectAttributes redirectAttributes) {
		bizCatePropValueService.delete(bizCatePropValue);
		addMessage(redirectAttributes, "删除分类下的属性值成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizCatePropValue/?repage";
	}

}