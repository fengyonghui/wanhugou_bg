/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.custom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.BuyerAdviser;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
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
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;

import java.util.List;

/**
 * 客户专员管理Controller 采购商客户专营关联
 * @author Ouyang Xiutian
 * @version 2018-01-13
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/custom/bizCustomCenterConsultant")
public class BizCustomCenterConsultantController extends BaseController {

	@Autowired
	private BizCustomCenterConsultantService bizCustomCenterConsultantService;

	@Autowired
	private OfficeService officeService;
    @Autowired
    private SystemService systemService;

    @RequiresPermissions("biz:common:commonImg:view")
    @RequestMapping(value = "form")
    public String form(BizCustomCenterConsultant bizCustomCenterConsultant, Model model) {
        model.addAttribute("entity", bizCustomCenterConsultant);
        return "modules/biz/common/commonImgForm";
    }

    @RequiresPermissions("biz:order:bizOrderDetail:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizCustomCenterConsultant bizCustomCenterConsultant, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizCustomCenterConsultant> page = bizCustomCenterConsultantService.findPage(new Page<BizCustomCenterConsultant>(request, response), bizCustomCenterConsultant);
        model.addAttribute("page", page);
        return "modules/biz/custom/bizCustomCenterConsultant/bizCustomCenterConsultantList";
    }

    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "returnConnIndex")
    public String returnConnIndex(BizCustomCenterConsultant bizCustomCenterConsultant,HttpServletRequest request, HttpServletResponse response, Model model){
//        Page<BizCustomCenterConsultant> page = bizCustomCenterConsultantService.findPage(new Page<BizCustomCenterConsultant>(request, response), bizCustomCenterConsultant);
        if(bizCustomCenterConsultant.getCenters()==null || bizCustomCenterConsultant.getConsultants()==null){
            model.addAttribute("entity", bizCustomCenterConsultant);
        }else {
            BizCustomCenterConsultant BCC = new BizCustomCenterConsultant();
            Office Centers = officeService.get(bizCustomCenterConsultant.getCenters());
            User Consultants = systemService.getUser(bizCustomCenterConsultant.getConsultants().getId());
            BCC.setCenters(Centers);//采购中心
            BCC.setConsultants(Consultants);//客户专员
            List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.findList(BCC);
            bizCustomCenterConsultant.setBccList(list);
            model.addAttribute("entity", bizCustomCenterConsultant);
        }
        return "modules/biz/custom/bizCustomCenterConsultantList";
    }

//    关联采购商
    @RequiresPermissions("sys:office:view")
    @RequestMapping(value = "connOfficeForm")
    public String connOfficeForm(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
        Office off = new Office();
        Office parentOff = new Office();
        String socID = DictUtils.getDictValue("部门", "sys_office_centerId","");
        String center = DictUtils.getDictValue("采购中心", "sys_office_type","");
        parentOff.setId(Integer.parseInt(socID));
        off.setParent(parentOff);
        off.setType(center);
        List<Office> officeList = officeService.queryList(off);
        user = systemService.getUser(user.getId());
        Integer aaa=user.getOffice().getId();
        BizCustomCenterConsultant bcc = bizCustomCenterConsultantService.get(aaa);
        if(bcc != null){
            bcc.setConsultants(systemService.getUser(user.getId()));
        }
        model.addAttribute("office", user);
        model.addAttribute("bcc", bcc);
        model.addAttribute("officeList", officeList);
        return "modules/biz/custom/bizCustomMembershipVolumeDATE";
    }

//    保存状态给 officeController
	@RequiresPermissions("sys:office:view")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(BizCustomCenterConsultant bizCustomCenterConsultant, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(bizCustomCenterConsultant == null || bizCustomCenterConsultant.getCustoms() == null || bizCustomCenterConsultant.getConsultants() == null){
			return "0";
		}
		BizCustomCenterConsultant BCC = bizCustomCenterConsultantService.get(bizCustomCenterConsultant.getCustoms().getId());
		bizCustomCenterConsultant.setIsNewRecord(true);
		if(BCC != null){
			bizCustomCenterConsultant.setIsNewRecord(false);
		}
		bizCustomCenterConsultant.setStatus("1");
		bizCustomCenterConsultantService.save(bizCustomCenterConsultant);
		return "1";
	}

//    @RequiresPermissions("biz:order:bizOrderDetail:edit")
//    @RequestMapping(value = "delete")
//    public String delete(BizCustomCenterConsultant bizCustomCenterConsultant, RedirectAttributes redirectAttributes) {
//        bizCustomCenterConsultant.getCustoms().getId();
//        addMessage(redirectAttributes, "删除订单详情成功");
//        return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+bizOrderDetail.getOrderHeader().getId();
//    }

}