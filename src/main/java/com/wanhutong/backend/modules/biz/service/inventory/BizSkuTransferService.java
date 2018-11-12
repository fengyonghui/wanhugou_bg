/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.TransferStatusEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransfer;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSkuTransferDao;

/**
 * 库存调拨Service
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@Service
@Transactional(readOnly = true)
public class BizSkuTransferService extends CrudService<BizSkuTransferDao, BizSkuTransfer> {

	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizSkuTransferDetailService bizSkuTransferDetailService;

	public BizSkuTransfer get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuTransfer> findList(BizSkuTransfer bizSkuTransfer) {
		return super.findList(bizSkuTransfer);
	}
	
	public Page<BizSkuTransfer> findPage(Page<BizSkuTransfer> page, BizSkuTransfer bizSkuTransfer) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(bizSkuTransfer.getSource()) && "out".equals(bizSkuTransfer.getSource())) {
			bizSkuTransfer.getSqlMap().put("transfer", BaseService.dataScopeFilter(user,"outCent","u"));
		}
		if (StringUtils.isNotBlank(bizSkuTransfer.getSource()) && "in".equals(bizSkuTransfer.getSource())) {
			bizSkuTransfer.getSqlMap().put("transfer", BaseService.dataScopeFilter(user,"inCent","u"));
		}
		return super.findPage(page, bizSkuTransfer);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSkuTransfer bizSkuTransfer) {
		if (StringUtils.isBlank(bizSkuTransfer.getSkuIds()) || StringUtils.isBlank(bizSkuTransfer.getTransferNums())) {
			return;
		}
		BizSkuTransfer skuTransfer = new BizSkuTransfer();
		if (bizSkuTransfer.getId() == null) {
			//生成调拨单号
			BizInventoryInfo fromInv = bizInventoryInfoService.get(bizSkuTransfer.getFromInv());
			BizInventoryInfo toInv = bizInventoryInfoService.get(bizSkuTransfer.getToInv());
			int s = dao.findCountByToCent(toInv.getId());
			String transferNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.TR, fromInv.getCustomer().getId(), toInv.getCustomer().getId(), s + 1);
			skuTransfer.setTransferNo(transferNo);
		} else {
			skuTransfer.setId(bizSkuTransfer.getId());
		}
		skuTransfer.setFromInv(bizSkuTransfer.getFromInv());
		skuTransfer.setToInv(bizSkuTransfer.getToInv());
		skuTransfer.setApplyer(UserUtils.getUser());
		skuTransfer.setBizStatus(bizSkuTransfer.getBizStatus() == null ? TransferStatusEnum.UNREVIEWED.getState() : bizSkuTransfer.getBizStatus());
		skuTransfer.setRecvEta(bizSkuTransfer.getRecvEta());
		skuTransfer.setRemark(bizSkuTransfer.getRemark());
		super.save(bizSkuTransfer);
		//调拨单详情
		String skuIds = bizSkuTransfer.getSkuIds();
		String transferNums = bizSkuTransfer.getTransferNums();
		String[] skuIdAttr = StringUtils.split(skuIds, ",");
		String[] transferAttr = StringUtils.split(transferNums, ",");
		for (int i = 0; i < skuIdAttr.length; i++) {
			BizSkuTransferDetail bizSkuTransferDetail = new BizSkuTransferDetail();
			bizSkuTransferDetail.setTransfer(skuTransfer);
			bizSkuTransferDetail.setSkuInfo(new BizSkuInfo(Integer.valueOf(skuIdAttr[i])));
			bizSkuTransferDetail.setTransQty(Integer.valueOf(transferAttr[i]));
			bizSkuTransferDetailService.save(bizSkuTransferDetail);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSkuTransfer bizSkuTransfer) {
		super.delete(bizSkuTransfer);
	}
	
}