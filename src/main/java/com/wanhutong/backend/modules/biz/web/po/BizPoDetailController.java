/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

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
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;

/**
 * 采购订单信息信息Controller
 * @author liuying
 * @version 2017-12-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizPoDetail")
public class BizPoDetailController extends BaseController {

	@Autowired
	private BizPoDetailService bizPoDetailService;
	
	@ModelAttribute
	public BizPoDetail get(@RequestParam(required=false) Integer id) {
		BizPoDetail entity = null;
		if (id!=null){
			entity = bizPoDetailService.get(id);
		}
		if (entity == null){
			entity = new BizPoDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:po:bizPoDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPoDetail bizPoDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPoDetail> page = bizPoDetailService.findPage(new Page<BizPoDetail>(request, response), bizPoDetail); 
		model.addAttribute("page", page);
		return "modules/biz/po/bizPoDetailList";
	}

	@RequiresPermissions("biz:po:bizPoDetail:view")
	@RequestMapping(value = "form")
	public String form(BizPoDetail bizPoDetail, Model model) {
		model.addAttribute("bizPoDetail", bizPoDetail);
		return "modules/biz/po/bizPoDetailForm";
	}

	@RequiresPermissions("biz:po:bizPoDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizPoDetail bizPoDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPoDetail)){
			return form(bizPoDetail, model);
		}
		bizPoDetailService.save(bizPoDetail);
		addMessage(redirectAttributes, "保存采购订单详细信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoDetail/?repage";
	}
	
	@RequiresPermissions("biz:po:bizPoDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPoDetail bizPoDetail, RedirectAttributes redirectAttributes) {
		bizPoDetailService.delete(bizPoDetail);
		addMessage(redirectAttributes, "删除采购订单详细信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoDetail/?repage";
	}

}