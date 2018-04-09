/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.service.DictService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 产品品牌Controller
 * @author ZhangTengfei
 * @version 2018-04-09
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/product/brand")
public class BrandController extends BaseController {

	//品牌
	private static final String DICTTYPE = "brand";
	@Autowired
	private DictService dictService;
	
	@ModelAttribute
	public Dict get(@RequestParam(required=false) Integer id) {
		if (id!=null){
			return dictService.get(id);
		}else{
			return new Dict();
		}
	}
	
	@RequiresPermissions("biz:product:brand:view")
	@RequestMapping(value = {"list", ""})
	public String list(Dict dict, HttpServletRequest request, HttpServletResponse response, Model model) {
		dict.setType(DICTTYPE);
        Page<Dict> page = dictService.findPage(new Page<Dict>(request, response), dict); 
        model.addAttribute("page", page);
		return "modules/biz/product/bizProductBrandList";
	}

	@RequiresPermissions("biz:product:brand:view")
	@RequestMapping(value = "form")
	public String form(Dict dict, Model model) {
		model.addAttribute("dict", dict);
		return "modules/biz/product/bizProductBrandForm";
	}

	@RequiresPermissions("biz:product:brand:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(Dict dict, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/biz/product/brand/?repage&type="+dict.getType();
		}
		if (!beanValidator(model, dict)){
			return form(dict, model);
		}
		dictService.save(dict);
		addMessage(redirectAttributes, "保存产品品牌'" + dict.getLabel() + "'成功");
		return "redirect:" + adminPath + "/biz/product/brand/?repage&type="+dict.getType();
	}
	
	@RequiresPermissions("biz:product:brand:edit")
	@RequestMapping(value = "delete")
	public String delete(Dict dict, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/biz/product/brand/?repage";
		}
		dictService.delete(dict);
		addMessage(redirectAttributes, "删除产品品牌成功");
		return "redirect:" + adminPath + "/biz/product/brand/?repage&type="+dict.getType();
	}
}
