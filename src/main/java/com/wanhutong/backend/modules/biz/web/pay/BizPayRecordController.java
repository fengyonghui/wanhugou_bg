/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.pay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易记录Controller
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/pay/bizPayRecord")
public class BizPayRecordController extends BaseController {

	@Autowired
	private BizPayRecordService bizPayRecordService;
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public BizPayRecord get(@RequestParam(required=false) Integer id) {
		BizPayRecord entity = null;
		if (id!=null){
			entity = bizPayRecordService.get(id);
		}
		if (entity == null){
			entity = new BizPayRecord();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:pay:bizPayRecord:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPayRecord bizPayRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPayRecord> page = bizPayRecordService.findPage(new Page<BizPayRecord>(request, response), bizPayRecord); 
		model.addAttribute("page", page);
		return "modules/biz/pay/bizPayRecordList";
	}

	@RequiresPermissions("biz:pay:bizPayRecord:view")
	@RequestMapping(value = "form")
	public String form(BizPayRecord bizPayRecord, Model model) {
		model.addAttribute("bizPayRecord", bizPayRecord);
		return "modules/biz/pay/bizPayRecordForm";
	}

	@RequiresPermissions("biz:pay:bizPayRecord:edit")
	@RequestMapping(value = "save")
	public String save(BizPayRecord bizPayRecord, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPayRecord)){
			return form(bizPayRecord, model);
		}
		bizPayRecordService.save(bizPayRecord);
		addMessage(redirectAttributes, "保存交易记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/?repage";
	}
	
	@RequiresPermissions("biz:pay:bizPayRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPayRecord bizPayRecord, RedirectAttributes redirectAttributes) {
		bizPayRecord.setDelFlag(BizPayRecord.DEL_FLAG_DELETE);
		bizPayRecordService.delete(bizPayRecord);
		addMessage(redirectAttributes, "删除交易记录成功");
		String a="end_dele";
		if(bizPayRecord.getCendDele()!=null && bizPayRecord.getCendDele().equals(a)){
			return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/CendList?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/?repage";
	}

	@RequiresPermissions("biz:pay:bizPayRecord:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizPayRecord bizPayRecord, RedirectAttributes redirectAttributes) {
		bizPayRecord.setDelFlag(BizPayRecord.DEL_FLAG_NORMAL);
		bizPayRecordService.delete(bizPayRecord);
		addMessage(redirectAttributes, "恢复交易记录成功");
		String a="end_dele";
		if(bizPayRecord.getCendDele()!=null && bizPayRecord.getCendDele().equals(a)){
			return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/CendList?repage";
		}
		return "redirect:"+Global.getAdminPath()+"/biz/pay/bizPayRecord/?repage";
	}

	/**
	 * 用于交易记录导出
	 * */
	@RequiresPermissions("biz:pay:bizPayRecord:view")
	@RequestMapping(value = "payBtnExport", method = RequestMethod.POST)
	public String torySkuExport(BizPayRecord bizPayRecord,String payExportCend, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String a="cend_pay";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String fileName = "交易记录数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<BizPayRecord> PayList = null;
			if (payExportCend != null && payExportCend.equals(a)) {
				//C端
				bizPayRecord.setListPayQuery("CqueryPay");
				PayList = bizPayRecordService.findList(bizPayRecord);
			} else {
				PayList = bizPayRecordService.findList(bizPayRecord);
			}
			List<List<String>> data = new ArrayList<List<String>>();
			PayList.forEach(pay -> {
				ArrayList<String> rowData = Lists.newArrayList();
				//订单编号
				rowData.add(pay.getOrderNum() == null ? StringUtils.EMPTY : pay.getOrderNum());
				//业务凭证号
				rowData.add(pay.getPayNum() == null ? StringUtils.EMPTY : pay.getPayNum());
				//业务流水号
				rowData.add(pay.getOutTradeNo() == null ? StringUtils.EMPTY : pay.getOutTradeNo());
				//支付金额
				rowData.add(pay.getPayMoney() == null ? StringUtils.EMPTY : String.valueOf(pay.getPayMoney()));
				//支付人
				rowData.add(pay.getCreateBy().getName());
				//客户名称
				if (pay.getCustomer() != null && pay.getCustomer().getName() != null) {
					rowData.add(pay.getCustomer().getName());
				} else {
					rowData.add(StringUtils.EMPTY);
				}
				//采购中心
				if (pay.getCustConsultant() != null && pay.getCustConsultant().getCenters() != null && pay.getCustConsultant().getCenters().getName() != null) {
					rowData.add(String.valueOf(pay.getCustConsultant().getCenters().getName()));
				} else {
					rowData.add(StringUtils.EMPTY);
				}
				//联系电话
				if (pay.getCustomer() != null && pay.getCustomer().getMoblieMoeny() != null && pay.getCustomer().getMoblieMoeny().getMobile() != null) {
					rowData.add(String.valueOf(pay.getCustomer().getMoblieMoeny().getMobile()));
				} else {
					rowData.add(StringUtils.EMPTY);
				}
//				支付账号
				rowData.add(pay.getAccount() == null ? StringUtils.EMPTY : pay.getAccount());
				//支付到账户
				rowData.add(pay.getToAccount() == null ? StringUtils.EMPTY : pay.getToAccount());
				//交易类型名称
				rowData.add(pay.getRecordTypeName() == null ? StringUtils.EMPTY : pay.getRecordTypeName());
				//支付类型名称
				rowData.add(pay.getPayTypeName() == null ? StringUtils.EMPTY : pay.getPayTypeName());
				//交易作用/原因
				rowData.add(pay.getTradeReason() == null ? StringUtils.EMPTY : pay.getTradeReason());
				//创建人
				rowData.add(pay.getCreateBy().getName());
				//创建时间
				rowData.add(String.valueOf(sdf.format(pay.getCreateDate())));
				//更新时间
				rowData.add(String.valueOf(sdf.format(pay.getUpdateDate())));
				data.add(rowData);
			});
			String[] payHeads = {"订单编号", "业务凭证号", "微信/支付宝 支付业务单号", "收支金额", "支付人", "客户名称", "采购中心(性质)", "联系电话", "支付账号",
					"支付到账户", "交易类型名称", "支付类型名称", "交易作用/原因", "创建人", "创建时间", "更新时间"};
			ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			eeu.exportExcel(workbook, 0, "交易记录数据", payHeads, data, fileName);
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
			workbook.write(response.getOutputStream());
			workbook.dispose();
			return null;
		}catch (Exception e){
			addMessage(redirectAttributes, "导出交易记录数据失败！失败信息：" + e.getMessage());
		}
		if(payExportCend!=null && payExportCend.equals(a)){
			//C端
			return "redirect:" + adminPath + "/biz/pay/bizPayRecord/CendList";
		}
		return "redirect:" + adminPath + "/biz/pay/bizPayRecord/";
	}

	/**
	 * C端交易记录
	 * */
	@RequiresPermissions("biz:pay:bizPayRecord:view")
	@RequestMapping(value ="CendList")
	public String CendList(BizPayRecord bizPayRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		//查询C端交易记录标识
		bizPayRecord.setListPayQuery("CqueryPay");
		Page<BizPayRecord> page = bizPayRecordService.findPage(new Page<BizPayRecord>(request, response), bizPayRecord);
		model.addAttribute("page", page);
		return "modules/biz/pay/bizPayRecordCendList";
	}

}