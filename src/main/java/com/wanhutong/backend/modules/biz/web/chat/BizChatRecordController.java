/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.chat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.sys.entity.Office;
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
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.service.chat.BizChatRecordService;

/**
 * 沟通记录：品类主管或客户专员，机构沟通Controller
 * @author Oy
 * @version 2018-05-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/chat/bizChatRecord")
public class BizChatRecordController extends BaseController {

	@Autowired
	private BizChatRecordService bizChatRecordService;
	
	@ModelAttribute
	public BizChatRecord get(@RequestParam(required=false) Integer id) {
		BizChatRecord entity = null;
		if (id!=null){
			entity = bizChatRecordService.get(id);
		}
		if (entity == null){
			entity = new BizChatRecord();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:chat:bizChatRecord:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizChatRecord bizChatRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizChatRecord> page = bizChatRecordService.findPage(new Page<BizChatRecord>(request, response), bizChatRecord); 
		model.addAttribute("page", page);
		if(bizChatRecord.getSource()!=null && bizChatRecord.getSource().equals("purchaser")){
			return "modules/biz/chat/bizChatRecordList";
		}
		return "modules/biz/chat/bizChatRecordSuppliList";
	}

	@RequiresPermissions("biz:chat:bizChatRecord:view")
	@RequestMapping(value = "form")
	public String form(BizChatRecord bizChatRecord, Model model) {
		model.addAttribute("bizChatRecord", bizChatRecord);
		if(bizChatRecord.getSource()!=null && bizChatRecord.getSource().equals("purchaser")){
			return "modules/biz/chat/bizChatRecordForm";
		}
		return "modules/biz/chat/bizChatRecordSuppliForm";
	}

	@RequiresPermissions("biz:chat:bizChatRecord:edit")
	@RequestMapping(value = "save")
	public String save(BizChatRecord bizChatRecord, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizChatRecord)){
			return form(bizChatRecord, model);
		}
		bizChatRecordService.save(bizChatRecord);
		addMessage(redirectAttributes, "保存沟通记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/chat/bizChatRecord/list?office.id="+bizChatRecord.getOffice().getId();
	}
	
	@RequiresPermissions("biz:chat:bizChatRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizChatRecord bizChatRecord, RedirectAttributes redirectAttributes) {
		bizChatRecordService.delete(bizChatRecord);
		addMessage(redirectAttributes, "删除沟通记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/chat/bizChatRecord/?repage";
	}

}