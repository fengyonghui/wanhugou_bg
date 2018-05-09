/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Controller
 *
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeader")
public class BizOrderHeaderController extends BaseController {

    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private OfficeService officeService;
    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;
    @Autowired
    private BizCustCreditService bizCustCreditService;
    @Autowired
    private BizPayRecordService bizPayRecordService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private DictService dictService;
    @Autowired
    private SystemService systemService;

    @ModelAttribute
    public BizOrderHeader get(@RequestParam(required = false) Integer id) {
        BizOrderHeader entity = null;
        Double sum = 0.0;
        if (id != null && id != 0) {
            entity = bizOrderHeaderService.get(id);
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(entity);
            List<BizOrderDetail> list = bizOrderDetailService.findList(bizOrderDetail);
            for (BizOrderDetail orderDetail : list) {
                if(orderDetail.getSuplyis()!=null && orderDetail.getSuplyis().getId()!=null){
                    if(orderDetail.getSuplyis().getId().equals(0) || orderDetail.getSuplyis().getId().equals(721)){
                        Office office = new Office();
                        office.setName("供货部");
                        office.setId(orderDetail.getSuplyis().getId());
                        orderDetail.setSuplyis(office);
                    }
                }
            }
            entity.setTotalDetail(entity.getTotalDetail());
            entity.setOrderDetailList(list);
        }
        if (entity == null) {
            entity = new BizOrderHeader();
        }
        return entity;
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        model.addAttribute("page", page);
        return "modules/biz/order/bizOrderHeaderList";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "form")
    public String form(BizOrderHeader bizOrderHeader, Model model, String orderNoEditable, String orderDetails) {
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            bizOrderHeader.setCustomer(office);
            model.addAttribute("entity2", bizOrderHeader);
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
            BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            if (bizCustomCenterConsultant != null) {
                bizCustomCenterConsultant.setConsultants(systemService.getUser(bizCustomCenterConsultant.getConsultants().getId()));
                model.addAttribute("orderCenter", bizCustomCenterConsultant);
            } else {
                model.addAttribute("orderCenter", new BizCustomCenterConsultant());
            }
        }
        BizOrderHeader bizOrderHeaderTwo = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (bizOrderHeader.getId() != null) {
            Double totalDetail = bizOrderHeaderTwo.getTotalDetail();//订单详情总价
            Double totalExp = bizOrderHeaderTwo.getTotalExp();//订单总费用
            Double freight = bizOrderHeaderTwo.getFreight();//运费
            Double orderHeaderTotal = totalDetail + totalExp + freight;
            bizOrderHeader.setTobePaid(orderHeaderTotal - bizOrderHeaderTwo.getReceiveTotal());//页面显示待支付总价
            if (orderNoEditable != null && orderNoEditable.equals("editable")) {//不可编辑标识符
                bizOrderHeaderTwo.setOrderNoEditable("editable");//待支付页面不能修改
            }
            if (orderDetails != null && orderDetails.equals("details")) {
                bizOrderHeaderTwo.setOrderDetails("details");//查看详情页面不能修改
            }
            BizOrderAddress bizOrderAddress = new BizOrderAddress();
            bizOrderAddress.setId(bizOrderHeaderTwo.getBizLocation().getId());
            List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
            for (BizOrderAddress orderAddress : list) {
//				    收货地址
                if (orderAddress.getType() == 1) {
                    model.addAttribute("orderAddress", orderAddress);
                }
            }
            BizOrderAddress orderAddress = new BizOrderAddress();
            orderAddress.setOrderHeaderID(bizOrderHeaderTwo);
            List<BizOrderAddress> Addresslist = bizOrderAddressService.findList(orderAddress);
            for (BizOrderAddress address : Addresslist) {
//				交货地址
                if (address.getType() == 2) {
                    model.addAttribute("address", address);
                }
            }
        }
        BizOrderHeader boh = new BizOrderHeader();
        if (bizOrderHeader != null) {
            boh.setCustomer(bizOrderHeader.getCustomer());
            boh.setBizStatus(BizOrderDiscount.ONE_ORDER.getOneOr());//条件为0
            List<BizOrderHeader> list = bizOrderHeaderDao.findListFirstOrder(boh);
            if (list.size() == 0) {
                logger.info("--是首单--");
                bizOrderHeader.setOneOrder("firstOrder");
            }
        }
        model.addAttribute("entity", bizOrderHeader);
        return "modules/biz/order/bizOrderHeaderForm";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "save")
    public String save(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizOrderHeader)) {
            return form(bizOrderHeader, model, null, null);
        }
        if (bizOrderHeader.getPlatformInfo() == null) {
            bizOrderHeader.getPlatformInfo().setId(6);
        }
        bizOrderHeaderService.save(bizOrderHeader);
        addMessage(redirectAttributes, "保存订单信息成功");
        Integer orId = bizOrderHeader.getId();
        String oneOrder = bizOrderHeader.getOneOrder();
        if (bizOrderHeader.getClientModify() != null && bizOrderHeader.getClientModify().equals("client_modify")) {
//			保存跳回客户专员
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=" + bizOrderHeader.getConsultantId();
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/?oneOrder=" + oneOrder;
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "delete")
    public String delete(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_DELETE);
        bizOrderHeaderService.delete(bizOrderHeader);
        addMessage(redirectAttributes, "删除订单信息成功");
        String a="cendDelete";
        if(bizOrderHeader.getFlag()!=null && bizOrderHeader.getFlag().equals(a)){
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/?repage&customer.id=" + bizOrderHeader.getCustomer().getId();
    }


    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "recovery")
    public String recovery(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_NORMAL);
        bizOrderHeaderService.delete(bizOrderHeader);
        addMessage(redirectAttributes, "恢复订单信息成功");
        String a="cendRecover";
        if(bizOrderHeader.getFlag()!=null && bizOrderHeader.getFlag().equals(a)){
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/?repage&customer.id=" + bizOrderHeader.getCustomer().getId();
    }


    /**
     * @param bizOrderHeader
     * @param flag           0为采购中心出库
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderDetail:view")
    @RequestMapping(value = "findByOrder")
    public Map<String, Object> findByOrder(BizOrderHeader bizOrderHeader, String flag, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user= UserUtils.getUser();
        if (StringUtils.isNotBlank(flag) && "0".equals(flag)) {
            bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            List<Role>roleList= user.getRoleList();
            Role role=new Role();
            role.setEnname(RoleEnNameEnum.DEPT.getState());
            if(user.isAdmin() ||roleList.contains(role) ){
                bizOrderHeader.setSupplyId(-1); //判断orderDetail不等于0
            }else {
                bizOrderHeader.setSupplyId(user.getCompany()==null?0:user.getCompany().getId());
            }


        } else {
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.PURCHASING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());

        }

        List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
        Map<String, Object> map = new HashMap<String, Object>();
        List<BizOrderHeader> bizOrderHeaderList = Lists.newArrayList();
        BizPoOrderReq bizPoOrderReq = null;
        if (StringUtils.isNotBlank(flag) && !"0".equals(flag)) {
            bizPoOrderReq = new BizPoOrderReq();
        }

        for (BizOrderHeader orderHeader : list) {
            List<BizOrderDetail> bizOrderDetailList = Lists.newArrayList();
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(orderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            if (StringUtils.isNotBlank(flag) && !"0".equals(flag)) {
                bizPoOrderReq.setOrderHeader(orderHeader);
            }
            for (BizOrderDetail orderDetail : orderDetailList) {
                if (StringUtils.isNotBlank(flag) && !"0".equals(flag)) {
                    bizPoOrderReq.setSoLineNo(orderDetail.getLineNo());
                    bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                    List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (poOrderReqList != null && poOrderReqList.size() > 0) {
                        BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                        orderDetail.setSkuInfo(skuInfo);
                        bizOrderDetailList.add(orderDetail);
                    }
                } else {
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                    orderDetail.setSkuInfo(skuInfo);
                    bizOrderDetailList.add(orderDetail);
                }

            }
            orderHeader.setOrderDetailList(bizOrderDetailList);

            bizOrderHeaderList.add(orderHeader);

        }
        List<BizInventoryInfo> inventoryInfoList = bizInventoryInfoService.findList(new BizInventoryInfo());
        map.put("inventoryInfoList", inventoryInfoList);
        map.put("bizOrderHeaderList", bizOrderHeaderList);

        return map;
    }

    /**
     * 用于客户专员页面-订单管理列表中的待审核验证
     **/
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "Commissioner")
    public String Commissioner(BizOrderHeader bizOrderHeader, String localSendIds, String boo, Integer objJsp, Model model, RedirectAttributes redirectAttributes) {
        String commis = "comError";
        try {
            if (bizOrderHeader.getId() != null) {
                BizOrderHeader bh = bizOrderHeaderService.get(bizOrderHeader);
                if (bh != null) {
                    if (objJsp == OrderHeaderBizStatusEnum.SUPPLYING.getState()) {
                        bh.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());//15供货中
                        bizOrderHeaderService.saveOrderHeader(bh);//保存状态
                        BizOrderAddress OrderAddressTwo = new BizOrderAddress();
                        OrderAddressTwo.setOrderHeaderID(bh);
                        List<BizOrderAddress> list = bizOrderAddressService.findList(OrderAddressTwo);
                        for (BizOrderAddress bizOrderAddress : list) {
                            if (bizOrderAddress.getType() == 2) {
                                OrderAddressTwo.setId(bizOrderAddress.getId());
                            }
                        }
                        OrderAddressTwo.setAppointedTime(bizOrderHeader.getBizLocation().getAppointedTime());
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getProvince()!=null && bizOrderHeader.getBizLocation().getCity()!=null
                                && bizOrderHeader.getBizLocation().getRegion()!=null){
                            OrderAddressTwo.setProvince(bizOrderHeader.getBizLocation().getProvince());
                            OrderAddressTwo.setCity(bizOrderHeader.getBizLocation().getCity());
                            OrderAddressTwo.setRegion(bizOrderHeader.getBizLocation().getRegion());
                        }
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getAddress()!=null){
                            OrderAddressTwo.setAddress(bizOrderHeader.getBizLocation().getAddress());
                        }else{
                            OrderAddressTwo.setAddress("");
                        }
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getReceiver()!=null){
                            OrderAddressTwo.setReceiver(bizOrderHeader.getBizLocation().getReceiver());
                        }else{
                            OrderAddressTwo.setReceiver("");
                        }
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getPhone()!=null){
                            OrderAddressTwo.setPhone(bizOrderHeader.getBizLocation().getPhone());
                        }else{
                            OrderAddressTwo.setPhone("");
                        }
                        OrderAddressTwo.setType(2);
                        bizOrderAddressService.save(OrderAddressTwo);
                        if (StringUtils.isNotBlank(localSendIds) && StringUtils.isNotBlank(boo)) {
                            String[] sidArr = localSendIds.split(",");
                            String[] booStr = boo.split(",");
                            for (int i = 0; i < sidArr.length; i++) {
                                BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(sidArr[i].trim()));
                                if ("false".equals(booStr[i].trim())) {
                                    bizOrderDetail.setSuplyis(officeService.get(0));
                                } else {
                                    BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                                    BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(orderHeader.getCustomer().getId());
                                    bizOrderDetail.setSuplyis(officeService.get(bizCustomCenterConsultant.getCenters().getId()));

                                }
                                bizOrderDetailService.saveStatus(bizOrderDetail);
                            }
                        }
                        commis = "ok";
                    } else if (objJsp == OrderHeaderBizStatusEnum.UNAPPROVE.getState()) {
                        bh.setBizStatus(OrderHeaderBizStatusEnum.UNAPPROVE.getState());//45审核失败
                        bizOrderHeaderService.saveOrderHeader(bh);//保存状态
                        BizOrderAddress OrderAddressTwo = new BizOrderAddress();
                        OrderAddressTwo.setOrderHeaderID(bh);
                        List<BizOrderAddress> list = bizOrderAddressService.findList(OrderAddressTwo);
                        for (BizOrderAddress bizOrderAddress : list) {
                            if (bizOrderAddress.getType() == 2) {
                                OrderAddressTwo.setId(bizOrderAddress.getId());
                            }
                        }
                        OrderAddressTwo.setAppointedTime(bizOrderHeader.getBizLocation().getAppointedTime());
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getProvince()!=null && bizOrderHeader.getBizLocation().getCity()!=null
                                && bizOrderHeader.getBizLocation().getRegion()!=null){
                            OrderAddressTwo.setProvince(bizOrderHeader.getBizLocation().getProvince());
                            OrderAddressTwo.setCity(bizOrderHeader.getBizLocation().getCity());
                            OrderAddressTwo.setRegion(bizOrderHeader.getBizLocation().getRegion());
                        }
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getAddress()!=null){
                            OrderAddressTwo.setAddress(bizOrderHeader.getBizLocation().getAddress());
                        }else{
                            OrderAddressTwo.setAddress("");
                        }
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getReceiver()!=null){
                            OrderAddressTwo.setReceiver(bizOrderHeader.getBizLocation().getReceiver());
                        }else{
                            OrderAddressTwo.setReceiver("");
                        }
                        if(bizOrderHeader.getBizLocation()!=null && bizOrderHeader.getBizLocation().getPhone()!=null){
                            OrderAddressTwo.setPhone(bizOrderHeader.getBizLocation().getPhone());
                        }else{
                            OrderAddressTwo.setPhone("");
                        }
                        OrderAddressTwo.setType(2);
                        bizOrderAddressService.save(OrderAddressTwo);
                        commis = "ok";
                    }
                }
            }
        } catch (Exception e) {
            commis = "comError";
            e.printStackTrace();
        }
        return commis;
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveBizOrderHeader")
    public String saveBizOrderHeader(BizOrderHeader bizOrderHeader, Integer orderId, Double money) {
        String flag = "";
        try {
            BizOrderHeader orderHeader = bizOrderHeaderService.get(orderId);
            orderHeader.setTotalExp(money);
            bizOrderHeaderService.saveOrderHeader(orderHeader);
            flag = "ok";

        } catch (Exception e) {
            flag = "error";
            logger.error(e.getMessage());
        }
        return flag;
    }

    /**
     * 导出订单数据
     *
     * @return ouyang
     * 2018-03-27
     */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "orderHeaderExport", method = RequestMethod.POST)
    public String orderHeaderExportFile(BizOrderHeader bizOrderHeader,String cendExportbs, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String a="cend_listPage";
        try {
            DecimalFormat df = new DecimalFormat();
            BizOrderDetail orderDetail = new BizOrderDetail();
            BizPayRecord bizPayRecord = new BizPayRecord();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileName = "订单数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<BizOrderHeader> pageList =null;
            if(cendExportbs!=null && cendExportbs.equals(a)) {
                //C端导出
                bizOrderHeader.setDataStatus("filter");
                Page<BizOrderHeader> bizOrderHeaderPage = bizOrderHeaderService.cendfindPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
                pageList = bizOrderHeaderPage.getList();
            }else{
                pageList = bizOrderHeaderService.findList(bizOrderHeader);
            }
            //1订单
            List<List<String>> data = new ArrayList<List<String>>();
            //2商品
            List<List<String>> detailData = new ArrayList<List<String>>();
            //3交易记录
            List<List<String>> payData = new ArrayList<List<String>>();
            for (BizOrderHeader o : pageList) {
                bizPayRecord.setOrderNum(o.getOrderNum());
                List<BizPayRecord> payList = bizPayRecordService.findList(bizPayRecord);
                if (payList == null || payList.size()==0){
                    orderDetail.setOrderHeader(o);
                    List<BizOrderDetail> list = bizOrderDetailService.findList(orderDetail);
                    Double dou=0.0;
                    if (list.size() != 0) {
                        for (BizOrderDetail d : list) {
                            d.setOrderHeader(o);
                            o.setOrderDetailList(list);
                            dou+=d.getBuyPrice()*d.getOrdQty();
                            List<String> detailListData = new ArrayList();
                            //订单编号
                            detailListData.add(String.valueOf(d.getOrderHeader().getOrderNum()));
                            //商品名称
                            detailListData.add(String.valueOf(d.getSkuName()));
                            //商品编码
                            detailListData.add(String.valueOf(d.getPartNo()));
                            //供应商
                            if(d.getVendor()!=null && d.getVendor().getName()!=null){
                                detailListData.add(String.valueOf(d.getVendor().getName()));
                            }else{
                                detailListData.add("");
                            }
                            //商品单价
                            detailListData.add(String.valueOf(d.getUnitPrice()));
                            //商品工厂价
                            BizSkuInfo skuInfo = bizSkuInfoService.get(d.getSkuInfo().getId());
                            detailListData.add(String.valueOf(skuInfo.getBuyPrice()));
                            //采购数量
                            detailListData.add(String.valueOf(d.getOrdQty()));
                            //商品总价
                            detailListData.add(String.valueOf(df.format(d.getUnitPrice() * d.getOrdQty())));
                            detailData.add(detailListData);
                        }
                        o.setTotalBuyPrice(dou);
                    }
                    //地址查询
                    o.setBizLocation(bizOrderAddressService.get(o.getBizLocation().getId()));
                    List<String> rowData = new ArrayList();
                    //订单编号
                    rowData.add(String.valueOf(o.getOrderNum()));
                    //描述
                    Dict dict = new Dict();
                    dict.setDescription("订单类型");
                    dict.setType("biz_order_type");
                    List<Dict> dictList = dictService.findList(dict);
                    for (Dict di : dictList) {
                        if (di.getValue().equals(String.valueOf(o.getOrderType()))) {
                            //订单类型
                            rowData.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    //采购商名称/电话
                    rowData.add(String.valueOf(o.getCustomer().getName() + "(" + o.getCustomer().getPhone() + ")"));
                    //所属采购中心
                    rowData.add(String.valueOf(o.getCentersName()));
                    //商品总价
                    rowData.add(String.valueOf(df.format(o.getTotalDetail())));
                    //商品工厂总价
                    List<BizOrderHeader> orderHeaderList = new ArrayList<>();
                    orderHeaderList.add(o);
                    List<BizOrderHeader> oheaderList = bizOrderHeaderService.getTotalBuyPrice(orderHeaderList);
                    BizOrderHeader orderHeader = oheaderList.get(0);
                    rowData.add(String.valueOf(df.format(orderHeader.getTotalBuyPrice())));
                    //交易金额
                    rowData.add(String.valueOf(o.getTotalExp()));
                    //运费
                    rowData.add(String.valueOf(o.getFreight()));
                    //应付金额
                    rowData.add(String.valueOf(o.getTotalDetail() + o.getTotalExp() + o.getFreight()));
                    //已收货款
                    rowData.add(String.valueOf(o.getReceiveTotal()));
                    Integer ten = 10, forTy = 40;
                    if (!o.getBizStatus().equals(ten) && !o.getBizStatus().equals(forTy) && o.getTotalDetail() + o.getTotalExp() + o.getFreight() != o.getReceiveTotal()) {
                        //尾款信息
                        rowData.add("有尾款");
                    } else {
                        //尾款信息
                        rowData.add("");
                    }
                    //利润
                    Double buy=0.0;
                    if(o.getTotalBuyPrice()!=null){
                        buy=o.getTotalBuyPrice();
                    }else{
                        buy=0.0;
                    }
                    rowData.add(String.valueOf(df.format(o.getTotalDetail() + o.getTotalExp() + o.getFreight() - buy)));
                    Dict dictInv = new Dict();
                    dictInv.setDescription("发票状态");
                    dictInv.setType("biz_order_invStatus");
                    List<Dict> dictListInv = dictService.findList(dictInv);
                    for (Dict dinv : dictListInv) {
                        if (dinv.getValue().equals(String.valueOf(o.getInvStatus()))) {
                            //发票状态
                            rowData.add(String.valueOf(dinv.getLabel()));
                            break;
                        }
                    }
                    Dict dictBiz = new Dict();
                    dictBiz.setDescription("业务状态");
                    dictBiz.setType("biz_order_status");
                    List<Dict> dictListBiz = dictService.findList(dictBiz);
                    for (Dict dbiz : dictListBiz) {
                        if (dbiz.getValue().equals(String.valueOf(o.getBizStatus()))) {
                            //业务状态
                            rowData.add(String.valueOf(dbiz.getLabel()));
                            break;
                        }
                    }
                    data.add(rowData);
                }
                if (payList.size() != 0) {
                    o.setBizPayRecordList(payList);
                    orderDetail.setOrderHeader(o);
                    payList.forEach(p -> {
                        //======================================================================
                        List<BizOrderDetail> list = bizOrderDetailService.findList(orderDetail);
                        Double dou=0.0;
                        if (list.size() != 0) {
                            for (BizOrderDetail d : list) {
                                d.setOrderHeader(o);
                                o.setOrderDetailList(list);
                                dou+=d.getBuyPrice()*d.getOrdQty();
                                List<String> detailListData = new ArrayList();
                                //订单编号
                                detailListData.add(String.valueOf(d.getOrderHeader().getOrderNum()));
                                //商品名称
                                detailListData.add(String.valueOf(d.getSkuName()));
                                //商品编码
                                detailListData.add(String.valueOf(d.getPartNo()));
                                //供应商
                                if(d.getVendor()!=null && d.getVendor().getName()!=null){
                                    detailListData.add(String.valueOf(d.getVendor().getName()));
                                }else{
                                    detailListData.add("");
                                }
                                //商品单价
                                detailListData.add(String.valueOf(d.getUnitPrice()));
                                //商品工厂价
                                BizSkuInfo skuInfo = bizSkuInfoService.get(d.getSkuInfo().getId());
                                detailListData.add(String.valueOf(skuInfo.getBuyPrice()));
                                //采购数量
                                detailListData.add(String.valueOf(d.getOrdQty()));
                                //商品总价
                                detailListData.add(String.valueOf(df.format(d.getUnitPrice() * d.getOrdQty())));
                                detailData.add(detailListData);
                            }
                            o.setTotalBuyPrice(dou);
                        }
                        //地址查询
                        o.setBizLocation(bizOrderAddressService.get(o.getBizLocation().getId()));
                        List<String> rowData = new ArrayList();
                        //订单编号
                        rowData.add(String.valueOf(o.getOrderNum()));
                        //描述
                        Dict dict = new Dict();
                        dict.setDescription("订单类型");
                        dict.setType("biz_order_type");
                        List<Dict> dictList = dictService.findList(dict);
                        for (Dict di : dictList) {
                            if (di.getValue().equals(String.valueOf(o.getOrderType()))) {
                                //订单类型
                                rowData.add(String.valueOf(di.getLabel()));
                                break;
                            }
                        }
                        //采购商名称/电话
                        rowData.add(String.valueOf(o.getCustomer().getName() + "(" + o.getCustomer().getPhone() + ")"));
                        //所属采购中心
                        rowData.add(String.valueOf(o.getCentersName()));
                        //商品总价
                        rowData.add(String.valueOf(df.format(o.getTotalDetail())));
                        //商品工厂总价
                        List<BizOrderHeader> orderHeaderList = new ArrayList<>();
                        orderHeaderList.add(o);
                        List<BizOrderHeader> oheaderList = bizOrderHeaderService.getTotalBuyPrice(orderHeaderList);
                        BizOrderHeader orderHeader = oheaderList.get(0);
                        rowData.add(String.valueOf(df.format(orderHeader.getTotalBuyPrice())));
                        //交易金额
                        rowData.add(String.valueOf(o.getTotalExp()));
                        //运费
                        rowData.add(String.valueOf(o.getFreight()));
                        //应付金额
                        rowData.add(String.valueOf(o.getTotalDetail() + o.getTotalExp() + o.getFreight()));
                        //已收货款
                        rowData.add(String.valueOf(o.getReceiveTotal()));
                        Integer ten = 10, forTy = 40;
                        if (!o.getBizStatus().equals(ten) && !o.getBizStatus().equals(forTy) && o.getTotalDetail() + o.getTotalExp() + o.getFreight() != o.getReceiveTotal()) {
                            //尾款信息
                            rowData.add("有尾款");
                        } else {
                            //尾款信息
                            rowData.add("");
                        }
                        //利润
                        Double buy=0.0;
                        if(o.getTotalBuyPrice()!=null){
                            buy=o.getTotalBuyPrice();
                        }else{
                            buy=0.0;
                        }
                        rowData.add(String.valueOf(df.format(o.getTotalDetail() + o.getTotalExp() + o.getFreight() - buy)));
                        Dict dictInv = new Dict();
                        dictInv.setDescription("发票状态");
                        dictInv.setType("biz_order_invStatus");
                        List<Dict> dictListInv = dictService.findList(dictInv);
                        for (Dict dinv : dictListInv) {
                            if (dinv.getValue().equals(String.valueOf(o.getInvStatus()))) {
                                //发票状态
                                rowData.add(String.valueOf(dinv.getLabel()));
                                break;
                            }
                        }
                        Dict dictBiz = new Dict();
                        dictBiz.setDescription("业务状态");
                        dictBiz.setType("biz_order_status");
                        List<Dict> dictListBiz = dictService.findList(dictBiz);
                        for (Dict dbiz : dictListBiz) {
                            if (dbiz.getValue().equals(String.valueOf(o.getBizStatus()))) {
                                //业务状态
                                rowData.add(String.valueOf(dbiz.getLabel()));
                                break;
                            }
                        }
                        //支付类型名称
                        rowData.add(String.valueOf(p.getPayTypeName()));
                        //业务流水号
                        rowData.add(String.valueOf(p.getOutTradeNo()));
                        //支付金额
                        rowData.add(String.valueOf(p.getPayMoney()));
                        //交易时间
                        rowData.add(String.valueOf(sdf.format(p.getCreateDate())));
                        //                        payData.add(payRow);
                        data.add(rowData);
                    });
                }
            }
            String[] headers = {"订单编号", "订单类型", "采购商名称/电话", "所属采购中心", "商品总价","商品工厂总价", "调整金额", "运费",
                    "应付金额", "已收货款", "尾款信息", "利润", "发票状态", "业务状态","支付类型名称","业务流水号","支付金额","交易时间"};
            String[] details = {"订单编号", "商品名称", "商品编码", "供应商", "商品单价","商品工厂价", "采购数量", "商品总价"};
            OrderHeaderExportExcelUtils eeu = new OrderHeaderExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            eeu.exportExcel(workbook, 0, "订单数据", headers, data, fileName);
            eeu.exportExcel(workbook, 1, "商品数据", details, detailData, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        } catch (Exception e) {
           e.printStackTrace();
            addMessage(redirectAttributes, "导出订单数据失败！失败信息：" + e.getMessage());
        }
        if(cendExportbs!=null && cendExportbs.equals(a)){
            //跳转C端列表
            return "redirect:" + adminPath + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + adminPath + "/biz/order/bizOrderHeader/";
    }

    /**
     * C端订单列表
     * */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "cendList")
    public String cendList(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizOrderHeader> page = bizOrderHeaderService.cendfindPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        model.addAttribute("page", page);
        return "modules/biz/order/bizOrderHeaderCendList";
    }

    /**
     * C端订单编辑
     * */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "cendform")
    public String cendform(BizOrderHeader bizOrderHeader, Model model, String orderNoEditable, String orderDetails) {
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = new Office();
            User user = systemService.getUser(bizOrderHeader.getCustomer().getId());
            office.setId(user.getId());
            office.setName(user.getName());
            bizOrderHeader.setCustomer(office);
        }
        BizOrderHeader bizOrderHeaderTwo = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (bizOrderHeader.getId() != null) {
            Double totalDetail = bizOrderHeaderTwo.getTotalDetail();
            Double totalExp = bizOrderHeaderTwo.getTotalExp();
            Double freight = bizOrderHeaderTwo.getFreight();
            Double orderHeaderTotal = totalDetail + totalExp + freight;
            //cendForm页面显示待支付总价
            bizOrderHeader.setTobePaid(orderHeaderTotal - bizOrderHeaderTwo.getReceiveTotal());
            //不可编辑标识符
            String a="editable";
            String b="details";
            if (orderNoEditable != null && orderNoEditable.equals(a)) {
                //待支付cendForm页面不能修改
                bizOrderHeaderTwo.setOrderNoEditable(a);
            }
            if (orderDetails != null && orderDetails.equals(b)) {
                //查看详情cendForm页面不能修改
                bizOrderHeaderTwo.setOrderDetails(b);
            }
            BizOrderAddress bizOrderAddress = new BizOrderAddress();
            bizOrderAddress.setId(bizOrderHeaderTwo.getBizLocation().getId());
            List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
            if(list.size()!=0){
                for (BizOrderAddress orderAddress : list) {
    //				    收货地址
                    if (orderAddress.getType() == 1) {
                        model.addAttribute("orderAddress", orderAddress);
                    }
                }
            }
        }
        model.addAttribute("entity", bizOrderHeader);
        return "modules/biz/order/bizOrderHeaderCendForm";
    }

    /**
     * C端订单保存
     * */
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "cendSave")
    public String cendSave(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizOrderHeader)) {
            return cendform(bizOrderHeader, model, null, null);
        }
        if (bizOrderHeader.getPlatformInfo() == null) {
            bizOrderHeader.getPlatformInfo().setId(6);
        }
        bizOrderHeaderService.CendorderSave(bizOrderHeader);
        addMessage(redirectAttributes, "保存订单信息成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
    }

}