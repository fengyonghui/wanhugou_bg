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
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfInfoService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuPropValueService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
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
    private BizOpShelfInfoService bizOpShelfInfoService;
    @Autowired
    private BizSkuPropValueService bizSkuPropValueService;
    @Autowired
    private BizOrderSkuPropValueService bizOrderSkuPropValueService;

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
        int a = 0;
        if (skuIdArr != null && saleQtyArr != null && shelfSkuArr != null) {
            for (int i = 0; i < skuIdArr.length; i++) {
                Integer skuId = Integer.parseInt(skuIdArr[i].trim());//多个Sku商品ID
                Integer ordQty = Integer.parseInt(saleQtyArr[i].trim());//多个采购数量
                Integer shelfSkuId = Integer.parseInt(shelfSkuArr[i].trim());//多个货架ID
                BizSkuInfo sku = bizSkuInfoService.get(skuId);
//                -------------------------------------查询材质/颜色/尺寸--------------------------------------------
                BizSkuPropValue bizSkuPropValue = new BizSkuPropValue();//sku商品属性表
                bizSkuPropValue.setSkuInfo(sku);//sku_Id
                List<BizSkuPropValue> skuValueList = bizSkuPropValueService.findList(bizSkuPropValue);
                BizOpShelfSku opShelfSku = bizOpShelfSkuService.get(shelfSkuId);//查询商品
                BizOrderDetail detailnew = new BizOrderDetail();
                detailnew.setOrderHeader(bizOrderDetail.getOrderHeader());//order_header.id
                detailnew.setLineNo(++a);//行号
                detailnew.setPartNo(null);//bom产品
                detailnew.setShelfInfo(opShelfSku);//货架
                detailnew.setSkuInfo(sku);//sku产品
                detailnew.setPartNo(sku.getPartNo());//编号
                detailnew.setSkuName(sku.getName());//商品名称
                detailnew.setUnitPrice(sku.getBasePrice());//单价
                detailnew.setOrdQty(ordQty);//采购数量
                detailnew.setSentQty(0);//发货数量默认0
                if(opShelfSku!=null){
                    BizOpShelfInfo bizOpShelfInfo = bizOpShelfInfoService.get(opShelfSku.getOpShelfInfo().getId());//货架
                    if(bizOpShelfInfo!=null && bizOpShelfInfo.getType()==3){//type==3属于栏目类型 本地备货
                        detailnew.setSuplyis(opShelfSku.getCenterOffice());//采购中心
                    }else{
                        Office officeCenter=new Office();
                        System.out.println("栏目不是本地备货 给默认值 0 ");
                        detailnew.setSuplyis(officeCenter);//采购中心
                    }
                }
                super.save(detailnew);
                for (BizSkuPropValue bsp : skuValueList) {
                    BizOrderSkuPropValue bizOrderSkuPropValueTwo = new BizOrderSkuPropValue();//订单商品属性表
                    BizOrderDetail detailPropTwo = new BizOrderDetail();
                    detailPropTwo.setId(detailnew.getId());
                    bizOrderSkuPropValueTwo.setOrderDetails(detailPropTwo);//detail_id
                    bizOrderSkuPropValueTwo.setPropInfos(bsp.getProdPropValue());//spu属性
                    bizOrderSkuPropValueTwo.setPropName(bsp.getPropName());//sku属性
                    bizOrderSkuPropValueTwo.setPropValue(bsp.getPropValue());//sku属性值
                    bizOrderSkuPropValueService.save(bizOrderSkuPropValueTwo);//保存到订单详情商品属性表
                }
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


    public List<BizOrderDetail> findOrderTotalByVendor(BizOrderHeader bizOrderHeader) {
        return bizOrderDetailDao.findOrderTotalByVendor(bizOrderHeader);
    }
}