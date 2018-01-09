/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

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
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;

/**
 * 销售采购备货中间表Controller
 * @author 张腾飞
 * @version 2018-01-09
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizPoOrderReq")
public class BizPoOrderReqController extends BaseController {

	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;
	
	@ModelAttribute
	public BizPoOrderReq get(@RequestParam(required=false) Integer id) {
		BizPoOrderReq entity = null;
		if (id!=null){
			entity = bizPoOrderReqService.get(id);
		}
		if (entity == null){
			entity = new BizPoOrderReq();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:request:bizPoOrderReq:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPoOrderReq bizPoOrderReq, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPoOrderReq> page = bizPoOrderReqService.findPage(new Page<BizPoOrderReq>(request, response), bizPoOrderReq); 
		model.addAttribute("page", page);
		return "modules/biz/request/bizPoOrderReqList";
	}

	@RequiresPermissions("biz:request:bizPoOrderReq:view")
	@RequestMapping(value = "form")
	public String form(BizPoOrderReq bizPoOrderReq, Model model) {
		model.addAttribute("bizPoOrderReq", bizPoOrderReq);
		return "modules/biz/request/bizPoOrderReqForm";
	}

	@RequiresPermissions("biz:request:bizPoOrderReq:edit")
	@RequestMapping(value = "save")
	public String save(BizPoOrderReq bizPoOrderReq, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPoOrderReq)){
			return form(bizPoOrderReq, model);
		}
		bizPoOrderReqService.save(bizPoOrderReq);
		addMessage(redirectAttributes, "保存销售采购备货中间表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizPoOrderReq/?repage";
	}
	
	@RequiresPermissions("biz:request:bizPoOrderReq:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPoOrderReq bizPoOrderReq, RedirectAttributes redirectAttributes) {
		bizPoOrderReqService.delete(bizPoOrderReq);
		addMessage(redirectAttributes, "删除销售采购备货中间表成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizPoOrderReq/?repage";
	}

}