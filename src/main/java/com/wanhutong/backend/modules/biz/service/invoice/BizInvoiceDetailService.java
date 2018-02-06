/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.invoice;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.invoice.BizInvoiceDetailDao;
import com.wanhutong.backend.modules.biz.dao.invoice.BizInvoiceInfoDao;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceDetail;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.enums.BizOrderHeadStatus;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发票详情(发票行号,order_header.id)Service
 *
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Service
@Transactional(readOnly = true)
public class BizInvoiceDetailService extends CrudService<BizInvoiceDetailDao, BizInvoiceDetail> {

    @Autowired
    private BizInvoiceInfoDao bizInvoiceInfoDao;
    @Autowired
    private BizInvoiceDetailDao bizInvoiceDetailDao;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    private BizInvoiceHeaderService bizInvoiceHeaderService;

    public BizInvoiceDetail get(Integer id) {
        return super.get(id);
    }

    public List<BizInvoiceDetail> findList(BizInvoiceDetail bizInvoiceDetail) {
        return super.findList(bizInvoiceDetail);
    }

    public Page<BizInvoiceDetail> findPage(Page<BizInvoiceDetail> page, BizInvoiceDetail bizInvoiceDetail) {
        return super.findPage(page, bizInvoiceDetail);
    }

    @Transactional(readOnly = false)
    public void save(BizInvoiceDetail bizInvoiceDetail) {
        if(bizInvoiceDetail.getMaxLineNo()==null){
            bizInvoiceDetail.setLineNo(0);
        }else{
            Integer maxLineNo = bizInvoiceDetail.getMaxLineNo();
            bizInvoiceDetail.setLineNo(++maxLineNo);
        }
        super.save(bizInvoiceDetail);
    }

    @Transactional(readOnly = false)
    public void delete(BizInvoiceDetail bizInvoiceDetail) {
        super.delete(bizInvoiceDetail);
        Double sum=0.0;
        BizInvoiceDetail invoiceDetail = new BizInvoiceDetail();
        invoiceDetail.setInvoiceHeader(bizInvoiceDetail.getInvoiceHeader());
        List<BizInvoiceDetail> list = super.findList(invoiceDetail);
        for (BizInvoiceDetail detail : list) {
            BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(detail.getOrderHead().getId());
            Double totalExp = bizOrderHeader.getTotalExp();//订单总费用
            Double freight = bizOrderHeader.getFreight();//运费
            Double totalDetail = bizOrderHeader.getTotalDetail();//订单详情总价
            sum += totalExp + freight + totalDetail;
        }
        BizInvoiceHeader invoiceHeader = new BizInvoiceHeader();
        invoiceHeader.setId(bizInvoiceDetail.getInvoiceHeader().getId());
        if(list.size()==0){
            invoiceHeader.setInvTotal(0.0);//发票数额
//            bizInvoiceHeaderService.updateInvTotal(invoiceHeader);
        }else{
            invoiceHeader.setInvTotal(sum);//发票数额
//            bizInvoiceHeaderService.updateInvTotal(invoiceHeader);
        }
    }

    @Transactional(readOnly = false)
    public Integer findMaxLine(BizInvoiceDetail bizInvoiceDetail) {
        return bizInvoiceInfoDao.findMaxLine(bizInvoiceDetail);
    }

}