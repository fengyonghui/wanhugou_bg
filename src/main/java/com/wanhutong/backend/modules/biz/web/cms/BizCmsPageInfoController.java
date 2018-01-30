/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.cms;

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
import com.wanhutong.backend.modules.biz.entity.cms.BizCmsPageInfo;
import com.wanhutong.backend.modules.biz.service.cms.BizCmsPageInfoService;

/**
 * 定义产品页面Controller
 * @author OuyangXiutian
 * @version 2018-01-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/cms/bizCmsPageInfo")
public class BizCmsPageInfoController extends BaseController {

	@Autowired
	private BizCmsPageInfoService bizCmsPageInfoService;
	
	@ModelAttribute
	public BizCmsPageInfo get(@RequestParam(required=false) Integer id) {
		BizCmsPageInfo entity = null;
		if (id!=null){
			entity = bizCmsPageInfoService.get(id);
		}
		if (entity == null){
			entity = new BizCmsPageInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:cms:bizCmsPageInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCmsPageInfo bizCmsPageInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCmsPageInfo> page = bizCmsPageInfoService.findPage(new Page<BizCmsPageInfo>(request, response), bizCmsPageInfo); 
		model.addAttribute("page", page);
		return "modules/biz/cms/bizCmsPageInfoList";
	}

	@RequiresPermissions("biz:cms:bizCmsPageInfo:view")
	@RequestMapping(value = "form")
	public String form(BizCmsPageInfo bizCmsPageInfo, Model model) {
		model.addAttribute("bizCmsPageInfo", bizCmsPageInfo);
		return "modules/biz/cms/bizCmsPageInfoForm";
	}

	@RequiresPermissions("biz:cms:bizCmsPageInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizCmsPageInfo bizCmsPageInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCmsPageInfo)){
			return form(bizCmsPageInfo, model);
		}
		bizCmsPageInfoService.save(bizCmsPageInfo);
		addMessage(redirectAttributes, "保存产品页面成功");
		return "redirect:"+Global.getAdminPath()+"/biz/cms/bizCmsPageInfo/?repage";
	}
	
	@RequiresPermissions("biz:cms:bizCmsPageInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCmsPageInfo bizCmsPageInfo, RedirectAttributes redirectAttributes) {
		bizCmsPageInfoService.delete(bizCmsPageInfo);
		addMessage(redirectAttributes, "删除产品页面成功");
		return "redirect:"+Global.getAdminPath()+"/biz/cms/bizCmsPageInfo/?repage";
	}

}