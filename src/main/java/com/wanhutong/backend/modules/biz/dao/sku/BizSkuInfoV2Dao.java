/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.sku;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品skuDAO接口
 * @author zx
 * @version 2017-12-07
 */
@MyBatisDao
public interface BizSkuInfoV2Dao extends CrudDao<BizSkuInfo> {

   void deleteSkuPropInfoReal(BizSkuInfo bizSkuInfo);

   List<BizSkuInfo> findListByParam(BizSkuInfo bizSkuInfo);

    BizSkuInfo getSkuInfoByItemNoProdId(@Param("itemNo") String itemNo, @Param("prodId")Integer prodId);

    List<BizSkuInfo> findListIgnoreStatus(BizSkuInfo oldSkuEntity);
}