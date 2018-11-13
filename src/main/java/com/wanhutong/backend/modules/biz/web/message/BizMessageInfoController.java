/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
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
import com.wanhutong.backend.modules.biz.entity.message.BizMessageInfo;
import com.wanhutong.backend.modules.biz.service.message.BizMessageInfoService;

/**
 * 站内信Controller
 * @author Ma.Qiang
 * @version 2018-07-27
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/message/bizMessageInfo")
public class BizMessageInfoController extends BaseController {

	@Autowired
	private BizMessageInfoService bizMessageInfoService;
	
	@ModelAttribute
	public BizMessageInfo get(@RequestParam(required=false) Integer id) {
		BizMessageInfo entity = null;
		if (id!=null){
			entity = bizMessageInfoService.get(id);
		}
		if (entity == null){
			entity = new BizMessageInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:message:bizMessageInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizMessageInfo bizMessageInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizMessageInfo> page = bizMessageInfoService.findPage(new Page<BizMessageInfo>(request, response), bizMessageInfo); 
		model.addAttribute("page", page);
		return "modules/biz/message/bizMessageInfoList";
	}

	@RequiresPermissions("biz:message:bizMessageInfo:view")
	@RequestMapping(value = "form")
	public String form(BizMessageInfo bizMessageInfo, Model model) {
		model.addAttribute("bizMessageInfo", bizMessageInfo);
		return "modules/biz/message/bizMessageInfoForm";
	}

	@RequiresPermissions("biz:message:bizMessageInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizMessageInfo bizMessageInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizMessageInfo)){
			return form(bizMessageInfo, model);
		}
		try {
			String saveType = bizMessageInfo.getSaveType();
			if ("save".equals(saveType)) {
				bizMessageInfo.setBizStatus(BizMessageInfo.BizStatus.NO_SEND.getStatus());
			} else if ("saveAndSend".equals(saveType)) {
				bizMessageInfo.setBizStatus(BizMessageInfo.BizStatus.SEND_COMPLETE.getStatus());
			}

			Pair<Boolean, String> booleanStringPair = bizMessageInfoService.saveMessage(bizMessageInfo);
			addMessage(redirectAttributes, booleanStringPair.getRight());
			return "redirect:"+Global.getAdminPath()+"/biz/message/bizMessageInfo/?repage";
		} catch (Exception e) {
			logger.error("save message error", e);
		}
		addMessage(redirectAttributes, "保存站内信失败");
		return "redirect:"+Global.getAdminPath()+"/biz/message/bizMessageInfo/?repage";
	}
	
	@RequiresPermissions("biz:message:bizMessageInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizMessageInfo bizMessageInfo, RedirectAttributes redirectAttributes) {
		bizMessageInfoService.delete(bizMessageInfo);
		addMessage(redirectAttributes, "删除站内信成功");
		return "redirect:"+Global.getAdminPath()+"/biz/message/bizMessageInfo/?repage";
	}

}