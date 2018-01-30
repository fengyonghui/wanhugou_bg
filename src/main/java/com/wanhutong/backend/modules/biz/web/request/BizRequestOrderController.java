/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 备货清单Controller
 * @author liuying
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestOrder")
public class BizRequestOrderController extends BaseController {

	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;


	@RequiresPermissions("biz:request:selecting:supplier:view")
	@RequestMapping(value = {"list", ""})
	public String list(String source, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader) {
		List<BizRequestHeader> requestHeaderList=null;
		List<BizOrderHeader> orderHeaderList=null;
		if("bhgh".equals(source)){
			List<BizRequestHeader> list=findBizRequest(bizRequestHeader,requestHeaderList);
			 model.addAttribute("requestHeaderList",list);
			}else if("xsgh".equals(source)){
			bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.APPROVE.getState());
			bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
			 orderHeaderList=bizOrderHeaderService.findList(bizOrderHeader);
			model.addAttribute("orderHeaderList",orderHeaderList);
			}

		model.addAttribute("source",source);
		return "modules/biz/request/bizRequestOrderList";
	}
	private List<BizRequestHeader>findBizRequest(BizRequestHeader bizRequestHeader,List<BizRequestHeader> requestHeaderList){
		bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
		bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
		requestHeaderList= bizRequestHeaderService.findList(bizRequestHeader);
		List<BizRequestHeader> list=Lists.newArrayList();
		for (BizRequestHeader bizRequestHeader1:requestHeaderList) {
			BizRequestDetail bizRequestDetail1 = new BizRequestDetail();
			bizRequestDetail1.setRequestHeader(bizRequestHeader1);
			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail1);
			Integer reqQtys = 0;
			Integer recvQtys = 0;
			for (BizRequestDetail bizRequestDetail:requestDetailList) {
				reqQtys += bizRequestDetail.getReqQty();
				recvQtys += bizRequestDetail.getRecvQty();
			}
			bizRequestHeader1.setReqQtys(reqQtys.toString());
			bizRequestHeader1.setRecvQtys(recvQtys.toString());
			list.add(bizRequestHeader1);
		}
		return list;
	}
	@RequiresPermissions("biz:request:selecting:supplier:view")
	@RequestMapping(value = {"form", ""})
	public String form(String source, Model model){
		Map<String,String> map = new HashMap<String, String>();
		if("gh".equals(source)){
			BizRequestHeader bizRequestHeader=new BizRequestHeader();
			bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
			bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
			List<BizRequestDetail> requestDetailList=bizRequestDetailService.findReqTotalByVendor(bizRequestHeader);

			BizOrderHeader bizOrderHeader=new BizOrderHeader();
			bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.APPROVE.getState());
			bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
			List<BizOrderDetail> orderDetailList=bizOrderDetailService.findOrderTotalByVendor(bizOrderHeader);

			String key="0";
			for(BizRequestDetail bizRequestDetail:requestDetailList){
				key=bizRequestDetail.getVendorId()+","+bizRequestDetail.getVendorName();
				Integer reqQty=bizRequestDetail.getTotalReqQty()-bizRequestDetail.getTotalRecvQty();
				map.put(key,reqQty+"-"+bizRequestDetail.getReqDetailIds());

			}

			for (BizOrderDetail bizOrderDetail:orderDetailList){
				key=bizOrderDetail.getVendorId()+","+bizOrderDetail.getVendorName();
				Integer ordQty=bizOrderDetail.getTotalReqQty()-bizOrderDetail.getTotalSendQty();
				if(map.containsKey(key)){
					String str=map.get(key)+"|"+ordQty+"-"+bizOrderDetail.getDetailIds();
					map.remove(key);
					map.put(key,str);
				}else {
					map.put(key,ordQty+"-"+bizOrderDetail.getDetailIds());
				}
			}

		}
		model.addAttribute("source",source);
		model.addAttribute("map",map);
		return "modules/biz/request/bizRequestPoList";
	}

	@RequestMapping(value = "goList")
	public String goList(String reqIds,String ordIds,Model model){
		List<BizRequestDetail> requestDetailList=null;
		List<BizOrderDetail> orderDetailList=null;
		if(reqIds!=null){
			 requestDetailList=Lists.newArrayList();
			String[] reqDetailArr= reqIds.split(",");
			for(int i=0;i<reqDetailArr.length;i++){
				BizRequestDetail bizRequestDetail=bizRequestDetailService.get(Integer.parseInt(reqDetailArr[i].trim()));
				BizSkuInfo bizSkuInfo=bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
				BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfo);
				if(skuInfo==null){
					continue;
				}
				bizRequestDetail.setSkuInfo(skuInfo);
				requestDetailList.add(bizRequestDetail);
			}
		}
		if(ordIds!=null){
			 orderDetailList=Lists.newArrayList();
			String[] ordDetailArr= reqIds.split(",");
			for(int i=0;i<ordDetailArr.length;i++){
				BizOrderDetail bizOrderDetail=bizOrderDetailService.get(Integer.parseInt(ordDetailArr[i].trim()));
				BizSkuInfo bizSkuInfo=bizOrderDetail.getSkuInfo();
				BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfo);
				bizOrderDetail.setSkuInfo(skuInfo);
				orderDetailList.add(bizOrderDetail);
			}
		}
		model.addAttribute("requestDetailList",requestDetailList);
		model.addAttribute("orderDetailList",orderDetailList);
		model.addAttribute("bizPoHeader",new BizPoHeader());

		return "modules/biz/po/bizPoHeaderForm";
	}
	@RequiresPermissions("biz:request:selecting:supplier:edit")
	@RequestMapping(value = "genSkuOrder")
	public String genSkuOrder(String reqIds,String orderIds,Model model){
		Map<Integer,List<BizSkuInfo>> map=new HashMap<>();
		Map<Integer,List<BizSkuInfo>> skuInfoMap=null;

		Map<BizSkuInfo,BizSkuInfo> skuMap=new HashMap<>();

		if(StringUtils.isNotBlank(reqIds)){
			String [] str=StringUtils.split(reqIds,",");
			for(int i=0;i<str.length;i++){
				BizRequestDetail bizRequestDetail=new BizRequestDetail();
				bizRequestDetail.setRequestHeader(bizRequestHeaderService.get(Integer.parseInt(str[i].trim())));
				List<BizRequestDetail> requestDetailList=bizRequestDetailService.findList(bizRequestDetail);
				for(BizRequestDetail requestDetail:requestDetailList) {
					BizSkuInfo bizSkuInfo=new BizSkuInfo();

					Integer key = requestDetail.getSkuInfo().getId();

					bizSkuInfo.setReqQty(requestDetail.getReqQty());
					bizSkuInfo.setSentQty(requestDetail.getRecvQty());
					bizSkuInfo.setReqIds(str[i].trim());
					skuInfoMap=getSkuInfoData(map,key,bizSkuInfo);
				}
			}

		}
		if(StringUtils.isNotBlank(orderIds)){
			String[] str= StringUtils.split(orderIds,",");
			for(int i=0;i<str.length;i++){
				BizOrderDetail bizOrderDetail=new BizOrderDetail();
				bizOrderDetail.setOrderHeader(bizOrderHeaderService.get(Integer.parseInt(str[i].trim())));
				List<BizOrderDetail> orderDetailList=bizOrderDetailService.findList(bizOrderDetail);
				for(BizOrderDetail orderDetail:orderDetailList) {
					BizSkuInfo bizSkuInfo=new BizSkuInfo();
					Integer key = orderDetail.getSkuInfo().getId();
					bizSkuInfo.setReqQty(orderDetail.getOrdQty());
					bizSkuInfo.setSentQty(orderDetail.getSentQty());
					bizSkuInfo.setOrderIds(str[i].trim());
					skuInfoMap= getSkuInfoData(map,key,bizSkuInfo);
				}
			}
		}

		for (Map.Entry<Integer, List<BizSkuInfo>> entry : skuInfoMap.entrySet()) {
			BizSkuInfo skuInfo= bizSkuInfoService.findListProd(bizSkuInfoService.get(entry.getKey()));
			List<BizSkuInfo> bizSkuInfoList=entry.getValue();
			Integer reqQty=bizSkuInfoList.stream().mapToInt(item -> item.getReqQty()==null?0:item.getReqQty()).sum();
			Integer sendQty=bizSkuInfoList.stream().mapToInt(item -> item.getSentQty()==null?0:item.getSentQty()).sum();
			StringBuilder strOrderIds = new StringBuilder();
			StringBuilder strReqIds = new StringBuilder();
			for (BizSkuInfo sku:bizSkuInfoList){
				strOrderIds.append(sku.getOrderIds()==null?0:sku.getOrderIds());
				strOrderIds.append(",");
				strReqIds.append(sku.getReqIds()==null?0:sku.getReqIds());
				strReqIds.append(",");
			}
			String strO= strOrderIds.toString();
			String strR=strReqIds.toString();
			BizSkuInfo sku=bizSkuInfoService.get(entry.getKey());
			sku.setReqQty(reqQty);
			sku.setSentQty(sendQty);
			sku.setOrderIds(strO.substring(0,strO.length()-1));
			sku.setReqIds(strR.substring(0,strR.length()-1));
			skuMap.put(skuInfo,sku);
		}

		model.addAttribute("skuInfoMap",skuMap);

		return "modules/biz/request/bizSkuInfoGhForm";
	}
	private Map<Integer,List<BizSkuInfo>> getSkuInfoData(Map<Integer,List<BizSkuInfo>> map,Integer key,BizSkuInfo bizSkuInfo){
		// BizSkuInfo bizSkuInfo= bizSkuInfoService.findListProd(bizSkuInfoService.get(key));
		if(map.containsKey(key)){
			List<BizSkuInfo> skuInfos = map.get(key);
			map.remove(key);
			skuInfos.add(bizSkuInfo);
			map.put(key,skuInfos);
		}
		else {
			List<BizSkuInfo>skuInfoList=new ArrayList<BizSkuInfo>();
			skuInfoList.add(bizSkuInfo);
			map.put(key,skuInfoList);
		}
		return map;
	}


}