/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web.office;

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
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;

import java.util.List;

/**
 * 地址管理(公司+详细地址)Controller
 * @author OuyangXiutian
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/office/sysOfficeAddress")
public class SysOfficeAddressController extends BaseController {

	@Autowired
	private SysOfficeAddressService sysOfficeAddressService;
	
	@ModelAttribute
	public SysOfficeAddress get(@RequestParam(required=false) Integer id) {
		SysOfficeAddress entity = null;
		if (id!=null){
			entity = sysOfficeAddressService.get(id);
		}
		if (entity == null){
			entity = new SysOfficeAddress();
		}
		return entity;
	}

	@RequiresPermissions("sys:office:sysOfficeAddress:view")
	@RequestMapping(value = "findAddrByOffice")
	public List<SysOfficeAddress> findAddrByOffice(SysOfficeAddress sysOfficeAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<SysOfficeAddress> list = sysOfficeAddressService.findList(sysOfficeAddress);
		return list;
	}

	@RequiresPermissions("sys:office:sysOfficeAddress:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysOfficeAddress sysOfficeAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysOfficeAddress> page = sysOfficeAddressService.findPage(new Page<SysOfficeAddress>(request, response), sysOfficeAddress); 
		model.addAttribute("page", page);
		return "modules/sys/office/sysOfficeAddressList";
	}

	@RequiresPermissions("sys:office:sysOfficeAddress:view")
	@RequestMapping(value = "form")
	public String form(SysOfficeAddress sysOfficeAddress, Model model) {
		model.addAttribute("entity", sysOfficeAddress);
		return "modules/sys/office/sysOfficeAddressForm";
	}

	@RequiresPermissions("sys:office:sysOfficeAddress:edit")
	@RequestMapping(value = "save")
	public String save(SysOfficeAddress sysOfficeAddress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysOfficeAddress)){
			return form(sysOfficeAddress, model);
		}
		sysOfficeAddressService.save(sysOfficeAddress);
		addMessage(redirectAttributes, "保存地址信息成功");
		return "redirect:"+Global.getAdminPath()+"/sys/office/sysOfficeAddress/?repage";
	}
	
	@RequiresPermissions("sys:office:sysOfficeAddress:edit")
	@RequestMapping(value = "delete")
	public String delete(SysOfficeAddress sysOfficeAddress, RedirectAttributes redirectAttributes) {
		sysOfficeAddressService.delete(sysOfficeAddress);
		addMessage(redirectAttributes, "删除地址信息成功");
		return "redirect:"+Global.getAdminPath()+"/sys/office/sysOfficeAddress/?repage";
	}

}