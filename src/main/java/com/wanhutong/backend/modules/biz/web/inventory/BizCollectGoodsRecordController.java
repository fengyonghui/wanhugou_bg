/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
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

import java.util.Date;

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
		boolean flagRequest = true;		//备货单完成状态
		for (BizCollectGoodsRecord bcgr : bizCollectGoodsRecord.getBizCollectGoodsRecordList()) {
			int receiveNum = bcgr.getReceiveNum();    //收货数
			//累计备货单收货数量和供货数量
			if (bcgr.getBizRequestDetail() != null && bcgr.getBizRequestDetail().getId() != 0) {
				int sendQty = bcgr.getBizRequestDetail().getSendQty();   //备货单已供货数量
				int recvQty = bcgr.getBizRequestDetail().getRecvQty();		//已收货数量
				//当收货数量和申报数量不相等时，更改备货单状态
				if (bcgr.getBizRequestDetail().getReqQty() != (recvQty + receiveNum)) {
					flagRequest = false;
				}
				if (receiveNum == 0) {
					continue;
				}
				BizRequestDetail bizRequestDetail = bizRequestDetailService.get(bcgr.getBizRequestDetail().getId());
				bizRequestDetail.setRecvQty(recvQty + receiveNum);
				bizRequestDetailService.save(bizRequestDetail);
			}
			//生成收货记录表
			//商品
			BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bcgr.getSkuInfo().getId());
//			bcgr.setInvInfo();
			bcgr.setSkuInfo(bizSkuInfo);
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			bcgr.setBizRequestHeader(bizRequestHeader);
			bcgr.setOrderNum(bcgr.getOrderNum());
			bcgr.setReceiveDate(new Date());
			bcgr.setReceiveNum(bcgr.getReceiveNum());
			bizCollectGoodsRecordService.save(bcgr);
			//修改库存，库存有该商品
		}
		//修改库存


		//更改订单状态
		if (flagRequest) {
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.COMPLETE.getState());
			bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
		}

		addMessage(redirectAttributes, "保存收货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizCollectGoodsRecord/?repage";
	}
	
	@RequiresPermissions("biz:inventory:bizCollectGoodsRecord:edit")
	@RequestMapping(value = "delete")
	public String delete(BizCollectGoodsRecord bizCollectGoodsRecord, RedirectAttributes redirectAttributes) {
		bizCollectGoodsRecordService.delete(bizCollectGoodsRecord);
		addMessage(redirectAttributes, "删除收货记录成功");
		return "redirect:"+Global.getAdminPath()+"/biz/inventory/bizCollectGoodsRecord/?repage";
	}

}