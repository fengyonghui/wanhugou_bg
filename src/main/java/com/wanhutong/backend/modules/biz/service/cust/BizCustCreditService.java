/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.cust;

import java.math.BigDecimal;
import java.util.List;

import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.dao.cust.BizCustCreditDao;

/**
 * 用户钱包Service
 *
 * @author Ouyang
 * @version 2018-03-09
 */
@Service
@Transactional(readOnly = true)
public class BizCustCreditService extends CrudService<BizCustCreditDao, BizCustCredit> {

    //支付状态
    public static final Integer BIZSTATUS = 1;
    //交易类型
    public static final Integer RECORDTYPE = 1;
    //支付类型
    public static final Integer PAYTYPE = 3;
    @Autowired
    private BizPayRecordService bizPayRecordService;

    public BizCustCredit get(Integer id) {
        return super.get(id);
    }

    public List<BizCustCredit> findList(BizCustCredit bizCustCredit) {
        return super.findList(bizCustCredit);
    }

    public Page<BizCustCredit> findPage(Page<BizCustCredit> page, BizCustCredit bizCustCredit) {
        if (bizCustCredit.getCustomer() != null) {
            bizCustCredit.getCustomer().getMoblieMoeny().setMobile(bizCustCredit.getCustomer().getMoblieMoeny().getMobile());
        }
        User user=UserUtils.getUser();
        if(user.isAdmin()){
            bizCustCredit.setDataStatus("filter");
        }
        return super.findPage(page, bizCustCredit);
    }

    @Transactional(readOnly = false)
    public void save(BizCustCredit bizCustCredit) {
        User user = UserUtils.getUser();
        BizCustCredit custCredit = this.get(bizCustCredit.getCustomer().getId());
        String a="0";
        if (custCredit != null && !custCredit.getDelFlag().equals(a)) {
            //原金额
            BigDecimal wallet = custCredit.getWallet();
            if (bizCustCredit.getCustFalg() != null && bizCustCredit.getCustFalg().equals("officeCust")) {
                custCredit.setWallet(wallet);
            }else{
                custCredit.setWallet(bizCustCredit.getWallet());
            }
            custCredit.setMoney(bizCustCredit.getMoney());
            custCredit.setId(bizCustCredit.getCustomer().getId());
            super.save(custCredit);
            if (bizCustCredit.getCustFalg() != null && bizCustCredit.getCustFalg().equals("officeCust")) {

            } else {
                /**
                 * 当用户钱包修改金额时创建交易记录
                 * */
                BizPayRecord bizPayRecord = new BizPayRecord();
                bizPayRecord.setPayNum("P_000000");
                bizPayRecord.setPayMoney(bizCustCredit.getWallet().subtract(wallet).doubleValue());
                bizPayRecord.setOriginalAmount(wallet);
                bizPayRecord.setCashAmount(bizCustCredit.getWallet());
                bizPayRecord.setPayer(user.getId());
                bizPayRecord.setCustomer(custCredit.getCustomer());
                bizPayRecord.setBizStatus(BIZSTATUS);
                bizPayRecord.setAccount(user.getCompany());
                bizPayRecord.setToAccount(custCredit.getCustomer());
                bizPayRecord.setRecordType(RECORDTYPE);
                bizPayRecord.setRecordTypeName("充值");
                bizPayRecord.setPayType(PAYTYPE);
                bizPayRecord.setPayTypeName("平台付款");
                bizPayRecordService.save(bizPayRecord);
            }
        } else {
            BizCustCredit officeCredit = super.get(bizCustCredit.getCustomer().getId());
            if(officeCredit!=null){
                officeCredit.setId(bizCustCredit.getCustomer().getId());
                officeCredit.setDelFlag("1");
                super.save(officeCredit);
            }else{
                super.save(bizCustCredit);
            }
        }

    }

    @Transactional(readOnly = false)
    public void delete(BizCustCredit bizCustCredit) {
        super.delete(bizCustCredit);
    }


    /**
     * 用于订单支付保存 原有金额减去 输入的支付金额
     */
    @Transactional(readOnly = false)
    public void orderHeaderSave(BizCustCredit bizCustCredit) {
        super.save(bizCustCredit);
    }

}