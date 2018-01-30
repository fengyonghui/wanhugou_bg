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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.cms.BizCmsColumInfo;
import com.wanhutong.backend.modules.biz.service.cms.BizCmsColumInfoService;

import java.util.List;

/**
 * 页面栏目设置Controller
 * @author OuyangXiutian
 * @version 2018-01-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/cms/bizCmsColumInfo")
public class BizCmsColumInfoController extends BaseController {

	@Autowired
	private BizCmsColumInfoService bizCmsColumInfoService;
	
	@ModelAttribute
	public BizCmsColumInfo get(@RequestParam(required=false) Integer id) {
		BizCmsColumInfo entity = null;
		if (id!=null){
			entity = bizCmsColumInfoService.get(id);
		}
		if (entity == null){
			entity = new BizCmsColumInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:cms:bizCmsColumInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCmsColumInfo bizCmsColumInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCmsColumInfo> page = bizCmsColumInfoService.findPage(new Page<BizCmsColumInfo>(request, response), bizCmsColumInfo); 
		model.addAttribute("page", page);
		return "modules/biz/cms/bizCmsColumInfoList";
	}

	@RequiresPermissions("biz:cms:bizCmsColumInfo:view")
	@RequestMapping(value = "form")
	public String form(BizCmsColumInfo bizCmsColumInfo, Model model) {
		model.addAttribute("bizCmsColumInfo", bizCmsColumInfo);
		return "modules/biz/cms/bizCmsColumInfoForm";
	}

	@RequiresPermissions("biz:cms:bizCmsColumInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizCmsColumInfo bizCmsColumInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCmsColumInfo)){
			return form(bizCmsColumInfo, model);
		}
		bizCmsColumInfoService.save(bizCmsColumInfo);
		addMessage(redirectAttributes, "保存栏目成功");
		return "redirect:"+Global.getAdminPath()+"/biz/cms/bizCmsColumInfo/?repage";
	}
	
	@RequiresPermissions("biz:cms:bizCmsColumInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCmsColumInfo bizCmsColumInfo, RedirectAttributes redirectAttributes) {
		bizCmsColumInfoService.delete(bizCmsColumInfo);
		addMessage(redirectAttributes, "删除栏目成功");
		return "redirect:"+Global.getAdminPath()+"/biz/cms/bizCmsColumInfo/?repage";
	}

	@ResponseBody
	@RequiresPermissions("biz:shelf:bizOpShelfSku:view")
	@RequestMapping(value = "shelfForm")
	public List<BizCmsColumInfo> shelfForm(BizCmsColumInfo bizCmsColumInfo, Model model) {
		List<BizCmsColumInfo> list = bizCmsColumInfoService.findList(bizCmsColumInfo);
		return list;
	}
}