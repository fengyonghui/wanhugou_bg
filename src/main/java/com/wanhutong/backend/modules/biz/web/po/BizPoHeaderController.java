/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.PoOrderReqTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购订单表Controller
 * @author liuying
 * @version 2017-12-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizPoHeader")
public class BizPoHeaderController extends BaseController {

	@Autowired
	private BizPoHeaderService bizPoHeaderService;
	@Autowired
	private BizPoDetailService bizPoDetailService;
	@Autowired
	private BizPlatformInfoService bizPlatformInfoService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private OfficeService officeService;
	@ModelAttribute
	public BizPoHeader get(@RequestParam(required=false) Integer id) {
		BizPoHeader entity = null;
		if (id!=null){
			entity = bizPoHeaderService.get(id);
			BizPoDetail bizPoDetail=new BizPoDetail();
			bizPoDetail.setPoHeader(entity);
			List<BizPoDetail> poDetailList=bizPoDetailService.findList(bizPoDetail);
			List<BizPoDetail> poDetails= Lists.newArrayList();
			for(BizPoDetail poDetail:poDetailList){
			    BizSkuInfo bizSkuInfo=poDetail.getSkuInfo();
                BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(bizSkuInfo.getId()));
                poDetail.setSkuInfo(skuInfo);
                poDetails.add(poDetail);
            }
			entity.setPoDetailList(poDetails);
			BizPoOrderReq bizPoOrderReq=new BizPoOrderReq();
			bizPoOrderReq.setPoHeader(entity);
			List<BizPoOrderReq> poOrderReqList=bizPoOrderReqService.findList(bizPoOrderReq);
			List<Map<String,Integer>> poOrderReqs= Lists.newArrayList();
			BizOrderDetail bizOrderDetail=new BizOrderDetail();
			BizRequestDetail bizRequestDetail=new BizRequestDetail();
			Map<Integer,List<BizPoOrderReq>> map=new HashMap<>();
			Map<String,Integer> mapSource=new HashMap<>();
			for (BizPoOrderReq poOrderReq:poOrderReqList){
				if(poOrderReq.getSoType()== Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())){
					BizOrderHeader bizOrderHeader=bizOrderHeaderService.get(poOrderReq.getSoId());
					String numKey=bizOrderHeader.getOrderNum();
					if(mapSource.containsKey(numKey)){
						int count = mapSource.get(numKey);
						mapSource.remove(numKey);
						mapSource.put(numKey,count+1);
					}else {
						mapSource.put(numKey,1);
					}
					poOrderReq.setOrderHeader(bizOrderHeader);
					bizOrderDetail.setOrderHeader(bizOrderHeader);
					bizOrderDetail.setLineNo(poOrderReq.getSoLineNo());
					List<BizOrderDetail> bizOrderDetailList=bizOrderDetailService.findList(bizOrderDetail);
					if(bizOrderDetailList!=null && bizOrderDetailList.size()!=0){
						BizOrderDetail orderDetail=bizOrderDetailList.get(0);
						Integer key=orderDetail.getSkuInfo().getId();
						if(map.containsKey(key)){
							List<BizPoOrderReq> bizPoOrderReqList= map.get(key);

							map.remove(key);

							String orderNumStr=bizOrderHeader.getOrderNum();
							poOrderReq.setOrderNumStr(orderNumStr);
							bizPoOrderReqList.add(poOrderReq);
							map.put(orderDetail.getSkuInfo().getId(),bizPoOrderReqList);
						}else {
							List<BizPoOrderReq> bizPoOrderReqList=Lists.newArrayList();
							String orderNumStr=bizOrderHeader.getOrderNum();
							poOrderReq.setOrderNumStr(orderNumStr);
							bizPoOrderReqList.add(poOrderReq);
							map.put(orderDetail.getSkuInfo().getId(),bizPoOrderReqList);
						}

					}
				}else if(poOrderReq.getSoType()==Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())){
					BizRequestHeader bizRequestHeader=bizRequestHeaderService.get(poOrderReq.getSoId());
					String reqKey=bizRequestHeader.getReqNo();
					if(mapSource.containsKey(reqKey)){
						int count = mapSource.get(reqKey);
						mapSource.remove(reqKey);
						mapSource.put(reqKey,count+1);
					}else {
						mapSource.put(reqKey,1);
					}
					poOrderReq.setRequestHeader(bizRequestHeader);
					bizRequestDetail.setRequestHeader(bizRequestHeader);
					bizRequestDetail.setLineNo(poOrderReq.getSoLineNo());
					List<BizRequestDetail> requestDetailList=bizRequestDetailService.findList(bizRequestDetail);
					if(requestDetailList!=null && requestDetailList.size()!=0){
						BizRequestDetail requestDetail=requestDetailList.get(0);
						Integer key=requestDetail.getSkuInfo().getId();
						if(map.containsKey(key)){

							List<BizPoOrderReq> bizPoOrderReqList= map.get(key);
							map.remove(key);
							poOrderReq.setOrderNumStr(bizRequestHeader.getReqNo());
							bizPoOrderReqList.add(poOrderReq);
							map.put(requestDetail.getSkuInfo().getId(),bizPoOrderReqList);

						}else {
							String orderNumStr=bizRequestHeader.getReqNo();
							poOrderReq.setOrderNumStr(orderNumStr);
							List<BizPoOrderReq> bizPoOrderReqList=Lists.newArrayList();
							bizPoOrderReqList.add(poOrderReq);
							map.put(requestDetail.getSkuInfo().getId(),bizPoOrderReqList);
						}


					}
				}

				//	poOrderReqs.add(mapSource);
			}

			entity.setOrderSourceMap(mapSource);



			entity.setOrderNumMap(map);
		}
		if (entity == null){
			entity = new BizPoHeader();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:po:bizPoHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPoHeader bizPoHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<BizPoHeader>(request, response), bizPoHeader); 
		model.addAttribute("page", page);
		return "modules/biz/po/bizPoHeaderList";
	}

	@RequiresPermissions("biz:po:bizPoHeader:view")
	@RequestMapping(value = "form")
	public String form(BizPoHeader bizPoHeader, Model model, String prewStatus) {
		if(bizPoHeader.getDeliveryOffice()!=null && bizPoHeader.getDeliveryOffice().getId()!=null && bizPoHeader.getDeliveryOffice().getId()!=0){
			Office office=officeService.get(bizPoHeader.getDeliveryOffice().getId());
			if("8".equals(office.getType())){
				bizPoHeader.setDeliveryStatus(0);
			}else {
				bizPoHeader.setDeliveryStatus(1);
			}
		}
		model.addAttribute("bizPoHeader", bizPoHeader);
		model.addAttribute("prewStatus", prewStatus);
		return "modules/biz/po/bizPoHeaderForm";
	}

	@RequiresPermissions("biz:po:bizPoHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes, String prewStatus) {
		if (!beanValidator(model, bizPoHeader)){
			return form(bizPoHeader, model, prewStatus);
		}
		int deOfifceId=0;
		if(bizPoHeader.getDeliveryOffice()!=null && bizPoHeader.getDeliveryOffice().getId()!=null){
			deOfifceId=bizPoHeader.getDeliveryOffice().getId();
		}
		String poNo="0";
		bizPoHeader.setOrderNum(poNo);
		bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
		bizPoHeader.setIsPrew("prew".equals(prewStatus) ? 1 : 0);
		bizPoHeaderService.save(bizPoHeader);
		if(bizPoHeader.getOrderNum()==null || "0".equals(bizPoHeader.getOrderNum())){
			poNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO,deOfifceId,bizPoHeader.getVendOffice().getId(),bizPoHeader.getId());
			bizPoHeader.setOrderNum(poNo);
			bizPoHeaderService.savePoHeader(bizPoHeader);
		}

		addMessage(redirectAttributes, "prew".equals(prewStatus) ? "采购订单预览信息" : "保存采购订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoHeader/form/?id=" + bizPoHeader.getId() + "&prewStatus=" + prewStatus;
	}

	@RequiresPermissions("biz:po:bizPoHeader:edit")
	@RequestMapping(value = "savePoHeader")
	public String savePoHeader(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes, String prewStatus) {
		if (!beanValidator(model, bizPoHeader)){
			return form(bizPoHeader, model, prewStatus);
		}
		bizPoHeader.setIsPrew("prew".equals(prewStatus) ? 1 : 0);
		bizPoHeaderService.savePoHeader(bizPoHeader);

		addMessage(redirectAttributes, "保存采购订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoHeader/?repage";
	}
	@RequiresPermissions("biz:po:bizPoHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPoHeader bizPoHeader, RedirectAttributes redirectAttributes) {
		bizPoHeaderService.delete(bizPoHeader);
		addMessage(redirectAttributes, "删除采购订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoHeader/?repage";
	}

}