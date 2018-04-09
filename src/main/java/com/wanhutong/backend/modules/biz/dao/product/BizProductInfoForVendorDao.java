/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.product;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 产品信息表DAO接口
 * @author zx
 * @version 2017-12-13
 */
@MyBatisDao
public interface BizProductInfoForVendorDao extends CrudDao<BizProductInfo> {

    public int  deleteProdCate(BizProductInfo bizProductInfo);

    public int insertProdCate(BizProductInfo bizProductInfo);

    public int deleteProdPropInfoReal(BizProductInfo bizProductInfo);

    int checkPass(@Param("id") Integer id);
}