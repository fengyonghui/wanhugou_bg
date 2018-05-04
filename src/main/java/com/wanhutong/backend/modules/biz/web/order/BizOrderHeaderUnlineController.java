/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
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
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderUnlineService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 线下支付订单(线下独有)Controller
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeaderUnline")
public class BizOrderHeaderUnlineController extends BaseController {

	@Autowired
	private BizOrderHeaderUnlineService bizOrderHeaderUnlineService;
	
	@ModelAttribute
	public BizOrderHeaderUnline get(@RequestParam(required=false) Integer id) {
		BizOrderHeaderUnline entity = null;
		if (id!=null){
			entity = bizOrderHeaderUnlineService.get(id);
		}
		if (entity == null){
			entity = new BizOrderHeaderUnline();
		}
		return entity;
	}
	
	@RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizOrderHeaderUnline bizOrderHeaderUnline, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizOrderHeaderUnline> page = bizOrderHeaderUnlineService.findPage(new Page<BizOrderHeaderUnline>(request, response), bizOrderHeaderUnline); 
		model.addAttribute("page", page);
		return "modules/biz/order/bizOrderHeaderUnlineList";
	}

	@RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
	@RequestMapping(value = "form")
	public String form(BizOrderHeaderUnline bizOrderHeaderUnline, Model model) {
		model.addAttribute("bizOrderHeaderUnline", bizOrderHeaderUnline);
		return "modules/biz/order/bizOrderHeaderUnlineForm";
	}

	@RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
	@RequestMapping(value = "save")
	public String save(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizOrderHeaderUnline)){
			return form(bizOrderHeaderUnline, model);
		}
		bizOrderHeaderUnlineService.save(bizOrderHeaderUnline);
		addMessage(redirectAttributes, "保存线下支付订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeaderUnline/?repage";
	}
	
	@RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
	@RequestMapping(value = "delete")
	public String delete(BizOrderHeaderUnline bizOrderHeaderUnline, RedirectAttributes redirectAttributes) {
		bizOrderHeaderUnlineService.delete(bizOrderHeaderUnline);
		addMessage(redirectAttributes, "删除线下支付订单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/order/bizOrderHeaderUnline/?repage";
	}

    /**
     * 财务对线下支付订单的审核
     * @param orderHeader
     * @param realMoney
     * @return
     */
	@ResponseBody
	@RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
	@RequestMapping(value = "changeOrderReceive")
	public BizOrderHeaderUnline changeOrderReceive(BizOrderHeader orderHeader, String realMoney) {
        BizOrderHeaderUnline bizOrderHeaderUnline = new BizOrderHeaderUnline();
        bizOrderHeaderUnline.setOrderHeader(orderHeader);
        List<BizOrderHeaderUnline> unlineList = bizOrderHeaderUnlineService.findList(bizOrderHeaderUnline);
        if (unlineList != null && !unlineList.isEmpty()) {
            bizOrderHeaderUnline = unlineList.get(0);
            bizOrderHeaderUnline.setRealMoney(new BigDecimal(realMoney));
            bizOrderHeaderUnlineService.save(bizOrderHeaderUnline);
        }
        return bizOrderHeaderUnline;
    }

}