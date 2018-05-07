/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shop;

import java.util.List;

import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopReceiverAddress;
import com.wanhutong.backend.modules.biz.dao.shop.BizShopReceiverAddressDao;

/**
 * 收货地址Service
 *
 * @author Oy
 * @version 2018-04-10
 */
@Service
@Transactional(readOnly = true)
public class BizShopReceiverAddressService extends CrudService<BizShopReceiverAddressDao, BizShopReceiverAddress> {

    @Autowired
    private CommonLocationService commonLocationService;

    public BizShopReceiverAddress get(Integer id) {
        return super.get(id);
    }

    public List<BizShopReceiverAddress> findList(BizShopReceiverAddress bizShopReceiverAddress) {
        return super.findList(bizShopReceiverAddress);
    }

    public Page<BizShopReceiverAddress> findPage(Page<BizShopReceiverAddress> page, BizShopReceiverAddress bizShopReceiverAddress) {
        return super.findPage(page, bizShopReceiverAddress);
    }

    @Transactional(readOnly = false)
    public void save(BizShopReceiverAddress bizShopReceiverAddress) {
        if (bizShopReceiverAddress.getBizLocation() != null) {
            commonLocationService.updateCommonLocation(bizShopReceiverAddress.getBizLocation());
        }
        Integer statu = bizShopReceiverAddress.getDefaultStatus();
        //  1，默认
        if (statu != null && statu == 1) {
            BizShopReceiverAddress shopAddress = new BizShopReceiverAddress();
            shopAddress.setUser(bizShopReceiverAddress.getUser());
            List<BizShopReceiverAddress> list = super.findList(shopAddress);
            if(list.size()!=0){
                for (BizShopReceiverAddress add : list) {
                    shopAddress=super.get(add);
                    shopAddress.setDefaultStatus(0);
                    super.save(shopAddress);
                }
            }
        }
        super.save(bizShopReceiverAddress);
    }

    @Transactional(readOnly = false)
    public void delete(BizShopReceiverAddress bizShopReceiverAddress) {
        super.delete(bizShopReceiverAddress);
    }

}