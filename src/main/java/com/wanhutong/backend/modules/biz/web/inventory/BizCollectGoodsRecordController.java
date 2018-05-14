/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.service.inventory.BizCollectGoodsRecordService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 收货记录表Controller
 * @author 张腾飞
 * @version 2018-01-03
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizCollectGoodsRecord")
public class BizCollectGoodsRecordController extends BaseController {

	@Autowired
	private BizCollectGoodsRecordService bizCollectGoodsRecordService;

	@Autowired
    private BizSendGoodsRecordService bizSendGoodsRecordService;

    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
	
	@ModelAttribute
	public BizCollectGoodsRecord get(@RequestParam(required=false) Integer id) {
		BizCollectGoodsRecord entity = null;
		if (id!=null){
			entity = bizCollectGoodsRecordService.get(id);
		}
		if (entity == null){
			entity = new BizCollectGoodsRecord();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizCollectGoodsRecord bizCollectGoodsRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizCollectGoodsRecord> page = bizCollectGoodsRecordService.findPage(new Page<BizCollectGoodsRecord>(request, response), bizCollectGoodsRecord); 
		model.addAttribute("page", page);
		return "modules/biz/inventory/bizCollectGoodsRecordList";
	}

	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:view")
	@RequestMapping(value = "form")
	public String form(BizCollectGoodsRecord bizCollectGoodsRecord, Model model) {
		model.addAttribute("bizCollectGoodsRecord", bizCollectGoodsRecord);
		return "modules/biz/inventory/bizCollectGoodsRecordForm";
	}

	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:edit")
	@RequestMapping(value = "save")
	public String save(BizCollectGoodsRecord bizCollectGoodsRecord, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, bizCollectGoodsRecord)){
//			return form(bizCollectGoodsRecord, model);
//		}
		bizCollectGoodsRecordService.save(bizCollectGoodsRecord);
		addMessage(redirectAttributes, "保存收货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestAll/?source=sh&ship=bh";
	}
	
	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCollectGoodsRecord bizCollectGoodsRecord, RedirectAttributes redirectAttributes) {
		bizCollectGoodsRecordService.delete(bizCollectGoodsRecord);
		addMessage(redirectAttributes, "删除收货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizCollectGoodsRecord/?repage";
	}

    /**
     * 库存变更记录列表
     * */
	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:view")
	@RequestMapping(value = "stockChangeList")
	public String stockChangeList(BizCollectGoodsRecord bizCollectGoodsRecord,HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizCollectGoodsRecord> bizCollectGoodsRecordPage = bizCollectGoodsRecordService.collectSendFindPage(new Page<BizCollectGoodsRecord>(request, response), bizCollectGoodsRecord);
        bizCollectGoodsRecordPage.getList().forEach(send->{
            if(send.getCustomer()!=null && send.getCustomer().getId()!=null){
                send.setChangeState("出库记录");
            }else{
                send.setChangeState("入库记录");
            }
        });
        model.addAttribute("page", bizCollectGoodsRecordPage);
		return "modules/biz/inventory/bizCollectStockChangeRecordList";
	}

	/**
	 * 导出
	 * */
	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:view")
	@RequestMapping(value = "exportList")
	public String exportList(BizCollectGoodsRecord bizCollectGoodsRecord, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String fileName = "收货记录" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<BizCollectGoodsRecord> page = bizCollectGoodsRecordService.findPage(new Page<BizCollectGoodsRecord>(request, response), bizCollectGoodsRecord);
			//1订单
			List<List<String>> order = new ArrayList<List<String>>();
			//2商品
			List<List<String>> commoDity = new ArrayList<List<String>>();
            page.getList().forEach(goods->{
                ArrayList<String> orderList = Lists.newArrayList();
                orderList.add(String.valueOf(goods.getOrderNum()));
                if(goods.getInvInfo()!=null && goods.getInvInfo().getName()!=null){
                    orderList.add(String.valueOf(goods.getInvInfo().getName()));
                }else{orderList.add("");}
                if(goods.getSkuInfo()!=null && goods.getSkuInfo().getName()!=null || goods.getSkuInfo().getPartNo()!=null){
                    orderList.add(String.valueOf(goods.getSkuInfo().getName()));
                    orderList.add(String.valueOf(goods.getSkuInfo().getPartNo()));
                }else{orderList.add("");orderList.add("");}
                orderList.add(String.valueOf(goods.getInvOldNum()==null?"":goods.getInvOldNum()));
                orderList.add(String.valueOf(goods.getReceiveNum()));
                orderList.add(String.valueOf(sdf.format(goods.getReceiveDate())));
                order.add(orderList);
                //商品
                if(goods.getBizRequestHeader()!=null && goods.getBizRequestHeader().getId()!=null){
                    BizRequestDetail bizRequestDetail = new BizRequestDetail();
                    bizRequestDetail.setRequestHeader(goods.getBizRequestHeader());
                    List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                    requestDetailList.forEach(requestDetail->{
                        if(requestDetail.getSkuInfo()!=null && requestDetail.getSkuInfo().getId()!=null){
                            BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
                            requestDetail.setSkuInfo(skuInfo);
                            ArrayList<String> commoDityList = Lists.newArrayList();
                            commoDityList.add(String.valueOf(goods.getOrderNum()));
                            if(requestDetail.getRequestHeader()!=null && requestDetail.getRequestHeader().getId()!=null){
                                BizRequestHeader requestHeader = bizRequestHeaderService.get(requestDetail.getRequestHeader());
                                if(requestHeader!=null && requestHeader.getFromOffice()!=null && requestHeader.getFromOffice().getName()!=null){
                                    commoDityList.add(String.valueOf(requestHeader.getFromOffice().getName()));
                                }else{commoDityList.add("");}
                                if(requestHeader!=null && requestHeader.getRecvEta()!=null){
                                    commoDityList.add(String.valueOf(sdf.format(requestHeader.getRecvEta())));
                                }else{commoDityList.add("");}
                            }else{commoDityList.add("");}
                            if(requestDetail.getSkuInfo().getProductInfo()!=null && requestDetail.getSkuInfo().getProductInfo().getBizVarietyInfo()!=null &&
                                    requestDetail.getSkuInfo().getProductInfo().getBizVarietyInfo().getName()!=null){
                                commoDityList.add(String.valueOf(requestDetail.getSkuInfo().getProductInfo().getBizVarietyInfo().getName()));
                            }else{commoDityList.add("");}
                            commoDityList.add(String.valueOf(requestDetail.getSkuInfo().getName()==null?"":requestDetail.getSkuInfo().getName()));
                            commoDityList.add(String.valueOf(requestDetail.getSkuInfo().getItemNo()==null?"":requestDetail.getSkuInfo().getItemNo()));
                            if(requestDetail.getSkuInfo().getProductInfo()!=null && requestDetail.getSkuInfo().getProductInfo().getOffice()!=null &&
                                    requestDetail.getSkuInfo().getProductInfo().getOffice().getName()!=null){
                                commoDityList.add(String.valueOf(requestDetail.getSkuInfo().getProductInfo().getOffice().getName()));
                            }else{commoDityList.add("");}
                            if(requestDetail.getSkuInfo().getProductInfo()!=null && requestDetail.getSkuInfo().getProductInfo().getBrandName()!=null){
                                commoDityList.add(String.valueOf(requestDetail.getSkuInfo().getProductInfo().getBrandName()));
                            }else{commoDityList.add("");}
                            commoDityList.add(String.valueOf(requestDetail.getReqQty()==null?"":requestDetail.getReqQty()));
                            commoDityList.add(String.valueOf(requestDetail.getSendQty()==null?"":requestDetail.getSendQty()));
                            commoDity.add(commoDityList);
                        }
                    });

                }
            });
            String[] orderArr = {"备货单号","仓库名称", "商品名称", "商品编号", "原库存数","收货数量", "收货时间"};
            String[] commoDityArr = {"备货单号","采购中心", "期望收货时间", "产品分类", "商品名称","商品货号", "供应商", "品牌", "申报数量", "已供货数量"};
            ExportExcelUtils eeu = new ExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            eeu.exportExcel(workbook, 0, "收货记录", orderArr, order, fileName);
            eeu.exportExcel(workbook, 1, "备货单数据", commoDityArr, commoDity, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出收货记录数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/biz/inventory/bizCollectGoodsRecord/";
	}

}