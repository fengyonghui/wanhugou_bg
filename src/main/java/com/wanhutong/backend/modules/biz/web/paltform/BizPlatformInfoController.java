/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.paltform;

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
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;

/**
 * Android, iOS, PC, 线下Controller
 * @author OuyangXiutian
 * @version 2017-12-21
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/paltform/bizPlatformInfo")
public class BizPlatformInfoController extends BaseController {

	@Autowired
	private BizPlatformInfoService bizPlatformInfoService;
	
	@ModelAttribute
	public BizPlatformInfo get(@RequestParam(required=false) Integer id) {
		BizPlatformInfo entity = null;
		if (id!=null){
			entity = bizPlatformInfoService.get(id);
		}
		if (entity == null){
			entity = new BizPlatformInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:paltform:bizPlatformInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPlatformInfo bizPlatformInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPlatformInfo> page = bizPlatformInfoService.findPage(new Page<BizPlatformInfo>(request, response), bizPlatformInfo); 
		model.addAttribute("page", page);
		return "modules/biz/paltform/bizPlatformInfoList";
	}

	@RequiresPermissions("biz:paltform:bizPlatformInfo:view")
	@RequestMapping(value = "form")
	public String form(BizPlatformInfo bizPlatformInfo, Model model) {
		model.addAttribute("bizPlatformInfo", bizPlatformInfo);
		return "modules/biz/paltform/bizPlatformInfoForm";
	}

	@RequiresPermissions("biz:paltform:bizPlatformInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizPlatformInfo bizPlatformInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPlatformInfo)){
			return form(bizPlatformInfo, model);
		}
		bizPlatformInfoService.save(bizPlatformInfo);
		addMessage(redirectAttributes, "保存设备成功");
		return "redirect:"+Global.getAdminPath()+"/biz/paltform/bizPlatformInfo/?repage";
	}
	
	@RequiresPermissions("biz:paltform:bizPlatformInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPlatformInfo bizPlatformInfo, RedirectAttributes redirectAttributes) {
		bizPlatformInfoService.delete(bizPlatformInfo);
		addMessage(redirectAttributes, "删除设备成功");
		return "redirect:"+Global.getAdminPath()+"/biz/paltform/bizPlatformInfo/?repage";
	}

}