/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
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
	private BizOrderHeaderService bizOrderHeaderService;

	@Autowired
	private BizSkuInfoService bizSkuInfoService;

	@Autowired
	private BizOpShelfSkuService bizOpShelfSkuService;
	@Autowired
	private BizOpShelfInfoService bizOpShelfInfoService;
	@Autowired
	private BizOrderDetailDao bizOrderDetailDao;

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
		Integer maxLine = bizOrderDetailDao.findMaxLine(bizOrderDetail);
		bizOrderDetail.setMaxLineNo(maxLine);
//		用于往页面传给savg保存
		bizOrderDetail.setOrdQtyUpda(bizOrderDetail.getOrdQty());
		bizOrderDetail.getOrderHeader().getOneOrder();
		model.addAttribute("bizOrderDetail", bizOrderDetail);
		return "modules/biz/order/bizOrderDetailForm";
	}

	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderDetail bizOrderDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderDetail)){
			return form(bizOrderDetail, model);
		}
		if(bizOrderDetail.getMaxLineNo()==null){
			bizOrderDetail.setLineNo(1);
		}else {
			Integer maxLineNo = bizOrderDetail.getMaxLineNo();
			maxLineNo++;
			bizOrderDetail.setLineNo(maxLineNo);
		}
		bizOrderDetailService.save(bizOrderDetail);
		addMessage(redirectAttributes, "保存订单详情成功");
		Integer orderId=bizOrderDetail.getOrderHeader().getId();
////		if(orderId !=null && orderId !=0){
//		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId;
////		}
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+orderId;
	}
	
	@RequiresPermissions("biz:order:bizOrderDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderDetail bizOrderDetail, RedirectAttributes redirectAttributes) {
		bizOrderDetailService.delete(bizOrderDetail);
		addMessage(redirectAttributes, "删除订单详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeader/form?id="+bizOrderDetail.getOrderHeader().getId();
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
}