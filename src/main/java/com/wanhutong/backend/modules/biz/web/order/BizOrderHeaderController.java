/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.*;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
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
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.SystemConfig;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Controller
 *
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeader")
public class BizOrderHeaderController extends BaseController {

    protected Logger LOGGER = LoggerFactory.getLogger(BizOrderHeaderController.class);


    private static final Integer IMGTYPE = 37;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private OfficeService officeService;
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
    @Autowired
    private BizOrderCommentService bizOrderCommentService;

    @ModelAttribute
    public BizOrderHeader get(@RequestParam(required = false) Integer id) {
        BizOrderHeader entity = null;
        if (id != null && id != 0) {
            entity = bizOrderHeaderService.get(id);
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(entity);
            List<BizOrderDetail> list = bizOrderDetailService.findList(bizOrderDetail);
            double totalPrice=0.0;
            for (BizOrderDetail orderDetail : list) {
                totalPrice+=orderDetail.getOrdQty()*orderDetail.getBuyPrice();
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
            entity.setTotalBuyPrice(totalPrice);
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
        if (bizOrderHeader.getSkuChickCount() != null) {
            //商品下单量标识
            bizOrderHeader.setSkuChickCount(bizOrderHeader.getSkuChickCount());
        }
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        model.addAttribute("page", page);
        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());

        return "modules/biz/order/bizOrderHeaderList";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "form")
    public String form(BizOrderHeader bizOrderHeader, Model model, String orderNoEditable, String orderDetails, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("orderType", bizOrderHeader.getOrderType());
        BizOrderComment bizOrderComment = new BizOrderComment();
        bizOrderComment.setOrder(bizOrderHeader);
        List<BizOrderComment> commentList = bizOrderCommentService.findList(bizOrderComment);
        model.addAttribute("commentList",commentList);
        List<BizOrderDetail> ordDetailList = Lists.newArrayList();
        Map<Integer, String> orderNumMap = new HashMap<Integer, String>();
        Map<Integer, Integer> detailIdMap = new HashMap<Integer, Integer>();
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null) {
                bizOrderHeader.setCustomer(office);
                model.addAttribute("entity2", bizOrderHeader);
            }
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
            BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            if (bizCustomCenterConsultant != null && bizCustomCenterConsultant.getConsultants() != null &&
                    bizCustomCenterConsultant.getConsultants().getName() != null) {
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
            List<BizOrderAddress> addresslist = bizOrderAddressService.findList(orderAddress);
            if (CollectionUtils.isNotEmpty(addresslist)) {
                for (BizOrderAddress address : addresslist) {
                    //				交货地址
                    if (address.getType() == 2) {
                        model.addAttribute("address", address);
                    }
                }
            }
            //代采
            if (bizOrderHeaderTwo != null) {
                if (bizOrderHeaderTwo.getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                    //经销店
                    Office office = officeService.get(bizOrderHeader.getCustomer().getId());
                    if (office != null && office.getPrimaryPerson() != null && office.getPrimaryPerson().getId() != null) {
                        User user = systemService.getUser(office.getPrimaryPerson().getId());
                        model.addAttribute("custUser", user);
                    }
                    //供应商
                    User vendUser = bizOrderHeaderService.findVendUser(bizOrderHeader.getId(), OfficeTypeEnum.VENDOR.getType());
                    model.addAttribute("vendUser", vendUser);
                    BizOrderAppointedTime bizOrderAppointedTime = new BizOrderAppointedTime();
                    bizOrderAppointedTime.setOrderHeader(bizOrderHeader);
                    List<BizOrderAppointedTime> appointedTimeList = bizOrderAppointedTimeService.findList(bizOrderAppointedTime);
                    if (appointedTimeList != null && !appointedTimeList.isEmpty()) {
                        model.addAttribute("appointedTimeList", appointedTimeList);
                    }
                }
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);
            for (BizOrderDetail orderDetail : orderDetailList) {
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                if (bizSkuInfo != null) {
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfo);
                    if (skuInfo != null) {
                        orderDetail.setSkuInfo(skuInfo);
                    }
                }
                ordDetailList.add(orderDetail);
                int keyId = orderDetail.getLineNo();
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getOrderNum() != null) {
                    String orderNum = orderDetail.getPoHeader().getOrderNum();
                    orderNumMap.put(keyId, orderNum);
                }
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getId() != null) {
                    int detailId = orderDetail.getPoHeader().getId();
                    detailIdMap.put(keyId, detailId);
                }
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
            if (CollectionUtils.isNotEmpty(unlineList)) {
                model.addAttribute("unlineList", unlineList);
            }
        }

        BizOrderStatus bizOrderStatus = new BizOrderStatus();
        bizOrderStatus.setOrderHeader(bizOrderHeader);
        bizOrderStatus.setOrderType(BizOrderStatus.OrderType.ORDER.getType());
        List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
        statusList.sort(Comparator.comparing(BizOrderStatus::getCreateDate).thenComparing(BizOrderStatus::getId));

        Map<Integer, OrderHeaderBizStatusEnum> statusMap = OrderHeaderBizStatusEnum.getStatusMap();

        String statuPath = request.getParameter("statu");
        model.addAttribute("statuPath", statuPath);

        String refundSkip = request.getParameter("refundSkip");
        model.addAttribute("refundSkip", refundSkip);

        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        String drawbackStatusStr = request.getParameter("drawbackStatus");
        if (StringUtils.isNotBlank(drawbackStatusStr)) {
            Integer drawbackStatus = Integer.valueOf(drawbackStatusStr);
            BizDrawBack bizDrawBack = new BizDrawBack();
            bizDrawBack.setDrawbackStatus(drawbackStatus);
            bizOrderHeader.setDrawBack(bizDrawBack);
        }
        model.addAttribute("entity", bizOrderHeader);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("orderNumMap", orderNumMap);
        model.addAttribute("detailIdMap", detailIdMap);
        model.addAttribute("statusMap", statusMap);

        //图片处理
        CommonImg commonImg = new CommonImg();
        commonImg.setImgType(ImgEnum.UNlINE_REFUND_VOUCHER.getCode());
        commonImg.setObjectId(bizOrderHeader.getId());
        commonImg.setObjectName("biz_order_header");
        if (bizOrderHeader.getId() != null) {
            List<CommonImg> imgList = commonImgService.findList(commonImg);
            String photos = "";
            Map<String, Integer> photosMap = new LinkedHashMap<>();

            for (CommonImg img : imgList) {
                photos += img.getImgServer().concat(img.getImgPath()).concat("|");
                photosMap.put(img.getImgServer() + img.getImgPath(), img.getImgSort());
            }
            if (StringUtils.isNotBlank(photos)) {
                bizOrderHeader.setPhotos(photos);
            }
            if (imgList != null && !imgList.isEmpty()) {
                model.addAttribute("photosMap", photosMap);
            }
        }

        return "modules/biz/order/bizOrderHeaderForm";
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "refundReject")
    public boolean refundReject(BizOrderHeader bizOrderHeader, String checkStatus, Integer id) {
        String str = checkStatus;
        Integer idt = id;
        bizOrderHeader = bizOrderHeaderService.get(idt);
        BizDrawBack bizDrawBack = new BizDrawBack();
        bizDrawBack.setDrawbackStatus(Integer.parseInt(checkStatus));
        bizOrderHeader.setDrawBack(bizDrawBack);
        boolean boo = false;
        try {
            bizOrderHeaderService.updateDrawbackStatus(bizOrderHeader);
            boo = true;
        } catch (Exception e) {
            boo = false;
            logger.error(e.getMessage());
        }
        return boo;
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveDrawStatus")
    public boolean saveDrawStatus(BizOrderHeader bizOrderHeader, String checkStatus, Integer id) {
        bizOrderHeader = bizOrderHeaderService.get(id);
        BizDrawBack bizDrawBack = new BizDrawBack();
        bizDrawBack.setDrawbackStatus(Integer.parseInt(checkStatus));
        bizOrderHeader.setDrawBack(bizDrawBack);
        boolean boo = false;
        try {
            bizOrderHeaderService.updateDrawbackStatus(bizOrderHeader);
            boo = true;
        } catch (Exception e) {
            boo = false;
            logger.error(e.getMessage());
        }
        return boo;
    }


    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "save")
    public String save(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        if (!beanValidator(model, bizOrderHeader)) {
            return form(bizOrderHeader, model, null, null, request, response);
        }
        if (bizOrderHeader.getPlatformInfo() == null) {
            //后台默认保存为 系统后台订单
            bizOrderHeader.getPlatformInfo().setId(6);
        }
        String statuPath = request.getParameter("statuPath");
        bizOrderHeaderService.save(bizOrderHeader);
        addMessage(redirectAttributes, "保存订单信息成功");
        if (bizOrderHeader.getClientModify() != null && "client_modify".equals(bizOrderHeader.getClientModify())) {
//			保存跳回客户专员
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=" + bizOrderHeader.getConsultantId();
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?statu=" + statuPath;
    }

    @RequiresPermissions("biz:order:bizOrderHeader:doRefund")
    @RequestMapping(value = "saveRefund")
    public String saveRefund(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        if (!beanValidator(model, bizOrderHeader)) {
            return form(bizOrderHeader, model, null, null, request, response);
        }
        Double receiveTotal = (-1) * (bizOrderHeaderService.get(bizOrderHeader.getId()).getReceiveTotal());
        bizOrderHeaderService.save(bizOrderHeader);
        String drawbackStatusStr = request.getParameter("drawbackStatus");
        if (StringUtils.isNotBlank(drawbackStatusStr)) {
            Integer drawbackStatus = Integer.valueOf(drawbackStatusStr);
            BizDrawBack bizDrawBack = new BizDrawBack();
            bizDrawBack.setDrawbackStatus(drawbackStatus);
            bizOrderHeader.setDrawBack(bizDrawBack);
        }
        bizOrderHeaderService.updateDrawbackStatus(bizOrderHeader);
        User user = UserUtils.getUser();
        BizPayRecord bizPayRecord = new BizPayRecord();
        // 支付编号 *同订单号*
        bizPayRecord.setPayNum(bizOrderHeader.getOrderNum());
        // 订单编号
        bizPayRecord.setOrderNum(bizOrderHeader.getOrderNum());
        // 支付人
        bizPayRecord.setPayer(user.getId());
        // 客户ID
        bizPayRecord.setCustomer(bizOrderHeader.getCustomer());
        // 支付到账户
        bizPayRecord.setToAccount("1");
        // 交易类型：充值、提现、支付
        bizPayRecord.setRecordType(TradeTypeEnum.REFUND_PAY_TYPE.getCode());
        bizPayRecord.setRecordTypeName(TradeTypeEnum.REFUND_PAY_TYPE.getTradeNoType());
        // 支付类型：wx(微信) alipay(支付宝)
        bizPayRecord.setPayType(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getCode());
        bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getMessage());
        bizPayRecord.setPayMoney(receiveTotal);
        bizPayRecord.setBizStatus(1);
        bizPayRecord.setCreateBy(user);
        bizPayRecord.setUpdateBy(user);
        bizPayRecordService.save(bizPayRecord);

        addMessage(redirectAttributes, "保存订单信息成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?statu=refund";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "delete")
    public String delete(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_DELETE);
        bizOrderHeaderService.delete(bizOrderHeader);
        addMessage(redirectAttributes, "删除订单信息成功");
        if (bizOrderHeader.getFlag() != null && "cendDelete".equals(bizOrderHeader.getFlag())) {
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/?repage&customer.id=" + bizOrderHeader.getCustomer().getId() + "&statu=" + bizOrderHeader.getStatu();
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "recovery")
    public String recovery(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_NORMAL);
        bizOrderHeaderService.delete(bizOrderHeader);
        addMessage(redirectAttributes, "恢复订单信息成功");
        if (bizOrderHeader.getFlag() != null && "cendRecover".equals(bizOrderHeader.getFlag())) {
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/?repage&customer.id=" + bizOrderHeader.getCustomer().getId() + "&statu=" + bizOrderHeader.getStatu();
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
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.APPROVE.getState());
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
        bizOrderHeader.setSendGoodsStatus(1);
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
                BizOrderHeader order = bizOrderHeaderService.get(bizOrderHeader);
                if (order != null) {
                    if (objJsp.equals(OrderHeaderBizStatusEnum.SUPPLYING.getState())) {
                        order.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                        bizOrderHeaderService.saveOrderHeader(order);
                        bizOrderStatusService.saveOrderStatus(order);
                        BizOrderAddress orderAddres = new BizOrderAddress();
                        orderAddres.setOrderHeaderID(order);
                        List<BizOrderAddress> list = bizOrderAddressService.findList(orderAddres);
                        for (BizOrderAddress bizOrderAddress : list) {
                            if (bizOrderAddress.getType() == 2) {
                                orderAddres.setId(bizOrderAddress.getId());
                                break;
                            }
                        }
                        orderAddres.setAppointedTime(bizOrderHeader.getBizLocation().getAppointedTime());
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getProvince() != null && bizOrderHeader.getBizLocation().getCity() != null
                                && bizOrderHeader.getBizLocation().getRegion() != null) {
                            orderAddres.setProvince(bizOrderHeader.getBizLocation().getProvince());
                            orderAddres.setCity(bizOrderHeader.getBizLocation().getCity());
                            orderAddres.setRegion(bizOrderHeader.getBizLocation().getRegion());
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getAddress() != null) {
                            orderAddres.setAddress(bizOrderHeader.getBizLocation().getAddress());
                        } else {
                            orderAddres.setAddress(StringUtils.EMPTY);
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getReceiver() != null) {
                            orderAddres.setReceiver(bizOrderHeader.getBizLocation().getReceiver());
                        } else {
                            orderAddres.setReceiver(StringUtils.EMPTY);
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getPhone() != null) {
                            orderAddres.setPhone(bizOrderHeader.getBizLocation().getPhone());
                        } else {
                            orderAddres.setPhone(StringUtils.EMPTY);
                        }
                        orderAddres.setType(2);
                        bizOrderAddressService.save(orderAddres);
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
                    } else if (objJsp.equals(OrderHeaderBizStatusEnum.UNAPPROVE.getState())) {
                        order.setBizStatus(OrderHeaderBizStatusEnum.UNAPPROVE.getState());
                        bizOrderHeaderService.saveOrderHeader(order);
                        bizOrderStatusService.saveOrderStatus(order);
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
        try {
            DecimalFormat df = new DecimalFormat();
            BizOrderDetail orderDetail = new BizOrderDetail();
            BizPayRecord bizPayRecord = new BizPayRecord();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileName = "订单数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<BizOrderHeader> pageList = null;
            if (cendExportbs != null && "cend_listPage".equals(cendExportbs)) {
                //C端导出
                bizOrderHeader.setDataStatus("filter");
                Page<BizOrderHeader> bizOrderHeaderPage = bizOrderHeaderService.cendfindPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
                pageList = bizOrderHeaderPage.getList();
            } else {
                pageList = bizOrderHeaderService.findListExport(bizOrderHeader);
            }
            List<List<String>> data = new ArrayList<List<String>>();
            List<List<String>> detailData = new ArrayList<List<String>>();
            for (BizOrderHeader order : pageList) {
                bizPayRecord.setOrderNum(order.getOrderNum());
                List<BizPayRecord> payList = bizPayRecordService.findList(bizPayRecord);
                order.setBizLocation(bizOrderAddressService.get(order.getBizLocation().getId()));
                List<BizOrderDetail> list = new ArrayList<>();
                if (!order.getOrderType().equals(BizOrderTypeEnum.PHOTO_ORDER.getState())) {
                    orderDetail.setOrderHeader(order);
                    list = bizOrderDetailService.findList(orderDetail);
                } else {
                    order.setTotalBuyPrice(order.getTotalDetail());
                }
                if (CollectionUtils.isEmpty(payList)) {
                    if (CollectionUtils.isNotEmpty(list)) {
                        Double dou = 0.0;
                        for (BizOrderDetail detail : list) {
                            double buy = 0.0;
                            int ord = 0;
                            if (detail.getBuyPrice() != null) {
                                buy = detail.getBuyPrice();
                            }
                            if (detail.getOrdQty() != null) {
                                ord = detail.getOrdQty();
                            }
                            dou += buy * ord;

                            List<String> detailListData = Lists.newArrayList();
                            detailListData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                            detailListData.add(detail.getSkuName() == null ? StringUtils.EMPTY : detail.getSkuName());
                            detailListData.add(detail.getPartNo() == null ? StringUtils.EMPTY : detail.getPartNo());
                            //供应商
                            if (detail.getVendor() != null && detail.getVendor().getName() != null) {
                                detailListData.add(detail.getVendor().getName());
                            } else {
                                detailListData.add(StringUtils.EMPTY);
                            }
                            detailListData.add(detail.getUnitPrice() == null ? StringUtils.EMPTY : String.valueOf(detail.getUnitPrice()));
                            detailListData.add(detail.getBuyPrice() == null ? StringUtils.EMPTY : String.valueOf(detail.getBuyPrice()));
                            detailListData.add(detail.getOrdQty() == null ? StringUtils.EMPTY : String.valueOf(detail.getOrdQty()));
                            //商品总价
                            double unitPrice = 0.0;
                            if (detail.getUnitPrice() != null) {
                                unitPrice = detail.getUnitPrice();
                            }
                            detailListData.add(String.valueOf(df.format(unitPrice * ord)));
                            detailData.add(detailListData);
                        }
                        order.setTotalBuyPrice(dou);
                    }

                    List<String> rowData = Lists.newArrayList();
                    //订单编号
                    rowData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                    //描述
                    Dict dict = new Dict();
                    dict.setDescription("订单类型");
                    dict.setType("biz_order_type");
                    List<Dict> dictList = dictService.findList(dict);
                    for (Dict di : dictList) {
                        if (di.getValue().equals(String.valueOf(order.getOrderType()))) {
                            //订单类型
                            rowData.add(di.getLabel());
                            break;
                        }
                    }
                    //经销店名称/电话
                    if (order.getCustomer() != null && order.getCustomer().getName() != null || order.getCustomer().getPhone() != null) {
                        rowData.add(order.getCustomer().getName() + "(" + order.getCustomer().getPhone() + ")");
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    //所属采购中心
                    rowData.add(order.getCentersName() == null ? StringUtils.EMPTY : order.getCentersName());
                    //所属客户专员
                    if (order.getCon() != null && order.getCon().getName() != null) {
                        rowData.add(order.getCon().getName());
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    if (order.getTotalDetail() != null) {
                        rowData.add(String.valueOf(df.format(order.getTotalDetail())));
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    //商品工厂总价
                    if (order.getTotalBuyPrice() != null) {
                        rowData.add(String.valueOf(df.format(order.getTotalBuyPrice())));
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    rowData.add(order.getTotalExp() == null ? StringUtils.EMPTY : String.valueOf(order.getTotalExp()));
                    rowData.add(order.getFreight() == null ? StringUtils.EMPTY : String.valueOf(order.getFreight()));
                    double total = 0.0;
                    double exp = 0.0;
                    double fre = 0.0;
                    if (order.getTotalDetail() != null) {
                        total = order.getTotalDetail();
                    }
                    if (order.getTotalExp() != null) {
                        exp = order.getTotalExp();
                    }
                    if (order.getFreight() != null) {
                        fre = order.getFreight();
                    }
                    rowData.add(String.valueOf(total + exp + fre));
                    rowData.add(order.getReceiveTotal() == null ? StringUtils.EMPTY : String.valueOf(order.getReceiveTotal()));
                    double sumTotal = total + exp + fre;
                    double receiveTotal = order.getReceiveTotal() == null ? 0.0 : order.getReceiveTotal();
                    if (!OrderHeaderBizStatusEnum.EXPORT_TAIL.contains(order.getBizStatus()) && sumTotal > receiveTotal) {
                        //尾款信息
                        rowData.add("有尾款");
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    //利润
                    Double buy = 0.0;
                    if (order.getTotalBuyPrice() != null) {
                        buy = order.getTotalBuyPrice();
                    }
                    rowData.add(BizOrderTypeEnum.PHOTO_ORDER.getState().equals(order.getOrderType()) ? "0.00" : String.valueOf(df.format(total + exp + fre - buy)));
                    Dict dictInv = new Dict();
                    dictInv.setDescription("发票状态");
                    dictInv.setType("biz_order_invStatus");
                    List<Dict> dictListInv = dictService.findList(dictInv);
                    for (Dict dinv : dictListInv) {
                        if (dinv.getValue().equals(String.valueOf(order.getInvStatus()))) {
                            //发票状态
                            rowData.add(dinv.getLabel());
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
                            rowData.add(dbiz.getLabel());
                            break;
                        }
                    }
                    //订单创建时间
                    rowData.add(sdf.format(order.getCreateDate()));
                    data.add(rowData);
                }
                if (CollectionUtils.isNotEmpty(payList)) {

                    for (BizPayRecord p : payList) {
                        Double douSum = 0.0;
                        if (CollectionUtils.isNotEmpty(list)) {
                            for (BizOrderDetail d : list) {
                                double buy = 0.0;
                                double ord = 0;
                                if (d.getBuyPrice() != null) {
                                    buy = d.getBuyPrice();
                                }
                                if (d.getOrdQty() != null) {
                                    ord = d.getOrdQty();
                                }
                                douSum += buy * ord;
                                List<String> detailListData = Lists.newArrayList();
                                detailListData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                                detailListData.add(d.getSkuName() == null ? StringUtils.EMPTY : d.getSkuName());
                                detailListData.add(d.getPartNo() == null ? StringUtils.EMPTY : d.getPartNo());
                                //供应商
                                if (d.getVendor() != null && d.getVendor().getName() != null) {
                                    detailListData.add(d.getVendor().getName());
                                } else {
                                    detailListData.add(StringUtils.EMPTY);
                                }
                                detailListData.add(d.getUnitPrice() == null ? StringUtils.EMPTY : String.valueOf(d.getUnitPrice()));
                                detailListData.add(d.getBuyPrice() == null ? StringUtils.EMPTY : String.valueOf(d.getBuyPrice()));
                                detailListData.add(d.getOrdQty() == null ? StringUtils.EMPTY : String.valueOf(d.getOrdQty()));
                                //商品总价
                                double unPri = 0.0;
                                int ordQty = 0;
                                if (d.getUnitPrice() != null) {
                                    unPri = d.getUnitPrice();
                                }
                                if (d.getOrdQty() != null) {
                                    ordQty = d.getOrdQty();
                                }
                                detailListData.add(String.valueOf(df.format(unPri * ordQty)));
                                detailData.add(detailListData);
                            }
                            order.setTotalBuyPrice(douSum);
                        }
                        //地址查询
                        List<String> rowData = Lists.newArrayList();
                        rowData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                        //描述
                        Dict dict = new Dict();
                        dict.setDescription("订单类型");
                        dict.setType("biz_order_type");
                        List<Dict> dictList = dictService.findList(dict);
                        for (Dict di : dictList) {
                            if (di.getValue().equals(String.valueOf(order.getOrderType()))) {
                                //订单类型
                                rowData.add(di.getLabel());
                                break;
                            }
                        }
                        if (order.getCustomer() != null && order.getCustomer().getName() != null) {
                            rowData.add(order.getCustomer().getName() + "(" + order.getCustomer().getPhone() + ")");
                        }
                        rowData.add(order.getCentersName() == null ? StringUtils.EMPTY : order.getCentersName());
                        //所属客户专员
                        if (order.getCon() != null && order.getCon().getName() != null) {
                            rowData.add(order.getCon().getName());
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        if (order.getTotalDetail() != null) {
                            rowData.add(String.valueOf(df.format(order.getTotalDetail())));
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        //商品工厂总价
                        if (order.getTotalBuyPrice() != null) {
                            rowData.add(String.valueOf(df.format(order.getTotalBuyPrice())));
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        rowData.add(order.getTotalExp() == null ? StringUtils.EMPTY : String.valueOf(order.getTotalExp()));
                        rowData.add(order.getFreight() == null ? StringUtils.EMPTY : String.valueOf(order.getFreight()));
                        //应付金额
                        double total = 0.0;
                        double exp = 0.0;
                        double Fre = 0.0;
                        if (order.getTotalDetail() != null) {
                            total = order.getTotalDetail();
                        }
                        if (order.getTotalExp() != null) {
                            exp = order.getTotalExp();
                        }
                        if (order.getFreight() != null) {
                            Fre = order.getFreight();
                        }
                        rowData.add(String.valueOf(total + exp + Fre));
                        //已收货款
                        rowData.add(String.valueOf(order.getReceiveTotal() == null ? StringUtils.EMPTY : order.getReceiveTotal()));
                        double sumTotal = total + exp + Fre;
                        double receiveTotal = order.getReceiveTotal() == null ? 0.0 : order.getReceiveTotal();
                        if (!OrderHeaderBizStatusEnum.EXPORT_TAIL.contains(order.getBizStatus()) && sumTotal > receiveTotal) {
                            rowData.add("有尾款");
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        //利润
                        Double buy = 0.0;
                        if (order.getTotalBuyPrice() != null) {
                            buy = order.getTotalBuyPrice();
                        }
                        rowData.add(BizOrderTypeEnum.PHOTO_ORDER.getState().equals(order.getOrderType()) ? "0.00" : String.valueOf(df.format(total + exp + Fre - buy)));
                        Dict dictInv = new Dict();
                        dictInv.setDescription("发票状态");
                        dictInv.setType("biz_order_invStatus");
                        List<Dict> dictListInv = dictService.findList(dictInv);
                        for (Dict dinv : dictListInv) {
                            if (dinv.getValue().equals(String.valueOf(order.getInvStatus()))) {
                                //发票状态
                                rowData.add(dinv.getLabel());
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
                                rowData.add(dbiz.getLabel());
                                break;
                            }
                        }
                        //订单创建时间
                        rowData.add(String.valueOf(sdf.format(order.getCreateDate())));
                        if (p.getBizStatus() != null && p.getBizStatus() == 1) {
                            //支付类型名称
                            rowData.add(p.getPayTypeName() == null ? StringUtils.EMPTY : p.getPayTypeName());
                            //支付编号
                            rowData.add(p.getPayNum() == null ? StringUtils.EMPTY : p.getPayNum());
                            rowData.add(p.getOutTradeNo() == null ? StringUtils.EMPTY : p.getOutTradeNo());
                            //支付账号
                            rowData.add(p.getAccount() == null ? StringUtils.EMPTY : p.getAccount());
                            //交易类型名称
                            rowData.add(p.getRecordTypeName() == null ? StringUtils.EMPTY : p.getRecordTypeName());
                            rowData.add(p.getPayMoney() == null ? StringUtils.EMPTY : String.valueOf(p.getPayMoney()));
                            //交易时间
                            rowData.add(String.valueOf(sdf.format(p.getCreateDate())));
                        }
                        data.add(rowData);
                    }
                }
            }
            String[] headers = {"订单编号", "订单类型", "经销店名称/电话", "所属采购中心", "所属客户专员", "商品总价", "商品工厂总价", "调整金额", "运费",
                    "应付金额", "已收货款", "尾款信息", "服务费", "发票状态", "业务状态", "创建时间", "支付类型名称", "支付编号", "业务流水号", "支付账号", "交易类型名称", "支付金额", "交易时间"};
            String[] details = {"订单编号", "商品名称", "商品编码", "供应商", "商品单价", "商品工厂价", "采购数量", "商品总价"};
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
        if ("cend_listPage".equals(cendExportbs)) {
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
        BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (bizOrderHeader.getId() != null) {
            Double totalDetail = orderHeader.getTotalDetail();
            Double totalExp = orderHeader.getTotalExp();
            Double freight = orderHeader.getFreight();
            Double orderHeaderTotal = totalDetail + totalExp + freight;
            //cendForm页面显示待支付总价
            bizOrderHeader.setTobePaid(orderHeaderTotal - orderHeader.getReceiveTotal());
            if (orderNoEditable != null && "editable".equals(orderNoEditable)) {
                orderHeader.setOrderNoEditable("editable");
            }
            if (orderDetails != null && "details".equals(orderDetails)) {
                orderHeader.setOrderDetails("details");
            }
            BizOrderAddress bizOrderAddress = new BizOrderAddress();
            bizOrderAddress.setId(orderHeader.getBizLocation().getId());
            List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
            if (CollectionUtils.isNotEmpty(list)) {
                for (BizOrderAddress orderAddress : list) {
                    //收货地址
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
        String flag = "ok".intern();
        BigDecimal totalDetail = BigDecimal.valueOf(bizOrderHeader.getTotalDetail() == null ? 0 : bizOrderHeader.getTotalDetail());
        BigDecimal freight = BigDecimal.valueOf(bizOrderHeader.getFreight() == null ? 0 : bizOrderHeader.getFreight());
        BigDecimal totalExp = BigDecimal.valueOf(bizOrderHeader.getTotalExp() == null ? 0 : -bizOrderHeader.getTotalExp());

        if (totalExp.compareTo(BigDecimal.ZERO) <= 0) {
            return flag;
        }

        if (bizOrderHeader.getId() == null) {
            return flag;
        }

        SystemConfig systemConfig = ConfigGeneral.SYSTEM_CONFIG.get();

        BigDecimal photoOrderRatio = systemConfig.getPhotoOrderRatio();


        boolean allActivityShlef = true;
        BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (orderHeader != null && BizOrderTypeEnum.PHOTO_ORDER.getState().equals(orderHeader.getOrderType())
                && totalExp.compareTo(totalDetail.multiply(photoOrderRatio)) > 0) {
            return "photoOrder";
        }

        BizOrderDetail orderDetail = new BizOrderDetail();
        orderDetail.setOrderHeader(orderHeader);
        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(orderDetail);
        BigDecimal totalBuyPrice = BigDecimal.ZERO;
        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                totalBuyPrice = totalBuyPrice.add(BigDecimal.valueOf(bizOrderDetail.getBuyPrice()).multiply(BigDecimal.valueOf(bizOrderDetail.getOrdQty())));
                if (!StringUtils.equals(String.valueOf(systemConfig.getActivityShelfId()), String.valueOf(bizOrderDetail.getShelfInfo().getOpShelfInfo().getId()))) {
                    allActivityShlef = false;
                }
            }
        }

        String activityDate = systemConfig.getActivityDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(activityDate);
        } catch (ParseException e) {
            LOGGER.error("order edit checkTotalExp error", e);
        }


        if (orderHeader == null || orderHeader.getCustomer() == null || orderHeader.getCustomer().getId() == null) {
            return flag;
        }

        BizOrderHeader header = new BizOrderHeader();
        header.setCustomer(orderHeader.getCustomer());

        User user = UserUtils.getUser();

        BigDecimal resultPrice = totalDetail.subtract(totalExp);

        if (parse != null && (System.currentTimeMillis() < parse.getTime()) && allActivityShlef) {
            if (resultPrice.compareTo(totalBuyPrice.multiply(BigDecimal.valueOf(0.8))) < 0) {
                return "orderLowest8";
            }
            return flag;
        }

        List<String> serviceChargeAudit = systemConfig.getServiceChargeAudit();
        List<String> orderLossAudit = systemConfig.getOrderLossAudit();
        List<String> orderLowestAudit = systemConfig.getOrderLowestAudit();

        boolean serviceChargeStatus = RoleUtils.hasRole(user, serviceChargeAudit);
        if (!serviceChargeStatus && totalExp.compareTo(totalDetail.subtract(totalBuyPrice).multiply(BigDecimal.valueOf(0.5))) > 0) {
            return "serviceCharge";
        }

        boolean orderLossStatus = RoleUtils.hasRole(user, orderLossAudit);
        if (!orderLossStatus && resultPrice.compareTo(totalBuyPrice) < 0) {
            return "orderLoss";
        }

        boolean orderLowestStatus = RoleUtils.hasRole(user, orderLowestAudit);
        if (!orderLowestStatus && resultPrice.compareTo(totalBuyPrice.multiply(BigDecimal.valueOf(0.95))) < 0) {
            return "orderLowest";
        }

        return flag;
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveInfo")
    public boolean saveInfo(BizOrderHeader bizOrderHeader, String checkStatus, Integer id) {
        bizOrderHeader = bizOrderHeaderService.get(id);
        bizOrderHeader.setBizStatus(Integer.parseInt(checkStatus));
        boolean boo = false;
        try {
            bizOrderHeaderService.save(bizOrderHeader);
            boo = true;
        } catch (Exception e) {
            boo = false;
            logger.error(e.getMessage());
        }
        return boo;
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "refund")
    public String refund(BizOrderHeader bizOrderHeader, Model model, String orderNoEditable, String orderDetails) {
        List<BizOrderDetail> ordDetailList = Lists.newArrayList();
        Map<Integer, String> orderNumMap = new HashMap<Integer, String>();
        Map<Integer, Integer> detailIdMap = new HashMap<Integer, Integer>();
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null) {
                bizOrderHeader.setCustomer(office);
                model.addAttribute("entity2", bizOrderHeader);
            }
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
            if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
                BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
                if (bizCustomCenterConsultant != null && bizCustomCenterConsultant.getConsultants() != null &&
                        bizCustomCenterConsultant.getConsultants().getName() != null) {
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
            List<BizOrderAddress> addresslist = bizOrderAddressService.findList(orderAddress);
            if (CollectionUtils.isNotEmpty(addresslist)) {
                for (BizOrderAddress address : addresslist) {
                    //				交货地址
                    if (address.getType() == 2) {
                        model.addAttribute("address", address);
                    }
                }
            }
            //代采
            if (bizOrderHeaderTwo != null) {
                if (bizOrderHeaderTwo.getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                    //经销店
                    Office office = officeService.get(bizOrderHeader.getCustomer().getId());
                    if (office != null && office.getPrimaryPerson() != null && office.getPrimaryPerson().getId() != null) {
                        User user = systemService.getUser(office.getPrimaryPerson().getId());
                        model.addAttribute("custUser", user);
                    }
                    //供应商
                    User vendUser = bizOrderHeaderService.findVendUser(bizOrderHeader.getId(), OfficeTypeEnum.VENDOR.getType());
                    model.addAttribute("vendUser", vendUser);
                    BizOrderAppointedTime bizOrderAppointedTime = new BizOrderAppointedTime();
                    bizOrderAppointedTime.setOrderHeader(bizOrderHeader);
                    List<BizOrderAppointedTime> appointedTimeList = bizOrderAppointedTimeService.findList(bizOrderAppointedTime);
                    if (appointedTimeList != null && !appointedTimeList.isEmpty()) {
                        model.addAttribute("appointedTimeList", appointedTimeList);
                    }
                }
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);
            for (BizOrderDetail orderDetail : orderDetailList) {
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                if (bizSkuInfo != null) {
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfo);
                    if (skuInfo != null) {
                        orderDetail.setSkuInfo(skuInfo);
                    }
                }
                ordDetailList.add(orderDetail);
                int keyId = orderDetail.getLineNo();
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getOrderNum() != null) {
                    String orderNum = orderDetail.getPoHeader().getOrderNum();
                    orderNumMap.put(keyId, orderNum);
                }
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getId() != null) {
                    int detailId = orderDetail.getPoHeader().getId();
                    detailIdMap.put(keyId, detailId);
                }
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
            if (CollectionUtils.isNotEmpty(unlineList)) {
                model.addAttribute("unlineList", unlineList);
            }
        }

        BizOrderStatus bizOrderStatus = new BizOrderStatus();
        bizOrderStatus.setOrderHeader(bizOrderHeader);
        bizOrderStatus.setOrderType(BizOrderStatus.OrderType.ORDER.getType());


        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        model.addAttribute("entity", bizOrderHeader);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("orderNumMap", orderNumMap);
        model.addAttribute("detailIdMap", detailIdMap);

        //图片处理
        CommonImg commonImg = new CommonImg();
        commonImg.setImgType(ImgEnum.UNlINE_REFUND_VOUCHER.getCode());
        commonImg.setObjectId(bizOrderHeader.getId());
        commonImg.setObjectName("biz_order_header");
        if (bizOrderHeader.getId() != null) {
            List<CommonImg> imgList = commonImgService.findList(commonImg);
            String photos = "";
            Map<String, Integer> photosMap = new LinkedHashMap<>();

            for (CommonImg img : imgList) {
                photos += img.getImgServer().concat(img.getImgPath()).concat("|");
                photosMap.put(img.getImgServer() + img.getImgPath(), img.getImgSort());
            }
            if (StringUtils.isNotBlank(photos)) {
                bizOrderHeader.setPhotos(photos);
            }
            if (imgList != null && !imgList.isEmpty()) {
                model.addAttribute("photosMap", photosMap);
            }
        }


        return "modules/biz/order/bizOrderHeaderRefund";
    }

    @RequestMapping(value = "saveColorImg")
    public void saveFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件名
        MultipartFile colorFile = multipartRequest.getFile("colorImg");
        if (colorFile != null) {
            String originalFilename = colorFile.getOriginalFilename();
            String fullName = UUID.randomUUID().toString().replaceAll("-", "").concat(originalFilename.substring(originalFilename.indexOf(".")));
            String msg = "";
            boolean ret = false;
            try {
                String result = AliOssClientUtil.uploadObject2OSS(colorFile.getInputStream(), fullName, colorFile.getSize(), AliOssClientUtil.getOssUploadPath());
                if (StringUtils.isNotBlank(result)) {
                    fullName = DsConfig.getImgServer().concat("/").concat(AliOssClientUtil.getOssUploadPath()).concat(fullName);
                    ret = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultMap.put("fullName", fullName);
            resultMap.put("ret", ret);
            resultMap.put("msg", msg);
        }

        List<MultipartFile> files = multipartRequest.getFiles("productImg");
        if (CollectionUtils.isNotEmpty(files)) {
            List<String> imgList = Lists.newArrayList();
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String fullName = UUID.randomUUID().toString().replaceAll("-", "").concat(originalFilename.substring(originalFilename.indexOf(".")));
                try {
                    String result = AliOssClientUtil.uploadObject2OSS(file.getInputStream(), fullName, file.getSize(), AliOssClientUtil.getOssUploadPath());
                    if (StringUtils.isNotBlank(result)) {
                        fullName = DsConfig.getImgServer().concat("/").concat(AliOssClientUtil.getOssUploadPath()).concat(fullName);
                        imgList.add(fullName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resultMap.put("imgList", imgList);
            resultMap.put("ret", CollectionUtils.isNotEmpty(imgList));
            resultMap.put("msg", "");
        }

        response.getWriter().write(JSONObject.fromObject(resultMap).toString());
    }

}