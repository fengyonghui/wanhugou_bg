package com.wanhutong.backend.modules.sys.web;


import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.MyPanelService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping(value = "${adminPath}/sys/myPanel")
public class MyPanelController extends BaseController {

    @Autowired
    private MyPanelService myPanelService;


    @RequestMapping(value = {"index"})
    public String index(HttpServletRequest request) {

//        订单审核
        int waitAuditCount = 0;
//        有尾款
        int hasRetainageCount = 0;
//        订单出库 供货中suply_id 不等于0的，采购中心供货
        int ddckCount = 0;
//        备货单入库
        int fhdrkCount = 0;
//        申请付款 po可以申请付款的
        int applyPaymentCount = 0;
//        付款单审核 poPaymentOrder 审核
        int paymentOrderAuditCount = 0;
//        备货单审核
        int reAuditCount = 0;
//        待排产
        int waitSchedulingCount = 0;
//        备货单/订单付款 付款单可以付款的
        int orderPaymentCount = 0;
//        需上架商品 所有没有上架的商品
        int needPutawayCount = 0;
//        待发货  备货单待发货
        int reWaitShipmentsCount = 0;
//        待发货  订单待发货  供货中suply_id 等于0的 供货中心发货
        int orderWaitShipmentsCount = 0;


        // 取当前用户角色
        User user = UserUtils.getUser();
        List<Role> userRoleList = user.getRoleList();
        Role tempRole = new Role();
        // 采购中心经理：订单审核；有尾款；订单出库；发货单入库；添加备货单
        tempRole.setEnname(RoleEnNameEnum.P_CENTER_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAgreedDeliveryCount();
            hasRetainageCount = myPanelService.getOrderHasRetainageCount();
            ddckCount = myPanelService.getDdckCount();
            fhdrkCount = myPanelService.getFhdrkCount();
        }

        // 客户专员：订单审核；有尾款
        tempRole.setEnname(RoleEnNameEnum.BUYER.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAgreedDeliveryCount();
            hasRetainageCount = myPanelService.getOrderHasRetainageCount();
        }

//        渠道主管：订单审核；添加备货单；有尾款;备货单审核；
        tempRole.setEnname(RoleEnNameEnum.CHANNEL_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("渠道经理");
            hasRetainageCount = myPanelService.getOrderHasRetainageCount();
            reAuditCount = myPanelService.getReAuditCount("1");
        }

//        总经理   ：订单审核；付款单审核
        tempRole.setEnname(RoleEnNameEnum.GENERAL_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("总经理");
            paymentOrderAuditCount = myPanelService.getPaymentOrderAuditCount("供货部");
        }

//        品类主管：订单审核；申请付款；付款单审核；备货单审核；待排产；需上架商品
        tempRole.setEnname(RoleEnNameEnum.SELECTION_OF_SPECIALIST.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("品类主管");
            waitSchedulingCount = myPanelService.getWaitSchedulingCount();
            reAuditCount = myPanelService.getReAuditCount("2");
            applyPaymentCount = myPanelService.getApplyPaymentCount();
            needPutawayCount = myPanelService.getNeedPutawayCount();
        }

        //        采销经理：订单审核；申请付款；付款单审核；备货单审核；待排产；需上架商品
        tempRole.setEnname(RoleEnNameEnum.MARKETINGMANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("采销经理");
            waitSchedulingCount = myPanelService.getWaitSchedulingCount();
            reAuditCount = myPanelService.getReAuditCount("2");
            applyPaymentCount = myPanelService.getApplyPaymentCount();
            needPutawayCount = myPanelService.getNeedPutawayCount();
        }

//        财务经理；财务总监；财务总经理：订单审核；备货单审核；
        tempRole.setEnname(RoleEnNameEnum.FINANCE.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("财务经理");
            paymentOrderAuditCount = myPanelService.getPaymentOrderAuditCount("财务经理");
            orderPaymentCount = myPanelService.getOrderPaymentCount();
        }
        tempRole.setEnname(RoleEnNameEnum.FINANCE_DIRECTOR.getState());
        if (userRoleList.contains(tempRole)) {
            paymentOrderAuditCount = myPanelService.getPaymentOrderAuditCount("财务总监");
            orderPaymentCount = myPanelService.getOrderPaymentCount();
        }
        tempRole.setEnname(RoleEnNameEnum.FINANCIAL_GENERAL_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            orderPaymentCount = myPanelService.getOrderPaymentCount();
            paymentOrderAuditCount = myPanelService.getPaymentOrderAuditCount("财务总经理");
        }

//        供货中心经理
        tempRole.setEnname(RoleEnNameEnum.PROVIDER_MANAGER.getState());
//        if (userRoleList.contains(tempRole)) {
//            paymentOrderAuditCount = myPanelService.getPaymentOrderAuditCount("供货部");
//        }

//        运营总监：备货审核
        tempRole.setEnname(RoleEnNameEnum.OP_DIRECTOR.getState());
        if (userRoleList.contains(tempRole)) {
            reAuditCount = myPanelService.getReAuditCount("8");
        }

//        仓储专员：订单出库；发货单入库；盘点异常
        tempRole.setEnname(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
        if (userRoleList.contains(tempRole)) {
            ddckCount = myPanelService.getDdckCount();
            fhdrkCount = myPanelService.getFhdrkCount();
        }

//        出      纳：备货单/订单付款；
        tempRole.setEnname(RoleEnNameEnum.TELLER.getState());
        if (userRoleList.contains(tempRole)) {
            orderPaymentCount = myPanelService.getOrderPaymentCount();
        }

//        发货专员：待发货
        tempRole.setEnname(RoleEnNameEnum.SHIPPER.getState());
        if (userRoleList.contains(tempRole)) {
            reWaitShipmentsCount = myPanelService.getReWaitShipmentsCount();
            orderWaitShipmentsCount = myPanelService.getOrderWaitShipmentsCount();
        }

        request.setAttribute("waitAuditCount", waitAuditCount);
        request.setAttribute("hasRetainageCount", hasRetainageCount);
        request.setAttribute("ddckCount", ddckCount);
        request.setAttribute("fhdrkCount", fhdrkCount);
        request.setAttribute("applyPaymentCount", applyPaymentCount);
        request.setAttribute("paymentOrderAuditCount", paymentOrderAuditCount);
        request.setAttribute("reAuditCount", reAuditCount);
        request.setAttribute("waitSchedulingCount", waitSchedulingCount);
        request.setAttribute("orderPaymentCount", orderPaymentCount);
        request.setAttribute("needPutawayCount", needPutawayCount);
        request.setAttribute("reWaitShipmentsCount", reWaitShipmentsCount);
        request.setAttribute("orderWaitShipmentsCount", orderWaitShipmentsCount);

        return "modules/sys/myPanel";
    }


    //        订单审核 
    @RequestMapping(value = {"waitAudit"})
    public String waitAudit(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");

        User user = UserUtils.getUser();
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
       if(user.isAdmin()){
           bizOrderHeader.setStr("orderAudit");
       }else{
           List<Role> list= user.getRoleList();
           for(Role role:list){
               if(role.getEnname().equals(RoleEnNameEnum.GENERAL_MANAGER.getState())){
                    bizOrderHeader.setSelectAuditStatus("总经理");
               } else if(role.getEnname().equals(RoleEnNameEnum.CHANNEL_MANAGER.getState())){
                bizOrderHeader.setSelectAuditStatus("渠道经理");
               }else if(role.getEnname().equals(RoleEnNameEnum.P_CENTER_MANAGER.getState()) || role.getEnname().equals(RoleEnNameEnum.BUYER.getState())){
                   bizOrderHeader.setFlag("check_pending");
                   bizOrderHeader.setMobileAuditStatus(0);
                   redirectModel.addFlashAttribute("bizOrderHeader",bizOrderHeader);
                   return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?flag=check_pending";
               }
               else if(role.getEnname().equals(RoleEnNameEnum.SELECTION_OF_SPECIALIST.getState())|| role.getEnname().equals(RoleEnNameEnum.MARKETINGMANAGER.getState()) ){
                   bizOrderHeader.setSelectAuditStatus("品类主管");
               }else if(role.getEnname().equals(RoleEnNameEnum.FINANCE.getState())){
                   bizOrderHeader.setSelectAuditStatus("财务经理");
               }
           }
       }

        redirectModel.addFlashAttribute("bizOrderHeader",bizOrderHeader);
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list";

    }

    //        有尾款
    @RequestMapping(value = {"hasRetainage"})
    public String hasRetainage(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?retainage=1";
    }

    //        订单出库
    @RequestMapping(value = {"ddck"})
    public String ddck(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestAll/list?source=kc&bizStatu=0&ship=xs&needOut=1";
    }

    //        发货单入库
    @RequestMapping(value = {"fhdrk"})
    public String fhdrk(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestAll/list?source=sh&bizStatu=0&ship=bh&needIn=1";
    }

    //        申请付款
    @RequestMapping(value = {"applyPayment"})
    public String applyPayment(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/listV2?applyPayment=1";
    }

    //        付款单审核
    @RequestMapping(value = {"paymentOrderAudit"})
    public String paymentOrderAudit(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
// 取当前用户角色
        User user = UserUtils.getUser();
        List<Role> userRoleList = user.getRoleList();
        Role tempRole = new Role();
        try {
            tempRole.setEnname(RoleEnNameEnum.FINANCE_DIRECTOR.getState());
            if (userRoleList.contains(tempRole)) {
                return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/listV2?selectAuditStatus=" + URLEncoder.encode("财务总监", "UTF-8");
            }
            tempRole.setEnname(RoleEnNameEnum.FINANCIAL_GENERAL_MANAGER.getState());
            if (userRoleList.contains(tempRole)) {
                return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/listV2?selectAuditStatus=" + URLEncoder.encode("财务总经理", "UTF-8");
            }
            tempRole.setEnname(RoleEnNameEnum.PROVIDER_MANAGER.getState());
            if (userRoleList.contains(tempRole)) {
                return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/listV2?selectAuditStatus=" + URLEncoder.encode("供货部", "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/listV2";
    }

    //        备货单审核
    @RequestMapping(value = {"reAudit"})
    public String reAudit(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
// 取当前用户角色
        User user = UserUtils.getUser();
        List<Role> userRoleList = user.getRoleList();
        Role tempRole = new Role();
//      渠道经理
        tempRole.setEnname(RoleEnNameEnum.CHANNEL_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor?process=1";
        }
//        品类主管：订单审核；申请付款；付款单审核；备货单审核；待排产；需上架商品
        tempRole.setEnname(RoleEnNameEnum.SELECTION_OF_SPECIALIST.getState());
        if (userRoleList.contains(tempRole)) {
            return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor?process=2";
        }
        //        采销经理：订单审核；申请付款；付款单审核；备货单审核；待排产；需上架商品
        tempRole.setEnname(RoleEnNameEnum.MARKETINGMANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor?process=2";
        }
//        运营总监：备货审核
        tempRole.setEnname(RoleEnNameEnum.OP_DIRECTOR.getState());
        if (userRoleList.contains(tempRole)) {
            return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor?process=8";
        }
        return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor";
    }

    //        待排产
    @RequestMapping(value = {"waitScheduling"})
    public String waitScheduling(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/listV2?poSchType=0";
    }

    //        备货单/订单付款
    @RequestMapping(value = {"orderPayment"})
    public String orderPayment(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/listV2?waitPay=1";
    }

    /**
     * 需上架商品:产品未删除，对应商品未删除，产品类行为自营商品，库存中库存数量大于0，不存在于任何一个未删除的货架
     * @param redirectModel
     * @return
     */
    @RequestMapping(value = {"needPutaway"})
    public String needPutaway(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        //return "redirect:" + Global.getAdminPath() + "/biz/sku/bizSkuInfo/list?notPutaway=1&productInfo.prodType=1";
        return "redirect:" + Global.getAdminPath() + "/biz/shelf/bizOpShelfSkuV2/form?shelfSign=0";
    }

    //        备货单待发货
    @RequestMapping(value = {"reWaitShipments"})
    public String reWaitShipments(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor?bizStatus=15";
    }

    //        订单待发货
    @RequestMapping(value = {"orderWaitShipments"})
    public String orderWaitShipments(RedirectAttributes redirectModel) {
        redirectModel.addAttribute("previousPage", "myPanel");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?waitShipments=1";
    }


}
