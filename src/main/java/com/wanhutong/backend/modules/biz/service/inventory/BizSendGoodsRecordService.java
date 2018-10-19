/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSendGoodsRecordDao;

import javax.annotation.Resource;

/**
 * 供货记录表Service
 * @author 张腾飞
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizSendGoodsRecordService extends CrudService<BizSendGoodsRecordDao, BizSendGoodsRecord> {

    @Resource
    private OfficeService officeService;
   /* @Resource
	private BizInventorySkuService bizInventorySkuService;
    @Resource
	private BizRequestHeaderService bizRequestHeaderService;
    @Resource
	private BizSkuInfoService bizSkuInfoService;
	@Resource
	private BizOrderDetailService bizOrderDetailService;
	@Resource
	private BizRequestDetailService bizRequestDetailService;
	@Resource
	private BizOrderHeaderService bizOrderHeaderService;
	@Resource
	private BizPoDetailService bizPoDetailService;
	@Resource
    private BizPoOrderReqService bizPoOrderReqService;
	@Resource
    private BizPoHeaderService bizPoHeaderService;
	@Resource
	private BizLogisticsService bizLogisticsService;
	@Resource
    private CommonImgService commonImgService;
	@Autowired
    private BizSendGoodsRecordDao bizSendGoodsRecordDao;*/

//    protected Logger log = LoggerFactory.getLogger(getClass());//日志

	public BizSendGoodsRecord get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSendGoodsRecord> findList(BizSendGoodsRecord bizSendGoodsRecord) {
		return super.findList(bizSendGoodsRecord);
	}
	
	public Page<BizSendGoodsRecord> findPage(Page<BizSendGoodsRecord> page, BizSendGoodsRecord bizSendGoodsRecord) {
			User user=UserUtils.getUser();
			if(user.isAdmin()){
				return super.findPage(page, bizSendGoodsRecord);
			}else {
				bizSendGoodsRecord.setDataStatus("filter");
				if(user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.WITHCAPITAL.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.NETWORKSUPPLY.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.NETWORK.getType())){
					bizSendGoodsRecord.getSqlMap().put("sendGoodsRecord", BaseService.dataScopeFilter(user, "cent", "su"));
				}

				return super.findPage(page, bizSendGoodsRecord);
			}

	}
	
	@Transactional(readOnly = false)
	public void save(BizSendGoodsRecord bizSendGoodsRecord) {
	    super.save(bizSendGoodsRecord);
    }
		/*boolean flagRequest = true;		//备货单完成状态
		boolean flagOrder = true;		//销售单完成状态
        boolean flagPo = true;     //采购单完成状态
        int i = 1;      //供货单序号
		// 取出当前用户所在机构，
		User user = UserUtils.getUser();
		Office office1 = officeService.get(user.getCompany().getId());
		for (BizSendGoodsRecord bsgr : bizSendGoodsRecord.getBizSendGoodsRecordList()) {
			//准备数据
			//采购商或采购中心
			Office office = officeService.get(bizSendGoodsRecord.getCustomer().getId());
			//商品
			BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bsgr.getSkuInfo().getId());

            //仓库biz_send_goods_record
			BizInventoryInfo invInfo = bizSendGoodsRecord.getInvInfo();
			int sendNum = bsgr.getSendNum();    //供货数
			//该单属于备货单，累计备货单供货数量
			if (bsgr.getBizRequestDetail() != null && bsgr.getBizRequestDetail().getId() != 0) {
				int sendQty = bsgr.getBizRequestDetail().getSendQty();   //备货单累计供货数量
				//当供货数量和申报数量不相等时，更改备货单状态
				if (bsgr.getBizRequestDetail().getReqQty() != (sendQty + sendNum)) {
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
//                bizPoOrderReq.setSoType((byte)2);
                bizPoOrderReq.setRequestHeader(bizSendGoodsRecord.getBizRequestHeader());
                List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                if (bizPoOrderReqList != null && bizPoOrderReqList.size() > 0){
                    poHeader = bizPoOrderReqList.get(0).getPoHeader();
                }
                bizPoDetail.setPoHeader(poHeader);
                bizPoDetail.setSkuInfo(bizSkuInfo);
                List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
                if (bizPoDetailList != null && bizPoDetailList.size() > 0){
                    poDetail = bizPoDetailList.get(0);
                }
                if (poDetail.getSendQty()+sendNum != poDetail.getOrdQty()){
                    flagPo = false;
                }
                poDetail.setSendQty(poDetail.getSendQty()+sendNum);
                bizPoDetailService.save(poDetail);
                //累计备货单供货数量
				BizRequestDetail bizRequestDetail = bizRequestDetailService.get(bsgr.getBizRequestDetail().getId());
				bizRequestDetail.setSendQty(sendQty + sendNum);
				bizRequestDetailService.save(bizRequestDetail);
				//改备货单状态为备货中(20)
                BizRequestHeader bizReqHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
                bizReqHeader.setBizStatus(ReqHeaderStatusEnum.STOCKING.getState());
                bizRequestHeaderService.save(bizReqHeader);
                //生成供货记录表
				bsgr.setSendNum(sendNum);
				if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
					BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
					bsgr.setBizRequestHeader(bizRequestHeader);
				}
				if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
					BizOrderHeader bizOrderHeader1 = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
					bsgr.setBizOrderHeader(bizOrderHeader1);
				}
                String sendNumber = GenerateOrderUtils.getOrderNum(OrderTypeEnum.SE,office1.getId(),office.getId(),i++);
				bsgr.setSendNumber(sendNumber);
				bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
				bsgr.setInvInfo(invInfo);
				bsgr.setCustomer(office);
				bsgr.setFreight(bsgr.getFreight());
				bsgr.setOperation(bsgr.getOperation());
				bsgr.setValuePrice(bsgr.getValuePrice());
				bsgr.setImgUrl(bsgr.getImgUrl());
                BizLogistics bizLogistics = bizLogisticsService.get(bsgr.getBizLogistics().getId());
                bsgr.setCarrier(bsgr.getCarrier());
                bsgr.setSettlementStatus(bsgr.getSettlementStatus());
                bsgr.setBizLogistics(bizLogistics);
				bsgr.setSkuInfo(bizSkuInfo);
				bsgr.setOrderNum(bsgr.getOrderNum());
				Date date = new Date();
				bsgr.setSendDate(date);
				super.save(bsgr);
				bsgr.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,office1.getId(),office.getId(),bsgr.getId()));
				super.save(bsgr);
                //保存图片
                saveCommonImg(bsgr);
			}
			//该订单属于销售订单，累计销售单供货数量
			if (bsgr.getBizOrderDetail() != null && bsgr.getBizOrderDetail().getId() != 0) {
				int sentQty = bsgr.getBizOrderDetail().getSentQty();	//销售单累计供货数量
				//当供货数量和申报数量不相等时，更改销售单状态
				if (bsgr.getBizOrderDetail().getOrdQty() != (sentQty + sendNum)){
					flagOrder = false;
				}
				if (sendNum == 0) {
					continue;
				}
				//该用户是采购中心
				if(bizStatu.equals("0")){

					//销售单状态改为同意供货（供货中）（15）
					BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
					bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
					bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
					//获取库存数
					BizInventorySku bizInventorySku = new BizInventorySku();
					bizInventorySku.setSkuInfo(bizSkuInfo);
					bizInventorySku.setInvInfo(invInfo);
					bizInventorySku.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
					List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
					int stock = 0;
					//没有库存，改销售单状态为采购中（17）
					if (list == null || list.size() == 0 || list.get(0).getStockQty() == 0){
						*//*bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
						bizOrderHeaderService.saveOrderHeader(bizOrderHeader);*//*
						flagOrder=false;
					}else {
						//有库存
						for (BizInventorySku invSku:list) {
							stock = invSku.getStockQty();
							//如果库存不够，则改销售单状态为采购中（17）
							if (stock < bsgr.getBizOrderDetail().getOrdQty()){
								*//*bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
								bizOrderHeaderService.saveOrderHeader(bizOrderHeader);*//*
								flagOrder=false;
								if(sendNum > stock){
									sendNum = stock;
								}
								//判断该用户是否是采购中心，如果是采购中心，对应的库存需要扣减
//								if (!office1.getType().equals(OfficeTypeEnum.SUPPLYCENTER.getType())){
									invSku.setStockQty(stock-sendNum);
									bizInventorySkuService.save(invSku);
//								}
							}else {
								invSku.setStockQty(stock-sendNum);
								bizInventorySkuService.save(invSku);
							}
						}
							//累计已供数量
							BizOrderDetail bizOrderDetail = bizOrderDetailService.get(bsgr.getBizOrderDetail().getId());
							bizOrderDetail.setSentQty(sentQty + sendNum);
							bizOrderDetailService.saveStatus(bizOrderDetail);
							//生成供货记录表
							bsgr.setSendNum(sendNum);
							if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
								BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
								bsgr.setBizRequestHeader(bizRequestHeader);
							}
							if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
								BizOrderHeader bizOrderHeader1 = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
								bsgr.setBizOrderHeader(bizOrderHeader1);
							}
							//如果不是供应中心，供货记录设为属于采购中心，否则设为供应中心
//							if (!office1.getType().equals(OfficeTypeEnum.SUPPLYCENTER.getType())){
							bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
//							}else{
//								bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
//							}
                            String sendNumber = GenerateOrderUtils.getOrderNum(OrderTypeEnum.SE,0,office.getId(),i++);
                            bsgr.setSendNumber(sendNumber);
							bsgr.setInvInfo(invInfo);
							bsgr.setCustomer(office);
							bsgr.setSkuInfo(bizSkuInfo);
							bsgr.setOrderNum(bsgr.getOrderNum());
							Date date = new Date();
							bsgr.setSendDate(date);
							super.save(bsgr);
                            bsgr.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,office1.getId(),office.getId(),bsgr.getId()));
                            super.save(bsgr);
					}
				}
				if(bizStatu.equals("1")) {//该用户是供应中心
                    //获取销售单相应的采购单详情,累计采购单单个商品的供货数
                    BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                    BizPoHeader poHeader = null;
                    BizPoDetail bizPoDetail = new BizPoDetail();
                    BizPoDetail poDetail = null;
                    BizOrderDetail bizOrdDetail = new BizOrderDetail();
                    bizOrdDetail.setSkuInfo(bizSkuInfo);
                    bizPoOrderReq.setOrderDetail(bizOrdDetail);
//                    bizPoOrderReq.setSoType((byte)1);
                    bizPoOrderReq.setOrderHeader(bizSendGoodsRecord.getBizOrderHeader());
                    List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (bizPoOrderReqList != null && bizPoOrderReqList.size()>0){
                        poHeader = bizPoOrderReqList.get(0).getPoHeader();
                    }
                    bizPoDetail.setPoHeader(poHeader);
                    bizPoDetail.setSkuInfo(bizSkuInfo);
                    List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
                    if (bizPoDetailList != null && bizPoDetailList.size() > 0){
                        poDetail = bizPoDetailList.get(0);
                    }
                    if (poDetail.getSendQty()+sendNum != poDetail.getOrdQty()){
                        flagPo = false;
                    }
					//累计已供数量
					BizOrderDetail bizOrderDetail = bizOrderDetailService.get(bsgr.getBizOrderDetail().getId());
					bizOrderDetail.setSentQty(sentQty + sendNum);
					bizOrderDetailService.saveStatus(bizOrderDetail);
					//修改订单状态为供应商发货（19）
                    BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
                    bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.STOCKING.getState());
                    bizOrderHeaderService.save(bizOrderHeader);
					//生成供货记录表
					bsgr.setSendNum(sendNum);
					if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
						BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
						bsgr.setBizRequestHeader(bizRequestHeader);
					}
					if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
						BizOrderHeader bizOrderHeader1 = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
						bsgr.setBizOrderHeader(bizOrderHeader1);
					}
                    String sendNumber = GenerateOrderUtils.getOrderNum(OrderTypeEnum.SE,0,office.getId(),i++);
                    bsgr.setSendNumber(sendNumber);
					bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
					bsgr.setInvInfo(invInfo);
					bsgr.setCustomer(office);
                    bsgr.setFreight(bsgr.getFreight());
                    bsgr.setOperation(bsgr.getOperation());
                    bsgr.setValuePrice(bsgr.getValuePrice());
                    bsgr.setImgUrl(bsgr.getImgUrl());
                    BizLogistics bizLogistics = bizLogisticsService.get(bsgr.getBizLogistics().getId());
                    bsgr.setCarrier(bsgr.getCarrier());
                    bsgr.setSettlementStatus(bsgr.getSettlementStatus());
                    bsgr.setBizLogistics(bizLogistics);
					bsgr.setSkuInfo(bizSkuInfo);
					bsgr.setOrderNum(bsgr.getOrderNum());
					Date date = new Date();
					bsgr.setSendDate(date);
					super.save(bsgr);
                    bsgr.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,office1.getId(),office.getId(),bsgr.getId()));
                    super.save(bsgr);
                    //保存图片
                    saveCommonImg(bsgr);
				}
			}
		}
		//更改备货单状态
		if (bizSendGoodsRecord.getBizRequestHeader() != null && bizSendGoodsRecord.getBizRequestHeader().getId() != 0) {
			if (flagRequest) {
				BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
				bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.STOCK_COMPLETE.getState());
				bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
			}
			//更改采购单状态,已完成（5）
			if (flagPo){
                BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                BizPoOrderReq por = null;
                bizPoOrderReq.setRequestHeader(bizSendGoodsRecord.getBizRequestHeader());
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
		//销售单完成时，更该销售单状态为已供货（20）
		if (bizSendGoodsRecord.getBizOrderHeader() != null && bizSendGoodsRecord.getBizOrderHeader().getId() != 0) {
			if (flagOrder) {
				BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizSendGoodsRecord.getBizOrderHeader().getId());
				bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.SEND.getState());
				bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
			}
			//当用户为供货中心时，才涉及采购单状态
            if(bizStatu.equals("1")) {
                //更改采购单状态,已完成（5）
                if (flagPo){
                    BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                    BizPoOrderReq por = null;
                    bizPoOrderReq.setOrderHeader(bizSendGoodsRecord.getBizOrderHeader());
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
		//
//			super.save(bizSendGoodsRecord);
	}

    *//**
     * 保存物流信息图片
     * @param bizSendGoodsRecord
     *//*
    @Transactional(readOnly = false)
    public void saveCommonImg(BizSendGoodsRecord bizSendGoodsRecord) {
        String imgUrl = null;
        try {
            imgUrl = URLDecoder.decode(bizSendGoodsRecord.getImgUrl(), "utf-8");//主图转换编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("物流信息图转换编码异常." + e.getMessage(), e);
        }
        if (imgUrl != null) {
            String[] photoArr = imgUrl.split("\\|");
            saveLogisticsImg(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizSendGoodsRecord, photoArr);
        }
        List<CommonImg> commonImgs = getImgList(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizSendGoodsRecord.getId());
        for (int i = 0; i < commonImgs.size(); i++) {
            CommonImg commonImg = commonImgs.get(i);
            commonImg.setImgSort(i);
            commonImgService.save(commonImg);

            if (i == 0) {
                bizSendGoodsRecord.setImgUrl(commonImg.getImgServer() + commonImg.getImgPath());
                bizSendGoodsRecordDao.update(bizSendGoodsRecord);
            }
        }
    }

    private List<CommonImg> getImgList(Integer imgType, Integer bizSendGoodsRecordId) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizSendGoodsRecordId);
        commonImg.setObjectName("biz_send_goods_record");
        commonImg.setImgType(imgType);
        return commonImgService.findList(commonImg);
    }

    public void saveLogisticsImg(Integer imgType, BizSendGoodsRecord bizSendGoodsRecord, String[] photoArr) {
        if (bizSendGoodsRecord.getId() == null) {
            log.error("Can't save logistics image without bizSendGoodsRecord ID!");
            return;
        }

        List<CommonImg> commonImgs = getImgList(imgType, bizSendGoodsRecord.getId());

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
        commonImg.setObjectId(bizSendGoodsRecord.getId());
        commonImg.setObjectName("biz_send_goods_record");
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
    }*/
	
	@Transactional(readOnly = false)
	public void delete(BizSendGoodsRecord bizSendGoodsRecord) {
		super.delete(bizSendGoodsRecord);
	}
}