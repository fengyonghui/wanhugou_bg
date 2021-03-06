/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.invoice;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceDetail;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.invoice.BizInvoiceDetailService;
import com.wanhutong.backend.modules.biz.service.invoice.BizInvoiceHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.sys.entity.Office;
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
import javax.swing.text.html.parser.Entity;
import java.util.List;

/**
 * 发票抬头，发票内容，发票类型Controller
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/invoice/bizInvoiceHeader")
public class BizInvoiceHeaderController extends BaseController {

	@Autowired
	private BizInvoiceHeaderService bizInvoiceHeaderService;

	@Autowired
	private BizInvoiceDetailService bizInvoiceDetailService;

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;

	@ModelAttribute
	public BizInvoiceHeader get(@RequestParam(required=false) Integer id) {
		BizInvoiceHeader entity = null;
		if (id!=null){
			entity = bizInvoiceHeaderService.get(id);
//			根据 invoice_order.id计算有多少订单
			BizInvoiceDetail bizInvoiceDetail = new BizInvoiceDetail();
			bizInvoiceDetail.setInvoiceHeader(entity);
			List<BizInvoiceDetail> list = bizInvoiceDetailService.findList(bizInvoiceDetail);
			for (BizInvoiceDetail invoiceDetail : list) {
				BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(invoiceDetail.getOrderHead().getId());
				invoiceDetail.setOrderHead(bizOrderHeader);
			}
			entity.setBizInvoiceDetailList(list);
		}
		if (entity == null){
			entity = new BizInvoiceHeader();
		}
		return entity;
	}

	@RequiresPermissions("biz:invoice:bizInvoiceHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizInvoiceHeader bizInvoiceHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizInvoiceHeader> page = bizInvoiceHeaderService.findPage(new Page<BizInvoiceHeader>(request, response), bizInvoiceHeader);
		model.addAttribute("page", page);
		return "modules/biz/invoice/bizInvoiceHeaderList";
	}

	@RequiresPermissions("biz:invoice:bizInvoiceHeader:view")
	@RequestMapping(value = "form")
	public String form(BizInvoiceHeader bizInvoiceHeader, Model model) {
		model.addAttribute("bizInvoiceHeader", bizInvoiceHeader);
		return "modules/biz/invoice/bizInvoiceHeaderForm";
	}

	@RequiresPermissions("biz:invoice:bizInvoiceHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizInvoiceHeader bizInvoiceHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizInvoiceHeader)){
			return form(bizInvoiceHeader, model);
		}
		if(bizInvoiceHeader.getInvTotal()==null){
			bizInvoiceHeader.setInvTotal(0.0);
		}
		bizInvoiceHeaderService.save(bizInvoiceHeader);
		addMessage(redirectAttributes, "保存发票抬头成功");
		return "redirect:"+Global.getAdminPath()+"/biz/invoice/bizInvoiceHeader/";
	}
	
	@RequiresPermissions("biz:invoice:bizInvoiceHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizInvoiceHeader bizInvoiceHeader, RedirectAttributes redirectAttributes) {
		bizInvoiceHeaderService.delete(bizInvoiceHeader);
		addMessage(redirectAttributes, "删除发票抬头成功");
		return "redirect:"+Global.getAdminPath()+"/biz/invoice/bizInvoiceHeader/?repage";
	}

}