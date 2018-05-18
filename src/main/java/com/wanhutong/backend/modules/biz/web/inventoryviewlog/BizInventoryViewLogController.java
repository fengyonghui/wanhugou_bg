/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventoryviewlog;

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
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.service.inventoryviewlog.BizInventoryViewLogService;

/**
 * 库存盘点记录Controller
 * @author zx
 * @version 2018-03-27
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventoryviewlog/bizInventoryViewLog")
public class BizInventoryViewLogController extends BaseController {

	@Autowired
	private BizInventoryViewLogService bizInventoryViewLogService;
	
	@ModelAttribute
	public BizInventoryViewLog get(@RequestParam(required=false) Integer id) {
		BizInventoryViewLog entity = null;
		if (id!=null){
			entity = bizInventoryViewLogService.get(id);
		}
		if (entity == null){
			entity = new BizInventoryViewLog();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInventoryViewLog bizInventoryViewLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInventoryViewLog> page = bizInventoryViewLogService.findPage(new Page<BizInventoryViewLog>(request, response), bizInventoryViewLog); 
		model.addAttribute("page", page);
		return "modules/biz/inventoryviewlog/bizInventoryViewLogList";
	}

	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:view")
	@RequestMapping(value = "form")
	public String form(BizInventoryViewLog bizInventoryViewLog, Model model) {
		model.addAttribute("bizInventoryViewLog", bizInventoryViewLog);
		return "modules/biz/inventoryviewlog/bizInventoryViewLogForm";
	}

	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:edit")
	@RequestMapping(value = "save")
	public String save(BizInventoryViewLog bizInventoryViewLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInventoryViewLog)){
			return form(bizInventoryViewLog, model);
		}
		bizInventoryViewLogService.save(bizInventoryViewLog);
		addMessage(redirectAttributes, "保存库存盘点记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventoryviewlog/bizInventoryViewLog/?repage";
	}
	
	@RequiresPermissions("biz:inventoryviewlog:bizInventoryViewLog:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInventoryViewLog bizInventoryViewLog, RedirectAttributes redirectAttributes) {
		bizInventoryViewLogService.delete(bizInventoryViewLog);
		addMessage(redirectAttributes, "删除库存盘点记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventoryviewlog/bizInventoryViewLog/?repage";
	}

}