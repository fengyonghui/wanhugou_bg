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
		boolean flagRequest = true;		//备货单完成状态
        boolean flagPo = true;      //采购单完成状态
        int recvQtySum = 0;
		//得到同一采购单下销售单信息
		BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
		bizPoOrderReq.setRequestHeader(bizCollectGoodsRecord.getBizRequestHeader());
		List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
		if (bizPoOrderReqList != null && bizPoOrderReqList.size() > 0) {
			bizPoOrderReq = bizPoOrderReqList.get(0);//得到备货单对应的采购单
		}
		//根据采购单ID,得到该采购单对应的所有销售单
		int sendNumSum = 0;     //累计供货记录的供货数
		List<BizPoOrderReq> bizPoOrderReqList1 = bizPoOrderReqService.findList(bizPoOrderReq);
		for (BizPoOrderReq bizPoOrderReq1:bizPoOrderReqList1 ) {
			BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizPoOrderReq1.getOrderHeader().getId());
			if (bizOrderHeader != null) {
				//得到同一采购单下所有销售单供货记录
				BizSendGoodsRecord bizSendGoodsRecord = new BizSendGoodsRecord();
				bizSendGoodsRecord.setOrderNum(bizOrderHeader.getOrderNum());
				List<BizSendGoodsRecord> bizSendGoodsRecordList = bizSendGoodsRecordService.findList(bizSendGoodsRecord);

				for (BizSendGoodsRecord bizSendGoodsRecord1 : bizSendGoodsRecordList) {
					int sendNum = bizSendGoodsRecord1.getSendNum();
					sendNumSum += sendNum;
				}
			}
		}

		for (BizCollectGoodsRecord bcgr : bizCollectGoodsRecord.getBizCollectGoodsRecordList()) {
			int receiveNum = bcgr.getReceiveNum();    //收货数
			int recvQty = bcgr.getBizRequestDetail().getRecvQty();		//已收货数量
			//累计备货单收货数量
			if (bcgr.getBizRequestDetail() != null && bcgr.getBizRequestDetail().getId() != 0) {
//				int sendQty = bcgr.getBizRequestDetail().getSendQty();   //备货单已供货数量
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
			//仓库
//			BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
//			bizInventoryInfo.setCustomer(bcgr.getCustomer());
//			List<BizInventoryInfo> bizInventoryInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
//			if (bizInventoryInfoList != null && bizInventoryInfoList.size() > 0){
//				bizInventoryInfo = bizInventoryInfoList.get(0);
//			}
			bcgr.setInvInfo(bizCollectGoodsRecord.getInvInfo());
			bcgr.setSkuInfo(bizSkuInfo);
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			bcgr.setBizRequestHeader(bizRequestHeader);
			bcgr.setOrderNum(bcgr.getOrderNum());
			bcgr.setReceiveDate(new Date());
			bcgr.setReceiveNum(bcgr.getReceiveNum());
			bizCollectGoodsRecordService.save(bcgr);

			//获取库存信息
            BizInventorySku bizInventorySku = new BizInventorySku();
            bizInventorySku.setSkuInfo(bcgr.getSkuInfo());
            bizInventorySku.setCustomer(bcgr.getCustomer());
            bizInventorySku.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
			//库存有该商品,增加相应数量
            if(bizInventorySkuService.findList(bizInventorySku) != null && bizInventorySkuService.findList(bizInventorySku).size() > 0){
                List<BizInventorySku> bizInventorySkuList = bizInventorySkuService.findList(bizInventorySku);
                BizInventorySku bizInventorySku1 = bizInventorySkuList.get(0);
                bizInventorySku1.setStockQty(bizInventorySku1.getStockQty()+receiveNum);
                bizInventorySkuService.save(bizInventorySku1);
            }
            //库存没有该商品，增加该商品相应库存
			if(bizInventorySkuService.findList(bizInventorySku) == null || bizInventorySkuService.findList(bizInventorySku).size() == 0){
            	BizInventorySku bizInventorySku1 = new BizInventorySku();
            	bizInventorySku1.setInvInfo(bizCollectGoodsRecord.getInvInfo());
				bizInventorySku1.setSkuInfo(bcgr.getSkuInfo());
				bizInventorySku1.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
				bizInventorySku1.setStockQty(receiveNum);
				bizInventorySku1.setCustomer(bcgr.getCustomer());
				bizInventorySkuService.save(bizInventorySku1);

			}

            //当采购数量和(销售单供货记录的累计供货数+采购中心已收货数量)不相等时，更改采购单完成状态
            //已采购数
            int poOrdQty = recvQty + sendNumSum;
			BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoOrderReq.getPoHeader().getId());
			BizPoDetail bizPoDetail = new BizPoDetail();
			bizPoDetail.setPoHeader(bizPoHeader);
			List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
			if (bizPoDetailList != null && bizPoDetailList.size() > 0){
				bizPoDetail = bizPoDetailList.get(0);
			}
            if(bizPoDetail.getOrdQty() != poOrdQty){
                flagPo = false;
            }
		}

		//更改备货单状态:收货完成
		if (flagRequest) {
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.COMPLETE.getState());
			bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
		}
		//更改采购单状态
		if(flagPo){
//			BizPoOrderReq bizPoOrderReq1 = new BizPoOrderReq();
//			bizPoOrderReq1.setRequestHeader(bizCollectGoodsRecord.getBizRequestHeader());
//			List<BizPoOrderReq> bizPoOrderReqList1 = bizPoOrderReqService.findList(bizPoOrderReq);
//			if (bizPoOrderReqList != null && bizPoOrderReqList.size() > 0) {
//				bizPoOrderReq1 = bizPoOrderReqList.get(0);
//			}
            BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoOrderReq.getPoHeader().getId());
			int a = ReqHeaderStatusEnum.COMPLETE.getState();
			byte b = (byte)a;
            bizPoHeader.setBizStatus(b);
            bizPoHeaderService.save(bizPoHeader);
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