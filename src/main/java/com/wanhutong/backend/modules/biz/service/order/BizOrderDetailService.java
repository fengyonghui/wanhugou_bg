/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
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
//    @Autowired
//    private BizProdPropValueService bizProdPropValueService;

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
        String oneOrder = bizOrderDetail.getOrderHeader().getOneOrder();//首单标记
        BizOrderHeader bb = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader());
        BizOrderHeader orderHeaderNew = new BizOrderHeader();
        orderHeaderNew.setOrderType(bb.getOrderType());
        orderHeaderNew.setCustomer(bb.getCustomer());
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
        Double sum2 = 0.0;//价格初始值
        if(skuIdArr!=null && saleQtyArr!=null && shelfSkuArr!=null){
            for (int i = 0; i < skuIdArr.length; i++) {
                Integer skuId= Integer.parseInt(skuIdArr[i].trim());//多个Sku商品ID
                Integer ordQty = Integer.parseInt(saleQtyArr[i].trim());//多个采购数量
                Integer shelfSkuId=Integer.parseInt(shelfSkuArr[i].trim());//多个货架ID
                BizSkuInfo sku = bizSkuInfoService.get(skuId);
    //          -------------------------------------查询材质/颜色/尺寸--------------------------------------------
                BizSkuPropValue bizSkuPropValue = new BizSkuPropValue();//sku商品属性表
                bizSkuPropValue.setSkuInfo(sku);//sku_Id
                List<BizSkuPropValue> skuValueList = bizSkuPropValueService.findList(bizSkuPropValue);
    //            BizSkuPropValue bizSkuPropValueTwo = new BizSkuPropValue();//sku商品属性表
    //            BizProdPropertyInfo bizProdPropertyInfoTwo = new BizProdPropertyInfo();//属性表
    //            bizProdPropertyInfoTwo.setId(2);
    //            bizSkuPropValueTwo.setSkuInfo(sku);//sku_Id
    //            bizSkuPropValueTwo.setSource("sys");// 系统属性级别，4层级别：系统属性/分类属性/产品属性spu/商品属性sku。
    //            bizSkuPropValueTwo.setProdPropertyInfo(bizProdPropertyInfoTwo);//属性名 1材质Quality，2颜色Color，3尺寸Standard
    //            List<BizSkuPropValue> skuValueListTwo = bizSkuPropValueService.findList(bizSkuPropValueTwo);
    //
                BizOpShelfSku opShelfSku =bizOpShelfSkuService.get(shelfSkuId);
                Integer type = sku.getSkuType();
                if (type == BizOrderDiscount.FIRST_ORDER.getOneOr() ) {//3 非专营
                    System.out.println(" --进了非专营订单-- ");
                    orderHeaderNew.setOrderNum(orderNum);//新建订单编号
                    orderHeaderNew.setBizType(BizOrderDiscount.THIS_ORDER.getOneOr());//2
//                    Double skuPrice=sku.getBasePrice();
//                    if(skuPrice==null){
//                        skuPrice=0.0;
//                    }
//                    if(ordQty==null){
//                        ordQty=0;
//                    }
//                    if(orderHeaderNew.getTotalDetail()!=null){
//                        sum2 += skuPrice * ordQty;
//                        orderHeaderNew.setTotalDetail(orderHeaderNew.getTotalDetail()+sum2);
//                    }else{
//                        sum2 += skuPrice * ordQty;
//                        orderHeaderNew.setTotalDetail(sum2);
//                    }
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
                    for(BizSkuPropValue bsp:skuValueList){
                        BizOrderSkuPropValue bizOrderSkuPropValue = new BizOrderSkuPropValue();//订单商品属性表
                        BizOrderDetail detailProp = new BizOrderDetail();
                        detailProp.setId(orderDetailNew.getId());
                        bizOrderSkuPropValue.setOrderDetails(detailProp);//detail_id
                        bizOrderSkuPropValue.setPropInfos(bsp.getProdPropValue());//spu属性
                        bizOrderSkuPropValue.setPropName(bsp.getPropName());//sku属性
                        bizOrderSkuPropValue.setPropValue(bsp.getPropValue());//sku属性值
                        bizOrderSkuPropValueService.save(bizOrderSkuPropValue);//保存到订单详情商品属性表
                    }
                    //删除执行库存表相应的加减数量
                    BizInventorySku bizInventorySku = new BizInventorySku();
                    bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
                    BizInventorySku bizInventorySku1 = bizInventorySkuService.get(bizInventorySku);
                    if(bizInventorySku1!=null){
                        Integer stockOrdQty = bizInventorySku1.getStockOrdQty();
                        bizInventorySku1.setStockOrdQty(++stockOrdQty);//加上相应的销售订单数量
                        bizInventorySkuService.save(bizInventorySku1);
                    }
                }else if (type == BizOrderDiscount.TWO_ORDER.getOneOr() || type == BizOrderDiscount.THIS_ORDER.getOneOr() || bizOrderDetail.getId()!=null) {//1 专营 2 定制商品
                    System.out.println(" --进了专营订单-- ");
                    BizOrderDetail detailnew = new BizOrderDetail();
                    detailnew.setOrderHeader(bb);//order_header.id
                    detailnew.setLineNo(++a);//行号
                    detailnew.setPartNo(null);//bom产品
                    detailnew.setShelfInfo(opShelfSku);//货架
                    detailnew.setSkuInfo(sku);//sku产品
                    detailnew.setPartNo(sku.getPartNo());//编号
                    detailnew.setSkuName(sku.getName());//商品名称
                    detailnew.setUnitPrice(sku.getBasePrice());//单价
                    detailnew.setOrdQty(ordQty);//采购数量
                    detailnew.setSentQty(0);//发货数量默认0
                    super.save(detailnew);
                    for(BizSkuPropValue bsp:skuValueList){
                        BizOrderSkuPropValue bizOrderSkuPropValueTwo = new BizOrderSkuPropValue();//订单商品属性表
                        BizOrderDetail detailPropTwo = new BizOrderDetail();
                        detailPropTwo.setId(detailnew.getId());
                        bizOrderSkuPropValueTwo.setOrderDetails(detailPropTwo);//detail_id
                        bizOrderSkuPropValueTwo.setPropInfos(bsp.getProdPropValue());//spu属性
                        bizOrderSkuPropValueTwo.setPropName(bsp.getPropName());//sku属性
                        bizOrderSkuPropValueTwo.setPropValue(bsp.getPropValue());//sku属性值
                        bizOrderSkuPropValueService.save(bizOrderSkuPropValueTwo);//保存到订单详情商品属性表
                    }
                    //删除执行库存表相应的加减数量
                    BizInventorySku bizInventorySku = new BizInventorySku();
                    bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
                    BizInventorySku bizInventorySku1 = bizInventorySkuService.get(bizInventorySku);
                    if(bizInventorySku1!=null){
                        Integer stockOrdQty = bizInventorySku1.getStockOrdQty();
                        bizInventorySku1.setStockOrdQty(++stockOrdQty);//加上相应的销售订单数量
                        bizInventorySkuService.save(bizInventorySku1);
                    }
                }else{System.out.println(" 未知 ");}
            }
        }
//		------------------------计算订单详情总价 专营----------------------
        BizOrderDetail deta = new BizOrderDetail();
        deta.setOrderHeader(bizOrderDetail.getOrderHeader());
        List<BizOrderDetail> detailList = super.findList(deta);
        Double sum=0.0;
        if(detailList!=null){
            for(BizOrderDetail bod : detailList){
                Double price = bod.getUnitPrice();//商品单价
                Integer ordQty = bod.getOrdQty();//采购数量
                if(price==null){price=0.0; }
                if(ordQty==null){ordQty=0; }
                sum+=price*ordQty;
            }
            if (oneOrder.equals("firstOrder")) {//首单标记判断
                BizOrderHeader bizOrder = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                Double Money01 = sum * BizOrderDiscount.SELF_SUPPORT.getCalcs();//0.1
                Double Money005 = sum * BizOrderDiscount.NON_SELF_SUPPORT.getCalcs();//0.05
                if(sum <= 10000){//限额 10000
                    if(bizOrder.getBizType() == BizOrderDiscount.TWO_ORDER.getOneOr()) {//专营订单 1
                        System.out.println(" 优惠1 ");
                        bb.setTotalDetail(sum - Money01);//0.1
                        bb.setTotalExp(Money01);
                        bizOrderHeaderService.updateMoney(bb);
                    }else if(bizOrder.getBizType() == BizOrderDiscount.THIS_ORDER.getOneOr()) {//非专营订单 2
                        System.out.println(" 优惠2 ");
                        bb.setTotalDetail(sum - Money005);//0.05
                        bb.setTotalExp(Money005);
                        bizOrderHeaderService.updateMoney(bb);
                    }else{
                        System.out.println(" 未知 ");
                    }
                }
            }else{
                System.out.println(" 无优惠 ");
                bb.setTotalDetail(sum);
                bizOrderHeaderService.updateMoney(bb);
            }
        }
//        ------------------------计算订单详情总价 非专营----------------------
        BizOrderDetail deta2 = new BizOrderDetail();
        deta2.setOrderHeader(orderDetailNew.getOrderHeader());
        List<BizOrderDetail> detaiNew = super.findList(deta2);
        Double sum3=0.0;//价格初始化
        if(detaiNew!=null){
            for(BizOrderDetail order : detaiNew){
                Double price1 = order.getUnitPrice();//商品单价
                Integer ordQty1 = order.getOrdQty();//采购数量
                if(price1==null){price1=0.0; }
                if(ordQty1==null){ordQty1=0; }
                sum3+=price1*ordQty1;
            }
            if (oneOrder.equals("firstOrder")) {//首单标记判断
                BizOrderHeader bizOrder2 = bizOrderHeaderService.get(orderDetailNew.getOrderHeader().getId());
                Double Money1 = sum3 * BizOrderDiscount.SELF_SUPPORT.getCalcs();//0.1
                Double Money2 = sum3 * BizOrderDiscount.NON_SELF_SUPPORT.getCalcs();//0.05
                if(sum3 <= 10000){//限额 10000
                    if(bizOrder2.getBizType() == BizOrderDiscount.TWO_ORDER.getOneOr()) {//专营订单 1
                        System.out.println(" 优惠1 ");
                        orderHeaderNew.setTotalDetail(sum3 - Money1);//0.1
                        orderHeaderNew.setTotalExp(-Money1);//订单总费用
                        bizOrderHeaderService.updateMoney(orderHeaderNew);
                    }else if(bizOrder2.getBizType() == BizOrderDiscount.THIS_ORDER.getOneOr()) {//非专营订单 2
                        System.out.println(" 优惠2 ");
                        orderHeaderNew.setTotalDetail(sum3 - Money2);//0.05
                        orderHeaderNew.setTotalExp(-Money2);//订单总费用
                        bizOrderHeaderService.updateMoney(orderHeaderNew);
                    }else{
                        System.out.println(" 未知 ");
                    }
                }
            }else{
                System.out.println(" 无优惠 ");
                orderHeaderNew.setTotalDetail(sum3);
                bizOrderHeaderService.updateMoney(orderHeaderNew);
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
        //删除执行库存表相应的加减数量
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


    public List<BizOrderDetail>findOrderTotalByVendor(BizOrderHeader bizOrderHeader){
        return bizOrderDetailDao.findOrderTotalByVendor(bizOrderHeader);
    }
}