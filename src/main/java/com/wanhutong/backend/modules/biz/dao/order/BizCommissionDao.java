/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.order.BizCommission;

import java.util.List;

/**
 * 佣金付款表DAO接口
 * @author wangby
 * @version 2018-10-18
 */
@MyBatisDao
public interface BizCommissionDao extends CrudDao<BizCommission> {

    /**
     * 忽略biz_commission_order中status为0的状态，获取所有关联的BizCommission数据
     */
    List<BizCommission> findPageForAllData(BizCommission bizCommission);

    /**
     * 通过佣金单获取该佣金单的支付凭证
     * @param bizCommission
     * @return
     */
    List<CommonImg> getImgList(BizCommission bizCommission);
}