/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.integration;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecode;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecodeDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分流水DAO接口
 * @author LX
 * @version 2018-09-16
 */
@MyBatisDao
public interface BizMoneyRecodeDao extends CrudDao<BizMoneyRecode> {

    BizMoneyRecodeDetail selectRecodeDetail();

    List<BizMoneyRecodeDetail> selectExpireMoney();

    void saveAll(List<BizMoneyRecode> list);

    void updateMoney(List<BizMoneyRecode> list);

    void updateExpireMoney(List<BizMoneyRecode> list);

    @Select("SELECT money as availableIntegration,user_money as usedIntegration,gain_money as gainIntegration,over_money as expireIntegration from biz_cust_credit where office_id = #{officeId}")
    BizMoneyRecodeDetail selectMoney(@Param("officeId") Integer officeId);
	
}