/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.order.*;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.order.*;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5:代采订单 6:拍照下单)Controller
 *
 * @author ZhangTengfei
 * @version 2018-06-14
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizPhotoOrderHeader")
public class BizPhotoOrderHeaderController extends BaseController {

    @Autowired
    private BizPhotoOrderHeaderService bizPhotoOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizOrderHeaderUnlineService bizOrderHeaderUnlineService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;
    @Autowired
    private BizOrderAppointedTimeService bizOrderAppointedTimeService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private BizOrderCommentService bizOrderCommentService;

    @ModelAttribute
    public BizOrderHeader get(@RequestParam(required = false) Integer id) {
        BizOrderHeader entity = null;
        if (id != null && id != 0) {
            entity = bizPhotoOrderHeaderService.get(id);
        }
        if (entity == null) {
            entity = new BizOrderHeader();
        }
        return entity;
    }


    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "form")
    public String form(BizOrderHeader bizOrderHeader, Model model) {
        model.addAttribute("orderType", bizOrderHeader.getOrderType());
        if (bizOrderHeader.getSource() != null) {
            model.addAttribute("source",bizOrderHeader.getSource());
        }
        BizOrderComment bizOrderComment = new BizOrderComment();
        bizOrderComment.setOrder(bizOrderHeader);
        List<BizOrderComment> commentList = bizOrderCommentService.findList(bizOrderComment);
        model.addAttribute("commentList",commentList);
        if (bizOrderHeader.getId() == null) {
            logger.info("该订单没有传入订单ID");
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list";
        }
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
        BizOrderHeader bizOrderHeaderTwo = bizPhotoOrderHeaderService.get(bizOrderHeader.getId());
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
            //经销店
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null && office.getPrimaryPerson() != null && office.getPrimaryPerson().getId() != null) {
                User user = systemService.getUser(office.getPrimaryPerson().getId());
                model.addAttribute("custUser", user);
            }
            //供应商
            User vendUser = bizPhotoOrderHeaderService.findVendUser(bizOrderHeader.getId(), OfficeTypeEnum.VENDOR.getType());
            model.addAttribute("vendUser", vendUser);
            BizOrderAppointedTime bizOrderAppointedTime = new BizOrderAppointedTime();
            bizOrderAppointedTime.setOrderHeader(bizOrderHeader);
            List<BizOrderAppointedTime> appointedTimeList = bizOrderAppointedTimeService.findList(bizOrderAppointedTime);
            if (appointedTimeList != null && !appointedTimeList.isEmpty()) {
                model.addAttribute("appointedTimeList", appointedTimeList);
            }
//        boolean flag = false;
//        User user = UserUtils.getUser();
//        if (user.getRoleList() != null) {
//            for (Role role : user.getRoleList()) {
//                if (RoleEnNameEnum.FINANCE.getState().equals(role.getEnname())) {
//                    flag = true;
//                    break;
//                }
//            }
//        }
        BizOrderHeaderUnline bizOrderHeaderUnline = new BizOrderHeaderUnline();
        bizOrderHeaderUnline.setOrderHeader(bizOrderHeader);
        List<BizOrderHeaderUnline> unlineList = bizOrderHeaderUnlineService.findList(bizOrderHeaderUnline);
        if (CollectionUtils.isNotEmpty(unlineList)) {
            model.addAttribute("unlineList", unlineList);
        }
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizOrderHeader.getId());
        commonImg.setObjectName(ImgEnum.ORDER_SKU_PHOTO.getTableName());
        commonImg.setImgType(ImgEnum.ORDER_SKU_PHOTO.getCode());
        List<CommonImg> commonImgList = commonImgService.findList(commonImg);
        BizOrderStatus bizOrderStatus = new BizOrderStatus();
        bizOrderStatus.setOrderHeader(bizOrderHeader);
        bizOrderStatus.setOrderType(BizOrderStatus.OrderType.ORDER.getType());
        List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
        statusList.sort(Comparator.comparing(BizOrderStatus::getCreateDate).thenComparing(BizOrderStatus::getId));

        Map<Integer, OrderHeaderBizStatusEnum> statusMap = OrderHeaderBizStatusEnum.getStatusMap();
        if (CollectionUtils.isNotEmpty(commonImgList)) {
            model.addAttribute("imgUrlList",commonImgList);
        }
        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        model.addAttribute("entity", bizOrderHeader);
        model.addAttribute("statusList", statusList);
        model.addAttribute("statusMap", statusMap);
        return "modules/biz/order/bizPhotoOrderHeaderForm";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "save")
    public String save(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizOrderHeader)) {
            return form(bizOrderHeader, model);
        }
        if (bizOrderHeader.getPlatformInfo() == null) {
            //后台默认保存为 系统后台订单
            bizOrderHeader.getPlatformInfo().setId(6);
        }
        bizPhotoOrderHeaderService.save(bizOrderHeader);
        addMessage(redirectAttributes, "保存订单信息成功");
        if (bizOrderHeader.getClientModify() != null && "client_modify".equals(bizOrderHeader.getClientModify())) {
//			保存跳回客户专员
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=" + bizOrderHeader.getConsultantId();
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?source=" + bizOrderHeader.getSource();
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
                BizOrderHeader order = bizPhotoOrderHeaderService.get(bizOrderHeader);
                if (order != null) {
                    if (objJsp.equals(OrderHeaderBizStatusEnum.SUPPLYING.getState())) {
                        order.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                        bizPhotoOrderHeaderService.saveOrderHeader(order);
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
                                    BizOrderHeader orderHeader = bizPhotoOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                                    BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(orderHeader.getCustomer().getId());
                                    bizOrderDetail.setSuplyis(officeService.get(bizCustomCenterConsultant.getCenters().getId()));

                                }
                                bizOrderDetailService.saveStatus(bizOrderDetail);
                            }
                        }
                        commis = "ok";
                    } else if (objJsp.equals(OrderHeaderBizStatusEnum.UNAPPROVE.getState())) {
                        order.setBizStatus(OrderHeaderBizStatusEnum.UNAPPROVE.getState());
                        bizPhotoOrderHeaderService.saveOrderHeader(order);
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
            BizOrderHeader orderHeader = bizPhotoOrderHeaderService.get(orderId);
            orderHeader.setTotalExp(money);
            bizPhotoOrderHeaderService.saveOrderHeader(orderHeader);
            flag = "ok";

        } catch (Exception e) {
            flag = "error";
            logger.error(e.getMessage());
        }
        return flag;
    }

    @ResponseBody
    @RequestMapping(value = "findOrder")
    public List<BizOrderHeader> findOrder(BizOrderHeader bizOrderHeader) {
        return bizPhotoOrderHeaderService.findDeliverGoodsOrderList(bizOrderHeader);
    }
}