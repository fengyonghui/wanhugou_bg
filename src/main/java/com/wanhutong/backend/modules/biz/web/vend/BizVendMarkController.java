/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.vend;

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
import com.wanhutong.backend.modules.biz.entity.vend.BizVendMark;
import com.wanhutong.backend.modules.biz.service.vend.BizVendMarkService;

/**
 * 供应商收藏Controller
 * @author zx
 * @version 2018-03-02
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/vend/bizVendMark")
public class BizVendMarkController extends BaseController {

	@Autowired
	private BizVendMarkService bizVendMarkService;
	
	@ModelAttribute
	public BizVendMark get(@RequestParam(required=false) Integer id) {
		BizVendMark entity = null;
		if (id!=null){
			entity = bizVendMarkService.get(id);
		}
		if (entity == null){
			entity = new BizVendMark();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:vend:bizVendMark:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizVendMark bizVendMark, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizVendMark> page = bizVendMarkService.findPage(new Page<BizVendMark>(request, response), bizVendMark); 
		model.addAttribute("page", page);
		return "modules/biz/vend/bizVendMarkList";
	}

	@RequiresPermissions("biz:vend:bizVendMark:view")
	@RequestMapping(value = "form")
	public String form(BizVendMark bizVendMark, Model model) {
		model.addAttribute("bizVendMark", bizVendMark);
		return "modules/biz/vend/bizVendMarkForm";
	}

	@RequiresPermissions("biz:vend:bizVendMark:edit")
	@RequestMapping(value = "save")
	public String save(BizVendMark bizVendMark, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizVendMark)){
			return form(bizVendMark, model);
		}
		bizVendMarkService.save(bizVendMark);
		addMessage(redirectAttributes, "保存供应商成功");
		return "redirect:"+Global.getAdminPath()+"/biz/vend/bizVendMark/?repage";
	}
	
	@RequiresPermissions("biz:vend:bizVendMark:edit")
	@RequestMapping(value = "delete")
	public String delete(BizVendMark bizVendMark, RedirectAttributes redirectAttributes) {
		bizVendMarkService.delete(bizVendMark);
		addMessage(redirectAttributes, "删除供应商成功");
		return "redirect:"+Global.getAdminPath()+"/biz/vend/bizVendMark/?repage";
	}

}