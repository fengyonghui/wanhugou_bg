/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.pay;

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
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;

/**
 * 交易记录Controller
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/pay/bizPayRecord")
public class BizPayRecordController extends BaseController {

	@Autowired
	private BizPayRecordService bizPayRecordService;
	
	@ModelAttribute
	public BizPayRecord get(@RequestParam(required=false) Integer id) {
		BizPayRecord entity = null;
		if (id!=null){
			entity = bizPayRecordService.get(id);
		}
		if (entity == null){
			entity = new BizPayRecord();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:pay:bizPayRecord:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPayRecord bizPayRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPayRecord> page = bizPayRecordService.findPage(new Page<BizPayRecord>(request, response), bizPayRecord); 
		model.addAttribute("page", page);
		return "modules/biz/pay/bizPayRecordList";
	}

	@RequiresPermissions("biz:pay:bizPayRecord:view")
	@RequestMapping(value = "form")
	public String form(BizPayRecord bizPayRecord, Model model) {
		model.addAttribute("bizPayRecord", bizPayRecord);
		return "modules/biz/pay/bizPayRecordForm";
	}

	@RequiresPermissions("biz:pay:bizPayRecord:edit")
	@RequestMapping(value = "save")
	public String save(BizPayRecord bizPayRecord, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPayRecord)){
			return form(bizPayRecord, model);
		}
		bizPayRecordService.save(bizPayRecord);
		addMessage(redirectAttributes, "保存交易记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/?repage";
	}
	
	@RequiresPermissions("biz:pay:bizPayRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPayRecord bizPayRecord, RedirectAttributes redirectAttributes) {
		bizPayRecord.setDelFlag(BizPayRecord.DEL_FLAG_DELETE);
		bizPayRecordService.delete(bizPayRecord);
		addMessage(redirectAttributes, "删除交易记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/?repage";
	}

	@RequiresPermissions("biz:pay:bizPayRecord:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizPayRecord bizPayRecord, RedirectAttributes redirectAttributes) {
		bizPayRecord.setDelFlag(BizPayRecord.DEL_FLAG_NORMAL);
		bizPayRecordService.delete(bizPayRecord);
		addMessage(redirectAttributes, "恢复交易记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/?repage";
	}

}