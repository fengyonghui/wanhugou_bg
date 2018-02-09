package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 备货清单和销售订单（source=gh审核通过、采购中，source=kc 采购中、采购完成、供应中心供货, source=sh 备货中, source=ghs 供货详情，ship=xs 销售单，ship=bh 备货单）
 * 采购中心和供货中心（bizStatu=0采购中心，bizStatu=1供货中心）
 */

@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestAll")
public class BizRequestAllController {
    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
    @Autowired
    private DefaultPropService defaultPropService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private SystemService systemService;

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"list", ""})
    public String list(String source,Integer bizStatu,String ship, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader){

        User user= UserUtils.getUser();
        DefaultProp defaultProp=new DefaultProp();
        defaultProp.setPropKey("vend_center");
        Integer vendId=0;
        List<DefaultProp> defaultPropList=defaultPropService.findList(defaultProp);
        if(defaultPropList!=null && defaultPropList.size() > 0){
            DefaultProp prop=defaultPropList.get(0);
            vendId=Integer.parseInt(prop.getPropValue());
        }
        //用户属于供应中心或管理员

       /*String type= user.getCompany().getType();
        System.out.println(type);

        boolean flag=false;
        if(user.getRoleList()!=null) {
            for (Role role : user.getRoleList()) {
                if (!RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                    flag = true;
                    break;
                }
            }
        }
        if(flag){
            model.addAttribute("ship",ship);
        }*/

        if (user.getCompany().getType().equals(OfficeTypeEnum.SUPPLYCENTER.getType())  || user.isAdmin()){
            model.addAttribute("ship",ship);
        }
        model.addAttribute("source",source);
        if(bizOrderHeader==null){
            bizOrderHeader=new BizOrderHeader();
        }
		 	if("kc".equals(source)) {
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCK_COMPLETE.getState().byteValue());
                if("0".equals(bizStatu)) {
                    bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                    bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.SEND.getState());
                }
                if ("1".equals(bizStatu)){
                    bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.PURCHASING.getState());
                    bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.SEND.getState());
                }
            }
            else if("sh".equals(source)){
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.COMPLETE.getState().byteValue());
    //                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.STOCKING.getState());
    //                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.COMPLETE.getState());
            }
             if("gh".equals(source)){
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.APPROVE.getState());
                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());
            }
            if("ghs".equals(source)) {
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());
            }
                List<BizRequestHeader> requestHeaderList= bizRequestHeaderService.findList(bizRequestHeader);
                model.addAttribute("requestHeaderList",requestHeaderList);
                List<BizOrderHeader> orderHeaderList=bizOrderHeaderService.findList(bizOrderHeader);
                model.addAttribute("orderHeaderList",orderHeaderList);
                if (bizStatu != null){
                    model.addAttribute("bizStatu",bizStatu.toString());
                }
                return "modules/biz/request/bizRequestAllList";
        }
    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = "form")
    public String form(String source,Integer id, Model model,Integer bizStatu,String ship) {
        List<BizRequestDetail> reqDetailList = Lists.newArrayList();
        List<BizOrderDetail> ordDetailList=Lists.newArrayList();
        BizOrderHeader orderHeader=null;
        BizRequestHeader requestHeader=null;
        BizRequestHeader bizRequestHeader = new BizRequestHeader();
        BizOrderHeader bizOrderHeader= new BizOrderHeader();
        if ("kc".equals(source) && "xs".equals(ship) || "ghs".equals(source)){
            bizOrderHeader = bizOrderHeaderService.get(id);
        }else{
            bizRequestHeader = bizRequestHeaderService.get(id);
        }
        if (bizRequestHeader != null && bizRequestHeader.getId() != null) {
            //取出用户所属采购中心
            BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
            User user = UserUtils.getUser();
//            List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
            if (user.isAdmin()){
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList",invInfoList);
            }else {
                Office company = systemService.getUser(user.getId()).getCompany();
                bizInventoryInfo.setCustomer(company);
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList",invInfoList);
            }
            BizRequestDetail bizRequestDetail = new BizRequestDetail();
            bizRequestDetail.setRequestHeader(bizRequestHeader);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
            for (BizRequestDetail requestDetail : requestDetailList) {
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
                requestDetail.setSkuInfo(skuInfo);
                reqDetailList.add(requestDetail);
            }
            requestHeader= bizRequestHeaderService.get(bizRequestHeader.getId());
        }
        if (bizOrderHeader != null && bizOrderHeader.getId() != null) {
            //取出用户所属采购中心
            BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
            User user = UserUtils.getUser();
            if (user.isAdmin()){
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList",invInfoList);
            }else {
                Office company = systemService.getUser(user.getId()).getCompany();
                BizInventoryInfo bizInventoryInfo1 = new BizInventoryInfo();
                bizInventoryInfo1.setCustomer(company);
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo1);
                model.addAttribute("invInfoList",invInfoList);
            }
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            for (BizOrderDetail orderDetail : orderDetailList) {
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                orderDetail.setSkuInfo(skuInfo);
                ordDetailList.add(orderDetail);
            }
            orderHeader=bizOrderHeaderService.get(bizOrderHeader.getId());
        }
        model.addAttribute("bizRequestHeader",requestHeader );
        model.addAttribute("bizOrderHeader", orderHeader);
        model.addAttribute("reqDetailList", reqDetailList);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("source",source);
        model.addAttribute("bizStatu",bizStatu);
        model.addAttribute("ship",ship);
        if (source != null && "kc".equals(source)) {
            return "modules/biz/request/bizRequestHeaderKcForm";
        }
        if (source != null && "ghs".equals(source)){
            return "modules/biz/request/bizRequestKcXqForm";
        }
        if(source != null && "sh".equals(source)){
            BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
            User user = UserUtils.getUser();
            if (user.isAdmin()){
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList",invInfoList);
            }else {
                Office company = systemService.getUser(user.getId()).getCompany();
                BizInventoryInfo bizInventoryInfo1 = new BizInventoryInfo();
                bizInventoryInfo1.setCustomer(company);
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo1);
                model.addAttribute("invInfoList",invInfoList);
            }
            return "modules/biz/request/bizRequestKcForm";
        }
       // if(source!=null && "gh".equals(source)){
            return "modules/biz/request/bizRequestHeaderGhForm";
      //  }

    }

}
