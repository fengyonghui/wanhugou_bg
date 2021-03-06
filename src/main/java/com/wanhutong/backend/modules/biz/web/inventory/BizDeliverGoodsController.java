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
import com.wanhutong.backend.modules.biz.service.inventory.BizDeliverGoodsService;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Role;
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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 发货单Controller
 * @author 张腾飞
 * @version 2018-06-15
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizDeliverGoods")
public class BizDeliverGoodsController extends BaseController {

	@Autowired
	private BizDeliverGoodsService bizDeliverGoodsService;
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
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private DictService dictService;

    private static final String DEF_EN_NAME = "shipper";


	@ModelAttribute
	public BizInvoice get(@RequestParam(required=false) Integer id) {
		BizInvoice entity = null;
		if (id!=null){
			entity = bizDeliverGoodsService.get(id);
		}
		if (entity == null){
			entity = new BizInvoice();
		}
		return entity;
	}

    @RequiresPermissions("biz:inventory:bizInvoice:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizInvoice bizInvoice, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizInvoice> page = bizDeliverGoodsService.findPage(new Page<BizInvoice>(request, response), bizInvoice);
        model.addAttribute("page", page);
        return "modules/biz/inventory/bizInvoiceList";
    }

	@RequiresPermissions("biz:inventory:bizInvoice:view")
	@RequestMapping(value = "form")
	public String form(BizInvoice bizInvoice, Model model) {
        BizLogistics bizLogistics = new BizLogistics();
		List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        boolean flag = false;
        for (Role role:roleList) {
            if (role.getEnname().equals(RoleEnNameEnum.DEPT.getState())) {
                flag = true;
                break;
            }
        }
        model.addAttribute("logisticsList",logisticsList);
        if (StringUtils.isBlank(bizInvoice.getCarrier())) {
            bizInvoice.setCarrier(user.getName());
        }
		model.addAttribute("bizInvoice", bizInvoice);
        if (flag && bizInvoice.getBizStatus()==1) {
            List<User> userList = systemService.findUserByRoleEnName(DEF_EN_NAME);
            model.addAttribute("userList", userList);
        }else if (flag && bizInvoice.getBizStatus()==0) {
            List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
            model.addAttribute("userList", userList);
        }else {
            model.addAttribute("userList",null);
        }
		model.addAttribute("bizOrderHeader",new BizOrderHeader());
		return "modules/biz/inventory/bizDeliverGoodsForm";
	}

    /**
     * 订单所属发货单详情和修改页面跳转
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
                orderHeaderList.add(orderHeader);
            }
        }

        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        boolean flag = false;
        for (Role role:roleList) {
            if (role.getEnname().equals(RoleEnNameEnum.DEPT.getState())) {
                flag = true;
                break;
            }
        }
        if (flag && bizInvoice.getBizStatus()==1) {
            List<User> userList = systemService.findUserByRoleEnName(DEF_EN_NAME);
            model.addAttribute("userList", userList);
        }else if (flag && bizInvoice.getBizStatus()==0) {
            List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
            model.addAttribute("userList", userList);
        }else {
            model.addAttribute("userList",null);
        }
        BizLogistics bizLogistics = new BizLogistics();
        List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
        model.addAttribute("logisticsList",logisticsList);
        model.addAttribute("source",source);
        model.addAttribute("orderHeaderList",orderHeaderList);
        model.addAttribute("bizInvoice", bizInvoice);
        return "modules/biz/inventory/bizDeliverGoodsDeForm";
    }

	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "save")
	public String save(BizInvoice bizInvoice, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInvoice)){
			return form(bizInvoice, model);
		}
		bizDeliverGoodsService.save(bizInvoice);
		addMessage(redirectAttributes, "保存发货单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizInvoice/?repage&bizStatus="+bizInvoice.getBizStatus()+"&ship="+bizInvoice.getShip();
	}
	
	@RequiresPermissions("biz:inventory:bizInvoice:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInvoice bizInvoice, RedirectAttributes redirectAttributes) {
		bizDeliverGoodsService.delete(bizInvoice);
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
            String fileName =null;
            if(bizInvoice.getBizStatus()!=null && bizInvoice.getBizStatus().equals(1) && bizInvoice.getShip()!=null && bizInvoice.getShip().equals(1)) {
                fileName = "备货单货信息数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            }else {
                fileName = "订发货信息数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            }
            List<BizInvoice> list = bizDeliverGoodsService.findList(bizInvoice);
            //1订单
            List<List<String>> data = new ArrayList<List<String>>();
            //2商品
            List<List<String>> detailData = new ArrayList<List<String>>();
            for (BizInvoice invoice : list) {
                List<String> headData = new ArrayList();
                headData.add(String.valueOf(invoice.getSendNumber()));
                if(invoice.getLogistics()!=null && invoice.getLogistics().getName()!=null){
                    headData.add(String.valueOf(invoice.getLogistics().getName()));
                }else{
                    headData.add("");
                }
                headData.add(String.valueOf(invoice.getFreight()==null?"":invoice.getFreight()));
                headData.add(String.valueOf(invoice.getOperation()==null?"":invoice.getOperation()));
                headData.add(String.valueOf(invoice.getValuePrice()==null?"":invoice.getValuePrice()));

                double price=0.0;
                if(invoice.getFreight()!=null && invoice.getValuePrice()!=null){
                    price=invoice.getFreight()*100/invoice.getValuePrice();
                }//运费/货值
                BigDecimal   b   =   new   BigDecimal(price);
                double   f1   =   b.setScale(0,   BigDecimal.ROUND_HALF_UP).doubleValue();
                headData.add(String.valueOf(f1+"%"));
                headData.add(String.valueOf(invoice.getCarrier()==null?"":invoice.getCarrier()));
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
                headData.add(String.valueOf(sdf.format(invoice.getSendDate())));
                data.add(headData);

                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(invoice);
                List<BizDetailInvoice> DetailInvoiceList = bizDetailInvoiceService.findList(bizDetailInvoice);
                if(DetailInvoiceList.size()!=0){
                    for (BizDetailInvoice detailInvoice : DetailInvoiceList) {
                        if(bizInvoice.getBizStatus()!=null && bizInvoice.getBizStatus().equals(1) && bizInvoice.getShip()!=null && bizInvoice.getShip().equals(1)){
                            //备货单发货信息
                            List<BizRequestDetail> requestDetailList =null;
                            BizRequestHeader requestHeader = bizRequestHeaderService.get(detailInvoice.getRequestHeader().getId());
                            if(detailInvoice.getRequestHeader()!=null && detailInvoice.getRequestHeader().getId()!=null){
                                if(requestHeader!=null){
                                    BizRequestDetail bizRequestDetail = new BizRequestDetail();
                                    bizRequestDetail.setRequestHeader(requestHeader);
                                    requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                                }
                            }
                            if (requestHeader != null && requestDetailList.size() != 0) {
                                List<String> detaData = new ArrayList();
                                for (BizRequestDetail requeDetail : requestDetailList) {
                                    requeDetail.setRequestHeader(requestHeader);
                                    BizSkuInfo skuInfo = bizSkuInfoService.get(requeDetail.getSkuInfo());
                                    BizSkuInfo sku = bizSkuInfoService.findListProd(skuInfo);
                                    requeDetail.setSkuInfo(sku);
                                    detaData.add(String.valueOf(invoice.getSendNumber() == null ? "" : invoice.getSendNumber()));
                                    if (invoice.getLogistics() != null && invoice.getLogistics().getName() != null) {
                                        detaData.add(String.valueOf(invoice.getLogistics().getName()));
                                    } else {
                                        detaData.add("");
                                    }
                                    if(requestHeader!=null){
                                        detaData.add(String.valueOf(requestHeader.getReqNo()==null?"":requestHeader.getReqNo()));
                                        if(requestHeader.getFromOffice()!=null && requestHeader.getFromOffice().getName()!=null){
                                            detaData.add(String.valueOf(requestHeader.getFromOffice().getName()));
                                        }
                                        Dict detadict = new Dict();
                                        detadict.setDescription("备货单业务状态");
                                        detadict.setType("biz_req_status");
                                        List<Dict> detadictList = dictService.findList(detadict);
                                        if (detadictList.size() != 0) {
                                            for (Dict di : detadictList) {
                                                if (di.getValue().equals(String.valueOf(requestHeader.getBizStatus()))) {
                                                    //业务状态
                                                    detaData.add(String.valueOf(di.getLabel()));
                                                    break;
                                                }
                                            }
                                        } else {
                                            detaData.add("");
                                        }
                                    }
                                    if(requeDetail.getSkuInfo()!=null){
                                        if(requeDetail.getSkuInfo()!=null && requeDetail.getSkuInfo().getName()!=null){
                                            detaData.add(String.valueOf(requeDetail.getSkuInfo().getName()));
                                            detaData.add(String.valueOf(requeDetail.getSkuInfo().getPartNo()==null?"":requeDetail.getSkuInfo().getPartNo()));
                                            detaData.add(String.valueOf(requeDetail.getSkuInfo().getSkuPropertyInfos()==null?"":requeDetail.getSkuInfo().getSkuPropertyInfos()));
                                        }else{
                                            detaData.add("");detaData.add("");detaData.add("");
                                        }
                                        detaData.add(String.valueOf(requeDetail.getReqQty()==null?"":requeDetail.getReqQty()));
                                        detaData.add(String.valueOf(requeDetail.getSendQty()==null?"":requeDetail.getSendQty()));
                                        detailData.add(detaData);
                                    }
                                }
                            }

                        }else {
                            BizOrderDetail bizOrderDetail = new BizOrderDetail();
                            List<BizOrderDetail> orderDetailList = null;
                            BizOrderHeader orderHeader = bizOrderHeaderService.get(detailInvoice.getOrderHeader().getId());
                            if (orderHeader != null) {
                                bizOrderDetail.setOrderHeader(orderHeader);
                                orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                            }
                            if (orderDetailList != null && orderDetailList.size() != 0) {
                                for (BizOrderDetail orderDetail : orderDetailList) {
                                    List<String> detaData = new ArrayList();
                                    BizSkuInfo sku = bizSkuInfoService.get(orderDetail.getSkuInfo());
                                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                                    orderDetail.setSkuInfo(skuInfo);
                                    detaData.add(String.valueOf(invoice.getSendNumber() == null ? "" : invoice.getSendNumber()));
                                    if (invoice.getLogistics() != null && invoice.getLogistics().getName() != null) {
                                        detaData.add(String.valueOf(invoice.getLogistics().getName()));
                                    } else {
                                        detaData.add("");
                                    }
                                    if (orderHeader != null) {
                                        detaData.add(String.valueOf(orderDetail.getOrderHeader().getOrderNum()));
                                        if (orderHeader.getCustomer() != null && orderHeader.getCustomer().getName() != null) {
                                            detaData.add(String.valueOf(orderHeader.getCustomer().getName()));
                                        } else {
                                            detaData.add("");
                                        }
                                    } else {
                                        detaData.add("");
                                        detaData.add("");
                                    }
                                    Dict detadict = new Dict();
                                    detadict.setDescription("业务状态");
                                    detadict.setType("biz_order_status");
                                    List<Dict> detadictList = dictService.findList(detadict);
                                    if (detadictList.size() != 0) {
                                        for (Dict di : detadictList) {
                                            if (di.getValue().equals(String.valueOf(orderHeader.getBizStatus()))) {
                                                //业务状态
                                                detaData.add(String.valueOf(di.getLabel()));
                                                break;
                                            }
                                        }
                                    } else {
                                        detaData.add("");
                                    }
                                    detaData.add(String.valueOf(orderDetail.getSkuName() == null ? "" : orderDetail.getSkuName()));
                                    detaData.add(String.valueOf(orderDetail.getPartNo() == null ? "" : orderDetail.getPartNo()));
                                    if (orderDetail != null && orderDetail.getSkuInfo() != null && orderDetail.getSkuInfo().getSkuPropertyInfos() != null) {
                                        detaData.add(String.valueOf(orderDetail.getSkuInfo().getSkuPropertyInfos()));
                                    } else {
                                        detaData.add("");
                                    }
                                    detaData.add(String.valueOf(orderDetail.getOrdQty() == null ? "" : orderDetail.getOrdQty()));
                                    detaData.add(String.valueOf(orderDetail.getSentQty() == null ? "" : orderDetail.getSentQty()));
                                    detailData.add(detaData);
                                }
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
            OrderHeaderExportExcelUtils eeu = new OrderHeaderExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            String[] headers = {"发货号", "物流商", "运费", "操作费", "货值","运费/货值", "发货人", "物流结算方式", "发货时间"};
            if(bizInvoice.getBizStatus()!=null && bizInvoice.getBizStatus().equals(1) && bizInvoice.getShip()!=null && bizInvoice.getShip().equals(1)){
                String[] details = {"发货号", "物流商", "备货单编号", "采购中心", "业务状态", "商品名称", "商品编码", "商品属性", "采购数量", "已发货数量"};
                eeu.exportExcel(workbook, 0, "发货数据", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "已发货详情", details, detailData, fileName);
            }else {
                String[] details = {"发货号", "物流商", "订单编号", "经销店名称", "业务状态", "商品名称", "商品编码", "商品属性", "采购数量", "已发货数量"};
                eeu.exportExcel(workbook, 0, "发货数据", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "已发货详情", details, detailData, fileName);
            }
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            addMessage(redirectAttributes, "导出发货信息数据失败！失败信息：" + e.getMessage());
        }
        return "modules/biz/inventory/bizInvoiceList";
    }
}