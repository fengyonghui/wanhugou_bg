/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
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
	public void save(BizInvoice bizInvoice) {
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
        bizInvoice.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,company.getId(),0,1));
        super.save(bizInvoice);
        bizInvoice.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,company.getId(),0,bizInvoice.getId()));
        super.save(bizInvoice);
        //保存图片
        saveCommonImg(bizInvoice);
        //获取订单ID
        String orderHeaders = bizInvoice.getOrderHeaders();
        //获取备货单ID
        String requestHeaders = bizInvoice.getRequestHeaders();

        //货值
        Double valuePrice = 0.0;
//        List<BizRequestHeader> requestHeaderList = bizInvoice.getRequestHeaderList();
        if(StringUtils.isNotBlank(orderHeaders)) {
            String[] orders = orderHeaders.split(",".trim());
            for (int a = 0; a < orders.length; a++) {
                String[] oheaders = orders[a].split("#".trim());
                BizOrderHeader orderHeader = bizOrderHeaderService.get(Integer.parseInt(oheaders[0]));
                //加入中间表关联关系
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setOrderHeader(orderHeader);
                bizDetailInvoiceService.save(bizDetailInvoice);
                String[] odNumArr = oheaders[1].split("\\*");
                for (int i = 0; i < odNumArr.length; i++) {
                    String[] odArr = odNumArr[i].split("-");
                    BizOrderDetail orderDetail = bizOrderDetailService.get(Integer.parseInt(odArr[0]));
                    //商品
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                    int sendNum = Integer.parseInt(odArr[1]);    //供货数
                    valuePrice += bizSkuInfo.getBuyPrice()*sendNum;//累计货值
                    //采购商
                    Office office = officeService.get(orderHeader.getCustomer().getId());
                    int sentQty = orderDetail.getSentQty();    //销售单累计供货数量
                    //当供货数量和申报数量不相等时，更改销售单状态
                    if (orderDetail.getOrdQty() != (sentQty + sendNum)) {
                        flagOrder = false;
                    }
                    if (sendNum == 0) {
                        continue;
                    }
                    //采购中心订单发货
                    if (bizInvoice.getBizStatus()==0) {
                        //销售单状态改为同意供货（供货中）（15）
                        orderHeader.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                        bizOrderHeaderService.saveOrderHeader(orderHeader);
                        //获取库存数
                        BizInventorySku bizInventorySku = new BizInventorySku();
                        bizInventorySku.setSkuInfo(bizSkuInfo);
                        bizInventorySku.setInvInfo(orderDetail.getInventoryInfo());
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
                                if (stock < orderDetail.getOrdQty()) {
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
                            orderDetail.setSentQty(sentQty + sendNum);
                            bizOrderDetailService.saveStatus(orderDetail);
                            //生成供货记录表
                            BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                            bsgr.setSendNum(sendNum);
                            bsgr.setInvInfo(orderDetail.getInventoryInfo());
                            bsgr.setCustomer(office);
                            bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
                            bsgr.setOrderNum(orderHeader.getOrderNum());
                            bsgr.setBizOrderHeader(orderHeader);
                            bsgr.setSkuInfo(bizSkuInfo);
                            bsgr.setSendDate(date);
                            bizSendGoodsRecordService.save(bsgr);
                        }
                    }
                    //供应商或供货部发货
                    if (bizInvoice.getBizStatus()==1) {
                        //获取销售单相应的采购单详情,累计采购单单个商品的供货数
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        BizPoHeader poHeader = null;
                        BizPoDetail bizPoDetail = new BizPoDetail();
                        BizPoDetail poDetail = null;
                        BizOrderDetail bizOrdDetail = new BizOrderDetail();
                        bizOrdDetail.setSkuInfo(bizSkuInfo);
                        bizPoOrderReq.setOrderDetail(bizOrdDetail);
                        bizPoOrderReq.setOrderHeader(orderHeader);
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
                        orderDetail.setSentQty(sentQty + sendNum);
                        bizOrderDetailService.saveStatus(orderDetail);
                        //修改订单状态为供应商发货（19）
                        orderHeader.setBizStatus(OrderHeaderBizStatusEnum.STOCKING.getState());
                        bizOrderHeaderService.saveOrderHeader(orderHeader);
                        //生成供货记录
                        BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                        bsgr.setSendNum(sendNum);
                        bsgr.setCustomer(office);
                        bsgr.setOrderNum(orderHeader.getOrderNum());
                        bsgr.setBizOrderHeader(orderHeader);
                        bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                        bsgr.setSkuInfo(bizSkuInfo);
                        bsgr.setSendDate(date);
                        bizSendGoodsRecordService.save(bsgr);
                    }
                }

                //销售单完成时，更该销售单状态为已供货（20）
                if (flagOrder) {
                    orderHeader.setBizStatus(OrderHeaderBizStatusEnum.SEND.getState());
                    bizOrderHeaderService.saveOrderHeader(orderHeader);
                    if (bizInvoice.getBizStatus()==0) {
                        BizOrderDetail bizOrderDetail = new BizOrderDetail();
                        bizOrderDetail.setOrderHeader(orderHeader);
                        List<BizOrderDetail> ordDetailList = bizOrderDetailService.findList(bizOrderDetail);
                        for (BizOrderDetail orderDetail : ordDetailList) {
                            BizSkuInfo skuInfo = orderDetail.getSkuInfo();
                            BizInventoryInfo inventoryInfo = orderDetail.getInventoryInfo();
                            BizInventorySku bizInventorySku = new BizInventorySku();
                            bizInventorySku.setSkuInfo(skuInfo);
                            bizInventorySku.setInvInfo(inventoryInfo);
                            List<BizInventorySku> invSkuList = bizInventorySkuService.findList(bizInventorySku);
                            if (invSkuList != null && invSkuList.size() > 0) {
                                BizInventorySku inventorySku = invSkuList.get(0);
                                inventorySku.setStockOrdQty(inventorySku.getStockOrdQty() + 1);
                            }
                        }
                    }
                }
                //当供货部或供应商发货时，才涉及采购单状态
                if (bizInvoice.getBizStatus()==1) {
                    //更改采购单状态,已完成（5）
                    if (flagPo) {
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        BizPoOrderReq por = null;
                        bizPoOrderReq.setOrderHeader(orderHeader);
                        List<BizPoOrderReq> porList = bizPoOrderReqService.findList(bizPoOrderReq);
                        if (porList != null && porList.size() > 0) {
                            por = porList.get(0);
                        }
                        BizPoHeader poHeader = por.getPoHeader();
                        //获取该采购单的所有采购详情，如果所有的供货数都和采购数相等，则更改采购单状态为完成
                        BizPoHeader bizPoHeader = bizPoHeaderService.get(poHeader.getId());
                        BizPoDetail poDetail = new BizPoDetail();
                        poDetail.setPoHeader(bizPoHeader);
                        List<BizPoDetail> poDetailList = bizPoDetailService.findList(poDetail);
                        boolean flag = true;
                        for (BizPoDetail bizPoDetail : poDetailList) {
                            if (bizPoDetail.getOrdQty() != bizPoDetail.getSendQty()) {
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
            bizInvoice.setValuePrice(valuePrice);
            super.save(bizInvoice);
        }




        if(StringUtils.isNotBlank(requestHeaders)) {
            String[] requests = requestHeaders.split(",".trim());
            for (int b = 0; b < requests.length; b++) {
                String[] rheaders = requests[b].split("#".trim());
                BizRequestHeader requestHeader = bizRequestHeaderService.get(Integer.parseInt(rheaders[0]));
//                BizRequestDetail reqDetail = new BizRequestDetail();
//                reqDetail.setRequestHeader(requestHeader);
//                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(reqDetail);
                //加入中间表关联关系
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setRequestHeader(requestHeader);
                bizDetailInvoiceService.save(bizDetailInvoice);
                String[] reNumArr = rheaders[1].split("\\*");
                for (int i = 0; i < reNumArr.length; i++) {
                    String[] reArr = reNumArr[i].split("-");
                    BizRequestDetail requestDetail = bizRequestDetailService.get(Integer.parseInt(reArr[0]));
                    //商品
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(requestDetail.getSkuInfo().getId());
                    int sendNum = Integer.parseInt(reArr[1]);     //供货数
                    valuePrice += bizSkuInfo.getBuyPrice()*sendNum;//累计货值
                    //采购中心
                    Office office = officeService.get(requestHeader.getFromOffice().getId());
                    int sendQty = requestDetail.getSendQty();   //备货单累计供货数量
                    //当供货数量和申报数量不相等时，更改备货单状态
                    if (requestDetail.getReqQty() != (sendQty + sendNum)) {
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
                    requestDetail.setSendQty(sendQty + sendNum);
                    bizRequestDetailService.save(requestDetail);
                    //改备货单状态为备货中(20)
                    requestHeader.setBizStatus(ReqHeaderStatusEnum.STOCKING.getState());
                    bizRequestHeaderService.save(requestHeader);
                    //生成供货记录
                    BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                    bsgr.setSendNum(sendNum);
                    bsgr.setBizRequestHeader(requestHeader);
                    bsgr.setCustomer(office);
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
                    bizPoOrderReq.setRequestHeader(requestHeader);
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