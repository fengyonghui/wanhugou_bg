/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.biz.web.po.BizPoHeaderController;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.ReqFromTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private OfficeService officeService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private DictService dictService;
    @Autowired
    private CommonImgService commonImgService;

    public static final String REQUEST_HEADER_TYPE = "1";
    public static final String DO_ORDER_HEADER_TYPE = "2";


    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"list", ""})
    public String list(String source, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader,HttpServletRequest request,HttpServletResponse response) {
        List<BizRequestHeader> requestHeaderList = null;
        Page<BizRequestHeader> requestHeaderPage = null;
        List<BizOrderHeader> orderHeaderList = null;
        Page<BizOrderHeader> orderHeaderPage = null;
        //备货清单供货
        if ("bhgh".equals(source)) {
            Page<BizRequestHeader> page = findBizRequest(bizRequestHeader, requestHeaderPage,request,response);
            //分页 20180427 改
            model.addAttribute("page", page);
            //判断
            model.addAttribute("requestHeaderPage", page.getList());

        } else if ("xsgh".equals(source)) { //订单供货
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
            orderHeaderPage=bizOrderHeaderService.pageFindList(new Page<BizOrderHeader>(request, response), bizOrderHeader);

            Set<Integer> set = new HashSet();
            List<BizOrderHeader> list = Lists.newArrayList();
            for (BizOrderHeader bizOrderHeader1 : orderHeaderPage.getList()) {
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
            //20180427 分页
            model.addAttribute("page", orderHeaderPage);
            //判断
            model.addAttribute("orderHeaderPage", orderHeaderPage.getList());
        }

        model.addAttribute("source", source);
        return "modules/biz/request/bizRequestOrderList";
    }


    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"listV2"})
    public String listV2(String source, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader,HttpServletRequest request,HttpServletResponse response) {
        //备货清单供货
        if ("bhgh".equals(source)) {
            Page<BizRequestHeader> page = findBizRequestV2(bizRequestHeader,request,response);
            //分页 20180427 改
            model.addAttribute("page", page);
            //判断
            model.addAttribute("requestHeaderPage", page.getList());

        } else if ("xsgh".equals(source)) { //订单供货
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
            Page<BizOrderHeader>  orderHeaderPage=bizOrderHeaderService.pageFindList(new Page<BizOrderHeader>(request, response), bizOrderHeader);
            //20180427 分页
            model.addAttribute("page", orderHeaderPage);
            //判断
            model.addAttribute("orderHeaderPage", orderHeaderPage.getList());
        }

        model.addAttribute("source", source);
        return "modules/biz/request/bizRequestOrderListV2";
    }

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = "listForPhotoOrder")
    public String listForPhotoOrder(String source, Model model, BizOrderHeader bizOrderHeader,HttpServletRequest request,HttpServletResponse response) {
        bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
        bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
        Page<BizOrderHeader> orderHeaderPage = bizOrderHeaderService.pageFindListForPhotoOrder(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        //20180427 分页
        model.addAttribute("page", orderHeaderPage);
        //判断
        model.addAttribute("orderHeaderPage", orderHeaderPage.getList());

        model.addAttribute("source", source);
        return "modules/biz/request/bizRequestOrderListForPhotoOrder";
    }


    /**
     * 分页
     * */
    private Page<BizRequestHeader> findBizRequestV2(BizRequestHeader bizRequestHeader,HttpServletRequest request, HttpServletResponse response) {
        bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
        bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
        bizRequestHeader.setFromType(ReqFromTypeEnum.CENTER_TYPE.getType());
        Page<BizRequestHeader> requestHeaderList = bizRequestHeaderService.pageFindListV2(new Page<BizRequestHeader>(request, response), bizRequestHeader);

        return requestHeaderList;
    }

    private Page<BizRequestHeader> findBizRequestV3(BizRequestHeader bizRequestHeader,HttpServletRequest request, HttpServletResponse response) {
        bizRequestHeader.setFromType(ReqFromTypeEnum.CENTER_TYPE.getType());
        return bizRequestHeaderService.pageFindListV2(new Page<BizRequestHeader>(request, response), bizRequestHeader);
    }


    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"form", ""})
    public String form(String source, Model model, Office office) {
        List<Map> requestMap = new ArrayList<>();
       if ("gh".equals(source)) {
            requestMap = bizOrderDetailService.findRequestTotalByVendorList(office);
        }
        model.addAttribute("source", source);
        model.addAttribute("map", requestMap);
        return "modules/biz/request/bizRequestPoList";
    }

    @RequestMapping(value = "goList")
    public String goList(String reqIds, Integer vendorId, String ordIds, Model model) {
        Map<String,List<BizRequestDetail>> reqDetailMap = new LinkedHashMap<>();
        Map<String,List<BizOrderDetail>> orderDetailMap = new LinkedHashMap<>();

        if (StringUtils.isNotBlank(reqIds)) {
            String[] reqDetailArr = reqIds.split(",");
            for (int i = 0; i < reqDetailArr.length; i++) {
                if (StringUtils.isBlank(reqDetailArr[i])){
                    continue;
                }
                BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailArr[i].trim()));
                Integer key =bizRequestDetail.getRequestHeader().getId();
                Integer lineNo=bizRequestDetail.getLineNo();
                BizPoOrderReq bizPoOrderReq =new BizPoOrderReq();
                bizPoOrderReq.setSoLineNo(lineNo);
                bizPoOrderReq.setRequestHeader(bizRequestDetail.getRequestHeader());
                bizPoOrderReq.setIsPrew(0);
                List<BizPoOrderReq> poOrderReqList=bizPoOrderReqService.findList(bizPoOrderReq);
                if(poOrderReqList!=null && poOrderReqList.size()==0){
                    BizSkuInfo sku = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                    BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizRequestDetail.getRequestHeader().getId());
                    bizRequestDetail.setRequestHeader(bizRequestHeader);
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                    bizRequestDetail.setSkuInfo(skuInfo);
                    if(reqDetailMap.containsKey(key.toString())){
                        List<BizRequestDetail> requestDetails = reqDetailMap.get(key.toString());
                        requestDetails.add(bizRequestDetail);
                        reqDetailMap.put(key.toString(),requestDetails);
                    }else {
                        List<BizRequestDetail> requestDetails =  new ArrayList<>();
                        requestDetails.add(bizRequestDetail);
                        reqDetailMap.put(key.toString(),requestDetails);
                    }
                }
            }

            model.addAttribute("reqDetailMap",reqDetailMap);
        }
        if (StringUtils.isNotBlank(ordIds)) {
            String[] ordDetailArr = ordIds.split(",");
            for (int i = 0; i < ordDetailArr.length; i++) {
                if (StringUtils.isBlank(ordDetailArr[i])){
                    continue;
                }
                BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(ordDetailArr[i].trim()));
                Integer key = bizOrderDetail.getOrderHeader().getId();
                Integer lineNo=bizOrderDetail.getLineNo();

                BizPoOrderReq bizPoOrderReq =new BizPoOrderReq();
                bizPoOrderReq.setSoLineNo(lineNo);
                bizPoOrderReq.setOrderHeader(bizOrderDetail.getOrderHeader());
                bizPoOrderReq.setIsPrew(0);
                List<BizPoOrderReq> poOrderReqList=bizPoOrderReqService.findList(bizPoOrderReq);
                if(poOrderReqList!=null && poOrderReqList.size()==0){
                    BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                    bizOrderDetail.setOrderHeader(bizOrderHeader);
                    BizSkuInfo sku = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                    bizOrderDetail.setSkuInfo(skuInfo);
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



            }
            model.addAttribute("orderDetailMap",orderDetailMap);
        }
        model.addAttribute("type","goList");
        model.addAttribute("vendorId", vendorId);
        model.addAttribute("bizPoHeader", new BizPoHeader());

        return "modules/biz/po/bizPoHeaderForm";
    }


    @RequestMapping(value = "goListForPhotoOrder")
    public String goListForPhotoOrder(HttpServletRequest request, Integer ordIds) {
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(ordIds);
        BizOrderAddress bizOrderAddress = bizOrderAddressService.get(bizOrderHeader.getBizLocation().getId());

        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(ordIds);
        commonImg.setImgType(ImgEnum.ORDER_SKU_PHOTO.getCode());
        commonImg.setObjectName("biz_order_header");
        List<CommonImg> imgList = commonImgService.findList(commonImg);

        Office vendor = officeService.get(bizOrderHeader.getSellersId());

        CommonImg compactImg = new CommonImg();
        compactImg.setImgType(ImgEnum.VEND_COMPACT.getCode());
        compactImg.setObjectId(vendor.getId());
        compactImg.setObjectName(BizPoHeaderController.VEND_IMG_TABLE_NAME);
        List<CommonImg> compactImgList = commonImgService.findList(compactImg);
        request.setAttribute("compactImgList", compactImgList);

        CommonImg identityCardImg = new CommonImg();
        identityCardImg.setImgType(ImgEnum.VEND_IDENTITY_CARD.getCode());
        identityCardImg.setObjectId(vendor.getId());
        identityCardImg.setObjectName(BizPoHeaderController.VEND_IMG_TABLE_NAME);
        List<CommonImg> identityCardImgList = commonImgService.findList(identityCardImg);
        request.setAttribute("identityCardImgList", identityCardImgList);

        request.setAttribute("imgList", imgList);
        request.setAttribute("vendor", vendor);
        request.setAttribute("bizOrderHeader", bizOrderHeader);
        request.setAttribute("bizOrderAddress", bizOrderAddress);
        return "modules/biz/po/bizPoHeaderFormForPhotoOrder";
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
                fileName = "备货清单采购数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
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
                eeu.exportExcel(workbook, 0, "备货清单采购", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "备货商品数据", details, detailData, fileName);
                response.reset();
                response.setContentType("application/octet-stream; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
                workbook.write(response.getOutputStream());
                workbook.dispose();
            } else if ("xsgh".equals(source)) {
                String[] headers = {"订单编号", "订单类型", "经销店名称", "订单详情总价", "订单总费用","运费", "发票状态", "业务状态",
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
                addMessage(redirectAttributes, "导出备货清单采购数据失败！失败信息：" + e.getMessage());
            } else if ("xsgh".equals(source)) {
                addMessage(redirectAttributes, "导出订单采购数据失败！失败信息：" + e.getMessage());
            }
        }
        return "redirect:" + adminPath + "/biz/request/bizRequestOrder/list?source="+source;
    }


    @RequestMapping(value = "goListAutoSave")
    @ResponseBody
    public Map<String, String> goListForAutoSave(Integer orderId, String type, HttpServletRequest request,HttpServletResponse response) {
        Map<String, String> result = new HashMap<String, String>();
        if (REQUEST_HEADER_TYPE.equals(type)) {
            BizRequestHeader requestHeader = new BizRequestHeader();
            requestHeader.setId(orderId);
            Page<BizRequestHeader> requestHeaderList = findBizRequestV3(requestHeader,request,response);
            if (requestHeaderList.getList().size() > 0) {
                requestHeader = requestHeaderList.getList().get(0);
                String reqDetailIds = requestHeader.getReqDetailIds();
                Integer vendorId = requestHeader.getOnlyVendor();
                result.put("reqDetailIds", reqDetailIds);
                result.put("vendorId", String.valueOf(vendorId));
                String unitPrices = "";
                String ordQtys = "";
                Map<String,List<BizRequestDetail>> reqDetailMap = new LinkedHashMap<>();
                if (StringUtils.isNotBlank(reqDetailIds)) {
                    String[] reqDetailArr = reqDetailIds.split(",");
                    for (int i = 0; i < reqDetailArr.length; i++) {
                        if (StringUtils.isBlank(reqDetailArr[i])){
                            continue;
                        }
                        BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailArr[i].trim()));
                        Integer reqQty = bizRequestDetail.getReqQty();
                        Integer recvQty = bizRequestDetail.getRecvQty();
                        Integer ordQty = reqQty - recvQty;
                        ordQtys += ordQty + ",";
                        Integer key =bizRequestDetail.getRequestHeader().getId();
                        Integer lineNo=bizRequestDetail.getLineNo();
                        BizPoOrderReq bizPoOrderReq =new BizPoOrderReq();
                        bizPoOrderReq.setSoLineNo(lineNo);
                        bizPoOrderReq.setRequestHeader(bizRequestDetail.getRequestHeader());
                        bizPoOrderReq.setIsPrew(0);
                        List<BizPoOrderReq> poOrderReqList=bizPoOrderReqService.findList(bizPoOrderReq);
                        if(poOrderReqList!=null && poOrderReqList.size()==0){
                            BizSkuInfo sku = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                            BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizRequestDetail.getRequestHeader().getId());
                            bizRequestDetail.setRequestHeader(bizRequestHeader);
                            BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                            Double buyPrice = skuInfo.getBuyPrice();
                            unitPrices += String.valueOf(buyPrice) + ",";
                            bizRequestDetail.setSkuInfo(skuInfo);
                            if(reqDetailMap.containsKey(key.toString())){
                                List<BizRequestDetail> requestDetails = reqDetailMap.get(key.toString());
                                requestDetails.add(bizRequestDetail);
                                reqDetailMap.put(key.toString(),requestDetails);
                            }else {
                                List<BizRequestDetail> requestDetails =  new ArrayList<>();
                                requestDetails.add(bizRequestDetail);
                                reqDetailMap.put(key.toString(),requestDetails);
                            }
                        }
                    }
                    ordQtys = ordQtys.substring(0, ordQtys.length()-1);
                    unitPrices = unitPrices.substring(0, unitPrices.length()-1);
                    result.put("ordQtys", ordQtys);
                    result.put("unitPrices", unitPrices);
                    //model.addAttribute("reqDetailMap",reqDetailMap);
                }

            }
        } else if (DO_ORDER_HEADER_TYPE.equals(type)) {
            BizOrderHeader orderHeader = new BizOrderHeader();
            orderHeader.setId(orderId);
            orderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            orderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
            Page<BizOrderHeader>  bizOrderHeaderList =bizOrderHeaderService.pageFindList(new Page<BizOrderHeader>(request, response), orderHeader);
            if (bizOrderHeaderList.getList().size() > 0) {
                orderHeader = bizOrderHeaderList.getList().get(0);
                String orderDetailIds = orderHeader.getOrderDetails();
                Integer vendorId = orderHeader.getOnlyVendor();

                result.put("orderDetailIds", orderDetailIds);
                result.put("vendorId", String.valueOf(vendorId));
                String unitPrices = "";
                String ordQtys = "";
                Map<String, List<BizOrderDetail>> orderDetailMap = new LinkedHashMap<>();
                if (StringUtils.isNotBlank(orderDetailIds)) {
                    String[] orderDetailArr = orderDetailIds.split(",");
                    for (int i = 0; i < orderDetailArr.length; i++) {
                        if (StringUtils.isBlank(orderDetailArr[i])) {
                            continue;
                        }
                        BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailArr[i].trim()));
                        Integer ordQty = bizOrderDetail.getOrdQty();
                        Integer sentQty = bizOrderDetail.getSentQty();
                        Integer ordHerderQty = ordQty - sentQty;
                        ordQtys += ordHerderQty + ",";

                        Integer key = bizOrderDetail.getOrderHeader().getId();
                        Integer lineNo = bizOrderDetail.getLineNo();
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        bizPoOrderReq.setSoLineNo(lineNo);
                        bizPoOrderReq.setOrderHeader(bizOrderDetail.getOrderHeader());
                        bizPoOrderReq.setIsPrew(0);
                        List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                        if (poOrderReqList != null && poOrderReqList.size() == 0) {
                            BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                            bizOrderDetail.setOrderHeader(bizOrderHeader);
                            BizSkuInfo sku = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                            BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                            Double buyPrice = skuInfo.getBuyPrice();
                            unitPrices += String.valueOf(buyPrice) + ",";
                            bizOrderDetail.setSkuInfo(skuInfo);
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
                    }
                    ordQtys = ordQtys.substring(0, ordQtys.length() - 1);
                    unitPrices = unitPrices.substring(0, unitPrices.length() - 1);
                    result.put("ordQtys", ordQtys);
                    result.put("unitPrices", unitPrices);
                }
            }
        }

//        var orderDetailIds = result['orderDetailIds'];
//        var vendorId = result['vendorId'];
//        var unitPrices = result['unitPrices'];
//        var ordQtys = result['ordQtys'];
        return result;
    }

}