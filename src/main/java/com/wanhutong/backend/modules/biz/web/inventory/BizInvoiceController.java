/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * 发货单Controller
 * @author 张腾飞
 * @version 2018-03-05
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizInvoice")
public class BizInvoiceController extends BaseController {

	@Autowired
	private BizInvoiceService bizInvoiceService;
	@Autowired
	private BizLogisticsService bizLogisticsService;
	@Autowired
    private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
    private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
    private BizOrderDetailService bizOrderDetailService;
	@Autowired
    private BizRequestDetailService bizRequestDetailService;
	@Autowired
    private BizInventoryInfoService bizInventoryInfoService;
	
	@ModelAttribute
	public BizInvoice get(@RequestParam(required=false) Integer id) {
		BizInvoice entity = null;
		if (id!=null){
			entity = bizInvoiceService.get(id);
		}
		if (entity == null){
			entity = new BizInvoice();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizInvoice:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInvoice bizInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizInvoice> page = bizInvoiceService.findPage(new Page<BizInvoice>(request, response), bizInvoice);
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizInvoiceList";
	}

	@ResponseBody
	@RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "findOrderDetail")
    public List<BizOrderDetail> findOrderDetail(Integer id){
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(id);
        BizOrderDetail bizOrderDetail = new BizOrderDetail();
        bizOrderDetail.setOrderHeader(bizOrderHeader);
        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
        return orderDetailList;
    }

    @ResponseBody
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "findRequestDetail")
    public List<BizRequestDetail> findRequestDetail(Integer id){
        BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(id);
        BizRequestDetail bizRequestDetail = new BizRequestDetail();
        bizRequestDetail.setRequestHeader(bizRequestHeader);
        List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
        return requestDetailList;
    }

	@RequiresPermissions("biz:inventory:bizInvoice:view")
	@RequestMapping(value = "form")
	public String form(BizInvoice bizInvoice, Model model) {
        BizLogistics bizLogistics = new BizLogistics();
		List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
		model.addAttribute("logisticsList",logisticsList);
        List<BizOrderHeader> orderList = bizOrderHeaderService.findList(new BizOrderHeader());
        List<BizRequestHeader> requestList = bizRequestHeaderService.findList(new BizRequestHeader());
        List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(new BizInventoryInfo());
        model.addAttribute("invInfoList",invInfoList);
        model.addAttribute("orderList",orderList);
        model.addAttribute("requestList",requestList);
		model.addAttribute("bizInvoice", bizInvoice);
		model.addAttribute("bizOrderHeader",new BizOrderHeader());
		return "modules/biz/inventory/bizInvoiceForm";
	}

	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "save")
	public String save(BizInvoice bizInvoice,String source,String bizStatu,String ship, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInvoice)){
			return form(bizInvoice, model);
		}
		bizInvoiceService.save(bizInvoice,bizStatu);
		addMessage(redirectAttributes, "保存发货单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInvoice/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInvoice bizInvoice, RedirectAttributes redirectAttributes) {
		bizInvoiceService.delete(bizInvoice);
		addMessage(redirectAttributes, "删除发货单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInvoice/?repage";
	}

}