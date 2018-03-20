/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.Date;
import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.InvSkuTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.dao.inventory.BizCollectGoodsRecordDao;

import javax.annotation.Resource;

/**
 * 收货记录表Service
 * @author 张腾飞
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizCollectGoodsRecordService extends CrudService<BizCollectGoodsRecordDao, BizCollectGoodsRecord> {

	@Resource
	private BizRequestDetailService bizRequestDetailService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Resource
	private BizRequestHeaderService bizRequestHeaderService;
	@Resource
	private BizInventorySkuService bizInventorySkuService;
	@Resource
    private OfficeService officeService;

	public BizCollectGoodsRecord get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCollectGoodsRecord> findList(BizCollectGoodsRecord bizCollectGoodsRecord) {

		return super.findList(bizCollectGoodsRecord);
	}
	
	public Page<BizCollectGoodsRecord> findPage(Page<BizCollectGoodsRecord> page, BizCollectGoodsRecord bizCollectGoodsRecord) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizCollectGoodsRecord);
		}else {
			bizCollectGoodsRecord.getSqlMap().put("collectGoodsRecord", BaseService.dataScopeFilter(user, "cent", "su"));
			return super.findPage(page, bizCollectGoodsRecord);
		}


	}
	
	@Transactional(readOnly = false)
	public void save(BizCollectGoodsRecord bizCollectGoodsRecord) {
        User user = UserUtils.getUser();
        officeService.get(user.getCompany().getId());
        boolean flagRequest = true;		//备货单完成状态
//		boolean flagPo = true;      //采购单完成状态
//		int recvQtySum = 0;
		//得到同一采购单下销售单信息
		/*BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
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
		}*/

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
			bcgr.setVender(officeService.get(user.getCompany().getId()));
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			bcgr.setBizRequestHeader(bizRequestHeader);
			bcgr.setOrderNum(bcgr.getOrderNum());
			bcgr.setReceiveDate(new Date());
			bcgr.setReceiveNum(bcgr.getReceiveNum());
			super.save(bcgr);

			//获取库存信息
			BizInventorySku bizInventorySku = new BizInventorySku();
			bizInventorySku.setSkuInfo(bcgr.getSkuInfo());
			bizInventorySku.setInvInfo(bcgr.getInvInfo());
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
			//修改备货单状态为：备货中（20）
			BizRequestHeader bizRequestHeader1 = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			bizRequestHeader1.setBizStatus(ReqHeaderStatusEnum.STOCKING.getState());
			bizRequestHeaderService.saveRequestHeader(bizRequestHeader1);

			//当采购数量和(销售单供货记录的累计供货数+采购中心已收货数量)不相等时，更改采购单完成状态
			//已采购数
            /*int poOrdQty = recvQty + sendNumSum;
			BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoOrderReq.getPoHeader().getId());
			BizPoDetail bizPoDetail = new BizPoDetail();
			bizPoDetail.setPoHeader(bizPoHeader);
			List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
			if (bizPoDetailList != null && bizPoDetailList.size() > 0){
				bizPoDetail = bizPoDetailList.get(0);
			}
            if(bizPoDetail.getOrdQty() != poOrdQty){
                flagPo = false;
            }*/
		}

		//更改备货单状态:收货完成（30）
		if (flagRequest) {
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.COMPLETE.getState());
			bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
		}
		//更改采购单状态
		/*if(flagPo){
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
        }*/
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCollectGoodsRecord bizCollectGoodsRecord) {
		super.delete(bizCollectGoodsRecord);
	}
	
}