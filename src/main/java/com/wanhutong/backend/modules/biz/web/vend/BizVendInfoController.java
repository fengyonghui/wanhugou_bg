/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.vend;

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
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;

/**
 * 供应商拓展表Controller
 * @author liuying
 * @version 2018-02-24
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/vend/bizVendInfo")
public class BizVendInfoController extends BaseController {

	@Autowired
	private BizVendInfoService bizVendInfoService;
	
	@ModelAttribute
	public BizVendInfo get(@RequestParam(required=false) Integer id) {
		BizVendInfo entity = null;
		if (id!=null){
			entity = bizVendInfoService.get(id);
		}
		if (entity == null){
			entity = new BizVendInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:vend:bizVendInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizVendInfo bizVendInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizVendInfo> page = bizVendInfoService.findPage(new Page<BizVendInfo>(request, response), bizVendInfo); 
		model.addAttribute("page", page);
		return "modules/biz/vend/bizVendInfoList";
	}

	@RequiresPermissions("biz:vend:bizVendInfo:view")
	@RequestMapping(value = "form")
	public String form(BizVendInfo bizVendInfo, Model model) {
		model.addAttribute("bizVendInfo", bizVendInfo);
		return "modules/biz/vend/bizVendInfoForm";
	}

	@RequiresPermissions("biz:vend:bizVendInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizVendInfo bizVendInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizVendInfo)){
			return form(bizVendInfo, model);
		}
		bizVendInfoService.save(bizVendInfo);
		addMessage(redirectAttributes, "保存供应商拓展表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/vend/bizVendInfo/?repage";
	}
	
	@RequiresPermissions("biz:vend:bizVendInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizVendInfo bizVendInfo, RedirectAttributes redirectAttributes) {
		bizVendInfoService.delete(bizVendInfo);
		addMessage(redirectAttributes, "删除供应商拓展表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/vend/bizVendInfo/?repage";
	}

}