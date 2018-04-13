/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderSkuPropValue;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderSkuPropValueService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
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
//		用于往页面传给savg保存 首单标记 OneOrder
		bizOrderDetail.setOrdQtyUpda(bizOrderDetail.getOrdQty());
        BizOrderHeader orderHeader = bizOrderDetail.getOrderHeader();
        if(orderHeader!=null){
			BizOrderHeader ord = bizOrderHeaderService.get(orderHeader.getId());
			model.addAttribute("orderH", ord);//用于页面订单供货中显示供货数量
			model.addAttribute("entity", bizOrderDetail);
			model.addAttribute("bizOpShelfSku",new BizOpShelfSku());
		}
//		订单详情修改按钮显示品规色
		BizOrderDetail detailOrder = bizOrderDetailService.get(bizOrderDetail);
        if(detailOrder!=null){
			BizOpShelfSku opShelfSku=bizOpShelfSkuService.get(bizOrderDetail.getShelfInfo().getId()) ;
			detailOrder.setShelfInfo(opShelfSku);
			BizOrderSkuPropValue bizOrderSkuPropValue = new BizOrderSkuPropValue();
			bizOrderSkuPropValue.setOrderDetails(detailOrder);
			List<BizOrderSkuPropValue> list = bizOrderSkuPropValueService.findList(bizOrderSkuPropValue);
			detailOrder.setOrderSkuValueList(list);
		}
		model.addAttribute("detail", detailOrder);
//		现价，销售区间
		if(bizOrderDetail.getShelfInfo()!=null){
			BizOpShelfSku bizOpShelfSku = bizOpShelfSkuService.get(bizOrderDetail.getShelfInfo());
			model.addAttribute("shelfSku", bizOpShelfSku);
		}
		return "modules/biz/order/bizOrderDetailForm";
	}

	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderDetail bizOrderDetail, Model model, RedirectAttributes redirectAttributes) {
		bizOrderDetailService.save(bizOrderDetail);
		addMessage(redirectAttributes, "保存订单详情成功");
		Integer orderId=bizOrderDetail.getOrderHeader().getId();
////		if(orderId !=null && orderId !=0){
//		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId;
////		}
		String consultantId ="";
		if(bizOrderDetail.getOrderHeader()!=null && bizOrderDetail.getOrderHeader().getConsultantId()!=null){
			consultantId = String.valueOf(bizOrderDetail.getOrderHeader().getConsultantId());
		}
		if(bizOrderDetail.getOrderHeader().getClientModify()!=null && bizOrderDetail.getOrderHeader().getClientModify().equals("client_modify")){
			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId+"&clientModify=client_modify"+
					"&consultantId="+consultantId;
		}else if(bizOrderDetail.getOrderHeader().getClientModify()!=null && bizOrderDetail.getOrderHeader().getClientModify().equals("cend_modifSave")){
			return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/cendform?id="+orderId+"&clientModify=cend_modifSave"+
					"&consultantId="+consultantId;
		}
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId;
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

}