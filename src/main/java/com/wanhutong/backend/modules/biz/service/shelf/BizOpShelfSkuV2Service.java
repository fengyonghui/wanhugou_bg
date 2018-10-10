/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shelf;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.shelf.BizOpShelfSkuV2Dao;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品上架管理Service
 * @author liuying
 * @version 2017-12-19
 */
@Service
@Transactional(readOnly = true)
public class BizOpShelfSkuV2Service extends CrudService<BizOpShelfSkuV2Dao, BizOpShelfSku> {

	private static Logger LOGGER = LoggerFactory.getLogger(BizOpShelfSkuV2Service.class);
	@Resource
	private BizProductInfoService bizProductInfoService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizOpShelfSkuV2Dao bizOpShelfSkuV2Dao;
	public BizOpShelfSku get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOpShelfSku> findList(BizOpShelfSku bizOpShelfSku) {
		return super.findList(bizOpShelfSku);
	}
	
	public Page<BizOpShelfSku> findPage(Page<BizOpShelfSku> page, BizOpShelfSku bizOpShelfSku) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			bizOpShelfSku.setDataStatus("filter");
			return super.findPage(page, bizOpShelfSku);
		}else {
			bizOpShelfSku.getSqlMap().put("shelfSku", BaseService.dataScopeFilter(user, "so", "suc"));
			return super.findPage(page, bizOpShelfSku);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(BizOpShelfSku bizOpShelfSku) {
		BizProductInfo bizProductInfo=saveProductPrice(bizOpShelfSku);
		bizOpShelfSku.setProductInfo(bizProductInfo);
		super.save(bizOpShelfSku);
	}
	@Transactional(readOnly = false)
	public BizProductInfo saveProductPrice(BizOpShelfSku bizOpShelfSku){

		Integer skuId=bizOpShelfSku.getSkuInfo().getId();//skuId 为key
		BizSkuInfo skuInfo=bizSkuInfoService.get(skuId);
		BizProductInfo bizProductInfo=bizProductInfoService.get(skuInfo.getProductInfo().getId());
		if(bizProductInfo.getMinPrice()==0.0){
			bizProductInfo.setMinPrice(bizOpShelfSku.getSalePrice());
		}else if(bizProductInfo.getMinPrice()>bizOpShelfSku.getSalePrice()){
			bizProductInfo.setMinPrice(bizOpShelfSku.getSalePrice());
		}
		if(bizProductInfo.getMaxPrice()==null){
			bizProductInfo.setMaxPrice(0.0);
		}else if(bizProductInfo.getMaxPrice()<bizOpShelfSku.getSalePrice()){
			bizProductInfo.setMaxPrice(bizOpShelfSku.getSalePrice());
		}



		bizProductInfoService.saveProd(bizProductInfo);
		return bizProductInfo;
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOpShelfSku bizOpShelfSku) {
		super.delete(bizOpShelfSku);
	}

	@Transactional(readOnly = false)
	public void updateShelves(BizOpShelfSku bizOpShelfSku){
		bizOpShelfSkuV2Dao.shelvesUpdate(bizOpShelfSku);
	}

	@Transactional(readOnly = false)
	public void updateDateTime(BizOpShelfSku bizOpShelfSku) {
		bizOpShelfSkuV2Dao.dateTimeUpdate(bizOpShelfSku);
	}
	@Transactional(readOnly = false)
	public String saveShelfProdInfoPrice(BizOpShelfSku bizOpShelfSku,int i){
		BizProductInfo productInfo=bizProductInfoService.get(bizOpShelfSku.getProductInfo().getId());
		BizOpShelfSku opShelfSku =new BizOpShelfSku();
		opShelfSku.setProductInfo(productInfo);
		Map<String,BigDecimal> map=bizOpShelfSkuV2Dao.findMinMaxPrice(opShelfSku);
		if(map!=null){
		productInfo.setMaxPrice(map.get("maxPrice").doubleValue());
		productInfo.setMinPrice(map.get("minPrice").doubleValue());
		bizProductInfoService.saveProd(productInfo);
			if (i == 2) {
				return "货号为:" + bizOpShelfSku.getSkuInfo().getItemNo() + ",货架为:" + bizOpShelfSku.getOpShelfInfo().getName() + "的商品下架成功";
			}
			if (i == 1) {
				return "货号为:" + bizOpShelfSku.getSkuInfo().getItemNo() + ",货架为:" + bizOpShelfSku.getOpShelfInfo().getName() + "的商品上架成功";
			}
		}else {
			if (i == 1) {
				LOGGER.error("[ERROR][上架商品失败]，货号[{}],货架[{}]",bizOpShelfSku.getSkuInfo().getItemNo(),bizOpShelfSku.getOpShelfInfo().getName());
				return "货号为:" + bizOpShelfSku.getSkuInfo().getItemNo() + ",货架为:" + bizOpShelfSku.getOpShelfInfo().getName() + "的商品上架失败";
			}
			if (i == 2) {
				LOGGER.error("[ERROR][下架商品失败]，货号[{}],货架[{}]",bizOpShelfSku.getSkuInfo().getItemNo(),bizOpShelfSku.getOpShelfInfo().getName());
				return "货号为:" + bizOpShelfSku.getSkuInfo().getItemNo() + ",货架为:" + bizOpShelfSku.getOpShelfInfo().getName() + "的商品下架失败";
			}
		}
		return "";
	}


	@Transactional(readOnly = false)
	public  void saveShelfProdInfoPrice(BizOpShelfSku bizOpShelfSku){

	}


	@Transactional(readOnly = false)
	public void sort() {
        List<BizOpShelfSku> opShelfSkuList = bizOpShelfSkuV2Dao.selectSort();
        int priority = 10;
        for (BizOpShelfSku opShelfSku:opShelfSkuList) {

            opShelfSku.setPriority(priority);
            if (opShelfSku.getCenterOffice()==null) {
                opShelfSku.setCenterOffice(new Office(0));
            }
            bizOpShelfSkuV2Dao.sort(opShelfSku);
            priority += 10;
        }
	}

	public List<BizOpShelfSku> findShelfSkuList(BizOpShelfSku bizOpShelfSku) {
		return bizOpShelfSkuV2Dao.findShelfSkuList(bizOpShelfSku);
	}
}