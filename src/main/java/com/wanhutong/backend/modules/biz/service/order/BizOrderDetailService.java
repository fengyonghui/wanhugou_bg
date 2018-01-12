/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单详情(销售订单)Service
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@Service
@Transactional(readOnly = true)
public class BizOrderDetailService extends CrudService<BizOrderDetailDao, BizOrderDetail> {

	@Autowired
	private BizSkuInfoService bizSkuInfoService;

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;

	@Autowired
	private  BizOrderDetailDao bizOrderDetailDao;

	//用于计算有多少库存
	@Autowired
	private BizInventorySkuService bizInventorySkuService;

	public BizOrderDetail get(Integer id) {
		return super.get(id);
	}

	public List<BizOrderDetail> findList(BizOrderDetail bizOrderDetail) {
		return super.findList(bizOrderDetail);
	}

	public Page<BizOrderDetail> findPage(Page<BizOrderDetail> page, BizOrderDetail bizOrderDetail) {
		return super.findPage(page, bizOrderDetail);
	}

	@Transactional(readOnly = false)
	public void save(BizOrderDetail bizOrderDetail) {
		Integer skuId = bizOrderDetail.getSkuInfo().getId();//sku_info.id
		BizSkuInfo bizSkuInfo = bizSkuInfoService.get(skuId);
		bizOrderDetail.setSkuName(bizSkuInfo.getName());
//		-----------------------------订单状态 专营与非专营------------------------------
		BizOrderHeader orderHeader = new BizOrderHeader();
		BizSkuInfo bizSkuInfo1 = new BizSkuInfo();
		bizSkuInfo1.setId(skuId);
		BizSkuInfo skuInfo = bizSkuInfoService.get(bizSkuInfo1);
		if(skuInfo.getSkuType()==1){//专营商品
			System.out.println("--专营商品--");

			orderHeader.setBizType(skuInfo.getSkuType());
//			bizOrderHeaderService.save(orderHeader);
		}else if(skuInfo.getSkuType()==2){
			System.out.println("-- 定制商品 暂时不处理 --");
		}else if(skuInfo.getSkuType()==3){//非专营商品

			System.out.println("非专营");
			orderHeader.setBizType(skuInfo.getSkuType());
//			bizOrderHeaderService.save(orderHeader);
		}else{
			System.out.println("--- 未知商品 暂不处理 ---");
		}
		Double sum = 0.0;
        BizOrderHeader BizOrderHeader = new BizOrderHeader();
		String oneOrder = bizOrderDetail.getOrderHeader().getOneOrder();//首次下单标记
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
//		------------------------计算订单详情总价----------------------
		List<BizOrderDetail> list = super.findList(bizOrderDetail);
		for (BizOrderDetail detail : list) {
			Double price = detail.getUnitPrice();//商品单价
			Integer ordQty = detail.getOrdQty();//采购数量
			sum+=price*ordQty;
			if(oneOrder.equals("") && oneOrder.equals(null)) {
                System.out.println("--计算优惠--");
                bizOrderHeader.setTotalDetail(orderHeader.getTotalDetail() + sum);
                bizOrderHeaderService.save(bizOrderHeader);
            }else{
                bizOrderHeader.setTotalDetail(orderHeader.getTotalDetail() + sum);
                bizOrderHeaderService.save(bizOrderHeader);
		    }
		}
//		-----------------------------分割一下是订单详情-------------------------------------------
		Integer ordQtyFront = bizOrderDetail.getOrdQtyUpda();//修改前采购数量
		if(ordQtyFront==null){
			ordQtyFront=0;//默认值
		}
		Integer ordQtyAfter = bizOrderDetail.getOrdQty();//修改后采购数量
//		查询有多少库存
		BizInventorySku bizInventorySku = new BizInventorySku();
		bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
		List<BizInventorySku> list2 = bizInventorySkuService.findList(bizInventorySku);
		for (BizInventorySku inventorySku : list2) {
			Integer stockQty = inventorySku.getStockQty();//库存数量
			if(ordQtyFront==ordQtyAfter){
				bizOrderDetail.setOrdQty(ordQtyAfter);
			}else if(ordQtyFront<ordQtyAfter){
				//计算库存数量
				inventorySku.setStockQty(stockQty-(ordQtyAfter-ordQtyFront));
				bizInventorySkuService.save(inventorySku);
				bizOrderDetail.setOrdQty(ordQtyAfter);
			}else{
				//计算库存数量
				inventorySku.setStockQty(stockQty+(ordQtyFront-ordQtyAfter));
				bizInventorySkuService.save(inventorySku);
				bizOrderDetail.setOrdQty(ordQtyAfter);
			}
			//计算库存销售订单数量
			Integer stockOrdNumber = inventorySku.getStockOrdQty();
			inventorySku.setStockOrdQty(++stockOrdNumber);
			bizInventorySkuService.save(inventorySku);
		}
		super.save(bizOrderDetail);
	}

	public Integer findMaxLine(BizOrderDetail bizOrderDetail){
		return bizOrderDetailDao.findMaxLine(bizOrderDetail);
	}

	@Transactional(readOnly = false)
	public void delete(BizOrderDetail bizOrderDetail) {
		//删除执行库存表相应的加减数量
		Integer ordQty = bizOrderDetail.getOrdQty();
		BizInventorySku bizInventorySku = new BizInventorySku();
		bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
		List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
		for (BizInventorySku inventorySku : list) {
			Integer stockQty = inventorySku.getStockQty();
			inventorySku.setStockQty(stockQty+ordQty);
			Integer stockOrdQty = inventorySku.getStockOrdQty();
			inventorySku.setStockOrdQty(--stockOrdQty);
			bizInventorySkuService.save(inventorySku);
		}
		super.delete(bizOrderDetail);
	}

}