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
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.SendGoodsRecordBizStatusEnum;
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
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
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
        bizInvoice.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE,company.getId(),0,bizInvoice.getId()));
        Date date = new Date();
        bizInvoice.setSendDate(date);
        super.save(bizInvoice);
        super.save(bizInvoice);
        List<BizOrderDetail> orderDetailList = bizInvoice.getOrderDetailList();
        List<BizRequestDetail> requestDetailList = bizInvoice.getRequestDetailList();
        if (orderDetailList != null && orderDetailList.size() > 0) {
            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                //加入中间表关联关系
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setOrderDetail(bizOrderDetail);
                bizDetailInvoiceService.save(bizDetailInvoice);
                //生成供货记录
                //商品
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                int sendNum = bizOrderDetail.getSendNum();    //供货数
                //订单号
                bizOrderDetail.getOrderHeader();
//                int sendQty = bsgr.getBizRequestDetail().getSendQty();   //备货单累计供货数量
                //当供货数量和申报数量不相等时，更改备货单状态
//                if (bsgr.getBizRequestDetail().getReqQty() != (sendQty + sendNum)) {
//                    flagRequest = false;
//                }
//                if (sendNum == 0) {
//                    continue;
//                }
                BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                bsgr.setSendNum(sendNum);
                bsgr.setOrderNum(bizOrderDetail.getOrderHeader().getOrderNum());
                bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                bsgr.setSkuInfo(bizSkuInfo);
                bsgr.setSendDate(date);
            }
        }
        if(requestDetailList != null && requestDetailList.size() > 0) {
            for (BizRequestDetail bizRequestDetail : requestDetailList) {
                //加入中间表关联关系
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setRequestDetail(bizRequestDetail);
                bizDetailInvoiceService.save(bizDetailInvoice);
                //商品
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
//                int sendNum = bizRequestDetail.getSendNum();    //供货数
                //订单号
                bizRequestDetail.getRequestHeader();
//                int sendQty = bsgr.getBizRequestDetail().getSendQty();   //备货单累计供货数量
                //当供货数量和申报数量不相等时，更改备货单状态
//                if (bsgr.getBizRequestDetail().getReqQty() != (sendQty + sendNum)) {
//                    flagRequest = false;
//                }
//                if (sendNum == 0) {
//                    continue;
//                }
                //获取备货单相应的采购单详情,累计采购单单个商品的供货数
                BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                BizPoHeader poHeader = null;
                BizPoDetail bizPoDetail = new BizPoDetail();
                BizPoDetail poDetail = null;
                BizRequestDetail bizReqDetail = new BizRequestDetail();
                bizReqDetail.setSkuInfo(bizSkuInfo);
                bizPoOrderReq.setRequestDetail(bizReqDetail);
//                bizPoOrderReq.setSoType((byte)2);
//                bizPoOrderReq.setRequestHeader(bizSendGoodsRecord.getBizRequestHeader());
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
//                if (poDetail.getSendQty()+sendNum != poDetail.getOrdQty()){
//                    flagPo = false;
//                }
//                poDetail.setSendQty(poDetail.getSendQty()+sendNum);
//                bizPoDetailService.save(poDetail);
//                bizRequestDetail.setSendQty(sendQty + sendNum);
                bizRequestDetailService.save(bizRequestDetail);
                //改备货单状态为备货中(20)
//                BizRequestHeader bizReqHeader = bizRequestHeaderService.get(bizSendGoodsRecord.getBizRequestHeader().getId());
//                bizReqHeader.setBizStatus(ReqHeaderStatusEnum.STOCKING.getState());
//                bizRequestHeaderService.save(bizReqHeader);
                //生成供货记录
                BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
//                bsgr.setSendNum(sendNum);
                BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizReqDetail.getRequestHeader().getId());
                bsgr.setBizRequestHeader(bizRequestHeader);
                bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                bsgr.setSkuInfo(bizSkuInfo);
                bsgr.setOrderNum(bizRequestHeader.getReqNo());
                bsgr.setSendDate(date);

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