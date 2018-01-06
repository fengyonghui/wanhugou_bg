/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;

import java.util.Date;
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
	private OfficeService officeService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	
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
	public String list(BizSendGoodsRecord bizSendGoodsRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizSendGoodsRecord> page = bizSendGoodsRecordService.findPage(new Page<BizSendGoodsRecord>(request, response), bizSendGoodsRecord); 
		model.addAttribute("page", page);
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
	public String save(BizSendGoodsRecord bizSendGoodsRecord, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, bizSendGoodsRecord)){
//			return form(bizSendGoodsRecord, model);
//		}

		for (BizSendGoodsRecord bsgr:bizSendGoodsRecord.getBizSendGoodsRecordList()) {
			int sendNum = bsgr.getSendNum();	//供货数
			if (sendNum == 0){
				continue;
			}

			//准备数据
			//采购中心
			Office office = officeService.get(bizSendGoodsRecord.getCustomer().getId());
			//商品
			BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bsgr.getSkuInfo().getId());
			//备货单申报数
			if(bsgr.getBizRequestDetail() != null && bsgr.getBizRequestDetail().getReqQty() != 0) {
				BizRequestDetail bizRequestDetail = bizRequestDetailService.get(bsgr.getBizRequestDetail().getId());
				bizRequestDetail.setSendQty(sendNum);
			}
			//销售单申报数
			if (bsgr.getBizOrderDetail() != null && bsgr.getBizOrderDetail().getOrdQty() != 0){
				BizOrderDetail bizOrderDetail = bizOrderDetailService.get(bsgr.getBizOrderDetail().getId());
				bizOrderDetail.setSentQty(sendNum);
			}
			//修改订单状态

			//生成供货记录表
			//当销售单为空则保存备货单内容，当备货单为空则保存销售单内容
//			if (bsgr.getBizOrderHeader() != null && bsgr.getBizOrderHeader().getId() != null){
//				bsgr.setBizOrderHeader(bizOrderHeaderService.get(bsgr.getBizOrderHeader().getId()));
//			}
//			if (bsgr.getBizRequestHeader() != null && bsgr.getBizRequestHeader().getId() != null){
//				bsgr.setBizRequestHeader(bizRequestHeaderService.get(bsgr.getBizRequestHeader().getId()));
//			}
			bsgr.setSendNum(sendNum);
			bsgr.setCustomer(office);
			bsgr.setSkuInfo(bizSkuInfo);
			bsgr.setOrderNum(bsgr.getOrderNum());
			Date date = new Date();
			bsgr.setSendDate(date);
			bizSendGoodsRecordService.save(bsgr);
		}
		addMessage(redirectAttributes, "保存供货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSendGoodsRecord/?repage";
	}

	@RequiresPermissions("biz:inventory:bizSendGoodsRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizSendGoodsRecord bizSendGoodsRecord, RedirectAttributes redirectAttributes) {
		bizSendGoodsRecordService.delete(bizSendGoodsRecord);
		addMessage(redirectAttributes, "删除供货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizSendGoodsRecord/?repage";
	}

}