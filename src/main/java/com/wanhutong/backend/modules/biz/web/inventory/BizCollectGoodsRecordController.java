/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.InvSkuTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.SendGoodsRecordBizStatusEnum;
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
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.service.inventory.BizCollectGoodsRecordService;

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
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
    private BizPoHeaderService bizPoHeaderService;
	@Autowired
    private BizPoDetailService bizPoDetailService;
	@Autowired
    private BizSendGoodsRecordService bizSendGoodsRecordService;
	@Autowired
    private BizInventorySkuService bizInventorySkuService;
	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;

	
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

//	用于库存变更记录列表
	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:view")
	@RequestMapping(value = "stockChangeList")
	public String stockChangeList(BizCollectGoodsRecord bizCollectGoodsRecord,HttpServletRequest request, HttpServletResponse response, Model model) {
		BizSendGoodsRecord bizSendGoodsRecord = new BizSendGoodsRecord();
        bizSendGoodsRecord.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());//0
		if(bizCollectGoodsRecord.getQueryClass()==null){
			Page<BizSendGoodsRecord> pageSend = bizSendGoodsRecordService.findPage(new Page<BizSendGoodsRecord>(request, response), bizSendGoodsRecord);
			model.addAttribute("pageSend", pageSend);
			Page<BizCollectGoodsRecord> pageGods = bizCollectGoodsRecordService.findPage(new Page<BizCollectGoodsRecord>(request, response), bizCollectGoodsRecord);
			model.addAttribute("pageGods", pageGods);
		}else if(bizCollectGoodsRecord.getQueryClass()==1){
//			入库记录
			Page<BizCollectGoodsRecord> pageGods = bizCollectGoodsRecordService.findPage(new Page<BizCollectGoodsRecord>(request, response), bizCollectGoodsRecord);
			model.addAttribute("pageGods", pageGods);
		}else{
			//出库记录
            Page<BizSendGoodsRecord> pageSend = bizSendGoodsRecordService.findPage(new Page<BizSendGoodsRecord>(request, response), bizSendGoodsRecord);
			model.addAttribute("pageSend", pageSend);
		}
		return "modules/biz/inventory/bizCollectStockChangeRecordList";
	}
}