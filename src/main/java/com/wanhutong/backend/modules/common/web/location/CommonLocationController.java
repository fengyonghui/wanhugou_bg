/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.common.web.location;

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
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;

/**
 * 地理位置信息Controller
 * @author OuyangXiutian
 * @version 2017-12-21
 */
@Controller
@RequestMapping(value = "${adminPath}/common/location/commonLocation")
public class CommonLocationController extends BaseController {

	@Autowired
	private CommonLocationService commonLocationService;
	
	@ModelAttribute
	public CommonLocation get(@RequestParam(required=false) Integer id) {
		CommonLocation entity = null;
		if (id!=null){
			entity = commonLocationService.get(id);
		}
		if (entity == null){
			entity = new CommonLocation();
		}
		return entity;
	}
	
	@RequiresPermissions("common:location:commonLocation:view")
	@RequestMapping(value = {"list", ""})
	public String list(CommonLocation commonLocation, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CommonLocation> page = commonLocationService.findPage(new Page<CommonLocation>(request, response), commonLocation); 
		model.addAttribute("page", page);
		return "modules/common/location/commonLocationList";
	}

	@RequiresPermissions("common:location:commonLocation:view")
	@RequestMapping(value = "form")
	public String form(CommonLocation commonLocation, Model model) {
		model.addAttribute("commonLocation", commonLocation);
		return "modules/common/location/commonLocationForm";
	}

	@RequiresPermissions("common:location:commonLocation:edit")
	@RequestMapping(value = "save")
	public String save(CommonLocation commonLocation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, commonLocation)){
			return form(commonLocation, model);
		}
		commonLocationService.save(commonLocation);
		addMessage(redirectAttributes, "保存位置信息成功");
		return "redirect:"+Global.getAdminPath()+"/common/location/commonLocation/?repage";
	}
	
	@RequiresPermissions("common:location:commonLocation:edit")
	@RequestMapping(value = "delete")
	public String delete(CommonLocation commonLocation, RedirectAttributes redirectAttributes) {
		commonLocationService.delete(commonLocation);
		addMessage(redirectAttributes, "删除位置信息成功");
		return "redirect:"+Global.getAdminPath()+"/common/location/commonLocation/?repage";
	}

}