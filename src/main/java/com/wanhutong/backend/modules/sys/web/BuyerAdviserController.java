package com.wanhutong.backend.modules.sys.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.sys.entity.BuyerAdviser;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.BuyerAdviserService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;

/**
 * * 
* <p>Title: BuyerAdviserController</p>
* <p>Description: 采购商采购顾问关联</p>
* <p>Company: WHT</p> 
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/buyerAdviser")
public class BuyerAdviserController extends BaseController {

	@Autowired
	private BuyerAdviserService buyerAdviserService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private SystemService systemService;
	
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(BuyerAdviser buyerAdviser, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(buyerAdviser == null || buyerAdviser.getCustId() == null || buyerAdviser.getConsultantId() == null){
			return "0";
		}
		BuyerAdviser adviser = buyerAdviserService.get(buyerAdviser.getCustId());
		buyerAdviser.setIsNewRecord(true);
		if (adviser != null) {
			buyerAdviser.setIsNewRecord(false);
			buyerAdviserService.delete(adviser);
		}
		buyerAdviser.setStatus("1");
		buyerAdviserService.save(buyerAdviser);
		return "1";
		
	}
	
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "interrelatedForm")
	public String interrelatedForm(Office office, HttpServletRequest request, HttpServletResponse response, Model model) {
		Office off = new Office();
//		Office parentOff = new Office();
//		String socID = DictUtils.getDictValue("部门", "sys_office_centerId","");
		String center = DictUtils.getDictValue("采购中心", "sys_office_type","");
//		parentOff.setId(Integer.valueOf(socID));
//		off.setParent(parentOff);
		off.setType(center);
		off.setCustomerTypeTen(OfficeTypeEnum.WITHCAPITAL.getType());
		off.setCustomerTypeEleven(OfficeTypeEnum.NETWORKSUPPLY.getType());
		List<Office> officeList = officeService.queryCenterList(off);
		office = officeService.get(office.getId());
		BuyerAdviser buyerAdviser =null;
		if(office!=null){
			buyerAdviser = buyerAdviserService.get(office.getId());
			if(buyerAdviser != null){
				if(!buyerAdviser.getStatus().equals("0")){
					User user = systemService.getUser(buyerAdviser.getConsultantId());
					if(user!=null && user.getName()!=null && !user.getName().equals("") && !user.getDelFlag().equals("0")){
						buyerAdviser.setConsultantName(user.getName());
					}else{
						buyerAdviser=null;
					}
				}else{
					buyerAdviser=null;
				}
			}
		}
		model.addAttribute("office", office);
		model.addAttribute("buyerAdviser", buyerAdviser);
		model.addAttribute("officeList", officeList);
		return "modules/sys/interrelatedForm";
	}
}
