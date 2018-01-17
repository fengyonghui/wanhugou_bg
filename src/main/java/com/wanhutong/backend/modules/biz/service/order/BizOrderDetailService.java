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
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
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
        BizOrderHeader oh = bizOrderDetail.getOrderHeader();
        BizOrderHeader bb = bizOrderHeaderService.get(oh);
        BizOrderHeader hh = new BizOrderHeader();
                        hh.setOrderType(bb.getOrderType());
                        hh.setCustomer(bb.getCustomer());
                        hh.setTotalDetail(sum);//计算总价
                        hh.setTotalExp(bb.getTotalExp());
                        hh.setFreight(bb.getFreight());
                        hh.setInvStatus(bb.getInvStatus());
                        hh.setBizStatus(bb.getBizStatus());
                        hh.setPlatformInfo(bb.getPlatformInfo());
                        hh.setBizLocation(bb.getBizLocation());
        String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(hh.getOrderType().toString()), hh.getCustomer().getId());

        String[] split = StringUtils.split(bizOrderDetail.getOrderDetaIds(), ",");//多选商品
        for (int i = 0; i < split.length; i++) {
            BizSkuInfo sku = bizSkuInfoService.get(Integer.parseInt(split[i].trim()));
//            biz_sku_info中的skuType：1是专营商品（自选商品）、2是定制商品，3是非专营商品
            Integer type = sku.getSkuType();
            if (bizOrderDetail.getId() == null) {//点击修改时不新增订单
                if(type == BizOrderDiscount.TWO_ORDER.getOneOr()){//1 专营
                        System.out.println(" 专营 ");
                        hh.setOrderNum(orderNum);//新建订单编号
                        hh.setBizType(BizOrderDiscount.TWO_ORDER.getOneOr());//1
                            Double price = sku.getBasePrice();//商品单价
                            Integer ordQty = bizOrderDetail.getOrdQty();//采购数量
                            sum += price * ordQty;
                        hh.setTotalDetail(sum);//商品详情总价
                        bizOrderHeaderService.save(hh);
                        BizOrderDetail DDD = new BizOrderDetail();
                            DDD.setOrderHeader(hh);//order_header.id
                                if(bizOrderDetail.getMaxLineNo()==null){
                                    DDD.setLineNo(1);
                                }else {
                                    Integer maxLineNo = bizOrderDetail.getMaxLineNo();
                                    maxLineNo++;
                                    DDD.setLineNo(maxLineNo);//行号
                                }
                            DDD.setPartNo(null);//bom
                            DDD.setShelfInfo(bizOrderDetail.getShelfInfo());//货架
                            DDD.setSkuInfo(sku);//sku产品
                            DDD.setPartNo(sku.getPartNo());//编号
                            DDD.setSkuName(sku.getName());//商品名称
                            DDD.setUnitPrice(sku.getBasePrice());//单价
                            DDD.setOrdQty(bizOrderDetail.getOrdQty());//采购数量
                            DDD.setSentQty(bizOrderDetail.getSentQty());//发货数量
                            DDD.setQuality(bizOrderDetail.getQuality());//材质
                            DDD.setColor(bizOrderDetail.getColor());//颜色
                            DDD.setStandard(bizOrderDetail.getStandard());//规格
                    super.save(DDD);//把商品放入新建订单中
                }else if (type == BizOrderDiscount.THIS_ORDER.getOneOr()) {//2 定制商品
                    System.out.println(" 定制商品 ");
                }else if(type ==BizOrderDiscount.FIRST_ORDER.getOneOr()){//3 非专营
                    System.out.println(" 非专营 ");
                    hh.setOrderNum(hh.getOrderNum());//不新建
                    hh.setBizType(BizOrderDiscount.THIS_ORDER.getOneOr());//非专营 2 order_header
                    bizOrderHeaderService.save(hh);
                    bizOrderDetail.setSkuInfo(sku);
                    bizOrderDetail.setSkuName(sku.getName());
                    super.save(bizOrderDetail);
                }else{
                    System.out.println(" 未知 ");
                }
            }
        }
//		------------------------计算订单详情总价----------------------
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
        List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
        Double d1 = bizOrderHeader.getTotalDetail();
        Double a1=d1*BizOrderDiscount.SELF_SUPPORT.getCalcs();//0.1
        Double a2=d1*BizOrderDiscount.NON_SELF_SUPPORT.getCalcs();//0.05

        for (BizOrderHeader order : list) {
            Double price = bizOrderDetail.getUnitPrice();//商品单价
            Integer ordQty = bizOrderDetail.getOrdQty();//采购数量
            sum += price * ordQty;
            if (oneOrder =="firstOrder") {//首单标记判断
                if(d1<=10000){//限额 10000
                    if (bizOrderHeader.getBizType() == BizOrderDiscount.TWO_ORDER.getOneOr()) {//自选订单-1
                        System.out.println(" 优惠1 ");
                        bizOrderHeader.setTotalDetail(d1 - a1);//0.1
                        bizOrderHeaderService.save(bizOrderHeader);
                    } else if (bizOrderHeader.getBizType() == BizOrderDiscount.THIS_ORDER.getOneOr()) {//非自选订单-2
                        System.out.println(" 优惠2 ");
                        bizOrderHeader.setTotalDetail(d1 - a2);//0.05
                        bizOrderHeaderService.save(bizOrderHeader);
                    } else {
                        System.out.println(" 未知 ");
                    }
                }
            }else {
                System.out.println(" 无优惠 ");
            }
            bizOrderHeader.setTotalDetail(bizOrderHeader.getTotalDetail() + sum);
            bizOrderHeaderService.save(bizOrderHeader);
        }
//		-----------------------------分割一下是订单详情-------------------------------------------
        Integer ordQtyFront = bizOrderDetail.getOrdQtyUpda();//修改前采购数量
        if (ordQtyFront == null) {
            ordQtyFront = 0;//默认值
        }
        Integer ordQtyAfter = bizOrderDetail.getOrdQty();//修改后采购数量
//		查询有多少库存
        BizInventorySku bizInventorySku = new BizInventorySku();
        bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
        List<BizInventorySku> list2 = bizInventorySkuService.findList(bizInventorySku);
        for (BizInventorySku inventorySku : list2) {
            Integer stockQty = inventorySku.getStockQty();//库存数量
            if (ordQtyFront == ordQtyAfter) {
                bizOrderDetail.setOrdQty(ordQtyAfter);
            } else if (ordQtyFront < ordQtyAfter) {
                //计算库存数量
                inventorySku.setStockQty(stockQty - (ordQtyAfter - ordQtyFront));
                bizInventorySkuService.save(inventorySku);
                bizOrderDetail.setOrdQty(ordQtyAfter);
            } else {
                //计算库存数量
                inventorySku.setStockQty(stockQty + (ordQtyFront - ordQtyAfter));
                bizInventorySkuService.save(inventorySku);
                bizOrderDetail.setOrdQty(ordQtyAfter);
            }
            //计算库存销售订单数量
            Integer stockOrdNumber = inventorySku.getStockOrdQty();
            inventorySku.setStockOrdQty(++stockOrdNumber);
            bizInventorySkuService.save(inventorySku);
        }
        super.save(bizOrderDetail);
    }

    public Integer findMaxLine(BizOrderDetail bizOrderDetail) {
        return bizOrderDetailDao.findMaxLine(bizOrderDetail);
    }

    @Transactional(readOnly = false)
    public void delete(BizOrderDetail bizOrderDetail) {
        //删除执行库存表相应的加减数量
        Integer ordQty = bizOrderDetail.getOrdQty();
        BizInventorySku bizInventorySku = new BizInventorySku();
        bizInventorySku.setSkuInfo(bizOrderDetail.getSkuInfo());
        List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
        for (BizInventorySku inventorySku : list) {
            Integer stockQty = inventorySku.getStockQty();
            inventorySku.setStockQty(stockQty + ordQty);
            Integer stockOrdQty = inventorySku.getStockOrdQty();
            inventorySku.setStockOrdQty(--stockOrdQty);
            bizInventorySkuService.save(inventorySku);
        }
        super.delete(bizOrderDetail);
    }

}