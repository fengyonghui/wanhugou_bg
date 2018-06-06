/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.RoleUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.order.*;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.order.*;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.SystemConfig;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
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
import java.math.BigDecimal;
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

    private static final Integer IMGTYPE = 37;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private OfficeService officeService;
    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;

    @Autowired
    private BizPayRecordService bizPayRecordService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;
    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private DictService dictService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizOrderHeaderUnlineService bizOrderHeaderUnlineService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private DefaultPropService defaultPropService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;
    @Autowired
    private BizOrderAppointedTimeService bizOrderAppointedTimeService;

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
//                        if(orderDetail.getSentQty()>0){
//                            entity.setFlag("supply_commodity");
//                        }
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
        if(bizOrderHeader.getSkuChickCount()!=null){
            bizOrderHeader.setSkuChickCount(bizOrderHeader.getSkuChickCount());
        }
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        model.addAttribute("page", page);
        model.addAttribute("statu",bizOrderHeader.getStatu()==null?"":bizOrderHeader.getStatu());

        return "modules/biz/order/bizOrderHeaderList";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "form")
    public String form(BizOrderHeader bizOrderHeader, Model model, String orderNoEditable, String orderDetails) {
        model.addAttribute("orderType",bizOrderHeader.getOrderType());
        List<BizOrderDetail> ordDetailList = Lists.newArrayList();
        Map<Integer, String> orderNumMap = new HashMap<Integer, String>();
        Map<Integer, Integer> detailIdMap = new HashMap<Integer, Integer>();
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            bizOrderHeader.setCustomer(office);
            model.addAttribute("entity2", bizOrderHeader);
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
            if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
                BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
                if (bizCustomCenterConsultant != null && bizCustomCenterConsultant.getConsultants()!=null &&
                        bizCustomCenterConsultant.getConsultants().getName()!=null) {
                    bizCustomCenterConsultant.setConsultants(systemService.getUser(bizCustomCenterConsultant.getConsultants().getId()));
                    model.addAttribute("orderCenter", bizCustomCenterConsultant);
                } else {
                    model.addAttribute("orderCenter", new BizCustomCenterConsultant());
                }
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
            //代采
            if (bizOrderHeaderTwo.getOrderType()==Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                //经销店
                Office office = officeService.get(bizOrderHeader.getCustomer().getId());
                User user = systemService.getUser(office.getPrimaryPerson().getId());
                model.addAttribute("custUser",user);
                //供应商
                User vendUser = bizOrderHeaderService.findVendUser(bizOrderHeader.getId(), OfficeTypeEnum.VENDOR.getType());
                model.addAttribute("vendUser",vendUser);
                BizOrderAppointedTime bizOrderAppointedTime = new BizOrderAppointedTime();
                bizOrderAppointedTime.setOrderHeader(bizOrderHeader);
                List<BizOrderAppointedTime> appointedTimeList = bizOrderAppointedTimeService.findList(bizOrderAppointedTime);
                if (appointedTimeList != null && !appointedTimeList.isEmpty()) {
                    model.addAttribute("appointedTimeList",appointedTimeList);
                }
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);
            for (BizOrderDetail orderDetail : orderDetailList) {
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                orderDetail.setSkuInfo(skuInfo);
                ordDetailList.add(orderDetail);
                int keyId = orderDetail.getLineNo();
                String orderNum = orderDetail.getPoHeader().getOrderNum();
                orderNumMap.put(keyId, orderNum);
                int detailId = orderDetail.getPoHeader().getId();
                detailIdMap.put(keyId, detailId);
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
        if (bizOrderHeader.getId() != null) {
            BizOrderHeaderUnline bizOrderHeaderUnline = new BizOrderHeaderUnline();
            bizOrderHeaderUnline.setOrderHeader(bizOrderHeader);
            List<BizOrderHeaderUnline> unlineList = bizOrderHeaderUnlineService.findList(bizOrderHeaderUnline);
            if (unlineList != null && !unlineList.isEmpty()) {
                model.addAttribute("unlineList", unlineList);
            }
        }

        BizOrderStatus bizOrderStatus = new BizOrderStatus();
        bizOrderStatus.setOrderHeader(bizOrderHeader);
        bizOrderStatus.setOrderType(BizOrderStatus.OrderType.ORDER.getType());
        List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
        statusList.sort((o1,o2) -> o1.getCreateDate().compareTo(o2.getCreateDate()));

        Map<Integer, OrderHeaderBizStatusEnum> statusMap = OrderHeaderBizStatusEnum.getStatusMap();

        model.addAttribute("statu",bizOrderHeader.getStatu()==null?"":bizOrderHeader.getStatu());
        model.addAttribute("entity", bizOrderHeader);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("orderNumMap", orderNumMap);
        model.addAttribute("detailIdMap", detailIdMap);
        model.addAttribute("statusMap", statusMap);
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
        DefaultProp defaultProp =new DefaultProp();
         defaultProp.setPropKey("vend_center");
        List<DefaultProp> defaultProps= defaultPropService.findList(defaultProp);
        Integer vendCenterId=0;

        if(defaultProps!=null){
            vendCenterId= Integer.parseInt(defaultProps.get(0).getPropValue());
        }
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
                if (StringUtils.isNotBlank(flag)  && !"0".equals(flag)) {
                    bizPoOrderReq.setSoLineNo(orderDetail.getLineNo());
                    bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                    List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (poOrderReqList != null && poOrderReqList.size() > 0) {
                        BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                        orderDetail.setSkuInfo(skuInfo);
                        bizOrderDetailList.add(orderDetail);
                    }
                } else if(orderDetail.getSuplyis()!=null && orderDetail.getSuplyis().getId()!=0 && !orderDetail.getSuplyis().getId().equals(vendCenterId)) {
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

                        /*用于 订单状态表 insert状态*/
                        if(bh!=null && bh.getId()!=null || bh.getBizStatus()!=null){
                            BizOrderStatus orderStatus = new BizOrderStatus();
                            orderStatus.setOrderHeader(bh);
                            orderStatus.setBizStatus(bh.getBizStatus());
                            bizOrderStatusService.save(orderStatus);
                        }

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

                        /*用于 订单状态表 insert状态*/
                        if(bh!=null && bh.getId()!=null || bh.getBizStatus()!=null){
                            BizOrderStatus orderStatus = new BizOrderStatus();
                            orderStatus.setOrderHeader(bh);
                            orderStatus.setBizStatus(bh.getBizStatus());
                            bizOrderStatusService.save(orderStatus);
                        }

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
            List<List<String>> data = new ArrayList<List<String>>();
            List<List<String>> detailData = new ArrayList<List<String>>();
            for (BizOrderHeader order : pageList) {
                bizPayRecord.setOrderNum(order.getOrderNum());
                List<BizPayRecord> payList = bizPayRecordService.findList(bizPayRecord);
                order.setBizLocation(bizOrderAddressService.get(order.getBizLocation().getId()));
                orderDetail.setOrderHeader(order);
                List<BizOrderDetail> list = bizOrderDetailService.findList(orderDetail);
                if (payList == null || payList.size()==0){
                    if (list.size() != 0) {
                        Double dou=0.0;
                        for (BizOrderDetail detail : list) {
                            double buy=0.0;
                            int ord=0;
                            if(detail.getBuyPrice()!=null){
                                buy = detail.getBuyPrice();
                            }
                            if(detail.getOrdQty()!=null){
                                ord = detail.getOrdQty();
                            }
                            dou += buy * ord;

                            List<String> detailListData = Lists.newArrayList();
                            detailListData.add(String.valueOf(order.getOrderNum()==null?"":order.getOrderNum()));
                            detailListData.add(String.valueOf(detail.getSkuName()==null?"":detail.getSkuName()));
                            detailListData.add(String.valueOf(detail.getPartNo()==null?"":detail.getPartNo()));
                            //供应商
                            if(detail.getVendor()!=null && detail.getVendor().getName()!=null){
                                detailListData.add(String.valueOf(detail.getVendor().getName()));
                            }else{
                                detailListData.add("");
                            }
                            detailListData.add(String.valueOf(detail.getUnitPrice()==null?"":detail.getUnitPrice()));
                            detailListData.add(String.valueOf(detail.getBuyPrice()==null?"":detail.getBuyPrice()));
                            detailListData.add(String.valueOf(detail.getOrdQty()==null?"":detail.getOrdQty()));
                            //商品总价
                            double unitPrice=0.0;
                            if(detail.getUnitPrice()!=null){
                                unitPrice=detail.getUnitPrice();
                            }
                            detailListData.add(String.valueOf(df.format(unitPrice * ord)));
                            detailData.add(detailListData);
                        }
                        order.setTotalBuyPrice(dou);
                    }

                    List<String> rowData = Lists.newArrayList();
                    //订单编号
                    rowData.add(String.valueOf(order.getOrderNum()==null?"":order.getOrderNum()));
                    //描述
                    Dict dict = new Dict();
                    dict.setDescription("订单类型");
                    dict.setType("biz_order_type");
                    List<Dict> dictList = dictService.findList(dict);
                    for (Dict di : dictList) {
                        if (di.getValue().equals(String.valueOf(order.getOrderType()))) {
                            //订单类型
                            rowData.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    //经销店名称/电话
                    if(order.getCustomer()!=null && order.getCustomer().getName()!=null || order.getCustomer().getPhone()!=null){
                        rowData.add(String.valueOf(order.getCustomer().getName() + "(" + order.getCustomer().getPhone() + ")"));
                    }else{
                        rowData.add("");
                    }
                    rowData.add(String.valueOf(order.getCentersName()==null?"":order.getCentersName()));
                    if(order.getTotalDetail()!=null){
                        rowData.add(String.valueOf(df.format(order.getTotalDetail())));
                    }else{
                        rowData.add("");
                    }
                    //商品工厂总价
                    if(order.getTotalBuyPrice()!=null){
                        rowData.add(String.valueOf(df.format(order.getTotalBuyPrice())));
                    }else{
                        rowData.add("");
                    }
                    rowData.add(String.valueOf(order.getTotalExp()==null?"":order.getTotalExp()));
                    rowData.add(String.valueOf(order.getFreight()==null?"":order.getFreight()));
                    double total=0.0;
                    double exp=0.0;
                    double fre=0.0;
                    if(order.getTotalDetail()!=null){
                        total=order.getTotalDetail();
                    }
                    if(order.getTotalExp()!=null){
                        exp=order.getTotalExp();
                    }
                    if(order.getFreight()!=null){
                       fre =order.getFreight();
                    }
                    rowData.add(String.valueOf(total + exp + fre));
                    rowData.add(String.valueOf(order.getReceiveTotal()==null?"":order.getReceiveTotal()));
                    double sumTotal = total + exp + fre;
                    double receiveTotal = order.getReceiveTotal()==null?0.0:order.getReceiveTotal();
                    if (order.getBizStatus()!=10 && order.getBizStatus()!=40 && df.format(sumTotal)!=df.format(receiveTotal)) {
                        //尾款信息
                        rowData.add("有尾款");
                    } else {
                        //尾款信息
                        rowData.add("");
                    }
                    //利润
                    Double buy=0.0;
                    if(order.getTotalBuyPrice()!=null){
                        buy=order.getTotalBuyPrice();
                    }else{
                        buy=0.0;
                    }
                    rowData.add(String.valueOf(df.format(total + exp + fre - buy)));
                    Dict dictInv = new Dict();
                    dictInv.setDescription("发票状态");
                    dictInv.setType("biz_order_invStatus");
                    List<Dict> dictListInv = dictService.findList(dictInv);
                    for (Dict dinv : dictListInv) {
                        if (dinv.getValue().equals(String.valueOf(order.getInvStatus()))) {
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
                        if (dbiz.getValue().equals(String.valueOf(order.getBizStatus()))) {
                            //业务状态
                            rowData.add(String.valueOf(dbiz.getLabel()));
                            break;
                        }
                    }
                    //订单创建时间
                    rowData.add(sdf.format(order.getCreateDate()));
                    data.add(rowData);
                }
                if (payList.size() != 0) {
                    payList.forEach(p -> {
                        //======================================================================
                        Double douSum=0.0;
                        if (list.size() != 0) {
                            for (BizOrderDetail d : list) {
                                double buy=0.0;
                                double ord=0;
                                if(d.getBuyPrice()!=null){
                                    buy=d.getBuyPrice();
                                }
                                if(d.getOrdQty()!=null){
                                    ord=d.getOrdQty();
                                }
                                douSum+=buy * ord;
                                List<String> detailListData = Lists.newArrayList();
                                detailListData.add(String.valueOf(order.getOrderNum()==null?"":order.getOrderNum()));
                                detailListData.add(String.valueOf(d.getSkuName()==null?"":d.getSkuName()));
                                detailListData.add(String.valueOf(d.getPartNo()==null?"":d.getPartNo()));
                                //供应商
                                if(d.getVendor()!=null && d.getVendor().getName()!=null){
                                    detailListData.add(String.valueOf(d.getVendor().getName()));
                                }else{
                                    detailListData.add("");
                                }
                                detailListData.add(String.valueOf(d.getUnitPrice()==null?"":d.getUnitPrice()));
                                detailListData.add(String.valueOf(d.getBuyPrice()==null?"":d.getBuyPrice()));
                                detailListData.add(String.valueOf(d.getOrdQty()==null?"":d.getOrdQty()));
                                //商品总价
                                double unPri=0.0;
                                int ordQty=0;
                                if(d.getUnitPrice()!=null){
                                    unPri=d.getUnitPrice();
                                }
                                if(d.getOrdQty()!=null){
                                    ordQty=d.getOrdQty();
                                }
                                detailListData.add(String.valueOf(df.format(unPri * ordQty)));
                                detailData.add(detailListData);
                            }
                            order.setTotalBuyPrice(douSum);
                        }
                        //地址查询
                        List<String> rowData = Lists.newArrayList();
                        rowData.add(String.valueOf(order.getOrderNum()==null?"":order.getOrderNum()));
                        //描述
                        Dict dict = new Dict();
                        dict.setDescription("订单类型");
                        dict.setType("biz_order_type");
                        List<Dict> dictList = dictService.findList(dict);
                        for (Dict di : dictList) {
                            if (di.getValue().equals(String.valueOf(order.getOrderType()))) {
                                //订单类型
                                rowData.add(String.valueOf(di.getLabel()));
                                break;
                            }
                        }
                        if(order.getCustomer()!=null && order.getCustomer().getName()!=null){
                            rowData.add(String.valueOf(order.getCustomer().getName() + "(" + order.getCustomer().getPhone() + ")"));
                        }
                        rowData.add(String.valueOf(order.getCentersName()==null?"":order.getCentersName()));
                        if(order.getTotalDetail()!=null){
                            rowData.add(String.valueOf(df.format(order.getTotalDetail())));
                        }else{
                            rowData.add("");
                        }
                        //商品工厂总价
                        if(order.getTotalBuyPrice()!=null){
                            rowData.add(String.valueOf(df.format(order.getTotalBuyPrice())));
                        }else{
                            rowData.add("");
                        }
                        rowData.add(String.valueOf(order.getTotalExp()==null?"":order.getTotalExp()));
                        rowData.add(String.valueOf(order.getFreight()==null?"":order.getFreight()));
                        //应付金额
                        double total=0.0;
                        double exp = 0.0;
                        double Fre =0.0;
                        if(order.getTotalDetail()!=null){
                            total=order.getTotalDetail();
                        }
                        if(order.getTotalExp()!=null){
                            exp=order.getTotalExp();
                        }
                        if(order.getFreight()!=null){
                            Fre=order.getFreight();
                        }
                        rowData.add(String.valueOf(total + exp + Fre));
                        //已收货款
                        rowData.add(String.valueOf(order.getReceiveTotal()==null?"":order.getReceiveTotal()));
                        double sumTotal= total + exp + Fre;
                        double receiveTotal = order.getReceiveTotal()==null?0.0:order.getReceiveTotal();
                        if (order.getBizStatus()!=10 && order.getBizStatus()!=40 && df.format(sumTotal) != df.format(receiveTotal)) {
                            rowData.add("有尾款");
                        } else {
                            rowData.add("");
                        }
                        Double buy=0.0;
                        if(order.getTotalBuyPrice()!=null){
                            buy=order.getTotalBuyPrice();
                        }else{
                            buy=0.0;
                        }
                        rowData.add(String.valueOf(df.format(total + exp + Fre - buy)));
                        Dict dictInv = new Dict();
                        dictInv.setDescription("发票状态");
                        dictInv.setType("biz_order_invStatus");
                        List<Dict> dictListInv = dictService.findList(dictInv);
                        for (Dict dinv : dictListInv) {
                            if (dinv.getValue().equals(String.valueOf(order.getInvStatus()))) {
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
                            if (dbiz.getValue().equals(String.valueOf(order.getBizStatus()))) {
                                //业务状态
                                rowData.add(String.valueOf(dbiz.getLabel()));
                                break;
                            }
                        }
                        //订单创建时间
                        rowData.add(sdf.format(order.getCreateDate()));
                        //支付类型名称
                        rowData.add(String.valueOf(p.getPayTypeName()==null?"":p.getPayTypeName()));
                        rowData.add(String.valueOf(p.getOutTradeNo()==null?"":p.getOutTradeNo()));
                        rowData.add(String.valueOf(p.getPayMoney()==null?"":p.getPayMoney()));
                        //交易时间
                        rowData.add(String.valueOf(sdf.format(p.getCreateDate())));
                        data.add(rowData);
                    });
                }
            }
            String[] headers = {"订单编号", "订单类型", "经销店名称/电话", "所属采购中心", "商品总价","商品工厂总价", "调整金额", "运费",
                    "应付金额", "已收货款", "尾款信息", "服务费", "发票状态", "业务状态","创建时间","支付类型名称","业务流水号","支付金额","交易时间"};
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

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "checkTotalExp")
    public String checkTotalExp(BizOrderHeader bizOrderHeader) {
        String flag = "ok";
        BigDecimal totalDetail = BigDecimal.valueOf(bizOrderHeader.getTotalDetail() == null ? 0 : bizOrderHeader.getTotalDetail());
        BigDecimal freight = BigDecimal.valueOf(bizOrderHeader.getFreight() == null ? 0 : bizOrderHeader.getFreight());
        BigDecimal totalExp = BigDecimal.valueOf(bizOrderHeader.getTotalExp() == null ? 0 : -bizOrderHeader.getTotalExp());

        if (totalExp.compareTo(BigDecimal.ZERO) <= 0) {
            return flag;
        }

        if (bizOrderHeader.getId() == null) {
            return flag;
        }

        BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
        BizOrderDetail orderDetail = new BizOrderDetail();
        orderDetail.setOrderHeader(orderHeader);
        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(orderDetail);
        BigDecimal totalBuyPrice = BigDecimal.ZERO;
        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                totalBuyPrice = totalBuyPrice.add(BigDecimal.valueOf(bizOrderDetail.getBuyPrice()).multiply(BigDecimal.valueOf(bizOrderDetail.getOrdQty())));
            }
        }

        if (orderHeader == null || orderHeader.getCustomer() == null || orderHeader.getCustomer().getId() == null) {
            return flag;
        }

        BizOrderHeader header = new BizOrderHeader();
        header.setCustomer(orderHeader.getCustomer());

        User user = UserUtils.getUser();
        SystemConfig systemConfig = ConfigGeneral.SYSTEM_CONFIG.get();

        List<String> serviceChargeAudit = systemConfig.getServiceChargeAudit();
        List<String> orderLossAudit = systemConfig.getOrderLossAudit();
        List<String> orderLowestAudit = systemConfig.getOrderLowestAudit();

        boolean serviceChargeStatus = RoleUtils.hasRole(user, serviceChargeAudit);

        if (!serviceChargeStatus && totalExp.compareTo(totalDetail.subtract(totalBuyPrice).multiply(BigDecimal.valueOf(0.5))) > 0) {
            return "serviceCharge";
        }

        boolean orderLossStatus = RoleUtils.hasRole(user, orderLossAudit);
        BigDecimal resultPrice = totalDetail.subtract(totalExp);
        if (!orderLossStatus && resultPrice.compareTo(totalBuyPrice) < 0) {
            return "orderLoss";
        }

        boolean orderLowestStatus = RoleUtils.hasRole(user, orderLowestAudit);
        if (!orderLowestStatus && resultPrice.compareTo(totalBuyPrice.multiply(BigDecimal.valueOf(0.95))) < 0) {
            return "orderLowest";
        }

        return flag;
    }

}