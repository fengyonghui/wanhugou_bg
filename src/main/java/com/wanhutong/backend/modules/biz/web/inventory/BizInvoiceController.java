/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizDetailInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
    private SystemService systemService;
	@Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired
    private DictService dictService;

    private static final String DEF_EN_NAME = "shipper";


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
	public String form(BizInvoice bizInvoice, Model model) {
        BizLogistics bizLogistics = new BizLogistics();
		List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
        List<User> userList = systemService.findUserByRoleEnName(DEF_EN_NAME);
        User user = UserUtils.getUser();
        model.addAttribute("logisticsList",logisticsList);
//        List<BizOrderHeader> orderList = bizOrderHeaderService.findList(new BizOrderHeader());
//        List<BizRequestHeader> requestList = bizRequestHeaderService.findList(new BizRequestHeader());
//        List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(new BizInventoryInfo());
//        model.addAttribute("invInfoList",invInfoList);
//        model.addAttribute("orderList",orderList);
//        model.addAttribute("requestList",requestList);
        if (StringUtils.isBlank(bizInvoice.getCarrier())) {
            bizInvoice.setCarrier(String.valueOf(user.getId()));
        }

		model.addAttribute("bizInvoice", bizInvoice);
		model.addAttribute("userList", userList);
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
    public String invoiceOrderDetail(BizInvoice bizInvoice,String source, Model model) {

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

        List<User> userList = systemService.findUserByRoleEnName(DEF_EN_NAME);
        User user = UserUtils.getUser();
        if (StringUtils.isBlank(bizInvoice.getCarrier())) {
            bizInvoice.setCarrier(String.valueOf(user.getId()));
        }
        BizLogistics bizLogistics = new BizLogistics();
        List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
        model.addAttribute("logisticsList",logisticsList);
        model.addAttribute("userList", userList);
        model.addAttribute("source",source);
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
    public String invoiceRequestDetail(BizInvoice bizInvoice,String source, Model model) {

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
        BizLogistics bizLogistics = new BizLogistics();
        List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
        model.addAttribute("logisticsList",logisticsList);
        model.addAttribute("source",source);
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

    /**
     * 导出
     * */
    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = "exportList",method = RequestMethod.POST)
    public String exportList(BizInvoice bizInvoice, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileName = "订单发货信息数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            Page<BizInvoice> page = bizInvoiceService.findPage(new Page<BizInvoice>(request, response), bizInvoice);
            //1订单
            List<List<String>> data = new ArrayList<List<String>>();
            //2商品
            List<List<String>> detailData = new ArrayList<List<String>>();
            for (BizInvoice invoice : page.getList()) {
                List<String> headData = new ArrayList();
                headData.add(String.valueOf(invoice.getSendNumber()));
                if(invoice.getLogistics()!=null && invoice.getLogistics().getName()!=null){
                    headData.add(String.valueOf(invoice.getLogistics().getName()));
                }else{
                    headData.add("");
                }
                if(invoice.getFreight()!=null){
                    headData.add(String.valueOf(invoice.getFreight()));
                }else{
                    headData.add("");
                }
                if(invoice.getOperation()!=null){
                    headData.add(String.valueOf(invoice.getOperation()));
                }else{
                    headData.add("");
                }
                if(invoice.getValuePrice()!=null){
                    headData.add(String.valueOf(invoice.getValuePrice()));
                }else{
                    headData.add("");
                }
                NumberFormat nf = NumberFormat.getPercentInstance();
                //设置2位小数、四舍五入
                nf.setMaximumFractionDigits(2);
                nf.setRoundingMode(RoundingMode.HALF_UP);
                Double price=0.0;
                if(invoice.getFreight()!=null && invoice.getValuePrice()!=null){
                    price=invoice.getFreight()*100/invoice.getValuePrice();
                }//运费/货值
                headData.add(String.valueOf(nf.format(price)));
                if(invoice.getCarrier()!=null){
                    headData.add(String.valueOf(invoice.getCarrier()));
                }else{
                    headData.add("");
                }
                Dict dict = new Dict();
                dict.setDescription("物流结算方式");
                dict.setType("biz_settlement_status");
                List<Dict> dictList = dictService.findList(dict);
                for (Dict di : dictList) {
                    if (di.getValue().equals(String.valueOf(invoice.getSettlementStatus()))) {
                        //结算方式
                        headData.add(String.valueOf(di.getLabel()));
                        break;
                    }
                }
                headData.add(String.valueOf(invoice.getSendDate()));
                data.add(headData);
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(invoice);
                List<BizDetailInvoice> DetailInvoiceList = bizDetailInvoiceService.findList(bizDetailInvoice);
                if(DetailInvoiceList.size()!=0){
                    for (BizDetailInvoice detailInvoice : DetailInvoiceList) {
                        BizOrderDetail bizOrderDetail = new BizOrderDetail();
                        List<BizOrderDetail> orderDetailList =null;
                        BizOrderHeader orderHeader = bizOrderHeaderService.get(detailInvoice.getOrderHeader().getId());
                        if(orderHeader!=null){
                            bizOrderDetail.setOrderHeader(orderHeader);
                            orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                        }
                        if(orderDetailList!=null && orderDetailList.size()!=0){
                            for (BizOrderDetail orderDetail:orderDetailList) {
                                List<String> detaData = new ArrayList();
                                BizSkuInfo sku = bizSkuInfoService.get(orderDetail.getSkuInfo());
                                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                                orderDetail.setSkuInfo(skuInfo);
                                detaData.add(String.valueOf(invoice.getSendNumber()));
                                if(invoice.getLogistics()!=null && invoice.getLogistics().getName()!=null){
                                    detaData.add(String.valueOf(invoice.getLogistics().getName()));
                                }else{
                                    detaData.add("");
                                }
                                if(orderHeader!=null){
                                    detaData.add(String.valueOf(orderDetail.getOrderHeader().getOrderNum()));
                                    if(orderHeader.getCustomer()!=null && orderHeader.getCustomer().getName()!=null){
                                        detaData.add(String.valueOf(orderHeader.getCustomer().getName()));
                                    }else{
                                        detaData.add("");
                                    }
                                }else{
                                    detaData.add("");
                                    detaData.add("");
                                }
                                Dict detadict = new Dict();
                                detadict.setDescription("业务状态");
                                detadict.setType("biz_order_status");
                                List<Dict> detadictList = dictService.findList(detadict);
                                for (Dict di : detadictList) {
                                    if (di.getValue().equals(String.valueOf(orderHeader.getBizStatus()))) {
                                        //业务状态
                                        detaData.add(String.valueOf(di.getLabel()));
                                        break;
                                    }
                                }
                                if(orderDetail.getSkuName()!=null){
                                    detaData.add(String.valueOf(orderDetail.getSkuName()));
                                }else{
                                    detaData.add("");
                                }
                                if(orderDetail.getPartNo()!=null){
                                    detaData.add(String.valueOf(orderDetail.getPartNo()));
                                }else{
                                    detaData.add("");
                                }
                                if(orderDetail!=null && orderDetail.getSkuInfo()!=null && orderDetail.getSkuInfo().getSkuPropertyInfos()!=null){
                                    detaData.add(String.valueOf(orderDetail.getSkuInfo().getSkuPropertyInfos()));
                                }else{
                                    detaData.add("");
                                }
                                detaData.add(String.valueOf(orderDetail.getOrdQty()));
                                detaData.add(String.valueOf(orderDetail.getSentQty()));
                                detailData.add(detaData);
                                }
                            }
                    }
                }else{
                    List<String> detaData = new ArrayList();
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detaData.add("");
                    detailData.add(detaData);
                }
            }
            String[] headers = {"发货号", "物流商", "运费", "操作费", "货值","运费/货值", "发货人", "物流结算方式", "发货时间"};
            String[] details = {"发货号","物流商", "订单编号", "经销店名称", "业务状态", "商品名称","商品编码", "商品属性", "采购数量","已发货数量"};
            OrderHeaderExportExcelUtils eeu = new OrderHeaderExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            eeu.exportExcel(workbook, 0, "发货数据", headers, data, fileName);
            eeu.exportExcel(workbook, 1, "已发货详情", details, detailData, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            addMessage(redirectAttributes, "导出订单发货信息数据失败！失败信息：" + e.getMessage());
        }
        return "modules/biz/inventory/bizInvoiceList";
    }
}