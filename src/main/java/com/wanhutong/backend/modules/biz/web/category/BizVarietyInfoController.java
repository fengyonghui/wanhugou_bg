/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.category;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
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
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;

/**
 * 产品种类管理Controller
 * @author liuying
 * @version 2018-02-24
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/category/bizVarietyInfo")
public class BizVarietyInfoController extends BaseController {

	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	
	@ModelAttribute
	public BizVarietyInfo get(@RequestParam(required=false) Integer id) {
		BizVarietyInfo entity = null;
		if (id!=null){
			entity = bizVarietyInfoService.get(id);
		}
		if (entity == null){
			entity = new BizVarietyInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:category:bizVarietyInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizVarietyInfo bizVarietyInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizVarietyInfo> page = bizVarietyInfoService.findPage(new Page<BizVarietyInfo>(request, response), bizVarietyInfo); 
		model.addAttribute("page", page);
		return "modules/biz/category/bizVarietyInfoList";
	}

	@RequiresPermissions("biz:category:bizVarietyInfo:view")
	@RequestMapping(value = "form")
	public String form(BizVarietyInfo bizVarietyInfo, Model model) {
		model.addAttribute("bizVarietyInfo", bizVarietyInfo);
		return "modules/biz/category/bizVarietyInfoForm";
	}

	@RequiresPermissions("biz:category:bizVarietyInfo:edit")
	@RequestMapping(value = "save")
	public String save(BizVarietyInfo bizVarietyInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizVarietyInfo)){
			return form(bizVarietyInfo, model);
		}
		String code=HanyuPinyinHelper.getFirstLetters(bizVarietyInfo.getName(), HanyuPinyinCaseType.UPPERCASE);
		bizVarietyInfo.setCode(code);
		bizVarietyInfoService.save(bizVarietyInfo);

		addMessage(redirectAttributes, "保存产品种类管理成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizVarietyInfo/?repage";
	}
	
	@RequiresPermissions("biz:category:bizVarietyInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BizVarietyInfo bizVarietyInfo, RedirectAttributes redirectAttributes) {
		bizVarietyInfoService.delete(bizVarietyInfo);
		addMessage(redirectAttributes, "删除产品种类管理成功");
		return "redirect:"+Global.getAdminPath()+"/biz/category/bizVarietyInfo/?repage";
	}

}