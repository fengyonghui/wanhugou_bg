/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizDetailInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
import java.util.ArrayList;
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
    private BizDetailInvoiceService bizDetailInvoiceService;
	@Autowired
    private BizSkuInfoService bizSkuInfoService;



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
//	    bizInvoice.setBizStatus(Integer.parseInt(bizStatu));
//	    bizInvoice.setShip(Integer.parseInt(ship));
        Page<BizInvoice> page = bizInvoiceService.findPage(new Page<BizInvoice>(request, response), bizInvoice);
		model.addAttribute("page", page);
//		model.addAttribute("ship",ship);
//		model.addAttribute("bizStatu",bizStatu);
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
	public String form(BizInvoice bizInvoice, Model model,String ship,String bizStatu) {
        BizLogistics bizLogistics = new BizLogistics();
		List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
		model.addAttribute("logisticsList",logisticsList);
//        List<BizOrderHeader> orderList = bizOrderHeaderService.findList(new BizOrderHeader());
//        List<BizRequestHeader> requestList = bizRequestHeaderService.findList(new BizRequestHeader());
//        List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(new BizInventoryInfo());
//        model.addAttribute("invInfoList",invInfoList);
//        model.addAttribute("orderList",orderList);
//        model.addAttribute("requestList",requestList);
		model.addAttribute("bizInvoice", bizInvoice);
		model.addAttribute("bizOrderHeader",new BizOrderHeader());
		if(bizInvoice.getShip() != null && bizInvoice.getShip()==1 ){
			model.addAttribute("bizRequestHeader",new BizRequestHeader());
		    return "modules/biz/inventory/bizInvoiceRequestForm";
        }
		return "modules/biz/inventory/bizInvoiceForm";
	}

    /**
     * 订单所属发货单详情
     * @param bizInvoice
     * @param model
     * @return
     */
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "invoiceOrderDetail")
    public String invoiceOrderDetail(BizInvoice bizInvoice, Model model) {

        BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
        bizDetailInvoice.setInvoice(bizInvoice);
        List<BizDetailInvoice> DetailInvoiceList = bizDetailInvoiceService.findList(bizDetailInvoice);
        List<BizOrderHeader> orderHeaderList = new ArrayList<>();
        if (DetailInvoiceList != null && !DetailInvoiceList.isEmpty()){
            for (BizDetailInvoice detailInvoice:DetailInvoiceList) {
                BizOrderHeader bizorderHeader = detailInvoice.getOrderHeader();
                BizOrderHeader orderHeader = bizOrderHeaderService.get(bizorderHeader.getId());
                BizOrderDetail bizOrderDetail = new BizOrderDetail();
                bizOrderDetail.setOrderHeader(orderHeader);
                List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                if (orderDetailList != null && !orderDetailList.isEmpty()){
                    for (BizOrderDetail orderDetail:orderDetailList) {
                        BizSkuInfo sku = bizSkuInfoService.get(orderDetail.getSkuInfo());
                        BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                        orderDetail.setSkuInfo(skuInfo);
                    }
                    orderHeader.setOrderDetailList(orderDetailList);
                }
                orderHeaderList.add(orderHeader);
            }
        }
        model.addAttribute("orderHeaderList",orderHeaderList);
        model.addAttribute("bizInvoice", bizInvoice);
        return "modules/biz/inventory/bizInvoiceDeForm";
    }

    /**
     * 备货单所属发货单详情
     * @param bizInvoice
     * @param model
     * @return
     */
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "invoiceRequestDetail")
    public String invoiceRequestDetail(BizInvoice bizInvoice, Model model) {

        BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
        bizDetailInvoice.setInvoice(bizInvoice);
        List<BizDetailInvoice> DetailInvoiceList = bizDetailInvoiceService.findList(bizDetailInvoice);
        List<BizRequestHeader> requestHeaderList = new ArrayList<>();
        if (DetailInvoiceList != null && !DetailInvoiceList.isEmpty()){
            for (BizDetailInvoice detailInvoice:DetailInvoiceList) {
                BizRequestHeader bizRequestHeader = detailInvoice.getRequestHeader();
                BizRequestHeader requestHeader = bizRequestHeaderService.get(bizRequestHeader.getId());
                BizRequestDetail bizRequestDetail = new BizRequestDetail();
                bizRequestDetail.setRequestHeader(requestHeader);
                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                if (requestDetailList != null && !requestDetailList.isEmpty()){
                    for (BizRequestDetail requestDetail:requestDetailList) {
                        BizSkuInfo skuInfo = bizSkuInfoService.get(requestDetail.getSkuInfo());
                        BizSkuInfo sku = bizSkuInfoService.findListProd(skuInfo);
                        requestDetail.setSkuInfo(sku);
                    }
                    requestHeader.setRequestDetailList(requestDetailList);
                }
                requestHeaderList.add(requestHeader);
            }
        }
        model.addAttribute("requestHeaderList",requestHeaderList);
        model.addAttribute("bizInvoice", bizInvoice);
        return "modules/biz/inventory/bizInvoiceReqDeForm";
    }

	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "save")
	public String save(BizInvoice bizInvoice, Model model, RedirectAttributes redirectAttributes) {
		/*if (!beanValidator(model, bizInvoice)){
			return form(bizInvoice, model);
		}*/
		bizInvoiceService.save(bizInvoice);
		addMessage(redirectAttributes, "保存发货单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInvoice/?repage&bizStatus="+bizInvoice.getBizStatus()+"&ship="+bizInvoice.getShip();
	}
	
	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInvoice bizInvoice, RedirectAttributes redirectAttributes) {
		bizInvoiceService.delete(bizInvoice);
		addMessage(redirectAttributes, "删除发货单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInvoice/?repage";
	}

}