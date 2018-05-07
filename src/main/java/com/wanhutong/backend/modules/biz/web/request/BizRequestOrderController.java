/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 备货清单Controller
 *
 * @author liuying
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestOrder")
public class BizRequestOrderController extends BaseController {

    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private DictService dictService;


    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"list", ""})
    public String list(String source, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader,HttpServletRequest request,HttpServletResponse response) {
        List<BizRequestHeader> requestHeaderList = null;
        Page<BizRequestHeader> requestHeaderPage = null;
        List<BizOrderHeader> orderHeaderList = null;
        Page<BizOrderHeader> orderHeaderPage = null;
        if ("bhgh".equals(source)) {
//            List<BizRequestHeader> list = findBizRequest(bizRequestHeader, requestHeaderList);
            Page<BizRequestHeader> page = findBizRequest(bizRequestHeader, requestHeaderPage,request,response);
//            model.addAttribute("requestHeaderList", list); //原来的不分页
            //分页 20180427 改
            model.addAttribute("page", page);
            //判断
            model.addAttribute("requestHeaderPage", page.getList());
        } else if ("xsgh".equals(source)) {
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
//            orderHeaderList = bizOrderHeaderService.findList(bizOrderHeader);
            orderHeaderPage=bizOrderHeaderService.pageFindList(new Page<BizOrderHeader>(request, response), bizOrderHeader);
            Set<Integer> set = new HashSet();
            List<BizOrderHeader> list = Lists.newArrayList();
            for (BizOrderHeader bizOrderHeader1 : orderHeaderPage.getList()) {
                if(bizOrderHeader1.getBizLocation()!=null && bizOrderHeader1.getBizLocation().getId()!=null){
                    bizOrderHeader1.setBizLocation(bizOrderAddressService.get(bizOrderHeader1.getBizLocation().getId()));
                }
                set.clear();
                boolean flag = false;
                BizOrderDetail bizOrderDetail = new BizOrderDetail();
                bizOrderDetail.setOrderHeader(bizOrderHeader1);
                //	bizOrderDetail.setSuplyis(officeService.get(0));
                StringBuffer sb = new StringBuffer();
                List<BizOrderDetail> orderDetails = bizOrderDetailService.findList(bizOrderDetail);
                for (BizOrderDetail bizOrderDetail1 : orderDetails) {
                    if (bizOrderDetail1.getSuplyis().getId() == 0 || bizOrderDetail1.getSuplyis().getId()== 721) {
                        flag = true;
                    }
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizOrderDetail1.getSkuInfo().getId());
                    set.add(bizSkuInfo.getProductInfo().getOffice().getId());
                    sb.append(bizOrderDetail1.getId());
                    sb.append(",");
                }

                if (set.size() == 1) {
                    for (Iterator it = set.iterator(); it.hasNext(); ) {
                        bizOrderHeader1.setOnlyVendor((Integer) it.next());
                    }
                    bizOrderHeader1.setOrderDetails(sb.substring(0, sb.lastIndexOf(",")));
                    bizOrderHeader1.setOwnGenPoOrder(true);
                }
                if (flag) {
                    list.add(bizOrderHeader1);
                }

            }
            orderHeaderPage.setList(list);
//            model.addAttribute("orderHeaderList", list);
            //20180427 分页
            model.addAttribute("page", orderHeaderPage);
            //判断
            model.addAttribute("orderHeaderPage", orderHeaderPage.getList());
        }

        model.addAttribute("source", source);
        return "modules/biz/request/bizRequestOrderList";
    }

    private List<BizRequestHeader> findBizRequest(BizRequestHeader bizRequestHeader, List<BizRequestHeader> requestHeaderList) {
        bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
        bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
        requestHeaderList = bizRequestHeaderService.findList(bizRequestHeader);
        List<BizRequestHeader> list = Lists.newArrayList();
        for (BizRequestHeader bizRequestHeader1 : requestHeaderList) {
            BizRequestDetail bizRequestDetail1 = new BizRequestDetail();
            bizRequestDetail1.setRequestHeader(bizRequestHeader1);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail1);
            Integer reqQtys = 0;
            Integer recvQtys = 0;
            Set set = new HashSet();
            StringBuffer sb = new StringBuffer();
            for (BizRequestDetail bizRequestDetail : requestDetailList) {
                reqQtys += bizRequestDetail.getReqQty();
                recvQtys += bizRequestDetail.getRecvQty();
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                set.add(bizSkuInfo.getProductInfo().getOffice().getId());
                sb.append(bizRequestDetail.getId());
                sb.append(",");
            }

            if (set.size() == 1) {
                for (Iterator it = set.iterator(); it.hasNext(); ) {
                    bizRequestHeader1.setOnlyVendor((Integer) it.next());
                    //System.out.println("value="+it.next().toString());
                }

                bizRequestHeader1.setReqDetailIds(sb.substring(0, sb.lastIndexOf(",")));
                bizRequestHeader1.setOwnGenPoOrder(true);
            }

            bizRequestHeader1.setReqQtys(reqQtys.toString());
            bizRequestHeader1.setRecvQtys(recvQtys.toString());
            list.add(bizRequestHeader1);
        }
        return list;
    }

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"form", ""})
    public String form(String source, Model model) {
        Map<String, String> map = new HashMap<String, String>();
        if ("gh".equals(source)) {
            BizRequestHeader bizRequestHeader = new BizRequestHeader();
            bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
            bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
            bizRequestHeader.setBizStatusNot(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState().byteValue());
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findReqTotalByVendor(bizRequestHeader);

            BizOrderHeader bizOrderHeader = new BizOrderHeader();
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());
            bizOrderHeader.setBizStatusNot(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState().byteValue());
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findOrderTotalByVendor(bizOrderHeader);

            String key = "0";

            for (BizRequestDetail bizRequestDetail : requestDetailList) {
                key = bizRequestDetail.getVendorId() + "," + bizRequestDetail.getVendorName();
                Integer reqQty = bizRequestDetail.getTotalReqQty() - bizRequestDetail.getTotalRecvQty();
                map.put(key, reqQty + "-" + bizRequestDetail.getReqDetailIds() + "-r");

            }

            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                StringBuffer sb = new StringBuffer();
                if (StringUtils.isNotBlank(bizOrderDetail.getSuplyIds())) {
                    String[] supplyIdArr = StringUtils.split(bizOrderDetail.getSuplyIds(), ",");
                    String[] detailIdArr = StringUtils.split(bizOrderDetail.getDetailIds(), ",");
                    for (int i = 0; i < supplyIdArr.length; i++) {
                        if (Integer.parseInt(supplyIdArr[i].trim()) == 0 || Integer.parseInt(supplyIdArr[i].trim()) == 721) {
                            sb.append(detailIdArr[i].trim());
                            if (i != supplyIdArr.length - 1) {
                                sb.append(",");
                            }

                        }
                    }
                }
                key = bizOrderDetail.getVendorId() + "," + bizOrderDetail.getVendorName();
                Integer ordQty = bizOrderDetail.getTotalReqQty() - bizOrderDetail.getTotalSendQty();
                if (map.containsKey(key)) {
                    String str = map.get(key) + "|" + ordQty + "-" + sb.toString() + "-s";
                    map.remove(key);
                    map.put(key, str);
                    System.out.println(map.get(key));
                } else {
                    map.put(key, ordQty + "-" + sb.toString() + "-s");
                    System.out.println(map.get(key));
                }
            }

        }
        model.addAttribute("source", source);
        model.addAttribute("map", map);
        return "modules/biz/request/bizRequestPoList";
    }

    @RequestMapping(value = "goList")
    public String goList(String reqIds, Integer vendorId, String ordIds, Model model) {
//        Map<Integer, List<BizSkuInfo>> skuInfoMap = null;
        Map<String,List<BizRequestDetail>> reqDetailMap = new LinkedHashMap<>();
        Map<String,List<BizOrderDetail>> orderDetailMap = new LinkedHashMap<>();
        List<BizRequestDetail> requestDetailList = new ArrayList<>();
//        Map<Integer, List<BizSkuInfo>> map = new HashMap<>();
//        Map<BizSkuInfo, BizSkuInfo> skuMap = new HashMap<>();
        if (StringUtils.isNotBlank(reqIds)) {
            String[] reqDetailArr = reqIds.split(",");
            for (int i = 0; i < reqDetailArr.length; i++) {
                BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailArr[i].trim()));
                Integer key =bizRequestDetail.getRequestHeader().getId();

                BizSkuInfo sku = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizRequestDetail.getRequestHeader().getId());
//                bizRequestHeader.setBizStatusStart((byte)(ReqHeaderStatusEnum.APPROVE.getState().intValue()));
//                bizRequestHeader.setBizStatusEnd((byte)(ReqHeaderStatusEnum.));
                bizRequestDetail.setRequestHeader(bizRequestHeader);
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                bizRequestDetail.setSkuInfo(skuInfo);
//                reqDetailMap.put(bizRequestDetail.getId()+","+bizRequestDetail.getRequestHeader().getReqNo(),bizRequestDetail);
                if(reqDetailMap.containsKey(key.toString())){
                    List<BizRequestDetail> requestDetails = reqDetailMap.get(key.toString());
                    requestDetails.add(bizRequestDetail);
                    reqDetailMap.put(key.toString(),requestDetails);
                }else {
                    List<BizRequestDetail> requestDetails =  new ArrayList<>();
                    requestDetails.add(bizRequestDetail);
                    reqDetailMap.put(key.toString(),requestDetails);
                }
//                bizSkuInfo.setReqQty(bizRequestDetail.getReqQty());
//                bizSkuInfo.setSentQty(bizRequestDetail.getRecvQty());
//                bizSkuInfo.setReqDetailIds(reqDetailArr[i].trim());
//                skuInfoMap = getSkuInfoData(map, bizSkuInfo.getId(), bizSkuInfo);
//				bizRequestDetail.setSkuInfo(skuInfo);
//				requestDetailList.add(bizRequestDetail);
            }
            model.addAttribute("reqDetailMap",reqDetailMap);
        }
        if (StringUtils.isNotBlank(ordIds)) {
            // orderDetailList=Lists.newArrayList();
            String[] ordDetailArr = ordIds.split(",");
            for (int i = 0; i < ordDetailArr.length; i++) {
                BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(ordDetailArr[i].trim()));
                Integer key = bizOrderDetail.getOrderHeader().getId();
                BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                bizOrderDetail.setOrderHeader(bizOrderHeader);
                BizSkuInfo sku = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                bizOrderDetail.setSkuInfo(skuInfo);
                if (bizOrderDetail.getSuplyis().getId()==0) {
                    if (orderDetailMap.containsKey(key.toString())) {
                        List<BizOrderDetail> orderDetails = orderDetailMap.get(key.toString());
                        orderDetails.add(bizOrderDetail);
                        orderDetailMap.put(key.toString(), orderDetails);
                    } else {
                        List<BizOrderDetail> orderDetails = new ArrayList<>();
                        orderDetails.add(bizOrderDetail);
                        orderDetailMap.put(key.toString(), orderDetails);
                    }
                }
//                if (bizOrderDetail.getSuplyis().getId()==0) {
//                    orderDetailMap.put(bizOrderDetail.getId() + "," + bizOrderDetail.getOrderHeader().getOrderNum(), bizOrderDetail);
//                }
//                BizSkuInfo bizSkuInfo = bizOrderDetail.getSkuInfo();
//                bizSkuInfo.setReqQty(bizOrderDetail.getOrdQty());
//                bizSkuInfo.setSentQty(bizOrderDetail.getSentQty());
//                bizSkuInfo.setOrderDetailIds(ordDetailArr[i].trim());
//                skuInfoMap = getSkuInfoData(map, bizSkuInfo.getId(), bizSkuInfo);

//				bizOrderDetail.setSkuInfo(skuInfo);
//				orderDetailList.add(bizOrderDetail);
            }
            model.addAttribute("orderDetailMap",orderDetailMap);
        }

//        for (Map.Entry<Integer, List<BizSkuInfo>> entry : skuInfoMap.entrySet()) {
//            List<BizSkuInfo> bizSkuInfoList = entry.getValue();
//            Integer reqQty = bizSkuInfoList.stream().mapToInt(item -> item.getReqQty() == null ? 0 : item.getReqQty()).sum();
//            Integer sendQty = bizSkuInfoList.stream().mapToInt(item -> item.getSentQty() == null ? 0 : item.getSentQty()).sum();
//            StringBuilder strOrderDetailIds = new StringBuilder();
//            StringBuilder strReqDetailIds = new StringBuilder();
//            for (BizSkuInfo sku : bizSkuInfoList) {
//                strOrderDetailIds.append(sku.getOrderDetailIds() == null ? 0 : sku.getOrderDetailIds());
//                strOrderDetailIds.append("_");
//                strReqDetailIds.append(sku.getReqDetailIds() == null ? 0 : sku.getReqDetailIds());
//                strReqDetailIds.append("_");
//            }
//            String strO = strOrderDetailIds.toString();
//            String strR = strReqDetailIds.toString();
//            BizSkuInfo sku = bizSkuInfoService.get(entry.getKey());
//            BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
//            if (skuInfo == null) {
//                continue;
//            }
//            sku.setReqQty(reqQty);
//            sku.setSentQty(sendQty);
//            sku.setOrderDetailIds(strO.substring(0, strO.length() - 1));
//            sku.setReqDetailIds(strR.substring(0, strR.length() - 1));
//            skuMap.put(skuInfo, sku);
//        }

//		model.addAttribute("requestDetailList",requestDetailList);
//		model.addAttribute("orderDetailList",orderDetailList);
//        model.addAttribute("skuInfoMap", skuMap);
        model.addAttribute("vendorId", vendorId);
        model.addAttribute("bizPoHeader", new BizPoHeader());

        return "modules/biz/po/bizPoHeaderForm";
    }

    private Map<Integer, List<BizSkuInfo>> getSkuInfoData(Map<Integer, List<BizSkuInfo>> map, Integer key, BizSkuInfo bizSkuInfo) {
        // BizSkuInfo bizSkuInfo= bizSkuInfoService.findListProd(bizSkuInfoService.get(key));
        if (map.containsKey(key)) {
            List<BizSkuInfo> skuInfos = map.get(key);
            map.remove(key);
            skuInfos.add(bizSkuInfo);
            map.put(key, skuInfos);
        } else {
            List<BizSkuInfo> skuInfoList = new ArrayList<BizSkuInfo>();
            skuInfoList.add(bizSkuInfo);
            map.put(key, skuInfoList);
        }
        return map;
    }

    /**
     * 分页
     * */
    private Page<BizRequestHeader> findBizRequest(BizRequestHeader bizRequestHeader,Page<BizRequestHeader> requestHeaderList,HttpServletRequest request, HttpServletResponse response) {
        bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
        bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
        requestHeaderList = bizRequestHeaderService.pageFindList(new Page<BizRequestHeader>(request, response), bizRequestHeader);
        List<BizRequestHeader> list = Lists.newArrayList();
        for (BizRequestHeader bizRequestHeader1 : requestHeaderList.getList()) {
            BizRequestDetail bizRequestDetail1 = new BizRequestDetail();
            bizRequestDetail1.setRequestHeader(bizRequestHeader1);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail1);
            Integer reqQtys = 0;
            Integer recvQtys = 0;
            Set set = new HashSet();
            StringBuffer sb = new StringBuffer();
            for (BizRequestDetail bizRequestDetail : requestDetailList) {
                reqQtys += bizRequestDetail.getReqQty();
                recvQtys += bizRequestDetail.getRecvQty();
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                set.add(bizSkuInfo.getProductInfo().getOffice().getId());
                sb.append(bizRequestDetail.getId());
                sb.append(",");
            }

            if (set.size() == 1) {
                for (Iterator it = set.iterator(); it.hasNext(); ) {
                    bizRequestHeader1.setOnlyVendor((Integer) it.next());
                    //System.out.println("value="+it.next().toString());
                }

                bizRequestHeader1.setReqDetailIds(sb.substring(0, sb.lastIndexOf(",")));
                bizRequestHeader1.setOwnGenPoOrder(true);
            }

            bizRequestHeader1.setReqQtys(reqQtys.toString());
            bizRequestHeader1.setRecvQtys(recvQtys.toString());
            list.add(bizRequestHeader1);
        }
        requestHeaderList.setList(list);
        return requestHeaderList;
    }

    /**
     * 导出
     * */
    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value ="ExportList",method = RequestMethod.POST)
    public String ExportList(String source, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader,HttpServletRequest request,HttpServletResponse response,RedirectAttributes redirectAttributes) {
        if(source==null && source.equals("")){
            source="";
        }
        model.addAttribute("source", source);
        Page<BizRequestHeader> requestHeaderPage = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Page<BizRequestHeader> page =null;
        Page<BizOrderHeader> headerPage =null;
        String fileName =null;
        try {
            //1订单
            List<List<String>> data = new ArrayList<List<String>>();
            //2商品
            List<List<String>> detailData = new ArrayList<List<String>>();
            if ("bhgh".equals(source)) {
                fileName = "备货清单数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
                page = findBizRequest(bizRequestHeader, requestHeaderPage,request,response);
                for (BizRequestHeader requestHeader : page.getList()) {
                    List<String> headerListData = new ArrayList();
                    headerListData.add(String.valueOf(requestHeader.getReqNo()));
                    if(requestHeader.getFromOffice()!=null && requestHeader.getFromOffice().getName()!=null){
                        headerListData.add(String.valueOf(requestHeader.getFromOffice().getName()));
                    }else{
                        headerListData.add("");
                    }
                    if(requestHeader.getRecvEta()!=null){
                        headerListData.add(String.valueOf(sdf.format(requestHeader.getRecvEta())));
                    }else{
                        headerListData.add("");
                    }
                    if(requestHeader.getReqQtys()!=null && requestHeader.getReqQtys()!=""){
                        headerListData.add(String.valueOf(requestHeader.getReqQtys()));
                    }else{
                        headerListData.add("");
                    }
                    if(requestHeader.getRecvQtys()!=null && requestHeader.getRecvQtys()!=""){
                        headerListData.add(String.valueOf(requestHeader.getRecvQtys()));
                    }else{
                        headerListData.add("");
                    }
                    if(requestHeader.getRemark()!=null){
                        headerListData.add(String.valueOf(requestHeader.getRemark()));
                    }else{
                        headerListData.add("");
                    }
                    Dict dict = new Dict();
                    dict.setDescription("备货单业务状态");
                    dict.setType("biz_req_status");
                    List<Dict> dictList = dictService.findList(dict);
                    for (Dict di : dictList) {
                        if (di.getValue().equals(String.valueOf(requestHeader.getBizStatus()))) {
                            //业务状态
                            headerListData.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    headerListData.add(String.valueOf(requestHeader.getCreateBy().getName()));
                    headerListData.add(String.valueOf(sdf.format(requestHeader.getUpdateDate())));
                    data.add(headerListData);
                    BizRequestDetail bizRequestDetail = new BizRequestDetail();
                    bizRequestDetail.setRequestHeader(requestHeader);
                    List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                    for (BizRequestDetail requestDetail : requestDetailList) {
                        List<String> detailListData = new ArrayList();
                        detailListData.add(String.valueOf(requestHeader.getReqNo()));
                        if(requestHeader.getFromOffice()!=null && requestHeader.getFromOffice().getName()!=null){
                            detailListData.add(String.valueOf(requestHeader.getFromOffice().getName()));
                        }else{
                            detailListData.add("");
                        }
                        BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
                        if(skuInfo!=null){
                            if(skuInfo.getName()!=null) {
                                detailListData.add(String.valueOf(skuInfo.getName()));
                            }else{
                                detailListData.add("");
                            }
                            if(skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getCategoryInfoList()!=null){
                                List<BizCategoryInfo> categoryInfoList = skuInfo.getProductInfo().getCategoryInfoList();
                                if(categoryInfoList.size()!=0){
                                    for (BizCategoryInfo bizCategoryInfo : categoryInfoList) {
                                        detailListData.add(String.valueOf(bizCategoryInfo.getName()));
                                        break;
                                    }
                                }else{
                                    detailListData.add("");
                                }
                            }else{
                                detailListData.add("");
                            }
                            if(skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getProdCode()!=null || skuInfo.getProductInfo().getBrandName()!=null){
                                detailListData.add(String.valueOf(skuInfo.getProductInfo().getProdCode()));
                                detailListData.add(String.valueOf(skuInfo.getProductInfo().getBrandName()));
                                if(skuInfo.getProductInfo().getOffice()!=null && skuInfo.getProductInfo().getOffice().getName()!=null){
                                    detailListData.add(String.valueOf(skuInfo.getProductInfo().getOffice().getName()));
                                }else{
                                    detailListData.add("");
                                }
                            }else{
                                detailListData.add("");
                                detailListData.add("");
                                detailListData.add("");
                            }
                            detailListData.add(String.valueOf(skuInfo.getName()));
                            detailListData.add(String.valueOf(skuInfo.getPartNo()));
                            detailListData.add(String.valueOf(requestDetail.getReqQty()));
                            detailListData.add(String.valueOf(requestDetail.getSendQty()));
                        }else{
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                            detailListData.add("");
                        }
                        detailData.add(detailListData);
                    }
                }
            } else if ("xsgh".equals(source)) {
                fileName = "订单采购数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
                headerPage=bizOrderHeaderService.pageFindList(new Page<BizOrderHeader>(request, response), bizOrderHeader);
                for (BizOrderHeader stringHeader : headerPage.getList()) {
                    List<String> headerListData = new ArrayList();
                    headerListData.add(String.valueOf(stringHeader.getOrderNum()));
                    Dict dict = new Dict();
                    dict.setDescription("订单类型");
                    dict.setType("order_biz_type");
                    List<Dict> dictList = dictService.findList(dict);
                    for (Dict di : dictList) {
                        if (di.getValue().equals(String.valueOf(stringHeader.getBizType()))) {
                            //类型
                            headerListData.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    if(stringHeader.getCustomer()!=null && stringHeader.getCustomer().getName()!=null){
                        headerListData.add(String.valueOf(stringHeader.getCustomer().getName()));
                    }else{
                        headerListData.add("");
                    }
                    headerListData.add(String.valueOf(stringHeader.getTotalDetail()));
                    headerListData.add(String.valueOf(stringHeader.getTotalExp()));
                    headerListData.add(String.valueOf(stringHeader.getFreight()));
                    Dict invdict = new Dict();
                    invdict.setDescription("发票状态");
                    invdict.setType("biz_order_invStatus");
                    List<Dict> invdictList = dictService.findList(invdict);
                    for (Dict di : invdictList) {
                        if (di.getValue().equals(String.valueOf(stringHeader.getInvStatus()))) {
                            //发票
                            headerListData.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    Dict bizdict = new Dict();
                    bizdict.setDescription("业务状态");
                    bizdict.setType("biz_order_status");
                    List<Dict> bizdictList = dictService.findList(bizdict);
                    for (Dict di : bizdictList) {
                        if (di.getValue().equals(String.valueOf(stringHeader.getBizStatus()))) {
                            //业务
                            headerListData.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    if(stringHeader.getPlatformInfo()!=null && stringHeader.getPlatformInfo().getName()!=null){
                        headerListData.add(String.valueOf(stringHeader.getPlatformInfo().getName()));
                    }else{
                        headerListData.add("");
                    }
                    headerListData.add(String.valueOf(stringHeader.getCreateBy().getName()));
                    headerListData.add(String.valueOf(sdf.format(stringHeader.getUpdateDate())));
                    data.add(headerListData);
                    BizOrderDetail orderDetail = new BizOrderDetail();
                    orderDetail.setOrderHeader(stringHeader);
                    List<BizOrderDetail> list = bizOrderDetailService.findList(orderDetail);
                    if(list.size()!=0){
                        for (BizOrderDetail bizOrderDetail : list) {
                            BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                            List<String> detailListData = new ArrayList();
                            detailListData.add(String.valueOf(stringHeader.getOrderNum()));
                            if(bizOrderDetail.getShelfInfo()!=null && bizOrderDetail.getShelfInfo().getOpShelfInfo()!=null && bizOrderDetail.getShelfInfo().getOpShelfInfo().getName()!=null){
                                detailListData.add(String.valueOf(bizOrderDetail.getShelfInfo().getOpShelfInfo().getName()));
                            }else{
                                detailListData.add("");
                            }
                            if(bizOrderDetail.getSkuName()!=null){
                                detailListData.add(String.valueOf(bizOrderDetail.getSkuName()));
                            }else{
                                detailListData.add("");
                            }
                            if(bizOrderDetail.getPartNo()!=null){
                                detailListData.add(String.valueOf(bizOrderDetail.getPartNo()));
                            }else{
                                detailListData.add("");
                            }
                            if(bizSkuInfo!=null && bizSkuInfo.getItemNo()!=null){
                                detailListData.add(String.valueOf(bizSkuInfo.getItemNo()));
                            }else{
                                detailListData.add("");
                            }
                            detailListData.add(String.valueOf(bizOrderDetail.getBuyPrice()));
                            if(bizOrderDetail.getVendor()!=null && bizOrderDetail.getVendor().getName()!=null){
                                detailListData.add(String.valueOf(bizOrderDetail.getVendor().getName()));
                            }else{
                                detailListData.add("");
                            }
                            if(bizOrderDetail.getPrimary()!=null && bizOrderDetail.getPrimary().getMobile()!=null){
                                detailListData.add(String.valueOf(bizOrderDetail.getPrimary().getMobile()));
                            }else{
                                detailListData.add("");
                            }
                            detailListData.add(String.valueOf(bizOrderDetail.getUnitPrice()));
                            detailListData.add(String.valueOf(bizOrderDetail.getOrdQty()));
                            Double unOrd=0.0;
                            if(bizOrderDetail.getUnitPrice()!=null && bizOrderDetail.getOrdQty()!=null){
                                //总额
                                unOrd=bizOrderDetail.getUnitPrice()*bizOrderDetail.getOrdQty();
                            }
                            detailListData.add(String.valueOf(unOrd));
                            detailListData.add(String.valueOf(bizOrderDetail.getSentQty()));
                            detailListData.add(String.valueOf(sdf.format(bizOrderDetail.getCreateDate())));
                            detailData.add(detailListData);
                        }
                    }
                }
            }
            if ("bhgh".equals(source)) {
                String[] headers = {"备货单号", "采购中心", "期望收货时间", "备货商品数量", "已到货数量","备注", "业务状态", "申请人",
                    "更新时间"};
                String[] details = {"备货单号", "采购中心", "商品名称", "商品分类", "商品代码","品牌名称", "供应商", "SKU","SKU编号","申报数量","已供货数量"};
                ExportExcelUtils eeu = new ExportExcelUtils();
                SXSSFWorkbook workbook = new SXSSFWorkbook();
                eeu.exportExcel(workbook, 0, "备货清单数据", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "备货商品数据", details, detailData, fileName);
                response.reset();
                response.setContentType("application/octet-stream; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
                workbook.write(response.getOutputStream());
                workbook.dispose();
            } else if ("xsgh".equals(source)) {
                String[] headers = {"订单编号", "订单类型", "采购商名称", "订单详情总价", "订单总费用","运费", "发票状态", "业务状态",
                        "订单来源","创建人","更新时间"};
                String[] details = {"订单编号", "货架名称", "商品名称", "商品编号", "商品货号","商品出厂价", "供应商", "供应商电话","商品单价",
                        "采购数量","总额","已发货数量","创建时间"};
                ExportExcelUtils eeu = new ExportExcelUtils();
                SXSSFWorkbook workbook = new SXSSFWorkbook();
                eeu.exportExcel(workbook, 0, "订单采购数据", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "商品数据", details, detailData, fileName);
                response.reset();
                response.setContentType("application/octet-stream; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
                workbook.write(response.getOutputStream());
                workbook.dispose();
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            if ("bhgh".equals(source)) {
                addMessage(redirectAttributes, "导出备货清单数据失败！失败信息：" + e.getMessage());
            } else if ("xsgh".equals(source)) {
                addMessage(redirectAttributes, "导出订单采购数据失败！失败信息：" + e.getMessage());
            }
        }
        return "redirect:" + adminPath + "/biz/request/bizRequestOrder/list?source="+source;
    }

}