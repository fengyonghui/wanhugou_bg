/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import com.alibaba.fastjson.JSONObject;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizOutTreasuryEntity;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.service.inventory.BizDetailInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 供货记录表Controller
 * @author 张腾飞
 * @version 2018-01-03
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/inventory/bizSendGoodsRecord")
public class BizSendGoodsRecordController extends BaseController {

	@Autowired
	private BizSendGoodsRecordService bizSendGoodsRecordService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizInvoiceService bizInvoiceService;
	@Autowired
	private BizDetailInvoiceService bizDetailInvoiceService;

	@ModelAttribute
	public BizSendGoodsRecord get(@RequestParam(required=false) Integer id) {
		BizSendGoodsRecord entity = null;
		if (id!=null){
			entity = bizSendGoodsRecordService.get(id);
		}
		if (entity == null){
			entity = new BizSendGoodsRecord();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:view")
	@RequestMapping(value = {"list", ""})
	public String list(Integer bizStatu, BizSendGoodsRecord bizSendGoodsRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
//		if ("0".equals(bizStatu)){
			bizSendGoodsRecord.setBizStatus(bizStatu);
//		}
//		if ("1".equals(bizStatu)){
//			bizSendGoodsRecord.setBizStatus(1);
//		}
		Page<BizSendGoodsRecord> page = bizSendGoodsRecordService.findPage(new Page<BizSendGoodsRecord>(request, response), bizSendGoodsRecord);
		model.addAttribute("page", page);
		model.addAttribute("bizStatus",bizStatu);
		return "modules/biz/inventory/bizSendGoodsRecordList";
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:view")
	@RequestMapping(value = "form")
	public String form(BizSendGoodsRecord bizSendGoodsRecord, Model model) {

		model.addAttribute("bizSendGoodsRecord", bizSendGoodsRecord);
		return "modules/biz/request/bizRequestHeaderKcForm";
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:edit")
	@RequestMapping(value = "save")
	public String save(BizSendGoodsRecord bizSendGoodsRecord,String source,String bizStatu,String ship, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, bizSendGoodsRecord)){
//			return form(bizSendGoodsRecord, model);
//		}
       		 bizSendGoodsRecordService.save(bizSendGoodsRecord);
			addMessage(redirectAttributes, "保存供货记录成功");
//			return "redirect:" + Global.getAdminPath() + "/biz/inventory/bizSendGoodsRecord/?repage&bizStatu="+bizSendGoodsRecord.getBizStatus();
//		跳回订单发货列表
		return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestAll?source="+source+"&bizStatu="+bizStatu+"&ship="+ship;
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSendGoodsRecord bizSendGoodsRecord, RedirectAttributes redirectAttributes) {
		bizSendGoodsRecord.setDelFlag(BizSendGoodsRecord.DEL_FLAG_DELETE);
		bizSendGoodsRecordService.delete(bizSendGoodsRecord);
		addMessage(redirectAttributes, "删除供货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSendGoodsRecord/?repage";
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizSendGoodsRecord bizSendGoodsRecord, RedirectAttributes redirectAttributes) {
		bizSendGoodsRecord.setDelFlag(BizSendGoodsRecord.DEL_FLAG_NORMAL);
		bizSendGoodsRecordService.delete(bizSendGoodsRecord);
		addMessage(redirectAttributes, "恢复供货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSendGoodsRecord/?repage";
	}

	@ResponseBody
	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:edit")
	@RequestMapping("outTreasury")
	public String outTreasury(HttpServletRequest request, @RequestBody String data) throws ParseException {
		try {
			data = URLDecoder.decode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = JsonUtil.parseJson(data);
		String treasuryList = jsonObject.getString("treasuryList");
		String bizInvoiceStr = jsonObject.getString("bizInvoiceStr");
		JSONObject bizInvoiceJson = JsonUtil.parseJson(bizInvoiceStr);

		List<BizOutTreasuryEntity> outTreasuryList = JsonUtil.parseArray(treasuryList, new TypeReference<List<BizOutTreasuryEntity>>() {});
		if (CollectionUtils.isEmpty(outTreasuryList)) {
			return "error";
		}
		BizInvoice bizInvoice = new BizInvoice();
		bizInvoice.setBizStatus(0);
		bizInvoice.setShip(0);
		bizInvoice.setIsConfirm(1);
		if (bizInvoiceJson != null && StringUtils.isNotBlank(bizInvoiceJson.getString("trackingNumber"))) {
//		"trackingNumber":trackingNumber,
			bizInvoice.setTrackingNumber(bizInvoiceJson.getString("trackingNumber"));
//				"inspectorId":inspectorId,
			bizInvoice.setInspector(new User(bizInvoiceJson.getInteger("inspectorId")));
//				"inspectDate":inspectDate,
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			bizInvoice.setInspectDate(simpleDateFormat.parse(bizInvoiceJson.getString("inspectDate")));
//				"inspectRemark":inspectRemark,
			bizInvoice.setRemarks(bizInvoiceJson.getString("inspectRemark"));
//				"collLocate":collLocate,
			bizInvoice.setCollLocate(bizInvoiceJson.getByte("collLocate"));
//				"sendDate":sendDate,
			bizInvoice.setSendDate(simpleDateFormat.parse(bizInvoiceJson.getString("sendDate")));
//				"settlementStatus":settlementStatus
			bizInvoice.setSettlementStatus(bizInvoiceJson.getInteger("settlementStatus"));
			bizInvoice.setSource("new");

			bizInvoiceService.save(bizInvoice);

			for (BizOutTreasuryEntity outTreasuryEntity : outTreasuryList) {
				Integer orderDetailId = outTreasuryEntity.getOrderDetailId();
				BizOrderDetail bizOrderDetail = bizOrderDetailService.get(orderDetailId);
				BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
				if (StringUtils.isNotBlank(orderHeader.getOrderNum())) {
					BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
					bizDetailInvoice.setInvoice(bizInvoice);
					bizDetailInvoice.setOrderHeader(orderHeader);
					bizDetailInvoiceService.save(bizDetailInvoice);
					break;
				}
			}
		}
//		return bizSendGoodsRecordService.outTreasury(outTreasuryList);
		return "ok";
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:view")
	@RequestMapping(value = "bizSendGoodsRecordExport", method = RequestMethod.POST)
	public String bizSendGoodsRecordExport(BizSendGoodsRecord bizSendGoodsRecord,String bizStatu, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String fileName = "供货记录数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<BizSendGoodsRecord> bsgrList = bizSendGoodsRecordService.findList(bizSendGoodsRecord);
            //供货记录数据容器
            List<List<String>> data = new ArrayList<List<String>>();
            for (BizSendGoodsRecord bsgr:bsgrList) {
                List<String> rowData = new ArrayList<>();
                //仓库名
                BizInventoryInfo bizInventoryInfo = bizInventoryInfoService.get(bsgr.getInvInfo().getId());
                if (bizStatu.equals("0")) {
                    rowData.add(bizInventoryInfo == null ? StringUtils.EMPTY : bizInventoryInfo.getName());
                }
                //商品名称
                rowData.add(bsgr.getSkuInfo().getName());
                //商品货号
                rowData.add(bsgr.getSkuInfo().getItemNo()==null?"":bsgr.getSkuInfo().getItemNo());
                //订单号
                rowData.add(bsgr.getOrderNum());
				if (bizStatu.equals("0")) {
					//供货之前库存数
					rowData.add(bsgr.getInvOldNum()==null?"":bsgr.getInvOldNum().toString());
				}
                //供货数量
                rowData.add(bsgr.getSendNum().toString());
                //客户
                rowData.add(bsgr.getCustomer().getName());
                //供货时间
                rowData.add(sdf.format(bsgr.getSendDate()));
                data.add(rowData);
            }
            ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			if (bizStatu.equals("1")) {
				String[] records = {"商品名称", "商品货号", "订单号", "供货数量", "客户", "供货时间"};
				eeu.exportExcel(workbook,0,"供货记录",records,data,fileName);
			}else {
				String[] records = {"仓库名", "商品名称", "商品货号", "订单号","原库存数", "供货数量", "客户", "供货时间"};
				eeu.exportExcel(workbook,0,"供货记录",records,data,fileName);
			}
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        }catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出供货记录失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/biz/inventory/bizSendGoodsRecord?bizStatu="+bizStatu;
	}

}