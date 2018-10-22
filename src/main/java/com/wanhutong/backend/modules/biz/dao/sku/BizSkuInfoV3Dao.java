/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.sku;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品skuDAO接口
 * @author zx
 * @version 2017-12-07
 */
@MyBatisDao
public interface BizSkuInfoV3Dao extends CrudDao<BizSkuInfo> {

   void deleteSkuPropInfoReal(BizSkuInfo bizSkuInfo);

   List<BizSkuInfo> findListByParam(BizSkuInfo bizSkuInfo);

    BizSkuInfo getSkuInfoByItemNoProdId(@Param("itemNo") String itemNo, @Param("prodId") Integer prodId);

    List<BizSkuInfo> findListIgnoreStatus(BizSkuInfo oldSkuEntity);

    List<BizSkuInfo> findPurseSkuList(BizSkuInfo bizSkuInfo);

    void recovery(BizSkuInfo bizSkuInfo);

    /**
     * 根据PART NO 查询数据
     * @param partNo
     * @return
     */
    BizSkuInfo getEntityByPartNo(String partNo);

    /**
     * 获取商品和订单的对应关系列表
     * @param bizSkuInfo
     * @return
     */
    List<BizSkuInfo> findPageForSkuInfo(BizSkuInfo bizSkuInfo);


    /**
     * 查询数据条数
     * @param bizSkuInfo
     * @return
     */
    int findCount(BizSkuInfo bizSkuInfo);

    List<Integer> findReqSku();

   /**
    * 修改商品对应的产品ID
    * @param skuId
    * @param prodId
    */
   void updateProdId(@Param("skuId") Integer skuId, @Param("prodId") Integer prodId);

   List<BizSkuInfo> findSkuBySpuAndSize(@Param("prodId")Integer prodId, @Param("itemNo")String itemNo, @Param("size")String size);

   void updatePrice(@Param("id")Integer id, @Param("settlementPrice")BigDecimal settlementPrice);

   void updateItemNo(@Param("id")Integer id, @Param("itemNo")String itemNo);
}