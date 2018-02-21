/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.po.BizPoDetailDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.PoOrderReqTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.dao.po.BizPoHeaderDao;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

import static java.util.stream.Collectors.groupingBy;

/**
 * 采购订单表Service
 * @author liuying
 * @version 2017-12-30
 */
@Service
@Transactional(readOnly = true)
public class BizPoHeaderService extends CrudService<BizPoHeaderDao, BizPoHeader> {
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizPoDetailService bizPoDetailService;

	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;


	public BizPoHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizPoHeader> findList(BizPoHeader bizPoHeader) {
		return super.findList(bizPoHeader);
	}
	
	public Page<BizPoHeader> findPage(Page<BizPoHeader> page, BizPoHeader bizPoHeader) {
		return super.findPage(page, bizPoHeader);
	}
	
	@Transactional(readOnly = false)
	public void save(BizPoHeader bizPoHeader) {

		super.save(bizPoHeader);

		savePoHeaderDetail(bizPoHeader);

		saveOrdReqBizStatus(bizPoHeader);

	}

    @Transactional(readOnly = false)
    public void savePoHeader(BizPoHeader bizPoHeader) {

		if(bizPoHeader.getDeliveryStatus()==1){
			BizPoHeader poHeader=get(bizPoHeader.getId());
			bizPoHeader.setDeliveryOffice(poHeader.getVendOffice());
		}
        super.save(bizPoHeader);

    }

	@Transactional(readOnly = false)
	public void savePoHeaderDetail(BizPoHeader bizPoHeader) {
		String orderDetailIds=bizPoHeader.getOrderDetailIds();
		String reqDetailIds=bizPoHeader.getReqDetailIds();
		String unitPrices=bizPoHeader.getUnitPrices();
		String skuInfoIds=bizPoHeader.getSkuInfoIds();
		String ordQtys=bizPoHeader.getOrdQtys();
		BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
		if(StringUtils.isNotBlank(skuInfoIds)){
			String[] skuIdArr=skuInfoIds.split(",");
			String[] ordQtyArr=ordQtys.split(",");
			String[] unitPriceArr=unitPrices.split(",");
			BizPoDetail bizPoDetail=new BizPoDetail();
			int t =0;
			for(int i=0;i<skuIdArr.length;i++){
				bizPoOrderReq.setId(null);
				bizPoDetail.setId(null);
				bizPoDetail.setPoHeader(bizPoHeader);
				BizSkuInfo skuInfo=bizSkuInfoService.get(Integer.parseInt(skuIdArr[i].trim()));
				bizPoDetail.setSkuInfo(skuInfo);
				bizPoDetail.setLineNo(++t);
				bizPoDetail.setPartNo(skuInfo.getPartNo());
				bizPoDetail.setSkuName(skuInfo.getName());
				bizPoDetail.setOrdQty(Integer.parseInt(ordQtyArr[i].trim()));
				bizPoDetail.setUnitPrice(Double.parseDouble(unitPriceArr[i].trim()));
				bizPoDetailService.save(bizPoDetail);
				bizPoDetailService.calculateTotalOrderPrice(bizPoDetail);
				bizPoOrderReq.setPoHeader(bizPoDetail.getPoHeader());
				bizPoOrderReq.setPoLineNo(bizPoDetail.getLineNo());
				if(StringUtils.isNotBlank(orderDetailIds)){
					String[] orderDetailArr=orderDetailIds.split(",");
					if(!"0".equals(orderDetailArr[i].trim())) {
						if(orderDetailArr[i].contains("_")){
						String[] detailArr=orderDetailArr[i].split("_");

								for (int a = 0; a < detailArr.length; a++) {
									if(!"0".equals(detailArr[a].trim())) {
										BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(detailArr[a].trim()));
										bizPoOrderReq.setOrderHeader(bizOrderDetail.getOrderHeader());
										bizPoOrderReq.setRequestHeader(null);
										bizPoOrderReq.setSoLineNo(bizOrderDetail.getLineNo());
										bizPoOrderReq.setSoQty(bizOrderDetail.getOrdQty());
										bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
										bizPoOrderReqService.save(bizPoOrderReq);
									}
								}
							}
							else {
								BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailArr[i].trim()));
								bizPoOrderReq.setOrderHeader(bizOrderDetail.getOrderHeader());
								bizPoOrderReq.setRequestHeader(null);
								bizPoOrderReq.setSoLineNo(bizOrderDetail.getLineNo());
								bizPoOrderReq.setSoQty(bizOrderDetail.getOrdQty());
								bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
								bizPoOrderReqService.save(bizPoOrderReq);
							}
						  }
						}

				if(StringUtils.isNotBlank(reqDetailIds)){
					String[] reqDetailIdArr=reqDetailIds.split(",");
						if(!"0".equals(reqDetailIdArr[i].trim())){
							if(reqDetailIdArr[i].contains("_")) {
								String[] detailArr = reqDetailIdArr[i].split("_");
								for (int a = 0; a < detailArr.length; a++) {
									if(!"0".equals(detailArr[a].trim())) {
										BizRequestDetail requestDetail=bizRequestDetailService.get(Integer.parseInt(detailArr[a].trim()));
										bizPoOrderReq.setRequestHeader(requestDetail.getRequestHeader());
										bizPoOrderReq.setOrderHeader(null);
										bizPoOrderReq.setSoLineNo(requestDetail.getLineNo());
										bizPoOrderReq.setSoQty(requestDetail.getReqQty());
										bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType()));
										bizPoOrderReqService.save(bizPoOrderReq);
									}
								}
							}else {
								BizRequestDetail requestDetail=bizRequestDetailService.get(Integer.parseInt(reqDetailIdArr[i].trim()));
								bizPoOrderReq.setRequestHeader(requestDetail.getRequestHeader());
								bizPoOrderReq.setOrderHeader(null);
								bizPoOrderReq.setSoLineNo(requestDetail.getLineNo());
								bizPoOrderReq.setSoQty(requestDetail.getReqQty());
								bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType()));
								bizPoOrderReqService.save(bizPoOrderReq);
							}

					}
				}

			}

		}
	}

	@Transactional(readOnly = false)
	public void saveOrdReqBizStatus(BizPoHeader bizPoHeader){
		BizPoOrderReq bizPoOrderReq=new BizPoOrderReq();
		BizOrderDetail bizOrderDetail=new BizOrderDetail();
		BizRequestDetail bizRequestDetail=new BizRequestDetail();
		bizPoOrderReq.setPoHeader(bizPoHeader);
		List<BizPoOrderReq> poOrderReqList=bizPoOrderReqService.findList(bizPoOrderReq);
		Map<Integer, List<BizPoOrderReq>> collectOrder = poOrderReqList.stream().filter(item -> item.getSoType()==Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())).collect(groupingBy(BizPoOrderReq::getSoId));
		Map<Integer, List<BizPoOrderReq>> collectReq = poOrderReqList.stream().filter(item -> item.getSoType()== Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())).collect(groupingBy(BizPoOrderReq::getSoId));
		 for (Map.Entry<Integer, List<BizPoOrderReq>> entry : collectOrder.entrySet()) {
		 	BizOrderHeader bizOrderHeader=	bizOrderHeaderService.get(entry.getKey());
		 	bizOrderDetail.setOrderHeader(bizOrderHeader);
		 	List<BizOrderDetail> orderDetailList=bizOrderDetailService.findList(bizOrderDetail);
			        if(orderDetailList.size()==entry.getValue().size()){
						bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
						bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
			        }else if(orderDetailList.size()>entry.getValue().size()){
						bizPoOrderReq.setOrderHeader(bizOrderHeader);
						bizPoOrderReq.setRequestHeader(null);
						bizPoOrderReq.setPoHeader(null);
						bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
						List<BizPoOrderReq> poOrderReqs=bizPoOrderReqService.findList(bizPoOrderReq);
						if(poOrderReqs.size()==orderDetailList.size()){
							bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
							bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
						}else {
							bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
							bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
						}
			        }


			 }
		for (Map.Entry<Integer, List<BizPoOrderReq>> entry : collectReq.entrySet())
		{
			BizRequestHeader bizRequestHeader=	bizRequestHeaderService.get(entry.getKey());
			bizRequestDetail.setRequestHeader(bizRequestHeader);
			List<BizRequestDetail> requestDetailList=bizRequestDetailService.findList(bizRequestDetail);
			if(requestDetailList.size()==entry.getValue().size()){
				bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
				bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
			}else if(requestDetailList.size()>entry.getValue().size()){
				bizPoOrderReq.setRequestHeader(bizRequestHeader);
				bizPoOrderReq.setOrderHeader(null);
				bizPoOrderReq.setPoHeader(null);
				bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType()));
				List<BizPoOrderReq> poOrderReqs=bizPoOrderReqService.findList(bizPoOrderReq);
				if(poOrderReqs.size()==requestDetailList.size()){
					bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
					bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
				}else {
					bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.PURCHASING.getState());
					bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
				}
			}

		}

	}

	/**
	 * 采购单供货完成时，更改采购单状态
	 * @param bizPoHeader
	 */
	@Transactional(readOnly = false)
	public  void saveStatus(BizPoHeader bizPoHeader){
		super.save(bizPoHeader);
	}
	@Transactional(readOnly = false)
	public void delete(BizPoHeader bizPoHeader) {
		super.delete(bizPoHeader);
	}
	
}