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
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuViewLog;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuViewLogService;

/**
 * 商品出厂价日志表Controller
 * @author Oy
 * @version 2018-04-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/sku/bizSkuViewLog")
public class BizSkuViewLogController extends BaseController {

	@Autowired
	private BizSkuViewLogService bizSkuViewLogService;
	
	@ModelAttribute
	public BizSkuViewLog get(@RequestParam(required=false) Integer id) {
		BizSkuViewLog entity = null;
		if (id!=null){
			entity = bizSkuViewLogService.get(id);
		}
		if (entity == null){
			entity = new BizSkuViewLog();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:sku:bizSkuViewLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizSkuViewLog bizSkuViewLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSkuViewLog> page = bizSkuViewLogService.findPage(new Page<BizSkuViewLog>(request, response), bizSkuViewLog); 
		model.addAttribute("page", page);
		return "modules/biz/sku/bizSkuViewLogList";
	}

	@RequiresPermissions("biz:sku:bizSkuViewLog:view")
	@RequestMapping(value = "form")
	public String form(BizSkuViewLog bizSkuViewLog, Model model) {
		model.addAttribute("bizSkuViewLog", bizSkuViewLog);
		return "modules/biz/sku/bizSkuViewLogForm";
	}

	@RequiresPermissions("biz:sku:bizSkuViewLog:edit")
	@RequestMapping(value = "save")
	public String save(BizSkuViewLog bizSkuViewLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizSkuViewLog)){
			return form(bizSkuViewLog, model);
		}
		bizSkuViewLogService.save(bizSkuViewLog);
		addMessage(redirectAttributes, "保存出厂价日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuViewLog/?repage";
	}
	
	@RequiresPermissions("biz:sku:bizSkuViewLog:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSkuViewLog bizSkuViewLog, RedirectAttributes redirectAttributes) {
		bizSkuViewLogService.delete(bizSkuViewLog);
		addMessage(redirectAttributes, "删除出厂价日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/sku/bizSkuViewLog/?repage";
	}

}