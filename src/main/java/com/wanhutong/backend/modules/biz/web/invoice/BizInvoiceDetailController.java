/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.invoice;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.invoice.BizInvoiceDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.enums.BizOrderHeadStatus;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
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
import java.io.PrintWriter;
import java.util.List;

/**
 * 发票详情(发票行号,order_header.id)Controller
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/invoice/bizInvoiceDetail")
public class BizInvoiceDetailController extends BaseController {

	@Autowired
	private BizInvoiceDetailService bizInvoiceDetailService;
	
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public BizInvoiceDetail get(@RequestParam(required=false) Integer id) {
		BizInvoiceDetail entity = null;
		if (id!=null){
			entity = bizInvoiceDetailService.get(id);
		}
		if (entity == null){
			entity = new BizInvoiceDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:invoice:bizInvoiceDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInvoiceDetail bizInvoiceDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInvoiceDetail> page = bizInvoiceDetailService.findPage(new Page<BizInvoiceDetail>(request, response), bizInvoiceDetail); 
		model.addAttribute("page", page);
		return "modules/biz/invoice/bizInvoiceDetailList";
	}

	@RequiresPermissions("biz:invoice:bizInvoiceDetail:view")
	@RequestMapping(value = "form")
	public String form(BizInvoiceDetail bizInvoiceDetail, Model model) {
		model.addAttribute("bizInvoiceDetail", bizInvoiceDetail);
		return "modules/biz/invoice/bizInvoiceDetailForm";
	}

	@RequiresPermissions("biz:invoice:bizInvoiceDetail:edit")
	@RequestMapping(value = "save")
	public String save(BizInvoiceDetail bizInvoiceDetail,String boxs, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInvoiceDetail)){
			return form(bizInvoiceDetail, model);
		}
		String[] split = boxs.split(",");
		Integer count=1;
		for(int i=0;i<split.length;i++){
			bizInvoiceDetail.setLineNo(count++);/*行号*/
			Integer integer = new Integer(split[i]);
			BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(integer);
			bizInvoiceDetail.setOrderHead(bizOrderHeader);/*orderHeader.id*/
			bizInvoiceDetail.setInvAmt(8888.0);/*发票金额*/
			bizInvoiceDetailService.save(bizInvoiceDetail);
		}
		addMessage(redirectAttributes, "保存发票详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/invoice/bizInvoiceDetail/form";
	}
	
	@RequiresPermissions("biz:invoice:bizInvoiceDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInvoiceDetail bizInvoiceDetail, RedirectAttributes redirectAttributes) {
		bizInvoiceDetailService.delete(bizInvoiceDetail);
		addMessage(redirectAttributes, "删除发票详情成功");
		return "redirect:"+Global.getAdminPath()+"/biz/invoice/bizInvoiceDetail/?repage";
	}
	
	@ResponseBody
	@RequiresPermissions("biz:invoice:bizInvoiceDetail:view")
	@RequestMapping(value = "invOrderHeader")
	public List<BizOrderHeader> invoOrderHeader(BizInvoiceDetail bizInvoiceDetail,Integer officeId, Model model) {
		BizOrderHeader bizOrderHeader = new BizOrderHeader();
		bizOrderHeader.setInvStatus(BizOrderHeadStatus.INVSTATUS.getStatu());
		bizOrderHeader.setBizStatus(BizOrderHeadStatus.BIZSTATUS.getStatu());
		Office office = officeService.get(officeId);
		bizOrderHeader.setCustomer(office);
		List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
		bizInvoiceDetail.setOrderHeaderList(list);
		return list;
	}
	
	/*@ResponseBody
	@RequiresPermissions("biz:invoice:bizInvoiceDetail:view")
	@RequestMapping(value = "checkboxAll")
	public BizInvoiceDetail setCheckboxAll(BizInvoiceDetail bizInvoiceDetail,String boxs,Model model) {
		String[] split = boxs.split(",");
		Integer count=1;
		for(int i=0;i<split.length;i++){
			bizInvoiceDetail.setLineNo(count++);*//*行号*//*
			Integer integer = new Integer(split[i]);
			BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(integer);
			bizInvoiceDetail.setOrderHead(bizOrderHeader);*//*orderHeader.id*//*
		}
			bizInvoiceDetail.setInvAmt(99.0);*//*发票金额*//*
			bizInvoiceDetailService.save(bizInvoiceDetail);
		return bizInvoiceDetail;
	}*/
}