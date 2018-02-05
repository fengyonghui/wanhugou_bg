/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.PoHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.PoOrderReqTypeEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
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

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;

import java.util.ArrayList;
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
	private BizRequestHeaderService bizRequestHeaderService;
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
			List<BizPoOrderReq> poOrderReqs= Lists.newArrayList();
				for (BizPoOrderReq poOrderReq:poOrderReqList){
				if(poOrderReq.getSoType()== Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())){
					BizOrderHeader bizOrderHeader=bizOrderHeaderService.get(poOrderReq.getSoId());
					poOrderReq.setOrderHeader(bizOrderHeader);
				}else if(poOrderReq.getSoType()==Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())){
					BizRequestHeader bizRequestHeader=bizRequestHeaderService.get(poOrderReq.getSoId());
					poOrderReq.setRequestHeader(bizRequestHeader);
				}
					poOrderReqs.add(poOrderReq);
			}
			entity.setPoOrderReqList(poOrderReqs);
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
	public String form(BizPoHeader bizPoHeader, Model model) {
		model.addAttribute("bizPoHeader", bizPoHeader);
		return "modules/biz/po/bizPoHeaderForm";
	}

	@RequiresPermissions("biz:po:bizPoHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPoHeader)){
			return form(bizPoHeader, model);
		}
		String poNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO,bizPoHeader.getVendOffice().getId());
		bizPoHeader.setOrderNum(poNo);
		bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
		bizPoHeaderService.save(bizPoHeader);

		addMessage(redirectAttributes, "保存采购订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoHeader/?repage";
	}
//	@RequiresPermissions("biz:po:bizPoHeader:edit")
//	@RequestMapping(value = "savePoHeaderDetail")
//	public String savePoHeaderDetail(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes){
//		bizPoHeaderService.savePoHeaderDetail(bizPoHeader);
//		return "redirect:"+Global.getAdminPath()+"/biz/po/bizPoHeader/?repage";
//	}

	@RequiresPermissions("biz:po:bizPoHeader:edit")
	@RequestMapping(value = "savePoHeader")
	public String savePoHeader(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPoHeader)){
			return form(bizPoHeader, model);
		}
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