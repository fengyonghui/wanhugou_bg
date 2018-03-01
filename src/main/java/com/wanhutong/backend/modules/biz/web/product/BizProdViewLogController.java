/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.product;

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
import com.wanhutong.backend.modules.biz.entity.product.BizProdViewLog;
import com.wanhutong.backend.modules.biz.service.product.BizProdViewLogService;

/**
 * 产品查看日志Controller
 * @author zx
 * @version 2018-02-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/product/bizProdViewLog")
public class BizProdViewLogController extends BaseController {

	@Autowired
	private BizProdViewLogService bizProdViewLogService;
	
	@ModelAttribute
	public BizProdViewLog get(@RequestParam(required=false) Integer id) {
		BizProdViewLog entity = null;
		if (id!=null){
			entity = bizProdViewLogService.get(id);
		}
		if (entity == null){
			entity = new BizProdViewLog();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:product:bizProdViewLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizProdViewLog bizProdViewLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizProdViewLog> page = bizProdViewLogService.findPage(new Page<BizProdViewLog>(request, response), bizProdViewLog); 
		model.addAttribute("page", page);
		return "modules/biz/product/bizProdViewLogList";
	}

	@RequiresPermissions("biz:product:bizProdViewLog:view")
	@RequestMapping(value = "form")
	public String form(BizProdViewLog bizProdViewLog, Model model) {
		model.addAttribute("bizProdViewLog", bizProdViewLog);
		return "modules/biz/product/bizProdViewLogForm";
	}

	@RequiresPermissions("biz:product:bizProdViewLog:edit")
	@RequestMapping(value = "save")
	public String save(BizProdViewLog bizProdViewLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizProdViewLog)){
			return form(bizProdViewLog, model);
		}
		bizProdViewLogService.save(bizProdViewLog);
		addMessage(redirectAttributes, "保存日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProdViewLog/?repage";
	}
	
	@RequiresPermissions("biz:product:bizProdViewLog:edit")
	@RequestMapping(value = "delete")
	public String delete(BizProdViewLog bizProdViewLog, RedirectAttributes redirectAttributes) {
		bizProdViewLogService.delete(bizProdViewLog);
		addMessage(redirectAttributes, "删除日志成功");
		return "redirect:"+Global.getAdminPath()+"/biz/product/bizProdViewLog/?repage";
	}

}