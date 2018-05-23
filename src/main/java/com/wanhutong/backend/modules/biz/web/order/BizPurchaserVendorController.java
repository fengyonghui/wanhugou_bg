/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.order.BizPurchaserVendor;
import com.wanhutong.backend.modules.biz.service.order.BizPurchaserVendorService;

import java.util.List;

/**
 * 采购商供应商关联关系Controller
 * @author ZhangTengfei
 * @version 2018-05-16
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizPurchaserVendor")
public class BizPurchaserVendorController extends BaseController {

	@Autowired
	private BizPurchaserVendorService bizPurchaserVendorService;
	
	@ModelAttribute
	public BizPurchaserVendor get(@RequestParam(required=false) Integer id) {
		BizPurchaserVendor entity = null;
		if (id!=null){
			entity = bizPurchaserVendorService.get(id);
		}
		if (entity == null){
			entity = new BizPurchaserVendor();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizPurchaserVendor:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizPurchaserVendor bizPurchaserVendor, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizPurchaserVendor> page = bizPurchaserVendorService.findPage(new Page<BizPurchaserVendor>(request, response), bizPurchaserVendor); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizPurchaserVendorList";
	}

	@RequiresPermissions("biz:order:bizPurchaserVendor:view")
	@RequestMapping(value = "form")
	public String form(BizPurchaserVendor bizPurchaserVendor, Model model) {
		model.addAttribute("bizPurchaserVendor", bizPurchaserVendor);
		return "modules/biz/order/bizPurchaserVendorForm";
	}

	@RequiresPermissions("biz:order:bizPurchaserVendor:edit")
	@RequestMapping(value = "save")
	public String save(BizPurchaserVendor bizPurchaserVendor, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizPurchaserVendor)){
			return form(bizPurchaserVendor, model);
		}
		bizPurchaserVendorService.save(bizPurchaserVendor);
		addMessage(redirectAttributes, "保存采购商供应商关联关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizPurchaserVendor/?repage";
	}
	
	@RequiresPermissions("biz:order:bizPurchaserVendor:edit")
	@RequestMapping(value = "delete")
	public String delete(BizPurchaserVendor bizPurchaserVendor, RedirectAttributes redirectAttributes) {
		bizPurchaserVendorService.delete(bizPurchaserVendor);
		addMessage(redirectAttributes, "删除采购商供应商关联关系成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizPurchaserVendor/?repage";
	}

    /**
     * 判断采购商和供应商关系是否已经建立
     * @param purchaser,vendor
     * @return
     */
	@ResponseBody
	@RequiresPermissions("biz:order:bizPurchaserVendor:view")
	@RequestMapping(value = "findPurchaserVendor")
	public String findPurchaserVendor(Integer purchaser, Integer vendor) {
	    String aa = "ok";
        BizPurchaserVendor purchaserVendor = new BizPurchaserVendor();
        purchaserVendor.setPurchaser(new Office(purchaser));
        purchaserVendor.setVendor(new Office(vendor));
        List<BizPurchaserVendor> purchaserVendorList = bizPurchaserVendorService.findList(purchaserVendor);
        if (purchaserVendorList != null && !purchaserVendorList.isEmpty()) {
            aa = "error";
        }
        return aa;
    }

}