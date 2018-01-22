/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.sku;

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
import com.wanhutong.backend.modules.biz.entity.sku.BizSearch;
import com.wanhutong.backend.modules.biz.service.sku.BizSearchService;

/**
 * 找货定制Controller
 * @author liuying
 * @version 2018-01-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/bizSearch")
public class BizSearchController extends BaseController {

	@Autowired
	private BizSearchService bizSearchService;
	
	@ModelAttribute
	public BizSearch get(@RequestParam(required=false) Integer id) {
		BizSearch entity = null;
		if (id!=null){
			entity = bizSearchService.get(id);
		}
		if (entity == null){
			entity = new BizSearch();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:sku:bizSearch:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSearch bizSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSearch> page = bizSearchService.findPage(new Page<BizSearch>(request, response), bizSearch); 
		model.addAttribute("page", page);
		return "modules/biz/sku/bizSearchList";
	}

	@RequiresPermissions("biz:sku:bizSearch:view")
	@RequestMapping(value = "form")
	public String form(BizSearch bizSearch, Model model) {
		model.addAttribute("bizSearch", bizSearch);
		return "modules/biz/sku/bizSearchForm";
	}

	@RequiresPermissions("biz:sku:bizSearch:edit")
	@RequestMapping(value = "save")
	public String save(BizSearch bizSearch, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSearch)){
			return form(bizSearch, model);
		}
		bizSearchService.save(bizSearch);
		addMessage(redirectAttributes, "保存找货定制成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSearch/?repage";
	}
	
	@RequiresPermissions("biz:sku:bizSearch:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSearch bizSearch, RedirectAttributes redirectAttributes) {
		bizSearchService.delete(bizSearch);
		addMessage(redirectAttributes, "删除找货定制成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSearch/?repage";
	}

}