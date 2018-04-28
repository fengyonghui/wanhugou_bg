/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
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
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;

/**
 * 备货清单详细信息Controller
 * @author liuying
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestDetail")
public class BizRequestDetailController extends BaseController {

	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	
	@ModelAttribute
	public BizRequestDetail get(@RequestParam(required=false) Integer id) {
		BizRequestDetail entity = null;
		if (id!=null){
			entity = bizRequestDetailService.get(id);
		}
		if (entity == null){
			entity = new BizRequestDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:request:bizRequestDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizRequestDetail bizRequestDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizRequestDetail> page = bizRequestDetailService.findPage(new Page<BizRequestDetail>(request, response), bizRequestDetail); 
		model.addAttribute("page", page);
		return "modules/biz/request/bizRequestDetailList";
	}

	@RequiresPermissions("biz:request:bizRequestDetail:view")
	@RequestMapping(value = "form")
	public String form(BizRequestDetail bizRequestDetail, Model model) {
		model.addAttribute("bizRequestDetail", bizRequestDetail);
		return "modules/biz/request/bizRequestDetailForm";
	}

	@RequiresPermissions("biz:request:bizRequestDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizRequestDetail bizRequestDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizRequestDetail)){
			return form(bizRequestDetail, model);
		}
		bizRequestDetailService.save(bizRequestDetail);
		addMessage(redirectAttributes, "保存备货清单详细信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestDetail/?repage";
	}


	@ResponseBody
	@RequiresPermissions("biz:request:bizRequestDetail:edit")
	@RequestMapping(value = "saveRequestDetail")
	public String saveRequestDetail(Integer detailId, Double money) {
		String flag = "";
		try {
			BizRequestDetail bizRequestDetail = bizRequestDetailService.get(detailId);
			bizRequestDetail.setUnitPrice(money);
			bizRequestDetailService.save(bizRequestDetail);
			flag = "ok";

		} catch (Exception e) {
			flag = "error";
			logger.error(e.getMessage());
		}
		return flag;
	}
	
	@RequiresPermissions("biz:request:bizRequestDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BizRequestDetail bizRequestDetail, RedirectAttributes redirectAttributes) {
		bizRequestDetailService.delete(bizRequestDetail);
		addMessage(redirectAttributes, "删除备货清单详细信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestDetail/?repage";
	}
	@ResponseBody
	@RequiresPermissions("biz:request:bizRequestDetail:edit")
	@RequestMapping(value = "delItem")
	public String delItem(BizRequestDetail bizRequestDetail, RedirectAttributes redirectAttributes) {
		String data="ok";
		try {
			bizRequestDetailService.delete(bizRequestDetail);
		}catch (Exception e){
			logger.error(e.getMessage());
			data="error"; }
		return data;


	}

}