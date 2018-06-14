/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.dao.order.BizPhotoOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.order.*;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5:代采订单 6:拍照下单)Service
 * @author ZhangTengfei
 * @version 2018-06-14
 */
@Service
@Transactional(readOnly = true)
public class BizPhotoOrderHeaderService extends CrudService<BizOrderHeaderDao, BizOrderHeader> {

    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;
    @Autowired
    private BizPhotoOrderHeaderDao bizPhotoOrderHeaderDao;
    @Autowired
    private UserDao userDao;

    public BizOrderHeader get(Integer id) {
        return super.get(id);
    }

    @Transactional(readOnly = false)
    public void saveOrderHeader(BizOrderHeader bizOrderHeader) {
        super.save(bizOrderHeader);
    }

    @Transactional(readOnly = false)
    public void delete(BizOrderHeader bizOrderHeader) {
        super.delete(bizOrderHeader);
    }

    /**
     * 查询供应商主负责人
     * @return
     */
    public User findVendUser(Integer orderId, String vendType) {
        return userDao.findVendUser(orderId,vendType);
    }

}