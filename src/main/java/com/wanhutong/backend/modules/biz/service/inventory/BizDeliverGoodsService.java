/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import com.google.common.collect.ImmutableMap;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.inventory.BizDeliverGoodsDao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.inventory.*;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.order.BizPhotoOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.PhoneConfig;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 发货单Service
 * @author 张腾飞
 * @version 2018-06-15
 */
@Service
@Transactional(readOnly = true)
public class BizDeliverGoodsService extends CrudService<BizDeliverGoodsDao, BizInvoice> {

	@Autowired
	private BizSendGoodsRecordService bizSendGoodsRecordService;
	@Autowired
    private CommonImgService commonImgService;
	@Autowired
    private BizDetailInvoiceService bizDetailInvoiceService;
	@Autowired
    private OfficeService officeService;
	@Autowired
    private BizDeliverGoodsDao bizDeliverGoodsDao;
	@Autowired
    private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
    private BizPhotoOrderHeaderService bizPhotoOrderHeaderService;
	@Autowired
    private BizPoHeaderService bizPoHeaderService;
	@Autowired
    private BizOrderStatusService bizOrderStatusService;

    protected Logger log = LoggerFactory.getLogger(getClass());//日志

    @Override
	public BizInvoice get(Integer id) {
		return super.get(id);
	}
    @Override
	public List<BizInvoice> findList(BizInvoice bizInvoice) {
		return super.findList(bizInvoice);
	}
    @Override
	public Page<BizInvoice> findPage(Page<BizInvoice> page, BizInvoice bizInvoice) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
            return super.findPage(page, bizInvoice);
        }
	    return super.findPage(page, bizInvoice);
	}
	
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void save(BizInvoice bizInvoice) {
	    //修改发货单
	    if (bizInvoice.getId() != null){
            BizInvoice invoice = bizDeliverGoodsDao.get(bizInvoice.getId());
            invoice.setTrackingNumber(bizInvoice.getTrackingNumber());
            invoice.setLogistics(bizInvoice.getLogistics());
            invoice.setOperation(bizInvoice.getOperation());
            invoice.setFreight(bizInvoice.getFreight());
            invoice.setCarrier(bizInvoice.getCarrier());
            invoice.setSettlementStatus(bizInvoice.getSettlementStatus());
            invoice.setSendDate(bizInvoice.getSendDate());
            invoice.setRemarks(bizInvoice.getRemarks());
            bizDeliverGoodsDao.update(invoice);
            //保存图片
            saveCommonImg(bizInvoice);
            return;
        }
        // 取出当前用户所在机构，
        User user = UserUtils.getUser();
        Office company = officeService.get(user.getCompany().getId());
        //采购商或采购中心
        bizInvoice.setSendNumber("");
        super.save(bizInvoice);
        bizInvoice.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,company.getId(),0,bizInvoice.getId()));
        super.save(bizInvoice);
        //保存图片
        saveCommonImg(bizInvoice);
        //获取订单ID
        String orderHeaders = bizInvoice.getOrderHeaders();
        //货值
        Double valuePrice = 0.0;
        if(StringUtils.isNotBlank(orderHeaders)) {
            String[] orders = orderHeaders.split(",");
            for (int a = 0; a < orders.length; a++) {
                BizOrderHeader orderHeader = bizPhotoOrderHeaderService.get(Integer.parseInt(orders[a]));
                //加入中间表关联关系
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setOrderHeader(orderHeader);
                bizDetailInvoiceService.save(bizDetailInvoice);
                valuePrice += orderHeader.getTotalDetail();//累计货值
                //采购商
                Office office = officeService.get(orderHeader.getCustomer().getId());
                //供应商或供货部发货
                if (bizInvoice.getBizStatus() == 1) {
                    BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                    bizPoOrderReq.setOrderHeader(orderHeader);
                    bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                    List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (CollectionUtils.isNotEmpty(bizPoOrderReqList)) {
                        BizPoHeader poHeader = bizPoOrderReqList.get(0).getPoHeader();
                        poHeader.setBizStatus(PoHeaderStatusEnum.COMPLETE.getCode().byteValue());
                        bizPoHeaderService.saveStatus(poHeader);
                        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), poHeader.getId());
                    }
                    //修改订单状态为已发货（20）
                    orderHeader.setBizStatus(OrderHeaderBizStatusEnum.SEND.getState());
                    bizPhotoOrderHeaderService.saveOrderHeader(orderHeader);
                    //生成供货记录
                    try {
                        BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                        bsgr.setCustomer(office);
                        bsgr.setOrderNum(orderHeader.getOrderNum());
                        bsgr.setBizOrderHeader(orderHeader);
                        bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                        bsgr.setSendDate(bizInvoice.getSendDate());
                        bizSendGoodsRecordService.save(bsgr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("[Exception]拍照下单生成供货记录异常[orderNum:{}]",orderHeader.getOrderNum(),e);
                        EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.SEND_GOODS_RECORD_EXCEPTION.name());
                        AliyunMailClient.getInstance().sendTxt("zhangtengfei_cn@163.com", "拍照下单生成供货记录异常",
                                String.format(orderHeader.getOrderNum(),
                                        orderHeader.getId(),
                                        orderHeader.getOrderNum(),
                                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                                ));
                        String exceptionName = e.toString();
                        exceptionName = exceptionName.substring(exceptionName.lastIndexOf(".") + 1, exceptionName.length());
                        PhoneConfig.Phone phone = PhoneConfig.getPhone(PhoneConfig.PhoneType.SEND_GOODS_RECORD_EXCEPTION.name());
                        AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.EXCEPTION_WARN.getCode(), phone == null ? "18515060437" : phone.getNumber(),
                                ImmutableMap.of("type", exceptionName, "service", "拍照下单供货记录"));
                    }
                }

                /*用于 订单状态表 保存状态*/
                if (orderHeader.getId() != null || orderHeader.getBizStatus() != null) {
                    BizOrderStatus orderStatus = new BizOrderStatus();
                    orderStatus.setOrderHeader(orderHeader);
                    orderStatus.setBizStatus(orderHeader.getBizStatus());
                    bizOrderStatusService.save(orderStatus);
                }
            }
            bizInvoice.setValuePrice(valuePrice);
            super.save(bizInvoice);
        }
	}

    /**
     * 保存物流信息图片
     * @param bizInvoice
     */
    @Transactional(readOnly = false)
    public void saveCommonImg(BizInvoice bizInvoice) {
        String imgUrl = null;
        try {
            imgUrl = URLDecoder.decode(bizInvoice.getImgUrl(), "utf-8");//主图转换编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("物流信息图转换编码异常." + e.getMessage(), e);
        }
        if (imgUrl != null) {
            String[] photoArr = imgUrl.split("\\|");
            saveLogisticsImg(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizInvoice, photoArr);
        }
        List<CommonImg> commonImgs = getImgList(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizInvoice.getId());
        for (int i = 0; i < commonImgs.size(); i++) {
            CommonImg commonImg = commonImgs.get(i);
            commonImg.setImgSort(i);
            commonImgService.save(commonImg);

            if (i == 0) {
                bizInvoice.setImgUrl(commonImg.getImgServer() + commonImg.getImgPath());
                bizDeliverGoodsDao.update(bizInvoice);
            }
        }
    }

    private List<CommonImg> getImgList(Integer imgType, Integer bizInvoiceId) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizInvoiceId);
        commonImg.setObjectName("biz_invoice");
        commonImg.setImgType(imgType);
        return commonImgService.findList(commonImg);
    }

    public void saveLogisticsImg(Integer imgType, BizInvoice bizInvoice, String[] photoArr) {
        if (bizInvoice.getId() == null) {
            log.error("Can't save logistics image without bizInvoice ID!");
            return;
        }

        List<CommonImg> commonImgs = getImgList(imgType, bizInvoice.getId());

        Set<String> existSet = new HashSet<>();
        for (CommonImg commonImg1 : commonImgs) {
            existSet.add(commonImg1.getImgServer() + commonImg1.getImgPath());
        }
        Set<String> newSet = new HashSet<>(Arrays.asList(photoArr));

        Set<String> result = new HashSet<String>();
        //差集，结果做删除操作
        result.clear();
        result.addAll(existSet);
        result.removeAll(newSet);
        for (String url : result) {
            for (CommonImg commonImg1 : commonImgs) {
                if (url.equals(commonImg1.getImgServer() + commonImg1.getImgPath())) {
                    commonImg1.setDelFlag("0");
                    commonImgService.delete(commonImg1);
                }
            }
        }
        //差集，结果做插入操作
        result.clear();
        result.addAll(newSet);
        result.removeAll(existSet);

        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizInvoice.getId());
        commonImg.setObjectName("biz_invoice");
        commonImg.setImgType(imgType);
        commonImg.setImgSort(40);
        for (String name : result) {
            if (name.trim().length() == 0 || name.contains(DsConfig.getImgServer())) {
                continue;
            }
            String pathFile = Global.getUserfilesBaseDir() + name;
            String ossPath = AliOssClientUtil.uploadFile(pathFile, true);

            commonImg.setId(null);
            commonImg.setImgPath("/"+ossPath);
            commonImg.setImgServer(DsConfig.getImgServer());
            commonImgService.save(commonImg);
        }
    }
	
	@Transactional(readOnly = false)
    @Override
	public void delete(BizInvoice bizInvoice) {
		super.delete(bizInvoice);
	}
	
}