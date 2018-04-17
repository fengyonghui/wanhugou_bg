/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderUnlineDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 线下支付订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Service
 * @author ZhangTengfei
 * @version 2017-12-20
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderUnlineService extends CrudService<BizOrderHeaderUnlineDao, BizOrderHeaderUnline> {

    @Resource
    private BizOrderHeaderUnlineDao bizOrderHeaderUnlineDao;


    public BizOrderHeaderUnline get(Integer id) {
        return super.get(id);
    }

    public List<BizOrderHeaderUnline> findList(BizOrderHeaderUnline bizOrderHeaderUnline) {
        User user= UserUtils.getUser();
        boolean oflag = false;
        if (UserUtils.getOfficeList() != null){
            for (Office office:UserUtils.getOfficeList()){
                if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
                    oflag = true;
                }
            }
        }
        List<BizOrderHeaderUnline> bizOrderHeaderList = super.findList(bizOrderHeaderUnline);
        return bizOrderHeaderList;
    }

    @Transactional(readOnly = false)
    public void save(BizOrderHeaderUnline bizOrderHeaderUnline) {
        super.save(bizOrderHeaderUnline);
    }

    @Transactional(readOnly = false)
    public void delete(BizOrderHeaderUnline bizOrderHeaderUnline) {
        super.delete(bizOrderHeaderUnline);
    }

}