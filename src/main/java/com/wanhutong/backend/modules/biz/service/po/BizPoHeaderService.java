/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.po.BizPoDetailDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
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

/**
 * 采购订单表Service
 * @author liuying
 * @version 2017-12-30
 */
@Service
@Transactional(readOnly = true)
public class BizPoHeaderService extends CrudService<BizPoHeaderDao, BizPoHeader> {
	@Resource
	private BizPlatformInfoService bizPlatformInfoService;
	@Resource
	private OfficeService officeService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizPoDetailService bizPoDetailService;

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;


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
	}
	@Transactional(readOnly = false)
	public void savePoHeaderDetail(BizPoHeader bizPoHeader) {
		if (bizPoHeader.getPoDetailList() != null) {
			Map<Integer, List<BizPoDetail>> map = new HashMap<Integer, List<BizPoDetail>>();
			for (BizPoDetail bizPoDetail : bizPoHeader.getPoDetailList()) {
				if (bizPoDetail.getPoHeader() == null) {
					continue;
				}
				Integer key = bizPoDetail.getPoHeader().getVendOffice().getId();
				if (map.containsKey(key)) {
					List<BizPoDetail> poDetails = map.get(key);
					map.remove(key);
					poDetails.add(bizPoDetail);
					map.put(key, poDetails);
				} else {
					List<BizPoDetail> poDetailList = new ArrayList<BizPoDetail>();
					poDetailList.add(bizPoDetail);
					map.put(key, poDetailList);
				}
			}
			bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
			for (Map.Entry<Integer, List<BizPoDetail>> entry : map.entrySet()) {
				System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
				bizPoHeader.setVendOffice(officeService.get(entry.getKey()));
				String poNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO, bizPoHeader.getVendOffice().getId());
				bizPoHeader.setOrderNum(poNo);
				super.save(bizPoHeader);
				int a = 0;
				Double totalDetail = 0.0;
				for (BizPoDetail poDetail : entry.getValue()) {
					poDetail.setLineNo(++a);
					poDetail.setPoHeader(bizPoHeader);
					BizSkuInfo bizSkuInfo = bizSkuInfoService.get(poDetail.getSkuInfo().getId());
					poDetail.setSkuName(bizSkuInfo.getName());
					poDetail.setPartNo(bizSkuInfo.getPartNo());
					bizPoDetailService.save(poDetail);
					totalDetail += poDetail.getOrdQty() * poDetail.getUnitPrice();

				}

				bizPoHeader.setTotalDetail(totalDetail);
				super.save(bizPoHeader);
			}

			String orders = bizPoHeader.getOrderIds();
			String[] orderIds = orders.split(",");
			String reqs = bizPoHeader.getReqIds();
			String[] reqIds = StringUtils.split(reqs, ",");
			logger.info(orders + "======" + reqs);
			for (int i = 0; i < orderIds.length; i++) {
				BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
				if (!"0".equals(orderIds[i])) {
					BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(Integer.parseInt(orderIds[i].trim()));
					bizPoOrderReq.setOrderHeader(bizOrderHeader);
					bizPoOrderReq.setPoHeader(bizPoHeader);
					bizPoOrderReqService.save(bizPoOrderReq);

				}
			}
			for (int i = 0; i < reqIds.length; i++) {
				BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
				if (!"0".equals(reqIds[i])) {
					BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(Integer.parseInt(reqIds[i].trim()));
					bizPoOrderReq.setRequestHeader(bizRequestHeader);
					bizPoOrderReq.setPoHeader(bizPoHeader);
					bizPoOrderReqService.save(bizPoOrderReq);
				}
			}

			BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
			bizPoOrderReq.setPoHeader(bizPoHeader);
			List<BizPoOrderReq> poOrderReqList=bizPoOrderReqService.findList(bizPoOrderReq);
				BizOrderDetail bizOrderDetail=new BizOrderDetail();
				Integer minOrdQty=0;
				int a=0;
				for (BizPoOrderReq poOrderReq:poOrderReqList){
					 Integer ohId=poOrderReq.getOrderHeader().getId();
					 Integer reqId=poOrderReq.getRequestHeader().getId();
					if(ohId!=0){
						bizOrderDetail.setId(null);
						BizOrderHeader bizOrderHeader=bizOrderHeaderService.get(ohId);
						bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.APPROVE.getState());
						bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
						bizOrderDetail.setOrderHeader(bizOrderHeader);
						List<BizOrderDetail> bizOrderDetailList=bizOrderDetailService.findList(bizOrderDetail);
						BizPoDetail bizPoDetail=new BizPoDetail();
						for(BizOrderDetail orderDetail:bizOrderDetailList){
							a++;
							bizPoDetail.setId(null);
							bizPoDetail.setSkuInfo(orderDetail.getSkuInfo());
							bizPoDetail.setPoHeader(bizPoOrderReq.getPoHeader());
							List<BizPoDetail> poDetailList=bizPoDetailService.findList(bizPoDetail);
							Integer poOrdQty=poDetailList.stream().mapToInt(item -> item.getOrdQty()).sum();
							Integer ordQty=orderDetail.getOrdQty();
							Integer sentQty=orderDetail.getSentQty();
							if(a==1){
								minOrdQty=poOrdQty-(ordQty-sentQty);
							}else {
								minOrdQty-=(ordQty-sentQty);
							}
						}

						if(minOrdQty>=0){
							bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
							bizOrderHeaderService.save(bizOrderHeader);
						}
						else if(minOrdQty<0){
							bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
							bizOrderHeaderService.save(bizOrderHeader);
						}

					}


				}

			}

//			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.STOCKING.ordinal());
//			super.save(bizRequestHeader);
		}


	
	@Transactional(readOnly = false)
	public void delete(BizPoHeader bizPoHeader) {
		super.delete(bizPoHeader);
	}
	
}