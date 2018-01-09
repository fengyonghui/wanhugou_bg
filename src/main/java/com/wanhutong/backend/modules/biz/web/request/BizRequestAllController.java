package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 备货清单和销售订单（source=gh审核通过、采购中，source=kc 采购中、采购完成、供货中）
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

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"list", ""})
    public String list(String source, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader){

        User user= UserUtils.getUser();
        DefaultProp defaultProp=new DefaultProp();
        defaultProp.setPropKey("vendCenter");
        Integer vendId=0;
        List<DefaultProp> defaultPropList=defaultPropService.findList(defaultProp);
        if(defaultPropList!=null && defaultPropList.size() > 0){
            DefaultProp prop=defaultPropList.get(0);
            vendId=Integer.parseInt(prop.getPropValue());
        }
        model.addAttribute("source",source);
        if(bizOrderHeader==null){
            bizOrderHeader=new BizOrderHeader();
        }


		 	if("kc".equals(source)) {
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.PURCHASING.getState());
                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());
            }
             if("gh".equals(source)){
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.APPROVE.getState());
                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
            }
                List<BizRequestHeader> requestHeaderList= bizRequestHeaderService.findList(bizRequestHeader);

                model.addAttribute("requestHeaderList",requestHeaderList);

                List<BizOrderHeader> orderHeaderList=bizOrderHeaderService.findList(bizOrderHeader);
                model.addAttribute("orderHeaderList",orderHeaderList);
                return "modules/biz/request/bizRequestAllList";
        }
    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = "form")
    public String form(String source,BizRequestHeader bizRequestHeader,BizOrderHeader bizOrderHeader, Model model) {
        List<BizRequestDetail> reqDetailList = Lists.newArrayList();
        List<BizOrderDetail> ordDetailList=Lists.newArrayList();
        BizOrderHeader orderHeader=null;
        BizRequestHeader requestHeader=null;
        if (bizRequestHeader.getId() != null) {
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
        if (bizOrderHeader.getId() != null) {
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
        if (source != null && "kc".equals(source)) {
            return "modules/biz/request/bizRequestHeaderKcForm";
        }
        if(source != null && "sh".equals(source)){
            return "modules/biz/request/bizRequestKcForm";
        }
       // if(source!=null && "gh".equals(source)){
            return "modules/biz/request/bizRequestHeaderGhForm";
      //  }

    }
    @RequiresPermissions("biz:request:selecting:supplier:edit")
    @RequestMapping(value = "save")
    public String save(String reqIds,String orderIds){
        if(StringUtils.isNotBlank(reqIds)){
            StringUtils.split(reqIds,",");
        }
        if(StringUtils.isNotBlank(orderIds)){
            StringUtils.split(orderIds,",");
        }

        return "";
    }
}
