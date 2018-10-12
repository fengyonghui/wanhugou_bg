/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderUnlineService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.PhoneConfig;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OutTradeNoTypeEnum;
import com.wanhutong.backend.modules.enums.TradeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 线下支付订单(线下独有)Controller
 *
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeaderUnline")
public class BizOrderHeaderUnlineController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizOrderHeaderUnlineController.class);


    private static final Byte BIZSTATUSONE = 1;
    private static final Byte BIZSTATUSTWO = 2;
    @Autowired
    private BizOrderHeaderUnlineService bizOrderHeaderUnlineService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizPayRecordService bizPayRecordService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;

    @ModelAttribute
    public BizOrderHeaderUnline get(@RequestParam(required = false) Integer id) {
        BizOrderHeaderUnline entity = null;
        if (id != null) {
            entity = bizOrderHeaderUnlineService.get(id);
        }
        if (entity == null) {
            entity = new BizOrderHeaderUnline();
        }
        return entity;
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizOrderHeaderUnline bizOrderHeaderUnline, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizOrderHeaderUnline> page = bizOrderHeaderUnlineService.findPage(new Page<BizOrderHeaderUnline>(request, response), bizOrderHeaderUnline);
        model.addAttribute("page", page);
        return "modules/biz/order/bizOrderHeaderUnlineList";
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
    @RequestMapping(value = "list4Mobile")
    @ResponseBody
    public String list4Mobile(BizOrderHeaderUnline bizOrderHeaderUnline, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        Page<BizOrderHeaderUnline> page = bizOrderHeaderUnlineService.findPage(new Page<BizOrderHeaderUnline>(request, response), bizOrderHeaderUnline);
        model.addAttribute("page", page);
        resultMap.put("page", page);
        return JsonUtil.generateData(resultMap, null);
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
    @RequestMapping(value = "form")
    public String form(BizOrderHeaderUnline bizOrderHeaderUnline, Model model) {
        if (bizOrderHeaderUnline.getId() != null) {
            CommonImg commonImg = new CommonImg();
            commonImg.setImgType(ImgEnum.UNlINE_PAYMENT_VOUCHER.getCode());
            commonImg.setObjectName(ImgEnum.UNlINE_PAYMENT_VOUCHER.getTableName());
            commonImg.setObjectId(bizOrderHeaderUnline.getId());
            List<CommonImg> commonImgList = commonImgService.findList(commonImg);
            if (commonImgList != null && !commonImgList.isEmpty()) {
                List<String> imgUrlList = new ArrayList<>();
                for (CommonImg comImg : commonImgList) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(comImg.getImgServer()).append(comImg.getImgPath());
                    imgUrlList.add(sb.toString());
                }
                model.addAttribute("imgUrlList", imgUrlList);
            }
        }
        model.addAttribute("bizOrderHeaderUnline", bizOrderHeaderUnline);
        return "modules/biz/order/bizOrderHeaderUnlineForm";
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
    @RequestMapping(value = "form4Mobile")
    @ResponseBody
    public String form4Mobile(BizOrderHeaderUnline bizOrderHeaderUnline, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (bizOrderHeaderUnline.getId() != null) {
            CommonImg commonImg = new CommonImg();
            commonImg.setImgType(ImgEnum.UNlINE_PAYMENT_VOUCHER.getCode());
            commonImg.setObjectName(ImgEnum.UNlINE_PAYMENT_VOUCHER.getTableName());
            commonImg.setObjectId(bizOrderHeaderUnline.getId());
            List<CommonImg> commonImgList = commonImgService.findList(commonImg);
            if (commonImgList != null && !commonImgList.isEmpty()) {
                List<String> imgUrlList = new ArrayList<>();
                for (CommonImg comImg : commonImgList) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(comImg.getImgServer()).append(comImg.getImgPath());
                    imgUrlList.add(sb.toString());
                }
                model.addAttribute("imgUrlList", imgUrlList);
                resultMap.put("imgUrlList", imgUrlList);
            }
        }
        model.addAttribute("bizOrderHeaderUnline", bizOrderHeaderUnline);
        resultMap.put("bizOrderHeaderUnline", bizOrderHeaderUnline);
        return JsonUtil.generateData(resultMap, null);
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:view")
    @RequestMapping(value = "offLineform")
    public String offLineform(BizOrderHeaderUnline bizOrderHeaderUnline, Model model) {
        BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeaderUnline.getOrderHeader().getId());
        bizOrderHeaderUnline.setOrderHeader(orderHeader);
        model.addAttribute("bizOrderHeaderUnline", bizOrderHeaderUnline);
        return "modules/biz/order/bizOrderHeaderOffLineForm";
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
    @RequestMapping(value = "save")
    public String save(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizOrderHeaderUnline)) {
            return form(bizOrderHeaderUnline, model);
        }
        bizOrderHeaderUnline = bizOrderHeaderUnlineService.get(bizOrderHeaderUnline.getId());
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderHeaderUnline.getOrderHeader().getId());
        bizOrderHeaderUnline.setRealMoney(bizOrderHeaderUnline.getUnlinePayMoney());
        bizOrderHeaderUnline.setBizStatus(BIZSTATUSONE);
        bizOrderHeaderUnlineService.save(bizOrderHeaderUnline);
        bizOrderHeader.setReceiveTotal(bizOrderHeader.getReceiveTotal() + bizOrderHeaderUnline.getRealMoney().doubleValue());
//        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
        if (bizOrderHeader.getBizStatus() == OrderHeaderBizStatusEnum.UNPAY.getState().intValue()) {
            if (bizOrderHeader.getTotalDetail().compareTo(bizOrderHeader.getReceiveTotal()) == 0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            } else {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.INITIAL_PAY.getState());
            }
        }
        if (bizOrderHeader.getBizStatus() == OrderHeaderBizStatusEnum.INITIAL_PAY.getState().intValue()) {
            if (bizOrderHeader.getTotalDetail().compareTo(bizOrderHeader.getReceiveTotal()) == 0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }
        }
        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
        bizOrderStatusService.saveOrderStatus(bizOrderHeader);

        try {
            User user = UserUtils.getUser();
            BizPayRecord bizPayRecord = new BizPayRecord();
            // 支付编号 *同订单号*
            bizPayRecord.setPayNum(GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE, user.getCompany().getId()));
            // 支付宝或微信的业务流水号
            bizPayRecord.setOutTradeNo(bizOrderHeaderUnline.getSerialNum());
            // 订单编号
            bizPayRecord.setOrderNum(bizOrderHeader.getOrderNum());
            // 支付人
            bizPayRecord.setPayer(bizOrderHeaderUnline.getCreateBy().getId());
            // 客户ID
            bizPayRecord.setCustomer(bizOrderHeader.getCustomer());
            // 支付账号
            bizPayRecord.setAccount(String.valueOf(bizOrderHeader.getCustomer().getId()));
            // 支付到账户
            bizPayRecord.setToAccount("1");
            // 交易类型：充值、提现、支付
            bizPayRecord.setRecordType(TradeTypeEnum.ORDER_PAY_TYPE.getCode());
            bizPayRecord.setRecordTypeName(TradeTypeEnum.ORDER_PAY_TYPE.getTradeNoType());
            // 支付类型：wx(微信) alipay(支付宝)
            bizPayRecord.setPayType(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getCode());
            bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getMessage());

            bizPayRecord.setPayMoney(bizOrderHeaderUnline.getUnlinePayMoney().doubleValue());
            bizPayRecord.setBizStatus(1);

            bizPayRecord.setCreateBy(user);
            bizPayRecord.setUpdateBy(user);
            bizPayRecordService.save(bizPayRecord);
        } catch (Exception e) {
            LOGGER.error("[exception]线下支付交易记录保存异常[orderHeaderId:{}][orderHeaderOfflineId{}]", bizOrderHeader.getId(), bizOrderHeaderUnline.getId(), e);
            EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.OFFLINE_PAY_RECORD_EXCEPTION.name());
            AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                    String.format(email.getBody(),
                            bizOrderHeader.getId(),
                            bizOrderHeaderUnline.getId(),
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                    ));

            String exceptionName = e.toString();
            exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1, exceptionName.length());
            PhoneConfig.Phone phone = PhoneConfig.getPhone(PhoneConfig.PhoneType.OFFLINE_PAY_RECORD_EXCEPTION.name());

            AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.EXCEPTION_WARN.getCode(), phone == null ? "18515060437" : phone.getNumber(),
                    ImmutableMap.of("type", exceptionName, "service", "线下支付记录"));
        }
        addMessage(redirectAttributes, "保存线下支付订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeaderUnline/?repage&orderHeader.id=" + bizOrderHeader.getId();
    }

    @RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
    @RequestMapping(value = "save4Mobile")
    @ResponseBody
    public String save4Mobile(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizOrderHeaderUnline)) {
            return form(bizOrderHeaderUnline, model);
        }
        bizOrderHeaderUnline = bizOrderHeaderUnlineService.get(bizOrderHeaderUnline.getId());
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderHeaderUnline.getOrderHeader().getId());
        bizOrderHeaderUnline.setRealMoney(bizOrderHeaderUnline.getUnlinePayMoney());
        bizOrderHeaderUnline.setBizStatus(BIZSTATUSONE);
        bizOrderHeaderUnlineService.save(bizOrderHeaderUnline);
        bizOrderHeader.setReceiveTotal(bizOrderHeader.getReceiveTotal() + bizOrderHeaderUnline.getRealMoney().doubleValue());
//        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
        if (bizOrderHeader.getBizStatus() == OrderHeaderBizStatusEnum.UNPAY.getState().intValue()) {
            if (bizOrderHeader.getTotalDetail().compareTo(bizOrderHeader.getReceiveTotal()) == 0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            } else {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.INITIAL_PAY.getState());
            }
        }
        if (bizOrderHeader.getBizStatus() == OrderHeaderBizStatusEnum.INITIAL_PAY.getState().intValue()) {
            if (bizOrderHeader.getTotalDetail().compareTo(bizOrderHeader.getReceiveTotal()) == 0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }
        }
        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
        bizOrderStatusService.saveOrderStatus(bizOrderHeader);

        try {
            User user = UserUtils.getUser();
            BizPayRecord bizPayRecord = new BizPayRecord();
            // 支付编号 *同订单号*
            bizPayRecord.setPayNum(GenerateOrderUtils.getTradeNum(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE, user.getCompany().getId()));
            // 支付宝或微信的业务流水号
            bizPayRecord.setOutTradeNo(bizOrderHeaderUnline.getSerialNum());
            // 订单编号
            bizPayRecord.setOrderNum(bizOrderHeader.getOrderNum());
            // 支付人
            bizPayRecord.setPayer(bizOrderHeaderUnline.getCreateBy().getId());
            // 客户ID
            bizPayRecord.setCustomer(bizOrderHeader.getCustomer());
            // 支付账号
            bizPayRecord.setAccount(String.valueOf(bizOrderHeader.getCustomer().getId()));
            // 支付到账户
            bizPayRecord.setToAccount("1");
            // 交易类型：充值、提现、支付
            bizPayRecord.setRecordType(TradeTypeEnum.ORDER_PAY_TYPE.getCode());
            bizPayRecord.setRecordTypeName(TradeTypeEnum.ORDER_PAY_TYPE.getTradeNoType());
            // 支付类型：wx(微信) alipay(支付宝)
            bizPayRecord.setPayType(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getCode());
            bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getMessage());

            bizPayRecord.setPayMoney(bizOrderHeaderUnline.getUnlinePayMoney().doubleValue());
            bizPayRecord.setBizStatus(1);

            bizPayRecord.setCreateBy(user);
            bizPayRecord.setUpdateBy(user);
            bizPayRecordService.save(bizPayRecord);
        } catch (Exception e) {
            LOGGER.error("[exception]线下支付交易记录保存异常[orderHeaderId:{}][orderHeaderOfflineId{}]", bizOrderHeader.getId(), bizOrderHeaderUnline.getId(), e);
            EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.OFFLINE_PAY_RECORD_EXCEPTION.name());
            AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                    String.format(email.getBody(),
                            bizOrderHeader.getId(),
                            bizOrderHeaderUnline.getId(),
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                    ));

            String exceptionName = e.toString();
            exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1, exceptionName.length());
            PhoneConfig.Phone phone = PhoneConfig.getPhone(PhoneConfig.PhoneType.OFFLINE_PAY_RECORD_EXCEPTION.name());

            AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.EXCEPTION_WARN.getCode(), phone == null ? "18515060437" : phone.getNumber(),
                    ImmutableMap.of("type", exceptionName, "service", "线下支付记录"));
        }
        addMessage(redirectAttributes, "保存线下支付订单成功");
        return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
    }

    @RequiresPermissions("biz:order:bizOrderHeaderOffLine:edit")
    @RequestMapping(value = "saveOffLine")
    public String saveOffLine(BizOrderHeaderUnline bizOrderHeaderUnline, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeaderUnlineService.saveOffLine(bizOrderHeaderUnline);
        addMessage(redirectAttributes, "保存线下支付订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeaderUnline/?repage&orderHeader.id=" + bizOrderHeaderUnline.getOrderHeader().getId();
    }
    @RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
    @RequestMapping(value = "delete")
    public String delete(BizOrderHeaderUnline bizOrderHeaderUnline, RedirectAttributes redirectAttributes) {
        bizOrderHeaderUnlineService.delete(bizOrderHeaderUnline);
        addMessage(redirectAttributes, "删除线下支付订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeaderUnline/?repage";
    }

    /**
     * 财务对线下支付订单的驳回
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
    @RequestMapping(value = "changeOrderReceive")
    public String changeOrderReceive(Integer id) {

        BizOrderHeaderUnline orderHeaderUnline = bizOrderHeaderUnlineService.get(id);
        orderHeaderUnline.setBizStatus(BIZSTATUSTWO);
        bizOrderHeaderUnlineService.saveOrderOffline(orderHeaderUnline);
        return "ok";
    }

    /**
     * 手机端财务对线下支付订单的驳回
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeaderUnline:edit")
    @RequestMapping(value = "changeOrderReceive4Mobile")
    public String changeOrderReceive4Mobile(Integer id) {
        BizOrderHeaderUnline orderHeaderUnline = bizOrderHeaderUnlineService.get(id);
        orderHeaderUnline.setBizStatus(BIZSTATUSTWO);
        bizOrderHeaderUnlineService.saveOrderOffline(orderHeaderUnline);
        return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
    }

}