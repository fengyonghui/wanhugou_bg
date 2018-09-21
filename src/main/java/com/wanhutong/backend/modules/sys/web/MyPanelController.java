package com.wanhutong.backend.modules.sys.web;


import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.MyPanelService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "${adminPath}/sys/myPanel")
public class MyPanelController extends BaseController {

    @Autowired
    private MyPanelService myPanelService;


    @RequestMapping(value = {"index"})
    public String list(HttpServletRequest request) {
//        originConfigMap.put("渠道经理", "渠道经理");
//        originConfigMap.put("总经理", "总经理");
//        originConfigMap.put("品类主管", "品类主管");
//        originConfigMap.put("财务经理", "财务经理");
//        originConfigMap.put("完成", "完成");
//        originConfigMap.put("驳回", "驳回");
//        originConfigMap.put("不需要审批", "不需要审批");

//        订单审核
        int waitAuditCount = 0;
//        有尾款
        int hasRetainageCount = 0;
//        订单出库
//        发货单入库
//        申请付款
//        付款单审核
//        备货单审核
//        待排产
//        发货单入库
//        备货单/订单付款
//        需上架商品
//        订单出库
//        待发货
//        添加备货单
//        盘点异常


        // 取当前用户角色
        User user = UserUtils.getUser();
        List<Role> userRoleList = user.getRoleList();
        Role tempRole = new Role();
        // 采购中心经理：订单审核；有尾款；订单出库；发货单入库；添加备货单
        tempRole.setEnname(RoleEnNameEnum.P_CENTER_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAgreedDeliveryCount();
            hasRetainageCount = myPanelService.getOrderHasRetainageCount();
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
        }

//        总经理   ：订单审核
        tempRole.setEnname(RoleEnNameEnum.GENERAL_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("总经理");

        }

//        品类主管：订单审核；申请付款；付款单审核；备货单审核；待排产；需上架商品
        tempRole.setEnname(RoleEnNameEnum.SELECTION_OF_SPECIALIST.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("品类主管");

        }

//        财务经理；财务总监；财务总经理：订单审核；备货单审核；
        tempRole.setEnname(RoleEnNameEnum.FINANCE.getState());
        if (userRoleList.contains(tempRole)) {
            waitAuditCount = myPanelService.getOrderWaitAuditCount("财务经理");

        }
        tempRole.setEnname(RoleEnNameEnum.FINANCE_DIRECTOR.getState());
        if (userRoleList.contains(tempRole)) {

        }
        tempRole.setEnname(RoleEnNameEnum.FINANCIAL_GENERAL_MANAGER.getState());
        if (userRoleList.contains(tempRole)) {
            System.out.println(2);
        }

//        运营总监：备货审核
        tempRole.setEnname(RoleEnNameEnum.OP_DIRECTOR.getState());
        if (userRoleList.contains(tempRole)) {
            System.out.println(2);
        }

//        仓储专员：订单出库；发货单入库；盘点异常
        tempRole.setEnname(RoleEnNameEnum.WAREHOUSESPECIALIST.getState());
        if (userRoleList.contains(tempRole)) {
            System.out.println(2);
        }

//        出      纳：备货单/订单付款；
        tempRole.setEnname(RoleEnNameEnum.TELLER.getState());
        if (userRoleList.contains(tempRole)) {
            System.out.println(2);
        }

//        发货专员：待发货
        tempRole.setEnname(RoleEnNameEnum.SHIPPER.getState());
        if (userRoleList.contains(tempRole)) {
            System.out.println(2);
        }


        request.setAttribute("waitAuditCount", waitAuditCount);
        request.setAttribute("hasRetainageCount", hasRetainageCount);

        return "modules/sys/myPanel";
    }


}
