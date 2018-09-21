package com.wanhutong.backend.modules.sys.service;


import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MyPanelService {

    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;



    public int getOrderWaitAuditCount(String roleName) {
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setSelectAuditStatus(roleName);
        List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }

    public int getOrderWaitAgreedDeliveryCount() {
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setMobileAuditStatus(0);
        List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }

    public int getOrderHasRetainageCount() {
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setRetainage(1);
        List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }
}
