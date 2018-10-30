package com.wanhutong.backend.modules.biz.web.custom;

/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustomerInfo;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomerInfoService;
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

/**
 * 机构信息entityController
 * @author wangby
 * @version 2018-10-22
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizCustomerInfo")
public class BizCustomerInfoController extends BaseController {

    @Autowired
    private BizCustomerInfoService bizCustomerInfoService;

    @ModelAttribute
    public BizCustomerInfo get(@RequestParam(required=false) Integer id) {
        BizCustomerInfo entity = null;
        if (id!=null){
            entity = bizCustomerInfoService.get(id);
        }
        if (entity == null){
            entity = new BizCustomerInfo();
        }
        return entity;
    }

    @RequiresPermissions("biz:order:bizCustomerInfo:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizCustomerInfo bizCustomerInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizCustomerInfo> page = bizCustomerInfoService.findPage(new Page<BizCustomerInfo>(request, response), bizCustomerInfo);
        model.addAttribute("page", page);
        return "modules/biz/order/bizCustomerInfoList";
    }

    @RequiresPermissions("biz:order:bizCustomerInfo:view")
    @RequestMapping(value = "form")
    public String form(BizCustomerInfo bizCustomerInfo, Model model) {
        model.addAttribute("bizCustomerInfo", bizCustomerInfo);
        return "modules/biz/order/bizCustomerInfoForm";
    }

    @RequiresPermissions("biz:order:bizCustomerInfo:edit")
    @RequestMapping(value = "save")
    public String save(BizCustomerInfo bizCustomerInfo, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizCustomerInfo)){
            return form(bizCustomerInfo, model);
        }
        bizCustomerInfoService.save(bizCustomerInfo);
        addMessage(redirectAttributes, "保存机构信息entity成功");
        return "redirect:"+Global.getAdminPath()+"/biz/order/bizCustomerInfo/?repage";
    }

    @RequiresPermissions("biz:order:bizCustomerInfo:edit")
    @RequestMapping(value = "delete")
    public String delete(BizCustomerInfo bizCustomerInfo, RedirectAttributes redirectAttributes) {
        bizCustomerInfoService.delete(bizCustomerInfo);
        addMessage(redirectAttributes, "删除机构信息entity成功");
        return "redirect:"+Global.getAdminPath()+"/biz/order/bizCustomerInfo/?repage";
    }

}