/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.common;

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
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;

/**
 * 通用图片Controller
 * @author zx
 * @version 2017-12-18
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/common/commonImg")
public class CommonImgController extends BaseController {

	@Autowired
	private CommonImgService commonImgService;
	
	@ModelAttribute
	public CommonImg get(@RequestParam(required=false) Integer id) {
		CommonImg entity = null;
		if (id!=null){
			entity = commonImgService.get(id);
		}
		if (entity == null){
			entity = new CommonImg();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:common:commonImg:view")
	@RequestMapping(value = {"list", ""})
	public String list(CommonImg commonImg, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CommonImg> page = commonImgService.findPage(new Page<CommonImg>(request, response), commonImg); 
		model.addAttribute("page", page);
		return "modules/biz/common/commonImgList";
	}

	@RequiresPermissions("biz:common:commonImg:view")
	@RequestMapping(value = "form")
	public String form(CommonImg commonImg, Model model) {
		model.addAttribute("commonImg", commonImg);
		return "modules/biz/common/commonImgForm";
	}

	@RequiresPermissions("biz:common:commonImg:edit")
	@RequestMapping(value = "save")
	public String save(CommonImg commonImg, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, commonImg)){
			return form(commonImg, model);
		}
		commonImgService.save(commonImg);
		addMessage(redirectAttributes, "保存图片成功");
		return "redirect:"+Global.getAdminPath()+"/biz/common/commonImg/?repage";
	}
	
	@RequiresPermissions("biz:common:commonImg:edit")
	@RequestMapping(value = "delete")
	public String delete(CommonImg commonImg, RedirectAttributes redirectAttributes) {
		commonImgService.delete(commonImg);
		addMessage(redirectAttributes, "删除图片成功");
		return "redirect:"+Global.getAdminPath()+"/biz/common/commonImg/?repage";
	}

}