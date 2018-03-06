/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.inventory.*;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInvoiceDao;

import javax.annotation.Resource;

/**
 * 发货单Service
 * @author 张腾飞
 * @version 2018-03-05
 */
@Service
@Transactional(readOnly = true)
public class BizInvoiceService extends CrudService<BizInvoiceDao, BizInvoice> {

	@Autowired
	private BizSendGoodsRecordService bizSendGoodsRecordService;
	@Autowired
    private CommonImgService commonImgService;
	@Autowired
    private BizDetailInvoiceService bizDetailInvoiceService;
	@Autowired
    private OfficeService officeService;
	@Autowired
    private BizSkuInfoService bizSkuInfoService;
	@Autowired
    private BizInvoiceDao bizInvoiceDao;
	@Autowired
    private BizPoDetailService bizPoDetailService;
	@Autowired
    private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
    private BizRequestDetailService bizRequestDetailService;
	@Autowired
    private BizRequestHeaderService bizRequestHeaderService;
	@Autowired
    private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
    private BizInventorySkuService bizInventorySkuService;
	@Autowired
    private BizOrderDetailService bizOrderDetailService;
	@Autowired
    private BizPoHeaderService bizPoHeaderService;

    protected Logger log = LoggerFactory.getLogger(getClass());//日志

	public BizInvoice get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInvoice> findList(BizInvoice bizInvoice) {
		return super.findList(bizInvoice);
	}
	
	public Page<BizInvoice> findPage(Page<BizInvoice> page, BizInvoice bizInvoice) {
		return super.findPage(page, bizInvoice);
	}
	
	@Transactional(readOnly = false)
	public void save(BizInvoice bizInvoice,String bizStatu) {
        boolean flagRequest = true;		//备货单完成状态
        boolean flagOrder = true;		//销售单完成状态
        boolean flagPo = true;     //采购单完成状态
        // 取出当前用户所在机构，
        User user = UserUtils.getUser();
        Office company = officeService.get(user.getCompany().getId());
        //采购商或采购中心
//        Office office = officeService.get(bizSendGoodsRecord.getCustomer().getId());
        Date date = new Date();
        bizInvoice.setSendDate(date);
        super.save(bizInvoice);
        bizInvoice.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,company.getId(),0,bizInvoice.getId()));
        super.save(bizInvoice);
        //保存图片
        saveCommonImg(bizInvoice);
        List<BizOrderHeader> orderHeaderList = bizInvoice.getOrderHeaderList();
        List<BizRequestHeader> requestHeaderList = bizInvoice.getRequestHeaderList();
        if (orderHeaderList != null && orderHeaderList.size() > 0){
            for(BizOrderHeader bizOrderHeader:orderHeaderList) {
                BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
                BizOrderDetail ordDetail = new BizOrderDetail();
                ordDetail.setOrderHeader(orderHeader);
                List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(ordDetail);
                //加入中间表关联关系
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setOrderHeader(bizOrderHeader);
                bizDetailInvoiceService.save(bizDetailInvoice);
                for (BizOrderDetail bizOrderDetail : orderDetailList) {
                        //商品
                        BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                        int sendNum = bizOrderDetail.getSendNum();    //供货数
                        //采购商
                        Office office = officeService.get(orderHeader.getCustomer().getId());
                        int sentQty = bizOrderDetail.getSentQty();    //销售单累计供货数量
                        //当供货数量和申报数量不相等时，更改销售单状态
                        if (bizOrderDetail.getOrdQty() != (sentQty + sendNum)) {
                            flagOrder = false;
                        }
                        if (sendNum == 0) {
                            continue;
                        }
                        //采购中心订单发货
                        if (bizStatu.equals("0")) {
                            //销售单状态改为同意供货（供货中）（15）
                            bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                            bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                            //获取库存数
                            BizInventorySku bizInventorySku = new BizInventorySku();
                            bizInventorySku.setSkuInfo(bizSkuInfo);
                            bizInventorySku.setInvInfo(bizOrderDetail.getInventoryInfo());
                            bizInventorySku.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
                            List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
                            int stock = 0;
                            //没有库存，
                            if (list == null || list.size() == 0 || list.get(0).getStockQty() == 0) {
                            /*bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
                            bizOrderHeaderService.saveOrderHeader(bizOrderHeader);*/
                                flagOrder = false;
                            } else {
                                //有库存
                                for (BizInventorySku invSku : list) {
                                    stock = invSku.getStockQty();
                                    //如果库存不够，
                                    if (stock < bizOrderDetail.getOrdQty()) {
                                    /*bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
                                    bizOrderHeaderService.saveOrderHeader(bizOrderHeader);*/
                                        flagOrder = false;
                                        if (sendNum > stock) {
                                            sendNum = stock;
                                        }
                                        invSku.setStockQty(stock - sendNum);
                                        bizInventorySkuService.save(invSku);
                                    } else {
                                        invSku.setStockQty(stock - sendNum);
                                        bizInventorySkuService.save(invSku);
                                    }
                                }
                                //累计已供数量
                                bizOrderDetail.setSentQty(sentQty + sendNum);
                                bizOrderDetailService.saveStatus(bizOrderDetail);
                                //生成供货记录表
                                BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                                bsgr.setSendNum(sendNum);
                                bsgr.setInvInfo(bizOrderDetail.getInventoryInfo());
                                bsgr.setCustomer(office);
                                bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
                                bsgr.setOrderNum(bizOrderHeader.getOrderNum());
                                bsgr.setBizOrderHeader(bizOrderHeader);
                                bsgr.setSkuInfo(bizSkuInfo);
                                bsgr.setSendDate(date);
                                bizSendGoodsRecordService.save(bsgr);
                            }
                        }
                        //供应商或供货部发货
                        if (bizStatu.equals("1")) {
                            //获取销售单相应的采购单详情,累计采购单单个商品的供货数
                            BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                            BizPoHeader poHeader = null;
                            BizPoDetail bizPoDetail = new BizPoDetail();
                            BizPoDetail poDetail = null;
                            BizOrderDetail bizOrdDetail = new BizOrderDetail();
                            bizOrdDetail.setSkuInfo(bizSkuInfo);
                            bizPoOrderReq.setOrderDetail(bizOrdDetail);
                            bizPoOrderReq.setOrderHeader(bizOrderHeader);
                            List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                            if (bizPoOrderReqList != null && bizPoOrderReqList.size() > 0) {
                                poHeader = bizPoOrderReqList.get(0).getPoHeader();
                            }
                            bizPoDetail.setPoHeader(poHeader);
                            bizPoDetail.setSkuInfo(bizSkuInfo);
                            List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
                            if (bizPoDetailList != null && bizPoDetailList.size() > 0) {
                                poDetail = bizPoDetailList.get(0);
                            }
                            if (poDetail.getSendQty() + sendNum != poDetail.getOrdQty()) {
                                flagPo = false;
                            }
                            //累计已供数量
                            bizOrderDetail.setSentQty(sentQty + sendNum);
                            bizOrderDetailService.saveStatus(bizOrderDetail);
                        }
                        //生成供货记录
                        BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                        bsgr.setSendNum(sendNum);
                        bsgr.setCustomer(office);
                        bsgr.setOrderNum(bizOrderHeader.getOrderNum());
                        bsgr.setBizOrderHeader(bizOrderHeader);
                        bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                        bsgr.setSkuInfo(bizSkuInfo);
                        bsgr.setSendDate(date);
                    }
                //销售单完成时，更该销售单状态为已供货（20）
                if (flagOrder) {
                    bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SEND.getState());
                    bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                    if (bizStatu.equals("0")){
                        BizOrderDetail bizOrderDetail = new BizOrderDetail();
                        bizOrderDetail.setOrderHeader(bizOrderHeader);
                        List<BizOrderDetail> ordDetailList = bizOrderDetailService.findList(bizOrderDetail);
                        for(BizOrderDetail orderDetail:ordDetailList){
                            BizSkuInfo skuInfo = orderDetail.getSkuInfo();
                            BizInventoryInfo inventoryInfo = orderDetail.getInventoryInfo();
                            BizInventorySku bizInventorySku = new BizInventorySku();
                            bizInventorySku.setSkuInfo(skuInfo);
                            bizInventorySku.setInvInfo(inventoryInfo);
                            List<BizInventorySku> invSkuList = bizInventorySkuService.findList(bizInventorySku);
                            if (invSkuList != null && invSkuList.size() > 0){
                                BizInventorySku inventorySku = invSkuList.get(0);
                                inventorySku.setStockOrdQty(inventorySku.getStockOrdQty()+1);
                            }
                        }
                    }
                }
                //当供货部或供应商发货时，才涉及采购单状态
                if(bizStatu.equals("1")) {
                    //更改采购单状态,已完成（5）
                    if (flagPo){
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        BizPoOrderReq por = null;
                        bizPoOrderReq.setOrderHeader(bizOrderHeader);
                        List<BizPoOrderReq> porList = bizPoOrderReqService.findList(bizPoOrderReq);
                        if (porList != null && porList.size() > 0 ){
                            por = porList.get(0);
                        }
                        BizPoHeader poHeader = por.getPoHeader();
                        //获取该采购单的所有采购详情，如果所有的供货数都和采购数相等，则更改采购单状态为完成
                        BizPoHeader bizPoHeader = bizPoHeaderService.get(poHeader.getId());
                        BizPoDetail poDetail = new BizPoDetail();
                        poDetail.setPoHeader(bizPoHeader);
                        List<BizPoDetail> poDetailList = bizPoDetailService.findList(poDetail);
                        boolean flag = true;
                        for (BizPoDetail bizPoDetail:poDetailList) {
                            if (bizPoDetail.getOrdQty() != bizPoDetail.getSendQty()){
                                flag = false;
                            }
                        }
                        if (flag) {
                            int status = PoHeaderStatusEnum.COMPLETE.getCode();
                            poHeader.setBizStatus((byte) status);
                            bizPoHeaderService.saveStatus(poHeader);
                        }
                    }
                }
            }
        }
        if(requestHeaderList != null && requestHeaderList.size() > 0) {
            for(BizRequestHeader bizRequestHeader:requestHeaderList) {
                BizRequestHeader requestHeader = bizRequestHeaderService.get(bizRequestHeader.getId());
                BizRequestDetail reqDetail = new BizRequestDetail();
                reqDetail.setRequestHeader(requestHeader);
                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(reqDetail);
                //加入中间表关联关系
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setRequestHeader(bizRequestHeader);
                bizDetailInvoiceService.save(bizDetailInvoice);
                for (BizRequestDetail bizRequestDetail : requestDetailList) {
                    //商品
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                    int sendNum = bizRequestDetail.getSendNum();    //供货数
                    int sendQty = bizRequestDetail.getSendQty();   //备货单累计供货数量
                    //当供货数量和申报数量不相等时，更改备货单状态
                    if (bizRequestDetail.getReqQty() != (sendQty + sendNum)) {
                        flagRequest = false;
                    }
                    if (sendNum == 0) {
                        continue;
                    }
                    //获取备货单相应的采购单详情,累计采购单单个商品的供货数
                    BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                    BizPoHeader poHeader = null;
                    BizPoDetail bizPoDetail = new BizPoDetail();
                    BizPoDetail poDetail = null;
                    BizRequestDetail bizReqDetail = new BizRequestDetail();
                    bizReqDetail.setSkuInfo(bizSkuInfo);
                    bizPoOrderReq.setRequestDetail(bizReqDetail);
                    bizPoOrderReq.setRequestHeader(requestHeader);
                    List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (bizPoOrderReqList != null && bizPoOrderReqList.size() > 0) {
                        poHeader = bizPoOrderReqList.get(0).getPoHeader();
                    }
                    bizPoDetail.setPoHeader(poHeader);
                    bizPoDetail.setSkuInfo(bizSkuInfo);
                    List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
                    if (bizPoDetailList != null && bizPoDetailList.size() > 0) {
                        poDetail = bizPoDetailList.get(0);
                    }
                    if (poDetail.getSendQty() + sendNum != poDetail.getOrdQty()) {
                        flagPo = false;
                    }
                    poDetail.setSendQty(poDetail.getSendQty() + sendNum);
                    bizPoDetailService.save(poDetail);
                    //累计备货单供货数量
                    bizRequestDetail.setSendQty(sendQty + sendNum);
                    bizRequestDetailService.save(bizRequestDetail);
                    //改备货单状态为备货中(20)
                    requestHeader.setBizStatus(ReqHeaderStatusEnum.STOCKING.getState());
                    bizRequestHeaderService.save(requestHeader);
                    //生成供货记录
                    BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                    bsgr.setSendNum(sendNum);
                    bsgr.setBizRequestHeader(requestHeader);
                    bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                    bsgr.setSkuInfo(bizSkuInfo);
                    bsgr.setOrderNum(requestHeader.getReqNo());
                    bsgr.setSendDate(date);
                    bizSendGoodsRecordService.save(bsgr);
                }
                //更改备货单状态
                if (flagRequest) {
                    requestHeader.setBizStatus(ReqHeaderStatusEnum.STOCK_COMPLETE.getState());
                    bizRequestHeaderService.saveRequestHeader(requestHeader);
                }
                //更改采购单状态,已完成（5）
                if (flagPo){
                    BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                    BizPoOrderReq por = null;
                    bizPoOrderReq.setRequestHeader(bizRequestHeader);
                    List<BizPoOrderReq> porList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (porList != null && porList.size() > 0 ){
                        por = porList.get(0);
                    }
                    BizPoHeader poHeader = por.getPoHeader();
                    //获取该采购单的所有采购详情，如果所有的供货数都和采购数相等，则更改采购单状态为完成
                    BizPoHeader bizPoHeader = bizPoHeaderService.get(poHeader.getId());
                    BizPoDetail poDetail = new BizPoDetail();
                    poDetail.setPoHeader(bizPoHeader);
                    List<BizPoDetail> poDetailList = bizPoDetailService.findList(poDetail);
                    boolean flag = true;
                    for (BizPoDetail bizPoDetail:poDetailList) {
                        if (bizPoDetail.getOrdQty() != bizPoDetail.getSendQty()){
                            flag = false;
                        }
                    }
                    if (flag) {
                        int status = PoHeaderStatusEnum.COMPLETE.getCode();
                        poHeader.setBizStatus((byte) status);
                        bizPoHeaderService.saveStatus(poHeader);
                    }
                }
            }
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
                bizInvoiceDao.update(bizInvoice);
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
	public void delete(BizInvoice bizInvoice) {
		super.delete(bizInvoice);
	}
	
}