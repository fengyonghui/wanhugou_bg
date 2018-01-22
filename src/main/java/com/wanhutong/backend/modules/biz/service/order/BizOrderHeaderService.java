/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Service
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderService extends CrudService<BizOrderHeaderDao, BizOrderHeader> {
//    @Resource
//    private CommonLocationService commonLocationService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;

    public List<BizOrderHeader> findListFirstOrder(BizOrderHeader bizOrderHeader) {
        //查询状态 status=0 和 1的所有信息
        return bizOrderHeaderDao.findListFirstOrder(bizOrderHeader);
    }

    public BizOrderHeader get(Integer id) {
        return super.get(id);
    }

    public List<BizOrderHeader> findList(BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
        if(user.isAdmin()){
            return super.findList(bizOrderHeader);
        }else {
            bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            return super.findList(bizOrderHeader);
        }
    }

    public Page<BizOrderHeader> findPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
        User user= UserUtils.getUser();
        if(user.isAdmin()){
            return super.findPage(page, bizOrderHeader);
        }else {
            bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
            return super.findPage(page, bizOrderHeader);
        }
    }

    @Transactional(readOnly = false)
    public void save(BizOrderHeader bizOrderHeader) {
        if(bizOrderHeader.getBizType()==null){
            bizOrderHeader.setBizType(1);//订单商品类型默认 2非专营      ----1专营
        }
        BizOrderAddress bizLocation = bizOrderHeader.getBizLocation();
        if (bizLocation.getRegion() == null) {
            bizLocation.setRegion(new SysRegion());
        }
        if(bizOrderHeader.getOrderType()==null){
            bizOrderHeader.setOrderType(1);//订单类型，默认选中  1普通订单
        }
        bizOrderAddressService.updateBizOrderAddress(bizLocation);
        bizOrderHeader.setOrderType(1);
        String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()), bizOrderHeader.getCustomer().getId());
        if(bizOrderHeader.getId()==null){
            bizOrderHeader.setOrderNum(orderNum);
        }else{
            bizOrderHeader.setOrderNum(bizOrderHeader.getOrderNum());
        }
        bizOrderHeader.setBizLocation(bizLocation);
        super.save(bizOrderHeader);
//		----------------------------查询是否首次下单--------------------------------------
        BizOrderHeader boh = new BizOrderHeader();
        Office custid = bizOrderHeader.getCustomer();
        boh.setCustomer(custid);
        boh.setBizStatus(BizOrderDiscount.ONE_ORDER.getOneOr());//条件为0
        List<BizOrderHeader> list = bizOrderHeaderDao.findListFirstOrder(boh);
        Double t1 = bizOrderHeader.getTotalDetail();//订单总详情费用
        if(list.size()==0){
            System.out.println("--是首单--");
            bizOrderHeader.setOneOrder("firstOrder");
            if(t1<=10000){//限额 10000
                if(bizOrderHeader.getBizType()==BizOrderDiscount.TWO_ORDER.getOneOr()){//专营 1
                    System.out.println(" 优惠10% ");
                    Double a1=t1*BizOrderDiscount.SELF_SUPPORT.getCalcs();//0.1
                    bizOrderHeader.setTotalDetail(t1-a1);
                }else if(bizOrderHeader.getBizType()==BizOrderDiscount.THIS_ORDER.getOneOr()){//非专营 2
                    System.out.println(" 优惠5% ");
                    Double a2=t1*BizOrderDiscount.NON_SELF_SUPPORT.getCalcs();//0.05
                    bizOrderHeader.setTotalDetail(t1-a2);
                }else{
                    System.out.println(" 未知 ");
                }
            }
        }else{
            System.out.println("--不是首单--");
        }


    }

    @Transactional(readOnly = false)
    public void saveOrderHeader(BizOrderHeader bizOrderHeader) {
        super.save(bizOrderHeader);
    }

    @Transactional(readOnly = false)
    public void delete(BizOrderHeader bizOrderHeader) {
        super.delete(bizOrderHeader);
    }


    @Transactional(readOnly = false)
    public void saveOrderHea(BizOrderHeader bizOrderHeader) {

    }

}