/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.shelf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
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
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;

import java.util.List;

/**
 * 运营货架信息Controller
 * @author liuying
 * @version 2017-12-19
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/shelf/bizOpShelfInfo")
public class BizOpShelfInfoController extends BaseController {

	@Autowired
	private BizOpShelfInfoService bizOpShelfInfoService;
	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@ModelAttribute
	public BizOpShelfInfo get(@RequestParam(required=false) Integer id) {
		BizOpShelfInfo entity = null;
		if (id!=null){
			entity = bizOpShelfInfoService.get(id);
			BizOpShelfSku bizOpShelfSku = new BizOpShelfSku();
			bizOpShelfSku.setOpShelfInfo(entity);
			List<BizOpShelfSku> opShelfSkusList = bizOpShelfSkuService.findList(bizOpShelfSku);
			entity.setOpShelfSkusList(opShelfSkusList);
		}
		if (entity == null){
			entity = new BizOpShelfInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOpShelfInfo bizOpShelfInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOpShelfInfo> page = bizOpShelfInfoService.findPage(new Page<BizOpShelfInfo>(request, response), bizOpShelfInfo); 
		model.addAttribute("page", page);
		return "modules/biz/shelf/bizOpShelfInfoList";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfInfo:view")
	@RequestMapping(value = "form")
	public String form(BizOpShelfInfo bizOpShelfInfo, Model model) {
		model.addAttribute("bizOpShelfInfo", bizOpShelfInfo);
		return "modules/biz/shelf/bizOpShelfInfoForm";
	}

	@RequiresPermissions("biz:shelf:bizOpShelfInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizOpShelfInfo bizOpShelfInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOpShelfInfo)){
			return form(bizOpShelfInfo, model);
		}
		bizOpShelfInfoService.save(bizOpShelfInfo);
		addMessage(redirectAttributes, "保存货架信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/?repage";
	}
	
	@RequiresPermissions("biz:shelf:bizOpShelfInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOpShelfInfo bizOpShelfInfo, RedirectAttributes redirectAttributes) {
		bizOpShelfInfoService.delete(bizOpShelfInfo);
		addMessage(redirectAttributes, "删除货架信息成功");
		return "redirect:"+Global.getAdminPath()+"/biz/shelf/bizOpShelfInfo/?repage";
	}

}