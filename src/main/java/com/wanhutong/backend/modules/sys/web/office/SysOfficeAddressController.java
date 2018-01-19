/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.web.office;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.service.office.SysOfficeAddressService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

	@ResponseBody
	@RequiresPermissions("sys:office:sysOfficeAddress:view")
	@RequestMapping(value = "findAddrByOffice")
	public SysOfficeAddress findAddrByOffice(SysOfficeAddress sysOfficeAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		SysOfficeAddress add = null;
	//	BeanUtils.copyProperties(sysOfficeAddress,add);
		sysOfficeAddress.setType(null);
	//	add.setType(null);
	//	add.setOffice(sysOfficeAddress.getOffice());
		List<SysOfficeAddress> list = sysOfficeAddressService.findList(sysOfficeAddress);
		if(list!=null && list.size()>0){
			for (SysOfficeAddress a : list) {
				if(a.getDeFaultStatus()==OrderHeaderBizStatusEnum.ORDER_DEFAULTSTATUS.getState()){//1
					add = list.get(0);
					break;
				}
			}
		}
		return add;
	}

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
	@RequestMapping(value = {"list", ""})
	public String list(SysOfficeAddress sysOfficeAddress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysOfficeAddress> page = sysOfficeAddressService.findPage(new Page<SysOfficeAddress>(request, response), sysOfficeAddress); 
		model.addAttribute("page", page);
		return "modules/sys/office/sysOfficeAddressList";
	}

	@RequiresPermissions("sys:office:sysOfficeAddress:view")
	@RequestMapping(value = "form")
	public String form(SysOfficeAddress sysOfficeAddress, Model model) {
		sysOfficeAddress.getDeFaultStatus();
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
		if(sysOfficeAddress.getFlag()!=null && sysOfficeAddress.getFlag().equals("order")){
			Integer ohId=sysOfficeAddress.getOhId();
			if(ohId==null){
				ohId=0;
			}
			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+ohId+"&customer.id="+sysOfficeAddress.getOffice().getId();
		}
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