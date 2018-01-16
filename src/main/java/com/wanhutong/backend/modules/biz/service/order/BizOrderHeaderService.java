/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.BizOrderDiscount;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
    @Resource
    private CommonLocationService commonLocationService;
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
        return super.findList(bizOrderHeader);
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
        System.out.println(bizOrderHeader);
        CommonLocation bizLocation = bizOrderHeader.getBizLocation();
        if (bizLocation.getRegion() == null) {
            bizLocation.setRegion(new SysRegion());
        }
        commonLocationService.updateCommonLocation(bizLocation);
        String orderNum = GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()), bizOrderHeader.getCustomer().getId());
        bizOrderHeader.setOrderNum(orderNum);
        bizOrderHeader.setBizLocation(bizLocation);
//		----------------------------查询是否首次下单--------------------------------------
        BizOrderHeader boh = new BizOrderHeader();
        Office custid = bizOrderHeader.getCustomer();
        boh.setCustomer(custid);
        boh.setBizStatus(BizOrderDiscount.ONE_ORDER.getOneOr());//条件为0
        List<BizOrderHeader> list = bizOrderHeaderDao.findListFirstOrder(boh);
        if(list.size()==0 ){
            System.out.println("-是首单-");
//            boh.setOneOrder("firstOrder");
        }else{
            System.out.println("-不是首单-");
            boh.setOneOrder("firstOrder");
        }
        super.save(bizOrderHeader);

    }

    @Transactional(readOnly = false)
    public void saveOrderHeader(BizOrderHeader bizOrderHeader) {
        super.save(bizOrderHeader);
    }

    @Transactional(readOnly = false)
    public void delete(BizOrderHeader bizOrderHeader) {
        super.delete(bizOrderHeader);
    }


}