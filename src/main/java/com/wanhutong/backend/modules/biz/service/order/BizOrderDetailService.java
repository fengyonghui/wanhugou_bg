/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizCustSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizCustSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.enums.DefaultPropEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单详情(销售订单)Service
 *
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@Service
@Transactional(readOnly = true)
public class BizOrderDetailService extends CrudService<BizOrderDetailDao, BizOrderDetail> {

    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;

    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailDao bizOrderDetailDao;

    @Autowired
    private BizInventorySkuService bizInventorySkuService;
    @Autowired
    private BizOpShelfSkuService bizOpShelfSkuService;
    @Autowired
    private BizOpShelfInfoService bizOpShelfInfoService;
    @Autowired
    private BizCustSkuService bizCustSkuService;

    public BizOrderDetail get(Integer id) {
        return super.get(id);
    }

    public List<BizOrderDetail> findList(BizOrderDetail bizOrderDetail) {
        return super.findList(bizOrderDetail);
    }

    public Page<BizOrderDetail> findPage(Page<BizOrderDetail> page, BizOrderDetail bizOrderDetail) {
        return super.findPage(page, bizOrderDetail);
    }

    @Transactional(readOnly = false)
    public void save(BizOrderDetail bizOrderDetail) {
        String[] skuIdArr = StringUtils.split(bizOrderDetail.getOrderDetaIds(), ",");//多选商品
        String[] saleQtyArr = StringUtils.split(bizOrderDetail.getSaleQtys(), ",");//多个采购数量
        String[] shelfSkuArr = StringUtils.split(bizOrderDetail.getShelfSkus(), ",");//多个货架ID
        String[] nowPriceArr = StringUtils.split(bizOrderDetail.getNowPrices(),",");//多个代采订单的商品价格
        String sentQtysStr = bizOrderDetail.getSentQtys();
        if (StringUtils.isBlank(sentQtysStr)) {
            sentQtysStr = "0";
        }
        String[] sentQtys = StringUtils.split(sentQtysStr,",");////多个发货数量
        int a = 0;
        if (bizOrderDetail.getOrderHeader() != null) {
            BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
            if (bizOrderHeader != null && bizOrderHeader.getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                if (skuIdArr != null && saleQtyArr != null) {
                    for (int i = 0; i < skuIdArr.length; i++) {
                        Integer skuId = Integer.parseInt(skuIdArr[i].trim());//多个Sku商品ID
                        Integer ordQty = Integer.parseInt(saleQtyArr[i].trim());//多个采购数量
                        Integer sendQty = Integer.parseInt(sentQtys[i].trim());//多个已发货数量
                        BizSkuInfo sku = bizSkuInfoService.get(skuId);
                        BizOrderDetail detailnew = new BizOrderDetail();
                        detailnew.setOrderHeader(bizOrderDetail.getOrderHeader());//order_header.id
                        if(bizOrderDetail.getOrderHeader()!=null && bizOrderDetail.getOrderHeader().getId()!=null){
                            List<BizOrderDetail> list = bizOrderDetailDao.findList(detailnew);
                            if(list.size()!=0){
                                for (BizOrderDetail orderDetail : list) {
                                    if(orderDetail.getLineNo()!=null){
                                        a=orderDetail.getLineNo();
                                    }
                                }
                            }
                        }
                        detailnew.setLineNo(++a);//行号
                        detailnew.setpLineNo(null);//bom产品
                        detailnew.setSkuInfo(sku);//sku产品
                        detailnew.setPartNo(sku.getItemNo());//货号
                        detailnew.setSkuName(sku.getName());//商品名称
                        BizCustSku bizCustSku = new BizCustSku();
                        bizCustSku.setSkuInfo(sku);
                        bizCustSku.setCustomer(bizOrderHeader.getCustomer());
                        List<BizCustSku> custSkuList = bizCustSkuService.findList(bizCustSku);
                        if (custSkuList != null && !custSkuList.isEmpty()) {
                            BizCustSku custSku = custSkuList.get(0);
                            custSku.setUnitPrice(new BigDecimal(nowPriceArr[i].trim()));
                            bizCustSkuService.save(custSku);
                        } else {
                            bizCustSku.setUnitPrice(new BigDecimal(nowPriceArr[i].trim()));
                            bizCustSkuService.save(bizCustSku);
                        }
                        detailnew.setUnitPrice(new BigDecimal(nowPriceArr[i].trim()).doubleValue());//单价
                        detailnew.setBuyPrice(detailnew.getUnitPrice());
                        detailnew.setOrdQty(ordQty);//采购数量
                        //detailnew.setSentQty(0);//发货数量默认0
                        detailnew.setSentQty(sendQty);
                        detailnew.setSuplyis(new Office());
                        super.save(detailnew);
                    }
                }
            }
        }
        if (skuIdArr != null && saleQtyArr != null && shelfSkuArr != null) {
            for (int i = 0; i < skuIdArr.length; i++) {
                Integer skuId = Integer.parseInt(skuIdArr[i].trim());//多个Sku商品ID
                Integer ordQty = Integer.parseInt(saleQtyArr[i].trim());//多个采购数量
                Integer shelfSkuId = Integer.parseInt(shelfSkuArr[i].trim());//多个货架ID
                Integer sendQty = Integer.parseInt(sentQtys[i].trim());//多个已发货数量
                BizSkuInfo sku = bizSkuInfoService.get(skuId);
                BizOpShelfSku opShelfSku = bizOpShelfSkuService.get(shelfSkuId);//查询商品
                BizOrderDetail detailnew = new BizOrderDetail();
                detailnew.setOrderHeader(bizOrderDetail.getOrderHeader());//order_header.id
                /*查询详情行号*/
                if(bizOrderDetail.getOrderHeader()!=null && bizOrderDetail.getOrderHeader().getId()!=null){
                    BizOrderDetail detail = new BizOrderDetail();
                    detail.setOrderHeader(bizOrderDetail.getOrderHeader());
                    List<BizOrderDetail> list = super.findList(detail);
                    if(list.size()!=0){
                        for (BizOrderDetail detailLinNo : list) {
                            a=detailLinNo.getLineNo();
                        }
                    }
                }
                detailnew.setLineNo(++a);//行号
                detailnew.setpLineNo(null);//bom产品
                detailnew.setShelfInfo(opShelfSku);//货架
                detailnew.setSkuInfo(sku);//sku产品
                detailnew.setPartNo(sku.getItemNo());//货号
                detailnew.setSkuName(sku.getName());//商品名称
                detailnew.setUnitPrice(opShelfSku.getSalePrice());//单价
                detailnew.setBuyPrice(sku.getBuyPrice());
                detailnew.setOrdQty(ordQty);//采购数量
                //detailnew.setSentQty(0);//发货数量默认0
                detailnew.setSentQty(sendQty);
                if(opShelfSku!=null){
                    BizOpShelfInfo bizOpShelfInfo = bizOpShelfInfoService.get(opShelfSku.getOpShelfInfo().getId());//货架
                    if(bizOpShelfInfo!=null && bizOpShelfInfo.getType()==3){//type==3属于栏目类型 本地备货
                        detailnew.setSuplyis(opShelfSku.getCenterOffice());//采购中心
                    }else{
                        Office officeCenter=new Office();
                        officeCenter.setId(Integer.valueOf(bizOrderDetail.getSuplyIds()));
                        detailnew.setSuplyis(officeCenter);//采购中心
                    }
                }
                super.save(detailnew);
            }
        }
//		------------------------计算订单详情总价 专营----------------------
        BizOrderDetail deta = new BizOrderDetail();
        deta.setOrderHeader(bizOrderDetail.getOrderHeader());
        List<BizOrderDetail> detailList = super.findList(deta);
        Double sum = 0.0;
        if (detailList != null) {
            for (BizOrderDetail bod : detailList) {
                Double price = bod.getUnitPrice();//商品单价
                Integer ordQty = bod.getOrdQty();//采购数量
                if (price == null) {
                    price = 0.0;
                }
                if (ordQty == null) {
                    ordQty = 0;
                }
                sum += price * ordQty;
            }
            BizOrderHeader bizOrder = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
            if(bizOrder !=null){
                bizOrder.setTotalDetail(sum);
                bizOrderHeaderService.updateMoney(bizOrder);
            }
            if (bizOrderDetail.getOrderHeader() != null) {
                BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                if (bizOrderHeader != null && bizOrderHeader.getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {

                } else {
                    //删除执行库存表相应的加减销售订单数量
                    BizInventorySku bizInventorySku = new BizInventorySku();
                    bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
                    BizInventorySku bizInventorySku1 = bizInventorySkuService.get(bizInventorySku);
                    if (bizInventorySku1 != null) {
                        Integer stockOrdQty = bizInventorySku1.getStockOrdQty();
                        bizInventorySku1.setStockOrdQty(++stockOrdQty);//加上相应的销售订单数量
                        bizInventorySkuService.save(bizInventorySku1);
                    }
                }
            }
        }

    }

    public Integer findMaxLine(BizOrderDetail bizOrderDetail) {
        return bizOrderDetailDao.findMaxLine(bizOrderDetail);
    }

    @Transactional(readOnly = false)
    public void saveStatus(BizOrderDetail bizOrderDetail) {
        super.save(bizOrderDetail);
    }

    @Transactional(readOnly = false)
    public void delete(BizOrderDetail bizOrderDetail) {
        super.delete(bizOrderDetail);
    }


    public List<BizOrderDetail> findOrderTotalByVendor(BizOrderHeader bizOrderHeader) {
        return bizOrderDetailDao.findOrderTotalByVendor(bizOrderHeader);
    }

    public List<Map> findRequestTotalByVendor() {
        return bizOrderDetailDao.findRequestTotalByVendor(false);
    }

    public List<BizOrderDetail> findPoHeader(BizOrderDetail bizOrderDetail) {
        return dao.findPoHeader(bizOrderDetail);
    }

    /**
     * 获取待供货需求汇总列表
     *
     * @param office
     * @return
     */
    public List<Map> findRequestTotalByVendorList(Office office) {
        return bizOrderDetailDao.findRequestTotalByVendorList(office);
    }

    public List<BizOrderDetail> findOrderDetailList(Integer invoiceId) {
        return bizOrderDetailDao.findOrderDetailList(invoiceId);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateSkuId(Integer needSkuId, Integer skuId) {
        BizOrderDetail orderDetail = new BizOrderDetail();
        orderDetail.setSkuInfo(new BizSkuInfo(skuId));
        List<BizOrderDetail> orderDetails = findList(orderDetail);
        if (CollectionUtils.isNotEmpty(orderDetails)) {
            for (BizOrderDetail bizOrderDetail : orderDetails) {
                bizOrderDetailDao.updateSkuId(needSkuId,bizOrderDetail.getId());
            }
        }
    }

    /**
     * 查询该商品详情的商品库存
     * @param orderDetailId
     * @return
     */
    public Integer getInvSkuNum(Integer orderDetailId,Integer centId) {
        return bizOrderDetailDao.getInvSkuNum(orderDetailId,centId);
    }
}