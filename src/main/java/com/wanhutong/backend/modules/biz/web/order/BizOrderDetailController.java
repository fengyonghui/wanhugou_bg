/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderSkuPropValue;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderSkuPropValueService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.enums.DefaultPropEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import com.wanhutong.backend.modules.sys.service.attribute.AttributeValueV2Service;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.List;
import java.util.Map;

/**
 * 订单详情(销售订单)Controller
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderDetail")
public class BizOrderDetailController extends BaseController {

	@Autowired
	private BizOrderDetailService bizOrderDetailService;
	@Autowired
	private BizOpShelfInfoService bizOpShelfInfoService;
//	@Autowired
//	private BizOrderDetailDao bizOrderDetailDao;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizOrderSkuPropValueService bizOrderSkuPropValueService;
	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@Autowired
	private BizSkuInfoV2Service bizSkuInfoV2Service;
	@Autowired
	private AttributeValueV2Service attributeValueV2Service;

	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = "findSysBySku")
	public List<BizOrderDetail> findSysBySku(BizOrderDetail bizOrderDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<BizOrderDetail> list = bizOrderDetailService.findList(bizOrderDetail);
		return list;
	}

	@ModelAttribute
	public BizOrderDetail get(@RequestParam(required=false) Integer id) {
		BizOrderDetail entity = null;
		if (id!=null){
			entity = bizOrderDetailService.get(id);
			BizOrderDetail orderDetail = new BizOrderDetail();
			orderDetail.setOrderHeader(entity.getOrderHeader());
			List<BizOrderDetail> list = bizOrderDetailService.findList(orderDetail);
			entity.setOrderHeaderList(list);//用于修改商品查询有多少商品
		}
		if (entity == null){
			entity = new BizOrderDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderDetail bizOrderDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderDetail> page = bizOrderDetailService.findPage(new Page<BizOrderDetail>(request, response), bizOrderDetail); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderDetailList";
	}

	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = "form")
	public String form(BizOrderDetail bizOrderDetail, Model model) {
		bizOrderDetail.setOrdQtyUpda(bizOrderDetail.getOrdQty());
        BizOrderHeader orderHeader = bizOrderDetail.getOrderHeader();
        if(orderHeader!=null){
            if (bizOrderDetail.getSkuInfo() != null) {
                if (bizOrderDetail.getOrderHeader().getOrderType() != null &&
                    bizOrderDetail.getOrderHeader().getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                    BizSkuInfo skuInfo = bizSkuInfoV2Service.get(bizOrderDetail.getSkuInfo().getId());
                    model.addAttribute("skuInfo",skuInfo);
                }
            }
            BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(orderHeader.getId());
            Office customer = bizOrderHeader.getCustomer();
            model.addAttribute("customer",customer);
            BizOrderHeader ord = bizOrderHeaderService.get(orderHeader.getId());
			model.addAttribute("orderH", ord);//用于页面订单供货中显示供货数量
			model.addAttribute("entity", bizOrderDetail);
			model.addAttribute("bizOpShelfSku",new BizOpShelfSku());
		}
//		订单详情修改按钮显示品规色
		BizOrderDetail detailOrder = bizOrderDetailService.get(bizOrderDetail);
        if(detailOrder!=null){
			BizOpShelfSku opShelfSku=bizOpShelfSkuService.get(bizOrderDetail.getShelfInfo().getId());
			detailOrder.setShelfInfo(opShelfSku);
			AttributeValueV2 valueV2 = new AttributeValueV2();
			valueV2.setObjectId(detailOrder.getSkuInfo().getId());
			valueV2.setObjectName("biz_sku_info");
            List<AttributeValueV2> attributeValueV2List = attributeValueV2Service.findList(valueV2);
            detailOrder.setAttributeValueV2List(attributeValueV2List);
        }
		model.addAttribute("detail", detailOrder);
//		现价，销售区间
		if(bizOrderDetail.getShelfInfo()!=null){
			BizOpShelfSku bizOpShelfSku = bizOpShelfSkuService.get(bizOrderDetail.getShelfInfo());
			model.addAttribute("shelfSku", bizOpShelfSku);
		}
		return "modules/biz/order/bizOrderDetailForm";
	}

	@RequiresPermissions("biz:order:bizOrderDetail:view")
	@RequestMapping(value = "form4Mobile")
	@ResponseBody
	public String form4Mobile(BizOrderDetail bizOrderDetail, Model model) {
		Map<String, Object> resultMap = Maps.newHashMap();
		bizOrderDetail.setOrdQtyUpda(bizOrderDetail.getOrdQty());
		BizOrderHeader orderHeader = bizOrderDetail.getOrderHeader();
		if(orderHeader!=null){
			if (bizOrderDetail.getSkuInfo() != null) {
				if (bizOrderDetail.getOrderHeader().getOrderType() != null &&
						bizOrderDetail.getOrderHeader().getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
					BizSkuInfo skuInfo = bizSkuInfoV2Service.get(bizOrderDetail.getSkuInfo().getId());
					model.addAttribute("skuInfo",skuInfo);
					resultMap.put("skuInfo", skuInfo);
				}
			}
			BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(orderHeader.getId());
			Office customer = bizOrderHeader.getCustomer();
			model.addAttribute("customer",customer);
			resultMap.put("customer", customer);
			BizOrderHeader ord = bizOrderHeaderService.get(orderHeader.getId());
			model.addAttribute("orderH", ord);//用于页面订单供货中显示供货数量
			model.addAttribute("entity", bizOrderDetail);
			model.addAttribute("bizOpShelfSku",new BizOpShelfSku());

			resultMap.put("orderH", ord);
			resultMap.put("bizOrderDetail", bizOrderDetail);
			resultMap.put("bizOpShelfSku", new BizOpShelfSku());
		}
//		订单详情修改按钮显示品规色
		BizOrderDetail detailOrder = bizOrderDetailService.get(bizOrderDetail);
		if(detailOrder!=null){
			BizOpShelfSku opShelfSku=bizOpShelfSkuService.get(bizOrderDetail.getShelfInfo().getId());
			detailOrder.setShelfInfo(opShelfSku);
			AttributeValueV2 valueV2 = new AttributeValueV2();
			valueV2.setObjectId(detailOrder.getSkuInfo().getId());
			valueV2.setObjectName("biz_sku_info");
			List<AttributeValueV2> attributeValueV2List = attributeValueV2Service.findList(valueV2);
			detailOrder.setAttributeValueV2List(attributeValueV2List);
		}
		model.addAttribute("detail", detailOrder);
		resultMap.put("detail", detailOrder);
//		现价，销售区间
		if(bizOrderDetail.getShelfInfo()!=null){
			BizOpShelfSku bizOpShelfSku = bizOpShelfSkuService.get(bizOrderDetail.getShelfInfo());
			model.addAttribute("shelfSku", bizOpShelfSku);
			resultMap.put("shelfSku", bizOpShelfSku);
		}
		return JsonUtil.generateData(resultMap, null);
	}

	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderDetail bizOrderDetail, Model model, RedirectAttributes redirectAttributes) {
		bizOrderDetailService.save(bizOrderDetail);
		addMessage(redirectAttributes, "保存订单详情成功");
		Integer orderId=bizOrderDetail.getOrderHeader().getId();
		String consultantId ="";
		String a="header_save";
		if(bizOrderDetail.getOrderHeader()!=null && bizOrderDetail.getOrderHeader().getConsultantId()!=null){
			consultantId = String.valueOf(bizOrderDetail.getOrderHeader().getConsultantId());
		}
		if(bizOrderDetail.getOrderHeader().getClientModify()!=null && bizOrderDetail.getOrderHeader().getClientModify().equals("client_modify")){
			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId+"&clientModify=client_modify"+
					"&consultantId="+consultantId;
		}else if(bizOrderDetail.getDetailFlag()!=null && bizOrderDetail.getDetailFlag().equals(a)){
			//跳回C端
			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/cendform?id="+orderId;
		}
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId;
	}

	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "save4Mobile")
	@ResponseBody
	public String save4Mobile(BizOrderDetail bizOrderDetail, Model model, RedirectAttributes redirectAttributes) {
		bizOrderDetailService.save(bizOrderDetail);
		addMessage(redirectAttributes, "保存订单详情成功");
		Integer orderId=bizOrderDetail.getOrderHeader().getId();
		String consultantId ="";
		String a="header_save";
		if(bizOrderDetail.getOrderHeader()!=null && bizOrderDetail.getOrderHeader().getConsultantId()!=null){
			consultantId = String.valueOf(bizOrderDetail.getOrderHeader().getConsultantId());
		}
//		if(bizOrderDetail.getOrderHeader().getClientModify()!=null && bizOrderDetail.getOrderHeader().getClientModify().equals("client_modify")){
//			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId+"&clientModify=client_modify"+
//					"&consultantId="+consultantId;
//		}else if(bizOrderDetail.getDetailFlag()!=null && bizOrderDetail.getDetailFlag().equals(a)){
//			//跳回C端
//			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/cendform?id="+orderId;
//		}
//		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId;
		return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
	}
	
	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderDetail bizOrderDetail,String orderDetailDetele, RedirectAttributes redirectAttributes) {
		bizOrderDetailService.delete(bizOrderDetail);
		BizOrderHeader bizOrderHeader = new BizOrderHeader();
		bizOrderHeader.setId(bizOrderDetail.getOrderHeader().getId());
		BizOrderDetail deta = new BizOrderDetail();
		deta.setOrderHeader(bizOrderDetail.getOrderHeader());
		List<BizOrderDetail> list = bizOrderDetailService.findList(deta);
		Double sum=0.0;
		if(list != null){
			for(BizOrderDetail bod:list){
				Double price = bod.getUnitPrice();//商品单价
				Integer ordQty = bod.getOrdQty();//采购数量
				if(price==null){price=0.0; }
				if(ordQty==null){ordQty=0; }
				sum+=price*ordQty;
			}
			bizOrderHeader.setTotalDetail(sum);
			bizOrderHeaderService.updateMoney(bizOrderHeader);
		}
		addMessage(redirectAttributes, "删除订单详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderDetail/form?orderHeader.id="+bizOrderDetail.getOrderHeader().getId();
	}

	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "delete4Mobile")
	@ResponseBody
	public String delete4Mobile(BizOrderDetail bizOrderDetail,String orderDetailDetele, RedirectAttributes redirectAttributes) {
		bizOrderDetailService.delete(bizOrderDetail);
		BizOrderHeader bizOrderHeader = new BizOrderHeader();
		bizOrderHeader.setId(bizOrderDetail.getOrderHeader().getId());
		BizOrderDetail deta = new BizOrderDetail();
		deta.setOrderHeader(bizOrderDetail.getOrderHeader());
		List<BizOrderDetail> list = bizOrderDetailService.findList(deta);
		Double sum=0.0;
		if(list != null){
			for(BizOrderDetail bod:list){
				Double price = bod.getUnitPrice();//商品单价
				Integer ordQty = bod.getOrdQty();//采购数量
				if(price==null){price=0.0; }
				if(ordQty==null){ordQty=0; }
				sum+=price*ordQty;
			}
			bizOrderHeader.setTotalDetail(sum);
			bizOrderHeaderService.updateMoney(bizOrderHeader);
		}

		return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
	}

	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "opShelfInfo")
	public List<BizOpShelfInfo> opShelfInfo(BizOrderDetail bizOrderDetail, RedirectAttributes redirectAttributes) {
		BizOpShelfInfo bizOpShelfInfo = new BizOpShelfInfo();
		List<BizOpShelfInfo> list = bizOpShelfInfoService.findList(bizOpShelfInfo);
		for (BizOpShelfInfo bizOpShelfSku : list) {
			System.out.println(bizOpShelfSku);
		}
		return list;
	}

	/**
	 * 订单商品详情实现不刷新删除
	 * */
	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "Detaildelete")
	public String Detaildelete(BizOrderDetail bizOrderDetail,String orderDetailDetele) {
		String aa="error";
		try {
			bizOrderDetailService.delete(bizOrderDetail);
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			bizOrderHeader.setId(bizOrderDetail.getOrderHeader().getId());
			BizOrderDetail deta = new BizOrderDetail();
			deta.setOrderHeader(bizOrderDetail.getOrderHeader());
			List<BizOrderDetail> list = bizOrderDetailService.findList(deta);
			Double sum=0.0;
			if(list != null){
				for(BizOrderDetail bod:list){
					Double price = bod.getUnitPrice();//商品单价
					Integer ordQty = bod.getOrdQty();//采购数量
					if(price==null){price=0.0; }
					if(ordQty==null){ordQty=0; }
					sum+=price*ordQty;
				}
				bizOrderHeader.setTotalDetail(sum);
				bizOrderHeaderService.updateMoney(bizOrderHeader);
			}
			aa="ok";
		}catch (Exception e){
			e.printStackTrace();
		}
		return aa;
	}

	/**
	 * 手机端订单商品详情实现不刷新删除
	 * */
	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "Detaildelete4Mobile")
	public String Detaildelete4Mobile(BizOrderDetail bizOrderDetail,String orderDetailDetele) {
		Map<String, Object> resultMap = Maps.newHashMap();
		String aa="error";
		try {
			bizOrderDetailService.delete(bizOrderDetail);
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			bizOrderHeader.setId(bizOrderDetail.getOrderHeader().getId());
			BizOrderDetail deta = new BizOrderDetail();
			deta.setOrderHeader(bizOrderDetail.getOrderHeader());
			List<BizOrderDetail> list = bizOrderDetailService.findList(deta);
			Double sum=0.0;
			if(list != null){
				for(BizOrderDetail bod:list){
					Double price = bod.getUnitPrice();//商品单价
					Integer ordQty = bod.getOrdQty();//采购数量
					if(price==null){price=0.0; }
					if(ordQty==null){ordQty=0; }
					sum+=price*ordQty;
				}
				bizOrderHeader.setTotalDetail(sum);
				bizOrderHeaderService.updateMoney(bizOrderHeader);
			}
			aa="ok";
		}catch (Exception e){
			e.printStackTrace();
		}
		resultMap.put("aa",aa);
		return JsonUtil.generateData(resultMap, null);
	}

}