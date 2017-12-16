/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;

import java.util.List;

/**
 * 产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多Entity
 * @author zx
 * @version 2017-12-14
 */
public class BizProdCate extends DataEntity<BizProdCate> {
	
	private static final long serialVersionUID = 1L;
	private BizProductInfo productInfo;		// biz_product_info.id
	private BizCategoryInfo categoryInfo;		// biz_category_info.id

	private List<String> catIds;

	
	public BizProdCate() {
		super();
	}

	public BizProdCate(Integer id){
		super(id);
	}

	public BizProductInfo getProductInfo() {
		return productInfo;
	}


	public void setProductInfo(BizProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	public BizCategoryInfo getCategoryInfo() {
		return categoryInfo;
	}

	public void setCategoryInfo(BizCategoryInfo categoryInfo) {
		this.categoryInfo = categoryInfo;
	}

	public List<String> getCatIds() {
		return catIds;
	}

	public void setCatIds(List<String> catIds) {
		this.catIds = catIds;
	}
}