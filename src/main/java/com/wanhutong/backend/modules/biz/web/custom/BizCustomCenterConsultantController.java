/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.custom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
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
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;

import java.util.List;

/**
 * 客户专员管理Controller
 * @author Ouyang Xiutian
 * @version 2018-01-13
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/custom/bizCustomCenterConsultant")
public class BizCustomCenterConsultantController extends BaseController {

	@Autowired
	private BizCustomCenterConsultantService bizCustomCenterConsultantService;

	@Autowired
	private OfficeService officeService;

	@ModelAttribute
	public BizCustomCenterConsultant get(@RequestParam(required=false) Integer id) {
		BizCustomCenterConsultant entity = null;
		if (id!=null){
			entity = bizCustomCenterConsultantService.get(id);
		}
		if (entity == null){
			entity = new BizCustomCenterConsultant();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCustomCenterConsultant bizCustomCenterConsultant, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCustomCenterConsultant> page = bizCustomCenterConsultantService.findPage(new Page<BizCustomCenterConsultant>(request, response), bizCustomCenterConsultant); 
		model.addAttribute("page", page);
		return "modules/biz/custom/bizCustomCenterConsultantList";
	}

	@RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
	@RequestMapping(value = "form")
	public String form(BizCustomCenterConsultant bizCustomCenterConsultant, Model model) {
//		查询该采购专员下的采购商
		String type=OfficeTypeEnum.CUSTOMER.getType();//"6"
		List<Office> list = officeService.filerOffice(null, OfficeTypeEnum.stateOf(type));
		bizCustomCenterConsultant.setOfficeList(list);
		model.addAttribute("entity", bizCustomCenterConsultant);
		return "modules/biz/custom/bizCustomCenterConsultantForm";
	}

	@RequiresPermissions("biz:custom:bizCustomCenterConsultant:edit")
	@RequestMapping(value = "save")
	public String save(BizCustomCenterConsultant bizCustomCenterConsultant, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizCustomCenterConsultant)){
			return form(bizCustomCenterConsultant, model);
		}
		bizCustomCenterConsultantService.save(bizCustomCenterConsultant);
		addMessage(redirectAttributes, "保存客户专员成功");
		return "redirect:"+Global.getAdminPath()+"/biz/custom/bizCustomCenterConsultant/form?repage";
	}
	
	@RequiresPermissions("biz:custom:bizCustomCenterConsultant:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCustomCenterConsultant bizCustomCenterConsultant, RedirectAttributes redirectAttributes) {
		Office customs = bizCustomCenterConsultant.getCustoms();
		bizCustomCenterConsultant.setCustoms(customs);
		bizCustomCenterConsultantService.delete(bizCustomCenterConsultant);
		addMessage(redirectAttributes, "删除客户专员成功");
		return "redirect:"+Global.getAdminPath()+"/biz/custom/bizCustomCenterConsultant/?repage";
	}

//	新增会员量 Method
	@RequiresPermissions("biz:custom:bizCustomCenterConsultant:view")
	@RequestMapping(value = "memberForm")
	public String memberForm(BizCustomCenterConsultant bizCustomCenterConsultant, Model model) {
		model.addAttribute("entity", bizCustomCenterConsultant);
		return "modules/biz/custom/bizCustomMembershipVolumeDATE";
	}
}