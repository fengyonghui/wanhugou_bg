/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import com.google.common.collect.Maps;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSendGoodsRecordDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryOrderRequest;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizOutTreasuryEntity;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.SendGoodsRecordBizStatusEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供货记录表Service
 * @author 张腾飞
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizSendGoodsRecordService extends CrudService<BizSendGoodsRecordDao, BizSendGoodsRecord> {

	@Autowired
    private BizSendGoodsRecordDao bizSendGoodsRecordDao;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizInventoryOrderRequestService bizInventoryOrderRequestService;
	@Autowired
	private BizInventorySkuService bizInventorySkuService;
	@Autowired
    private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
    private BizOrderStatusService bizOrderStatusService;

    private static final Logger LOGGER = LoggerFactory.getLogger(BizSendGoodsRecordService.class);

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
				bizSendGoodsRecord.setDataStatus("filter");
				if(user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.WITHCAPITAL.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.NETWORKSUPPLY.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.NETWORK.getType())){
					bizSendGoodsRecord.getSqlMap().put("sendGoodsRecord", BaseService.dataScopeFilter(user, "cent", "su"));
				}

				return super.findPage(page, bizSendGoodsRecord);
			}

	}
	
	@Transactional(readOnly = false)
	public void save(BizSendGoodsRecord bizSendGoodsRecord) {
	    super.save(bizSendGoodsRecord);
    }

    public Integer getSumSendNumByReqDetailId(Integer reqDetailId) {

		Date todayStartTime = DateUtils.getTodayStartTime();
		Date oneDayBefore = DateUtils.getOneDayBefore(todayStartTime);
		Date yesterdayEnd = DateUtils.getYesterdayEnd();
		return bizSendGoodsRecordDao.getSumSendNumByReqDetailId(reqDetailId,oneDayBefore,yesterdayEnd);
	}
	@Transactional(readOnly = false)
	public void delete(BizSendGoodsRecord bizSendGoodsRecord) {
		super.delete(bizSendGoodsRecord);
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public String outTreasury(List<BizOutTreasuryEntity> outTreasuryList) {
	    boolean orderFlag = true; //用于判断订单是否已全部出库
        BizOrderHeader orderHeader = new BizOrderHeader();
		Map<Integer, Integer> orderDetailMap = Maps.newHashMap();
		for (BizOutTreasuryEntity outTreasuryEntity : outTreasuryList) {
			Integer orderDetailId = outTreasuryEntity.getOrderDetailId();
			Integer reqDetailId = outTreasuryEntity.getReqDetailId();
			Integer invId = outTreasuryEntity.getInvSkuId();
			Integer outQty = outTreasuryEntity.getOutQty();
			Integer version = outTreasuryEntity.getuVersion();
			BizOrderDetail bizOrderDetail = bizOrderDetailService.get(orderDetailId);
			BizInventorySku inventorySku = bizInventorySkuService.get(invId);
			BizRequestDetail bizRequestDetail = bizRequestDetailService.get(reqDetailId);
            orderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
            if (!version.equals(inventorySku.getuVersion())) {
				return "其他人正在出库，请刷新页面重新操作";
			}
			//生成发货单
			BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
			bsgr.setSendNo(outTreasuryEntity.getSendNo());
			bsgr.setSkuInfo(bizOrderDetail.getSkuInfo());
			bsgr.setInvOldNum(inventorySku.getStockQty());
			bsgr.setInvInfo(inventorySku.getInvInfo());
			bsgr.setBizOrderHeader(bizOrderDetail.getOrderHeader());
			bsgr.setOrderNum(bizOrderDetail.getOrderHeader().getOrderNum());
			bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
			bsgr.setSendNum(outQty);
			bsgr.setCustomer(bizOrderDetail.getOrderHeader().getCustomer());
			bsgr.setSendDate(new Date());
			save(bsgr);
			//修改库存数量
			bizInventorySkuService.updateStockQty(inventorySku,inventorySku.getStockQty() - outQty);
			//修改备货单详情已出库数量
			bizRequestDetail.setOutQty((bizRequestDetail.getOutQty() == null ? 0 : bizRequestDetail.getOutQty()) + outQty);
			bizRequestDetailService.save(bizRequestDetail);
			//修改订单详情已发货数量
			bizOrderDetail.setSentQty((bizOrderDetail.getSentQty() == null ? 0 : bizOrderDetail.getSentQty()) + outQty);
			bizOrderDetailService.saveStatus(bizOrderDetail);
			if (orderDetailMap.get(bizOrderDetail.getId()) != null) {
			    orderDetailMap.put(bizOrderDetail.getId(),orderDetailMap.get(bizOrderDetail.getId()) + outQty);
            } else {
                orderDetailMap.put(bizOrderDetail.getId(), bizOrderDetail.getSentQty());
            }
			//修改订单状态
            int status = orderHeader.getBizStatus();
            orderHeader.setBizStatus(OrderHeaderBizStatusEnum.APPROVE.getState());
            bizOrderHeaderService.saveOrderHeader(orderHeader);
            if (status < OrderHeaderBizStatusEnum.APPROVE.getState()) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SELLORDER.getDesc(),BizOrderStatusOrderTypeEnum.SELLORDER.getState(),orderHeader.getId());
            }
			//订单关联出库备货单
			BizInventoryOrderRequest ior = new BizInventoryOrderRequest();
            ior.setInvSku(inventorySku);
			ior.setOrderDetail(bizOrderDetail);
			ior.setRequestDetail(bizRequestDetail);
			List<BizInventoryOrderRequest> iorList = bizInventoryOrderRequestService.findList(ior);
			if (CollectionUtils.isEmpty(iorList)) {
				bizInventoryOrderRequestService.save(ior);
			}
		}
//		if (!bizOrderDetail.getOrdQty().equals(bizOrderDetail.getSentQty())) {
//			orderFlag = false;
//		} else {
//			orderFlag = true;
//		}
		BizOrderDetail orderDetail = new BizOrderDetail();
		orderDetail.setOrderHeader(new BizOrderHeader(orderHeader.getId()));
		List<BizOrderDetail> detailList = bizOrderDetailService.findList(orderDetail);
		if (CollectionUtils.isNotEmpty(detailList)) {
			for (BizOrderDetail bizOrderDetail : detailList) {
				Integer ordQty = bizOrderDetail.getOrdQty();
				Integer id = bizOrderDetail.getId();
				if (!orderDetailMap.get(id).equals(ordQty)) {
					orderFlag = false;
					break;
				}
			}
		}
		if (orderFlag) {
		    int status = orderHeader.getBizStatus();
            orderHeader.setBizStatus(OrderHeaderBizStatusEnum.SEND.getState());
            bizOrderHeaderService.saveOrderHeader(orderHeader);
            if (status < OrderHeaderBizStatusEnum.SEND.getState()) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SELLORDER.getDesc(),BizOrderStatusOrderTypeEnum.SELLORDER.getState(),orderHeader.getId());
            }
        }
		return "ok";
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateSkuId(Integer needSkuId, Integer skuId) {
		BizSendGoodsRecord orderDetail = new BizSendGoodsRecord();
		orderDetail.setSkuInfo(new BizSkuInfo(skuId));
		List<BizSendGoodsRecord> orderDetails = findList(orderDetail);
		if (CollectionUtils.isNotEmpty(orderDetails)) {
			for (BizSendGoodsRecord bizOrderDetail : orderDetails) {
				bizSendGoodsRecordDao.updateSkuId(needSkuId,bizOrderDetail.getId());
			}
		}
	}
}