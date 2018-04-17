/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderUnlineService;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * 线下支付订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Controller
 *
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeaderUnline")
public class BizOrderHeaderUnlineController extends BaseController {

   @Autowired
   private BizOrderHeaderUnlineService bizOrderHeaderUnlineService;

    @ModelAttribute
    public BizOrderHeaderUnline get(@RequestParam(required = false) Integer id) {
        BizOrderHeaderUnline entity = null;
        if (id != null && id != 0) {
            entity = bizOrderHeaderUnlineService.get(id);
        }
        if (entity == null) {
            entity = new BizOrderHeaderUnline();
        }
        return entity;
    }

//    @RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
//    @RequestMapping(value = {"list", ""})
//    public String list(BizOrderHeaderUnline bizOrderHeaderUnline, HttpServletRequest request, HttpServletResponse response, Model model) {
//        Page<BizOrderHeaderUnline> page = bizOrderHeaderUnlineService.findPage(new Page<BizOrderHeaderUnline>(request, response), bizOrderHeaderUnline);
//        model.addAttribute("page", page);
//        return "modules/biz/order/bizOrderHeaderUnlineList";
//    }


    @RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
    @RequestMapping(value = "form")
    public String form(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, String orderNoEditable, String orderDetails) {

        BizOrderHeaderUnline orderHeaderUnline = bizOrderHeaderUnlineService.get(bizOrderHeaderUnline.getId());
        if (bizOrderHeaderUnline.getId() != null) {

        }
        model.addAttribute("entity", bizOrderHeaderUnline);
        boolean flag = false;
        User user = UserUtils.getUser();
        if (user.getRoleList() != null) {
            for (Role role : user.getRoleList()) {
                if (RoleEnNameEnum.FINANCE.getState().equals(role.getEnname())) {
                    flag = true;
                    break;
                }
            }
        }
        return "modules/biz/order/bizOrderHeaderUnlineForm";
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
    @RequestMapping(value = "save")
    public String save(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizOrderHeaderUnline)) {
            return form(bizOrderHeaderUnline, model, null, null);
        }
        bizOrderHeaderUnlineService.save(bizOrderHeaderUnline);
        addMessage(redirectAttributes, "保存订单信息成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeaderUnline/?oneOrder="+bizOrderHeaderUnline.getId();
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
    @RequestMapping(value = "delete")
    public String delete(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeaderUnline.setDelFlag(BizOrderHeaderUnline.DEL_FLAG_DELETE);
        bizOrderHeaderUnlineService.delete(bizOrderHeaderUnline);
        addMessage(redirectAttributes, "删除订单信息成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeaderUnline/?repage";
    }

}