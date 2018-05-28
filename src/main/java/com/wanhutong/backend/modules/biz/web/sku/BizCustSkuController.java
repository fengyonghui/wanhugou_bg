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
import com.wanhutong.backend.modules.biz.entity.sku.BizCustSku;
import com.wanhutong.backend.modules.biz.service.sku.BizCustSkuService;

/**
 * 采购商商品价格Controller
 * @author ZhangTengfei
 * @version 2018-05-15
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/bizCustSku")
public class BizCustSkuController extends BaseController {

	@Autowired
	private BizCustSkuService bizCustSkuService;
	
	@ModelAttribute
	public BizCustSku get(@RequestParam(required=false) Integer id) {
		BizCustSku entity = null;
		if (id!=null){
			entity = bizCustSkuService.get(id);
		}
		if (entity == null){
			entity = new BizCustSku();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:sku:bizCustSku:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCustSku bizCustSku, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCustSku> page = bizCustSkuService.findPage(new Page<BizCustSku>(request, response), bizCustSku);
		model.addAttribute("page", page);
		return "modules/biz/sku/bizCustSkuList";
	}

	@RequiresPermissions("biz:sku:bizCustSku:view")
	@RequestMapping(value = "form")
	public String form(BizCustSku bizCustSku, Model model) {
			model.addAttribute("bizCustSku", bizCustSku);
		return "modules/biz/sku/bizCustSkuForm";
	}

	@RequiresPermissions("biz:sku:bizCustSku:edit")
	@RequestMapping(value = "save")
	public String save(BizCustSku bizCustSku, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCustSku)){
			return form(bizCustSku, model);
		}
		bizCustSkuService.save(bizCustSku);
		addMessage(redirectAttributes, "保存经销店商品价格成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizCustSku/?repage";
	}
	
	@RequiresPermissions("biz:sku:bizCustSku:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCustSku bizCustSku, RedirectAttributes redirectAttributes) {
		bizCustSkuService.delete(bizCustSku);
		addMessage(redirectAttributes, "删除经销店商品价格成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizCustSku/?repage";
	}

}