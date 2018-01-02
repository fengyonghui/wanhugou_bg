/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.invoice;

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
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceInfo;
import com.wanhutong.backend.modules.biz.service.invoice.BizInvoiceInfoService;

/**
 * 记录客户发票信息(发票开户行,税号)Controller
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/invoice/bizInvoiceInfo")
public class BizInvoiceInfoController extends BaseController {

	@Autowired
	private BizInvoiceInfoService bizInvoiceInfoService;
	
	@ModelAttribute
	public BizInvoiceInfo get(@RequestParam(required=false) Integer id) {
		BizInvoiceInfo entity = null;
		if (id!=null){
			entity = bizInvoiceInfoService.get(id);
		}
		if (entity == null){
			entity = new BizInvoiceInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:invoice:bizInvoiceInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInvoiceInfo bizInvoiceInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInvoiceInfo> page = bizInvoiceInfoService.findPage(new Page<BizInvoiceInfo>(request, response), bizInvoiceInfo); 
		model.addAttribute("page", page);
		return "modules/biz/invoice/bizInvoiceInfoList";
	}

	@RequiresPermissions("biz:invoice:bizInvoiceInfo:view")
	@RequestMapping(value = "form")
	public String form(BizInvoiceInfo bizInvoiceInfo, Model model) {
		model.addAttribute("bizInvoiceInfo", bizInvoiceInfo);
		return "modules/biz/invoice/bizInvoiceInfoForm";
	}

	@RequiresPermissions("biz:invoice:bizInvoiceInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizInvoiceInfo bizInvoiceInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInvoiceInfo)){
			return form(bizInvoiceInfo, model);
		}
		bizInvoiceInfoService.save(bizInvoiceInfo);
		addMessage(redirectAttributes, "保存发票信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/invoice/bizInvoiceInfo/?repage";
	}
	
	@RequiresPermissions("biz:invoice:bizInvoiceInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInvoiceInfo bizInvoiceInfo, RedirectAttributes redirectAttributes) {
		bizInvoiceInfoService.delete(bizInvoiceInfo);
		addMessage(redirectAttributes, "删除发票信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/invoice/bizInvoiceInfo/?repage";
	}

}