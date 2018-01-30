/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.Date;
import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSendGoodsRecordDao;

import javax.annotation.Resource;

/**
 * 供货记录表Service
 * @author 张腾飞
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizSendGoodsRecordService extends CrudService<BizSendGoodsRecordDao, BizSendGoodsRecord> {

	@Resource
	private BizInventorySkuService bizInventorySkuService;
	@Resource
	private BizRequestHeaderService bizRequestHeaderService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Resource
	private OfficeService officeService;
	@Resource
	private BizOrderDetailService bizOrderDetailService;
	@Resource
	private BizRequestDetailService bizRequestDetailService;
	@Resource
	private BizOrderHeaderService bizOrderHeaderService;
	@Resource
	private BizPoDetailService bizPoDetailService;

	public BizSendGoodsRecord get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSendGoodsRecord> findList(BizSendGoodsRecord bizSendGoodsRecord) {
		return super.findList(bizSendGoodsRecord);
	}
	
	public Page<BizSendGoodsRecord> findPage(Page<BizSendGoodsRecord> page, BizSendGoodsRecord bizSendGoodsRecord) {
			User user=UserUtils.getUser();
			if(user.isAdmin()){
				return super.findPage(page, bizSendGoodsRecord);
			}else {
				bizSendGoodsRecord.getSqlMap().put("sendGoodsRecord", BaseService.dataScopeFilter(user, "s", "su"));
				return super.findPage(page, bizSendGoodsRecord);
			}

	}
	
	@Transactional(readOnly = false)
	public void save(BizSendGoodsRecord bizSendGoodsRecord) {
		boolean flagRequest = true;		//备货单完成状态
		boolean flagOrder = true;		//销售单完成状态
		// 取出当前用户所在机构，
		User user = UserUtils.getUser();
		Office office1 = officeService.get(user.getCompany().getId());
		for (BizSendGoodsRecord bsgr : bizSendGoodsRecord.getBizSendGoodsRecordList()) {
			//准备数据
			//采购商或采购中心
			Office office = officeService.get(bizSendGoodsRecord.getCustomer().getId());
			//商品
			BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bsgr.getSkuInfo().getId());
            BizPoDetail bizPoDetail = new BizPoDetail();
            bizPoDetail.setSkuInfo(bizSkuInfo);
            List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);

            //仓库
			BizInventoryInfo invInfo = bizSendGoodsRecord.getInvInfo();
			int sendNum = bsgr.getSendNum();    //供货数
			//累计备货单供货数量
			if (bsgr.getBizRequestDetail() != null && bsgr.getBizRequestDetail().getId() != 0) {
				int sendQty = bsgr.getBizRequestDetail().getSendQty();   //备货单累计供货数量
				//当供货数量和申报数量不相等时，更改备货单状态
				if (bsgr.getBizRequestDetail().getReqQty() != (sendQty + sendNum)) {
					flagRequest = false;
				}
				if (sendNum == 0) {
					continue;
				}

				//累计备货单供货数量
				BizRequestDetail bizRequestDetail = bizRequestDetailService.get(bsgr.getBizRequestDetail().getId());
				bizRequestDetail.setSendQty(sendQty + sendNum);
				bizRequestDetailService.save(bizRequestDetail);
				//生成供货记录表
				bsgr.setSendNum(sendNum);
				if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
					BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
					bsgr.setBizRequestHeader(bizRequestHeader);
				}
				if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
					BizOrderHeader bizOrderHeader1 = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
					bsgr.setBizOrderHeader(bizOrderHeader1);
				}
				bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
				bsgr.setInvInfo(invInfo);
				bsgr.setCustomer(office);
				bsgr.setSkuInfo(bizSkuInfo);
				bsgr.setOrderNum(bsgr.getOrderNum());
				Date date = new Date();
				bsgr.setSendDate(date);
				super.save(bsgr);
			}
			//该订单属于销售订单，累计销售单供货数量
			if (bsgr.getBizOrderDetail() != null && bsgr.getBizOrderDetail().getId() != 0) {
				int sentQty = bsgr.getBizOrderDetail().getSentQty();	//销售单累计供货数量
				//当供货数量和申报数量不相等时，更改销售单状态
				if (bsgr.getBizOrderDetail().getOrdQty() != (sentQty + sendNum)){
					flagOrder = false;
				}
				if (sendNum == 0) {
					continue;
				}
				//该用户是采购中心
				if(!office1.getType().equals(OfficeTypeEnum.SUPPLYCENTER.getType())){

					//销售单状态改为同意供货（供货中）（15）
					BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
					bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
					bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
					//获取库存数
					BizInventorySku bizInventorySku = new BizInventorySku();
					bizInventorySku.setSkuInfo(bizSkuInfo);
					bizInventorySku.setInvInfo(invInfo);
					bizInventorySku.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
					List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
					int stock = 0;
					//没有库存，改销售单状态为采购中（17）
					if (list == null || list.size() == 0 || list.get(0).getStockQty() == 0){
						bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
						bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
					}else {
						//有库存
						for (BizInventorySku invSku:list) {
							stock = invSku.getStockQty();
							//如果库存不够，则改销售单状态为采购中（17）
							if (stock < bsgr.getBizOrderDetail().getOrdQty()){
								bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
								bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
								if(sendNum > stock){
									sendNum = stock;
								}
								//判断该用户是否是采购中心，如果是采购中心，对应的库存需要扣减
//								if (!office1.getType().equals(OfficeTypeEnum.SUPPLYCENTER.getType())){
									invSku.setStockQty(stock-sendNum);
									bizInventorySkuService.save(invSku);
//								}
							}else {
								invSku.setStockQty(stock-sendNum);
								bizInventorySkuService.save(invSku);
							}
						}
							//累计已供数量
							BizOrderDetail bizOrderDetail = bizOrderDetailService.get(bsgr.getBizOrderDetail().getId());
							bizOrderDetail.setSentQty(sentQty + sendNum);
							bizOrderDetailService.saveStatus(bizOrderDetail);
							//生成供货记录表
							bsgr.setSendNum(sendNum);
							if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
								BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
								bsgr.setBizRequestHeader(bizRequestHeader);
							}
							if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
								BizOrderHeader bizOrderHeader1 = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
								bsgr.setBizOrderHeader(bizOrderHeader1);
							}
							//如果不是供应中心，供货记录设为属于采购中心，否则设为供应中心
//							if (!office1.getType().equals(OfficeTypeEnum.SUPPLYCENTER.getType())){
							bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
//							}else{
//								bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
//							}
							bsgr.setInvInfo(invInfo);
							bsgr.setCustomer(office);
							bsgr.setSkuInfo(bizSkuInfo);
							bsgr.setOrderNum(bsgr.getOrderNum());
							Date date = new Date();
							bsgr.setSendDate(date);
							super.save(bsgr);
					}
				}else {//该用户是供应中心
					//累计已供数量
					BizOrderDetail bizOrderDetail = bizOrderDetailService.get(bsgr.getBizOrderDetail().getId());
					bizOrderDetail.setSentQty(sentQty + sendNum);
					bizOrderDetailService.saveStatus(bizOrderDetail);
					//生成供货记录表
					bsgr.setSendNum(sendNum);
					if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
						BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
						bsgr.setBizRequestHeader(bizRequestHeader);
					}
					if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
						BizOrderHeader bizOrderHeader1 = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
						bsgr.setBizOrderHeader(bizOrderHeader1);
					}
					bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
					bsgr.setInvInfo(invInfo);
					bsgr.setCustomer(office);
					bsgr.setSkuInfo(bizSkuInfo);
					bsgr.setOrderNum(bsgr.getOrderNum());
					Date date = new Date();
					bsgr.setSendDate(date);
					super.save(bsgr);
				}
			}
		}
		//更改备货单状态
		if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
			if (flagRequest) {
				BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
				bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.STOCK_COMPLETE.getState());
				bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
			}
		}
		//销售单完成时，更该销售单状态为已供货（20）
		if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
			if (flagOrder) {
				BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
				bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SEND.getState());
				bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
			}
		}
		//
//			super.save(bizSendGoodsRecord);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSendGoodsRecord bizSendGoodsRecord) {
		super.delete(bizSendGoodsRecord);
	}
	
}