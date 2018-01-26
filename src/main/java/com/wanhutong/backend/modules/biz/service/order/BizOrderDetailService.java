/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderSkuPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.product.BizProdPropValueService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuPropValueService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private BizSkuInfoService bizSkuInfoService;

    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;

    @Autowired
    private BizOrderDetailDao bizOrderDetailDao;

    @Autowired
    private BizInventorySkuService bizInventorySkuService;
    @Autowired
    private BizOpShelfSkuService bizOpShelfSkuService;
    @Autowired
    private BizSkuPropValueService bizSkuPropValueService;
    @Autowired
    private BizOrderSkuPropValueService bizOrderSkuPropValueService;
    @Autowired
    private BizProdPropValueService bizProdPropValueService;

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
        Double sum = 0.0;//价格初始值
        String oneOrder = bizOrderDetail.getOrderHeader().getOneOrder();//首单标记
        BizOrderHeader orderDetailHeader = bizOrderDetail.getOrderHeader();
        BizOrderHeader bb = bizOrderHeaderService.get(orderDetailHeader);
        BizOrderHeader orderHeaderNew = new BizOrderHeader();
        orderHeaderNew.setOrderType(bb.getOrderType());
        orderHeaderNew.setCustomer(bb.getCustomer());
        orderHeaderNew.setTotalDetail(sum);//计算总价
        orderHeaderNew.setTotalExp(bb.getTotalExp());
        orderHeaderNew.setFreight(bb.getFreight());
        orderHeaderNew.setInvStatus(bb.getInvStatus());
        orderHeaderNew.setBizStatus(bb.getBizStatus());
        orderHeaderNew.setPlatformInfo(bb.getPlatformInfo());
        orderHeaderNew.setBizLocation(bb.getBizLocation());
        String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(orderHeaderNew.getOrderType().toString()), orderHeaderNew.getCustomer().getId());

        BizOrderDetail orderDetailNew = null;
        String[] skuIdArr = StringUtils.split(bizOrderDetail.getOrderDetaIds(), ",");//多选商品
        String[] saleQtyArr = StringUtils.split(bizOrderDetail.getSaleQtys(), ",");//多个采购数量
        String[] shelfSkuArr = StringUtils.split(bizOrderDetail.getShelfSkus(), ",");//多个货架ID
        int a=0;
        for (int i = 0; i < skuIdArr.length; i++) {
            Integer skuId= Integer.parseInt(skuIdArr[i].trim());//多个Sku商品ID
            Integer ordQty = Integer.parseInt(saleQtyArr[i].trim());//多个采购数量
            Integer shelfSkuId=Integer.parseInt(shelfSkuArr[i].trim());//多个货架ID
            BizSkuInfo sku = bizSkuInfoService.get(skuId);
//            -------------------------------------查询材质/颜色/尺寸--------------------------------------------
            BizSkuPropValue bizSkuPropValue = new BizSkuPropValue();//sku商品属性表
//            BizProdPropertyInfo bizProdPropertyInfo = new BizProdPropertyInfo();//属性表
//            bizProdPropertyInfo.setId(1);
            bizSkuPropValue.setSkuInfo(sku);//sku_Id
            bizSkuPropValue.setSource("sys");// 系统属性级别，4层级别：系统属性/分类属性/产品属性spu/商品属性sku。
//            bizSkuPropValue.setProdPropertyInfo(bizProdPropertyInfo);//属性名 1材质Quality，2颜色Color，3尺寸Standard
            List<BizSkuPropValue> skuValueList = bizSkuPropValueService.findList(bizSkuPropValue);

//            BizSkuPropValue bizSkuPropValueTwo = new BizSkuPropValue();//sku商品属性表
//            BizProdPropertyInfo bizProdPropertyInfoTwo = new BizProdPropertyInfo();//属性表
//            bizProdPropertyInfoTwo.setId(2);
//            bizSkuPropValueTwo.setSkuInfo(sku);//sku_Id
//            bizSkuPropValueTwo.setSource("sys");// 系统属性级别，4层级别：系统属性/分类属性/产品属性spu/商品属性sku。
//            bizSkuPropValueTwo.setProdPropertyInfo(bizProdPropertyInfoTwo);//属性名 1材质Quality，2颜色Color，3尺寸Standard
//            List<BizSkuPropValue> skuValueListTwo = bizSkuPropValueService.findList(bizSkuPropValueTwo);
//
//            BizSkuPropValue bizSkuPropValueThis = new BizSkuPropValue();//sku商品属性表
//            BizProdPropertyInfo bizProdPropertyInfoThis = new BizProdPropertyInfo();//属性表
//            bizProdPropertyInfoThis.setId(3);
//            bizSkuPropValueThis.setSkuInfo(sku);//sku_Id
//            bizSkuPropValueThis.setSource("sys");// 系统属性级别，4层级别：系统属性/分类属性/产品属性spu/商品属性sku。
//            bizSkuPropValueThis.setProdPropertyInfo(bizProdPropertyInfoThis);//属性名 1材质Quality，2颜色Color，3尺寸Standard
//            List<BizSkuPropValue> skuValueListThis = bizSkuPropValueService.findList(bizSkuPropValueThis);
            BizOpShelfSku opShelfSku =bizOpShelfSkuService.get(shelfSkuId);
            Integer type = sku.getSkuType();
            if(bizOrderDetail.getId()==null){
                if (type == BizOrderDiscount.FIRST_ORDER.getOneOr() ) {//3 非专营
                    System.out.println(" --进了非专营订单-- ");
                    orderHeaderNew.setOrderNum(orderNum);//新建订单编号
                    orderHeaderNew.setBizType(BizOrderDiscount.THIS_ORDER.getOneOr());//2
                    Double price = sku.getBasePrice();//商品单价
                    sum += price * ordQty;
                    orderHeaderNew.setTotalDetail(sum);//商品详情总价
                    bizOrderHeaderService.save(orderHeaderNew);
                    orderDetailNew=new BizOrderDetail();
                    orderDetailNew.setOrderHeader(orderHeaderNew);//order_header.id
                    orderDetailNew.setLineNo(++a);//行号
                    orderDetailNew.setPartNo(null);//bom
                    orderDetailNew.setShelfInfo(opShelfSku);//货架
                    orderDetailNew.setSkuInfo(sku);//sku产品
                    orderDetailNew.setPartNo(sku.getPartNo());//编号
                    orderDetailNew.setSkuName(sku.getName());//商品名称
                    orderDetailNew.setUnitPrice(sku.getBasePrice());//单价
                    orderDetailNew.setOrdQty(ordQty);//采购数量
                    orderDetailNew.setSentQty(0);//发货数量默认0
                    super.save(orderDetailNew);//把商品放入新建订单中
                    if(bizOrderDetail.getId()!=null){
                        BizOrderSkuPropValue bizOrderSkuPropValue = new BizOrderSkuPropValue();//订单商品属性表
    //                    BizProdPropValue bizProdPropValue = new BizProdPropValue();//spu商品属性
                        BizOrderDetail detailProp = new BizOrderDetail();
                        detailProp.setId(bizOrderDetail.getId());
                        bizOrderSkuPropValue.setOrderDetails(detailProp);
    //                    bizProdPropValue.setId(skuValueList.get(0).getProdPropValue().getId());
                        bizOrderSkuPropValue.setPropInfos(skuValueList.get(0).getProdPropValue());//spu属性
                        bizOrderSkuPropValue.setPropName(skuValueList.get(0).getPropName());//sku属性
                        bizOrderSkuPropValue.setPropValue(skuValueList.get(0).getPropValue());//sku属性值
                        bizOrderSkuPropValueService.save(bizOrderSkuPropValue);//保存到订单详情商品属性表
                    }
    //                    if(skuValueList!=null && skuValueList.size()>0){
    //                        orderDetailNew.setQuality(skuValueList.get(0).getPropValue());//材质
    //                    }else{
    //                        orderDetailNew.setQuality("无");
    //                    }if(skuValueListTwo!=null && skuValueListTwo.size()>0){
    //                        orderDetailNew.setColor(skuValueListTwo.get(0).getPropValue());//颜色
    //                    }else{
    //                        orderDetailNew.setColor("无");
    //                    }if(skuValueListThis!=null && skuValueListThis.size()>0){
    //                        orderDetailNew.setStandard(skuValueListThis.get(0).getPropValue());//规格
    //                    }else{
    //                        orderDetailNew.setStandard("无");
    //                    }
                }else if (type == BizOrderDiscount.TWO_ORDER.getOneOr() || type == BizOrderDiscount.THIS_ORDER.getOneOr()) {//1 专营 2 定制商品
                    System.out.println(" --进了专营订单-- ");
                    bb.setOrderNum(bb.getOrderNum());//不新建
                    bb.setBizType(BizOrderDiscount.TWO_ORDER.getOneOr());//专营 1 order_header
                    bizOrderHeaderService.save(bb);
                    bizOrderDetail.setOrderHeader(bb);//order_header.id
                    bizOrderDetail.setLineNo(++a);//行号
                    bizOrderDetail.setPartNo(null);//bom产品
                    bizOrderDetail.setShelfInfo(opShelfSku);//货架
                    bizOrderDetail.setSkuInfo(sku);//sku产品
                    bizOrderDetail.setPartNo(sku.getPartNo());//编号
                    bizOrderDetail.setSkuName(sku.getName());//商品名称
                    bizOrderDetail.setUnitPrice(sku.getBasePrice());//单价
                    bizOrderDetail.setOrdQty(ordQty);//采购数量
                    bizOrderDetail.setSentQty(0);//发货数量默认0
                    super.save(bizOrderDetail);
                    if(bizOrderDetail.getId()!=null) {
                        BizOrderSkuPropValue bizOrderSkuPropValueTwo = new BizOrderSkuPropValue();//订单商品属性表
//                    BizProdPropValue bizProdPropValueTwo = new BizProdPropValue();//spu商品属性
                        BizOrderDetail detailPropTwo = new BizOrderDetail();
                        detailPropTwo.setId(bizOrderDetail.getId());
                        bizOrderSkuPropValueTwo.setOrderDetails(detailPropTwo);
//                    bizProdPropValueTwo.setId(skuValueList.get(0).getProdPropValue().getId());
                        bizOrderSkuPropValueTwo.setPropInfos(skuValueList.get(0).getProdPropValue());//spu属性
                        bizOrderSkuPropValueTwo.setPropName(skuValueList.get(0).getPropName());//sku属性
                        bizOrderSkuPropValueTwo.setPropValue(skuValueList.get(0).getPropValue());//sku属性值
                        bizOrderSkuPropValueService.save(bizOrderSkuPropValueTwo);//保存到订单详情商品属性表
                    }
                } else {
                    System.out.println(" 未知 ");
                }
            }else{
                BizOrderSkuPropValue bizOrderSkuPropValueThis = new BizOrderSkuPropValue();//订单商品属性表
//                BizProdPropValue bizProdPropValueThis = new BizProdPropValue();//spu商品属性
                BizOrderDetail detailPropThis = new BizOrderDetail();
                detailPropThis.setId(bizOrderDetail.getId());
                bizOrderSkuPropValueThis.setOrderDetails(detailPropThis);
//                bizProdPropValueThis.setId(skuValueList.get(0).getProdPropValue().getId());
                bizOrderSkuPropValueThis.setPropInfos(skuValueList.get(0).getProdPropValue());//spu属性
                bizOrderSkuPropValueThis.setPropName(skuValueList.get(0).getPropName());//sku属性
                bizOrderSkuPropValueThis.setPropValue(skuValueList.get(0).getPropValue());//sku属性值
                bizOrderSkuPropValueService.save(bizOrderSkuPropValueThis);//保存到订单详情商品属性表
                super.save(bizOrderDetail);
            }
        }
//		------------------------计算订单详情总价----------------------
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
        List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
        Double orderHeaderMoney = bizOrderHeader.getTotalDetail();
        Double Money01 = orderHeaderMoney * BizOrderDiscount.SELF_SUPPORT.getCalcs();//0.1
        Double Money005 = orderHeaderMoney * BizOrderDiscount.NON_SELF_SUPPORT.getCalcs();//0.05
        for (BizOrderHeader order : list) {
            Double price = bizOrderDetail.getUnitPrice();//商品单价
            Integer ordQty = bizOrderDetail.getOrdQty();//采购数量
            if(price==null){
                price=0.0;
            }
            if(ordQty==null){
                ordQty=0;
            }
            sum += price * ordQty;
            if (oneOrder == "firstOrder") {//首单标记判断
                if (orderHeaderMoney <= 10000) {//限额 10000
                    if (bizOrderHeader.getBizType() == BizOrderDiscount.TWO_ORDER.getOneOr()) {//专营订单-1
                        System.out.println(" 优惠1 ");
                        bizOrderHeader.setTotalDetail(orderHeaderMoney - Money01);//0.1
                        bizOrderHeaderService.save(bizOrderHeader);
                    } else if (bizOrderHeader.getBizType() == BizOrderDiscount.THIS_ORDER.getOneOr()) {//非专营订单-2
                        System.out.println(" 优惠2 ");
                        bizOrderHeader.setTotalDetail(orderHeaderMoney - Money005);//0.05
                        bizOrderHeaderService.save(bizOrderHeader);
                    } else {
                        System.out.println(" 未知 ");
                    }
                }
            } else {
                System.out.println(" 无优惠 ");
            }
            bizOrderHeader.setTotalDetail(bizOrderHeader.getTotalDetail() + sum);
            bizOrderHeaderService.save(bizOrderHeader);
        }
        //删除执行库存表相应的加减数量
        Integer ordQty = bizOrderDetail.getOrdQty();
        BizInventorySku bizInventorySku = new BizInventorySku();
        bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
        List<BizInventorySku> inventorySku = bizInventorySkuService.findList(bizInventorySku);
        for (BizInventorySku is : inventorySku) {
            Integer stockOrdQty = is.getStockOrdQty();
            is.setStockOrdQty(++stockOrdQty);//加上相应的销售订单数量
            bizInventorySkuService.save(is);
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
        //删除执行库存表相应的加减数量
        Integer ordQty = bizOrderDetail.getOrdQty();
        BizInventorySku bizInventorySku = new BizInventorySku();
        bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
        List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
        for (BizInventorySku inventorySku : list) {
            Integer stockOrdQty = inventorySku.getStockOrdQty();
            inventorySku.setStockOrdQty(--stockOrdQty);//减去相应的销售订单数量
            bizInventorySkuService.save(inventorySku);
        }
        super.delete(bizOrderDetail);
    }

}